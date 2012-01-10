package org.xmlcml.cml.converters.cli;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.Ignore;

public class TestConverterCli {

	@Test
	public void dummy() {

	}

	@Test
//	@Ignore
    public void testList() {
    	String[] args = {"-i", "junk",  "junk",  "-o", "grot", "grot"};
    	try {
    		ConverterCli.main(args);
    		Assert.fail("Should throw exception");
    	} catch (Exception e) {
    	}

    }

    @Test
    @Ignore
    public void testList1() {
    	String[] args = {"-i junk junk -o grot grot"};
    	ConverterCli.main(args);
    	// should list available classes
    }


    @Test
    @Ignore
    public void testFoo() {
    	String[] args = {"-i", "dalton-log",  "dalton.log",  "-o", "dalton-xml", "dalton.xml"};
    	try {
    		ConverterCli.main(args);
    	} catch (Exception e) {
    		Assert.fail("Should not throw "+ e);
    	}
    }


    @Test
    public void testJCamp() {
    	String[] args = {"-i", "jcamp-dx",  "materials/in/spectrum.jdx",  "-o", "cml", "materials/out/spectrum.cml"};
    	try {
    		ConverterCli.main(args);
    	} catch (Exception e) {
    		Assert.fail("Should not throw "+ e);
    	}
    }
}
