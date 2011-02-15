package org.xmlcml.cml.converters.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Text;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.euclid.Int2;

public class LineContainer /*extends Element */ {

//	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(LineContainer.class);
	
	private static final String CHUNK = "chunk";
	private static final String LINE_CONTAINER = "lineContainer";
	private static final String TEMPLATE_REF = "templateRef";

	private int currentNodeNumber = 0;
	private int nodeIndex;
	private Element linesElement;
	
	public LineContainer() {
//		super(LINE_CONTAINER);
	}
	
	/** extracts children of element and destroys element
	 * @param element
	 */
	public LineContainer(Element element) {
		this();
		this.linesElement = element;
	}
	
	public LineContainer(String s) {
		this();
		this.setContent(s);
	}
	
	public void setContent(String string) {
		List<String> lines = (string == null) ? null : Arrays.asList(string.split(CMLConstants.S_NEWLINE));
		if (lines != null) {
			setContent(lines);
		}
	}

	public void setContent(List<String> lines) {
		linesElement = new Element(LINE_CONTAINER);
		for (String line : lines) {
			linesElement.appendChild(line);
		}
	}
	
	public Node getNextNode() {
		Node currentNode = null;
		if (hasMoreNodes()) {
			currentNode = linesElement.getChild(currentNodeNumber);
			currentNodeNumber++;
		}
		return currentNode;
	}
	
	public Node peekCurrentNode() {
		return (hasMoreNodes()) ? linesElement.getChild(currentNodeNumber) : null;
	}
	
	public boolean hasMoreNodes() {
		return currentNodeNumber < linesElement.getChildCount();
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

	public Int2 matchLines(int start, List<Pattern> contiguousPatterns) {
		Int2 range = new Int2(start, start);
		if (this.canReadContiguousTextNodes(start, contiguousPatterns.size())) {
			for (int i = 0; i < contiguousPatterns.size(); i++) {
				Node node = linesElement.getChild(i + start);
				Matcher matcher = contiguousPatterns.get(i).matcher(node.getValue());
				if (!matcher.matches()) {
					range = null;
					break;
				}
			}
			if (range != null) {
				range = new Int2(start, start+contiguousPatterns.size()-1);
			}
		}
		return range;
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
	
	public List<Element> applyChunkers(int maxRepeatCount, PatternChunker startChunker, PatternChunker endChunker, String templateId) {
		LOG.trace("max "+maxRepeatCount+" "+startChunker+"|"+endChunker+ " | "+templateId);
		nodeIndex = 0;
		int repeatCount = 0; // use this later
		List<Element> chunkedElements = new ArrayList<Element>();
		while (repeatCount < maxRepeatCount) {
			Element chunk = findNextChunk(startChunker, endChunker);
			if (chunk == null) {
				break;
			}
			chunk.addAttribute(new Attribute(TEMPLATE_REF, templateId));
//			CMLUtil.debug(chunk,"CHUNK--------");
			chunkedElements.add(chunk);
			repeatCount++;
		}
//		System.out.println("CHUNKED elements "+chunkedElements.size()+"=====================");
//		for (Element chunk : chunkedElements) {
//			CMLUtil.debug(chunk, "inlist");
//		}
//		CMLUtil.debug(linesElement, "LINECONTAINER");
		
		return chunkedElements;
	}

	private Element findNextChunk(PatternChunker startChunker, PatternChunker endChunker) {
		Element chunk = null;
		Int2 startRange = findNextMatch(nodeIndex, startChunker);
		if (startRange != null) {
			int start = startRange.getX()+startChunker.getOffset();
			int end = startRange.getY();
			Int2 endRange = findNextMatch(end+1, endChunker);
			if (endRange != null) {
				end = endRange.getX()+endChunker.getOffset();
				nodeIndex = endRange.getY()+1;
			}
			chunk = chunk(start, end);
		}
		return chunk;
	}

	private Int2 findNextMatch(int nodeIndex, PatternChunker startChunker) {
		Int2 contiguous = this.getContiguousTextRange(nodeIndex);
		while (nodeIndex < contiguous.getY()) {
			Int2 start = this.matchLines(nodeIndex, startChunker.getPatternList());
			if (start != null) {
				return start;
			}
			nodeIndex++;
		}
		return null;
	}

	private Element chunk(int start, int end) {
		LOG.debug("chunk "+start+" | "+end);
		Element chunk = new Element(CHUNK);
		for (int i = 0; i < end-start; i++) {
			Node node = linesElement.getChild(start);
			if (node == null) {
				LOG.error("null node: "+start);
				continue;
			}
			if (!(node instanceof Text)) {
				throw new RuntimeException("expected text node, found "+node);
			}
			if (chunk == null) {
				chunk = new Element(CHUNK);
			}
			node.detach();
			chunk.appendChild(node);
		}
		if (chunk != null) {
			linesElement.insertChild(chunk, start);
		}
//		CMLUtil.debug(chunk,"chunk extracted");
		return chunk;
	}

	public void debug(String string) {
		CMLUtil.debug(linesElement, string);
	}

	public Element getLinesElement() {
		return linesElement;
	}

}
