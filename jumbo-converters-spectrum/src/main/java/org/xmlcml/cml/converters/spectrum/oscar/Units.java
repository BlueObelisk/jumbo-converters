package org.xmlcml.cml.converters.spectrum.oscar;

import nu.xom.Element;

import org.xmlcml.cml.base.CMLUtil;

public class Units {
	
	private String rawValue;
	private String qname;

	public String getQname() {
		return qname;
	}

	public Units(Element units) {
		if (units.getChildElements().size() > 0) {
			CMLUtil.debug(units, "UNITS");
			throw new RuntimeException("cannot have child elements in units");
		}
		rawValue = units.getValue();
		qname = null;
		if ("Hz".equals(rawValue)) {
			qname = "unit:hz";
		} else if ("MHz".equals(rawValue)) {
			qname = "unit:mhz";
		} else if ("H".equals(rawValue)) {
			qname = "unit:hydrogen";
		} else {
			throw new RuntimeException("unknown units "+rawValue);
		}
	}
}
