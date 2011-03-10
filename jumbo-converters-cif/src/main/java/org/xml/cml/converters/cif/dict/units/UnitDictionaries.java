package org.xml.cml.converters.cif.dict.units;

public enum UnitDictionaries {
    allUnits("allUnits","http://www.xml-cml.org/units/allUnits/"),
    si("si","http://www.xml-cml.org/unit/si");
    
    public String prefix;
    public String URI;
    
    private UnitDictionaries(String prefix, String URI) {
       this.prefix=prefix;
       this.URI=URI;
    }

}
