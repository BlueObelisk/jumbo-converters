package org.xmlcml.cml.converters.compchem.qchem.log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.XML2XMLConverter;
import org.xmlcml.cml.converters.compchem.qchem.QchemCommon;
import org.xmlcml.cml.converters.text.XML2XMLTransformConverter;
import org.xmlcml.cml.converters.util.ConverterUtils;
import org.xmlcml.euclid.Util;

public class QchemLogXML2CompchemConverter extends XML2XMLTransformConverter {

    private static final Logger LOG = Logger.getLogger(QchemLogXML2CompchemConverter.class);

    private static final String DEFAULT_TEMPLATE_RESOURCE =
            "org/xmlcml/cml/converters/compchem/qchem/log/templates/qchem2compchem.xml";

    private static final String BASE_URI = "classpath:/"+DEFAULT_TEMPLATE_RESOURCE;


    public QchemLogXML2CompchemConverter() {
        this(BASE_URI, "qchem2compchem.xml");
    }

    public QchemLogXML2CompchemConverter(File transformFile) throws IOException {
        super(transformFile);
    }

    public QchemLogXML2CompchemConverter(InputStream inputStream) throws IOException {
        super(inputStream);
    }


    public QchemLogXML2CompchemConverter(String baseUri, String templateName) {
        this(ConverterUtils.buildElementIncludingBaseUri(baseUri, templateName, QchemLog2XMLConverter.class));
    }

    public QchemLogXML2CompchemConverter(Element templateElement) {
        super(templateElement);
    }

	public Element convertToXML(Element xml) {
		return super.convertToXML(xml);
	}

    private void runTests(String dirName) {
        File dir = new File(dirName);
        File[] files = dir.listFiles();
        if (files == null) {
            throw new RuntimeException("No files found in "+dir.getAbsolutePath());
        }
        LOG.info("Processing "+files.length+" files");
        for (File file : files) {
            if (file.getAbsolutePath().endsWith(".out")) {
                File out = new File(file.getAbsolutePath()+".cml");
//					if (!out.exists()) {
                System.out.println("converting "+file+" to "+out);
                this.convert(file, out);
//					}
            }
        }
    }


    public static void main(String[] args) throws IOException {
        if (args.length == 1) {
            QchemLogXML2CompchemConverter converter = new QchemLogXML2CompchemConverter();
            converter.runTests(args[0]);
        } else if (args.length == 2) {
            // QchemLogXML2CompchemConverter converter = new QchemLogXML2CompchemConverter(BASE_URI,
            //         "templates/"+args[1]);
            // converter.runTests(args[0]);
            InputStream transformStream = Util.getResourceUsingContextClassLoader(
                    "org/xmlcml/cml/converters/compchem/qchem/log/qchem2compchem.xml", QchemLogXML2CompchemConverter.class);
            XML2XMLConverter converter = new QchemLogXML2CompchemConverter(transformStream);
            File in = new File(args[0]);
            File out = new File(args[1]);
            converter.convert(in, out);

        } else {
            InputStream transformStream = Util.getResourceUsingContextClassLoader(
                    "org/xmlcml/cml/converters/compchem/qchem/log/qchem2compchem.xml", QchemLogXML2CompchemConverter.class);
            XML2XMLConverter converter = new QchemLogXML2CompchemConverter(transformStream);
            File in = new File("src/test/resources/compchem/qchem/log/out/ch4_B3LYP_631Gd_Opt_20130501R1.xml");
            File out = new File("test/ch4_B3LYP_631Gd_Opt_20130501R1.compchem.xml");
            converter.convert(in, out);
        }
    }
    
	@Override
	public String getRegistryInputType() {
		return QchemCommon.QCHEM_LOG_XML;
	}

	@Override
	public String getRegistryOutputType() {
		return QchemCommon.QCHEM_LOG_CML;
	}

	@Override
	public String getRegistryMessage() {
		return "Convert Q-Chem Log_XML to CML-compchem";
	}

}
