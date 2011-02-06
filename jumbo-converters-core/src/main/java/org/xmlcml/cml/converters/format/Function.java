package org.xmlcml.cml.converters.format;

import java.util.HashSet;
import java.util.Set;

public class Function {
	public final static String COS = "cos";
	public final static String SIN = "sin";
	public final static Set<String> functions = new HashSet<String>();
	static {
		functions.add(COS);
		functions.add(SIN);
	}
	private String name;
	
	public Function(String f) {
		name = f;
	}
	
	public String getName() {
		return name;
	}

	public static Function createFunction(String s) {
		Function f = null;
		if (functions.contains(s)) {
			f = new Function(s);
		}
		return f;
	}
}
