package org.xmlcml.cml.converters.molecule.mdl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;

public class MDLExample {

/**
 * The task is to convert a series of MDL molfiles (*.mol, a common legacy format) to
 * CML (an XML format for chemistry). There is an existing library (JUMBOConverters)
 * which will convert many formats to and from CML. You will use the class
 * 
 * org.xmlcml.cml.converters.molecule.mdl.MDL2CMLConverter
 * 
 * to convert a series of *.mol files to CML files 
 * in the "examples/mdl/examples1" and "examples/mdl/examples2" directories
 * 
 * You are encouraged to use the Eclipse IDE to manage the process, and this will be provided
 * with a working version of the converter. 
 * 
 * Task 1:
 * =======
 * in the directory "examples/mdl/examples1" read the file crystal.mol and convert to crystal.xml
 * in the same directory. 
 * 
 * (Hint: To serialize an XML element to a file use:
			nu.xom.Serializer serializer = new nu.xom.Serializer(new FileOutputStream(fileout));
			serializer.write(new nu.xom.Document(element));
 * )
 * 
 * Task 2:
 * =======
 * Use your converter to iterate over all the *.mol files in "examples/mdl/examples2"
 * and create a list of XML Elements. print the number of elements in the list.
 */
		public MDLExample() {
		}
		
		public static void main(String[] args) throws Exception {
			
			convertSingleFile();
			readMultipleFiles();
		}

		private static void convertSingleFile() throws IOException {
			File filein;
			File fileout;
			MDL2CMLConverter converter;
			Element element;
			
			String examples1DirectoryName = "examples/mdl/examples1";
			String molFilenameIn = "crystal.mol";
			String molFilenameOut = "crystal.xml";
			
// for the interview exercise delete below this line...			
// example code (without exception management)			
			filein = new File(examples1DirectoryName+File.separator+molFilenameIn);
			fileout = new File(examples1DirectoryName+File.separator+molFilenameOut);
			
			converter = new MDL2CMLConverter();
			element = converter.convertToXML(filein);
			System.out.println("writing: "+fileout.getAbsolutePath());
			nu.xom.Serializer serializer = new nu.xom.Serializer(new FileOutputStream(fileout));
			serializer.write(new nu.xom.Document(element));
			
		}

		private static void readMultipleFiles() throws IOException {
			MDL2CMLConverter converter;
			Element element;
			
			String examples2DirectoryName = "examples/mdl/examples2";
			
// for the interview exercise delete below this line...			
// example code (without exception management)			
			
			converter = new MDL2CMLConverter();
			File dir = new File(examples2DirectoryName);
			File[] files = dir.listFiles();
			List<Element> elementList = new ArrayList<Element>();
			if (files != null) {
				for (File file : files) {
					if (!file.getPath().endsWith(".mol")) {
						System.out.println("...skipped "+file.getPath());
						continue;
					}
					converter = new MDL2CMLConverter();
					try {
						element = converter.convertToXML(file);
						elementList.add(element);
						System.out.println("Converted: "+file.getPath());
					} catch (Exception e) {
						System.err.println("conversion failed for "+file.getAbsolutePath()+
								"; reason "+ e.getMessage());
					}
				}
			}
			System.out.println("Number of files converted: "+elementList.size());
		}
}
