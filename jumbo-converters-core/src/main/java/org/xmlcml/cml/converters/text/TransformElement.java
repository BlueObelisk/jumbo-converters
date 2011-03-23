package org.xmlcml.cml.converters.text;

import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParentNode;

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
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLMatrix;
import org.xmlcml.cml.element.CMLModule;
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

	private static final String DATE = "date";
	private static final String DOUBLE = "double";
	private static final String FORMAT = "format";
	private static final String FROM = "from";
	private static final String INTEGER = "integer";
	private static final String MOLECULE = "molecule";
	private static final String MOVE = "move";
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
	private static final String UNIT_SI_URI = "http://www.xml-cml.org/unit/si/";
	private static final String COMPCHEM_URI = "http://www.xml-cml.org/unit/dictionary/compchem";
	private static final String COMPCHEM = "compchem";

	private static final String ADD_DICTREF = "addDictRef";
	private static final String ADDPARAMETERLIST = "addParameterList";
	private static final String ADDPROPERTYLIST = "addPropertyList";
	private static final String WRAP = "wrap";



	
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

	private String idMethod;
	private String elementMethod;
	private CMLMolecule molecule;

	private List<CMLAtom> atoms;
	private String format;


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
			FORMAT, 
			FROM, 
			ID, 
			NAME,
			OUTPUT,
			PARENT,
			PROCESS,
			TO,
			VALUE,
		});
				
		format = element.getAttributeValue(FORMAT);
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
		if (ADD_DICTREF.equals(process)) {
			addDictRef();
		} else if (ADDROLE.equals(process)) {
			addRole();
		} else if (ADDPARAMETERLIST.equals(process)) {
			addParameterList();
		} else if (ADDPROPERTYLIST.equals(process)) {
			addPropertyList();
		} else if (DATE.equals(process)) {
			addDate();
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
	
	private void wrapPropertiesAndParameters() {
		Nodes names = parsedElement.query(name, CMLConstants.CML_XPATH);
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
		Nodes names = parsedElement.query(name, CMLConstants.CML_XPATH);
		for (int i = 0; i < names.size(); i++) {
			CMLPropertyList propertyList = new CMLPropertyList();
			propertyList.setId(id);
			((Element)names.get(i)).appendChild(propertyList);
		}
	}

	private void addParameterList() {
		Nodes names = parsedElement.query(name, CMLConstants.CML_XPATH);
		for (int i = 0; i < names.size(); i++) {
			CMLParameterList parameterList = new CMLParameterList();
			parameterList.setId(id);
			((Element)names.get(i)).appendChild(parameterList);
			
		}
		
	}

	private void addDictRef() {
		if (name == null) {
			throw new RuntimeException("Must give name");
		}
		Nodes names = parsedElement.query(name, CMLConstants.CML_XPATH);
		for (int i = 0; i < names.size(); i++) {
			((Element)names.get(i)).addAttribute(new Attribute("dictRef", value));
		}
	}

	private void move() {
		Nodes names = parsedElement.query(name, CMLConstants.CML_XPATH);
		Nodes toParents = parsedElement.query(to, CMLConstants.CML_XPATH);
		ParentNode toParent = (toParents.size() == 1) ? (ParentNode) toParents.get(0) : null;
		if (toParent != null) {
			for (int i = 0; i < names.size(); i++) {
				Node fromNode = names.get(i);
				fromNode.detach();
				toParent.appendChild(fromNode);
			}
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
				double length = v3.getLength();
				CMLProperty property = new CMLProperty();
				property.setDictRef(DictRefAttribute.createValue(COMPCHEM, "nuclearDipole"));
				CMLScalar lengthScalar = new CMLScalar(length);
				property.appendChild(lengthScalar);
//				property.appendChild(lengthScalar.copy());
				property.appendChild(v3);
				node0.getParent().replaceChild(node0, property);
			} else if (scalarNodes.size() == 0) {
			} else {
				CMLUtil.debug(parent, "PAR");
				throw new RuntimeException("No vector3 here");
			}
		}
	}
	
	private void createMolecule() {
		createMethodNames();
		Nodes arrays = parsedElement.query(name, CMLConstants.CML_XPATH);
		if (arrays.size() > 0) {
			CMLArray array0 = (CMLArray) arrays.get(0);
			int natoms = array0.getSize();
			molecule = new CMLMolecule();
			ParentNode parent = array0.getParent();
			parent.replaceChild(array0, molecule);
			for (int iatom = 0; iatom < natoms; iatom++) {
				id = "a"+(iatom+1);
				CMLAtom atom = new CMLAtom(id);
				molecule.addAtom(atom);
			}
			atoms = molecule.getAtoms();
			for (int i = 0; i < arrays.size(); i++) {
				Node node = arrays.get(i);
				if (!(node instanceof CMLArray)) {
					CMLUtil.debug((Element) node, "ELEM:"+name);
					throw new RuntimeException("molecule only operates on arrays");
				}
				CMLArray array = (CMLArray) arrays.get(i);
				processDictRef(array);
				array.detach();
			}
			for (CMLAtom atom : atoms) {
				String elementType = atom.getElementType();
				if (elementType != null) {
					ChemicalElement chemicalElement = ChemicalElement.getChemicalElement(elementType);
					CMLScalar scalar = new CMLScalar(chemicalElement.getAtomicNumber());
					scalar.setDictRef(DictRefAttribute.createValue(COMPCHEM, "atomicNumber"));
					atom.addScalar(scalar);
				}
			}
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
			molecule.debug("SSSSSSSSSS");
		}
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
		atom.setId(id);
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
		Nodes nodes = parsedElement.query(name, CMLConstants.CML_XPATH);
		for (int i = 0; i < nodes.size(); i++) {
			Node node = nodes.get(i);
			if (node instanceof CMLScalar) {
				CMLScalar scalar = (CMLScalar) node;
				String val = scalar.getValue();
				try {
					DateTime dateTime = null;
					if (format != null) {
						format = "EEE MMM  d HH:mm:ss zzz yyyy";
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
		Nodes nodes = parsedElement.query(name, CMLConstants.CML_XPATH);
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
		Nodes nodes = parsedElement.query(name, CMLConstants.CML_XPATH);
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
	}
}
