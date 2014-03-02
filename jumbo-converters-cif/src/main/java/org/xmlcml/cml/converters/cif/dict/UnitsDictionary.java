package org.xmlcml.cml.converters.cif.dict;

import java.util.Map;

import nu.xom.Attribute;
import nu.xom.Element;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLDictionary;
import org.xmlcml.cml.element.CMLEntry;

public class UnitsDictionary extends CMLDictionary{

    public static final String URI = "http://www.xml-cml.org/dict/cifUnit";
    public static final String PREFIX = "cifUnit";
    private static final String conv_URI="http://www.xml-cml.org/convention/";
    private static final String conv_PREFIX="convention";

    
    public UnitsDictionary() {
        super();
        init();
    }
    private void init(){
        setNamespace(URI);
        setDictionaryPrefix(PREFIX);
        addNamespaceDeclaration("xhtml", CIFFields.HTMLNS);
        addNamespaceDeclaration(conv_PREFIX, conv_URI);
        this.addAttribute(new Attribute("convention","convention:dictionary"));
    }
    public void addUnit(String id, String description){
        CMLEntry entry = new CMLEntry();
        String nid=CIFFields.mungeIDString(id);
        if(nid.equals("")){
            return;
        }
        entry.setId(nid);
        entry.setTerm(id);
        CMLElement desc = new CMLElement("description");
        Element text = new Element("xhtml:p",CIFFields.HTMLNS);
        text.appendChild(description);
        desc.appendChild(text);
        entry.appendChild(desc);
        this.addEntry(entry);
    };
    public void addUnits(Map<String, String> unitMap){
        for(String key:unitMap.keySet()){
            addUnit(key, unitMap.get(key));
        }
    }
    
}
