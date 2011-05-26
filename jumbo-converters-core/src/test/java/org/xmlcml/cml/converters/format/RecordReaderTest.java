package org.xmlcml.cml.converters.format;

import java.util.List;
import java.util.regex.Pattern;

import junit.framework.Assert;
import nu.xom.Element;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.text.LineContainer;
import org.xmlcml.cml.testutil.JumboTestUtils;


public class RecordReaderTest {

	@Test
	public void createRecord() {
		Element recordElement = CMLUtil.parseXML("<record id='i1' formatType='REGEX'>line(.*)</record>");
		RecordReader recordReader = new RecordReader(recordElement, null);
		RegexProcessor regexProcessor = recordReader.getRegexProcessor();
		Assert.assertEquals("patterns", 1, regexProcessor.getPatternList().size());
		Assert.assertEquals("names", "", regexProcessor.getNames());
		Assert.assertEquals("types", "", regexProcessor.getTypes());
	}
	
	@Test
	public void createRecordWithNamesAndTypes() {
		Element recordElement = CMLUtil.parseXML("<record id='i1' " +
				"formatType='REGEX'>line{A,a:line}junk{I,a:junk}grot{F,a:grot}</record>");
		RecordReader recordReader = new RecordReader(recordElement, null);
		RegexProcessor regexProcessor = recordReader.getRegexProcessor();
		List<Pattern> patternList = regexProcessor.getPatternList();
		Assert.assertEquals("patterns", 1, patternList.size());
		Assert.assertEquals("pattern 0", 
				"line((?:\\s*(?:\\S+)\\s*){1,1})junk((?:\\s*(?:\\-?\\d+)\\s*){1,1})grot((?:\\s*(?:(?:\\-?\\d+\\.?\\d*)|(?:\\-?\\d*\\.?\\d+))\\s*){1,1})", 
				patternList.get(0).toString());

		
		Assert.assertEquals("names", "a:line a:junk a:grot", regexProcessor.getNames());
//		Assert.assertEquals("types", 
//				CMLConstants.XSD_STRING+" "+CMLConstants.XSD_INTEGER+" "+CMLConstants.XSD_DOUBLE,
//				regexProcessor.getTypes());
		Assert.assertEquals("types", "A I F",
				regexProcessor.getTypes());
	}
	
	
	@Test
	public void createRecordWithFixedWidthNamesAndTypes() {
		Element recordElement = CMLUtil.parseXML(
				"<record id='i1' formatType='REGEX'>line{A5,a:line}junk{I8,a:junk}grot{F10.3,a:grot}</record>");
		RecordReader recordReader = new RecordReader(recordElement, null);
		RegexProcessor regexProcessor = recordReader.getRegexProcessor();
		List<Pattern> patternList = regexProcessor.getPatternList();
		Assert.assertEquals("patterns", 1, patternList.size());
		Assert.assertEquals("pattern 0", 
				"line((?:(?:(?=[ ]*\\S+)[ \\S]{5})){1,1})junk((?:(?:(?=[ ]*\\-?\\d+)[ \\-\\d]{8})){1,1})grot((?:(?:(?=[ ]*\\-?\\d+)[ \\-\\d]{6}\\.\\d{3})){1,1})", 
				patternList.get(0).toString());
		Assert.assertEquals("names", "a:line a:junk a:grot", regexProcessor.getNames());
//		Assert.assertEquals("types", 
//				CMLConstants.XSD_STRING+" "+CMLConstants.XSD_INTEGER+" "+CMLConstants.XSD_DOUBLE,
//				regexProcessor.getTypes());
		Assert.assertEquals("types", "A I F", regexProcessor.getTypes());
	}

	@Test
	public void getNormalizedLineElement() {
		LineContainer lineContainer = new LineContainer("line1\nline2\nline3");
		Element ref = CMLUtil.parseXML( 
			"<module xmlns='http://www.xml-cml.org/schema' xmlns:cmlx='http://www.xml-cml.org/schema/cmlx'>"+
		      "<l xmlns=''>line1</l>"+
		      "<l xmlns=''>line2</l>"+
		      "<l xmlns=''>line3</l>"+
		    "</module>"
		    );
		Element element = lineContainer.getNormalizedLinesElement();
		JumboTestUtils.assertEqualsCanonically("lines", ref, element, true);
	}

	@Test
	@Ignore // FIXME
	public void readRecord() {
		LineContainer lineContainer = new LineContainer("line1\nline2\nline3");
		Element recordElement = CMLUtil.parseXML("<record id='i1' formatType='REGEX'>line(.*)</record>");
		RecordReader recordReader = new RecordReader(recordElement, null);
		recordReader.applyMarkup(lineContainer);
		Element ref = CMLUtil.parseXML(
			"<?xml version='1.0' encoding='UTF-8'?>"+
			"<module xmlns='http://www.xml-cml.org/schema'>"+
			"  <list cmlx:templateRef='i1'>"+
			"    <list>"+
			"      <scalar dataType='xsd:string'>1</scalar>"+
			"    </list>"+
			"  </list>line2line3"+
			"</module>"+
			"");
		JumboTestUtils.assertEqualsCanonically("record1", ref, lineContainer.getLinesElement(), true);
		Assert.assertEquals("record", 1, lineContainer.getCurrentNodeIndex());
	}
	
	@Test
	@Ignore // FIXME
	public void readRecordRepeat() {
		LineContainer lineContainer = new LineContainer("line1\nline2\nline3");
		Element recordElement = CMLUtil.parseXML("<record id='i1' repeat='2' formatType='REGEX'>line(.*)</record>");
		RecordReader recordReader = new RecordReader(recordElement, null);
		recordReader.applyMarkup(lineContainer);
		Element ref = CMLUtil.parseXML(
				"<?xml version='1.0' encoding='UTF-8'?>"+
				"<module xmlns='http://www.xml-cml.org/schema'>"+
				"  <list cmlx:templateRef='i1'>"+
				"    <list>"+
				"      <scalar dataType='xsd:string'>1</scalar>"+
				"    </list>"+
				"    <list>"+
				"      <scalar dataType='xsd:string'>2</scalar>"+
				"    </list>"+
				"  </list>line3"+
				"</module>"+
				"");
			JumboTestUtils.assertEqualsCanonically("record1", ref, lineContainer.getLinesElement(), true);
			// note than line count is after the adjustment for matched lines
			Assert.assertEquals("repeat", 1, lineContainer.getCurrentNodeIndex());
	}
	
	@Test
	public void readRecordFail() {
		LineContainer lineContainer = new LineContainer("grot1\nline2\nline3");
		Element recordElement = CMLUtil.parseXML("<record id='i1' repeat='2' formatType='REGEX'>line(.*)</record>");
		RecordReader recordReader = new RecordReader(recordElement, null);
		recordReader.applyMarkup(lineContainer);
		Element ref = CMLUtil.parseXML(
				"<?xml version='1.0' encoding='UTF-8'?>"+
				"<module xmlns='http://www.xml-cml.org/schema' xmlns:cmlx='http://www.xml-cml.org/schema/cmlx'>grot1\nline2\nline3\n"+
				"<list cmlx:templateRef='i1'/>"+
				"</module>"+
				"");
			JumboTestUtils.assertEqualsCanonically("record1", ref, lineContainer.getLinesElement(), true);
			Assert.assertEquals("recordFail", 0, lineContainer.getCurrentNodeIndex());
	}
	
	@Test
	@Ignore // FIXME
	public void readRecordRepeat2() {
		LineContainer lineContainer = new LineContainer("line1\ngrot2\nline3");
		Element recordElement = CMLUtil.parseXML("<record id='i1' repeat='2' " +
				"formatType='REGEX'>line(.*)</record>");
		RecordReader recordReader = new RecordReader(recordElement, null);
		recordReader.applyMarkup(lineContainer);
		Element ref = CMLUtil.parseXML(
				"<?xml version='1.0' encoding='UTF-8'?>"+
				"<module xmlns='http://www.xml-cml.org/schema'>"+
				"  <list cmlx:templateRef='i1'>"+
				"    <list>"+
				"      <scalar dataType='xsd:string'>1</scalar>"+
				"    </list>"+
				"  </list>grot2line3"+
				"</module>"+
				"");
			JumboTestUtils.assertEqualsCanonically("record1", ref, lineContainer.getLinesElement(), true);
		Assert.assertEquals("repeat2", 1, lineContainer.getCurrentNodeIndex());
	}
	
	@Test
	@Ignore // loops
	public void readRecordRepeatStar() {
		LineContainer lineContainer = new LineContainer("line1\nline2\nline3");
		Element recordElement = CMLUtil.parseXML("<record id='i1' repeat='*' formatType='REGEX'>line(.*)</record>");
		RecordReader recordReader = new RecordReader(recordElement, null);
		recordReader.applyMarkup(lineContainer);
		Element ref = CMLUtil.parseXML(
				"<?xml version='1.0' encoding='UTF-8'?>"+
				"<module xmlns='http://www.xml-cml.org/schema'>"+
				"  <list cmlx:templateRef='i1'>"+
				"    <list>"+
				"      <scalar dataType='xsd:string'>1</scalar>"+
				"    </list>"+
				"    <list>"+
				"      <scalar dataType='xsd:string'>2</scalar>"+
				"    </list>"+
				"    <list>"+
				"      <scalar dataType='xsd:string'>3</scalar>"+
				"    </list>"+
				"  </list>"+
				"</module>"+
				"");
			JumboTestUtils.assertEqualsCanonically("record1", ref, lineContainer.getLinesElement(), true);
			Assert.assertEquals("repeat", 1, lineContainer.getCurrentNodeIndex());
	}
	
	@Test
	@Ignore // FIXME
	public void readRecordMid() {
		LineContainer lineContainer = new LineContainer("line1\nline2\nline3");
		Element recordElement = CMLUtil.parseXML("<record id='i1' repeat='*' " +
				"formatType='REGEX'>line(.*)</record>");
		RecordReader recordReader = new RecordReader(recordElement, null);
		recordReader.applyMarkup(lineContainer);
		Element ref = CMLUtil.parseXML(
				"<?xml version='1.0' encoding='UTF-8'?>"+
				"<module xmlns='http://www.xml-cml.org/schema'>"+
				"  <list cmlx:templateRef='i1'>"+
				"    <list>"+
				"      <scalar dataType='xsd:string'>1</scalar>"+
				"    </list>"+
				"    <list>"+
				"      <scalar dataType='xsd:string'>2</scalar>"+
				"    </list>"+
				"    <list>"+
				"      <scalar dataType='xsd:string'>3</scalar>"+
				"    </list>"+
				"  </list>"+
				"</module>"+
				"");
			JumboTestUtils.assertEqualsCanonically("record1", ref, lineContainer.getLinesElement(), true);
	}
	
	@Test
	@Ignore // FIXME
	public void read2Records() {
		LineContainer lineContainer = new LineContainer("line1\ngrot2\njunk3");
		Element recordElement = CMLUtil.parseXML("<record id='i1' " +
				"formatType='REGEX'>line(.*)</record>");
		RecordReader recordReader = new RecordReader(recordElement, null);
		recordReader.applyMarkup(lineContainer);
		Element ref = CMLUtil.parseXML(
				"<?xml version='1.0' encoding='UTF-8'?>"+
				"<module xmlns='http://www.xml-cml.org/schema'>"+
				"  <list cmlx:templateRef='i1'>"+
				"    <list>"+
				"      <scalar dataType='xsd:string'>1</scalar>"+
				"    </list>"+
				"  </list>grot2junk3"+
				"</module>"+
				"");
		JumboTestUtils.assertEqualsCanonically("record1", ref, lineContainer.getLinesElement(), true);
		// note than line count is after the adjustment for matched lines
		Assert.assertEquals("repeat", 1, lineContainer.getCurrentNodeIndex());
		// now read next line
		recordElement = CMLUtil.parseXML("<record id='i2' repeat='2' formatType='REGEX'>grot(.*)</record>");
		recordReader = new RecordReader(recordElement, null);
		recordReader.applyMarkup(lineContainer);
		ref = CMLUtil.parseXML(
				"<?xml version='1.0' encoding='UTF-8'?>"+
				"<module xmlns='http://www.xml-cml.org/schema'>"+
				"  <list cmlx:templateRef='i1'>"+
				"    <list>"+
				"      <scalar dataType='xsd:string'>1</scalar>"+
				"    </list>"+
				"  </list>"+
				"  <list cmlx:templateRef='i2'>"+
				"    <list>"+
				"      <scalar dataType='xsd:string'>2</scalar>"+
				"    </list>"+
				"  </list>junk3"+
				"</module>"+
				"");
		JumboTestUtils.assertEqualsCanonically("record2", ref, lineContainer.getLinesElement(), true);
		// note than line count is after the adjustment for matched lines
		Assert.assertEquals("repeat", 2, lineContainer.getCurrentNodeIndex());
	}
	
	@Test
	@Ignore // FIXME
	public void readRecordInMiddle() {
		LineContainer lineContainer = new LineContainer("grot1\nline2\njunk3");
		Element recordElement = CMLUtil.parseXML("<record id='i1' " +
				"formatType='REGEX'>line(.*)</record>");
		RecordReader recordReader = new RecordReader(recordElement, null);
		lineContainer.setCurrentNodeIndex(1);
		recordReader.applyMarkup(lineContainer);
		Element ref = CMLUtil.parseXML(
				"<?xml version='1.0' encoding='UTF-8'?>"+
				"<module xmlns='http://www.xml-cml.org/schema'>grot1"+
				"<list cmlx:templateRef='i1'>"+
				"    <list>"+
				"      <scalar dataType='xsd:string'>2</scalar>"+
				"    </list>"+
				"  </list>junk3"+
				"</module>"+
				"");
		JumboTestUtils.assertEqualsCanonically("record1", ref, lineContainer.getLinesElement(), true);
		// note than line count is after the adjustment for matched lines
		Assert.assertEquals("repeat", 2, lineContainer.getCurrentNodeIndex());
		// now read next line
		recordElement = CMLUtil.parseXML("<record id='i2' formatType='REGEX'>junk(.*)</record>");
		recordReader = new RecordReader(recordElement, null);
		recordReader.applyMarkup(lineContainer);
		ref = CMLUtil.parseXML(
				"<?xml version='1.0' encoding='UTF-8'?>"+
				"<module xmlns='http://www.xml-cml.org/schema'>grot1"+
				"<list cmlx:templateRef='i1'>"+
				"    <list>"+
				"      <scalar dataType='xsd:string'>2</scalar>"+
				"    </list>"+
				"  </list>"+
				"  <list cmlx:templateRef='i2'>"+
				"    <list>"+
				"      <scalar dataType='xsd:string'>3</scalar>"+
				"    </list>"+
				"  </list>"+
				"</module>"+
				"");
		JumboTestUtils.assertEqualsCanonically("record2", ref, lineContainer.getLinesElement(), true);
//		CMLUtil.debug(lineContainer.getNormalizedLinesElement(), "AAA");
		// note than line count is after the adjustment for matched lines
		Assert.assertEquals("repeat", 3, lineContainer.getCurrentNodeIndex());
	}
	
	@Test
	public void createMultipleRecord() {
		Element recordElement = CMLUtil.parseXML("<record id='i1' newline='$' formatType='REGEX'>grot(.*)$junk(.*)</record>");
		RecordReader recordReader = new RecordReader(recordElement, null);
		RegexProcessor regexProcessor = recordReader.getRegexProcessor();
		Assert.assertEquals("patterns", 2, regexProcessor.getPatternList().size());
		Assert.assertEquals("pattern0", "grot(.*)", regexProcessor.getPatternList().get(0).toString());
		Assert.assertEquals("pattern1", "junk(.*)", regexProcessor.getPatternList().get(1).toString());
		Assert.assertEquals("names", "", regexProcessor.getNames());
		Assert.assertEquals("types", "", regexProcessor.getTypes());
	}
	
	@Test
	public void createMultipleRecord1() {
		Element recordElement = CMLUtil.parseXML("<record id='i1' newline='$' " +
				"formatType='REGEX'>grot{I,a:grot}$junk{A,a:junk}</record>");
		RecordReader recordReader = new RecordReader(recordElement, null);
		RegexProcessor regexProcessor = recordReader.getRegexProcessor();
		Assert.assertEquals("patterns", 2, regexProcessor.getPatternList().size());
		Assert.assertEquals("pattern0", "grot((?:\\s*(?:\\-?\\d+)\\s*){1,1})", regexProcessor.getPatternList().get(0).toString());
		Assert.assertEquals("pattern1", "junk((?:\\s*(?:\\S+)\\s*){1,1})", regexProcessor.getPatternList().get(1).toString());
		Assert.assertEquals("names", "a:grot a:junk", regexProcessor.getNames());
//		Assert.assertEquals("types", CMLConstants.XSD_INTEGER+" "+CMLConstants.XSD_STRING, regexProcessor.getTypes());
		Assert.assertEquals("types", "I A", regexProcessor.getTypes());
	}
	
	@Test
	@Ignore // FIXME
	// just to check against the next
	public void readMultipleRecord0() {
		LineContainer lineContainer = new LineContainer("grot1\nline2\njunk3");
		Element recordElement = CMLUtil.parseXML("<record id='i1' newline='$' " +
				"formatType='REGEX'>grot(.*)</record>");
		RecordReader recordReader = new RecordReader(recordElement, null);
		recordReader.applyMarkup(lineContainer);
		Element ref = CMLUtil.parseXML(
				"<?xml version='1.0' encoding='UTF-8'?>"+
				"<module xmlns='http://www.xml-cml.org/schema'>"+
				"<list cmlx:templateRef='i1'>"+
				"    <list>"+
				"      <scalar dataType='xsd:string'>1</scalar>"+
				"    </list>"+
				"  </list>line2junk3"+
				"</module>"+
				"");
		JumboTestUtils.assertEqualsCanonically("record1", ref, lineContainer.getLinesElement(), true);
		// note than line count is after the adjustment for matched lines
		Assert.assertEquals("multiple", 1, lineContainer.getCurrentNodeIndex());
	}
	
	@Test
	@Ignore // FIXME
	// just to check against the next
	public void readMultipleRecord00() {
		LineContainer lineContainer = new LineContainer("grot1\nline2\njunk3");
		lineContainer.setCurrentNodeIndex(1);
		Element recordElement = CMLUtil.parseXML("<record id='i1' newline='$' " +
				"formatType='REGEX'>line(.*)</record>");
		RecordReader recordReader = new RecordReader(recordElement, null);
		recordReader.applyMarkup(lineContainer);
		Element ref = CMLUtil.parseXML(
				"<?xml version='1.0' encoding='UTF-8'?>"+
				"<module xmlns='http://www.xml-cml.org/schema'>grot1"+
				"<list cmlx:templateRef='i1'>"+
				"    <list>"+
				"      <scalar dataType='xsd:string'>2</scalar>"+
				"    </list>"+
				"  </list>junk3"+
				"</module>"+
				"");
		JumboTestUtils.assertEqualsCanonically("record1", ref, lineContainer.getLinesElement(), true);
		// note than line count is after the adjustment for matched lines
		Assert.assertEquals("multiple", 2, lineContainer.getCurrentNodeIndex());
	}
	
	@Test
	@Ignore // FIXME
	public void readMultipleRecord() {
		LineContainer lineContainer = new LineContainer("grot1\nline2\njunk3");
		Element recordElement = CMLUtil.parseXML("<record id='i1' newline='$' " +
				"formatType='REGEX'>grot(.*)$line(.*)</record>");
		RecordReader recordReader = new RecordReader(recordElement, null);
		recordReader.applyMarkup(lineContainer);
		Element ref = CMLUtil.parseXML(
				"<?xml version='1.0' encoding='UTF-8'?>"+
				"<module xmlns='http://www.xml-cml.org/schema'>"+
				"<list cmlx:templateRef='i1'>"+
				"    <list>"+
				"      <scalar dataType='xsd:string'>1</scalar>"+
				"      <scalar dataType='xsd:string'>2</scalar>"+
				"    </list>"+
				"  </list>junk3"+
				"</module>"+
				"");
		JumboTestUtils.assertEqualsCanonically("record1", ref, lineContainer.getLinesElement(), true);
		// note than line count is after the adjustment for matched lines
		Assert.assertEquals("multiple", 1, lineContainer.getCurrentNodeIndex());
	}
	
	@Test
	@Ignore // FIXME
	public void readMultipleRecord1() {
		LineContainer lineContainer = new LineContainer("grot1\nline2\njunk3\nfoo4");
		Element recordElement = CMLUtil.parseXML("<record id='i1' newline='$' " +
				"formatType='REGEX'>grot(.*)$line(.*)$junk(.*)</record>");
		RecordReader recordReader = new RecordReader(recordElement, null);
		recordReader.applyMarkup(lineContainer);
		Element ref = CMLUtil.parseXML(
				"<?xml version='1.0' encoding='UTF-8'?>"+
				"<module xmlns='http://www.xml-cml.org/schema'>"+
				"<list cmlx:templateRef='i1'>"+
				"    <list>"+
				"      <scalar dataType='xsd:string'>1</scalar>"+
				"      <scalar dataType='xsd:string'>2</scalar>"+
				"      <scalar dataType='xsd:string'>3</scalar>"+
				"    </list>"+
				"  </list>foo4"+
				"</module>"+
				"");
		JumboTestUtils.assertEqualsCanonically("record1", ref, lineContainer.getLinesElement(), true);
		// note than line count is after the adjustment for matched lines
		Assert.assertEquals("multiple", 1, lineContainer.getCurrentNodeIndex());
	}
	
	@Test
	@Ignore // FIXME
	public void readMultipleRecordWithBlank() {
		LineContainer lineContainer = new LineContainer("grot1\nline2\n\njunk4\ngrot5\n");
		Element recordElement = CMLUtil.parseXML("<record id='i1' newline='$' " +
				"formatType='REGEX'>grot(.*)$line(.*)$$junk(.*)</record>");
		RecordReader recordReader = new RecordReader(recordElement, null);
		recordReader.applyMarkup(lineContainer);
		Element ref = CMLUtil.parseXML(
				"<?xml version='1.0' encoding='UTF-8'?>"+
				"<module xmlns='http://www.xml-cml.org/schema'>"+
				"<list cmlx:templateRef='i1'>"+
				"    <list>"+
				"      <scalar dataType='xsd:string'>1</scalar>"+
				"      <scalar dataType='xsd:string'>2</scalar>"+
				"      <scalar dataType='xsd:string'>4</scalar>"+
				"    </list>"+
				"  </list>grot5"+
				"</module>"+
				"");
		JumboTestUtils.assertEqualsCanonically("record1", ref, lineContainer.getLinesElement(), true);
		// note than line count is after the adjustment for matched lines
		Assert.assertEquals("multiple", 1, lineContainer.getCurrentNodeIndex());
		Assert.assertEquals("multiple", "grot5", lineContainer.peekLine());
	}
	
	@Test
	@Ignore // FIXME
	public void readRepeatedMultipleRecord() {
		LineContainer lineContainer = new LineContainer("grot1\nline2\ngrot3\nline4\ngrot5");
		Element recordElement = CMLUtil.parseXML("<record id='i1' newline='$' repeat='2' formatType='REGEX'>grot(.*)$line(.*)</record>");
		RecordReader recordReader = new RecordReader(recordElement, null);
		recordReader.applyMarkup(lineContainer);
		Element ref = CMLUtil.parseXML(
				"<?xml version='1.0' encoding='UTF-8'?>"+
				"<module xmlns='http://www.xml-cml.org/schema'>"+
				"<list cmlx:templateRef='i1'>"+
				"    <list>"+
				"      <scalar dataType='xsd:string'>1</scalar>"+
				"      <scalar dataType='xsd:string'>2</scalar>"+
				"    </list>"+
				"    <list>"+
				"      <scalar dataType='xsd:string'>3</scalar>"+
				"      <scalar dataType='xsd:string'>4</scalar>"+
				"    </list>"+
				"  </list>grot5"+
				"</module>"+
				"");
		JumboTestUtils.assertEqualsCanonically("record1", ref, lineContainer.getLinesElement(), true);
		// note than line count is after the adjustment for matched lines
		Assert.assertEquals("multiple", 1, lineContainer.getCurrentNodeIndex());
	}
	
	@Test
	@Ignore
	public void readMultipleRecordWithBlank1() {
		LineContainer lineContainer = new LineContainer("grot1\nline2\n\ngrot3\nline4\n\ngrot5");
		Element recordElement = CMLUtil.parseXML("<record id='i1' newline='$' formatType='REGEX'>grot(.*)$line(.*)$</record>");
		RecordReader recordReader = new RecordReader(recordElement, null);
		recordReader.applyMarkup(lineContainer);
		Element ref = CMLUtil.parseXML(
				"<?xml version='1.0' encoding='UTF-8'?>"+
				"<module xmlns='http://www.xml-cml.org/schema'>"+
				"<list cmlx:templateRef='i1'>"+
				"    <list>"+
				"      <scalar dataType='xsd:string'>1</scalar>"+
				"      <scalar dataType='xsd:string'>2</scalar>"+
				"    </list>"+
				"    <list>"+
				"      <scalar dataType='xsd:string'>3</scalar>"+
				"      <scalar dataType='xsd:string'>4</scalar>"+
				"    </list>"+
				"  </list>grot5"+
				"</module>"+
				"");
		JumboTestUtils.assertEqualsCanonically("record1", ref, lineContainer.getLinesElement(), true);
		// note than line count is after the adjustment for matched lines
		Assert.assertEquals("multiple", 1, lineContainer.getCurrentNodeIndex());
	}
	
	@Test
	@Ignore // FIXME
	public void readRepeatedMultipleRecordWithBlank() {
		LineContainer lineContainer = new LineContainer("grot1\nline2\n\ngrot3\nline4\n\ngrot5");
		Element recordElement = CMLUtil.parseXML("<record id='i1' newline='$' repeat='2' " +
				"formatType='REGEX'>grot(.*)$line(.*)$(\\s*)</record>");
		RecordReader recordReader = new RecordReader(recordElement, null);
		recordReader.applyMarkup(lineContainer);
		Element ref = CMLUtil.parseXML(
				"<?xml version='1.0' encoding='UTF-8'?>"+
				"<module xmlns='http://www.xml-cml.org/schema'>"+
				"<list cmlx:templateRef='i1'>"+
				"    <list>"+
				"      <scalar dataType='xsd:string'>1</scalar>"+
				"      <scalar dataType='xsd:string'>2</scalar>"+
				"      <scalar dataType='xsd:string'/>"+
				"    </list>"+
				"    <list>"+
				"      <scalar dataType='xsd:string'>3</scalar>"+
				"      <scalar dataType='xsd:string'>4</scalar>"+
				"      <scalar dataType='xsd:string'/>"+
				"    </list>"+
				"  </list>grot5"+
				"</module>"+
				"");
		JumboTestUtils.assertEqualsCanonically("record1", ref, lineContainer.getLinesElement(), true);
		// note than line count is after the adjustment for matched lines
		Assert.assertEquals("multiple", 1, lineContainer.getCurrentNodeIndex());
	}
	
	@Test
	@Ignore // FIXME
	public void readSymbolic() {
		LineContainer lineContainer = new LineContainer("123");
		Element recordElement = CMLUtil.parseXML("<record id='i1' " +
				"formatType='REGEX' >{I3}.*</record>");
		RecordReader recordReader = new RecordReader(recordElement, null);
		recordReader.applyMarkup(lineContainer);
		Element ref = CMLUtil.parseXML(
				"<?xml version='1.0' encoding='UTF-8'?>"+
				"<module xmlns='http://www.xml-cml.org/schema'>"+
				"  <list cmlx:templateRef='i1'>"+
				"    <list>"+
				"      <scalar dataType='xsd:integer'>123</scalar>"+
				"    </list>"+
				"  </list>"+
				"</module>"+
				"");
		JumboTestUtils.assertEqualsCanonically("record1", ref, lineContainer.getLinesElement(), true);
		// note than line count is after the adjustment for matched lines
		Assert.assertEquals("multiple", 1, lineContainer.getCurrentNodeIndex());
	}
	
	@Test
	@Ignore // FIXME
	public void readSymbolic1() {
		LineContainer lineContainer = new LineContainer("123 abc 123.45");
		Element recordElement = CMLUtil.parseXML("<record id='i1' " +
				"formatType='REGEX' >{I3} {A3} {F6.2}</record>");
		RecordReader recordReader = new RecordReader(recordElement, null);
		recordReader.applyMarkup(lineContainer);
		Element ref = CMLUtil.parseXML(
				"<?xml version='1.0' encoding='UTF-8'?>"+
				"<module xmlns='http://www.xml-cml.org/schema'>"+
				"  <list cmlx:templateRef='i1'>"+
				"    <list>"+
				"      <scalar dataType='xsd:integer'>123</scalar>"+
				"      <scalar dataType='xsd:string'>abc</scalar>"+
				"      <scalar dataType='xsd:double'>123.45</scalar>"+
				"    </list>"+
				"  </list>"+
				"</module>"+
				"");
		JumboTestUtils.assertEqualsCanonically("record1", ref, lineContainer.getLinesElement(), true);
		// note than line count is after the adjustment for matched lines
		Assert.assertEquals("multiple", 1, lineContainer.getCurrentNodeIndex());
	}
	
	@Test
	public void readSymbolicWithNames() {
		LineContainer lineContainer = new LineContainer("123 abc 123.45");
		Element recordElement = CMLUtil.parseXML("<record id='i1' " +
				"formatType='REGEX' >{I3,x:i} {A3,x:a} {F6.2,x:f}\\n</record>");
		RecordReader recordReader = new RecordReader(recordElement, null);
		recordReader.applyMarkup(lineContainer);
		Element ref = CMLUtil.parseXML(
				"<?xml version='1.0' encoding='UTF-8'?>"+
				"<module xmlns='http://www.xml-cml.org/schema' xmlns:cmlx='http://www.xml-cml.org/schema/cmlx'>"+
				"  <list cmlx:templateRef='i1'>"+
				"    <list>"+
				"      <scalar dataType='xsd:integer' dictRef='x:i'>123</scalar>"+
				"      <scalar dataType='xsd:string' dictRef='x:a'>abc</scalar>"+
				"      <scalar dataType='xsd:double' dictRef='x:f'>123.45</scalar>"+
				"    </list>"+
				"  </list>"+
				"</module>"+
				"");
		JumboTestUtils.assertEqualsCanonically("record1", ref, lineContainer.getLinesElement(), true);
		// note than line count is after the adjustment for matched lines
		Assert.assertEquals("multiple", 1, lineContainer.getCurrentNodeIndex());
	}
	
	@Test
	public void readArray0() {
		Element recordElement = CMLUtil.parseXML("<record id='i1' " +
				"formatType='REGEX'>{3I2,x:a,u:g}</record>");
		RecordReader recordReader = new RecordReader(recordElement, null);
		RegexProcessor regexProcessor = recordReader.getRegexProcessor();
//		System.out.println("RR "+regexProcessor);
	}
	
	@Test
	public void readArray00() {
		Element recordElement = CMLUtil.parseXML("<record id='i1' " +
				"formatType='REGEX'>{3I2,x:a}{A4,x:aa}</record>");
		RecordReader recordReader = new RecordReader(recordElement, null);
		RegexProcessor regexProcessor = recordReader.getRegexProcessor();
//		System.out.println("RR "+regexProcessor);
	}
	
	@Test
	@Ignore // FIXME
	public void readArray() {
		LineContainer lineContainer = new LineContainer("123456");
		Element recordElement = CMLUtil.parseXML("<record id='i1' " +
				"formatType='REGEX'>{3I2,x:a,u:g}</record>");
		RecordReader recordReader = new RecordReader(recordElement, null);
		recordReader.applyMarkup(lineContainer);
		Element ref = CMLUtil.parseXML(
				"<?xml version='1.0' encoding='UTF-8'?>"+
				"<module xmlns='http://www.xml-cml.org/schema'>"+
				"<list cmlx:templateRef='i1'>"+
				"<list/>"+
				"</list>"+
				"</module>"+
				"");
		JumboTestUtils.assertEqualsCanonically("record1", ref, lineContainer.getLinesElement(), true);
		// note than line count is after the adjustment for matched lines
		Assert.assertEquals("multiple", 1, lineContainer.getCurrentNodeIndex());
	}
}
