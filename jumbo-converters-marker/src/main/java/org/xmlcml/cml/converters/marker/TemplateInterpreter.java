package org.xmlcml.cml.converters.marker;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import nu.xom.Nodes;

import org.xmlcml.cml.attribute.DictRefAttribute;

public class TemplateInterpreter {

	private Template template;
	private List<String> arrayNames = new ArrayList<String>();
	private List<MarkerTable> markerTableList = new ArrayList<MarkerTable>();

	public TemplateInterpreter(Template template) {
		this.template = template;
		interpretSpecificAttributesAndContent();
	}

	private void interpretSpecificAttributesAndContent() {
		template.tidyEmptyElements = template.getXMLElement().getAttribute(TopTemplateContainer.TIDY_ATT) != null;
		parseDictRefAndSetTemplatePrefixAndLocalName();
	}

	void parseDictRefAndSetTemplatePrefixAndLocalName() {
		String dictRef = template.getXMLElement().getAttributeValue("dictRef");
		if (dictRef == null) {
			throw new RuntimeException("template must have dictRef ");
		}
		String dictRefLocalName = DictRefAttribute.getLocalName(dictRef);
		String dictRefPrefix = DictRefAttribute.getPrefix(dictRef);
		if (dictRefLocalName == null || dictRefPrefix == null) {
			throw new RuntimeException("dictRef must be QName: "+dictRef);
		}
	}

	public List<String> getArrayNames() {
		return arrayNames;
	}

	public List<MarkerTable> getMarkerTableList() {
		return markerTableList;
	}

	// TODO these have got orphaned
	@SuppressWarnings("unused")
	private void createArrayNames() {
		arrayNames = new ArrayList<String>();
		String names = template.getXMLElement().getAttributeValue(TopTemplateContainer.ARRAY_DATA_ATT);
		if (names != null) {
			String[] aNames = names.trim().split("\\s+");
			for (String name : aNames) {
				arrayNames.add(name);
			}
		}
	}

	// TODO these have got orphaned
//	private void createTableNamesAndCheckUniqueness() {
//		String names = template.getXMLElement().getAttributeValue(TopTemplateContainer.TABLE_DATA_ATT);
//		markerTableList = MarkerTable.createMarkerTablesFromTableAttribute(names);
//		for (MarkerTable markerTable : markerTableList) {
//			String title = markerTable.getTitle();
//			List<String> columnNames = markerTable.getColumnNames();
//			this.checkNamesUniqueBetweenTablesAndArrays(columnNames);
//			this.checkNamesUniqueBetweenTables(markerTable, title, columnNames);
//		}
//	}

//	private void checkNamesUniqueBetweenTables(MarkerTable markerTable, String title, List<String> columnNames) {
// 		for (MarkerTable markerTable2 : markerTableList) {
//			if (markerTable.equals(markerTable2)) {
//				continue;
//			}
//			if (markerTable2.getTitle().equals(title)) {
//				throw new RuntimeException("duplicate table title: "+title);
//			}
//			this.checkColumnNamesUnique(columnNames, markerTable2);
//		}
//	}

//	private void checkColumnNamesUnique(List<String> columnNames, MarkerTable markerTable2) {
//		for (String columnName : columnNames) {
//			for (String columnName2 : markerTable2.getColumnNames()) {
//				if (columnName.equals(columnName2)) {
//					throw new RuntimeException("Cannot have same column name in two tables: "+columnName);
//				}
//			}
//		}
//	}

//	private void checkNamesUniqueBetweenTablesAndArrays(List<String> columnNames) {
//		for (String columnName : columnNames) {
//			if (arrayNames.contains(columnName)) {
//				throw new RuntimeException("Cannot have same name in table and array: "+columnName);
//			}
//		}
//	}

	List<Template> createAndProcessTemplates() {
		Nodes templateNodes = template.getXMLElement().query(TopTemplateContainer.TEMPLATE_NAME);
		List<Template> templateList = new ArrayList<Template>();
		if (templateNodes.size() > 0) {
			for (int i = 0; i < templateNodes.size(); i++) {
				Template newTemplate = new Template((Element)templateNodes.get(i), template);
				templateList.add(newTemplate);
			}
		}
		return templateList;
	}
}
