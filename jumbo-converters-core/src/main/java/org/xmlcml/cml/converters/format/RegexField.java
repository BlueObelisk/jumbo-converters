package org.xmlcml.cml.converters.format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.interfacex.HasDataType;
import org.xmlcml.cml.interfacex.HasDictRef;

public class RegexField {

	private static final String A = "A";
	private static final String B = "B";
	private static final String D = "D";
	private static final String F = "F";
	private static final String I = "I";
	public static final String X = "X";
	public static final String E = "E";    // scientific notation
	
	private Integer minMultiplier;
	private Integer maxMultiplier;
	private String type;
	private Integer width;
	private Integer decimal;
	private String name;
	private String unit;
	private String expandedField;
	private String multipliedField;

	public RegexField() {
		
	}
	public Integer getMinMultiplier() {
		return minMultiplier;
	}

	public void setMinMultiplier(Integer multiplier) {
		this.minMultiplier = multiplier;
	}

	public Integer getMaxMultiplier() {
		return maxMultiplier;
	}

	public void setMaxMultiplier(Integer multiplier) {
		this.maxMultiplier = multiplier;
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

	HasDataType createNamedTypedScalar(Matcher matcher, int i, String matcherGroup) {
		HasDataType hasDataType = null;
		String dataType = createType(type);
		if (minMultiplier == null || (minMultiplier == 1 && maxMultiplier == 1)) {
			hasDataType = LineReader.createScalar(matcherGroup.trim(), dataType);
		} else {
			dataType = createType(type);
			dataType = (E.equals(dataType)) ? CMLConstants.XSD_DOUBLE : dataType;
			CMLArray array = new CMLArray(dataType);
			Pattern pattern = Pattern.compile(multipliedField);
			Matcher matcher0 = pattern.matcher(matcherGroup);
			int start = 0;
			while (matcher0.find(start)) {
				int end = matcher0.end();
				String scalarS = matcherGroup.substring(start, end);
				CMLScalar scalar = LineReader.createScalar(scalarS, dataType);
				array.append(scalar);
				start = end;
			}
			if (matcherGroup.substring(start).trim().length() != 0) {
				throw new RuntimeException("Cannot parse multiplied field ("+multipliedField+") into "+matcherGroup);
			}
			hasDataType = array;
		}
		if (name != null) {
			((HasDictRef)hasDataType).setDictRef(name);
		}
		return hasDataType;
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
		} else if (type.equals(A)) {
			expandedField = RegexProcessor.createStringField(width);
		} else if (type.equals(CMLConstants.XSD_BOOLEAN)|| type.equals(B)) {
			expandedField = RegexProcessor.createIntegerField(width);
		} else if (type.equals(CMLConstants.XSD_INTEGER)|| type.equals(I)) {
			expandedField = RegexProcessor.createIntegerField(width);
		} else if (type.equals(CMLConstants.XSD_DOUBLE) || type.equals(F)) {
			expandedField = RegexProcessor.createFloatField(width, decimal);
		} else if (type.equals(E)) {
			expandedField = RegexProcessor.createExponentialField(width, decimal);
		} else if (type.equals(CMLConstants.XSD_DATE)|| type.equals(D)) {
			expandedField = RegexProcessor.createDateField(width);
		} else if (type.equals(X)) {
			expandedField = RegexProcessor.createAnyField(width);
		} else {
			throw new RuntimeException("bad type ("+type+") in: "+context);
		}
		addMultipliers();
		return expandedField;
	}
	private void addMultipliers() {
		if (minMultiplier != null) {
			multipliedField = expandedField;
			if (minMultiplier.equals(maxMultiplier) && minMultiplier != 1) {
				addNonCapturingGroup();
				expandedField = "(?:"+expandedField+")"+"{"+minMultiplier+"}";
			} else {
				addNonCapturingGroup();
				expandedField = "(?:"+expandedField+")"+"{"+minMultiplier+","+maxMultiplier+"}";
			}
			expandedField = "("+expandedField+")";
		}
	}
	private void addNonCapturingGroup() {
		int idx = expandedField.indexOf(CMLConstants.S_LBRAK);
		expandedField = expandedField.substring(0, idx+1)+"?:"+expandedField.substring(idx+1);
	}
	
	public void debug() {
		System.out.println(">>> "+this);
	}
	
	public String toString() {
		String s = "";
		s += " multiplier: "+minMultiplier;
		s += " type: "+type;
		s += " width: "+width;
		s += " decimal: "+decimal;
		s += " name: "+name;
		s += " unit: "+unit;
		s += "\n"+expandedField;
		return s;
	}
}
