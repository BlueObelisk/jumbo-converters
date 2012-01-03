package org.xmlcml.cml.converters.compchem.cml;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractCommon;

/**
 * A collection of common objects such as namespaces, dictionaries used
 * in the Foo system
 * @author pm286
 *
 */
public class CMLCommon extends AbstractCommon {
	
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(CMLCommon.class);
	
	public static final String CML                 = "cml";
	public static final String CML_XML             = "cml-xml";

	@Override
	protected String getDictionaryResource() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getPrefix() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getNamespace() {
		// TODO Auto-generated method stub
		return null;
	}

}
