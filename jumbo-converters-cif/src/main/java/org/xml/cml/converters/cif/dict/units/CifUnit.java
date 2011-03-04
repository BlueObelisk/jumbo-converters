package org.xml.cml.converters.cif.dict.units;

import org.xmlcml.cml.converters.cif.dict.UnitsDictionary;

public enum CifUnit {
    min(""),
    deg_min(""),
    mm(""),
    kW(""),
    A3(""),
    kV(""),
    A(""),
    A_MINUS_1("A-1",""),
    Da(""),
    mm_MINUS_1("mm-1",""),
    kPa(""),
    A2(""),
    degrees(""),
    dimensionless(""),
    K(""),
    fm(""),
    sec(""),
    deg(""),
    mA(""),
    Mg_MINUS_3("Mg-3",""),
    e_A_MINUS_3("e_A-3",""),
    Mgm_MINUS_3("Mgm-3","");
    
    
    String cmlUnitId;
    String cmlUnitPrefixString="allUnits";
    String cmlUnitNamespace="http://www.xml-cml.org/units/allUnits/";
    
    String cifUnitId;
    String cifPrefix=UnitsDictionary.PREFIX;
    String cifNamespace=UnitsDictionary.URI;
    
    CifUnit(String cmlUnitId){
        this.cifUnitId=this.name();
        this.cmlUnitId=cmlUnitId;
    }
    CifUnit (String cifID, String cmlUnitId){
    	this.cifUnitId=cifID;
    	this.cmlUnitId=cmlUnitId;
    }
    
    
}
