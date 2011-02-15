package org.xmlcml.cml.converters.compchem.input;

import java.util.HashSet;
import java.util.Set;

import nu.xom.Attribute;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLParameter;
import org.xmlcml.cml.element.CMLScalar;


public class QM extends ParameterContainer {
	@SuppressWarnings("unused")
	private static Logger LOG = Logger.getLogger(QM.class);
	
	public final static String ROLE = "qm";
	
	public final static String BASIS = "ccml:basis";
	public final static String METHOD = "ccml:method";
	public final static String DIFFUSE = "ccml:diffuseFunctions";
	
	private static Set<String> keywordSet = new HashSet<String>();
	static {
		keywordSet.add(BASIS);
		keywordSet.add(METHOD);
		keywordSet.add(DIFFUSE);
	}
	
	public QM(AbstractCompchemInputProcessor processor) {
		super(processor);
		this.addAttribute(new Attribute("role", ROLE));
	}
	
	public void addParameter(CMLParameter parameter) {
		String dictRef = parameter.getDictRef();
		if (dictRef == null) {
			throw new RuntimeException("parameter requires dictRef");
		} else if (keywordSet.contains(dictRef)) {
		} else {
			throw new RuntimeException("UNKNOWN dictRef in QM "+dictRef);
		}
		super.addParameter(parameter);
	}
	
	public String makeMethodAndBasis() {
		String s = "";
		String method = CMLUtil.getSingleValue(this, ".//cml:parameter[@dictRef='"+METHOD+"']/" +
				"cml:"+CMLScalar.TAG, CMLConstants.CML_XPATH);
		if (method == null) {
			method = processor.getDefaultMethod();
		}
		s += method;
		String basis = CMLUtil.getSingleValue(this, ".//cml:parameter[@dictRef='"+BASIS+"']/" +
				"cml:"+CMLScalar.TAG, CMLConstants.CML_XPATH);
		if (basis == null) {
			basis = processor.getDefaultBasis();
		}
			s += "/"+basis;
		String diffuseFunctions = CMLUtil.getSingleValue(this, 
				".//cml:parameter[@dictRef='"+DIFFUSE+"']/" +
				"cml:"+CMLScalar.TAG, CMLConstants.CML_XPATH);
		if (diffuseFunctions != null) {
			s += "("+diffuseFunctions+")";
		}
		return s;
	}


}
