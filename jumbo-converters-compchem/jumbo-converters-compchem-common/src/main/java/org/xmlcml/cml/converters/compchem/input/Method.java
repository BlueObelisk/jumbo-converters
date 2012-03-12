package org.xmlcml.cml.converters.compchem.input;

import java.util.HashSet;
import java.util.Set;

public class Method extends Parameter {

	public static final String B3LYP = "B3LYP";
	public static final String HF = "HF";

	public final static Set<String> methodSet;
	public static final String DEFAULT = HF;
	public static final String NAME = "method";
	
	static {
		methodSet = new HashSet<String>();
		methodSet.add(B3LYP);
		methodSet.add(HF);
	}

	public Method(String method) {
		if (method == null) {
			method = DEFAULT;
		}
		String method1 = method.toUpperCase();
		if (!methodSet.contains(method1)) {
			System.err.println("Supported method:");
			for (String methodx : methodSet) {
				System.err.println(">> "+methodx);
			}
			throw new RuntimeException("Unknown/unsupported method "+method);
		}
		this.value = method;
	}
}
