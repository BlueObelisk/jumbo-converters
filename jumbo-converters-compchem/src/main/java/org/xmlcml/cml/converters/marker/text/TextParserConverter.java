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
	public final static String[] typicalArgsForConverterCommand = {
		"-sd", "marker/text",
		"-odir", "../temp",
		"-is", "txt",
		"-os", "cml",
		"-converter", "org.xmlcml.cml.converters.marker.text.TextParserConverter"
	};
	
	/**
	 * 
	 */
	@Override
	public Element convertToXML(List<String> lines) {
		super.convertToXML(lines);
		return getCmlModule();
	}
	
	@Override
	public int getConverterVersion() {
		return 0;
	}

}
