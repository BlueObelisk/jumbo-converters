package org.xmlcml.cml.converters;

import java.io.StringReader;

import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Nodes;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLSymmetry;


public abstract class AbstractCompchemCML2XHTMLConverter extends AbstractConverter implements CMLConstants {

	private static final Logger LOG = Logger.getLogger(AbstractCompchemCML2XHTMLConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	
	public Type getInputType() {
		return Type.CML;
	}
	
	public Type getOutputType() {
		return Type.XHTML;
	}

	public AbstractCompchemCML2XHTMLConverter() {
		;
	}
	
	protected void init() {
		super.init();
		clearVars();
	}
	
	private void clearVars() {
		
	}

	protected Element createSingleSummaryPage(ChemistrySummary cs, Element cmlxml) {
		
		String xmls = cmlxml.toXML();
		StringReader sr = new StringReader(xmls);
		CMLCml cml;
		try {
			cml = (CMLCml) new CMLBuilder().build(sr).getRootElement();
		} catch (Exception e1) {
			throw new RuntimeException("bad cml file: ", e1);	
		}
		Nodes titleNodes = cml.query(".//cml:molecule[@title]", CML_XPATH);
		if (titleNodes.size() > 0) {
			cs.title = ((CMLMolecule)titleNodes.get(0)).getTitle();
		}
		
		Nodes idNodes = cml.query("/cml:cml[@id] | .//cml:molecule[@id]", CML_XPATH);
		if (idNodes.size() > 0) {
			cs.id = ((CMLElement)idNodes.get(0)).getId();
		}

		Nodes formulaNodes = cml.query(".//cml:formula[@concise]", CML_XPATH);
		if (formulaNodes.size() > 0) {
			CMLFormula formula = (CMLFormula)formulaNodes.get(0);
			cs.formulaSum = CMLFormula.getSubscriptedConcise(formula.getConcise()).toXML();
			cs.formulaSum = cs.formulaSum.replace(S_WHITEREGEX, S_EMPTY);
		}

		formulaNodes = cml.query(".//cml:formula[@inline]", CML_XPATH);
		if (formulaNodes.size() > 0) {
			cs.formulaInline = ((CMLFormula)formulaNodes.get(0)).getInline();
		}

		Nodes doiNodes = cml.query("//cml:scalar[@dictRef=\"idf:doi\"]", CML_XPATH);
		cs.doi = "none";
		if (doiNodes.size() != 0) {
			cs.doi = doiNodes.get(0).getValue();
		}
		cs.inchi = "";
		Nodes molNodes = cml.query("./cml:molecule", CML_XPATH);
		if (molNodes.size() > 0) {
			CMLMolecule mol = (CMLMolecule)molNodes.get(0);
			molNodes = mol.query("./cml:identifier[@convention=\"iupac:inchi\"]", CML_XPATH);
			if (molNodes.size() != 0) {
				cs.inchi = molNodes.get(0).getValue();
				cs.inchi = cs.inchi.replaceAll("-", "-<wbr />");
			}
		}
		cs.smiles = "";
		Nodes molNodes2 = cml.query("./cml:molecule", CML_XPATH);
		if (molNodes2.size() > 0) {
			CMLMolecule mol = (CMLMolecule)molNodes2.get(0);
			molNodes2 = mol.query("./cml:identifier[@convention=\"daylight:smiles\"]", CML_XPATH);
			if (molNodes2.size() != 0) {
				cs.smiles = molNodes2.get(0).getValue();
				cs.smiles = cs.smiles.replaceAll("\\)", ")<wbr />");
			}
		}

		cs.author = "";
		Nodes authorNodes = cml.query(".//cml:metadata[@name='cml:author']", CML_XPATH);
		if (authorNodes.size() != 0) {
			cs.author = authorNodes.get(0).getValue();
		}
		
		cs.email = "";
		Nodes emailNodes = cml.query(".//cml:metadata[@name='cml:email']", CML_XPATH);
		if (emailNodes.size() != 0) {
			cs.email = emailNodes.get(0).getValue();
		}
		
		cs.website = "";
		Nodes websiteNodes = cml.query(".//cml:metadata[@name='cml:website']", CML_XPATH);
		if (websiteNodes.size() != 0) {
			cs.website = websiteNodes.get(0).getValue();
		}
		
		cs.rights = "";
		Nodes rightsNodes = cml.query(".//cml:metadata[@name='cml:rights']", CML_XPATH);
		if (rightsNodes.size() != 0) {
			cs.rights = rightsNodes.get(0).getValue();
		}
		
		cs.method = "";
		Nodes methodNodes = cml.query(".//cml:parameter[contains(@dictRef,'gauss:method')]/cml:scalar", CML_XPATH);
		if (methodNodes.size() != 0) {
			cs.method = methodNodes.get(0).getValue();
			LOG.trace("METHOD "+cs.method);
		}

		cs.basisSet = "";
		Nodes basisSetNodes = cml.query(".//cml:parameter[contains(@dictRef,'gauss:basis')]/cml:scalar", CML_XPATH);
		if (basisSetNodes.size() != 0) {
			cs.basisSet = basisSetNodes.get(0).getValue();
		}

		cs.hfvalue = "";
		Nodes hfvalueNodes = cml.query(".//cml:property[contains(@dictRef,'gauss:hfvalue')]/cml:scalar", CML_XPATH);
		if (hfvalueNodes.size() != 0) {
			cs.hfvalue = hfvalueNodes.get(0).getValue();
		}

		cs.dipole = "";
		Nodes dipoleNodes = cml.query(".//cml:property[contains(@dictRef,'gauss:dipolevalue')]/cml:vector3", CML_XPATH);
		if (dipoleNodes.size() != 0) {
			cs.dipole = dipoleNodes.get(0).getValue();
		}

		cs.pointGroup = "";
		Nodes pointGroupNodes = cml.query(".//cml:symmetry[@pointGroup]", CML_XPATH);
		if (pointGroupNodes.size() != 0) {
			cs.pointGroup = ((CMLSymmetry)pointGroupNodes.get(0)).getPointGroup();
		}

      //TODO Fix me, need the Freemarker functionality replacing. 
		String page = "";// cs.getWebpage();
		sr = new StringReader(page);
		Element html = null;
		try {
			html = new Builder().build(sr).getRootElement();
		} catch (Exception e) {
			throw new RuntimeException("bad Webpage HTML ");
		}
		return html;
	}

}
