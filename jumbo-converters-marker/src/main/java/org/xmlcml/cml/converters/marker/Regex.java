package org.xmlcml.cml.converters.marker;

import java.util.List;
import java.util.regex.Pattern;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLScalar;

/** a single line regular expression
 * The template components are normally split into a list of ParserRegex
 * 
 * @author pm286
 *
 */
public class Regex extends Marker {
	private static Logger LOG = Logger.getLogger(Regex.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	
	public static final String ID_PREFIX = "r";
	public static final String NON_CAPTURE = "?:";
	
	FieldNameList fieldNameList;
	private Template parentTemplate;
	
	// the capturegroups defined by the regex
	CaptureGroupList captureGroupList = new CaptureGroupList();
	// dictRefLocalName of template ancestor
	String templateDictRefLocalName;
	// prefix of template ancestor
	String templateDictRefPrefix;
	private RegexInterpreter regexInterpreter;
	
	/**
	 * creates a regex from a <regex> elements
	 * attributes include:
	 *   minCount
	 *   maxCount
	 * which determine the number of times the regex is used sequentially
	 *   translate (are regex abbreviations translated into full regex)
	 * @param regexElement
	 * @param rawRegexString
	 */
	public Regex(Element regexElement, Template parentTemplate) {
		super(regexElement, parentTemplate);
		this.parentTemplate = parentTemplate;
		makeComponents();
	}

	private void makeComponents() {
//		this.generatingElement = new Element(generatingElement);
		counter = new RegexCounter(this);
		regexInterpreter = new RegexInterpreter(this);
		regexInterpreter.interpretAttributesAndContent();
		parseExtractor = new RegexExtractor(this);
	}

	public RegexInterpreter getRegexInterpreter() {
		return regexInterpreter;
	}

	public void addName(String name) {
		fieldNameList.add(name);
	}

	public boolean match(LegacyStore legacyStore) {
		CMLElement currentElement = null;
		counter.setModuleStart(legacyStore.getPointer());
		counter.setModuleEnd(legacyStore.getPointer());
		LOG.debug("Regex.Starting Match loop");
		// eat elements against current regex until limit reached
		while ((currentElement = legacyStore.getCurrentLegacyElement()) != null) {
			// fail if no match and no skip set
			counter.markerMatched = matches(currentElement);
			if (counter.markerMatched == counter.markerSkip) {
				LOG.debug("match aborted (matched=skip)="+counter.markerMatched+"; "+legacyStore.getPointer() +
						" with regex ~"+this.getRegexInterpreter().getParserRegexPattern().pattern()+
						"~ ... ~"+currentElement.getValue()+"~");
				counter.setMatched(counter.isWithinCount());
				break;
			}
			counter.markerMatched = true;
			counter.forceIncrementCount();
			LOG.debug(((counter.markerSkip) ? "skipped" : "matched")+ " module "+legacyStore.getPointer()+" with regex ~"+this.getRegexString()+
					"~ / "+counter.markerCount+ "... ~"+currentElement.getValue()+"~ pointer = "+counter.markerCount);
			counter.setModuleEnd(legacyStore.getPointer());  // record for later extraction
			if (!legacyStore.canIncrementPointer()) {		// more elements to match?
				legacyStore.forceIncrementPointer();
				LOG.debug("no more legacy");
				break;
			}
			currentElement = legacyStore.incrementLegacyElement();  // get next legacyElement line
			if (!counter.canIncrementCount()) {
				LOG.debug("Cannot increase this regex count; getting next regex");
				break;  // go on to next regex
			}
			LOG.debug("incrementing legacy count: "+counter.getCount());
		}
		LOG.debug("After regexCounterLoop: "+this.counter);
		return counter.markerMatched;
	}
	
	public boolean matches(CMLElement element) {
		counter.markerMatched = false;
		if (element instanceof CMLScalar) {
			String line = ((CMLScalar)element).getValue();
			counter.markerMatched = this.getParserRegexPattern().matcher(line).matches();
		}
		return counter.markerMatched;
	}

	public void debug() {
		LOG.debug("<pref> "+templateDictRefPrefix+" <dictRef> "+templateDictRefLocalName);
		if (fieldNameList.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for (String name : fieldNameList.getNames()) {
				sb.append(name+" ");
			}
			LOG.debug("<names> "+sb.toString());
		}
		if (fieldNameList.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for (CaptureGroup captureGroup : captureGroupList.getGroups()) {
				sb.append(captureGroup+" ~~~ ");
			}
			LOG.debug("<capture> "+sb.toString());
		}
		LOG.debug(getRegexString());
	}

	public CaptureGroupList getCaptureGroupList() {
		return captureGroupList;
	}

	public FieldNameList getFieldNameList() {
		return fieldNameList;
	}

	@Override
	protected String createId(String id, int i) {
		return Regex.ID_PREFIX+id.substring(1)+CMLConstants.S_PERIOD+(i);
	}

	public String getString() {
		String s = "regexString ~"+getRegexString()+"~\n";
	       s += "pattern ~"+getParserRegexPattern()+"~\n";
	       s += counter.toString();
	       s += ((captureGroupList == null) ? "" : "captureGroups "+captureGroupList.size())+"\n";
	    return s;
	}

	Pattern getParserRegexPattern() {
		return ((RegexInterpreter)regexInterpreter).getParserRegexPattern();
	}

	public String getTemplateDictRefLocalName() {
		return templateDictRefLocalName;
	}
	
	public void setCaptureGroups(CaptureGroupList captureGroups) {
		this.captureGroupList = captureGroups;
	}

	public void setTemplateDictRefLocalName(String templateDictRefLocalName) {
		this.templateDictRefLocalName = templateDictRefLocalName;
	}

	public void setTemplateDictRefPrefix(String prefix) {
		this.templateDictRefPrefix = prefix;
	}
	
	public String getRegexString() {
		return regexInterpreter.getRegexString();
	}

	public void setRegexString(String regexString) {
		regexInterpreter.setRegexString(regexString);
	}

	CaptureGroupList createCaptureGroupsFromRegexBrackets(String dictRef, String prefix) {
		String regexString = this.getRegexString();
		CaptureGroupList groupList = createCaptureGroupsFromRegexBrackets(
				dictRef, prefix, regexString);
		return groupList;
	}

	static CaptureGroupList createCaptureGroupsFromRegexBrackets(
			String dictRef, String prefix, String regexString) {
		CaptureGroupList groupList = new CaptureGroupList();
		int start = 0;
		while (true) {
			start = ParserUtil.getFirstBalanceableBracketSkippingEscapes(regexString, start);
			if (start == -1) {
				break;
			}
			int end = ParserUtil.getBalancedBracketSkippingEscapes(regexString, start);
			if (end == -1) {
				// unbalanced - could be a later problem
				break;
			}
			// non-capturing group
			if (start >= 2 && regexString.substring(start-2, start).equals(NON_CAPTURE)) {
				// skip (?:...) construct for non-capture group
			} else {
				CaptureGroup captureGroup = new CaptureGroup(regexString.substring(start, end+1), dictRef, prefix);
				groupList.add(captureGroup);
			}
			start = end+1;
		}
		return groupList;
	}

	/** count number of capture groups in complete regex
	 * 
	 * @param regex
	 * @return number of top brackets (except non-capture)
	 */
	CaptureGroupList createCaptureGroupsAndAddNames(List<String> nameList) {
		CaptureGroupList groupList = this.createCaptureGroupsFromRegexBrackets(parentTemplate.getDictRefLocalName(), parentTemplate.getDictRefPrefix());
		if (groupList.size() != nameList.size()) {
			if (this.isSkip()) {
				nameList = groupList.createDummyNameListAndAddNamesAttributeForSkippedRegex(this);
			} else {
				LOG.error("regex: "+this.getString());
				for (String name : nameList) {
					LOG.error("name: "+name);
				}
				for (CaptureGroup group : groupList.getGroups()) {
					LOG.error("group: "+group.captureExpression);
				}
				throw new RuntimeException("captureGroups ("+groupList.size()+") different length from names ("+nameList.size()+")");
			}
		}
		CaptureGroup.createCaptureGroupsFromGroupList(nameList, groupList);
		return groupList;
	}

	protected void tidyModulesAfterMatching(LegacyStore legacyStore) {
		for (int modulePos = counter.getModuleStart(), j = 0; modulePos < counter.getModuleEnd(); modulePos++, j++) {
//				Marker marker = childMarkerSequence.markerList.get(j);
			this.childMarkerSequence.tidyAfterMatching(legacyStore);
		}
	}
	
	public String createDictRef() {
		String dictRef = DictRefAttribute.createValue(templateDictRefPrefix, templateDictRefLocalName);
		return dictRef;
	}

	public String toString() {

		String sp = getIndentForFormatting();
		StringBuilder sb = new StringBuilder();
		sb.append( sp+super.toString()+"\n");
		sb.append( sp+"parentTemplate: "+parentTemplate.hashCode()+"   ");
		sb.append( "tDictRef: "+templateDictRefLocalName+"   ");
		sb.append( "tDictRefPref: "+templateDictRefPrefix+"   ");
		sb.append( "fieldNameList: "+fieldNameList+"\n");
		if (captureGroupList != null && captureGroupList.size() > 0) {
			sb.append( sp+"++++captureGroupList>>>> "+"\n");
			for (CaptureGroup captureGroup : captureGroupList.getGroups()) {
				sb.append( sp+captureGroup+"\n");
			}
			sb.append( sp+"<<<<captureGroupList++++ "+"\n");
		}
		return sb.toString();
	}
	
}

