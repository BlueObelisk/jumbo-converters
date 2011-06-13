package org.xmlcml.cml.converters.compchem.gaussian.log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.XML2XMLConverter;
import org.xmlcml.cml.converters.text.XML2XMLTransformConverter;
import org.xmlcml.cml.converters.util.ConverterUtils;
import org.xmlcml.euclid.Util;

public class GaussianLogXML2CompchemConverter extends XML2XMLTransformConverter {

    private static final Logger LOG = Logger.getLogger(GaussianLogXML2CompchemConverter.class);

    private static final String DEFAULT_TEMPLATE_RESOURCE =
            "org/xmlcml/cml/converters/compchem/gaussian/log/templates/gaussian2compchem.xml";

    private static final String BASE_URI = "classpath:/"+DEFAULT_TEMPLATE_RESOURCE;


    public GaussianLogXML2CompchemConverter() {
        this(BASE_URI, "gaussian2compchem.xml");
    }

    public GaussianLogXML2CompchemConverter(File transformFile) throws IOException {
        super(transformFile);
    }

    public GaussianLogXML2CompchemConverter(InputStream inputStream) throws IOException {
        super(inputStream);
    }


    public GaussianLogXML2CompchemConverter(String baseUri, String templateName) {
        this(ConverterUtils.buildElementIncludingBaseUri(baseUri, templateName, GaussianLog2XMLConverter.class));
    }

    public GaussianLogXML2CompchemConverter(Element templateElement) {
        super(templateElement);
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
            GaussianLogXML2CompchemConverter converter = new GaussianLogXML2CompchemConverter();
            converter.runTests(args[0]);
        } else if (args.length == 2) {
            GaussianLogXML2CompchemConverter converter = new GaussianLogXML2CompchemConverter(BASE_URI,
                    "templates/"+args[1]);
            converter.runTests(args[0]);
        } else {
            InputStream transformStream = Util.getResourceUsingContextClassLoader(
                    "org/xmlcml/cml/converters/compchem/gaussian/log/gaussian2compchem.xml", GaussianLogXML2CompchemConverter.class);
            XML2XMLConverter converter = new GaussianLogXML2CompchemConverter(transformStream);
            File in = new File("src/test/resources/compchem/gaussian/log/ref/anna1.xml");
            File out = new File("test/anna1.compchem.xml");
            converter.convert(in, out);
        }
    }
}
