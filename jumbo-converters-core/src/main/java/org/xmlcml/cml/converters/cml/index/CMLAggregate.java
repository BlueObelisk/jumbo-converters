package org.xmlcml.cml.converters.cml.index;

import static org.xmlcml.cml.base.CMLConstants.CML_XPATH;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractAggregator;
import org.xmlcml.cml.converters.Type;

/** 
 * aggregates CML files using XPath
 * (not quite sure what the difference from indexer is,
 * indexer returns filenames...)
 * 
 * typical result:
 * <?xml version="1.0" encoding="UTF-8"?>
 <list xmlns="http://www.xml-cml.org/schema">
 <header xmlns="">
  <column xpath="//*[]/@id[1]"/>
 </header>
 <row xmlns="">
  <value>pyridone</value>
 </row>
 <row xmlns="">
  <value>I</value>
 </row>
</list>

 * @author pm286
 *
 */
public class CMLAggregate extends AbstractAggregator {

	private static final Logger LOG = Logger.getLogger(CMLAggregate.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	
	public static String COUNT_FUNCTION = "COUNT";

	private static Pattern XPATH_PATTERN = Pattern.compile("(COUNT|FOO)\\((.*)\\)");
	public Element getSubElement(Element element) {
		
		Element newElem = new Element("row");
		List<String> xPathList = this.getXPathList();
		if (xPathList != null) {
			for (String xPath : xPathList) {
				Matcher matcher = XPATH_PATTERN.matcher(xPath);
				String function = null;
				if (matcher.matches()) {
					function = matcher.group(1);
					xPath = matcher.group(2);
				}
				Nodes nodes = null ;
				try {
					nodes = element.query(xPath, CML_XPATH);
				} catch (Exception ex) {
					LOG.warn("Xpath: " + xPath) ;
					ex.printStackTrace() ;
				}
				if (nodes.size() >= 1) {
					if (function == null) {
						Node node = nodes.get(0).copy();
						if (node instanceof Element) {
							newElem.appendChild(node);
						} else if(node instanceof Attribute){
							Element elem = new Element("value");
							elem.appendChild(node.getValue());
							newElem.appendChild(elem);
						} else{
							Element elem = new Element("value");
							elem.appendChild("BUG");
							newElem.appendChild(elem);
						}
					} else if (function.equals(COUNT_FUNCTION)) {
						Element elem = new Element("value");
						elem.appendChild(""+nodes.size());
						newElem.appendChild(elem);
					}
				} else {
					Element elem = new Element("value");
					newElem.appendChild(elem);
				}
			}
		}
		return newElem;
	}
	
	protected Element getHeader() {
		Element header = new Element("header");
		for (String xPath : this.getXPathList()) {
			Element cell = new Element("column");
			cell.addAttribute(new Attribute("xpath", xPath));
			header.appendChild(cell);
		}
		return header;
	}
	
	public Type getInputType() {
		return Type.CML;
	}
	
	@Override
	public String getRegistryInputType() {
		return null;
	}
	
	@Override
	public String getRegistryOutputType() {
		return null;
	}
	
	@Override
	public String getRegistryMessage() {
		return "null";
	}

}
