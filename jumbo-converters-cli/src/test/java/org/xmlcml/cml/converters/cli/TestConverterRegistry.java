package org.xmlcml.cml.converters.cli;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.Ignore;
import org.xmlcml.cml.converters.Converter;
import org.xmlcml.cml.converters.cli.ConverterRegistry.Type;
import org.xmlcml.cml.converters.registry.ConverterInfo;

public class TestConverterRegistry {

	@Test
	public void dummy() {
	}

    @Test
    @Ignore
    public void testMap() {
    	Map<Type,ConverterInfo> map = ConverterRegistry.getMap();
    	Assert.assertNotNull(map);
    	// size will change as more are added
    	Assert.assertTrue(map.size() > 0);
    }

    @Test
//    @Ignore
    public void testList() {
    	List<ConverterInfo> list = ConverterRegistry.getConverterList();
    	Assert.assertNotNull(list);
    	Assert.assertTrue(list.size() > 0);
    	// size will change as more are added
//    	Assert.assertEquals("list size", 11, list.size());
    	for (ConverterInfo info : list) {
    		System.out.println(info);
    	}
    }

    @Test
    @Ignore
    public void testFindConverter() {
    	Converter converter = ConverterRegistry.findConverter("gaussian-log", "gaussian-log-xml");
    	Assert.assertNotNull("converter dalton", converter);
    }
}
