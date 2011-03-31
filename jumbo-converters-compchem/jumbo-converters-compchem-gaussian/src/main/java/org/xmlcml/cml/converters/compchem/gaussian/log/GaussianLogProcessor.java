package org.xmlcml.cml.converters.compchem.gaussian.log;

import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.compchem.gaussian.GaussianCommon;
import org.xmlcml.cml.converters.text.Template;
import org.xmlcml.cml.converters.text.TemplateProcessor;

/**
 * @author pm286
 *
 */
public class GaussianLogProcessor extends TemplateProcessor {
	
	public GaussianLogProcessor(Template template) {
		super(template);
	}
	
	protected AbstractCommon getCommon() {
		return new GaussianCommon();
	}

}

