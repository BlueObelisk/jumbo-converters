package org.xmlcml.cml.converters.compchem.input;


import java.util.HashSet;
import java.util.Set;

import nu.xom.Attribute;

import org.apache.log4j.Logger;
import org.xmlcml.cml.element.CMLParameter;

public class Operations extends ParameterContainer {
	@SuppressWarnings("unused")
	private static Logger LOG = Logger.getLogger(Operations.class);

	public final static String ROLE         = "operations";

	public final static String FREQUENCY    = "ccml:frequency";
	public final static String GFINPUT      = "ccml:gfinput";
	public final static String INTEGRAL     = "ccml:integral";
	public final static String NMR          = "ccml:nmr";
	public final static String OPTIMIZATION = "ccml:optimization";
	public final static String POPULATION   = "ccml:population";
	public final static String SCRF         = "ccml:scrf";
	
	public final static Set<String> keywordSet   = new HashSet<String>();
	static {
		keywordSet.add(FREQUENCY);
		keywordSet.add(GFINPUT);
		keywordSet.add(INTEGRAL);
		keywordSet.add(NMR);
		keywordSet.add(OPTIMIZATION);
		keywordSet.add(POPULATION);
		keywordSet.add(SCRF);
	}

	
	public Operations(AbstractCompchemInputProcessor processor) {
		super(processor);
		this.addAttribute(new Attribute("role", ROLE));
	}
		
	public void addParameter(CMLParameter parameter) {
		String dictRef = parameter.getDictRef();
		if (dictRef == null) {
			throw new RuntimeException("parameter requires dictRef");
		} else if (keywordSet.contains(dictRef)) {
		} else {
			throw new RuntimeException("UNKNOWN dictRef in Operations "+dictRef);
		}
		super.addParameter(parameter);
	}
	

}
