package org.xmlcml.cml.converters.text;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParentNode;

import org.apache.log4j.Logger;
import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.Outputter;
import org.xmlcml.cml.converters.Outputter.OutputLevel;
import org.xmlcml.cml.element.CMLMatrix;
import org.xmlcml.cml.element.CMLModule;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.element.CMLVector3;
import org.xmlcml.cml.interfacex.HasDictRef;

public class TransformElement implements MarkupApplier {
	private final static Logger LOG = Logger.getLogger(TransformElement.class);
	
	public static final String TAG = "transform";

	private static final String FROM = "from";
	private static final String NAME = "name";
	private static final String OUTPUT = "output";
	private static final String PARENT = "parent";
	private static final String PROCESS = "process";
	private static final String TO = "to";
	private static final String VALUE = "value";

	// process values
	private static final String ADDROLE = "addRole";
	private static final String DATATYPE = "dataType";
	private static final String GROUP = "group";
	private static final String MATRIX33 = "matrix33";
	private static final String NAMEVALUE = "nameValue";
	private static final String PULLUP_SINGLETON = "pullupSingleton";
	private static final String RENAME = "rename";
	private static final String TEMPLATE_REF = "templateRef";
	private static final String VECTOR3 = "vector3";

	
	private Element element;
	private OutputLevel outputLevel;
	private String from;
	private String id;
	private String name;
	private String parentXpath;
	private String process;
	private String to;
	private String value;
	private Element parsedElement;


	public TransformElement(Element element) {
		this.element = element;
//		try {
//			XIncluder.resolveInPlace(element.getDocument());
//		} catch (Exception e) {
//			throw new RuntimeException("Bad XInclude", e);
//		}
//		CMLUtil.removeWhitespaceNodes(this.theElement);
		processChildElementsAndAttributes();
	}

	public String getId() {
		return element.getAttributeValue(ID);
	}
	
	private void processChildElementsAndAttributes() {
		processAttributes();
//		createSubclassedElementsFromChildElements();
//		if (!OutputLevel.NONE.equals(outputLevel)) {
//			this.debug();
//		}
	}

	
	private void processAttributes() {
		Template.checkIfAttributeNamesAreAllowed(element, new String[]{
			FROM, 
			ID, 
			NAME,
			OUTPUT,
			PARENT,
			PROCESS,
			TO,
			VALUE,
		});
				
		from = element.getAttributeValue(FROM);
		id = element.getAttributeValue(ID);
		name = element.getAttributeValue(NAME);
		parentXpath = element.getAttributeValue(PARENT);
		process = element.getAttributeValue(PROCESS);
		if (process == null) {
			throw new RuntimeException("Must give process attribute");
		}
		to = element.getAttributeValue(TO);
		value = element.getAttributeValue(VALUE);
		
		outputLevel = Outputter.extractOutputLevel(this.element);
		LOG.trace(outputLevel+"/"+this.element.getAttributeValue(Outputter.OUTPUT));
		if (!OutputLevel.NONE.equals(outputLevel)) {
			System.out.println("OUTPUT "+outputLevel);
		}
	}

	public void applyMarkup(LineContainer lineContainer) {
		
		parsedElement = lineContainer.getLinesElement();
		if (ADDROLE.equals(process)) {
			addRole();
		} else if (DATATYPE.equals(process)) {
			addDataType();
		} else if (GROUP.equals(process)) {
			makeGroup();
		} else if (MATRIX33.equals(process)) {
			createMatrix33();
		} else if (NAMEVALUE.equals(process)) {
			createNameValue();
		} else if (PULLUP_SINGLETON.equals(process)) {
			pullupSingleton();
		} else if (RENAME.equals(process)) {
			rename();
		} else if (VECTOR3.equals(process)) {
			createVector3();
		}
	}
	
	private void addRole() {
		if (name == null) {
			throw new RuntimeException("Must give name");
		}
		Nodes names = parsedElement.query(name, CMLConstants.CML_XPATH);
		for (int i = 0; i < names.size(); i++) {
			((Element)names.get(i)).addAttribute(new Attribute("role", value));
		}
	}

	private void makeGroup() {
		if (name == null) {
			throw new RuntimeException("Must give name");
		}
		Nodes names = parsedElement.query(name, CMLConstants.CML_XPATH);
		if (names.size() != 0) {
			ParentNode parent = names.get(0).getParent();
			for (int i = 0; i < names.size(); i++) {
				groupFollowingSiblings(names, parent, i);
			}
		}
	}

	private void groupFollowingSiblings(Nodes names, ParentNode parent, int i) {
		Element nameElement = (Element)names.get(i);
		CMLModule module = new CMLModule();
		module.setRole("group");
		int index = parent.indexOf(nameElement);
		parent.insertChild(module, index);
		index++;
		int nextIndex = (i == names.size()-1) ? parent.getChildCount() :
			parent.indexOf((Element)names.get(i+1));
		for (int j = nextIndex - 1; j >= index; j--) {
			Node node = parent.getChild(j);
			if (node == null) {
				break;
			}
			node.detach();
			module.insertChild(node, 0);
		}
	}

	private void pullupSingleton() {
		if (name == null) {
			throw new RuntimeException("Must give name");
		}
		Nodes names = parsedElement.query(name, CMLConstants.CML_XPATH);
		for (int i = 0; i < names.size(); i++) {
			Element nameElement = (Element)names.get(i);
//			CMLUtil.debug(nameElement, "NNNNN");
			Elements childElements = nameElement.getChildElements();
			if (childElements.size() == 1) {
				Element child = childElements.get(0);
				nameElement.getParent().replaceChild(nameElement, child);
				String templateRef = nameElement.getAttributeValue(TEMPLATE_REF);
				if (templateRef != null) {
					child.addAttribute(new Attribute(TEMPLATE_REF, templateRef));
				}
			}
		}
	}

	private void createNameValue() {
		/**
  <list templateRef="list1">
    <list>
      <scalar dataType="xsd:string" dictRef="n:name">hostname</scalar>
      <scalar dataType="xsd:string" dictRef="n:value">arcen</scalar>
    </list>
    ...
  </list>
		 */
		if (parsedElement == null) {
			throw new RuntimeException("Null parsedElement");
		}
		if (parentXpath == null) {
			CMLUtil.debug(this.element, "DBG");
			throw new RuntimeException("Null parentXpath "+this.getId());
		}
		Nodes parents = parsedElement.query(parentXpath, CMLConstants.CML_XPATH);
		for (int i = 0; i < parents.size(); i++) {
			Element parent = (Element)parents.get(i);
			Nodes nameNodes = parent.query(name, CMLConstants.CML_XPATH);
			Nodes valueNodes = parent.query(value, CMLConstants.CML_XPATH);
			if (nameNodes.size() == 1 && valueNodes.size() == 1) {
				createNameValue(nameNodes, valueNodes);
			}
		}
	}

	private void createNameValue(Nodes nameNodes, Nodes valueNodes) {
		Element nameElement = (Element) nameNodes.get(0);
		String nameValue = nameElement.getValue().trim();
		DictRefAttribute nameDictRef = (DictRefAttribute)((HasDictRef) nameElement).getDictRefAttribute(); 
		String namePrefix = nameDictRef.getPrefix();
		((HasDictRef)nameElement).setDictRef(DictRefAttribute.createValue(namePrefix, nameValue));
		Element valueElement = (Element) valueNodes.get(0);
		String valueValue = valueElement.getValue().trim();
		valueElement.detach();
		((CMLScalar)nameElement).setValue(valueValue);
	}

	private void createMatrix33() {
		Nodes parents = parsedElement.query(parentXpath, CMLConstants.CML_XPATH);
		for (int i = 0; i < parents.size(); i++) {
			Element parent = (Element)parents.get(i);
			Nodes scalarNodes = parent.query(name, CMLConstants.CML_XPATH);
			double[] dd = new double[3*3];
			Node node0 = null;
			if (scalarNodes.size() == 3*3) {
				for (i = 0; i < 3*3; i++) {
					CMLScalar scalar = (CMLScalar) scalarNodes.get(i);
					dd[i] = scalar.getDouble();
					if (i == 0) {
						node0 = scalar;
					} else {
						scalar.detach();
					}
				}
				CMLMatrix mat = new CMLMatrix(3, 3, dd);
				node0.getParent().replaceChild(node0, mat);
			} else if (scalarNodes.size() == 0) {
			} else {
				CMLUtil.debug(parent, "PAR");
				throw new RuntimeException("No matrix here");
			}
		}
	}


	private void createVector3() {
		/**
- <list templateRef="vv2">
- <list>
  <scalar dataType="xsd:double" dictRef="n:mo1">1.2</scalar> 
  <scalar dataType="xsd:double" dictRef="n:mo2">-0.061</scalar> 
  <scalar dataType="xsd:double" dictRef="n:mo3">-2.7E-15</scalar> 
  </list>
  </list>
		 */
		Nodes parents = parsedElement.query(parentXpath, CMLConstants.CML_XPATH);
		for (int i = 0; i < parents.size(); i++) {
			Element parent = (Element)parents.get(i);
			Nodes scalarNodes = parent.query(name, CMLConstants.CML_XPATH);
			double[] dd = new double[3];
			Node node0 = null;
			if (scalarNodes.size() == 3) {
				for (i = 0; i < 3; i++) {
					CMLScalar scalar = (CMLScalar) scalarNodes.get(i);
					dd[i] = scalar.getDouble();
					if (i == 0) {
						node0 = scalar;
					} else {
						scalar.detach();
					}
				}
				CMLVector3 v3 = new CMLVector3(dd);
				node0.getParent().replaceChild(node0, v3);
			} else if (scalarNodes.size() == 0) {
			} else {
				CMLUtil.debug(parent, "PAR");
				throw new RuntimeException("No vector3 here");
			}
		}
	}
	
	private void rename() {
	}

	private void addDataType() {
	}

	public void debug() {
	}
}
