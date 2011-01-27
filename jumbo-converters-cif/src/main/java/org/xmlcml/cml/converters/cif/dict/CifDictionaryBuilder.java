package org.xmlcml.cml.converters.cif.dict;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import nu.xom.Document;
import nu.xom.Serializer;

import org.xmlcml.cif.CIF;
import org.xmlcml.cif.CIFDataBlock;
import org.xmlcml.cif.CIFException;
import org.xmlcml.cif.CIFParser;
import org.xmlcml.cml.element.CMLDictionary;

/**
 * @author sea36
 */
public class CifDictionaryBuilder {

    private static final String URI = "http://www.xml-cml.org/dict/cif/";
    private static final String PREFIX = "cif";

    private CMLDictionary dictionary;

    public CifDictionaryBuilder() {
        dictionary = new CMLDictionary();
        dictionary.setNamespace(URI);
        dictionary.setDictionaryPrefix(PREFIX);
    }


    public void build(Document cifDict) {
        CIF cif = (CIF) cifDict.getRootElement();
        for (CIFDataBlock dataBlock : cif.getDataBlockList()) {
            if ("on_this_dictionary".equals(dataBlock.getId())) {
                continue;
            }
            if (dataBlock.getId().endsWith("[]") || dataBlock.getId().endsWith("_")) {
                continue;
            }
            
            dictionary.appendChild(CIFFields.parseToEntry(dataBlock));
            //addEntry(dataBlock);
        }
    }
    
    

//    private void addEntry(CIFDataBlock dataBlock) {
//
//        CIFItem cifName = dataBlock.getChildItem("_name");
//        if (cifName == null) {
//            return;
//        }
//
//        String name = cifName.getValue().substring(1);  // Remove '_' prefix
//
//        CMLEntry entry = new CMLEntry();
//        entry.setTerm(name);
//        entry.setId(name);
//
//        Element description = new Element("description", CMLConstants.CML_NS);
//        description.appendChild("Corresponds to the _"+name+" term in the IUCr Core CIF dictionary.");
//        entry.appendChild(description);
//
//        CIFItem cifDefinition = dataBlock.getChildItem("_definition");
//        if (cifDefinition != null) {
//            Element definition = new Element("definition", CMLConstants.CML_NS);
//            definition.appendChild(cifDefinition.getValue());
//
//            entry.appendChild(definition);
//        }
//
//
//
//        dictionary.appendChild(entry);
//    }


    public Document getCmlDoc() {
        return dictionary.getDocument() == null ? new Document(dictionary) : dictionary.getDocument();
    }


    private static void build(File in, File out) throws IOException, CIFException {
        CIFParser parser = new CIFParser();
        Document cifDict = parser.parse(in);
        CifDictionaryBuilder builder = new CifDictionaryBuilder();
        builder.build(cifDict);

        BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(out));
        Serializer ser = new Serializer(os);
        ser.setIndent(4);
        ser.write(builder.getCmlDoc());
        os.close();
    }



    public static void main(String[] args) throws Exception {

        CifDictionaryBuilder.build(new File("cif_core.dic"), new File("cif-dictionary.cml"));

    }



}
