package org.xmlcml.cml.converters.compchem.qchem.log;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.compchem.AbstractCompchem2CMLConverter;
import org.xmlcml.cml.converters.compchem.qchem.QchemCommon;

public class QchemLogXML2CMLConverter extends AbstractCompchem2CMLConverter{
	private static final Logger LOG = Logger.getLogger(QchemLogXML2CMLConverter.class);
	private static final String QCHEM_LOG_XML_TO_CML = "Q-Chem log to CML";
	static {
		LOG.setLevel(Level.INFO);
	}	
	
	public QchemLogXML2CMLConverter() {
	}

	@Override
	protected AbstractCommon getCommon() {
		return new QchemCommon();
	}

	public Type getInputType() {
		return Type.XML;
	}

	public Type getOutputType() {
		return Type.CML;
	}

	/**
	 * @param xml
	 */
	// public Element convertToXML(Element xml) {
	// 	rawXml2CmlProcessor = new QchemInputXMLProcessor();
	// 	return convert(xml);
	// }

	@Override
	public String getRegistryInputType() {
		return QchemCommon.QCHEM_LOG_XML;
	}
	
	@Override
	public String getRegistryOutputType() {
		return QchemCommon.QCHEM_LOG_CML;
	}
	
	@Override
	public String getRegistryMessage() {
		return QCHEM_LOG_XML_TO_CML;
	}
}
