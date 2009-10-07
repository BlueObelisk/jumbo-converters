package org.xmlcml.cml.converters.marker;

import java.util.ArrayList;

import nu.xom.Element;
import nu.xom.Nodes;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.element.CMLModule;

public class TemplateSequence extends MarkerSequence {
	static Logger LOG = Logger.getLogger(MarkerSequence.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public TemplateSequence(Template parentTemplate) {
		super(parentTemplate);
		createTemplateSequence();
	}

	protected Marker createNewMarker(Element element) {
		return new Template(element, parentTemplate);
	}
	
	private void createTemplateSequence() {
		markerList = new ArrayList<Marker>();
		Nodes templateNodes = parentTemplate.xmlElement.query(TopTemplateContainer.TEMPLATE_NAME);
		for (int i = 0; i < templateNodes.size(); i++) {
			Element element = (Element)templateNodes.get(i);
			Template template = new Template(element, parentTemplate);
			markerList.add(template);
		}
	}

	@Override
	protected void tidyAfterCreatingSequence() {
		
	}
	
	@Override
	protected void tidyAfterAllMatching() {
	}

//	@Override
//	protected void tidyAfterMatching(LegacyStore legacyStore/*, int modulePos*/) {
//		LOG.debug("TEMPLATE tidyAfterMatching");
//		// work backwards since we are removing things from the module XML tree
//		LOG.debug("Legacy: "+legacyStore+"; markerList: "+markerList.size());
//		for (int i = markerList.size()-1; i >= 0; i--) {
//			Regex regex = (Regex) markerList.get(i);
//			MarkerCounter regexInternalCounter = regex.getMarkerCounter();
//			int moduleStart = regexInternalCounter.getModuleStart();
//			int moduleEnd = regexInternalCounter.getModuleEnd();
//			LOG.debug("MADE MODULE for matched REGEX: "+regexInternalCounter.getModuleStart()+"..."+regexInternalCounter.getModuleEnd());
//			CMLModule totalRegexModule = new CMLModule();
//			totalRegexModule.setId(regex.getId());
//			totalRegexModule.setRole(TopTemplateContainer.REGEX_MATCHES);
//			totalRegexModule.setDictRef(regex.createDictRef());
//			// go through in reverse order
//			LOG.debug("removing elements "+moduleEnd+" ... "+moduleStart);
//			parseExtractor = regex.getParseExtractor();
//			parseExtractor.extractModulesForEachRegexMatch(legacyStore,
//					moduleStart, moduleEnd,	wrapperModule);
//			// insert at end of replaceable section
//			CMLModule legacyModule = legacyStore.getModule();
//			try {
//				LOG.debug("inserting at: "+moduleStart+" ; childCount = "+legacyModule.getChildCount());
//			// kludge
//				if (moduleStart > legacyModule.getChildCount()) {
//					legacyModule.insertChild(totalRegexModule, legacyModule.getChildCount());
//				} else {
//					legacyModule.insertChild(totalRegexModule, moduleStart);
//				}
//				LOG.debug("childCount: "+legacyModule.getChildCount());
//				if (moduleStart > 0) {
//					LOG.debug("added child: "+legacyModule.getChild(moduleStart));
//				}
//				LOG.debug("last child: "+legacyModule.getChild(legacyModule.getChildCount()-1));
//			} catch (NullPointerException npe) {
//				throw new RuntimeException("BUG ", npe);
//			}
//			
//		}
//		LOG.trace("\n=============================end insert================================");
//		
////		parentTemplate.wrapInModule();
//	}
	
//	@Override
//	protected void processParsedModules() {
//		
//	}
	
	public String toString() {
		return super.toString();
	}
}
