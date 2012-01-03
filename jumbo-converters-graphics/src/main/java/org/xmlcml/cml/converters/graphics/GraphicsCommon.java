package org.xmlcml.cml.converters.graphics;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractCommon;

/**
 * A collection of common objects such as namespaces, dictionaries used
 * in the Graphics system
 * @author pm286
 *
 */
public class GraphicsCommon extends AbstractCommon {

	private static final String GRAPHICS_PREFIX = "graphics";
	private static final String GRAPHICS_URI = "http://wwmm.ch.cam.ac.uk/dict/graphics";

	public static final String INPUT = "graphics_input";
	public static final String OSRA_XML = "osra_xml";

	public static final String LOG = "graphics_log";
	public static final String LOG_XML = "graphics_log_xml";
	public static final String LOG_CML = "graphics_log_compchem";

    protected String getDictionaryResource() {
    	return "org/xmlcml/cml/converters/compchem/graphics/graphicsDictionary.xml";
    }

	public String getPrefix() {
		return GRAPHICS_PREFIX;
	}

	public String getNamespace() {
		return GRAPHICS_URI;
	}

}
