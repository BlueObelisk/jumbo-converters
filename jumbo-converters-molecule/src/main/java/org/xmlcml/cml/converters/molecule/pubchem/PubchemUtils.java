package org.xmlcml.cml.converters.molecule.pubchem;

import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.XPathContext;

public class PubchemUtils {

	/**
	 * create from element with id in string content
	 * @param element
	 * @return
	 */
	static String createAtomId(Element element) {
		return PubchemUtils.createAtomId(element.getValue());
	}

	/**
	 * create from string (an integer)
	 * @param id
	 * @return
	 */
	static String createAtomId(String id) {
		try {
			new Integer(id);
		} catch (NumberFormatException nfe) {
			throw new RuntimeException("id should be integer");
		}
		return "a"+id;
	}

	static void checkUnknowns(Element element, String[] ss) {
		String qq = "*[not(";
		for (String s : ss) {
			qq += "local-name()='"+s+"' or ";
		}
		qq += "'')]";
		Nodes nodes = element.query(qq, PubchemUtils.NIH_XPATH);
		if (nodes.size() > 0) {
			String msg = "Unknown elements: ";
			for (int i = 0; i < nodes.size(); i++) {
				msg += ((Element) nodes.get(i)).getLocalName()+" ";
			}
			throw new RuntimeException(msg);
		}
	}

	static String NIH_NS = "http://www.ncbi.nlm.nih.gov";
	static XPathContext NIH_XPATH = new XPathContext("p", NIH_NS);

}
