package org.xml.cml.converters.cif.dict.units;

public enum UnitDictionaries {
    nonSi("nonSi","http://www.xml-cml.org/unit/nonSi/"),
    si("si","http://www.xml-cml.org/unit/si");
    
    public String prefix;
    public String URI;
    
    private UnitDictionaries(String prefix, String URI) {
       this.prefix=prefix;
       this.URI=URI;
    }

}
