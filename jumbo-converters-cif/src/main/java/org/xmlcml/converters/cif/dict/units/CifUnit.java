package org.xmlcml.converters.cif.dict.units;

import org.xmlcml.cml.converters.cif.dict.UnitsDictionary;
import static org.xmlcml.converters.cif.dict.units.UnitType.*;

public enum CifUnit {
    min("min",time),
    deg_min("degree_per_min",angular_velocity),
    mm("millimeters",length),
    kW("kilowatt",power),
    A3("angstrom3",volume),
    kV("kiloVolts",electric_potential_difference),
    A("angstrom",length),
    A_MINUS_1("A-1","reciprocal_angstrom",reciprocal_length),
    Da("dalton",mass),
    mm_MINUS_1("mm-1","reciprocal_millimeters",reciprocal_length),
    kPa("kPascal",pressure),
    A2("angstrom2",area),
    degrees("degree",angle),
    dimensionless("dimensionless",UnitDictionaries.si,UnitType.dimensionless),
    K("k",UnitDictionaries.si,temperature),
    fm("femtometers",length),
    sec("s",UnitDictionaries.si,time),
    deg("degree",angle),
    mA("milliamperes",current),
    Mg_MINUS_3("Mg-3","megagrams_per_cubic_metre",mass_density),
    e_A_MINUS_3("e_A-3","electrons_per_cubic_angstrom",amount_concentration),
    Mgm_MINUS_3("Mgm-3","megagrams_per_cubic_metre",mass_density);
    
    
    String cmlUnitId;
    UnitDictionaries cmlDict;
    public UnitType type;
    
    String cifUnitId;
    String cifPrefix=UnitsDictionary.PREFIX;
    String cifNamespace=UnitsDictionary.URI;
    
    /**
     * Returns the cml prefix:id of the unit.
     */
    public String toString(){
        return this.cmlDict.prefix+":"+this.cmlUnitId;
    }
    
    CifUnit(String cmlUnitId, UnitType type){
        this.cifUnitId=this.name();
        this.cmlUnitId=cmlUnitId;
        this.cmlDict=UnitDictionaries.nonSi;
        this.type=type;
    }
    CifUnit(String cmlUnitString, UnitDictionaries dict, UnitType type){
        this.cifUnitId=this.name();
        this.cmlUnitId=cmlUnitString;
        this.cmlDict=dict;
        this.type=type;
    }
    
    CifUnit (String cifID, String cmlUnitId, UnitType type){
    	this.cifUnitId=cifID;
    	this.cmlUnitId=cmlUnitId;
    	this.cmlDict=UnitDictionaries.nonSi;
    	this.type=type;
    }
    CifUnit(String cifID, String cmlUnitString,UnitDictionaries dict, UnitType type){
        this.cifUnitId=cifID;
        this.cmlUnitId=cmlUnitString;
        this.cmlDict=dict;
        this.type=type;
    }
    
}
