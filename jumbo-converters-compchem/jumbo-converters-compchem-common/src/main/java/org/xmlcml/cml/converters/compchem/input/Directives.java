package org.xmlcml.cml.converters.compchem.input;

import java.util.HashSet;
import java.util.Set;

import nu.xom.Attribute;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLParameter;

public class Directives extends ParameterContainer {
	@SuppressWarnings("unused")
	private static Logger LOG = Logger.getLogger(Directives.class);

	public final static String ROLE    = "directives";
	
	public final static String VERBOSITY = "ccml:verbosity";
	public final static String MAXMEM = "ccml:maximumMemory";
	public final static String PROCCOUNT = "ccml:processorCount";
	public final static String NOSYMMETRY = "ccml:nosymmetry";
	
	private static Set<String> keywordSet = new HashSet<String>();
	static {
		keywordSet.add(VERBOSITY);
		keywordSet.add(MAXMEM);
		keywordSet.add(PROCCOUNT);
		keywordSet.add(NOSYMMETRY);
	};
	
	public Directives(AbstractCompchemInputProcessor processor) {
		super(processor);
		this.addAttribute(new Attribute("role", ROLE));
	}
	
	protected void addParameter(CMLParameter parameter) {
		String dictRef = parameter.getDictRef();
		if (dictRef == null) {
			throw new RuntimeException("parameter requires dictRef");
		} else if (keywordSet.contains(dictRef)) {
		} else {
			throw new RuntimeException("UNKNOWN dictRef in Directive "+dictRef);
		}
		super.addParameter(parameter);
	}
	
	public String outputMaximumMemory() {
	/*
	 	    <parameter dictRef="ccml:maximumMemory">
	 	      <scalar dataType="xsd:integer" units="units:megaword">500</scalar>
	 	    </parameter>
	 */
		return CMLUtil.getSingleValue(this, ".//cml:parameter[@dictRef='"+MAXMEM+"']/" +
				"cml:scalar[@dataType='xsd:integer']", CMLConstants.CML_XPATH);
	}
	
	public String outputMemoryUnits() {
		String memoryUnits = CMLUtil.getSingleValue(this, ".//cml:parameter[@dictRef='"+MAXMEM+"']/" +
				"cml:scalar[@dataType='xsd:integer']/@units", CMLConstants.CML_XPATH);
		if (memoryUnits == null) {
			memoryUnits = "Unknown";
		} else if (memoryUnits.toLowerCase().equals("units:megaword")) {
			memoryUnits = "MW";
		}
		return memoryUnits;
	}

	public String outputProcessorCount() {
		return CMLUtil.getSingleValue(this, ".//cml:parameter[@dictRef='"+PROCCOUNT+"']/cml:scalar[@dataType='xsd:integer']", CMLConstants.CML_XPATH);
	}
	
	public String outputVerbosity() {
		String s = CMLUtil.getSingleValue(this, ".//cml:parameter[@dictRef='"+VERBOSITY+"']/cml:scalar", CMLConstants.CML_XPATH);
		return s;
	}

}
