package org.xmlcml.cml.converters.compchem;

import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;

public class NumericFormat {
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(NumericFormat.class);
	private int width = -1;
	int decimal = -1;
	Pattern exponent = Pattern.compile("[EeGgDd][\\+\\- ]\\d\\d");
	
	public NumericFormat() {
		
	}
	public int getDecimal() {
		return decimal;
	}
	public void setDecimal(int decimal) {
		this.decimal = decimal;
	}
	public Pattern getExponent() {
		return exponent;
	}
	public void setExponent(Pattern exponent) {
		this.exponent = exponent;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int w) {
		this.width = w;
	}
	public void setF(int width, int decimal) {
		this.width = width;
		this.decimal = decimal;
	}
	public double readFormattedNumber(String field) {
		if (field == null || field.length() < width) {
			throw new RuntimeException("Cannot fit field into width");
		}
		double d = Double.NaN;
		if (field.trim().equals("")) {
			/** fortran can regard blank as zero */
			d = 0.0;
		} else if (field.indexOf(CMLConstants.S_STAR) != -1) {
			// asterisks are over/underflow in FORTRAN
		} else if (field.indexOf(CMLConstants.S_PERIOD) == -1) {
			// fortran can make decimal implicit ARGGGGH
			throw new RuntimeException("No decimal point given:"+field+":");
		} else {
			try {
				d = new Double(field.trim());
			} catch (Exception e) {
				throw new RuntimeException("cannot parse double: "+field, e);
			}
		}
		return d;
	}

}
