package org.xmlcml.cml.converters.compchem.qespresso;

import java.io.InputStream;

import nu.xom.Builder;
import nu.xom.Element;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.converters.compchem.qespresso.log.QuantumEspressoLogProcessor;
import org.xmlcml.cml.converters.text.Template;
import org.xmlcml.euclid.Util;

@Ignore // till we fix templates
public class QuantumEspressoLogProcessorTest {

	@Test
	public void test1() throws Exception {
		InputStream stream = Util.getInputStreamFromResource("org/xmlcml/cml/converters/compchem/qespresso/log/template1.xml");
		Element templateElement = new Builder().build(stream).getRootElement();
		new QuantumEspressoLogProcessor(new Template(templateElement));
	}
	
	public void testParse() {
//		QuantumEspressoLogProcessor qep = new QuantumEspressoLogProcessor();
//		String s = "     Program PWSCF v.> 4.2      starts on 10Nov2010 at 16:25:42 ";
//		Element templateXML = qep.getTemplateString();
//		 = CMLUtil.parseXML(templateS);
//		Template template = new Template(CMLUtil.parseXML(templateS));
//		template.applyMarkup(stringToBeParsed);
//		LineContainer lineContainer = template.getLineContainer();
//		Element linesElement = lineContainer.getNormalizedLinesElement();

	}
}
