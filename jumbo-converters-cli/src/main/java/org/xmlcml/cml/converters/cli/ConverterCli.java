package org.xmlcml.cml.converters.cli;

import java.io.File;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.Converter;
import org.xmlcml.cml.converters.registry.ConverterInfo;
import org.xmlcml.euclid.Util;

/**
 * @author Sam Adams
 */
public class ConverterCli {

    private static Logger LOG = Logger.getLogger(Converter.class);

    public static void main(String[] args) {

        if (args.length != 6) {
            usage();
        } else {

            String intype = args[1];
            String infilename = args[2];
            String outtype = args[4];
            String outfilename = args[5];

            Converter converter = null;
            try {
                converter = ConverterRegistry.findConverter(intype, outtype);
                if (converter == null) {
                    throw new RuntimeException("Error finding coverter!");
                    // // usage();
                }
            } catch (Exception e) {
                System.err.println("cannot find converter for: "
                        + Util.concatenate(args, " "));
                System.exit(0);
            }
            LOG.info("Converter: " + converter);

            File infile = new File(infilename);
            try {
                LOG.info("input file: " + infile.getAbsolutePath());
                System.out.println("input file: " + infile.getCanonicalPath());
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Cannot find file: ", e);
            }
            File outfile = new File(outfilename);
            try {
                LOG.info("output file: " + outfile.getAbsolutePath());
                System.out
                        .println("output file: " + outfile.getCanonicalPath());
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Cannot open file: ", e);
            }

            converter.convert(infile, outfile);

        }
    }

    private static void usage() {
        System.out.println("Usage:");
        System.out.println("   -i gau-arc foo.arc -o cml foo.cml");
        System.out.println();
        System.out.println("Available converters:");
        for (ConverterInfo info : ConverterRegistry.getConverterList()) {
            System.out.println(info.getInType() + "\t" + info.getOutType()
                    + "\t" + info.getName());
        }
    }

}
