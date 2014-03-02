package org.xmlcml.cml.converters.cmllite;

import java.io.InputStream;

import org.junit.Test;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.testutil.JumboTestUtils;
import org.xmlcml.euclid.Util;

public class CML2CMLLiteConverterTest {
	@Test
	public void reaction() throws Exception 
	{
		InputStream is = Util.getInputStreamFromResource("org/xmlcml/cml/converters/cmllite/in/reaction.map.cml");
		CMLElement element = (CMLElement) new CMLBuilder().build(is).getRootElement();
		CMLElement elementOut = (CMLElement) new CML2CMLLiteConverter().convertToXML(element);
		InputStream ref = Util.getInputStreamFromResource("org/xmlcml/cml/converters/cmllite/ref/reaction.map.cml");
		CMLElement elementRef = (CMLElement) new CMLBuilder().build(ref).getRootElement();
		JumboTestUtils.assertEqualsIncludingFloat("cmllite", elementRef, elementOut, true, 0.00001);
	}
}
