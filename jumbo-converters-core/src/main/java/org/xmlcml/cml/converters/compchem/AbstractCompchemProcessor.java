package org.xmlcml.cml.converters.compchem;

public class AbstractCompchemProcessor {

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
