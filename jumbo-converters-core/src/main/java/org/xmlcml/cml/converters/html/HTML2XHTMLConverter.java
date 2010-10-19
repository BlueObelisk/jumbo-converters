package org.xmlcml.cml.converters.html;

import static org.xmlcml.euclid.EuclidConstants.S_NEWLINE;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import nu.xom.Document;
import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.w3c.tidy.Tidy;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.Util;

/** allows editing of CMLObjects
 * 
 * @author pm286
 *
 */
public class HTML2XHTMLConverter extends AbstractConverter {

	private static final Logger LOG = Logger.getLogger(HTML2XHTMLConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	}

	public Type getInputType() {
		return Type.HTML;
	}
	
	public Type getOutputType() {
		return Type.XHTML;
	}
	
	public HTML2XHTMLConverter() {
	}
	
	/**
	 * reads HTML in inputStream and tidies it.
	 * First with HTML tidy (using as many cleaning options as possible
	 * then excises the DOCTYP and namespace from result
	 * Tidy may throw warnings and errors to syserr than cannot be 
	 * removed from console
	 * @param inputStream if null throws IOException
	 * @return document with some HTML root element
	 * @throws IOException
	 */
	public static Document htmlTidy(InputStream inputStream) throws IOException {
	    	
		if (inputStream == null) {
			throw new RuntimeException("Null input for HTMLTidy");
		}
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	Tidy tidy = createTidyWithOptions();
    	tidy.parse(inputStream, baos );
    	baos.close();
    	Document document = null;
    	String baosS0 = ""+new String(baos.toByteArray());
    	if (baosS0.length() > 0) {
    		document = Util.stripDTDAndOtherProblematicXMLHeadings(baosS0);
    	}
    	return document;
    }

	private static Tidy createTidyWithOptions() {
		Tidy tidy = new Tidy();
    	tidy.setDocType(null);
    	tidy.setXmlOut(true);
    	tidy.setDropEmptyParas(true);
    	tidy.setDropFontTags(true);
    	tidy.setMakeClean(true);
    	tidy.setNumEntities(true);
    	tidy.setXHTML(true);
    	tidy.setQuiet(true);
    	tidy.setQuoteMarks(true);
    	tidy.setShowWarnings(false);
		return tidy;
	}

	/** convert
	 */
	public Element convert(List<String> stringList) {
		Element element = null;
		StringBuilder sb = new StringBuilder();
		for (String s : stringList) {
			sb.append(s+S_NEWLINE);
		}
		element = convertStringToXHTML(sb.toString());
		return element;
	}

	public static Element convertStringToXHTML(String s) {
		Element element = null;
		ByteArrayInputStream bais = new ByteArrayInputStream(s.getBytes());
		try {
			element = HTML2XHTMLConverter.htmlTidy(bais).getRootElement();
		} catch (Exception e) {
			throw new RuntimeException("parse: "+e);
		}
		return element;
	}

}
