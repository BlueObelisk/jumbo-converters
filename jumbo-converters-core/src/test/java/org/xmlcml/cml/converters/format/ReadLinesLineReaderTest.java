package org.xmlcml.cml.converters.format;

import static org.junit.Assert.fail;
import nu.xom.Element;

import org.junit.Test;
import org.xmlcml.cml.base.CMLUtil;

public class ReadLinesLineReaderTest {

//	@Test
//	public void testReadLinesAndParse() {
//		fail("Not yet implemented");
//	}

	@Test
	public void testScalarsLineReaderElement() {
		String templateXML = "<templateList>" +
				"  <template name='foo' id='foo'>" +
				"    <readLines format='FORTRAN'>(/,/,/)</readLines>" +
				"  </template>" +
				"</templateList>";
		Element template = CMLUtil.parseXML(templateXML);
		String scalarXML = "<scalar>\n" +
				"foo\n" +
				"bar\n" +
				"plugh\n" +
				"baz\n" +
		"</scalar>";
	}

}
