package org.xmlcml.cml.converters.cli;

import org.xmlcml.cml.converters.Converter;
import org.xmlcml.cml.converters.registry.ConverterInfo;
import org.xmlcml.cml.converters.registry.ConverterRegistry;

import java.io.File;

/**
 * @author Sam Adams
 */
public class ConverterCli {

    public static void main(String[] args) {

        if (args.length != 6) {
            System.out.println("Usage:");
            System.out.println("   -i gau-arc foo.arc -o cml foo.cml");
            System.out.println();
            System.out.println("Available converters:");
            for (ConverterInfo info : ConverterRegistry.getConverterList()) {
                System.out.println(info.getInType()+"\t"+info.getOutType()+"\t"+info.getName());
            }
        } else {

            String intype = args[1];
            String infilename = args[2];
            String outtype = args[4];
            String outfilename = args[5];

            File infile = new File(infilename);
            File outfile = new File(outfilename);

            Converter converter = ConverterRegistry.findConverter(intype, outtype);
            converter.convert(infile, outfile);

        }
    }

}
