package org.xmlcml.cml.converters.marker;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.element.CMLModule;

public abstract class MarkerSequence {
	private final static Logger LOG = Logger.getLogger(MarkerSequence.class);

	// implements the sequence
	protected List<Marker> markerList = new ArrayList<Marker>();
	protected Template parentTemplate;
	protected LegacyStore legacyStore;

	protected ParseExtractor parseExtractor;

	public MarkerSequence(Template parentTemplate) {
		this.parentTemplate = parentTemplate;
	}
	
	public Marker getParentTemplate() {
		return parentTemplate;
	}

	public List<Marker> getMarkerList() {
		return markerList;
	}
	
	public Marker getMarkerForSequencePointer(int pointer) {
		return (pointer >= 0 && pointer < markerList.size() ? markerList.get(pointer) : null);
	}

	public int size() {
		return markerList.size();
	}

	// test only
	public int[] getMatchedCounts() {
		int[] counts = new int[this.markerList.size()];
		int i = 0;
		for (Marker matcher : markerList) {
			counts[i++] = matcher.getMarkerCounter().getCount();
		}
		return counts;
	}


	public boolean areAllMarkersMatched() {
		boolean matched = true;
//		if (sequencePointer == markerList.size()) {
//			Marker marker = markerList.get(sequencePointer - 1);
		for (int i = 0; i < markerList.size(); i++) {
			matched = markerList.get(i).getMarkerCounter().isMatched();
			if (!matched) {
				break;
			}
		}
//		}
		return matched;
	}

	protected void match(LegacyStore legacyStore) {
		this.legacyStore = legacyStore;
		this.legacyStore = legacyStore;
		while (legacyStore.hasCurrentLegacyElement()) {
			legacyStore.recreateChildElementListAndResetPointer();
			if (!this.areAllMarkersMatched()) {
				break;
			}
		} 
		tidyAfterAllMatching();
	}

	
//	protected abstract void tidyAfterMatching(LegacyStore legacyStore, int modulePos);
//	protected abstract void tidyAfterMatching(LegacyStore legacyStore);

	protected abstract void tidyAfterAllMatching();
	
	protected abstract void tidyAfterCreatingSequence();
	
	protected abstract Marker createNewMarker(Element element);

//	protected abstract void processParsedModules();

	public void createAndProcessSequenceMarkers(String markerName) {
		markerList = new ArrayList<Marker>();
		Nodes nodes = parentTemplate.getXMLElement().query(markerName);
		if (nodes.size() > 0) {
			createMarkersAndAddToList(nodes);
			tidyAfterCreatingSequence();
		}
	}

	private void createMarkersAndAddToList(Nodes nodes) {
		for (int i = 0; i < nodes.size(); i++) {
			Element element = (Element)nodes.get(i);
			Marker marker = createNewMarker(element);
			markerList.add(marker);
		}
	}
	
	/**
	 * TODO bleuch! StringBuilder!
	 */
	public String toString() {
		String sp = parentTemplate.getIndentForFormatting();
		StringBuilder sb = new StringBuilder();
		sb.append( sp+"MARKER_SEQUENCE "+ParserUtil.getLastDotField(""+this.getClass())+"\n");
		sb.append( sp+"parentTemplate: "+parentTemplate.hashCode()+"\n");
		sb.append( sp+"legacy: "+legacyStore+"\n");
		sb.append( sp+"===sequence ===\n");
		for (Marker marker : markerList) {
			sb.append( sp+marker+"\n");
		}
		sb.append( sp+"===sequence ===\n");
		return sb.toString();
	}

	protected void tidyAfterMatching(LegacyStore legacyStore) {
		// work backwards since we are removing things from the module XML tree
		LOG.debug("Legacy: "+legacyStore+"; markerList: "+markerList.size());
		for (int i = markerList.size()-1; i >= 0; i--) {
			Marker marker = markerList.get(i);
			MarkerCounter counter = marker.getMarkerCounter();
			int moduleStart = counter.getModuleStart();
			int moduleEnd = counter.getModuleEnd();
			LOG.debug("MADE MODULE for matched MARKER: "+counter.getModuleStart()+"..."+counter.getModuleEnd());
			CMLModule wrapperModule = new CMLModule();
			wrapperModule.setId(marker.getId());
			wrapperModule.setRole(TopTemplateContainer.REGEX_MATCHES);
			wrapperModule.setDictRef(marker.createDictRef());
			// go through in reverse order
			LOG.debug("removing elements "+moduleEnd+" ... "+moduleStart);
			parseExtractor = marker.getParseExtractor();
			parseExtractor.extractModulesForEachMatch(legacyStore,
					moduleStart, moduleEnd,	wrapperModule);
			// insert at end of replaceable section
			CMLModule legacyModule = legacyStore.getModule();
			try {
				LOG.debug("inserting at: "+moduleStart+" ; childCount = "+legacyModule.getChildCount());
			// kludge
				if (moduleStart > legacyModule.getChildCount()) {
					legacyModule.insertChild(wrapperModule, legacyModule.getChildCount());
				} else {
					legacyModule.insertChild(wrapperModule, moduleStart);
				}
				LOG.debug("childCount: "+legacyModule.getChildCount());
				if (moduleStart > 0) {
					LOG.debug("added child: "+legacyModule.getChild(moduleStart));
				}
				LOG.debug("last child: "+legacyModule.getChild(legacyModule.getChildCount()-1));
			} catch (NullPointerException npe) {
				throw new RuntimeException("BUG ", npe);
			}
			
		}
		LOG.trace("\n=============================end insert================================");
		
	}
}
