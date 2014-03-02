package org.xmlcml.cml.converters.compchem;

import java.util.List;

import nu.xom.Element;
import nu.xom.Nodes;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.cml.RawXML2CMLProcessor;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLMetadata;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.tools.MoleculeTool;


public abstract class AbstractCompchem2CMLConverter extends AbstractConverter {

	private static final Logger LOG = Logger.getLogger(AbstractCompchem2CMLConverter.class);
	public final static Double BOHR2ANGSTROM = 0.52917720859;

	static {
		LOG.setLevel(Level.INFO);
	}

	private CMLMolecule molecule;
	protected RawXML2CMLProcessor rawXml2CmlProcessor;
	protected AbstractCommon abstractCommon;

	protected AbstractCompchem2CMLConverter() {
		abstractCommon = getCommon();
	}
	/** form class name for Common and instantiate
	 * assumes package of form: package org.xmlcml.cml.converters.gaussian.log
	 * can always be overridden if names are more tricky
	 * @return
	 */
	protected AbstractCommon getCommon() {
		return null;
	}
	
	public CMLMolecule getMolecule() {
		return molecule;
	}

	public void setMolecule(CMLMolecule molecule) {
		this.molecule = molecule;
	}

	protected void addCommonNamespaces(CMLElement cml) {
		if (cml != null) {
			cml.addNamespaceDeclaration(CMLConstants.XSD_PREFIX, CMLConstants.XSD_NS);
			cml.addNamespaceDeclaration(CMLConstants.CML_UNITS, CMLConstants.UNIT_NS);
			cml.addNamespaceDeclaration(CMLConstants.CML_PREFIX, CMLConstants.CML_NS);
			cml.addNamespaceDeclaration(CMLConstants.CMLX_PREFIX, CMLConstants.CMLX_NS);
		}
	}

	/**
	 * @param topCml
	 * @return
	 */
	protected CMLCml processParamsTopMetadataNamespaces(CMLCml topCml) {
		tidyParametersAndProperties(topCml);
		topCml = tidyTopCml(topCml);
		addMetadata(topCml);
		addNamespaces(topCml);
		return topCml;
	}

	/**
	 * @param topCml
	 * @return
	 */
	private CMLCml tidyTopCml(CMLCml topCml) {
		Nodes cmlNodes;
		cmlNodes = topCml.query("cml:cml", CMLConstants.CML_XPATH);
		// tidy cml
		if (cmlNodes.size() == 0) {
			// keep topCml
		} else if (cmlNodes.size() == 1) {
			String id = topCml.getId();
			topCml = (CMLCml) cmlNodes.get(0);
			if (id != null) {
				topCml.setId(id);
			}
		} else {
			// keep topCml
		}
		return topCml;
	}

	/**
	 * @param topCml
	 */
	private void tidyParametersAndProperties(CMLCml topCml) {
		// tidy parameters and properties
		Nodes cmlNodes = topCml.query("cml:cml", CMLConstants.CML_XPATH);
		for (int i = 0; i < cmlNodes.size(); i++) {
			CMLCml cml = (CMLCml) cmlNodes.get(i);
			Nodes mols = cml.query("cml:molecule", CMLConstants.CML_XPATH);
			CMLMolecule molecule = (CMLMolecule) mols.get(0);
			Nodes others = cml.query("*[not(self::cml:molecule)]", CMLConstants.CML_XPATH);
			for (int j = 0; j < others.size(); j++) {
				others.get(j).detach();
				molecule.appendChild(others.get(j));
			}
		}
	}
	
	protected void addMetadata(CMLElement topCml) {
		
		if (metadataCml != null && topCml != null) {
			LOG.trace("Adding metadata");
			for (int i = 0; i < metadataCml.getChildElements().size(); i++) {
				CMLMetadata metadata = (CMLMetadata) metadataCml.getChildElements().get(i).copy();
				if (metadata != null) {
					topCml.insertChild(metadata, i);
				}
			}
		}
	}

	protected CMLCml processChemistryAndMetadata(CMLCml cml) {
		CMLMolecule molecule = (CMLMolecule)cml.getChildElements().get(0); 
		MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(
			(CMLMolecule)cml.getChildElements().get(0)); 
		moleculeTool.calculateBondedAtoms();
		moleculeTool.adjustBondOrdersToValency();
		CMLFormula formula = new CMLFormula(molecule);
		molecule.appendChild(formula);
		cml = processParamsTopMetadataNamespaces(cml);
		return cml;
	}

	protected CMLElement convert(Element xml) {
		rawXml2CmlProcessor.process(xml);
		CMLElement cml = rawXml2CmlProcessor.getCMLElement();
		addNamespaces(cml);
		return cml;
	}

	protected CMLElement readAndProcess(List<String> lines) {
		legacyProcessor.read(lines);
		return addNamespaces();
	}

	protected CMLElement readAndProcess(CMLElement scalars) {
		legacyProcessor.read(scalars);
		return addNamespaces();
	}
	
	private CMLElement addNamespaces() {
		CMLElement cmlElement = legacyProcessor.getCMLElement();
		addCommonNamespaces(cmlElement);
		addNamespaces(cmlElement);
		return cmlElement;
	}
	
	public void addNamespaces(CMLElement cml) {
		addCommonNamespaces(cml);
		this.abstractCommon.addNamespaceDeclaration(cml);
	}

	public Type getInputType() {
		return Type.XML;
	}
	public Type getOutputType() {
		return Type.CML;
	}
}
