package org.xmlcml.cml.converters.compchem.nwchem.log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import nu.xom.Element;

import org.xmlcml.cml.converters.text.TemplateConverter;
import org.xmlcml.euclid.Util;

public class NWChemLog2XMLConverter extends TemplateConverter {
	
	public NWChemLog2XMLConverter(Element templateElement) {
		super(templateElement, "nwchem", "log");
	}

	public static void usage() {
		System.err.println("Usage : <infile> <outfile>");
	}
	
	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			usage();
		} else {
			File in = new File(args[0]);
			File out = new File(args[1]);
			String templateXML = "org/xmlcml/cml/converters/compchem/nwchem/log/templateListAll.xml";
			InputStream templateStream = Util.getInputStreamFromResource(templateXML);
			TemplateConverter tc = TemplateConverter.createTemplateConverter(templateStream, "nwchem", "log");
			tc.convert(in, out);
		}
	}
}
