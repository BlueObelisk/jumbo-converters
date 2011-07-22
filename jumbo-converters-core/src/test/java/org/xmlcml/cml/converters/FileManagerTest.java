package org.xmlcml.cml.converters;

import java.io.File;

import org.junit.Test;
import org.xmlcml.euclid.Util;

public class FileManagerTest {

	@Test
	public void testCreateOutputFileName() {
//		Util.println("=============FileManagerTest==============");
//		FileManager fm = new FileManager(null);
		FileManager fm = new FileManager();
		fm.setInputDirectory(new File("/m/n"));
		fm.setOutputDirectory(new File("/x/y"));
		fm.setInputExtension("d");
		fm.setOutputExtension("e");
		File file = new File("a/b.c.d");
		String filename = fm.createOutputFileName(file);
//		Util.println("filename"+filename);
	}

	@Test
	public void testSubstituteInputExtensionByOutputExtension() {
//		FileManager fm = new FileManager(null);
//		String filename = createOutputFileName(file);
	}

}
