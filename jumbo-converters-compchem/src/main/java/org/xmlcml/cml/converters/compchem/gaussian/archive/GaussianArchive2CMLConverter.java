package org.xmlcml.cml.converters.compchem.gaussian.archive;

import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.compchem.AbstractCompchem2CMLConverter;
import org.xmlcml.cml.converters.compchem.gamessus.GamessUSCommon;
import org.xmlcml.cml.converters.compchem.gaussian.GaussianCommon;
import org.xmlcml.cml.element.CMLCml;

public class GaussianArchive2CMLConverter extends AbstractCompchem2CMLConverter{
	private static final Logger LOG = Logger.getLogger(GaussianArchive2CMLConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	
	public GaussianArchive2CMLConverter() {
	}
	
   @Override
   protected AbstractCommon getCommon() {
	   return new GaussianCommon();
   }
	public Type getInputType() {
		return Type.GAU_ARC;
	}

	public Type getOutputType() {
		return Type.CML;
	}

	/**
	 * converts an MDL object to CML. returns cml:cml/cml:molecule
	 * 
	 * @param in input stream
	 */
	public Element convertToXML(List<String> lines) {
		CMLCml topCml = new CMLCml();
		GaussianArchiveOrigProcessor processor = new GaussianArchiveOrigProcessor();
		List<CMLElement> cmlElementList = processor.readArchives(lines);
		for (CMLElement cmlElement : cmlElementList) {
			topCml.appendChild(cmlElement);
		}
		try {
			ensureId(topCml);
		} catch (RuntimeException e) {
			// no id
		}
		topCml = processParamsTopMetadataNamespaces(topCml);
		return topCml;
	}

}
