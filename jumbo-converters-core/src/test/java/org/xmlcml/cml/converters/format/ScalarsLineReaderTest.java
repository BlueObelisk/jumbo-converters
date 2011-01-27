package org.xmlcml.cml.converters.format;

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import nu.xom.Element;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.util.JumboReader;

public class ScalarsLineReaderTest {

	@Test
	@Ignore
	public void testReadLinesAndParse() {
		fail("Not yet implemented");
	}

	@Test
	public void testReadLinesReaderElement() {
		String templateXML = "<templateList>" +
				"  <template name='foo' id='foo'>" +
				"    <readLines format='FORTRAN'>(/,/,/)</readLines>" +
				"  </template>" +
				"</templateList>";
		Element template = CMLUtil.parseXML(templateXML);
//		String readLinesXML = "<readLines formatType='FORTRAN'>(/,/,/)</readLines>";
		String readLinesXML = "<readLines formatType='FORTRAN'>(/)</readLines>";
		Element readLines = CMLUtil.parseXML(readLinesXML);
		List<String> lines = Arrays.asList(new String[]{
				"foo",
				"bar",
				"plugh",
				"baz",
		});
		ReadLinesLineReader rllr = new ReadLinesLineReader(readLines);
		JumboReader jumboReader = new JumboReader(SimpleFortranFormatTest.getDictionary(), "pref", lines);
		System.out.println("line0 "+jumboReader.peekLine());
		rllr.readLinesAndParse(jumboReader);
		System.out.println("line1 "+jumboReader.peekLine());
	}

}
