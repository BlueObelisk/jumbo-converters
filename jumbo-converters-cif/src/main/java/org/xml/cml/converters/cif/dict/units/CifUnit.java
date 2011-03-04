package org.xml.cml.converters.cif.dict.units;

import org.xmlcml.cml.converters.cif.dict.UnitsDictionary;

public enum CifUnit {
    min(""),
    deg_min(""),
    mm(""),
    kW(""),
    A3(""),
    kV(""),
    A("angstrom"),
    A_MINUS_1("A-1",""),
    Da(""),
    mm_MINUS_1("mm-1",""),
    kPa(""),
    A2(""),
    degrees(""),
    dimensionless(""),
    K("k",UnitDictionaries.si),
    fm(""),
    sec("s",UnitDictionaries.si),
    deg(""),
    mA(""),
    Mg_MINUS_3("Mg-3",""),
    e_A_MINUS_3("e_A-3",""),
    Mgm_MINUS_3("Mgm-3","");
    
    
    String cmlUnitId;
    UnitDictionaries cmlDict;
    
    String cifUnitId;
    String cifPrefix=UnitsDictionary.PREFIX;
    String cifNamespace=UnitsDictionary.URI;
    
    CifUnit(String cmlUnitId){
        this.cifUnitId=this.name();
        this.cmlUnitId=cmlUnitId;
        this.cmlDict=UnitDictionaries.allUnits;
    }
    CifUnit(String cmlUnitString, UnitDictionaries dict){
        this.cifUnitId=this.name();
        this.cmlUnitId=cmlUnitString;
        this.cmlDict=dict;
    }
    
    CifUnit (String cifID, String cmlUnitId){
    	this.cifUnitId=cifID;
    	this.cmlUnitId=cmlUnitId;
    	this.cmlDict=UnitDictionaries.allUnits;
    }
    CifUnit(String cifID, String cmlUnitString,UnitDictionaries dict){
        this.cifUnitId=cifID;
        this.cmlUnitId=cmlUnitString;
        this.cmlDict=dict;
    }
    
}
