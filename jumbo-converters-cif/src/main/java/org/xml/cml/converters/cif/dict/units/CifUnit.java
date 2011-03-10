package org.xml.cml.converters.cif.dict.units;

import org.xmlcml.cml.converters.cif.dict.UnitsDictionary;

public enum CifUnit {
    min("min"),
    deg_min("degree_per_min"),
    mm("millimeters"),
    kW("kilowatt"),
    A3("angstrom3"),
    kV("kiloVolts"),
    A("angstrom"),
    A_MINUS_1("A-1","reciprocal_angstrom"),
    Da("atomic_mass_unit"),
    mm_MINUS_1("mm-1","reciprocal_millimeters"),
    kPa("kPascal"),
    A2("angstrom2"),
    degrees("degree"),
    dimensionless("dimensionless"),
    K("k",UnitDictionaries.si),
    fm("femtometers"),
    sec("s",UnitDictionaries.si),
    deg("degree"),
    mA("milliamperes"),
    Mg_MINUS_3("Mg-3","megagrams_per_cubic_metre"),
    e_A_MINUS_3("e_A-3","electrons_per_cubic_angstrom"),
    Mgm_MINUS_3("Mgm-3","megagrams_per_cubic_metre");
    
    
    String cmlUnitId;
    UnitDictionaries cmlDict;
    
    String cifUnitId;
    String cifPrefix=UnitsDictionary.PREFIX;
    String cifNamespace=UnitsDictionary.URI;
    
    /**
     * Returns the cml prefix:id of the unit.
     */
    public String toString(){
        return this.cmlDict.prefix+":"+this.cmlUnitId;
    }
    
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
