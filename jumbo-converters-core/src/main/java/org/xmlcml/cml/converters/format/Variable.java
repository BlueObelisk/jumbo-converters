package org.xmlcml.cml.converters.format;

public class Variable {
	private Double number;
	private Symbol symbol;

	public Variable() {
	}
	
	public Variable(Double d) {
		this.number = d;
	}
	
	public Variable(Symbol s) {
		symbol = s;
	}
	
	public static Variable createVariable(String s) {
		Variable v = null;
		try {
			Double number = new Double(s);
			v = new Variable(number);
		} catch (NumberFormatException nfe) {
			Symbol symbol = Symbol.createSymbol(s);
			if (symbol != null) {
				v = new Variable(symbol);
			}
		}
		return v;
	}

	public Double getDouble() {
		return number;
	}

	public Symbol getSymbol() {
		return symbol;
	}
}
