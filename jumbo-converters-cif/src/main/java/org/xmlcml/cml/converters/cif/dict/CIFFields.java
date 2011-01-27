package org.xmlcml.cml.converters.cif.dict;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;

import org.xmlcml.cif.CIFDataBlock;
import org.xmlcml.cif.CIFItem;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLEntry;

public enum CIFFields {
	name("_name") {
		@Override
		public void custom(CIFItem cifItem, CMLEntry entry) {
			String name = cifItem.getValue().substring(1);
	        entry.setTerm(name);
	        entry.setId(name);
	        CMLElement description = new CMLElement("description");
	        description.appendChild("Corresponds to the _"+name+" term in the IUCr Core CIF dictionary.");
	        entry.appendChild(description);
		}
	},
	definition("_definition") {
		@Override
		public void custom(CIFItem cifItem, CMLEntry entry) {
			CMLElement definition = new CMLElement("definition");
            definition.appendChild(cifItem.getValue());
            entry.appendChild(definition);
		}
	},
	superclass("_category"){
		@Override
		public void custom(CIFItem cifItem, CMLEntry entry) {
			CMLElement superclass = new CMLElement("superclass");
			superclass.appendChild(cifItem.getValue());
			entry.appendChild(superclass);
		}
	},
	type("_type"){
		@Override
		public void custom(CIFItem cifItem, CMLEntry entry) {
			String type = ("numb".equals(cifItem.getValue())) ? CMLUtil.XSD_FLOAT : CMLUtil.XSD_STRING;
			entry.addAttribute(new Attribute("dataType",type));
		}
	},
	units("_units"){
		@Override
		public void custom(CIFItem cifItem, CMLEntry entry) {
			String unit=cifItem.getValue();
			String newUnit="cifUnit:"+unit;
			entry.addAttribute(new Attribute("units",newUnit));
		}
		
	};

	final String cifName;
	static List<String> unitList=new ArrayList<String>();

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
	
	public static CMLEntry parseToEntry(CIFDataBlock dataBlock){
		CMLEntry entry = new CMLEntry();
		for(CIFFields field:CIFFields.values()){
			field.parse(dataBlock, entry);
		}
		return entry;
	}
}
