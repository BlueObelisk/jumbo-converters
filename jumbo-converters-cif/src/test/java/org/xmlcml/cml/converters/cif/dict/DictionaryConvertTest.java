package org.xmlcml.cml.converters.cif.dict;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import nu.xom.Document;
import nu.xom.Serializer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xmlcml.cif.CIFException;
import org.xmlcml.cif.CIFParser;
import org.xmlcml.cml.converters.cif.dict.units.CifUnit;
import org.xmlcml.cml.element.CMLDictionary;
import org.xmlcml.cml.element.CMLEntry;

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
