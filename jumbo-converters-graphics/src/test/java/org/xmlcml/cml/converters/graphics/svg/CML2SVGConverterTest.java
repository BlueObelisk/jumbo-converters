package org.xmlcml.cml.converters.graphics.svg;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.converters.testutils.RegressionSuite;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.graphics.SVGGBox;
import org.xmlcml.cml.graphics.SVGSVG;
import org.xmlcml.cml.testutil.JumboTestUtils;
import org.xmlcml.cml.tools.MoleculeDisplay;
import org.xmlcml.cml.tools.MoleculeTool;

public class CML2SVGConverterTest {

   @Test
   public void testConverter() {
      RegressionSuite.run("graphics/cml2svg", "cml", "svg",
                          new CML2SVGConverter());
   }

   @Test
   @Ignore("Test output for mol.cml is nothing like the ref - large problem")
   public void test2CMLConverter() {
      RegressionSuite.run("graphics/svg2cml", "svg", "cml",
                          new SVG2CMLConverter());
   }
   
   @Test
   public void createSVG() {
	   CMLMolecule molecule = (CMLMolecule) JumboTestUtils.parseValidFile("graphics/cml2svg/in/crystal.cml").getChildElements().get(0);
	   MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
	   moleculeTool.scaleAverage2DBondLength(50.0);
	   SVGGBox svgg = moleculeTool.draw(MoleculeDisplay.getDEFAULT());
	   SVGSVG svgSvg = SVGSVG.wrapAsSVG(svgg);
	   svgSvg.debug("SVG");
	   
//	   CML2SVGConverter cml2svgConverter = new CML2SVGConverter();
//	   CMLElement cmlIn = (CMLElement) JumboTestUtils.parseValidFile("graphics/cml2svg/in/crystal.cml");
//	   CMLEditorConverter cmlEditorConverter = new CMLEditorConverter();
//	   cmlEditorConverter.getCmlEditor().setScale2D(50.0);
//	   Element svgOut = cml2svgConverter.convertToXML(cmlIn);
//	   ((SVGElement) svgOut).debug("SVG");
   }
}
