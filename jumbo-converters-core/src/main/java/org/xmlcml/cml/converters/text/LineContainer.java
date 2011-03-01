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

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLList;
import org.xmlcml.cml.element.CMLModule;
import org.xmlcml.euclid.Int2;

public class LineContainer {

	private final static Logger LOG = Logger.getLogger(LineContainer.class);
	
	public  static final String LINE_COUNT = "lineCount";

	private int currentNodeIndex = 0;
	private Element linesElement;
	
	public LineContainer() {
	}
	
	/** extracts children of element and destroys element
	 * @param element
	 */
	public LineContainer(Element element) {
		this();
		this.linesElement = element;
	}
	
	/** splits lines at line endings	 * 
	 * @param s
	 */
	public LineContainer(String s) {
		this();
		this.setContent(s);
	}
	
	/** takes list of split lines
	 * @param lines
	 */
	public LineContainer(List<String> lines) {
		this();
		LOG.trace("lines: "+lines.size());
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
		for (String line : lines) {
			linesElement.appendChild(line);
		}
		LOG.trace(linesElement.getChildCount());
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
		return (hasMoreNodes()) ? linesElement.getChild(currentNodeIndex) : null;
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
		Int2 range = new Int2(startIndex, startIndex);
		if (this.canReadContiguousTextNodes(startIndex, contiguousPatterns.size())) {
			for (int i = 0; i < contiguousPatterns.size(); i++) {
				Node node = linesElement.getChild(i + startIndex);
				String value = node.getValue();
				Pattern pattern = contiguousPatterns.get(i);
				LOG.debug("matching ["+value+"] against ["+pattern+"]");
				Matcher matcher = pattern.matcher(value);
				if (!matcher.matches()) {
					range = null;
					break;
				}
			}
			if (range != null) {
				range = new Int2(startIndex, startIndex+contiguousPatterns.size()-1);
			}
		}
		return range;
	}

	Int2 findNextMatch(int nodeIndex, PatternChunker chunker) {
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
		for (; index < linesElement.getChildCount(); index++) {
			if (!(linesElement.getChild(index) instanceof Text)) {
				break;
			}
		}
		return new Int2(start, index);
	}
	
	Element createChunk(int start, int end) {
		LOG.trace("chunk "+start+" | "+end);
		Element chunk = null;
		int nchunked = 0;
		for (int i = 0; i < end-start; i++) {
			Node node = linesElement.getChild(start);
			if (node == null) {
				LOG.error("null node: "+start);
				continue;
			}
			if (!(node instanceof Text)) {
//				throw new RuntimeException("making chunk: expected text node, found "+node.toXML());
				LOG.error("making chunk: expected text node, found "+node.toXML());
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
			chunk.addAttribute(new Attribute(LINE_COUNT, ""+nchunked));
//			CMLUtil.debug(chunk, "CH");
			linesElement.insertChild(chunk, start);
		}
		currentNodeIndex -= (nchunked-1);
		return chunk;
	}

	public void debug(String string) {
		CMLUtil.debug(linesElement, string);
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
		Element element = this.getLinesElement();
		Nodes nodes = element.query(".//text()");
		for (int i = 0; i < nodes.size(); i++) {
			wrapText((Text)nodes.get(i));
		}
		CMLUtil.normalizeWhitespaceInTextNodes(element);
		return element;
	}

	private void wrapText(Text text) {
		Element parent = (Element) text.getParent();
		Element l = new Element("l");
		parent.replaceChild(text, l);
		l.appendChild(text);
		CMLUtil.debug(l);
	}

	public void insertChunk(Element chunk) {
		if (chunk != null) {
			Node node = linesElement.getChild(currentNodeIndex-1);
			if (node != null) {
				int nchunk = getChunkChildCount(chunk);
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
}