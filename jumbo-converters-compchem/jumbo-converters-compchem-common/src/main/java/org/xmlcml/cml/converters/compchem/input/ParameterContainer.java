package org.xmlcml.cml.converters.compchem.input;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLModule;
import org.xmlcml.cml.element.CMLParameter;

public abstract class ParameterContainer extends CMLModule {
	private static Logger LOG = Logger.getLogger(ParameterContainer.class);
	
	private List<CMLParameter> parameterList = null;
	protected AbstractCompchemInputProcessor processor;
	
	protected ParameterContainer(AbstractCompchemInputProcessor processor) {
		this.processor = processor;
	}

	public List<CMLParameter> getParameterList() {
		return parameterList;
	}

	protected void addParameter(CMLParameter parameter) {
		if (parameterList == null) {
			parameterList = new ArrayList<CMLParameter>();
		}
		String dictRef = parameter.getDictRef();
		if (dictRef == null) {
			throw new RuntimeException("parameter must have dictRef");
		}
		// have we seen this parameter already
		boolean found = false;
		for (CMLParameter parameterx : parameterList) {
			String dictRefx = parameterx.getDictRef();
			if (dictRefx == null) {
				throw new RuntimeException("Should have trapped missing dictRef");
			}
			// if same dictRef, overwrite/replace
			if (dictRefx.equals(dictRef)) {
				parameterx.getParent().replaceChild(parameterx, parameter);
				int serial = parameterList.indexOf(parameterx);
				if (serial == -1) {
					throw new RuntimeException("should have found parameter in list");
				}
				parameterList.remove(serial);
				parameterList.add(serial, parameter);
				LOG.info("later version of dictRef overwrote earlier");
				found = true;
				break;
			}
		}
		if (!found) {
			this.appendChild(parameter);
			parameterList.add(parameter);
		}
	}
	
	protected void addModule(CMLModule module) {
		List<CMLElement> childElements = module.getChildCMLElements();
		for (int i = 0; i < childElements.size(); i++) {
			CMLElement childElement = childElements.get(i);
			if (childElement instanceof CMLParameter) {
				addParameter((CMLParameter) childElement);
			} else {
				throw new RuntimeException("non-parameter child of job not allowed: "+childElement);
			}
		}
	}
}
