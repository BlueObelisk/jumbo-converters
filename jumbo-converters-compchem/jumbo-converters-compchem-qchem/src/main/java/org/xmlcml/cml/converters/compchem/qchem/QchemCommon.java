package org.xmlcml.cml.converters.compchem.qchem;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractCommon;

/**
 * A collection of common objects such as namespaces, dictionaries used
 * in the Qchem system
 * @author ostueker
 *
 */
public class QchemCommon extends AbstractCommon {
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(QchemCommon.class);
	
	public static final String QCHEM_PREFIX = "qchem";
	public static final String QCHEM_URI = "http://www.xml-cml.org/dictionary/qchem";

	public static final String NSERCH = "NSERCH";
	public static final String STEP = "STEP";
	public static final String RESULTS = "RESULTS";
	public static final String KEYWORD = " $";
	public static final String END = " $END";
	public static final String NCYC = "ncyc";

	public static final String QCHEM_LOG = "qchem_log";
	public static final String QCHEM_LOG_XML = "qchem_log_xml";
	public static final String QCHEM_LOG_CML = "qchem_log_cml";

	public static final String PUNCH = "qchem_punch";
	public static final String PUNCH_XML = "qchem_punch_xml";
	
    protected String getDictionaryResource() {
    	return "org/xmlcml/cml/converters/compchem/qchem/qchemDict.xml";
    }
    
	public String getPrefix() {
		return QCHEM_PREFIX;
	}
	
	public String getNamespace() {
		return QCHEM_URI;
	}
	
}
