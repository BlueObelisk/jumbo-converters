package org.xmlcml.cml.converters.graphics.osra;

import java.io.File;
import java.util.List;

import nu.xom.Element;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class Graphics2CMLConverterTest {

	@Test
	public void dummy() {
		Assert.assertTrue("dummy", true);
	}
	/*
	 * Tests are not portable (rely on OSRA.exe)o
	 */
	  @Test
	  @Ignore
	   public void testConverter1() {
	      Graphics2CMLConverter converter = new Graphics2CMLConverter();
	      File image = new File("src/test/resources/graphics/osra/ci5085.gif");
	      List<String> smilesList = converter.convertToText(image);
	      Assert.assertEquals("Smiles ", 1, smilesList.size());
	      Assert.assertEquals("Smiles ", "Clc1ccc(cc1)COC(Cn1nnc2ccccc12)c1ccccc1", smilesList.get(0));
	   }
	  
	  @Test
	  @Ignore
	   public void testConverter2() {
	      Graphics2CMLConverter converter = new Graphics2CMLConverter();
	      File image = new File("src/test/resources/graphics/osra/ci5085.gif");
	      Element cml = converter.convertToXML(image);
//	      CMLUtil.debug(cml, "IMG CML");
	      Assert.assertNotNull(cml);
	   }

   }
