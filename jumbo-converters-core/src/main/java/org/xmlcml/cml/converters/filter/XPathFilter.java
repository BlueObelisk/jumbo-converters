package org.xmlcml.cml.converters.filter;

import nu.xom.Nodes;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.euclid.IntRange;
import org.xmlcml.euclid.Real;
import org.xmlcml.euclid.RealRange;


public class XPathFilter implements AbstractCMLFilter {
	
	
	public static final String NODE_COUNT = "NodeCount";
	public static final String SINGLE_NODE_VALUE = "SingleNodeValue";
	
	private Object matchObject;
	private String control;
	private String xpath;

	public XPathFilter(String control, String xpath, Object matchObject) {
		if (NODE_COUNT.equals(control)) {
			
		} else if(SINGLE_NODE_VALUE.equals(control)) {
			
		} else {
			throw new RuntimeException("Unknown XpathFilter control: "+control);
		}
		this.matchObject = matchObject;
		this.xpath = xpath;
		this.control = control;
	}

	public boolean accept(CMLElement element) {
		boolean accept = false;
		if (NODE_COUNT.equals(control)) {
			accept = nodeCount(element);
		} else if(SINGLE_NODE_VALUE.equals(control)) {
			accept = singleNodeValue(element);			
		} else {
		}
		return accept;
	}
	
	private boolean nodeCount(CMLElement element) {
		boolean matches = false;
		int count = element.query(xpath).size();
		if (matchObject instanceof Integer) {
			matches = ((Integer)matchObject) == count;
		} else if (matchObject instanceof Double) {
			matches = (new Double(matchObject.toString()).intValue() == count);
		} else if (matchObject instanceof IntRange) {
			matches = ((IntRange)matchObject).contains(count);
		} else if (matchObject instanceof RealRange) {
			matches = ((RealRange)matchObject).contains(count);
		} else {
			System.err.println("Cannot match node count to "+matchObject);
		}
		return matches;
	}
	
	private boolean singleNodeValue(CMLElement element) {
		boolean matches = false;
		Nodes nodes = element.query(xpath);
		if (nodes.size() == 1) {
			String value = nodes.get(0).getValue();
			if (matchObject instanceof Integer) {
				matches = ((Integer)matchObject) == Integer.parseInt(value);
			} else if (matchObject instanceof Double) {
				matches = Real.isEqual(
						Double.parseDouble(matchObject.toString()),
						Double.parseDouble(value), Real.EPS);
			} else if (matchObject instanceof IntRange) {
				matches = ((IntRange)matchObject).contains(Integer.parseInt(value));
			} else if (matchObject instanceof RealRange) {
				matches = ((RealRange)matchObject).contains(Double.parseDouble(value));
			} else if (matchObject instanceof String) {
				matches = matchObject.equals(value);
			} else {
				System.err.println("Cannot match value to "+matchObject);
			}
		}
		return matches;
	}
	
}
