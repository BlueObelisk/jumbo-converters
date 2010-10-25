package org.xmlcml.cml.converters.compchem.gaussian;

import nu.xom.Nodes;

import org.xmlcml.cml.converters.cml.RawXML2CMLProcessor;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.tools.MoleculeTool;

public class GaussianArchiveXMLProcessor extends RawXML2CMLProcessor {

	protected void processXML() {
		wrapWithProperty("./*[local-name()='scalar']");
		calculateFormulaAndBonds();
	}

	private void calculateFormulaAndBonds() {
		CMLMolecule molecule = (CMLMolecule) cmlElement.query(".//*[local-name()='molecule']").get(0);
		MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
		moleculeTool.calculateBondedAtoms();
		moleculeTool.adjustBondOrdersToValency();
		CMLFormula formula = moleculeTool
				.getCalculatedFormula(CMLMolecule.HydrogenControl.USE_EXPLICIT_HYDROGENS);
		formula.setDictRef("cml:calculatedFormula");
		molecule.addFormula(formula);
		Nodes formulaElements = molecule.query("./*[local-name()='formula']");
		// Jmol doesn't like child atomArray
		for (int i = 0; i < formulaElements.size(); i++) {
			CMLFormula formulax = (CMLFormula) formulaElements.get(i);
			formulax.detachAllAtomArraysAsTheyAreAMenace();
		}
	}

	
}
