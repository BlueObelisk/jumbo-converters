package org.xmlcml.cml.converters.compchem.gaussian.log;

import java.io.File;
import java.io.IOException;

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.compchem.gaussian.GaussianCommon;

public class GaussianLog2CompchemConverter extends AbstractConverter {

    private static final Logger LOG = Logger.getLogger(GaussianLog2CompchemConverter.class);

    private static final String DEFAULT_TEMPLATE_RESOURCE =
            "org/xmlcml/cml/converters/compchem/gaussian/log/templates/gaussian2compchem.xml";

    private static final String BASE_URI = "classpath:/"+DEFAULT_TEMPLATE_RESOURCE;

	private GaussianLog2XMLConverter logConverter;
	private GaussianLogXML2CompchemConverter xmlConverter;


    public GaussianLog2CompchemConverter() {
    	logConverter = new GaussianLog2XMLConverter();
    	xmlConverter = new GaussianLogXML2CompchemConverter();
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
            if (! logFile.exists()) {
                throw new RuntimeException("Cannot find file: " + logFile.getAbsolutePath() +"!\n");
            }
            String cmlFilename = getCmlFilenameFromLogFilename(args[0]);
            File cmlFile = new File(cmlFilename);

            System.out.println("Converting: " + logFile.getAbsolutePath()
                    + "\n to \n" + cmlFile.getAbsolutePath());
            
            try {
                    GaussianLog2CompchemConverter converter = new GaussianLog2CompchemConverter();
                    converter.convert(logFile, cmlFile);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Cannot read/convert "
                        + logFile.getAbsolutePath() + "; " + e);
            }
        } else if (args.length == 2) {
        } else {
            AbstractConverter converter = new GaussianLog2CompchemConverter();
            // convertFile(converter, "jobTest");
            // convertFile(converter, "anna0");
            for (int i = 1; i <= 4; i++) {
                convertFile(converter, "anna" + i);
            }
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
			in = new File("src/test/resources/compchem/gaussian/log/in/"+fileRoot+".log");
			System.out.println("converting: "+in);
			out = new File("test/"+fileRoot+".compchem.xml");
			if (in.exists()) {
				converter.convert(in, out);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Cannot read/convert "+in+"; "+e);
		}
	}

	@Override
	public String getRegistryInputType() {
		return GaussianCommon.LOG;
	}

	@Override
	public String getRegistryOutputType() {
		return GaussianCommon.LOG_CML;
	}

	@Override
	public String getRegistryMessage() {
		return "Convert Gaussian Log to CML-compchem";
	}

}
