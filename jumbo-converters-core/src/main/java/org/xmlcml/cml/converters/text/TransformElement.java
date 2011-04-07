package org.xmlcml.cml.converters.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParentNode;
import nu.xom.Text;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.Outputter;
import org.xmlcml.cml.converters.Outputter.OutputLevel;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLDictionary;
import org.xmlcml.cml.element.CMLEntry;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLMatrix;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLParameter;
import org.xmlcml.cml.element.CMLParameterList;
import org.xmlcml.cml.element.CMLProperty;
import org.xmlcml.cml.element.CMLPropertyList;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.element.CMLVector3;
import org.xmlcml.cml.interfacex.HasDictRef;
import org.xmlcml.cml.tools.MoleculeTool;
import org.xmlcml.euclid.JodaDate;
import org.xmlcml.euclid.Real;
import org.xmlcml.molutil.ChemicalElement;

public class TransformElement implements MarkupApplier {
	private final static Logger LOG = Logger.getLogger(TransformElement.class);
	
	public static final String TAG = "transform";

	private static final String DICT_REF = "dictRef";
	private static final String ROLE = "role";
	private static final String LAST = "LAST";
	private static final String ATOMIC_NUMBER = "atomicNumber";

	private static final String CONVERT = "convert";
	private static final String DATE = "date";
	private static final String DOUBLE = "double";
	private static final String FORMAT = "format";
	private static final String FROM = "from";
	private static final String INTEGER = "integer";
	private static final String MOLECULE = "molecule";
	private static final String MOVE = "move";
	private static final String OUTPUT = "output";
	private static final String PROCESS = "process";
	private static final String TO_XPATH = "toXpath";
	private static final String VALUE = "value";
	private static final String XPATH = "xpath";

	// process values
	private static final String ADD_DICTREF = "addDictRef";
	private static final String ADDPARAMETERLIST = "addParameterList";
	private static final String ADDPROPERTYLIST = "addPropertyList";
	private static final String ADDROLE = "addRole";
	private static final String CHECK_DICTIONARY = "checkDictionary";
	private static final String CREATE_CHILD = "createChild";
	private static final String CREATE_WRAPPER = "createWrapper";
	private static final String DATATYPE = "dataType";
	private static final String DEBUG = "debug";
	private static final String DELETE = "delete";
	private static final String GROUP = "group";
	private static final String MATRIX33 = "matrix33";
	private static final String NAMEVALUE = "nameValue";
	private static final String PULLUP_SINGLETON = "pullupSingleton";
	private static final String RENAME = "rename";
	private static final String VECTOR3 = "vector3";
	private static final String WRAP = "wrap";
	
	private static final String UNIT_SI_URI = "http://www.xml-cml.org/unit/si/";
	private static final String COMPCHEM_URI = "http://www.xml-cml.org/unit/dictionary/compchem";
	private static final String COMPCHEM = "compchem";

	private static final String POSITION = "position";
	private static final String DICTREF = DICT_REF;
	private static final String NAME = "name";
	private static final String ELEMENT_NAME = "elementName";
	private static final String FOLLOW_BEFORE = "followingSiblingsBefore";
	private static final String PREVIOUS_AFTER = "previousSiblingsAfter";


	private Element element;
	private OutputLevel outputLevel;
	private String from;
	private String id;
	private String name;
	private String position;
	private String process;
	private String to;
	private String value;
	private Element parsedElement;

	private String idMethod;
	private String elementMethod;
	private CMLMolecule molecule;

	private List<CMLAtom> atoms;
	private String format;

	private String dictRef;
	private String xpath;
	private String followingSiblingsBefore;
	private String previousSiblingsAfter;
	private String elementName;
	private Template template;


	public TransformElement(Element element, Template template) {
		this.element = element;
		this.template = template;
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
	}

	private void processAttributes() {
		Template.checkIfAttributeNamesAreAllowed(element, new String[]{
			DICTREF, 
			ELEMENT_NAME, 
			FORMAT, 
			FROM, 
			ID, 
			NAME,
			OUTPUT,
			POSITION,
			PROCESS,
			FOLLOW_BEFORE,
			PREVIOUS_AFTER,
			TO_XPATH,
			VALUE,
			XPATH,
		});
				
		dictRef = element.getAttributeValue(DICTREF);
		elementName = element.getAttributeValue(ELEMENT_NAME);
		format = element.getAttributeValue(FORMAT);
		from = element.getAttributeValue(FROM);
		id = element.getAttributeValue(ID);
		name = element.getAttributeValue(NAME);
		position = element.getAttributeValue(POSITION);
		process = element.getAttributeValue(PROCESS);
		if (process == null) {
			throw new RuntimeException("Must give process attribute");
		}
		followingSiblingsBefore = element.getAttributeValue(FOLLOW_BEFORE);
		previousSiblingsAfter = element.getAttributeValue(PREVIOUS_AFTER);
		to = element.getAttributeValue(TO_XPATH);
		value = element.getAttributeValue(VALUE);
		xpath = element.getAttributeValue(XPATH);
		
		outputLevel = Outputter.extractOutputLevel(this.element);
		LOG.trace(outputLevel+"/"+this.element.getAttributeValue(Outputter.OUTPUT));
		if (!OutputLevel.NONE.equals(outputLevel)) {
			LOG.debug("OUTPUT "+outputLevel);
		}
	}

	public void applyMarkup(LineContainer lineContainer) {
		
		parsedElement = lineContainer.getLinesElement();
		if (ADD_DICTREF.equals(process)) {
			addDictRef();
		} else if (ADDROLE.equals(process)) {
			addRole();
		} else if (ADDPARAMETERLIST.equals(process)) {
			addParameterList();
		} else if (ADDPROPERTYLIST.equals(process)) {
			addPropertyList();
		} else if (CHECK_DICTIONARY.equals(process)) {
//			try {
				checkDictionary();
//			} catch (Exception e) {
//				LOG.error("missing id: "+e);
//			}
		} else if (CONVERT.equals(process)) {
			convert();
		} else if (CREATE_CHILD.equals(process)) {
			createChild();
		} else if (CREATE_WRAPPER.equals(process)) {
			createWrapper();
		} else if (DATE.equals(process)) {
			addDate();
		} else if (DEBUG.equals(process)) {
			debugNodes();
		} else if (DELETE.equals(process)) {
			deleteNodes();
		} else if (DOUBLE.equals(process)) {
			addDouble();
		} else if (GROUP.equals(process)) {
			makeGroup();
		} else if (INTEGER.equals(process)) {
			addInteger();
		} else if (process.startsWith(MOLECULE)) {
			createMolecule();
		} else if (MATRIX33.equals(process)) {
			createMatrix33();
		} else if (MOVE.equals(process)) {
			move();
		} else if (NAMEVALUE.equals(process)) {
			createNameValue();
		} else if (PULLUP_SINGLETON.equals(process)) {
			pullupSingleton();
		} else if (RENAME.equals(process)) {
			rename();
		} else if (VECTOR3.equals(process)) {
			createVector3();
		} else if (WRAP.equals(process)) {
			wrapPropertiesAndParameters();
		} else {
			throw new RuntimeException("Unknown process: "+process);
		}
	}
	
	private /*List<Element>*/ void checkDictionary() {
		xpath = "//*[@dictRef]";
		Nodes nodes = getXpathQueryResults();
		Map<String, List<Element>> dictRefNodes = new HashMap<String, List<Element>>();
		for (int i = 0; i < nodes.size(); i++) {
			Node node = nodes.get(i);
			Element element = (Element) node;
			Attribute att = element.getAttribute(DICT_REF);
			String value = att.getValue();
			String prefix = DictRefAttribute.getPrefix(value);
			if (CMLConstants.CMLX_PREFIX.equals(prefix)) {
				continue;
			}
			String templateRef = element.getAttributeValue(Template.TEMPLATE_REF, CMLConstants.CMLX_NS);
			String id = element.getAttributeValue(ID);
//			if ((templateRef == null || templateRef.equals("nullId")) && id == null) {
//				CMLUtil.debug(element, "EEEEE"+prefix);
//				throw new RuntimeException("Null templateRef and id");
////				continue;
//			}
	    	try {
	    		checkAttval(att);
			} catch (Exception e) {
				List<Element> elementList = dictRefNodes.get(value);
				if (elementList == null) {
					elementList = new ArrayList<Element>();
					dictRefNodes.put(value, elementList);
				}
				elementList.add(element);
			}
		}
		for (String s : dictRefNodes.keySet()) {
			System.out.println(">>>>>>>>>>>>>>>>>"+s);
			for (Element element : dictRefNodes.get(s)) {
//				CMLUtil.debug(element, "QQQQQQQQ");
			}
		}
	}

	private void checkAttval(Attribute att) {
		DictRefAttribute dictRefAtt = new DictRefAttribute(att);
		String prefix = dictRefAtt.getPrefix();
		String value = DictRefAttribute.getLocalName(dictRefAtt.getValue());
		String namespace = template.theElement.getNamespaceURI(prefix);
		if (namespace == null) {
			throw new RuntimeException("Cannot discover namespace for: "+prefix);
		}
		boolean found = false;
		for (DictionaryContainer dictionaryContainer : template.getDictionaryContainerList()) {
			CMLDictionary dictionary = dictionaryContainer.getDictionary();
			String dictionaryNamespace = dictionary.getNamespace();
			if (namespace.equals(dictionaryNamespace)) {
				found = true;
				CMLEntry entry = dictionary.getCMLEntry(value);
				if (entry == null) {
					throw new RuntimeException("Cannot find entry ("+value+") in dictionary "+namespace);
				}
			}
		}
		if (!found) {
			throw new RuntimeException("cannot find dictionary namespace: "+namespace);
		}
	}

	private void convert() {
		assertRequired(XPATH, xpath);
		Nodes nodes = getXpathQueryResults();
		if (value != null) {
			for (int i = 0; i < nodes.size(); i++) {
				Node node = nodes.get(i);
				if (node instanceof Attribute) {
					Attribute att = (Attribute) nodes.get(i);
					att.setValue(value);
				} else {
					throw new RuntimeException("Cannot convert: "+node.getClass().getName());
				}
			}
		}
	}

	private void debugNodes() {
		assertRequired(XPATH, xpath);
		Nodes nodes = getXpathQueryResults();
		LOG.debug("Nodes for "+xpath+": "+nodes.size());
		for (int i = 0; i < nodes.size(); i++) {
			debug(nodes.get(i));
		}
	}

	private void debug(Node node) {
		if (node instanceof Text) {
			LOG.debug("text in "+template.getId()+": "+node.getValue());
		} else if (node instanceof Element){
			Element element = (Element) node;
			Nodes texts = element.query("./text()");
			Nodes nodes = element.query("./node()");
			Nodes elems = element.query("./*");
			LOG.debug("text: "+texts.size()+"; node: "+nodes.size()+"; elem: "+elems.size());
			if (texts.size() > 0 && nodes.size()>1) {
				for (int i = 0; i < nodes.size(); i++) {
					Node nodex = nodes.get(i);
					LOG.debug(nodex.getClass().getName()+": "+nodex.getValue());
				}
			}
			CMLUtil.debug(element, xpath+" in "+template.getId());
		}
	}

	private void deleteNodes() {
		assertRequired(XPATH, xpath);
		Nodes nodes = getXpathQueryResults();
		for (int i = 0; i < nodes.size(); i++) {
			nodes.get(i).detach();
		}
	}
	
	private void createChild() {
		assertRequired(ID, id);
		assertRequired(XPATH, xpath);
		assertRequired(ELEMENT_NAME, elementName);
		Nodes parents = getXpathQueryResults();
		Object pos = (position == null) ? LAST : getPosition(position);
		Element templateElement = createNewElement();
		for (int i = 0; i < parents.size(); i++) {
			Element parent = (Element) parents.get(i);
			element = (Element) templateElement.copy();
			if (LAST.equals(pos)) {
				parent.appendChild(element);
			} else if (pos instanceof Integer){
				parent.insertChild(element, (Integer)pos);
			}
		}
	}


	private Element createNewElement() {
		
		String[] names = elementName.split(CMLConstants.S_COLON);
		String prefix = (names.length == 2) ? names[0] : null;
		String local = (names.length == 2) ? names[1] : elementName;
		Element newElement = null;
		try {
			newElement = new Element(local);
		} catch (Exception e) {
			CMLUtil.debug(this.element, "BAD ELEMENT");
			throw new RuntimeException("Cannot create element: "+newElement, e);
		}
		if (prefix == null) {
			newElement = new CMLElement(local);
		} else {
			String namespaceURI = parsedElement.getNamespaceURI(prefix);
			if (namespaceURI == null) {
				throw new RuntimeException("no namespace given for: "+prefix);
			}
			newElement = new Element(prefix, local);
		}
		if (id != null) {
			newElement.addAttribute(new Attribute(ID, id));
		}
		if (dictRef != null) {
			newElement.addAttribute(new Attribute(DICT_REF, dictRef));
		}
		Template.copyNamespaces(this.element, newElement);
		return newElement;
	}

	private void createWrapper() {
		assertRequired(XPATH, xpath);
		assertRequired(ELEMENT_NAME, elementName);
		Element templateElement = createNewElement();
		Nodes nodes = getXpathQueryResults();
		for (int i = 0; i < nodes.size(); i++) {
			Node node = nodes.get(i);
//			System.out.println("****["+node.getValue()+"]****");
			ParentNode parent = node.getParent();
			Element element = (Element) templateElement.copy();
			parent.replaceChild(node, element);
			element.appendChild(node);
		}
	}

	private Nodes getXpathQueryResults() {
		assertRequired(XPATH, xpath);
		return TransformElement.queryUsingNamespaces(parsedElement, xpath);
	}

	private int getPosition(String attVal) {
		try {
			int value = Integer.parseInt(attVal);
			if (value < 0 || value > Integer.MAX_VALUE) {
				throw new RuntimeException(
					"position must be non-negative; found:"+value);
			}
			return value;
		} catch (Exception e) {
			throw new RuntimeException("must give integer for position; found: "+attVal);
		}
	}

	private void assertRequired(String attName, String attVal) {
		if (attVal == null) {
			CMLUtil.debug(this.element, "BAD Transform");
			throw new RuntimeException("Must give "+attName+" attribute");
		}
	}

	private void wrapPropertiesAndParameters() {
		Nodes names = createXpathNodes();
		for (int i = 0; i < names.size(); i++) {
			Element pp = (Element) names.get(i);
			Elements scalars = pp.getChildElements();
			for (int j = 0; j < scalars.size(); j++) {
				Element scalar = scalars.get(j);
				if (scalar instanceof CMLScalar) {
					CMLElement wrapper = (pp instanceof CMLPropertyList) ? new CMLProperty() : new CMLParameter();
					ParentNode parent = scalar.getParent();
					parent.replaceChild(scalar, wrapper);
					((HasDictRef)wrapper).setDictRef(((CMLScalar)scalar).getDictRef());
					wrapper.appendChild(scalar);
				}
			}
		}
	}

	private void addPropertyList() {
		Nodes names = createXpathNodes();
		for (int i = 0; i < names.size(); i++) {
			CMLPropertyList propertyList = new CMLPropertyList();
			propertyList.setId(id);
			((Element)names.get(i)).appendChild(propertyList);
		}
	}

	private void addParameterList() {
		Nodes names = createXpathNodes();
		for (int i = 0; i < names.size(); i++) {
			CMLParameterList parameterList = new CMLParameterList();
			parameterList.setId(id);
			((Element)names.get(i)).appendChild(parameterList);
			
		}
		
	}

	private void addDictRef() {
		assertRequired(XPATH, xpath);
		Nodes names = createXpathNodes();
		for (int i = 0; i < names.size(); i++) {
			((Element)names.get(i)).addAttribute(new Attribute(DICT_REF, value));
		}
	}

	private void move() {
//		assertRequired(NAME, name);
		assertRequired(TO_XPATH, to);
		Nodes nodes = createXpathNodes();
		Nodes toParents = TransformElement.queryUsingNamespaces(parsedElement, to);
		ParentNode toParent = (toParents.size() == 1) ? (ParentNode) toParents.get(0) : null;
		if (toParent != null) {
			for (int i = 0; i < nodes.size(); i++) {
				Node fromNode = nodes.get(i);
				fromNode.detach();
				toParent.appendChild(fromNode);
			}
		}
	}

	private Nodes createXpathNodes() {
		try {
			Nodes xpaths = TransformElement.queryUsingNamespaces(parsedElement, xpath);
			return xpaths;
		} catch (Exception e) {
			throw new RuntimeException("error in name Xpath: "+xpath, e);
		}
	}

	private void addRole() {
		assertRequired(XPATH, xpath);
		assertRequired(VALUE, value);
		Nodes nodes = createXpathNodes();
		for (int i = 0; i < nodes.size(); i++) {
			((Element)nodes.get(i)).addAttribute(new Attribute(ROLE, value));
		}
	}

	private void makeGroup() {
		assertRequired(XPATH, xpath);
		if (previousSiblingsAfter == null && followingSiblingsBefore == null) {
			CMLUtil.debug(this.element, "Transform");
			LOG.warn("should give previousSiblingsAfter or followingSiblingsBefore");
		}
		Nodes nodes = createXpathNodes();
		for (int i = 0; i < nodes.size(); i++) {
			groupPreviousSiblings((Element)nodes.get(i));
			groupFollowingSiblings((Element)nodes.get(i));
		}
	}

	private void groupPreviousSiblings(Element element) {
		if (previousSiblingsAfter != null) {
			ParentNode parent = element.getParent();
			Nodes afterNodes = queryUsingNamespaces(element, previousSiblingsAfter);
			int afterIndex = (afterNodes.size() == 0) ? -1 : parent.indexOf(afterNodes.get(0));
			int elementIdx = parent.indexOf(element);
			int minIndex = (afterIndex == -1) ? 0 : afterIndex + 1;
			List<Node> nodeList = new ArrayList<Node>();
			for (int i = minIndex; i < elementIdx; i++) {
				nodeList.add(parent.getChild(i));
			}
			for (int i = nodeList.size()-1; i >= 0; i--) {
				nodeList.get(i).detach();
				element.insertChild(nodeList.get(i), 0);
			}
		}
	}

	private void groupFollowingSiblings(Element element) {
		if (followingSiblingsBefore != null) {
			ParentNode parent = element.getParent();
			Nodes beforeNodes = queryUsingNamespaces(element, followingSiblingsBefore);
			int beforeIndex = (beforeNodes.size() == 0) ? -1 : parent.indexOf(beforeNodes.get(0));
			int elementIdx = parent.indexOf(element);
			int maxIndex = (beforeIndex == -1) ? parent.getChildCount() : beforeIndex;
			List<Node> nodeList = new ArrayList<Node>();
			for (int i = elementIdx+1; i < maxIndex; i++) {
				nodeList.add(parent.getChild(i));
			}
			for (int i = 0; i < nodeList.size(); i++) {
				nodeList.get(i).detach();
				element.appendChild(nodeList.get(i));
			}
		}
	}

	public static Nodes queryUsingNamespaces(Element element, String xpath) {
		Nodes nodes = null;
		try {
			nodes = element.query(xpath, Template.CML_CMLX_CONTEXT);
		} catch (Exception e) {
			throw new RuntimeException("query fails: "+xpath, e);
		}
		return nodes;
	}

	private void pullupSingleton() {
		assertRequired(XPATH, xpath);
		Nodes nodes = createXpathNodes();
		for (int i = 0; i < nodes.size(); i++) {
			Element nameElement = (Element)nodes.get(i);
			Elements childElements = nameElement.getChildElements();
			if (childElements.size() == 1) {
				Element child = childElements.get(0);
				nameElement.getParent().replaceChild(nameElement, child);
				String templateRef = ((CMLElement)nameElement).getCMLXAttribute(Template.TEMPLATE_REF);
				if (templateRef != null) {
					CMLElement.addCMLXAttribute(child, Template.TEMPLATE_REF, templateRef);
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
		assertRequired(NAME, name);
		assertRequired(XPATH, xpath);
		assertRequired(VALUE, value);
		Nodes nodes = createXpathNodes();
		for (int i = 0; i < nodes.size(); i++) {
			Element element = (Element)nodes.get(i);
			Nodes nameNodes = TransformElement.queryUsingNamespaces(element, name);
			Nodes valueNodes = TransformElement.queryUsingNamespaces(element, value);
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
		assertRequired(FROM, from);
		assertRequired(XPATH, xpath);
		Nodes nodes = getXpathQueryResults();
		for (int i = 0; i < nodes.size(); i++) {
			Element element = (Element)nodes.get(i);
			Nodes scalarNodes = TransformElement.queryUsingNamespaces(element, from);
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
				CMLUtil.debug(element, "PAR");
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
		assertRequired(DICTREF, dictRef);
		assertRequired(FROM, from);
		assertRequired(XPATH, xpath);
		String[] dictRefNames = splitDictRef();
		Nodes nodes = getXpathQueryResults();
		for (int i = 0; i < nodes.size(); i++) {
			Element element = (Element)nodes.get(i);
			Nodes scalarNodes = TransformElement.queryUsingNamespaces(element, from);
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
				double length = v3.getLength();
				CMLProperty property = new CMLProperty();
				property.setDictRef(DictRefAttribute.createValue(dictRefNames[0], dictRefNames[1]));
				CMLScalar lengthScalar = new CMLScalar(length);
				lengthScalar.setDictRef("cmlx:length");
				property.appendChild(lengthScalar);
				property.appendChild(v3);
				node0.getParent().replaceChild(node0, property);
			} else if (scalarNodes.size() == 0) {
			} else {
				CMLUtil.debug(element, "PAR");
				throw new RuntimeException("No vector3 here");
			}
		}
	}

	private String[]  splitDictRef() {
		String[] dictRefNames = dictRef.split(CMLConstants.S_COLON);
		if (dictRefNames.length != 2) {
			throw new RuntimeException("dictRef must be qualified name; found: "+dictRef);
		}
		return dictRefNames;
	}
	
	private void createMolecule() {
		assertRequired(XPATH, xpath);
		assertRequired(ID, id);
		createMethodNames();
		Nodes arrays = createXpathNodes();
		if (arrays.size() > 0) {
			Element element = (Element) arrays.get(0);
			if (!(element instanceof CMLArray)) {
				throw new RuntimeException(
						"Molecule requires list of arrays, but found: "+element.getClass().getName());
			}
			CMLArray array0 = (CMLArray) element;
			int natoms = array0.getSize();
			molecule = new CMLMolecule();
			molecule.setId(id);
			ParentNode parent = array0.getParent();
			parent.replaceChild(array0, molecule);
			createAndAddAtoms(natoms);
			atoms = molecule.getAtoms();
			addArrays(arrays);
			addElementTypes();
			addFormulaAndProperties();
		}
	}

	private void createAndAddAtoms(int natoms) {
		for (int iatom = 0; iatom < natoms; iatom++) {
			id = "a"+(iatom+1);
			CMLAtom atom = new CMLAtom(id);
			molecule.addAtom(atom);
		}
	}

	private void addArrays(Nodes arrays) {
		for (int i = 0; i < arrays.size(); i++) {
			Node node = arrays.get(i);
			if (!(node instanceof CMLArray)) {
				CMLUtil.debug((Element) node, "ELEM:"+from);
				throw new RuntimeException("molecule only operates on arrays");
			}
			CMLArray array = (CMLArray) arrays.get(i);
			processDictRef(array);
			array.detach();
		}
	}

	private void addElementTypes() {
		for (CMLAtom atom : atoms) {
			String elementType = atom.getElementType();
			if (elementType != null) {
				ChemicalElement chemicalElement = ChemicalElement.getChemicalElement(elementType);
				CMLScalar scalar = new CMLScalar(chemicalElement.getAtomicNumber());
				scalar.setDictRef(DictRefAttribute.createValue(COMPCHEM, ATOMIC_NUMBER));
				atom.addScalar(scalar);
			}
		}
	}

	private void addFormulaAndProperties() {
		CMLFormula formula = CMLFormula.createFormula(molecule);
		molecule.addFormula(formula);
		MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
		moleculeTool.calculateBondedAtoms();
		moleculeTool.adjustBondOrdersToValency();
		double mwt = moleculeTool.getCalculatedMolecularMass();
		CMLScalar scmwt = new CMLScalar(mwt);
		scmwt.setUnits("unit", "dalton", UNIT_SI_URI);
		CMLProperty mmprop = new CMLProperty(); 
		mmprop.setDictRef("cml:molmass");
		mmprop.appendChild(scmwt);
		molecule.appendChild(mmprop);
	}

	private void processDictRef(CMLArray array) {
		int size = array.getSize();
		DictRefAttribute dictRefAttribute = (DictRefAttribute) array.getDictRefAttribute();
		if (dictRefAttribute == null) {
			throw new RuntimeException("Array must have dictRef");
		}
		String localValue = DictRefAttribute.getLocalValue(array);
		if (
			localValue.equals("x3") ||
			localValue.equals("y3") ||
			localValue.equals("z3") ||
			localValue.equals("id") ||
			localValue.equals("elementType") ||
			localValue.equals("label") ||
			localValue.equals("atomTypeRef")
			) {
			addScalar(array, localValue);
		} else {
			addCMLAttribute(array, localValue);
		}
	}

	private void addScalar(CMLArray array, String localName) {
		String[] values = array.getStrings();
		double[] doubles = array.getDoubles();
		int[] ints = array.getInts();
		for (int i = 0; i < atoms.size(); i++) {
			CMLAtom atom = atoms.get(i);
			if (localName.equals("x3")) {
				atom.setX3(doubles[i]);
			} else if (localName.equals("y3")) {
				atom.setY3(doubles[i]);
			} else if (localName.equals("z3")) {
				atom.setZ3(doubles[i]);
			} else if (localName.equals("id")) {
				addId(array, values, ints, i, atom);
			} else if (localName.equals("elementType")) {
				addElementType(array, values, doubles, ints, i, atom);
			}
		}
	}

	private void addId(CMLArray array, String[] values, int[] ints, int i,
			CMLAtom atom) {
		String id = null;
		if (array.getDataType().equals(CMLConstants.XSD_STRING)) {
			id = values[i];
		} else if (array.getDataType().equals(CMLConstants.XSD_INTEGER)){
			id = "a"+ints[i];
		}
		if (id == null) {
			throw new RuntimeException("Cannot make id");
		}
		atom.resetId(id);
	}

	private void addElementType(CMLArray array, String[] values,
			double[] doubles, int[] ints, int i, CMLAtom atom) {
		String elem = null;
		ChemicalElement chemicalElement = null;
		if (array.getDataType().equals(CMLConstants.XSD_STRING)) {
			elem = values[i];
			if (elem.length() > 2) {
				elem = elem.substring(0,2);
			}
			if (elem.length() == 2) {
				if (Character.isLetter(elem.charAt(1))) {
					elem = elem.substring(0, 1)+elem.substring(1,2).toLowerCase();
				} else {
					elem = elem.substring(0, 1);
				}
			}
			chemicalElement = ChemicalElement.getChemicalElement(elem);
			if (chemicalElement == null) {
				throw new RuntimeException("Unknown element: "+values[i]);
			}
		} else if (array.getDataType().equals(CMLConstants.XSD_INTEGER)){
			chemicalElement = ChemicalElement.getElement(ints[i]);
		} else if (array.getDataType().equals(CMLConstants.XSD_DOUBLE)){
			chemicalElement = ChemicalElement.getElement((int)doubles[i]);
		}
		if (chemicalElement == null) {
			throw new RuntimeException("Cannot make chemical element");
		}
		atom.setElementType(chemicalElement.getSymbol());
	}

	private void addCMLAttribute(CMLArray array, String localName) {
		String[] values = array.getStrings();
		double[] doubles = array.getDoubles();
		int[] ints = array.getInts();
		String dictRef = array.getDictRef();
		// we need a getScalar method
		for (int i = 0; i < array.getSize(); i++) {
			CMLScalar scalar = null;
			String dataType = array.getDataType();
			if (dataType.equals(CMLConstants.XSD_STRING)) {
				scalar = new CMLScalar(values[i]);
			} else if (dataType.equals(CMLConstants.XSD_DOUBLE)) {
				scalar = new CMLScalar(doubles[i]);
			} else if (dataType.equals(CMLConstants.XSD_INTEGER)) {
				scalar = new CMLScalar(ints[i]);
			}
			scalar.setDictRef(dictRef);
			atoms.get(i).addScalar(scalar);
		}
	}

	private void createMethodNames() {
		String argx = process.substring(MOLECULE.length()).trim();
		String[] args = argx.split(" ");
		idMethod = null;
		elementMethod = null;
		for (String arg : args) {
			if (arg.trim().length() > 0) {
				String[] atts = arg.split("=");
				if (atts.length != 2) {
					throw new RuntimeException("Cannot parse: "+argx);
				}
				String name = atts[0];
				String value = atts[1];
				if (name.equals("id")) {
					idMethod = value;
				} else if (name.equals("elementType")) {
					elementMethod = value;
				} else {
					throw new RuntimeException("Unknown methodType: "+name);
				}
			}
		}
	}
	
	private void rename() {
		throw new RuntimeException("rename NYI");
	}

	private void addDate() {
		assertRequired(XPATH, xpath);
		Nodes nodes = createXpathNodes();
		for (int i = 0; i < nodes.size(); i++) {
			Node node = nodes.get(i);
			if (node instanceof CMLScalar) {
				CMLScalar scalar = (CMLScalar) node;
				String val = scalar.getValue();
				try {
					DateTime dateTime = null;
					if (format != null) {
						dateTime = JodaDate.parseDate(val, format);
					} else {
						dateTime = JodaDate.parseDate(val);
					}
					scalar.setValue(dateTime);
				} catch (Exception e) {
					LOG.error("Cannot parse/set date: "+val+ " (format='"+format+"'); "+e);
				}
			}
		}
	}

	private void addInteger() {
		assertRequired(XPATH, xpath);
		Nodes nodes = createXpathNodes();
		for (int i = 0; i < nodes.size(); i++) {
			Node node = nodes.get(i);
			if (node instanceof CMLScalar) {
				CMLScalar scalar = (CMLScalar) node;
				String val = scalar.getValue();
				try {
					Integer ii = new Integer(val);
					scalar.setValue(ii);
				} catch (Exception e) {
					LOG.error("bad integer: "+e);
				}
			}
		}
	}

	private void addDouble() {
		assertRequired(XPATH, xpath);
		Nodes nodes = createXpathNodes();
		for (int i = 0; i < nodes.size(); i++) {
			Node node = nodes.get(i);
			if (node instanceof CMLScalar) {
				CMLScalar scalar = (CMLScalar) node;
				String val = scalar.getValue();
				try {
					Double d = Real.parseDouble(val);
					scalar.setValue(d);
				} catch (Exception e) {
					LOG.error("bad double: "+e);
				}
			}
		}
	}

	public void debug() {
//		CMLUtil.debug(this.element, "DEBUG");
	}
}
