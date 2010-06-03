package org.xmlcml.cml.converters.graphics.png;

import java.io.File;

import nu.xom.Document;
import nu.xom.Element;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLMolecule;

public class CML2PNGConverterTest {

	  @Test
	  @Ignore
	   public void testConverter1() {
		  CML2PNGConverter converter = new CML2PNGConverter();
	      Element molecule = CMLUtil.parseQuietlyToDocument(
	    		  new File("src/test/resources/graphics/cml2png/in/crystal.cml")).getRootElement().getChildElements().get(0);
	      byte[] image = converter.convertToBytes(molecule);

	      Assert.assertTrue("bytes ", image.length > 1000);
	   }
	  
	  @Test
	public void testConverter2() throws Exception {
      CMLBuilder cb = new CMLBuilder();
      Document d = cb.build(new File("src/test/resources/graphics/cml2png/in/crystal1.xml"));
      Cml2Png cml2png = new Cml2Png((CMLMolecule) d.getRootElement().getChildElements().get(0));
      cml2png.renderMolecule("out.png");
   }
}