package org.xml.cml.converters.cif.dict.units;

import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.converters.cif.dict.UnitsDictionary;

public enum CifUnit {
    deg_min("test"),min("test2");
    
    
    String cmlUnitId;
    String cmlPrefixString="cml";
    String cmlNamespace=CMLConstants.CML_NS;
    
    String cifUnitId;
    String cifPrefix=UnitsDictionary.PREFIX;
    String cifNamespace=UnitsDictionary.URI;
    
    CifUnit(String cmlUnitId){
        this.cifUnitId=this.name();
        this.cmlUnitId=cmlUnitId;
    }
    
}
