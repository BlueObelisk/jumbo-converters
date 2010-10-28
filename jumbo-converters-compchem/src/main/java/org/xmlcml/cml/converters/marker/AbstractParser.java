package org.xmlcml.cml.converters.marker;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Nodes;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.ConverterLog;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.element.CMLModule;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.euclid.Util;

/** a helper class for converters
 * common for complex formats which cannot all be done in the converter
 * 
 * mutating into JUMBOMarker2.
 * 
 * The Abstract Parser should be subclassed - a simple example is TextParserConverter.
 * The key routine is convertToXML(lines) which takes in a list of lines to be parsed
 * It reads a list of templates from an auxiliary file and converts them to a list
 * of ParserTemplates (obtainable through getParserTemplateList()).
 * it then parses the lines into a
 * CMLModule.
 * 
 * 
 * The static nature will be refactored when I work out the classes.
 * 
 * @author pm286
 *
 */
public abstract class AbstractParser extends AbstractConverter {
	private static Logger LOG = Logger.getLogger(AbstractParser.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	
	private CMLModule cmlModule;
	private String prefix = "foo";
//	@SuppressWarnings("unused")
	protected ConverterLog converterLog;
	protected List<TopTemplateContainer> parserTemplateList; 
	protected int originOfParse = 0;
	
	public AbstractParser() {
	}

	public Type getInputType() {
		return Type.TXT;
	}

	public Type getOutputType() {
		return Type.CML;
	}
	
	public int getOriginOfParse() {
		return originOfParse;
	}

	public void setOriginOfParse(int originOfParse) {
		this.originOfParse = originOfParse;
	}

	protected void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	protected String getPrefix() {
		return this.prefix;
	}
	
	public List<TopTemplateContainer> getParserTemplateList() {
		return parserTemplateList;
	}

	public void setParserTemplateList(List<TopTemplateContainer> parserTemplateList) {
		this.parserTemplateList = parserTemplateList;
	}

	public CMLModule getCmlModule() {
		return cmlModule;
	}

	@Override
	public Element convertToXML(List<String> lines) {
		readLinesIntoScalars(lines);
		readTemplateListFromAuxElement();
		parseModule();
		return cmlModule;
	}
	/** reads lines as ASCII
	 */
	public CMLElement readLinesIntoScalars(List<String> lines) {
		cmlModule = new CMLModule();
		cmlModule.setRole("line");
		for (String line : lines) {
			CMLScalar scalar = new CMLScalar();
			scalar.setValueNoTrim(Util.rightTrim(line));
			scalar.removeAttribute("dataType");
			cmlModule.appendChild(scalar);
		}
		LOG.debug("read lines: "+lines.size());
//		cmlModule.debug("Note debug() does not copy scalar values properly");
		return cmlModule;
	}


	private List<TopTemplateContainer> readTemplateListFromAuxElement() {
		Element templateListElement = getAuxElement();
		if (!templateListElement.getLocalName().equals("templateList")) {
			throw new RuntimeException("template list must be under <templateList>");
		}
		generateTemplateList(templateListElement);
		return parserTemplateList;
	}

	/**
	 * processes the list of templates into a list of ParseTemplate objects
	 * stored as parserTemplateList
	 * @param templateListElement
	 */
	public void generateTemplateList(Element templateListElement) {
		String prefix = templateListElement.getAttributeValue("prefix");
		if (prefix == null) {
			throw new RuntimeException("templateList must have prefix attribute");
		}
		parserTemplateList = new ArrayList<TopTemplateContainer>();
		// read templates
		Nodes nodes = templateListElement.query("template");
		for (int i = 0; i < nodes.size(); i++) {
			TopTemplateContainer parserTemplate = new TopTemplateContainer((Element)nodes.get(i));
//			parserTemplate.setPrefix(prefix);
			parserTemplateList.add(parserTemplate);
		}
		LOG.debug("read templates: "+parserTemplateList.size());
	}

	/** read templateList as XML
	 * 
	 * @param name
	 * @return
	 */
	protected Element getAuxElement(String name) {
		Element auxElement = null;
		URL inputUrl = Util.getResource(name);
		try {
			auxElement = new Builder().build(inputUrl.openStream()).getRootElement();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return auxElement;
	}

	/** parse module
	 * result ends in cmlModule
	 */
	public void parseModule() {
		for (TopTemplateContainer parserTemplate : parserTemplateList) {
			LOG.debug("template");
			parserTemplate.matchModuleAgainstTemplates(cmlModule, originOfParse);
		}
	}

	public void setConverterLog(ConverterLog converterLog) {
		this.converterLog = converterLog;
	}


}
