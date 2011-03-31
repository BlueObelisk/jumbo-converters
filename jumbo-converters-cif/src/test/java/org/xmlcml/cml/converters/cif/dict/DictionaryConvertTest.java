package org.xmlcml.cml.converters.cif.dict;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xmlcml.cif.CIFException;
import org.xmlcml.cif.CIFParser;
import org.xmlcml.cml.element.CMLDictionary;
import org.xmlcml.cml.element.CMLEntry;
import org.xmlcml.converters.cif.dict.units.CifUnit;

public class DictionaryConvertTest {

    Document cifDict;

    @Test
    public void testCifXom() throws CIFException, IOException {
        File dir = new File("src/test/resources/dict");
        dir.mkdirs();
        File in = new File("src/main/resources/cif_core.dic");
        CIFParser parser = new CIFParser();
        Document cifDict = parser.parse(in);
        Serializer ser = new Serializer(new FileOutputStream(new File("src/test/resources/dict/cif.xml")));
        ser.setIndent(2);
        ser.write(cifDict);
    }

    @Test
    public void testCreateDictionaryPeter() throws IOException {
        File in = new File("src/main/resources/cif_core.dic");
        CIFDict2CMLConverter dictConv = new CIFDict2CMLConverter();
        Element xml = dictConv.convertToXML(in);
        Serializer ser = new Serializer(new FileOutputStream(new File("src/test/resources/dict/peter.xml")));
        ser.setIndent(2);
        ser.write(new Document(xml));
    }

    @Before
    public void createCIFResource() throws CIFException, IOException {
        File in = new File("src/main/resources/cif_core.dic");
        CIFParser parser = new CIFParser();
        cifDict = parser.parse(in);
    }

    @Test
    public void testCreateDictionarySam() throws CIFException, IOException {
        CifDictionaryBuilder builder = new CifDictionaryBuilder();
        builder.build(cifDict);
        Serializer ser = new Serializer(new FileOutputStream(new File("src/test/resources/dict/sam.xml")));
        ser.setIndent(2);
        ser.write(builder.getCmlDoc());
    }

    @Test
    public void testCreateUnitDictionary() throws CIFException, IOException {
        CifDictionaryBuilder builder = new CifDictionaryBuilder();
        builder.build(cifDict);
        CMLDictionary dict = builder.unitsDict;
        Assert.assertNotNull(dict);
        // for(CMLEntry entry:dict.getEntryElements()){
        // System.out.println(entry.getId());
        // }
    }

    @Test
    public void testUnitMapping() {
        CifDictionaryBuilder builder = new CifDictionaryBuilder();
        CMLEntry entry = new CMLEntry();
        entry.setUnits("cifUnits:deg");
        builder.mapUnits(entry);
        Assert.assertEquals(CifUnit.deg.toString(), entry.getUnitsAttribute().getValue());
    }
}
