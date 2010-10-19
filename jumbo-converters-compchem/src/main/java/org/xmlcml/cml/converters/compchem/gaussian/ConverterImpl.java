package org.xmlcml.cml.converters.compchem.gaussian;

import java.io.File;
import java.io.InputStream;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Elements;

import org.xmlcml.cml.converters.Converter;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.euclid.Util;

import com.hp.hpl.jena.util.FileUtils;

/** don't know why this is here.
 * I think it's used in a test
 * @author pm286
 *
 */
public class ConverterImpl {
	
	private static final String CONVERTERS_FILE = "org/xmlcml/cml/converters/compchem/gaussian/converterList.xml";

	public static void main(String[] args) {
		if (args.length == 0) {
			usage();
		} else {
			process(args);
		}
	}

	private static void usage() {
		System.err.println("usage: Converter\n" +
				"    -in <infile> mandatory\n" +
				"    -out <outfile> mandatory\n" +
				"    -intype <intype> optional, else guessed from suffix\n" +
				"    -outtype <outtype> optional, else guessed from suffix\n" +
				"    -converter <converter> optional, else guessed from types\n" +
				"");
	}

	private static void process(String[] args) {
		String intype = null;
		String outtype = null;
		String infilename = null;
		String outfilename = null;
		String converterName = null;
		int i = 0;
		while (i < args.length) {
			if (args[i].equalsIgnoreCase("-in")) {
				infilename = args[++i]; i++;
			} else if (args[i].equalsIgnoreCase("-out")) {
				outfilename = args[++i]; i++;
			} else if (args[i].equalsIgnoreCase("-intype")) {
				intype = args[++i]; i++;
			} else if (args[i].equalsIgnoreCase("-outtype")) {
				outtype = args[++i]; i++;
			} else if (args[i].equalsIgnoreCase("-converter")) {
				converterName = args[++i]; i++;
			} else {
				System.err.println("Unknown arg "+args[i]);
				i++;
			}
		}
		if (infilename == null) {
			throw new RuntimeException("No input file given");
		}
		File infile = new File(infilename);
		if (!infile.exists()) {
			throw new RuntimeException("Input file does not exist: "+infile.getAbsolutePath());
		}
		System.out.println("Input file: "+infile.getAbsolutePath());
		if (outfilename == null) {
			throw new RuntimeException("No output file given");
		}
		File outfile = new File(outfilename);
		System.out.println("Output file: "+outfile.getAbsolutePath());
		org.apache.commons.io.FileUtils.deleteQuietly(outfile);
		if (intype == null) {
			intype = getType(infilename);
			if (intype == null) {
				throw new RuntimeException("Cannot find input type");
			}
		}
		System.out.println("Input type: "+intype);
		if (outtype == null) {
			outtype = getType(outfilename);
			if (outtype == null) {
				throw new RuntimeException("Cannot find output type");
			}
		}
		System.out.println("Output type: "+outtype);
		if (converterName == null) {
			converterName = getConverterName(intype, outtype);
		}
		if (converterName == null) {
			throw new RuntimeException("Cannot find converter name");
		}
		System.out.println("Converter name: "+converterName);
		
		try {
			Converter converter = (Converter) Class.forName(converterName).newInstance();
			System.out.println("Converting "+infile.getAbsolutePath()+" => \n"+outfile.getAbsolutePath());
			
			converter.convert(infile, outfile);
		} catch (Exception e) {
			throw new RuntimeException("Cannot create/run converter", e);
		}
	}

	private static String getConverterName(String intype, String outtype) {
		String converterName = null;
		try {
			InputStream is = Util.getInputStreamFromResource(CONVERTERS_FILE);
			Document doc = new Builder().build(is);
			if (doc != null) {
				Elements childElements = doc.getRootElement().getChildElements();
				for (int i = 0; i < childElements.size(); i++) {
					if (intype.equals(childElements.get(i).getAttributeValue("in")) &&
						outtype.equals(childElements.get(i).getAttributeValue("out"))) {
						converterName = childElements.get(i).getValue();
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Cannot find converter for "+intype+" "+outtype, e);
		}
		if (converterName == null) {
			throw new RuntimeException("Cannot find converter for "+intype+" "+outtype);
		}
		return converterName;
	}

	private static String getType(String filename) {
		return Type.getTypeForFilename(filename);
	}
}
