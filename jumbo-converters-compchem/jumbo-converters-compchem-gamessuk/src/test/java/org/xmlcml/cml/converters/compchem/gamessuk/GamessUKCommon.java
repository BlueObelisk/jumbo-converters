package org.xmlcml.cml.converters.compchem.gamessuk;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractCommon;

/**
 * A collection of common objects such as namespaces, dictionaries used
 * in the Foo system
 * @author pm286
 *
 */
public class GamessUKCommon extends AbstractCommon {


	private static final String GAMESS_PREFIX = "amber";
	private static final String GAMESS_URI = "http://wwmm.ch.cam.ac.uk/dict/amber";

	public static final String GAMESS_FF                 = "amber-ff";
	public static final String GAMESS_FF_XML             = "amber-ff-xml";
	public static final String GAMESS_FF_CML             = "amber-ff-cml";

	public static final String GAMESS_MD_XML = "amber-mdout-xml";
	public static final String GAMESS_MD_CML = "amber-mdout-cml";

    protected String getDictionaryResource() {
    	return "org/xmlcml/cml/converters/compchem/amber/amberDict.xml";
    }

	public String getPrefix() {
		return GAMESS_PREFIX;
	}

	public String getNamespace() {
		return GAMESS_URI;
	}

}
