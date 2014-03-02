package org.xmlcml.cml.converters.compchem.nwchem.log;

import java.io.File;
import java.io.IOException;

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.compchem.nwchem.NWChemCommon;

public class NWChemLog2CompchemConverter extends AbstractConverter {

    private static final Logger LOG = Logger.getLogger(NWChemLog2CompchemConverter.class);

    private static final String DEFAULT_TEMPLATE_RESOURCE =
            "org/xmlcml/cml/converters/compchem/NWChem/log/templates/nwchem2compchem.xml";

    private static final String BASE_URI = "classpath:/"+DEFAULT_TEMPLATE_RESOURCE;

	private NWChemLog2XMLConverter logConverter;
	private NWChemLogXML2CompchemConverter xmlConverter;


    public NWChemLog2CompchemConverter() {
    	logConverter = new NWChemLog2XMLConverter();
    	xmlConverter = new NWChemLogXML2CompchemConverter();
    }       

    public Type getInputType() {
    	return logConverter.getInputType();
    }
    
    public Type getOutputType() {
    	return xmlConverter.getOutputType();
    }
    
    public void convert(File in, File out) {
    	Element xmlElement = logConverter.convertToXML(in);
    	xmlConverter.convert(xmlElement, out);
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 1) {
            File logFile = new File(args[0]);
            if (!logFile.exists()) {
                throw new RuntimeException("Cannot find file: "
                        + logFile.getAbsolutePath() + "!\n");
            }
            String cmlFilename = getCmlFilenameFromLogFilename(args[0]);
            File cmlFile = new File(cmlFilename);

            System.out.println("Converting: " + logFile.getAbsolutePath()
                    + "\n to \n" + cmlFile.getAbsolutePath());

            try {
                NWChemLog2CompchemConverter converter = new NWChemLog2CompchemConverter();
                converter.convert(logFile, cmlFile);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Cannot read/convert "
                        + logFile.getAbsolutePath() + "; " + e);
            }

        }
        else if (args.length == 2) {
        } else {
            AbstractConverter converter = new NWChemLog2CompchemConverter();
//            convertFile(converter, "fukuilite");
            convertFile(converter, "methanol");
//			for (int i = 1; i < 4; i++) {
////		convertFile(converter, "anna"+i);
//			}
        }
    }

    private static String getCmlFilenameFromLogFilename(String logfileName) {
        String cmlExt = "cml";
        int mid = logfileName.lastIndexOf(".");
        String cmlFileName;
        if (mid > 0) {
            cmlFileName = logfileName.substring(0, mid) + "."+ cmlExt;
        } else {
            cmlFileName = logfileName + "." + cmlExt;
        }
        return cmlFileName;
    }
    
	private static void convertFile(AbstractConverter converter, String fileRoot) {
		File out;
		File in = null;
		try {
			in = new File("src/test/resources/compchem/nwchem/log/in/"+fileRoot+".out");
			System.out.println("converting: "+in);
			out = new File("test/"+fileRoot+".compchem.cml");
			converter.convert(in, out);
		} catch (Exception e) {
			System.err.println("Cannot read/convert "+in+"; "+e);
		}
	}
	
	@Override
	public String getRegistryInputType() {
		return NWChemCommon.LOG;
	}
	
	@Override
	public String getRegistryOutputType() {
		return NWChemCommon.LOG_CML;
	}
	
	@Override
	public String getRegistryMessage() {
		return "Convert NWChem log files to compchem CML";
	}

}
