package org.xmlcml.cml.converters.format;

public class WidthField extends Field {

	public WidthField(SimpleFortranFormat sff, FieldType type) {
		super(sff, type);
		setTypesAndParameters();
	}
	private void setTypesAndParameters() {
		this.setDataTypeClass(fieldType);
		int widthToRead= simpleFortranFormat.readWidth();
		this.setWidth(widthToRead);
	}
}
