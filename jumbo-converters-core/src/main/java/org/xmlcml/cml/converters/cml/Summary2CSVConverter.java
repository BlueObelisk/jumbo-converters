package org.xmlcml.cml.converters.cml;

import static org.xmlcml.euclid.EuclidConstants.S_COMMA;
import static org.xmlcml.euclid.EuclidConstants.S_NEWLINE;
import static org.xmlcml.euclid.EuclidConstants.S_QUOT;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import nu.xom.Nodes;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Converter;
import org.xmlcml.cml.converters.Type;

/** converts ML summary to CSV
* may change later
* 
* <list xmlns="http://www.xml-cml.org/schema">
 <header xmlns="">
  <column xpath="//cml:cml/@id"/>
  <column xpath="//cml:scalar[@dictRef='iucr:_symmetry_cell_setting']/."/>
  <column xpath="//cml:scalar[@dictRef='iucr:_chemical_formula_weight']"/>
  ...
  </header>
 <row xmlns="">
  <value>wj0001</value>
  <scalar dictRef="iucr:_symmetry_cell_setting" dataType="xsd:string" xmlns="http://www.xml-cml.org/schema">Triclinic</scalar>
  <scalar dictRef="iucr:_chemical_formula_weight" dataType="xsd:double" errorValue="0.0" xmlns="http://www.xml-cml.org/schema">280.24</scalar>
  ...
  </row>
* */

public class Summary2CSVConverter extends AbstractConverter {

	private static final Logger LOG = Logger
	.getLogger(Summary2CSVConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	}

	public Type getInputType() {
		return Type.XML;
	}

	public Type getOutputType() {
		return Type.CSV;
	}

	/**
	 * converts a CML object to MDL. assumes a single CMLMolecule as descendant
	 * of root
	 * 
	 * @param xml
	 * @return strings
	 */
	public List<String> convertToText(Element xml) {
		List<String> stringList = null;
		if (!xml.getLocalName().equals("list")) {
			return stringList;
		}
		Element header = null;
		Nodes nodes = xml.query("./*[local-name()='header']");
		if (nodes.size() == 1) {
			header = (Element) nodes.get(0);
		}
		if (header == null) {
			return stringList;
		}
		stringList = new ArrayList<String>();
		Nodes columns = header.query("./*");
		int columnCount = columns.size();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < columnCount; i++) {
			sb.append(S_QUOT+((Element)columns.get(i)).getAttributeValue("xpath")+S_QUOT);
			if (i < columnCount-1) {
				sb.append(S_COMMA);
			}
		}
		sb.append(S_NEWLINE);
		Nodes rows = xml.query("./*[local-name()='row']");
		for (int irow = 0; irow < rows.size(); irow++) {
			Element row = (Element) rows.get(irow);
			Nodes cells = row.query("*");
			if (cells.size() != columnCount) {
				throw new RuntimeException("Bad row cell count: "+cells.size()+"; expected "+columnCount);
			}
			for (int i = 0; i < columnCount; i++) {
				sb.append(S_QUOT+((Element)cells.get(i)).getValue()+S_QUOT);
				if (i < columnCount-1) {
					sb.append(S_COMMA);
				}
			}
			sb.append(S_NEWLINE);
		}
		String[] ss = sb.toString().split(S_NEWLINE);
		for (String s : ss) {
			stringList.add(s);
		}
		return stringList;
	}
	
	@Override
	public String getRegistryInputType() {
		return null;
	}
	
	@Override
	public String getRegistryOutputType() {
		return null;
	}
	
	@Override
	public String getRegistryMessage() {
		return "null";
	}


}
