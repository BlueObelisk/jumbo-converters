package org.xmlcml.cml.converters;

import java.io.File;

import org.junit.Test;

public class SimpleConverterTest {

	@Test
	public void testConverter() {
		SimpleConverter converter = new SimpleConverter();
		converter.convert(new File("src/test/resources/org/xmlcml/cml/converters/text/text1.txt"), new File("target/text1.txt"));
//		                           \src\test\resources\org\xmlcml\cml\converters\text
	}
}
