package org.xmlcml.cml.converters.compchem.input;

import java.util.HashSet;
import java.util.Set;

public class BasisSet extends Parameter {

	public static final String STO_3G = "STO-3G";
	public static final String B631GSTARSTAR = "6-31G**";
	
	public final static Set<String> basisSetSet;
	public static final String DEFAULT = STO_3G;
	public static final String NAME = "basisSet";
	
	static {
		basisSetSet = new HashSet<String>();
		basisSetSet.add(STO_3G);
		basisSetSet.add(B631GSTARSTAR);
	}
	
	public BasisSet(String basis) {
		if (basis == null) {
			basis = DEFAULT;
		}
		String basisUpper = basis.toUpperCase();
		if (!basisSetSet.contains(basisUpper)) {
			System.err.println("Supported basis sets:");
			for (String basisSet : basisSetSet) {
				System.err.println(">> "+basisSet);
			}
			throw new RuntimeException("Unknown/unsupported basis set "+basis);
		}
		this.value = basisUpper;
	}

	public AtomBasis getAtomBasis() {
		return new AtomBasis(AtomBasis.WILDCARD, value);
	}

}
