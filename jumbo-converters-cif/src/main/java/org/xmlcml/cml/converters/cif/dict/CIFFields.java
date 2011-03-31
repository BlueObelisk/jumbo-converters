package org.xmlcml.cml.converters.cif.dict;

import nu.xom.Attribute;
import nu.xom.Element;

import org.xmlcml.cif.CIFDataBlock;
import org.xmlcml.cif.CIFItem;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLEntry;

public enum CIFFields {

    name("_name") {
        @Override
        public void custom(CIFItem cifItem, CMLEntry entry) {
            String name = cifItem.getValue().substring(1).toLowerCase();
            if(name.endsWith("_")){
                name=name.substring(0, name.length()-1);
            }
            entry.setTerm(name);
            entry.setId(mungeIDString(name));
            CMLElement description = new CMLElement("description");
            Element html = new Element("xhtml:p", HTMLNS);
            html.appendChild("Corresponds to the _" + name + " term in the IUCr Core CIF dictionary.");
            description.appendChild(html);
            entry.appendChild(description);
        }
    },
    definition("_definition") {
        @Override
        public void custom(CIFItem cifItem, CMLEntry entry) {
            CMLElement definition = new CMLElement("definition");
            Element html = new Element("xhtml:p", HTMLNS);
            html.appendChild(cifItem.getValue());
            definition.appendChild(html);
            entry.appendChild(definition);
        }
    },
    superclass("_category") {
        @Override
        public void custom(CIFItem cifItem, CMLEntry entry) {
            Element superclass = new Element("superclass");
            superclass.appendChild(cifItem.getValue());
            entry.appendChild(superclass);
        }
    },
    type("_type") {
        @Override
        public void custom(CIFItem cifItem, CMLEntry entry) {
            String type = ("numb".equals(cifItem.getValue())) ? CMLUtil.XSD_DOUBLE : CMLUtil.XSD_STRING;
            entry.addAttribute(new Attribute("dataType", type));
        }
    },
    units("_units") {
        @Override
        public void custom(CIFItem cifItem, CMLEntry entry) {
            String unit = cifItem.getValue();
            if(cifItem.getValue()==null){
                System.out.println(cifItem.toXML());
            }
            String newUnit = mungeUnitID(unit);
            
            entry.addAttribute(new Attribute("units", newUnit));
            CIFFields.lastUnit = unit;
        }

    },
    unitDesc("_units_detail") {

        @Override
        public void custom(CIFItem cifItem, CMLEntry entry) {
            String unitDesc = cifItem.getValue();
            // CMLElement elem=new CMLElement("unitsDescription");
            // elem.appendChild(unitDesc);
            // entry.appendChild(elem);
            CIFFields.lastUnitDesc = unitDesc;
        }
    };

    final String cifName;
    public static final String HTMLNS = "http://www.w3.org/1999/xhtml";
    private static String lastUnit;
    private static String lastUnitDesc;

    public static String getLastUnit() {
        return lastUnit;
    }

    public static String getLastUnitDesc() {
        return lastUnitDesc;
    }

    private CIFFields(String cifName) {
        this.cifName = cifName;
    }

    public void parse(CIFDataBlock datablock, CMLEntry entry) {
        CIFItem cifItem = datablock.getChildItem(cifName);
        if (cifItem == null) {
            return;
        }
        this.custom(cifItem, entry);
    }
    
    abstract void custom(CIFItem cifItem, CMLEntry entry);

    public static CMLEntry parseToEntry(CIFDataBlock dataBlock) {
        CMLEntry entry = new CMLEntry();
        for (CIFFields field : CIFFields.values()) {
            field.parse(dataBlock, entry);
        }
        return entry;
    }

    public static String mungeUnitID(String term){
        StringBuilder builder= new StringBuilder(UnitsDictionary.PREFIX);
        builder.append(':');
        builder.append(mungeIDString(term));
        return builder.toString();
    }
    
    public static String mungeIDString(String unit) {
        StringBuilder out = new StringBuilder();
        if(unit==null){
            return out.toString();
        }
        for (int x = 0; x < unit.length(); x++) {
            char c = unit.charAt(x);
            if (isValidCharacter(c)) {
                out.append(c);
            } else {
                switch (c) {
                case '.':
                    if (unit.length() == 1) {
                        out.append("dimensionless");
                    } else {
                        out.append('.');
                    }
                    break;
                case '/':
                    out.append('_');
                    break;
                default:
                    break;
                }
            }
        }
        return out.toString();
    }

    public static boolean isValidCharacter(char c) {
        return "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_-".indexOf(c) != -1 ? true : false;
    }

}
