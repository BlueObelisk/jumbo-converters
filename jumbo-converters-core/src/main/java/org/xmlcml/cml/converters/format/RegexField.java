package org.xmlcml.cml.converters.format;

import java.util.regex.Matcher;

import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.element.CMLScalar;

public class RegexField {

	private static final String A = "A";
	private static final String B = "B";
	private static final String D = "D";
	private static final String F = "F";
	private static final String I = "I";
	public static final String X = "X";
	public static final String E = "E";    // scientific notation
	
	private Integer multiplier;
	private String type;
	private Integer width;
	private Integer decimal;
	private String name;
	private String unit;
	private String expandedField;

	public RegexField() {
		
	}
	public Integer getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(Integer multiplier) {
		this.multiplier = multiplier;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getDecimal() {
		return decimal;
	}

	public void setDecimal(Integer decimal) {
		this.decimal = decimal;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	CMLScalar createNamedTypedScalar(Matcher matcher, int i, String matcherGroup) {
		CMLScalar scalar = LineReader.createScalar(matcherGroup.trim(), type);
		if (name != null) {
			scalar.setDictRef(name);
		}
		return scalar;
	}
	

	public static String createType(String string) {
		String type = null;
		if (string.equals(A)) {
			type = CMLConstants.XSD_STRING;
		} else if (string.equals(I)) {
			type = CMLConstants.XSD_INTEGER;
		} else if (string.equals(D)) {
			type = CMLConstants.XSD_DATE;
		} else if (string.equals(F)) {
			type = CMLConstants.XSD_DOUBLE;
		} else if (string.equals(B)) {
			type = CMLConstants.XSD_BOOLEAN;
		} else if (string.equals(E)) {
			type = E;
		} else if (string.equals(X)) {
//			type = CMLConstants.XSD_STRING;
			type = X;
		} else {
			throw new RuntimeException("bad type: "+string);
		}
		return type;
	}
	
	public String createExpandedField(String context) {
		expandedField = "";
		if (type.equals(CMLConstants.XSD_STRING)) {
			expandedField = RegexProcessor.createStringField(width);
		} else if (type.equals(CMLConstants.XSD_BOOLEAN)) {
			expandedField = RegexProcessor.createIntegerField(width);
		} else if (type.equals(CMLConstants.XSD_INTEGER)) {
			expandedField = RegexProcessor.createIntegerField(width);
		} else if (type.equals(CMLConstants.XSD_DOUBLE)) {
			expandedField = RegexProcessor.createFloatField(width, decimal);
		} else if (type.equals(E)) {
			expandedField = RegexProcessor.createExponentialField(width, decimal);
		} else if (type.equals(CMLConstants.XSD_DATE)) {
			expandedField = RegexProcessor.createDateField(width);
		} else if (type.equals(X)) {
			expandedField = RegexProcessor.createAnyField(width);
		} else {
			throw new RuntimeException("bad type in: "+context);
		}
		return expandedField;
	}
	
	public void debug() {
		System.out.println(">>> "+this);
	}
	
	public String toString() {
		String s = "";
		s += " multiplier: "+multiplier;
		s += " type: "+type;
		s += " width: "+width;
		s += " decimal: "+decimal;
		s += " name: "+name;
		s += " unit: "+unit;
		s += "\n"+expandedField;
		return s;
	}
}
