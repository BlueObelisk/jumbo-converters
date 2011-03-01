package org.xmlcml.cml.converters.cif;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.cif.dict.CifDictionaryBuilder;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLDictionary;
import org.xmlcml.cml.element.CMLEntry;
import org.xmlcml.cml.element.CMLModule;
import org.xmlcml.cml.element.CMLScalar;

import org.apache.commons.io.IOUtils;

public class OutPutModuleBuilder {
    public static final String CONVETION_URI = "http://www.xml-cml.org/convention/";
    public static final String CONVETION_PREFIX = "convention";
    public static final String CONVENTION_CRYSTALOGRAPHY = "CrystalographyExperiment";
    public static final String CONVENTION_CRYSTAL = "crystalStructure";
    public static final String CONVENTION_MOLECULAR = "molecular";
    public static final String IUCR_DICT_URI = CifDictionaryBuilder.URI;
    public static final String IUCR_DICT_PREFIX = CifDictionaryBuilder.PREFIX;
    public static final String XHTML_PREFIX = "xhtml";
    public static final String XHTML_URI = "http://www.w3.org/1999/xhtml";
    private String _dict_path = "/cif-dictionary.cml";
    CMLDictionary dict;
    Map<String, CMLEntry> idMap;
    CMLModule topModule;
    CMLModule crystalModule;
    CMLModule moleculeModule;
    CMLCml cml;

    public CMLModule getTopModule() {
        return topModule;
    }

    public CMLModule getCrystalModule() {
        return crystalModule;
    }

    public CMLModule getMoleculeModule() {
        return moleculeModule;
    }

    public CMLCml getCml() {
        return cml;
    }

    boolean isFinalised = false;

    /**
     * Creates an OutputModuleBuilder with a non-default path to a dictionary
     * for dataType resolution
     * 
     * @param dictPath
     *            to CML dictionary
     */
    public OutPutModuleBuilder(String dictPath) {
        this._dict_path = dictPath;
        init();
    }

    public OutPutModuleBuilder() {
        init();
    }

    private void loadDict(String path) throws ParsingException, IOException {
        CMLBuilder builder = new CMLBuilder();
        InputStream is = findInput(path);
        Document doc ;
        try {
            doc = builder.build(is);
        } finally {
            IOUtils.closeQuietly(is);
        }
        CMLDictionary dict = (CMLDictionary) doc.getRootElement();
        this.dict = dict;
        idMap = new HashMap<String, CMLEntry>();
        for (CMLEntry entry : dict.getEntryElements()) {
            this.idMap.put(entry.getId(), entry);
        }
    }

    private InputStream findInput(String path) throws FileNotFoundException {
        File file = new File(path);
        if (file.isFile()) {
            return new BufferedInputStream(new FileInputStream(file));
        }
        InputStream in = getClass().getResourceAsStream(path);
        if (in != null) {
            return in;
        }
        throw new FileNotFoundException("File not found: "+path);
    }

    private void init() {
        cml = new CMLCml();
        cml.addNamespaceDeclaration("convention", CONVETION_URI);
        topModule = new CMLModule();
        topModule.addAttribute(new Attribute("convention", CONVETION_PREFIX + ":" + CONVENTION_CRYSTALOGRAPHY));
        crystalModule = new CMLModule();
        crystalModule.addAttribute(new Attribute("convention", CONVETION_PREFIX + ":" + CONVENTION_CRYSTAL));
        moleculeModule = new CMLModule();
        moleculeModule.addAttribute(new Attribute("convention", CONVETION_PREFIX + ":" + CONVENTION_MOLECULAR));

        cml.addNamespaceDeclaration(IUCR_DICT_PREFIX, IUCR_DICT_URI);
        cml.addNamespaceDeclaration(XHTML_PREFIX, XHTML_URI);
        cml.appendChild(topModule);
        try {
            this.loadDict(_dict_path);
        } catch (ValidityException e) {
            System.err.println("Dictionary not valid");
            e.printStackTrace();
            this.dict = null;
        } catch (ParsingException e) {
            System.err.println("Cannot parse dictionary");
            e.printStackTrace();
            this.dict = null;
        } catch (IOException e) {
            System.err.println("IOException reading dictionary");
            e.printStackTrace();
            this.dict = null;
        }
    }

    public void finalise() {
        if (!isFinalised) {
            topModule.appendChild(crystalModule);
            crystalModule.appendChild(moleculeModule);
            Nodes nodes = this.cml.query("//cml:*[@dictRef]", CMLConstants.CML_XPATH);
            for (int x = 0; x < nodes.size(); x++) {
                CMLElement elem = (CMLElement) nodes.get(x);
                fetchDataType(elem);
            }
        }
        isFinalised = true;
    }

    public  String getDataType(String id) {
        CMLEntry dictEntry = idMap.get(id);
        if (dictEntry == null) {
             return null;
         }
        String type = dictEntry.getDataType();
        return type;
    }
    
    protected void fetchDataType(CMLElement element) {
        Attribute dictURI = element.getAttribute("dictRef");
        // If no dictRef, don't do anything
        if (dictURI == null) {
            return;
        }
        String dictRef = dictURI.getValue();
        int pos = dictRef.indexOf(':');
        if (pos == -1) {
            System.err.println("Cannot dereference non-namespaced reference: " + dictRef);
            return;
        }
        String prefix = dictRef.substring(0, pos);
        String id = dictRef.substring(pos + 1);
        
        if (!CifDictionaryBuilder.PREFIX.equals(prefix)) {
            System.err.println("Dictionary Prefix: " + CifDictionaryBuilder.PREFIX + " does not match " + prefix + " from file");
            return;
        }
        CMLEntry dictEntry = idMap.get(id);
        
        //Ignore dictrefs which aren't from the CIF dictionary
        if (dictEntry == null) {
           // System.err.println("No entry matches " + id);
            return;
        }
        Elements elems = element.getChildCMLElements("scalar");
        for (int x = 0; x < elems.size(); x++) {
            CMLScalar scalar = (CMLScalar) elems.get(x);
            String type = dictEntry.getDataType();
            if ("xsd:float".equals(type) || "xsd:double".equals(type)) {
                double d = getDoubleFromString(scalar.getValue());
                Double e = getErrorFromString(scalar.getValue());
                scalar.setValue(d);
                if (e != null) {
                    scalar.setErrorValue(e);
                }
            } else if ("xsd:string".equals(type)) {
                String s = scalar.getValue();
                scalar.setValue(s);
            }
        }
    }

    protected Double getErrorFromString(String value) {
        int x = value.indexOf('(');
        int y = value.indexOf('.');
        if(x==-1){
            return null;
        }
        int error = Integer.parseInt(value.substring(x+1,value.indexOf(')')));
        
        int exponent;
        if(y==-1){
            exponent=0;
        }
        else{
            exponent=y-(x-1);
        }
        double d= error*Math.pow(10, exponent);
        return d;
    }

    protected double getDoubleFromString(String value) {
        int x = value.indexOf('(');
        String number = value.substring(0, (x == -1) ? value.length() : x);
        double d = Double.parseDouble(number);
        return d;
    }

    public void addToTop(Element element) {
        if (element == null) {
            throw new IllegalArgumentException("Cannot add null element");
        }
        element.detach();
        if (element instanceof CMLElement) {
            fetchDataType((CMLElement) element);
        }
        this.topModule.appendChild(element);
    }

    public void addToCrystal(Element element) {
        if (element == null) {
            throw new IllegalArgumentException("Cannot add null element");
        }
        element.detach();
        if (element instanceof CMLElement) {
            fetchDataType((CMLElement) element);
        }
        this.crystalModule.appendChild(element);
    }

    public void addToMolecule(Element element) {
        if (element == null) {
            throw new IllegalArgumentException("Cannot add null element");
        }
        element.detach();
        if (element instanceof CMLElement) {
            fetchDataType((CMLElement) element);
        }
        fetchDataTypeOfScalarChildren(element);
        this.moleculeModule.appendChild(element);
    }

    protected void fetchDataTypeOfScalarChildren(Element element) {
        Nodes nodes=element.query(".//cml:*["+CMLScalar.NS+"]", CMLConstants.CML_XPATH);
        for (int x = 0; x < nodes.size(); x++) {
            CMLElement parent = (CMLElement) nodes.get(x);
            fetchDataType(parent);
        }
    }

    /**
     * 
     * @param parent
     * 
     */
    public void addAllChildrenToTop(Element parent) {
        Elements children = parent.getChildElements();
        for (int x = 0; x < children.size(); x++) {
            Element element = children.get(x);
            element.detach();
            if(element instanceof CMLElement){
                fetchDataType((CMLElement)element);
            }
            this.addToTop(element);
        }
    }

    public void cloneIdsFromElement(CMLCml parent) {
        String id = parent.getId();
        if (id != null) {
            this.topModule.setId(id);
        }
        String title = parent.getTitle();
        if (title != null) {
            this.topModule.setTitle(title);
        }
    }

}
