package org.xmlcml.cml.converters.cif.dict;
         
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cif.CIF;
import org.xmlcml.cif.CIFDataBlock;
import org.xmlcml.cif.CIFException;
import org.xmlcml.cif.CIFItem;
import org.xmlcml.cif.CIFLoop;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.cif.CIF2CIFXMLConverter;
import org.xmlcml.cml.converters.cif.CIFConstants;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLDictionary;
import org.xmlcml.cml.element.CMLEntry;

/** converts CML to from CIF
 * 
 * @author pm286
 *
 */
public class CIFDict2CMLConverter extends CIF2CIFXMLConverter {
	
	private static final Logger LOG = Logger
	.getLogger(CIFDict2CMLConverter.class);
	private CMLDictionary dictionary;
	private CMLCml cml;
	
	public Type getInputType() {
		return Type.CIF;
	}
	
	public Type getOutputType() {
		return Type.CML;
	}
	
    /** constructor.
	 *
	 */
	public CIFDict2CMLConverter() {
		super.init();
	}	

	/**
	 * converts a CIF object to CML. returns cml:cml/cml:molecule
	 * 
	 * @param lines
	 */
	public Element convertToXML(List<String> lines) {
		CIF cifxml = (CIF) super.convertToXML(lines);
		detachComments(cifxml);
		cml = new CMLCml();
		dictionary = new CMLDictionary();
		dictionary.addNamespaceDeclaration(CIFConstants.CIFX_PREFIX, CIFConstants.CIFX_NS);
		dictionary.addNamespaceDeclaration(CMLConstants.CMLX_PREFIX, CMLConstants.CMLX_NS);
		cml.appendChild(dictionary);
		expandNameLoops(cifxml);
		processChildNodes(cifxml);
		return cml;
	}

	/**
	 * @param cifxml
	 */
	private void expandNameLoops(CIF cifxml) {
		List<CIFDataBlock> dataBlocks = cifxml.getDataBlockList();
		for (CIFDataBlock dataBlock : dataBlocks) {
			CIFLoop nameLoop = dataBlock.getLoopContainingName("_name");
			if (nameLoop != null) {
				List<String> valueList = nameLoop.getValueList();
				for (String value : valueList) {
					CIFDataBlock newDataBlock = new CIFDataBlock(dataBlock);
					CIFLoop nameLoop1 = newDataBlock.getLoopContainingName("_name");
					nameLoop1.detach();
					try {
						newDataBlock.setId(value);
						CIFItem nameItem = new CIFItem("_name", value);
						newDataBlock.add(nameItem, false);
					} catch (CIFException e) {
						throw new RuntimeException("cannot reset id: ", e);
					}
					cifxml.appendChild(newDataBlock);
				}
				dataBlock.detach();
			}
		}
	}

	/**
	 * @param cifxml
	 */
	private void processChildNodes(CIF cifxml) {
		List<CIFDataBlock> dataBlocks = cifxml.getDataBlockList();
		for (CIFDataBlock dataBlock : dataBlocks) {
			processDataBlock(dataBlock);
		}
	}

	/**
	 * 
	 */
	private void processDataBlock(CIFDataBlock dataBlock) {
		List<CIFItem> items = dataBlock.getItemList();
		CMLEntry entry = new CMLEntry();
		entry.setId(dataBlock.getId());
		for (CIFItem item : items) {
			String name = item.getName();
			String value = item.getValue();
			if ("_dictionary_name".equals(name)) {
			} else if ("_on_this_dictionary".equals(name)) {
			} else if ("_dictionary_version".equals(name)) {
			} else if ("_dictionary_update".equals(name)) {
			} else if ("_dictionary_history".equals(name)) {
			} else if ("_name".equals(name)) {
				Attribute attribute = new Attribute(
						CMLConstants.CMLX_PREFIX+CMLConstants.S_COLON+"name", 
						CMLConstants.CMLX_NS, value);
				entry.addAttribute(attribute);
			} else if ("_category".equals(name)) {
				Attribute attribute = new Attribute(
						CMLConstants.CMLX_PREFIX+CMLConstants.S_COLON+"superclass", 
						CMLConstants.CMLX_NS, value);
				entry.addAttribute(attribute);
			} else if ("_type".equals(name)) {
				String type = ("numb".equals(value)) ? CMLUtil.XSD_FLOAT : CMLUtil.XSD_STRING;
				Attribute attribute = new Attribute(
						CMLConstants.CMLX_PREFIX+CMLConstants.S_COLON+"type", 
						CMLConstants.CMLX_NS, type);
				entry.addAttribute(attribute);
			} else if ("_definition".equals(name)) {
				Attribute attribute = new Attribute(
						CMLConstants.CMLX_PREFIX+CMLConstants.S_COLON+"description", 
						CMLConstants.CMLX_NS, value);
				entry.addAttribute(attribute);
			} else if ("_units".equals(name)) {
				Attribute attribute = new Attribute(
						CMLConstants.CMLX_PREFIX+CMLConstants.S_COLON+"unitsName", 
						CMLConstants.CMLX_NS, value);
				entry.addAttribute(attribute);
			} else if ("_units_detail".equals(name)) {
				Attribute attribute = new Attribute(
						CMLConstants.CMLX_PREFIX+CMLConstants.S_COLON+"unitsDescription", 
						CMLConstants.CMLX_NS, value);
				entry.addAttribute(attribute);
			} else {
				Attribute attribute = new Attribute(CIFConstants.CIFX_PREFIX+CMLConstants.S_COLON+name, CIFConstants.CIFX_NS, item.getValue());
				entry.addAttribute(attribute);
			}
		}
		dictionary.appendChild(entry);
		List<CIFLoop> loops = dataBlock.getLoopList();
		for (CIFLoop loop : loops) {
			List<String> nameList = loop.getNameList();
			if (nameList.contains("_name")) {
			} else if (nameList.contains("_example")) {
			} else if (nameList.contains("_list_link_child")) {
			} else if (nameList.contains("_related_item")) {
			} else if (nameList.contains("_enumeration")) {
				List<String> enumerationValues = loop.getColumnValues("_enumeration");
				for (String enumerationValue : enumerationValues) {
					Element enumeration = new Element("enumeration", CMLConstants.CMLX_NS);
					enumeration.appendChild(enumerationValue);
					entry.appendChild(enumeration);
				}
			} else {
				loop.debug();
			}
		}
		CIFLoop nameLoop = dataBlock.getLoopContainingName("_name");
		if (nameLoop != null) {
			List<String> valueList = nameLoop.getValueList();
			for (String value : valueList) {
				System.out.println(value);
			}
			System.out.println("------------------");
//			nameLoop.debug();
		}
	}

	/**
	 * @param cifxml
	 */
	private void detachComments(Element cifxml) {
		Nodes comments = cifxml.query(".//comment");
		for (int i = 0; i < comments.size(); i++) {
			comments.get(i).detach();
		}
	}
	
}

