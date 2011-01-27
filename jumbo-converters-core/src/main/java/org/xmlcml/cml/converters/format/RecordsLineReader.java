package org.xmlcml.cml.converters.format;

import java.util.List;

import nu.xom.Element;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.base.CMLElement;
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

	private static final String DOLLAR_NAME = "$name";
	private static final String DOLLAR_VALUE = "$value";

	public RecordsLineReader(Element childElement) {
		super(RECORD_READER, childElement);
	}

	public RecordsLineReader(List<Field> fieldList) {
		super(fieldList);
	}

	@Override
	public CMLElement readLinesAndParse(JumboReader jumboReader) {
		this.jumboReader = jumboReader;
		CMLElement element = null;
		this.debug();
		Integer linesToRead = this.getLinesToRead();
		if (linesToRead != null) {
			element = readRecordList(linesToRead);
			jumboReader.appendChild(element);
			element = jumboReader.getParentElement(); // ?
		} else {
			readRecord();
		}
		return element;
	}

	private CMLList readRecordList(Integer linesToRead) {
		CMLList list = new CMLList();
		for (int i = 0; i < linesToRead; i++) {
			CMLList list0 = readRecord();
			if (list0 == null) {
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
			throw new RuntimeException("mismatched name/values");
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
				for (CMLElement childElement : childElements) {
					List<CMLElement> recordList = childElement.getChildCMLElements();
					if (size != recordList.size()) {
						throw new RuntimeException();
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
		List<HasDataType> hasDataTypeList = parseInlineHasDataTypes(jumboReader);
		if (hasDataTypeList != null) {
			list = new CMLList();
			for (HasDataType hasDataType : hasDataTypeList) {
				if (hasDataType != null) {
					jumboReader.addElementWithDictRef((CMLElement)hasDataType, DictRefAttribute.getLocalName(((HasDictRef)hasDataType).getDictRef()));
					// this will detach from the module and add to list
					list.appendChild((CMLElement)hasDataType);
				}
			}
			jumboReader.addElementWithDictRef(list, RECORD);
		}
		resolveSymbolicVariables(jumboReader.getParentElement());
		return list;
	}

}
