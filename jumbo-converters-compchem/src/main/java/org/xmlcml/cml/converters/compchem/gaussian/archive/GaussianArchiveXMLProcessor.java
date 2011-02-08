package org.xmlcml.cml.converters.compchem.gaussian.archive;

import nu.xom.Nodes;

import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.cml.RawXML2CMLProcessor;
import org.xmlcml.cml.converters.compchem.gamessuk.GamessukCommon;
import org.xmlcml.cml.converters.compchem.gaussian.GaussianCommon;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.tools.MoleculeTool;

public class GaussianArchiveXMLProcessor extends RawXML2CMLProcessor {

	public GaussianArchiveXMLProcessor() {
		
	}
	
	@Override
	protected AbstractCommon getCommon() {
		return new GaussianCommon();
	}
	protected void processXML() {
		xmlInput = CMLBuilder.ensureCML(xmlInput);
//		wrapWithProperty("./*[local-name()='scalar']");
		calculateFormulaAndBonds();
	}

	private void calculateFormulaAndBonds() {
		CMLMolecule molecule = (CMLMolecule) xmlInput.query(".//*[local-name()='molecule']").get(0);
		MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
		moleculeTool.calculateBondedAtoms();
		moleculeTool.adjustBondOrdersToValency();
		CMLFormula formula = moleculeTool
				.getCalculatedFormula(CMLMolecule.HydrogenControl.USE_EXPLICIT_HYDROGENS);
		formula.setDictRef("cml:calculatedFormula");
		molecule.addFormula(formula);
	}

	
}
