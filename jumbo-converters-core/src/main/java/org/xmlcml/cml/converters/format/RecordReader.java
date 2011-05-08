package org.xmlcml.cml.converters.format;

import java.util.List;

import nu.xom.Element;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.Outputter.OutputLevel;
import org.xmlcml.cml.converters.text.LineContainer;
import org.xmlcml.cml.converters.text.Template;
import org.xmlcml.cml.converters.text.TransformElement;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLList;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.interfacex.HasDataType;
import org.xmlcml.cml.interfacex.HasDictRef;

public class RecordReader extends LineReader {
	private final static Logger LOG = Logger.getLogger(RecordReader.class);

	private static final String RECORD = "__record";
	public static final String RECORD_READER = "recordReader";

	private static final String DOLLAR_NAME = "__name";
	private static final String DOLLAR_VALUE = "__value";

	private static String prefix = "ChangeMe";
	
	public RecordReader(Element recordReaderElement, Template template) {
		super(RECORD_READER, recordReaderElement, template);
		init0();
	}

	/** only used in tests? */
	public RecordReader(List<Field> fieldList) {
		super(fieldList);
		init0();
	}
	
	protected void init0() {
//		this.debug();
	}

	@Override
	public void applyMarkup(LineContainer lineContainer) {
		this.lineContainer = lineContainer;
		CMLElement element = null;
		int originalNodeIndex = lineContainer.getCurrentNodeIndex();
		debugLine("first line", OutputLevel.VERBOSE);
		if (FormatType.NONE.equals(formatType)) {
			lineContainer.increaseCurrentNodeIndex(repeatCount);
		} else {
			element = readRecordList();
			if (element != null) {
				CMLElement.addCMLXAttribute(element, Template.TEMPLATE_REF, this.getId());
				lineContainer.insertChunk(element, originalNodeIndex);
			} else {
				LOG.debug("null chunk");
			}
		}
		debugLine("current line", OutputLevel.VERBOSE);
	}


	private CMLList readRecordList() {
		LOG.trace("Current node in: "+lineContainer.getCurrentNodeIndex()+ " rptCnt: "+repeatCount);
		CMLList list = new CMLList();
		for (int i = 0; i < repeatCount; i++) {
			CMLList list0 = readRecord();
			list0 = (list0 == null || list0.getChildElements().size() == 0) ? null : list0;
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
		Template.removeNamelessScalars(newList);
		Template.removeEmptyLists(newList);
		Template.flattenSingletonLists(newList);
		return newList;
	}
	
	private CMLElement resolveSymbolicVariables(CMLElement newElement) {
		Nodes names = TransformElement.queryUsingNamespaces(newElement, ".//cml:scalar[contains(@dictRef,':"+DOLLAR_NAME+"')]");
		Nodes values = TransformElement.queryUsingNamespaces(newElement, ".//cml:scalar[contains(@dictRef,':"+DOLLAR_VALUE+"')]");
		if (names.size() != values.size()) {
			newElement.debug("name/value");
			throw new RuntimeException("mismatched counts name ("+names.size()+") values ("+values.size()+")");
		}
		for (int i = 0; i < names.size(); i++) {
			CMLScalar name = (CMLScalar) names.get(i);
			CMLScalar value = (CMLScalar) values.get(i);
			value.setDictRef(DictRefAttribute.createValue(prefix, tidyName(name.getXMLContent())));
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
						CMLArray theArray = (CMLArray)list.getChild(i);
						HasDictRef hasDictRef = (HasDictRef) recordList.get(i);
						try {
							((CMLArray)list.getChild(i)).append(hasDictRef);
						} catch (Exception e) {
							String msg = e.getMessage();
							if (msg.contains("cannot delimit")) {
								String[] strings = theArray.getStrings();
								CMLArray newArray = new CMLArray();
								newArray.setDictRef(theArray.getDictRef());
								newArray.setDelimiter("~");
								newArray.setArray(strings);
								newArray.append(hasDictRef);
							} else {
								throw new RuntimeException("Failed to append to array", e);
							}
						}
					}
				}
			}
		}
		return list;
	}

	private void recordSizeOfOriginalArrays(CMLList list, int size) {
		CMLElement.addCMLXAttribute(list, LineContainer.LINE_COUNT, ""+size);
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
		if (regexProcessor != null) {
			List<HasDataType> hasDataTypeList = regexProcessor.readRecord(lineContainer);
			if (hasDataTypeList != null) {
				list = new CMLList();
				for (HasDataType hasDataType : hasDataTypeList) {
					if (hasDataType != null) {
						// this will detach from the module and add to list
						list.appendChild((CMLElement)hasDataType);
					}
				}
			}
			resolveSymbolicVariables();
		}
		return list;
	}
	
	private void resolveSymbolicVariables() {
	}

	public RegexProcessor getRegexProcessor() {
		return regexProcessor;
	}
}
