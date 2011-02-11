package org.xmlcml.cml.converters.cif.dict;

import java.util.Map;

import nu.xom.Element;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLDictionary;
import org.xmlcml.cml.element.CMLEntry;

public class UnitsDictionary extends CMLDictionary{

    public UnitsDictionary() {
        super();
        init();
    }
    private void init(){
        
    }
    public void addUnit(String id, String description){
        CMLEntry entry = new CMLEntry();
        entry.setId(CIFFields.mungeUnitID(id));
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
