package org.xmlcml.cml.converters.compchem;


/**
 * this may be obsolete
 * @author pm286
 *
 */
public abstract class AbstractCompchemProcessor {

	public enum Solvent {
		ACETONE("Acetone"),
		DICHLOROMETHANE("DiChloroMethane"),
		BENZENE("Benzene"),
		WATER("Water"),
		ETHER("Ether"),
		METHANOL("Methanol"),
		CHLOROFORM("Chloroform"),
		DMSO("DMSO"),
		ACETONITRILE("Acetonitrile"),
		TOLUENE("Toluene"),
		THF("THF"),
		CCL4("CCl4");
	
		private final String name;
	
		Solvent(String name) {
			this.name = name;
		}
	
		public String getName() {
			return this.name;
		}
	}

}
