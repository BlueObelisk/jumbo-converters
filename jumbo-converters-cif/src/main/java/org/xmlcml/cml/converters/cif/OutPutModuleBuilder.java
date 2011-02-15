package org.xmlcml.cml.converters.cif;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLDictionary;
import org.xmlcml.cml.element.CMLEntry;
import org.xmlcml.cml.element.CMLModule;
import org.xmlcml.cml.element.CMLScalar;

public class OutPutModuleBuilder {
    public static final String CONVETION_URI="http://www.xml-cml.org/convention/";
    public static final String CONVETION_PREFIX="convention";
    public static final String CONVENTION_CRYSTALOGRAPHY = "CrystalographyExperiment";
    public static final String CONVENTION_CRYSTAL = "crystalStructure";
    public static final String CONVENTION_MOLECULAR = "molecular";
    public static final String IUCR_DICT_URI = "http://www.iucr.org/cif";
    public static final String IUCR_DICT_PREFIX = "iucr";
    public static final String XHTML_PREFIX = "xhtml";
    public static final String XHTML_URI = "http://www.w3.org/1999/xhtml";
    private String _dict_path="src/main/resources/cif-dictionary.cml";
    
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
     * Creates an OutputModuleBuilder with a non-default path to a dictionary for dataType resolution
     * @param path to CML dictionary
     */
    public OutPutModuleBuilder(String dictPath){
        this._dict_path=dictPath;
        init();
    }
    
    public OutPutModuleBuilder() {
        init();
    }

    private  void loadDict(String path) throws ValidityException, ParsingException, IOException{
        CMLBuilder builder = new CMLBuilder();
        Document doc=builder.build(new File(path));
        CMLDictionary dict=(CMLDictionary)doc.getRootElement();
        this.dict=dict;
        idMap=new HashMap<String, CMLEntry>();
        for(CMLEntry entry:dict.getEntryElements()){
            this.idMap.put(entry.getId(), entry);
        }
    }
    
    private void init() {
        cml = new CMLCml();
        cml.addNamespaceDeclaration("convention", CONVETION_URI);
        topModule = new CMLModule();
        topModule.addAttribute(new Attribute("convention", CONVETION_PREFIX+":"+CONVENTION_CRYSTALOGRAPHY));
        crystalModule = new CMLModule();
        crystalModule.addAttribute(new Attribute("convention", CONVETION_PREFIX+":"+CONVENTION_CRYSTAL));
        moleculeModule = new CMLModule();
        moleculeModule.addAttribute(new Attribute("convention", CONVETION_PREFIX+":"+CONVENTION_MOLECULAR));

        cml.addNamespaceDeclaration(IUCR_DICT_PREFIX, IUCR_DICT_URI);
        cml.addNamespaceDeclaration(XHTML_PREFIX, XHTML_URI);
        cml.appendChild(topModule);
        try {
            this.loadDict(_dict_path);
        } catch (ValidityException e) {
            System.err.println("Dictionary not valid");
            e.printStackTrace();
            this.dict=null;
        } catch (ParsingException e) {
            System.err.println("Cannot parse dictionary");
            e.printStackTrace();
            this.dict=null;
        } catch (IOException e) {
            System.err.println("IOException reading dictionary");
            e.printStackTrace();
            this.dict=null;
        }
    }

    public void finalise() {
        if (!isFinalised) {
            topModule.appendChild(crystalModule);
            crystalModule.appendChild(moleculeModule);
        }
        isFinalised = true;
    }

    protected void fetchDataType(CMLElement element){
        Attribute dictURI = element.getAttribute("dictRef");
        String dictRef=dictURI.getValue();
        int pos=dictRef.indexOf(':');
        if(pos==-1){
            System.err.println("Cannot dereference non-namespaced reference: "+dictRef);
            return;
        }
        String prefix=dictRef.substring(0, pos);
        String id=dictRef.substring(pos+1);
        String URI=element.getNamespaceURI(prefix);
        if(!dict.getNamespace().equals(URI)){
            System.err.println("Dictionary URI: "+dict.getNamespace()+" does not match "+URI+ " from file");
            return;
        }
        CMLEntry dictEntry=idMap.get(id);
        if(dictEntry==null){
            System.err.println("No entry matches "+id);
            return;
        }
        Elements elems =element.getChildCMLElements("scalar");
        for(int x=0;x<elems.size();x++){
            CMLScalar scalar = (CMLScalar)elems.get(x);
            String type=dictEntry.getDataType();
            if("xsd:float".equals(type)||"xsd:double".equals(type)){
                double d=Double.parseDouble(scalar.getValue());
                scalar.setValue(d);
            }
            else if("xsd:string".equals(type)){
                String s=scalar.getValue();
                scalar.setValue(s);
            }
        }
    }
    
    public void addToTop(Element element) {
        if (element == null) {
            throw new IllegalArgumentException("Cannot add null element");
        }
        element.detach();
        this.topModule.appendChild(element);
    }

    public void addToCrystal(Element element) {
        if (element == null) {
            throw new IllegalArgumentException("Cannot add null element");
        }
        element.detach();
        this.crystalModule.appendChild(element);
    }

    public void addToMolecule(Element element) {
        if (element == null) {
            throw new IllegalArgumentException("Cannot add null element");
        }
        element.detach();
        this.moleculeModule.appendChild(element);
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
            this.addToTop(element);
        }
    }

    public void cloneIdsFromElement(CMLCml parent) {
        String id = parent.getId();
        if (id != null) {
            this.topModule.setId(id);
        }
        String title= parent.getTitle();
        if(title!=null){
            this.topModule.setTitle(title);
        }
    }

}
