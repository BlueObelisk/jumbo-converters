package org.xmlcml.cml.converters.compchem.qespresso.log;

import nu.xom.Builder;
import nu.xom.Document;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.text.Template;
import org.xmlcml.cml.converters.text.TemplateProcessor;
import org.xmlcml.euclid.Util;

/**
 * @author pm286
 *
 */
public class QuantumEspressoLogProcessor extends TemplateProcessor {
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(QuantumEspressoLogProcessor.class);
	
	private static final String COMMON_XML = "common.xml";

	public QuantumEspressoLogProcessor(Template template) {
		super(template);
		init();
	}
	
	private void init() {
		readProperties();
	}

	private void readProperties() {
		try {
		Class<?> clazz = this.getClass();
		String[] packageBits = clazz.getPackage().getName().split("\\.");
		String dir = "";
		for (int i = 0; i < packageBits.length-1; i++) {
			dir += packageBits[i];
			dir += CMLConstants.S_SLASH;
		}
		Document props = new Builder().build(Util.getResourceUsingContextClassLoader(dir+COMMON_XML, clazz));
		CMLUtil.debug(props.getRootElement(), "PROPS");
		} catch (Exception e) {
			throw new RuntimeException("cannot read common: ", e);
		}
	}
	
}

