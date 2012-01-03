package org.xmlcml.cml.converters.compchem.gaussian.archive;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.compchem.AbstractCompchem2CMLConverter;
import org.xmlcml.cml.converters.compchem.gaussian.GaussianCommon;

public class GaussianArchiveXML2CMLConverter extends AbstractCompchem2CMLConverter{
	private static final Logger LOG = Logger.getLogger(GaussianArchiveXML2CMLConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	}

	public GaussianArchiveXML2CMLConverter() {
	}

   @Override
   protected AbstractCommon getCommon() {
	   return new GaussianCommon();
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
	public Element convertToXML(Element xml) {
		rawXml2CmlProcessor = new GaussianArchiveXMLProcessor();
		return convert(xml);
	}

	@Override
	public String getRegistryInputType() {
		return GaussianCommon.ARCHIVE_XML;
	}

	@Override
	public String getRegistryOutputType() {
		return GaussianCommon.ARCHIVE_CML;
	}

	@Override
	public String getRegistryMessage() {
		return "Gaussian archive XML to CML";
	}

}
