package org.xmlcml.cml.converters.marker;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLModule;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.element.CMLTable;
import org.xmlcml.cml.interfacex.HasDataType;
import org.xmlcml.cml.interfacex.HasDictRef;
import org.xmlcml.euclid.IntArray;
import org.xmlcml.euclid.RealArray;

public class TableAndArrayCreator {
	private static Logger LOG = Logger.getLogger(TableAndArrayCreator.class);
	
	private Template template;
	private TemplateInterpreter templateInterpreter;
	
	public TableAndArrayCreator(Template template) {
		this.template = template;
		templateInterpreter = template.templateInterpreter;
	}
	
	void processArraysAndTables(LegacyStore legacyStore) {
		CMLModule module = legacyStore.getModule();
		if (module.getChildElements().size() == 0) {
			LOG.debug("No child elements in module");
			return;
		}
		// user-requested array concatenation
		createCMLArrays(module);
		createCMLTables(module);
		//tidy only if flagged and have created arrays and/or tables
		if (template.tidyEmptyElements && 
				templateInterpreter.getArrayNames().size() + 
				templateInterpreter.getMarkerTableList().size() > 0) {
			while (countEmptyElementsAndRemove(module) != 0) {
				;
			}
		}
	}

	private void createCMLArrays(CMLModule module) {
		for (String arrayName : templateInterpreter.getArrayNames()) {
			CMLArray array = this.createRawArray(template, arrayName, module);
			module.appendChild(array);
		}
	}

	private void createCMLTables(CMLModule module) {
		for (MarkerTable markerTable : templateInterpreter.getMarkerTableList()) {
			CMLTable table = new CMLTable();
			table.setTableType(CMLTable.TableType.COLUMN_BASED);
			table.setTitle(markerTable.getTitle());
			for (String columnName : markerTable.getColumnNames()) {
				CMLArray columnArray = this.createRawArray(template, columnName, module);
				table.addArray(columnArray);
			}
			module.appendChild(table);
		}
	}

	private CMLArray createRawArray(Template template, String arrayName, CMLModule module) {
		String qName = DictRefAttribute.createValue(
				template.getDictRefPrefix(), template.getDictRefLocalName()+CMLConstants.S_PERIOD+arrayName);
		// could be arrays or scalars
		String xPath = ".//*[@dictRef='"+qName+"']";
		Nodes hasDictRefs = module.query(xPath, CMLConstants.CML_XPATH);
		if (hasDictRefs.size() == 0) {
			throw new RuntimeException("Cannot find array/columnName: "+arrayName+" in "+qName);
		}
		// crude - convert to strings and then reconvert later
		List<String> values = new ArrayList<String>();
		String dataType = this.concatenateScalarsAndArrayValuesIntoStringList(hasDictRefs,
				values);
		CMLArray array = null;
		String[] vv = values.toArray(new String[0]);
		if (CMLConstants.XSD_DOUBLE.equals(dataType)) {
			RealArray ra = new RealArray(vv);
			array = new CMLArray(ra);
		} else if (CMLConstants.XSD_INTEGER.equals(dataType)) {
			IntArray ia = new IntArray(vv);
			array = new CMLArray(ia.getArray());
		} else {
			array = new CMLArray(vv);
		}
		array.setDictRef(qName);
		return array;
	}

	int countEmptyElementsAndRemove(CMLModule module) {
		// nodes with no text or element children
		Nodes deletableNodes = module.query(
				".//*[(local-name()='"+CMLScalar.TAG+"' " +
						"or local-name()='"+CMLArray.TAG+"'" +
						" or local-name()='"+CMLModule.TAG+"')" +
						" and .='' and count(*)=0 ]");
		int count = deletableNodes.size();
		for (int i = 0; i < count; i++) {
			deletableNodes.get(i).detach();
		}
		return count;
	}

	String concatenateScalarsAndArrayValuesIntoStringList(
			Nodes hasDictRefs, List<String> values) {
		String dataType = null;
		for (int i = 0; i < hasDictRefs.size(); i++) {
			HasDictRef hasDictRef = (HasDictRef) hasDictRefs.get(i);
			dataType = ((HasDataType)hasDictRef).getDataType();
			String value = ((HasDataType)hasDictRef).getXMLContent().trim();
			String[] vv = value.split("\\s+");
			for (String v : vv) {
				values.add(""+v);
			}
			((Element)hasDictRef).detach();
		}
		return dataType;
	}
}
