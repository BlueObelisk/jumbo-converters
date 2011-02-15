package org.xmlcml.cml.converters.marker;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;
import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLScalar;

public class RegexExtractor extends ParseExtractor {
	private static Logger LOG = Logger.getLogger(RegexExtractor.class);
	private Regex regex;
	
	public RegexExtractor(Marker marker) {
		super(marker);
		regex = (Regex)marker;
	}
	
	private CMLScalar createScalarWithoutCaptureGroup() {
		CMLScalar scalar = new CMLScalar();
		String dictRef = marker.getAttributeValue(TopTemplateContainer.DICT_REF_ATT);
		if (dictRef == null) {
			dictRef = DictRefAttribute.createValue(regex.templateDictRefPrefix, regex.templateDictRefLocalName+".unk");
		} else {
			String dictRefLocalName = DictRefAttribute.getLocalName(dictRef);
			scalar.setDictRef(DictRefAttribute.createValue(regex.templateDictRefPrefix, regex.templateDictRefLocalName+"."+dictRefLocalName));
		}
		return scalar;
	}
	
	@Override
	protected List<CMLElement> getExtractedElementList(CMLElement legacyElement) {
		List<CMLElement> extractedElementList = new ArrayList<CMLElement>();
		if (legacyElement instanceof CMLScalar && !marker.isSkip()) {
			String legacyLine = ((CMLScalar)legacyElement).getValue();
			Matcher matcher = regex.getParserRegexPattern().matcher(legacyLine);
			if (!matcher.matches()) {
				throw new RuntimeException("Match failure - BUG? it matched earlier ("+regex.getParserRegexPattern()+" != "+legacyLine);
			}
			int groupCount = matcher.groupCount();
			// start at 1 because 0 is whole group
			for (int i = 1; i <= groupCount; i++) {
				CaptureGroup captureGroup = regex.captureGroupList.get(i-1);
				String matchValue = matcher.group(i);   
				if (captureGroup.getSubCaptureExpression() != null) {
					// we only want group 0 as this is the whole lot
					matchValue = matcher.group(i);   
				}
				LOG.trace("dataType "+captureGroup.getDataType());
				captureGroup.setMatchedValue(matchValue);
				captureGroup.processCaptureGroupOrSubCaptureGroups(matchValue);
				extractedElementList.add(captureGroup.getCMLElement());
			}
		}
		return extractedElementList;
	}
	private List<CMLElement> processCountExpressions(Regex parserRegex, List<CMLElement> extractedElementList) {
		CaptureGroupList captureGroupList = parserRegex.getCaptureGroupList();
		if (captureGroupList.size() != extractedElementList.size()) {
			throw new RuntimeException("BUG captureGroups should match extracted elements");
		}
		List<CMLElement> elementListNew = new ArrayList<CMLElement>();
		for (int i = 0; i < extractedElementList.size(); i++) {
			CMLElement element = extractedElementList.get(i);
			List<CMLElement> splitElements = captureGroupList.get(i).processCountExpressions(element);
			// TODO is this right???
			for (@SuppressWarnings("unused") CMLElement splitElement : splitElements) {
				elementListNew.add(element);
			}
		}
		return elementListNew;
	}

	@Override
	protected List<CMLElement> processExtractedElements(
			List<CMLElement> extractedElements) {
		if (extractedElements.size() == 0) {
			if (!marker.isSkip()) {
				extractedElements.add(createScalarWithoutCaptureGroup());
			}
		} else {
			extractedElements = processCountExpressions(regex, extractedElements);
		}
		return extractedElements;
	}
}
