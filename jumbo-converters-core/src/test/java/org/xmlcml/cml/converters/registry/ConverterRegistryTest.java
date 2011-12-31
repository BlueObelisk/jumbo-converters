package org.xmlcml.cml.converters.registry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;
import org.xmlcml.cml.converters.Converter;

public class ConverterRegistryTest {

	@Test
	public void testRegistryLoadsConverterList() {
		List<ConverterInfo> list = ConverterRegistry.getConverterList();
		assertEquals(2, list.size());
	}
	
	@Test
	public void testFindHtml2TxtConverter() {
		Converter converter = ConverterRegistry.findConverter("html", "txt");
		assertNotNull(converter);
		assertEquals(StubHtml2TxtConverter.class, converter.getClass());
	}
	
	@Test
	public void testFindTxt2PngConverter() {
		Converter converter = ConverterRegistry.findConverter("txt", "png");
		assertNotNull(converter);
		assertEquals(StubTxt2PngConverter.class, converter.getClass());
	}
	
	@Test
	public void testFindFoo2BarConverter() {
		Converter converter = ConverterRegistry.findConverter("foo", "bar");
		assertNull(converter);
	}
	
}
