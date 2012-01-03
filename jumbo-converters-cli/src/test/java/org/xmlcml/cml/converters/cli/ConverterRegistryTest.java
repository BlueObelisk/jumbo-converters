package org.xmlcml.cml.converters.cli;

import static org.junit.Assert.assertEquals;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.Ignore;
import org.xmlcml.cml.converters.Converter;
import org.xmlcml.cml.converters.cli.ConverterRegistry;
import org.xmlcml.cml.converters.registry.ConverterInfo;
import org.xmlcml.cml.converters.registry.StubTxt2PngConverter;
import org.xmlcml.cml.converters.registry.StubHtml2TxtConverter;

public class ConverterRegistryTest {

	@Test
	public void testRegistryLoadsConverterList() {
		List<ConverterInfo> list = ConverterRegistry.getConverterList();
		assertTrue(list.size()>0);
	}

	@Test
	@Ignore
	public void testFindHtml2TxtConverter() {
		Converter converter = ConverterRegistry.findConverter("html", "txt");
		assertNotNull(converter);
		assertEquals(StubHtml2TxtConverter.class, converter.getClass());
	}

	@Test
	@Ignore
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
