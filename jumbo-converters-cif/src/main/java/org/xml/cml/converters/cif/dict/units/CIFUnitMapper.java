package org.xml.cml.converters.cif.dict.units;

import java.util.HashMap;
import java.util.Map;

public class CIFUnitMapper {

    Map<String, CifUnit> cifUnitMap;

    public CIFUnitMapper() {
        init();
    }

    public String getCMLIdFor(String cifId) {
        CifUnit unit = this.cifUnitMap.get(cifId);
        if (unit != null) {
            return unit.cmlUnitId;
        } else {
            return null;
        }
    }

    protected void init() {
        this.cifUnitMap = new HashMap<String, CifUnit>(CifUnit.values().length);
        for (CifUnit unit : CifUnit.values()) {
            this.cifUnitMap.put(unit.cifUnitId, unit);
        }
    }

}
