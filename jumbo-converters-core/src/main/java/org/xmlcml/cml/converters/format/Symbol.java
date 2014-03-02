package org.xmlcml.cml.converters.format;

import java.util.regex.Pattern;

public class Symbol {
	private String name;
	public static final String SYMBOL_REGEX = "[A-Za-z][A-Za-z_0-9]*";
	public static final String NUMBER_REGEX = "\\-?\\d+\\.\\d*";
	private final static Pattern SYMBOL_PATTERN = Pattern.compile(SYMBOL_REGEX);
	
	private Symbol(String s) {
		name = s;
	}
	
	public String getName() {
		return name;
	}

	public static Symbol createSymbol(String s) {
		Symbol symbol = null;
		if (isNumber(s)) {
			symbol = new Symbol(s);
		}
		return symbol;
	}

	public static boolean isNumber(String s) {
		return SYMBOL_PATTERN.matcher(s).matches();
	}
}
