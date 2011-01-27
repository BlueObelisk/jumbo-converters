package org.xmlcml.cml.converters.format;


public class FloatField extends Field {

	public FloatField(SimpleFortranFormat sff, FieldType type) {
		super(sff, type);
		setFortranTypesAndParameters();
	}

	private void setFortranTypesAndParameters() {
		this.setFieldType(FieldType.F);
		this.setDataTypeClass(FieldType.F);
		simpleFortranFormat.parseFloatFormatInto(this);
	}
}
