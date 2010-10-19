package org.xmlcml.cml.converters.marker.text;

import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.Converter;
import org.xmlcml.cml.converters.marker.AbstractParser;
import org.xmlcml.cml.converters.marker.TopTemplateContainer;

public class TextParserConverter extends AbstractParser implements
		Converter {

	private static final Logger LOG = Logger.getLogger(TextParserConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	};
	
	/**
	 * 
	 */
	@Override
	public Element convertToXML(List<String> lines) {
		super.convertToXML(lines);
		return getCmlModule();
	}
	
}
