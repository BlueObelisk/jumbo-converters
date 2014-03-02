package org.xmlcml.cml.converters.cif.dict.units;

import java.util.HashMap;
import java.util.Map;

import nu.xom.Element;

public class CIFUnitMapper {

    public Map<String, CifUnit> cifUnitMap;

    public CIFUnitMapper() {
        init();
    }

    public CifUnit getUnitForId(String cifId){
        return this.cifUnitMap.get(cifId);
    }
    
    public String getCompleteIdFor(String cifId) {
        CifUnit unit = this.cifUnitMap.get(cifId);
        if (unit == null) {
            return null;
        } else {
            return unit.toString();
        }
    }

    public String getCMLIdFor(String cifId) {
        CifUnit unit = this.cifUnitMap.get(cifId);
        if (unit != null) {
            return unit.cmlUnitId;
        } else {
            return null;
        }
    }

    public void addNamespacesOnElement(Element element) {
        for (UnitDictionaries dict : UnitDictionaries.values()) {
            element.addNamespaceDeclaration(dict.prefix, dict.URI);
        }
    }

    protected void init() {
        this.cifUnitMap = new HashMap<String, CifUnit>(CifUnit.values().length);
        for (CifUnit unit : CifUnit.values()) {
            this.cifUnitMap.put(unit.cifUnitId, unit);
        }
    }

}
