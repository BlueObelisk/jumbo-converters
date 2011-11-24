package org.xmlcml.cml.converters.cif.dict;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

import org.xmlcml.cif.CIF;
import org.xmlcml.cif.CIFDataBlock;
import org.xmlcml.cif.CIFException;
import org.xmlcml.cif.CIFLoop;
import org.xmlcml.cif.CIFParser;
import org.xmlcml.cml.base.CMLAttribute;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.cif.dict.units.CIFUnitMapper;
import org.xmlcml.cml.converters.cif.dict.units.CifUnit;
import org.xmlcml.cml.element.CMLDictionary;
import org.xmlcml.cml.element.CMLEntry;

/**
 * @author sea36
 * @author nwe23
 */
public class CifDictionaryBuilder {

    public static final String URI = "http://www.xml-cml.org/dictionary/cif/";
    public static final String PREFIX = "iucr";
    private static final String conv_URI = "http://www.xml-cml.org/convention/";
    private static final String conv_PREFIX = "convention";

    protected CMLDictionary dictionary;
    protected CMLDictionary unitsDict;
    private Map<String, String> unitMap = new HashMap<String, String>();
    protected CIFUnitMapper mapper = new CIFUnitMapper();

    public CifDictionaryBuilder() {
        dictionary = new CMLDictionary();
        dictionary.setNamespace(URI);
        dictionary.setDictionaryPrefix(PREFIX);
        dictionary.addNamespaceDeclaration("xhtml", CIFFields.HTMLNS);
        dictionary.addNamespaceDeclaration(conv_PREFIX, conv_URI);
        dictionary.addAttribute(new Attribute("convention", "convention:dictionary"));
        dictionary.addNamespaceDeclaration(UnitsDictionary.PREFIX, UnitsDictionary.URI);
        dictionary.addAttribute(new Attribute("title", "Auto-generated from the CIF CORE DEFINITIONS version 2.4.1"));
        CMLElement desc = new CMLElement("description");
        Element xhtml = new Element("xhtml:p", CIFFields.HTMLNS);
        xhtml
                .appendChild("A CML dictionary of the IUCR standard CIF CORE DEFINITIONS available from the IUCR website (http://www.iucr.org/resources/cif/dictionaries/cif_core).");
        desc.appendChild(xhtml);
        dictionary.appendChild(desc);
        mapper.addNamespacesOnElement(dictionary);
    }

    public void build(Document cifDict) {
        CIF cif = (CIF) cifDict.getRootElement();
        for (CIFDataBlock dataBlock : cif.getDataBlockList()) {
            if ("on_this_dictionary".equals(dataBlock.getId())) {
                continue;
            }
            if (dataBlock.getId().endsWith("[]")) {
                continue;
            }

            if (dataBlock.getId().endsWith("_")) {

            }

            // Parse datablock using enum methods.
            CMLEntry entry = CIFFields.parseToEntry(dataBlock);

            List<CMLEntry> extraEntires = parseLoopsfromDataBlock(dataBlock, entry);
            for (CMLEntry extraEntry : extraEntires) {
                mapUnits(extraEntry);
                dictionary.appendChild(extraEntry);
            }
            if (entry.getId() != null) {
                mapUnits(entry);
                dictionary.appendChild(entry);
            }
            this.unitMap.put(CIFFields.getLastUnit(), CIFFields.getLastUnitDesc());
        }
        // writeUnitsUsed(System.out);
        unitsDict = createUnitsDictionary(unitMap);
    }

    protected void mapUnits(CMLEntry entry) {
        CMLAttribute att = entry.getUnitsAttribute();
        if (att == null) {
            return;
        }
        String id = att.getValue();
        id = id.substring(id.indexOf(':') != -1 ? (id.indexOf(':') + 1) : 0, id.length());
        CifUnit unit = mapper.getUnitForId(id);
        String value = unit.toString();
        att.setValue(value);
        unit.type.setUnitType(entry);
    }

    private CMLDictionary createUnitsDictionary(Map<String, String> unitMap) {
        UnitsDictionary dict = new UnitsDictionary();
        dict.addUnits(unitMap);
        return dict;

    }

    private void writeUnitsUsed(PrintStream out) {
        for (String unit : unitMap.keySet()) {
            out.println(unit + " " + unitMap.get(unit));
        }

    }

    private List<CMLEntry> parseLoopsfromDataBlock(CIFDataBlock dataBlock, CMLEntry entry) {
        List<CIFLoop> loops = dataBlock.getLoopList();
        List<String> extra_names = new ArrayList<String>();
        List<CMLEntry> entries = new ArrayList<CMLEntry>();
        for (CIFLoop loop : loops) {
            List<String> nameList = loop.getNameList();
            if (nameList.contains("_enumeration")) {
                List<String> enumerationValues = loop.getColumnValues("_enumeration");
                for (String enumerationValue : enumerationValues) {
                    Element enumeration = new Element("enumeration", CMLConstants.CMLX_NS);
                    enumeration.appendChild(enumerationValue);
                    entry.appendChild(enumeration);
                }
            }
            if (nameList.contains("_name")) {
                List<String> nameValues = loop.getColumnValues("_name");
                for (String name : nameValues) {
                    extra_names.add(name);
                }
            }
        }
        for (String name : extra_names) {
            CMLEntry second = new CMLEntry(entry);
            String newName = name.substring(1).toLowerCase();
            second.setTerm(newName);
            second.setId(CIFFields.mungeIDString(newName));
            CMLElement description = new CMLElement("description");
            Element html = new Element("xhtml:p", CMLConstants.XHTML_NS);
            html.appendChild("Corresponds to the _" + newName + " term in the IUCr Core CIF dictionary.");
            description.appendChild(html);
            second.appendChild(description);
            entries.add(second);
        }
        return entries;
    }

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

        os = new BufferedOutputStream(new FileOutputStream(new File(out.getAbsolutePath() + ".units")));
        ser.setOutputStream(os);
        ser.write(new Document(builder.unitsDict));
    }

    public static void main(String[] args) throws Exception {
        /**
         * Running this rebuilds the cif-dictionary form the cif_core.dic
         */
        CifDictionaryBuilder.build(new File("src/main/resources/cif_core.dic"), new File("src/main/resources/cif-dictionary.cml"));

    }

}
