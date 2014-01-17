package org.xmlcml.cml.converters.cif;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.Serializer;
import nu.xom.XPathContext;

import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cif.CIF;
import org.xmlcml.cif.CIFDataBlock;
import org.xmlcml.cif.CIFException;
import org.xmlcml.cif.CIFParser;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;

public class CIFConverterTest {
    @Test
    public void convertToXML() throws IOException {
        InputStream stream=CIFConverterTest.class.getResourceAsStream("/cif/test.cif");
        List<String> stringList = IOUtils.readLines(stream);
        CIF2CIFXMLConverter cif2cifxml = new CIF2CIFXMLConverter();
        CIF cif = cif2cifxml.parseLegacy(stringList);
        List<CIFDataBlock> a = cif.getDataBlockList();
        Collections.sort(a);
        int score = 0;
        for (CIFDataBlock block : a) {
            score += block.getChildCount();
        }
        assertEquals(123, score);
        assertEquals(2, a.size());
    }

    @Test
    public void testCIFXOMtoCMLRaw() throws IOException{
        List<String> stringList = IOUtils.readLines(this.getClass().getResourceAsStream("/cif/test.cif"));
        CIF2CIFXMLConverter cif2cifxml = new CIF2CIFXMLConverter();
        CIF cif = cif2cifxml.parseLegacy(stringList);
        CIFXML2CMLConverter cifxml2cml = new CIFXML2CMLConverter();
        CMLElement raw = (CMLElement)cifxml2cml.convertToXML(cif);
        Nodes nodes=raw.query("//cml:array[@dataType=\"xsd:double\"]", CMLConstants.CML_XPATH);
        assertEquals(14, nodes.size());
    }

    @Test
    public void convertRawToComplete() throws IOException {
        List<String> stringList = IOUtils.readLines(this.getClass().getResourceAsStream("/cif/test.cif"));
        CIF2CIFXMLConverter cif2cifxml = new CIF2CIFXMLConverter();
        CIF cif = cif2cifxml.parseLegacy(stringList);
        CIFXML2CMLConverter cifxml2cml = new CIFXML2CMLConverter();
        Element raw = cifxml2cml.convertToXML(cif);

        RawCML2CompleteCMLConverter conv = new RawCML2CompleteCMLConverter();
        CMLElement cml = (CMLElement) conv.convertToXML(raw);

        XPathContext context = new XPathContext();
        context.addNamespace(CMLConstants.CML_PREFIX, CMLConstants.CML_NS);
        String XQuery = "/cml:cml/cml:module[@convention=\"convention:" + OutPutModuleBuilder.CONVENTION_CRYSTALOGRAPHY + "\"]/cml:module[@convention=\"convention:"
                + OutPutModuleBuilder.CONVENTION_CRYSTAL + "\"]/cml:molecule[@convention=\"convention:" + OutPutModuleBuilder.CONVENTION_MOLECULAR + "\"]";
        assertEquals(1, cml.query(XQuery, context).size());
    }

    @Test
    public void testGettingNewDataType() throws IOException{
        List<String> stringList = IOUtils.readLines(this.getClass().getResourceAsStream("/cif/cif/in/actaEtest.cif"));
        CIF2CIFXMLConverter cif2cifxml = new CIF2CIFXMLConverter();
        CIF cif = cif2cifxml.parseLegacy(stringList);
        CIFXML2CMLConverter cifxml2cml = new CIFXML2CMLConverter();
        Element raw = cifxml2cml.convertToXML(cif);
        RawCML2CompleteCMLConverter conv = new RawCML2CompleteCMLConverter();
        CMLElement cml = (CMLElement) conv.convertToXML(raw);
        Nodes nodes=cml.query("//cml:property/cml:scalar[@dataType=\"xsd:double\"]", CMLConstants.CML_XPATH);
        assertEquals(83, nodes.size());
    }

    @Test
    public void testMultiMoietyCifToCml() throws IOException {
        CIF cif;
        InputStream in = getClass().getResourceAsStream("/bt5475sup1.cif");
        try {
            CIF2CIFXMLConverter cif2cifxml = new CIF2CIFXMLConverter();
            cif = (CIF) cif2cifxml.convertToXML(in);
        } finally {
            IOUtils.closeQuietly(in);
        }

        CIFXML2CMLConverter conv1 = new CIFXML2CMLConverter();
        Element raw = conv1.convertToXML(cif);

        RawCML2CompleteCMLConverter conv2 = new RawCML2CompleteCMLConverter();
        Element cml = conv2.convertToXML(raw);

        Nodes n0 = cml.query("/cml:cml/cml:module/cml:module/cml:molecule", CMLConstants.CML_XPATH);
        assertEquals(1, n0.size());

        Element e = (Element) n0.get(0);
        Nodes n1 = e.query("./cml:molecule", CMLConstants.CML_XPATH);
        assertEquals(2, n1.size());

    }

    @Ignore
    @Test
    public void convertRawToComplete2() throws IOException {
        List<String> stringList = IOUtils.readLines(this.getClass().getResourceAsStream("/zs2096.cif"));
        CIF2CIFXMLConverter cif2cifxml = new CIF2CIFXMLConverter();
        CIF cif = cif2cifxml.parseLegacy(stringList);
        CIFXML2CMLConverter cifxml2cml = new CIFXML2CMLConverter();
        Element raw = cifxml2cml.convertToXML(cif);
        RawCML2CompleteCMLConverter conv = new RawCML2CompleteCMLConverter();
        CMLElement cml = (CMLElement) conv.convertToXML(raw);
        Serializer ser= new Serializer(new FileOutputStream(new File("target/zs2096.cml")));
        ser.setIndent(2);
        ser.write(new Document(cml));
    }

    @Test
    public void testHn0102() throws IOException, CIFException {
        String filename = "/hn0121.cif";
        Element cml = convertToCml(filename);
        assertNotNull(cml);
    }

    @Test
    public void testKo5132() throws IOException, CIFException {
        String filename = "/ko5132.cif";
        Element cml = convertToCml(filename);
//        CMLUtil.debug(cml, "cml");
        assertNotNull(cml);
    }

    @Test
    public void testKv2001() throws IOException, CIFException {
        String filename = "/kv2001.cif";
        Element cml = convertToCml(filename);
        assertNotNull(cml);
    }

    @Test
    @Ignore
    public void testMl5212() throws IOException, CIFException {
        String filename = "/ml5212.cif";
        Element cml = convertToCml(filename);
        assertNotNull(cml);
    }

    @Test
    public void testOs0053() throws IOException, CIFException {
        String filename = "/os0053.cif";
        Element cml = convertToCml(filename);
        assertNotNull(cml);
    }

    @Test
    public void testBt5475() throws IOException, CIFException {
        String filename = "/bt5475sup1.cif";
        Element cml = convertToCml(filename);
        assertNotNull(cml);
    }


    private Element convertToCml(String filename) throws CIFException, IOException {
        InputStream in = getClass().getResourceAsStream(filename);
        CIF cif;
        try {
            BufferedReader r = new BufferedReader ( new InputStreamReader ( in ) );
            cif = (CIF) new CIFParser().parse(r).getRootElement();
            
        } finally {
            IOUtils.closeQuietly(in);
        }

//        Serializer ser = new Serializer(System.err);
//        ser.setIndent(2);
//        ser.write(cif.getDocument());

        CIFXML2CMLOptions opts = new CIFXML2CMLOptions();
        opts.setSkipErrors(true);
        CIFXML2CMLConverter cif2raw = new CIFXML2CMLConverter(opts);
        Element raw = cif2raw.convertToXML(cif);

        RawCML2CompleteCMLConverter raw2cml = new RawCML2CompleteCMLConverter();
        return raw2cml.convertToXML(raw);
    }
    
    public void testKvCif() {
    	
    }

    @Test 
    public void testMain() {
    	CIF2CMLConverter converter = new CIF2CMLConverter();
    	String[] args = {"-i", "src/test/resources/kv2001.cif", "-o", "target/kv.cml"};
    	converter.runArgs(args);
    }
    
    @Test
    public void testCoordinate1() {
    	CIF2CMLConverter converter = new CIF2CMLConverter();
    	String[] args = {"-i", "src/test/resources/coordinate-problem/2234515.cif", "-o", "target/2234515.cml"};
    	converter.runArgs(args);
    }
    
    @Test
    public void testCoordinate2() {
    	CIF2CMLConverter converter = new CIF2CMLConverter();
    	String[] args = {"-i", "src/test/resources/coordinate-problem/2234529.cif", "-o", "target/2234529.cml"};
    	converter.runArgs(args);
    }
    
    @Test
    public void testCoordinate3() {
    	CIF2CMLConverter converter = new CIF2CMLConverter();
    	String[] args = {"-i", "src/test/resources/coordinate-problem/2234500.cif", "-o", "target/2234500.cml"};
    	converter.runArgs(args);
    }
    
    
    @Test
    public void testDir() {
    	CIF2CMLConverter converter = new CIF2CMLConverter();
    	String[] args = {"-i", "src/test/resources/bad/", "-o", "target/bad/"};
    	converter.runArgs(args);
    }
    
    @Test
    public void testBad2File() {
    	CIF2CMLConverter converter = new CIF2CMLConverter();
    	String[] args = {"-i", "src/test/resources/bad2/", "-o", "target/bad2/"};
    	converter.runArgs(args);
    }
}
