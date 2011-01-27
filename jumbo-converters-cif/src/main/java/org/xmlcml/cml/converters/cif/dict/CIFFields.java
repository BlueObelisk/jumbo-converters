package org.xmlcml.cml.converters.cif.dict;

import org.xmlcml.cif.CIFDataBlock;
import org.xmlcml.cif.CIFItem;
import org.xmlcml.cml.base.CMLElement;
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
	};

	final String cifName;

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
}
