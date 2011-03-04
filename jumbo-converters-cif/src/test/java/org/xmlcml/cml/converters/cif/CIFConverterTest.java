package org.xmlcml.cml.converters.cif;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.Serializer;
import nu.xom.XPathContext;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.xmlcml.cif.CIF;
import org.xmlcml.cif.CIFDataBlock;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;

public class CIFConverterTest {
    @Test
    public void convertToXML() throws IOException {
        InputStream stream=CIFConverterTest.class.getResourceAsStream("/cif/test.cif");
        List<String> stringList = (List<String>) IOUtils.readLines(stream);
        CIF2CIFXMLConverter cif2cifxml = new CIF2CIFXMLConverter();
        CIF cif = cif2cifxml.parseLegacy(stringList);
        List<CIFDataBlock> a = cif.getDataBlockList();
        Collections.sort(a);
        int score = 0;
        for (CIFDataBlock block : a) {
            score += block.getChildCount();
        }
        Assert.assertEquals(123, score);
        Assert.assertEquals(2, a.size());
    }

    @Test
    public void testCIFXOMtoCMLRaw() throws IOException{
        List<String> stringList = (List<String>) IOUtils.readLines(this.getClass().getResourceAsStream("/cif/test.cif"));
        CIF2CIFXMLConverter cif2cifxml = new CIF2CIFXMLConverter();
        CIF cif = cif2cifxml.parseLegacy(stringList);
        CIFXML2CMLConverter cifxml2cml = new CIFXML2CMLConverter();
        CMLElement raw = (CMLElement)cifxml2cml.convertToXML(cif);
        Nodes nodes=raw.query("//cml:array[@dataType=\"xsd:double\"]", CMLConstants.CML_XPATH);
        Assert.assertEquals(14, nodes.size());
    }
    
    @Test
    public void convertRawToComplete() throws IOException {
        List<String> stringList = (List<String>) IOUtils.readLines(this.getClass().getResourceAsStream("/cif/test.cif"));
        CIF2CIFXMLConverter cif2cifxml = new CIF2CIFXMLConverter();
        CIF cif = cif2cifxml.parseLegacy(stringList);
        CIFXML2CMLConverter cifxml2cml = new CIFXML2CMLConverter();
        Element raw = cifxml2cml.convertToXML(cif);
        RawCML2CompleteCMLConverter conv = new RawCML2CompleteCMLConverter();
        CMLElement cml = (CMLElement) conv.convertToXML(raw);
        Assert.assertEquals(0, conv.cml.getChildCount());
        XPathContext context = new XPathContext();
        context.addNamespace(CMLConstants.CML_PREFIX, CMLConstants.CML_NS);
        String XQuery = "/cml:cml/cml:module[@convention=\"convention:" + OutPutModuleBuilder.CONVENTION_CRYSTALOGRAPHY + "\"]/cml:module[@convention=\"convention:"
                + OutPutModuleBuilder.CONVENTION_CRYSTAL + "\"]/cml:molecule[@convention=\"convention:" + OutPutModuleBuilder.CONVENTION_MOLECULAR + "\"]";
        Assert.assertEquals(1, cml.query(XQuery, context).size());
        Serializer ser= new Serializer(new FileOutputStream(new File("target/testCif.xml")));
        ser.setIndent(2);
        ser.write(new Document(cml));
    }

    @Test
    public void testGettingNewDataType() throws IOException{
        List<String> stringList = (List<String>) IOUtils.readLines(this.getClass().getResourceAsStream("/cif/cif/in/actaEtest.cif"));
        CIF2CIFXMLConverter cif2cifxml = new CIF2CIFXMLConverter();
        CIF cif = cif2cifxml.parseLegacy(stringList);
        CIFXML2CMLConverter cifxml2cml = new CIFXML2CMLConverter();
        Element raw = cifxml2cml.convertToXML(cif);
        RawCML2CompleteCMLConverter conv = new RawCML2CompleteCMLConverter();
        CMLElement cml = (CMLElement) conv.convertToXML(raw);
        Nodes nodes=cml.query("//cml:property/cml:scalar[@dataType=\"xsd:double\"]", CMLConstants.CML_XPATH);
        Assert.assertEquals(83, nodes.size());
    }
    
}
