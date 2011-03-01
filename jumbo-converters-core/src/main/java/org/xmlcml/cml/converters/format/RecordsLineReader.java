package org.xmlcml.cml.converters.format;

import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.Outputter.OutputLevel;
import org.xmlcml.cml.converters.text.LineContainer;
import org.xmlcml.cml.converters.text.Template;
import org.xmlcml.cml.converters.util.JumboReader;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLList;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.interfacex.HasDataType;
import org.xmlcml.cml.interfacex.HasDictRef;

public class RecordsLineReader extends LineReader {
	private final static Logger LOG = Logger.getLogger(RecordsLineReader.class);

	private static final String RECORD = "__record";
	public static final String RECORD_READER = "recordReader";

	private static final String DOLLAR_NAME = "__name";
	private static final String DOLLAR_VALUE = "__value";

	public RecordsLineReader(Element childElement, Template template) {
		super(RECORD_READER, childElement, template);
		init0();
	}

	public RecordsLineReader(List<Field> fieldList) {
		super(fieldList);
		init0();
	}
	
	protected void init0() {
//		this.debug();
	}

	@Override
	public Element apply(LineContainer lineContainer) {
		this.lineContainer = lineContainer;
		CMLElement element = null;
		debugLine("first line", OutputLevel.VERBOSE);
		Integer linesToRead = this.getLinesToRead();
		if (linesToRead != null) {
			element = readRecordList(linesToRead);
		} else {
			readRecordFromLineContainer();
		}
		debugLine("current line", OutputLevel.VERBOSE);
		return element;
	}

	@Override
	public CMLElement readLinesAndParse(JumboReader jumboReader) {
		this.jumboReader = jumboReader;
		CMLElement element = null;
		debugLine("first line", OutputLevel.VERBOSE);
		Integer linesToRead = this.getLinesToRead();
		if (linesToRead != null) {
			element = readRecordList(linesToRead);
			jumboReader.appendChild(element);
			element = jumboReader.getParentElement(); // ?
		} else {
			readRecord();
		}
		debugLine("current line", OutputLevel.VERBOSE);
		return element;
	}

	private CMLList readRecordList(Integer linesToRead) {
		CMLList list = new CMLList();
		for (int i = 0; i < linesToRead; i++) {
			CMLList list0 = readRecord();
			if (list0 == null) {
				LOG.trace("failed to read line");
				break;
			}
			((CMLElement)list).appendChild(list0);
		}
		resolveSymbolicVariables(list);
		list = tidyRecords((CMLList)list);
		return list;
	}

	private CMLList tidyRecords(CMLList list) {
		CMLList newList = list;
		newList = transformRecordsIntoArrays(newList);
		newList = flattenSingletonLists(newList);
		return newList;
	}

	private CMLList flattenSingletonLists(CMLList list) {
		Nodes singletonLists = list.query(".//*[local-name()='list' and count(*)=1 and count(*[local-name()='scalar' or local-name()='array'])=1 and count(*[@dictRef])=1]");
		for (int i = 0; i < singletonLists.size(); i++) {
			CMLList singletonList = (CMLList) singletonLists.get(i);
			CMLElement scalarOrArray = (CMLElement) singletonList.getChildElements().get(0);
			scalarOrArray.detach();
			singletonList.detach();
			list.appendChild(scalarOrArray);
		}
		return list;
	}

	private CMLElement resolveSymbolicVariables(CMLElement newElement) {
		Nodes names = newElement.query(".//*[local-name()='scalar' and contains(@dictRef,':"+DOLLAR_NAME+"')]");
		Nodes values = newElement.query(".//*[local-name()='scalar' and contains(@dictRef,':"+DOLLAR_VALUE+"')]");
		if (names.size() != values.size()) {
			newElement.debug("name/value");
			throw new RuntimeException("mismatched counts name ("+names.size()+") values ("+values.size()+")");
		}
		for (int i = 0; i < names.size(); i++) {
			CMLScalar name = (CMLScalar) names.get(i);
			CMLScalar value = (CMLScalar) values.get(i);
			value.setDictRef(DictRefAttribute.createValue(jumboReader.getDictionaryPrefix(), tidyName(name.getXMLContent())));
			value.setXMLContent(value.getXMLContent().trim());
			name.detach();
		}
		return newElement;
	}

	private String tidyName(String name) {
		return name.trim().replaceAll("[^A-za-z0-9\\.\\-_]", "_");
	}

	private CMLList transformRecordsIntoArrays(CMLList list) {
		if (new Boolean(makeArray)) {
			List<CMLElement> childElements = list.getChildCMLElements();
			if (childElements.size() > 0) {
				list = makeLists(childElements.get(0));
				int size = list.getChildCMLElements().size();
				recordSizeOfOriginalArrays(list, childElements.size());
				for (CMLElement childElement : childElements) {
					List<CMLElement> recordList = childElement.getChildCMLElements();
					// not sure what this did
					if (size != recordList.size()) {
						list.debug("LIST");
						throw new RuntimeException("size of childElements ("+size+") != recordList.size() ("+recordList.size()+")");
					}
					for (int i = 0; i < size; i++) {
						HasDictRef hasDictRef = (HasDictRef) recordList.get(i);
						((CMLArray)list.getChild(i)).append(hasDictRef);
					}
				}
			}
		}
		return list;
	}

	private void recordSizeOfOriginalArrays(CMLList list, int size) {
		list.addAttribute(new Attribute(LineContainer.LINE_COUNT, ""+size));
	}

	@SuppressWarnings("unused")
	private CMLList makeLists(CMLElement element) {
		CMLList arrayList = new CMLList();
		List<CMLElement> fieldList = element.getChildCMLElements();
		for (CMLElement fieldElement : fieldList) {
			String dataType = ((HasDataType)fieldElement).getDataType();
			CMLArray array = CMLArray.createArrayWithAttributes((HasDataType)fieldElement);
			arrayList.appendChild(array);
		}
		return arrayList;
	}

	/**
	 * this adds records to jumboReader
	 * needs refactoring
	 * @return
	 */
	private CMLList readRecord() {
		CMLList list = null;
		// this adds parsed lines to parent element in jumboReader
		LOG.trace("Reading "+peekLine());
		List<HasDataType> hasDataTypeList = parseInlineHasDataTypes();
		if (hasDataTypeList != null) {
			list = new CMLList();
			for (HasDataType hasDataType : hasDataTypeList) {
				if (hasDataType != null) {
					addElementWithDictRef((CMLElement)hasDataType, DictRefAttribute.getLocalName(((HasDictRef)hasDataType).getDictRef()));
					// this will detach from the module and add to list
					list.appendChild((CMLElement)hasDataType);
				}
			}
			addElementWithDictRef(list, RECORD);
		}
		resolveSymbolicVariables();
		return list;
	}

	protected void addElementWithDictRef(CMLElement element, String name) {
		if (jumboReader != null) {
			jumboReader.addElementWithDictRef(element, name);
		}
	}
	
	private void resolveSymbolicVariables() {
		if (jumboReader != null) {
			jumboReader.getParentElement();
		}
	}

	/**
	 * this adds records to jumboReader
	 * needs refactoring
	 * @return
	 */
	private CMLList readRecordFromLineContainer() {
		CMLList list = null;
		List<HasDataType> hasDataTypeList = parseInlineHasDataTypes(lineContainer);
		if (hasDataTypeList != null) {
			list = new CMLList();
			for (HasDataType hasDataType : hasDataTypeList) {
				if (hasDataType != null) {
					jumboReader.addElementWithDictRef((CMLElement)hasDataType, DictRefAttribute.getLocalName(((HasDictRef)hasDataType).getDictRef()));
					// this will detach from the module and add to list
					list.appendChild((CMLElement)hasDataType);
				}
			}
			if (jumboReader != null) {
				jumboReader.addElementWithDictRef(list, RECORD);
			}
		}
		if (jumboReader != null) {
			resolveSymbolicVariables(jumboReader.getParentElement());
		}
		return list;
	}
}
