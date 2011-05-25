package org.xmlcml.cml.converters.text;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.Text;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLList;
import org.xmlcml.cml.element.CMLModule;
import org.xmlcml.euclid.Int2;

public class LineContainer {
	private final static Logger LOG = Logger.getLogger(LineContainer.class);
	
	static{
	    LOG.setLevel(Level.ERROR);
	}
	
	public  static final String LINE_COUNT = "lineCount";
	private static final String L = "l";

	private int currentNodeIndex = 0;
	private Element linesElement;
	private Template template;
	
	public LineContainer(Template template) {
		this.template = template;
	}
	
	/** extracts children of element and destroys element
	 * @param element
	 */
	public LineContainer(Element element, Template template) {
		this(template);
		this.linesElement = element;
		ensureNamespacesOnLinesElement();
	}

	private void ensureNamespacesOnLinesElement() {
		if (template != null) {
			String convention = template.getConvention();
			if (convention != null) {
				linesElement.addAttribute(new Attribute(Template.CONVENTION, convention));
			}
		}
		linesElement.addNamespaceDeclaration(CMLConstants.CMLX_PREFIX, CMLConstants.CMLX_NS);
	}
	
	/** splits lines at line endings	 * 
	 * @param s
	 */
	public LineContainer(String s, Template template) {
		this(template);
		this.setContent(s);
	}
	
	/** splits lines at line endings	 * 
	 * @param s
	 */
	public LineContainer(String s) {
		this(s, null);
		this.setContent(s);
	}
	
	/** takes list of split lines
	 * @param lines
	 */
	public LineContainer(List<String> lines, Template template) {
		this(template);
		LOG.debug("lines: "+lines.size());
		this.setContent(lines);
	}
	
	public int getCurrentNodeIndex() {
		return currentNodeIndex;
	}

	public void setCurrentNodeIndex(int nodeIndex) {
		this.currentNodeIndex = nodeIndex;
	}

	public void setContent(String string) {
		List<String> lines = (string == null) ? null : Arrays.asList(string.split(CMLConstants.S_NEWLINE));
		if (lines != null) {
			setContent(lines);
		}
	}

	public void setContent(List<String> lines) {
		linesElement = new CMLModule();
		ensureNamespacesOnLinesElement();
		for (String line : lines) {
			try {
				linesElement.appendChild(line+'\n');
			} catch (Exception e) {
				LOG.error("line: "+line);
				throw new RuntimeException("Bad line (probably null or non-xml character?)");
			}
		}
		LOG.debug(linesElement.getChildCount());
	}
	
	public Node getNextNode() {
		Node currentNode = null;
		if (hasMoreNodes()) {
			currentNode = linesElement.getChild(currentNodeIndex);
			currentNodeIndex++;
		}
		return currentNode;
	}
	
	public Node peekCurrentNode() {
		return (hasMoreNodes() && currentNodeIndex >= 0) ? linesElement.getChild(currentNodeIndex) : null;
	}
	
	public boolean hasMoreNodes() {
		return currentNodeIndex < linesElement.getChildCount();
	}

	public boolean canReadContiguousTextNodes(int start, int linesToRead) {
		boolean can = true;
		if (start < 0 || linesToRead >= linesElement.getChildCount()) {
			can = false;
		} else {
			for (int i = start; i < start + linesToRead; i++) {
				if (!(linesElement.getChild(i) instanceof Text)) {
					can = false;
					break;
				}
			}
		}
		return can;
	}

	public Int2 matchLines(int startIndex, List<Pattern> contiguousPatterns) {
		Int2 range = null;
		if (isEOIPattern(contiguousPatterns)) {
			// point to line after end
			range = new Int2(linesElement.getChildCount(), linesElement.getChildCount());
		} else {
			if (this.canReadContiguousTextNodes(startIndex, contiguousPatterns.size())) {
				if (contiguousPatterns.size() == 0) {
					range = new Int2(startIndex, startIndex);
				} else if(matchesContiguousPatterns(startIndex, contiguousPatterns)) {
					range = new Int2(startIndex, startIndex+contiguousPatterns.size()-1);
				}
			}
		}
		LOG.trace("Range to match: "+range);
		return range;
	}

	private boolean matchesContiguousPatterns(int startIndex,
			List<Pattern> contiguousPatterns) {
		for (int i = 0; i < contiguousPatterns.size(); i++) {
			Node node = linesElement.getChild(i + startIndex);
			String value = node.getValue();
			Pattern pattern = contiguousPatterns.get(i);
//			LOG.trace("contiguous pattern matching ["+value+"] against ["+pattern+"]");
			Matcher matcher = pattern.matcher(value);
			if (!matcher.matches()) {
				return false;
			}
		}
		return true;
	}

	private boolean isEOIPattern(List<Pattern> contiguousPatterns) {
		return contiguousPatterns.size() == 1 && Template.EOI.equals(contiguousPatterns.get(0).toString());
	}

	Int2 findNextMatch(int nodeIndex, PatternContainer chunker) {
		// loop through lines till end (might tweak this later??)
		while (nodeIndex < this.linesElement.getChildCount()) {
			LOG.trace(nodeIndex+"/"+this.linesElement.getChildCount());
			Int2 contiguous = this.getContiguousTextRange(nodeIndex);
			while (nodeIndex < contiguous.getY()) {
				Int2 start = this.matchLines(nodeIndex, chunker.getPatternList());
				if (start != null) {
					return start;
				}
				nodeIndex++;
			}
			nodeIndex++;
		}
		return null;
	}


	public Int2 getContiguousTextRange(int start) {
		@SuppressWarnings("unused")
		Int2 range = null;
		int index = start;
		if (index < 0) {
			LOG.warn("BUG: negative index for lineContainer");
			index = 0;
		}
		for (; index < linesElement.getChildCount(); index++) {
			if (!(linesElement.getChild(index) instanceof Text)) {
				break;
			}
		}
		return new Int2(start, index);
	}
	
	Element createChunk(int start, int end) {
		LOG.trace("chunk "+start+" | "+end);
		CMLElement chunk = null;
		int nchunked = 0;
		for (int i = 0; i < end-start; i++) {
			Node node = linesElement.getChild(start);
			if (node == null) {
				LOG.trace("null node: "+start);
				continue;
			}
			if (!(node instanceof Text)) {
//				throw new RuntimeException("making chunk: expected text node, found "+node.toXML());
				LOG.trace("making chunk: expected text node at: "+start+", found "+node.toXML());
				break;
			} else {
				if (chunk == null) {
					chunk = new CMLModule();
				}
				node.detach();
				chunk.appendChild(node);
				nchunked++;
			}
		}
		if (chunk != null) {
			chunk.setCMLXAttribute(LINE_COUNT, ""+nchunked);
//			CMLUtil.debug(chunk, "CH");
			linesElement.insertChild(chunk, start);
		}
		currentNodeIndex -= (nchunked-1);
		return chunk;
	}

	public void debug(String string) {
		CMLUtil.debug(linesElement, string+" line: " +currentNodeIndex);
	}

	public String readLine() {
		String s = peekLine();
		currentNodeIndex++;
		return s;
	}
	
	public String peekLine() {
		Node node = peekCurrentNode();
		return (node == null || !(node instanceof Text)) ? null : node.getValue();
	}

	public Element getLinesElement() {
		return linesElement;
	}

	public Element getNormalizedLinesElement() {
		Element element = (Element) this.getLinesElement().copy();
		Nodes nodes = element.query(".//text()");
		for (int i = 0; i < nodes.size(); i++) {
			wrapText((Text)nodes.get(i));
		}
		CMLUtil.normalizeWhitespaceInTextNodes(element);
		return element;
	}

	private void wrapText(Text text) {
		Element parent = (Element) text.getParent();
		Element l = new Element(L);
		parent.replaceChild(text, l);
		l.appendChild(text);
//		CMLUtil.debug(l);
	}

	public void insertChunk(Element chunk, int originalNodeIndex) {
		if (chunk != null) {
			Node node = (currentNodeIndex == 0) ? null : linesElement.getChild(currentNodeIndex-1);
			if (node != null) {
//				int nchunk = getChunkChildCount(chunk);
				int nchunk = currentNodeIndex - originalNodeIndex;
				LOG.trace("NCHUNK "+nchunk);
				deleteLinesIfMoreThanOne(nchunk);
				node.getParent().replaceChild(node, chunk);
			} else {
				linesElement.appendChild(chunk);
			}
		}
	}

	private void deleteLinesIfMoreThanOne(int nchunk) {
		int firstIndexToDelete = currentNodeIndex - nchunk;
		for (int i = 0; i < nchunk-1; i++) {
			linesElement.getChild(firstIndexToDelete).detach();
			currentNodeIndex--;
		}
	}

	private int getChunkChildCount(Element chunk) {
		int count = 0;
		if (chunk instanceof CMLList) {
			CMLList list = (CMLList) chunk;
			count = Template.readIntegerAttribute(list, LINE_COUNT);
			if (count == 0) {
				count = list.getChildElements().size();
			}
		} else {
			count = Template.readIntegerAttribute(chunk, LINE_COUNT);
		}
		return count;
	}

	public void increaseCurrentNodeIndex(Integer linesToRead) {
		for (int i = 0; i < linesToRead; i++) {
			Node node = linesElement.getChild(currentNodeIndex);
			if (!hasMoreNodes()) {
				break;
			}
			if (node == null || !(node instanceof Text)) {
				throw new RuntimeException("failed to find Text node at: "+currentNodeIndex);
			}
			currentNodeIndex++;
		}
	}

	public void removeEmptyLists() {
		
	}
}
