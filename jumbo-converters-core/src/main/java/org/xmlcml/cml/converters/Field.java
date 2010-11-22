package org.xmlcml.cml.converters;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

public class Field extends Element {
	private final static Logger LOG = Logger.getLogger(Field.class);
	
	public static final String POINT = "point";
	public static final char C_PERIOD = '.';
	public static final String CLASS = "class";
	public static final String TYPE = "type";
	public static final String LAST = "last";
	
	public static final char C_X = 'x';
    public  static final char C_FLAG = '~';
	public static final char C_B = 'b';
	public static final char C_D = 'd';
	public static final char C_F = 'f';
	public static final char C_I = 'i';
	public static final char C_S = 's';
	public static final String FIELD = "field";
	
	private int last;
	private Field overflow;
	private char typeChar;
	private Class clazz;
	private String localDictRef;
	
	public Field() {
		super(FIELD);
	}

	public Field(String field) {
		this();
		process(field);
	}

	public void setLocalDictRef(String localDictRef) {
		this.localDictRef = localDictRef;
	}

	public Class getParseClass() {
		return clazz;
	}

	public int getLast() {
		return last;
	}

	public Field getOverflow() {
		return overflow;
	}

	public char getTypeChar() {
		return typeChar;
	}
	public String getLocalDictRef() {
		return localDictRef;
	}

	void process(String field) {
		clazz = null;
		if (field.length() == 0) {
			
		} else if (field.charAt(0) == C_FLAG && field.length() > 1){
			typeChar = field.charAt(1);
			last = field.length()-1;
			if (false) {
			} else if (typeChar == C_B) {
				last = field.lastIndexOf(C_B);
				clazz = Boolean.class;
			} else if (typeChar == C_D) {
				last = field.lastIndexOf(C_D);
				clazz = DateTime.class;
			} else if (typeChar == C_F) {
				last = field.lastIndexOf(C_F);
				int dot = field.indexOf(C_PERIOD);
				if (dot >= 0) {
					this.addAttribute(new Attribute(POINT, ""+dot));
				}
				clazz = Double.class;
			} else if (typeChar == C_I) {
				last = field.lastIndexOf(C_I);
				clazz = Integer.class;
			} else if (typeChar == C_S) {
				last = field.lastIndexOf(C_S);
				clazz = String.class;
			} else if (typeChar == C_X) {
				last = field.lastIndexOf(C_X);
				clazz = null;
			} else {
				last = field.length()-1;
				clazz = null;
				LOG.warn("Uninterpretable field (?skipped):"+field);
			}
			if (last < field.length()-1) {
				overflow = new Field(field.substring(last+1));
				field = field.substring(0, last+1);
			}
			this.addAttribute(new Attribute(LAST, ""+last));
		} else {
			typeChar =C_X;
			clazz = null;
		}
		this.addAttribute(new Attribute(TYPE, ""+typeChar));
		this.addAttribute(new Attribute(CLASS, ""+clazz));
		
	}

	public boolean hasScalarType() {
		return (
			typeChar == C_B || 
			typeChar == C_D ||
			typeChar == C_F ||
			typeChar == C_I ||
			typeChar == C_S
		);
	}

}
