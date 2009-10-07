package org.xmlcml.cml.converters.marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParentNode;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLModule;

/**
 * contains a list of regexes, compiled into ParserRegex objects
 * order of objects matters as they are processed sequentially
 * ParserTemplate
 * ...ParserRegex
 * ...ParserRegex
 * 
 * manages prefix and dictRef so that variables can be matched to
 * dictionaries
 * records the start and end line of parsing for current parse operation
 * 
 * @author pm286
 *
 */
public class TopTemplateContainer {
	private static Logger LOG = Logger.getLogger(TopTemplateContainer.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	static final String ARRAY_DATA_ATT = "arrayData";
	static final String TABLE_DATA_ATT = "tableData";
	static final String DICT_REF_ATT = "dictRef";
	static final String CAPTURE_GROUPS = "captureGroups";
	static final String REGEX_MATCHES = "regexMatches";
	static final String TEMPLATE_MATCHES = "templateMatches";

	static final String REGEX_NAME = "regex";
	static final String TEMPLATE_NAME = "template";
	
	public static final String TIDY_ATT = "tidyEmptyElements";
	public static final String DUMMY = "dummy";

//	private String prefix;
	// initial top-level template
	private Template topTemplate;
	private Template template;
	private LegacyStore legacyStore;
	private Map<String, Marker> markerIndex;
	private boolean tidyTemplates;
	private boolean createNestedModules;
	
	public TopTemplateContainer(String templateElementString) {
		createTemplate(CMLUtil.parseXML(templateElementString));
	}

	public TopTemplateContainer(Element templateElement) {
		init();
		createTemplate(templateElement);
	}
	
	private void init() {
		tidyTemplates = true;
		createNestedModules = true;
	}
	
	private void createTemplate(Element templateElement) {
		if (templateElement == null) {
			throw new RuntimeException("Null template");
		}
		Element topTemplateElement = new Element("topTemplate");
		ParentNode parent = templateElement.getParent();
		parent.replaceChild(templateElement, topTemplateElement);
		topTemplateElement.appendChild(templateElement);
		topTemplateElement.addAttribute(new Attribute("dictRef", "top:template"));
		topTemplate = new Template(topTemplateElement, null);
		this.template = new Template(templateElement, topTemplate);
		topTemplate.addIdsAndTLTRecursivelyAndCreateIndex(Template.ID_PREFIX, this);
		this.createIdIndex();
		LOG.trace("\nTOP: "+topTemplate);
	}
	
	private void createIdIndex() {
		markerIndex = new HashMap<String, Marker>();
		addIndexForMarkerSequence(topTemplate);
	}

	private void addIndexForMarkerSequence(Marker marker) {
		markerIndex.put(marker.getId(), marker);
		if (marker.childMarkerSequence != null) {
			for (Marker childMarker : marker.childMarkerSequence.markerList) {
				addIndexForMarkerSequence(childMarker);
			}
		}
	}
	
	public Marker getMarkerById(String id) {
		return (this.markerIndex != null) ? markerIndex.get(id) : null;
	}

	public CMLModule matchModuleAgainstTemplates(CMLModule moduleToParse, int originOfParse) {
		LOG.debug("TOP TEMPLATE: matchModuleAgainstTemplates");
		legacyStore = setModuleAndCreateLegacyStore(moduleToParse);
		legacyStore.setOrigin(originOfParse);
		this.topTemplate.legacyStore = legacyStore;
		this.template.match(legacyStore); 
		if (template.isCompletelyMatched()) {
			if (createNestedModules) {
				createNestedModulesByIds(moduleToParse);
				if (tidyTemplates) {
					while (true) {
						boolean change = cleanDuplicateTemplatesParents(moduleToParse);
						if (!change) {
							break;
						}
					}
				}
			}
			return moduleToParse;
		}
		return null;
	}

	private boolean cleanDuplicateTemplatesParents(CMLModule parentModule) {
		boolean change = false;
		Nodes childNodes = parentModule.query("cml:module[starts-with(@id,'"+Template.ID_PREFIX+"')]", CMLConstants.CML_XPATH);
		for (int i = childNodes.size()-1; i > 0; i--) {
			CMLModule child = (CMLModule) childNodes.get(i);
			CMLModule previousChild = (CMLModule) childNodes.get(i-1);
			if (child.getId().equals(previousChild.getId())) {
				moveChildrenFromTo(parentModule, child, previousChild);
				change = true;
				child.detach();
			}
			boolean change1 = cleanDuplicateTemplatesParents(previousChild);
			change = change || change1;
		}
		return change;
	}

	private void moveChildrenFromTo(CMLModule moduleToParse, CMLModule fromElement,
			CMLModule toElement) {
		if (fromElement.equals(toElement)) {
			throw new RuntimeException("IDENTICAL elements");
		}
		Elements fromChildElements = fromElement.getChildElements();
		for (int j = 0, max = fromChildElements.size(); j < max; j++) {
			Element fromChildElement = fromChildElements.get(j);
			fromChildElement.detach();
			toElement.appendChild(fromChildElement);
		}
		
	}

	private void createNestedModulesByIds(CMLModule module) {
		// only nodes of form id='r1.n.m....'
		Elements childElements = module.getChildElements();
		for (int i = 0; i < childElements.size(); i++) {
			Element childElement = childElements.get(i);
			Nodes idNodes = childElement.query("descendant-or-self::cml:module[starts-with(@id, '"+Regex.ID_PREFIX+"')]", CMLConstants.CML_XPATH);
			if (idNodes.size() > 0) {
				nestChildrenAndReplace(module, childElement, idNodes);
			}
		}
	}

	private void nestChildrenAndReplace(CMLModule module, Element childElement,
			Nodes idNodes) {
		List<CMLModule> idModuleList = createListOfNodesWithIds(idNodes);
		int level = 0;
		int rememberModulePosition = module.indexOf(childElement);
		CMLModule topModule = detachChildRecursivelyNestListReturnTopModule(idModuleList, level);
		Node newTop = topModule.getChild(0);
		module.insertChild(newTop, rememberModulePosition);
	}

	private List<CMLModule> createListOfNodesWithIds(Nodes idNodes) {
		List<CMLModule> idModuleList = new ArrayList<CMLModule>();
		for (int j = 0, max = idNodes.size(); j < max; j++) {
			CMLModule idModule = (CMLModule) idNodes.get(j);
			idModuleList.add(idModule);
		}
		return idModuleList;
	}

	private CMLModule detachChildRecursivelyNestListReturnTopModule(List<CMLModule> idModuleList, int level) {
		List<CMLModule> newParentModuleList = new ArrayList<CMLModule>();
		String lastRootId = null;
		CMLModule newParentModule = null;
		for (int i = 0, max = idModuleList.size(); i < max; i++) {
			CMLModule idModule = idModuleList.get(i);
			String id = idModule.getId();
			String rootId = getRootIdBeforeLastDot(id);
			LOG.trace("ROOT ID "+rootId);
			if (!rootId.equals(lastRootId)) {
				lastRootId = rootId;
				newParentModule = createNewParentModuleAndAddToNewModuleList(rootId, level);
				newParentModuleList.add(newParentModule);
			}
			idModule.detach();
			newParentModule.appendChild(idModule);
		}
		CMLModule topModule;
		if (lastRootId.lastIndexOf(CMLConstants.S_PERIOD) != -1) {
			topModule = detachChildRecursivelyNestListReturnTopModule(newParentModuleList, level-1);
		} else {
			topModule = newParentModuleList.get(0);
		}
		return topModule;
	}

	private CMLModule createNewParentModuleAndAddToNewModuleList(String rootId, int level) {
		CMLModule newParentModule = new CMLModule();
		String moduleId = createModuleIdForTemplateOrRegex(rootId, level);
		LOG.trace("MODULE ID "+moduleId);
		newParentModule.setId(moduleId);
		addTemplateInformation(newParentModule, moduleId);
		return newParentModule;
	}

	private void addTemplateInformation(CMLModule module, String moduleId) {
		Marker marker = this.getMarkerById(moduleId);
		if (marker == null) {
			throw new RuntimeException("Cannot find marker: "+moduleId);
		}
		marker.addTemplateInformationToModule(module);
	}

	private String createModuleIdForTemplateOrRegex(String rootId, int level) {
		return Template.ID_PREFIX+rootId.substring(1);
	}

	private String getRootIdBeforeLastDot(String id) {
		int idx = id.lastIndexOf(CMLConstants.S_PERIOD);
		return (idx < 0) ? id : id.substring(0, idx);
	}

	private LegacyStore setModuleAndCreateLegacyStore(CMLModule moduleToParse) {
		legacyStore = new LegacyStore(moduleToParse);
		return legacyStore;
	}
	
	public boolean isCompletelyMatched() {
		return template.isCompletelyMatched();
	}
	
	public String toString() {
		String s = "\n";
		s += "TOP_TEMPLATE: ("+topTemplate.hashCode()+")\n";
		s += ((template != null) ? template : "NULL")+"\n";
		s += "legacyStore: "+((legacyStore != null) ? legacyStore : "")+"\n";
		return s;
	}

	public void setTidyTemplates(boolean b) {
		tidyTemplates = b;
	}

	public boolean isTidyTemplates() {
		return tidyTemplates;
	}

	public boolean isCreateNestedModules() {
		return createNestedModules;
	}

	public void setCreateNestedModules(boolean createNestedModules) {
		this.createNestedModules = createNestedModules;
	}

	public LegacyStore getLegacyStore() {
		return legacyStore;
	}

	public Template getRootTemplate() {
		return template;
	}

	public Template getTopTemplate() {
		return topTemplate;
	}

}
