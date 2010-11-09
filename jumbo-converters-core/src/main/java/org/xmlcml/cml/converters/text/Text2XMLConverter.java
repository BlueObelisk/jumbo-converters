package org.xmlcml.cml.converters.text;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.euclid.Util;

public class Text2XMLConverter extends AbstractConverter {

	private static final String STAGO = "<";
	private static final String MARKER = "marker";
	private List<String> lines;
	private String markerResourceName;
	private List<ChunkerMarker> markerList;

	public Type getInputType() {
		return Type.TXT;
	}
	
	public Type getOutputType() {
		return Type.XML;
	}
	
	public Text2XMLConverter() {
		// must be faithful to whitespace
		this.setIndent(0);
	}
	
	@Override
	public Element convertToXML(List<String> lines) {
		List<String> linesCopy = this.convertToIntermediateText(lines);
		Element element = createXML(linesCopy);
		element = processIntoBlocks(element);
		return element;
	}

	/**
	 * override if further processing is required
	 * @param element
	 * @return
	 */
	public Element processIntoBlocks(Element element) {
		return element;
	}
	public void setMarkerResourceName(String resourceName) {
		this.markerResourceName = resourceName;
	}
	
	public String getMarkerResourceName() {
		return markerResourceName;
	}
	
	private InputStream getMarkerInputStream() {
		InputStream is = null;
		try {
			is = Util.getInputStreamFromResource(getMarkerResourceName());
		} catch (Exception e) {
			throw new RuntimeException("Cannot read input stream", e);
		}
		return is;
	}
	protected void readMarkers(InputStream is) {
		Document doc = CMLUtil.parseQuietlyToDocument(is);
		markerList = new ArrayList<ChunkerMarker>();
		Elements childElements = doc.getRootElement().getChildElements();
		for (int i = 0; i < childElements.size(); i++) {
			Element childElement = childElements.get(i);
			if (childElement.getLocalName() != MARKER) {
				throw new RuntimeException("Expected marker elemnt, found: "+childElement.getLocalName());
			}
			ChunkerMarker marker = new ChunkerMarker(childElement);
			markerList.add(marker);
		}
	}

	private void read(List<String> lines) {
		this.lines = new ArrayList<String>(lines.size());
		for (String line : lines) {
			this.lines.add(line);
		}
	}
	
	private List<String> convertToIntermediateText(List<String> lines) {
		readMarkers(getMarkerInputStream());
		List<String> linesCopy = new ArrayList<String>(lines.size());
		int lineCount = 0;
		for (lineCount = 0;lineCount < lines.size();lineCount++) {
			String line = lines.get(lineCount);
			for (ChunkerMarker marker : markerList) {
				if (marker.matches(line)) {
					int lineCount0 = lineCount;
					lineCount += marker.getOffset();
					String markedLine = lines.get(lineCount);
					String markup = marker.getMarkup(markedLine);
					lineCount = lineCount0;
					linesCopy.add(markup);
				}
			}
			linesCopy.add(line);
		}
		return linesCopy;
	}

	private CMLElement createXML(List<String> lines) {
		CMLCml cml = new CMLCml();
		CMLScalar scalar = null;
		StringBuilder sb = null;
		Element lineXML = null;
		for (String line : lines) {
			if (isXML(line)) {
				lineXML = null;
				try {
					lineXML = CMLUtil.parseXML(line);
				} catch (Exception e) {
					throw new RuntimeException("BUG", e);
				}
				sb = processScalar(scalar, sb, cml);
			} else {
				if (sb == null) {
					sb = new StringBuilder();
					scalar = new CMLScalar();
					if (lineXML != null) {
						CMLUtil.copyAttributes(lineXML, scalar);
					}
				}
				sb.append(line);
				sb.append("\n");
			}
		}
		sb = processScalar(scalar, sb, cml);
		return cml;
	}
	
	private StringBuilder processScalar(CMLScalar scalar, StringBuilder sb, CMLCml cml) {
		if (scalar != null) {
			if (sb != null) {
				String value = sb.toString();
				scalar.setValueNoTrim(value);
				cml.appendChild(scalar);
			}
			sb = null;
		}
		return sb;
	}

	private boolean isXML(String line) {
		boolean isXML = false;
		if (line.startsWith(STAGO)) {
			try {
				CMLUtil.parseXML(line);
				isXML = true;
			} catch (Exception e) {
				// not XML
			}
		}
		return isXML;
	}
	
	
}
