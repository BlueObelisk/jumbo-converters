package org.xmlcml.cml.converters.text;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParentNode;
import nu.xom.Text;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
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
import org.xmlcml.cml.element.CMLLabel;
import org.xmlcml.cml.element.CMLLink;
import org.xmlcml.cml.element.CMLList;
import org.xmlcml.cml.element.CMLMap;
import org.xmlcml.cml.element.CMLMatrix;
import org.xmlcml.cml.element.CMLMetadata;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLMolecule.HydrogenControl;
import org.xmlcml.cml.element.CMLParameter;
import org.xmlcml.cml.element.CMLProperty;
import org.xmlcml.cml.element.CMLPropertyList;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.element.CMLVector3;
import org.xmlcml.cml.interfacex.HasDictRef;
import org.xmlcml.cml.interfacex.HasUnits;
import org.xmlcml.cml.tools.MoleculeTool;
import org.xmlcml.euclid.IntArray;
import org.xmlcml.euclid.JodaDate;
import org.xmlcml.euclid.Real;
import org.xmlcml.euclid.RealArray;
import org.xmlcml.euclid.Util;
import org.xmlcml.molutil.ChemicalElement;

public class TransformElement implements MarkupApplier {

	private final static Logger LOG = Logger.getLogger(TransformElement.class);
	
	public static final String TAG = "transform";

	// processList
	private static final String ARG                 = "arg";
	private static final String DESC                = "desc";
	private static final String MANDATORY           = "mandatory";
	private static final String NODE                = "node";

	// attributes
	private static final String DATA_TYPE           = "dataType";
	private static final String DELIMITER           = "delimiter";
	private static final String DICT_REF            = "dictRef";
	private static final String ELEMENT_NAME        = "elementName";
	private static final String FOLLOW_BEFORE       = "followingSiblingsBefore";
	private static final String FORMAT              = "format";
	private static final String FROM                = "from";
	private static final String FROM_POSITION       = "fromPosition";
	private static final String MAP                 = "map";
	private static final String NAME                = "name";
	private static final String OUTPUT              = "output";
	private static final String POSITION            = "position";
	private static final String PROCESS             = "process";
	private static final String PREVIOUS_AFTER      = "previousSiblingsAfter";
	private static final String REGEX               = "regex";
	private static final String REPEAT              = "repeat";
	private static final String SPLITTER            = "splitter";
	private static final String TO_XPATH            = "toXpath";
	private static final String VALUE               = "value";
	private static final String XPATH               = "xpath";

	public static String[] OPTIONS = 
		new String[]{
		DATA_TYPE, 
		DELIMITER, 
		DICT_REF, 
		ELEMENT_NAME, 
		FOLLOW_BEFORE,
		FORMAT, 
		FROM, 
		FROM_POSITION, 
		ID, 
		MAP,
		NAME,
		OUTPUT,
		POSITION,
		PREVIOUS_AFTER,
		REGEX,
		REPEAT,
		SPLITTER,
		TO_XPATH,
		VALUE,
		XPATH,
// because it is always required		
		PROCESS,
	};
	
	// attribute values
	private static final String ATOMIC_NUMBER = "atomicNumber";
	private static final String LAST = "LAST";

	// process values
	private static final String ADD_ATTRIBUTE          = "addAttribute";
	private static final String ADD_CHILD              = "addChild";
	private static final String ADD_DICTREF            = "addDictRef";
	private static final String ADD_LABEL              = "addLabel";
	private static final String ADD_LINK               = "addLink";
	private static final String ADD_MAP                = "addMap";
	private static final String ADD_NAMESPACE          = "addNamespace";
	private static final String ADD_UNITS               = "addUnits";
	private static final String CHECK_DICTIONARY       = "checkDictionary";
	private static final String CREATE_ARRAY           = "createArray";
	private static final String CREATE_DATE            = "createDate";
	private static final String CREATE_DOUBLE          = "createDouble";
	private static final String CREATE_FORMULA         = "createFormula";
	private static final String CREATE_GROUP           = "group";
	private static final String CREATE_INTEGER         = "createInteger";
	private static final String CREATE_MATRIX33        = "createMatrix33";
	private static final String CREATE_MOLECULE        = "createMolecule";
	private static final String CREATE_NAMEVALUE       = "createNameValue";
	private static final String CREATE_STRING          = "createString";
	private static final String CREATE_VECTOR3         = "createVector3";
	private static final String CREATE_WRAPPER         = "createWrapper";
	private static final String CREATE_WRAPPER_METADATA= "createWrapperMetadata";
	private static final String CREATE_WRAPPER_PARAMETER= "createWrapperParameter";
	private static final String CREATE_WRAPPER_PROPERTY= "createWrapperProperty";
	private static final String CREATE_ZMATRIX         = "createZMatrix";
	private static final String DEBUG_NODES            = "debugNodes";
	private static final String DELETE                 = "delete";
	private static final String JOIN_ARRAYS            = "joinArrays";
	private static final String MOVE                   = "move";
	private static final String PULLUP                 = "pullup";
	private static final String PULLUP_SINGLETON       = "pullupSingleton";
	private static final String RENAME                 = "rename";
	private static final String SET_VALUE              = "setValue";
	private static final String SPLIT                  = "split";
	private static final String WRAP                   = "wrap";
	private static final String HELP                   = "help";
	
	private static final String UNIT_SI_URI = "http://www.xml-cml.org/unit/si/";
	private static final String COMPCHEM_URI = "http://www.xml-cml.org/unit/dictionary/compchem";
	private static final String COMPCHEM = "compchem";

	private static final String PROCESS_LIST_URI = 
		"org/xmlcml/cml/converters/text/processList.xml";

	private static final String DEFAULT_DELIMITER = CMLConstants.S_PIPE;

	private static final String STRING = "string(";
	private static final String NUMBER = "number(";

	private static final String UNITS = "units";

	private static final String DHMS = "DHMS";

	private static String[] PROCESS_NAMES = new String[] {
		ADD_ATTRIBUTE,
		ADD_CHILD,
		ADD_DICTREF,
		ADD_LABEL,
		ADD_LINK,
		ADD_MAP,
		ADD_NAMESPACE,
		ADD_UNITS,
		CHECK_DICTIONARY,
		CREATE_ARRAY,
		CREATE_DATE,
		CREATE_DOUBLE,
		CREATE_GROUP,
		CREATE_INTEGER,
		CREATE_MOLECULE,
		CREATE_MATRIX33,
		CREATE_NAMEVALUE,
		CREATE_STRING,
		CREATE_VECTOR3,
		CREATE_WRAPPER,
		CREATE_WRAPPER_PARAMETER,
		CREATE_WRAPPER_PROPERTY,
		CREATE_ZMATRIX,
		DEBUG_NODES,
		DELETE,
		HELP,
		JOIN_ARRAYS,
		MOVE,
		PULLUP_SINGLETON,
		RENAME,
		SET_VALUE,
		SPLIT,
		WRAP,
	};
	
	
	private Template template;
	private Element processListElement;
	private Element transformElement;
	private OutputLevel outputLevel;
	private Element parsedElement;

	private CMLMolecule molecule;
	private List<CMLAtom> atoms;

	private String idMethod;
	private String elementMethod;
	
	private String delimiter;
	private String dataType;
	private String dictRef;
	private String elementName;
	private String followingSiblingsBefore;
	private String format;
	private String from;
	private String fromPosition;
	private String id;
	private String map;
	private String name;
	private String position;
	private String previousSiblingsAfter;
	private String process;
	private String regex;
	private String repeat;
	private String splitter;
	private String to;
	private String value;
	private String xpath;
	

	public TransformElement(Element element, Template template) {
		this.transformElement = element;
		this.template = template;
		processChildElementsAndAttributes();
	}

	public String getId() {
		return transformElement.getAttributeValue(ID);
	}
	
	private void processChildElementsAndAttributes() {
		processAttributes();
	}

	private void processAttributes() {
		Template.checkIfAttributeNamesAreAllowed(transformElement, OPTIONS);
				
		process = transformElement.getAttributeValue(PROCESS);
		if (process == null) {
			throw new RuntimeException("Must give process attribute");
		}
		
		dataType               = addAndIndexAttribute(DATA_TYPE);
		delimiter              = addAndIndexAttribute(DELIMITER);
		dictRef                = addAndIndexAttribute(DICT_REF);
		elementName            = addAndIndexAttribute(ELEMENT_NAME);
		followingSiblingsBefore= addAndIndexAttribute(FOLLOW_BEFORE);
		format                 = addAndIndexAttribute(FORMAT);
		from                   = addAndIndexAttribute(FROM);
		fromPosition           = addAndIndexAttribute(FROM_POSITION);
		id                     = addAndIndexAttribute(ID);
		map                    = addAndIndexAttribute(MAP);
		name                   = addAndIndexAttribute(NAME);
		position               = addAndIndexAttribute(POSITION);
		previousSiblingsAfter  = addAndIndexAttribute(PREVIOUS_AFTER);
		regex                  = addAndIndexAttribute(REGEX);
		repeat                 = addAndIndexAttribute(REPEAT);
		splitter               = addAndIndexAttribute(SPLITTER);
		to                     = addAndIndexAttribute(TO_XPATH);
		value                  = addAndIndexAttribute(VALUE);
		xpath                  = addAndIndexAttribute(XPATH);
		
		outputLevel = Outputter.extractOutputLevel(this.transformElement);
		LOG.trace(outputLevel+"/"+transformElement.getAttributeValue(Outputter.OUTPUT));
		if (!OutputLevel.NONE.equals(outputLevel)) {
			LOG.debug("OUTPUT "+outputLevel);
		}
	}

	private String addAndIndexAttribute(String attName) {
		String attVal = transformElement.getAttributeValue(attName);
//		if (attVal != null) {
//			attValMap.add(attName, attVal);
//		}
		return attVal;
	}

// ================ markup ================
	
	public void applyMarkup(Element element) {
		this.parsedElement = element;
		applyMarkup();
	}

	public void applyMarkup(LineContainer lineContainer) {
		parsedElement = lineContainer.getLinesElement();
		applyMarkup();
	}

	private void applyMarkup() {
		checkProcessExists(process);
		if (process == null) {
			// never reach here
		} else if (ADD_ATTRIBUTE.equals(process)) {
			addAttribute();
		} else if (ADD_CHILD.equals(process)) {
			addChild();
		} else if (ADD_DICTREF.equals(process)) {
			addDictRef();
		} else if (ADD_LABEL.equals(process)) {
			addLabel();
		} else if (ADD_LINK.equals(process)) {
			addLink();
		} else if (ADD_MAP.equals(process)) {
			addMap();
		} else if (ADD_NAMESPACE.equals(process)) {
			addNamespace();
		} else if (ADD_UNITS.equals(process)) {
			addUnits();
		} else if (CHECK_DICTIONARY.equals(process)) {
			checkDictionary();
		} else if (CREATE_ARRAY.equals(process)) {
			createArray();
		} else if (CREATE_DATE.equals(process)) {
			createDate();
		} else if (CREATE_DOUBLE.equals(process)) {
			createDouble();
		} else if (CREATE_FORMULA.equals(process)) {
			createFormula();
		} else if (CREATE_GROUP.equals(process)) {
			createGroup();
		} else if (CREATE_INTEGER.equals(process)) {
			createInteger();
		} else if (CREATE_MOLECULE.equals(process)) {
			try {
				createMolecule();
			} catch (Exception e) {
				LOG.error("CANNOT CREATE MOLECULE - CONTINUING");
			}
		} else if (CREATE_MATRIX33.equals(process)) {
			createMatrix33();
		} else if (CREATE_NAMEVALUE.equals(process)) {
			createNameValue();
		} else if (CREATE_STRING.equals(process)) {
			createString();
		} else if (CREATE_VECTOR3.equals(process)) {
			createVector3();
		} else if (CREATE_WRAPPER.equals(process)) {
			createWrapper();
		} else if (CREATE_WRAPPER_METADATA.equals(process)) {
			createWrapperMetadata();
		} else if (CREATE_WRAPPER_PARAMETER.equals(process)) {
			createWrapperParameter();
		} else if (CREATE_WRAPPER_PROPERTY.equals(process)) {
			createWrapperProperty();
		} else if (CREATE_ZMATRIX.equals(process)) {
			createZMatrix();
		} else if (DEBUG_NODES.equals(process)) {
			debugNodes();
		} else if (DELETE.equals(process)) {
			delete();
		} else if (HELP.equals(process)) {
			help();
		} else if (JOIN_ARRAYS.equals(process)) {
			joinArrays();
		} else if (MOVE.equals(process)) {
			move();
		} else if (PULLUP.equals(process)) {
			pullup();
		} else if (PULLUP_SINGLETON.equals(process)) {
			pullupSingleton();
		} else if (RENAME.equals(process)) {
			rename();
		} else if (SET_VALUE.equals(process)) {
			setValue();
		} else if (SPLIT.equals(process)) {
			split();
		} else if (WRAP.equals(process)) {
			wrapPropertiesAndParameters();
		} else {
			if (processExists(process)) {
				invokeProcess(process);
			} else {
				help();
				throw new RuntimeException("Unknown process: "+process);
			}
		}
	}
	
// ========================== methods ===============================
	
	
	private void addAttribute() {
		assertRequired(XPATH, xpath);
		assertRequired(NAME, name);
		assertRequired(VALUE, value);
		String[] attnames = name.split(CMLConstants.S_COLON);
		String prefix = null;
//		String localname = null;
		String uri = null;
		Integer fromPos = (fromPosition == null) ? null : new Integer(fromPosition);
		if (attnames.length == 2) {
			prefix = attnames[0];
//			localname = attnames[1];
			if (prefix.equals(CMLConstants.CMLX_PREFIX)) {
				uri = CMLConstants.CMLX_NS;
			}
		}
		String valuex = evaluateValue(value);
		if (valuex != null) {
			List<Node> nodeList = getXpathQueryResults();
			for (Node node : nodeList) {
				Element element = (Element) node;
				List<Node> fromNodeList = TransformElement.queryUsingNamespaces(element, from, fromPos);
				for (Node fromNode : fromNodeList) {
					Element fromElement = (Element) fromNode;
					if (uri != null) {
						fromElement.addAttribute(new Attribute(name, uri, valuex));
					} else {
						fromElement.addAttribute(new Attribute(name, valuex));
					}
				}
			}
		}
	}

	private void addChild() {
//		assertRequired(POSITION, position);
		assertRequired(XPATH, xpath);
		assertRequired(ELEMENT_NAME, elementName);
		List<Node> nodeList = getXpathQueryResults();
		Object pos = (position == null) ? LAST : getPosition(position);
		Element templateElement = createNewElement(elementName, id, dictRef);
		for (Node node : nodeList) {
			Element parent = (Element) node;
			transformElement = (Element) templateElement.copy();
			if (LAST.equals(pos)) {
				parent.appendChild(transformElement);
			} else if (pos instanceof Integer){
				parent.insertChild(transformElement, (Integer)pos);
			}
		}
	}

	private void addDictRef() {
		assertRequired(XPATH, xpath);
		assertRequired(VALUE, value);
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			Element element = (Element) node;
			element.addAttribute(new Attribute(DICT_REF, value));
		}
	}

	private void addLabel() {
		assertRequired(XPATH, xpath);
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			if (node instanceof CMLAtom) {
				CMLAtom atom = (CMLAtom) node;
				String id = atom.getId();
				if (id.startsWith("a")) {
					String labelS = atom.getElementType() + id.substring(1);
					CMLLabel label = new CMLLabel();
					label.setCMLValue(labelS);
					atom.appendChild(label);
				}
			} else {
				throw new RuntimeException("Cannot convert: "+node.getClass().getName());
			}
		}
	}

	private void addLink() {
		assertRequired(XPATH, xpath);
		assertRequired(XPATH, from);
		assertRequired(XPATH, to);
		assertRequired(XPATH, map);
		assertRequired(XPATH, value);
		List<Node> nodeList = getXpathQueryResults();
		Nodes mapNodes = TransformElement.queryUsingNamespaces(parsedElement, map);
		CMLMap map = (mapNodes.size() != 1) ? null : (CMLMap) mapNodes.get(0);
		if (map == null) {
			throw new RuntimeException("Cannot create map for link");
		}
		for (Node node : nodeList) {
			Element element = (Element)node;
			Nodes toNodes = TransformElement.queryUsingNamespaces(element, to);
			Nodes fromNodes = TransformElement.queryUsingNamespaces(element, from);
			String toRef = null;
			String[] toRefs = null;
			String fromRef = null;
			String[] fromRefs = null;
			if (toNodes.size() == 1) {
				toRef = makeToFromAttribute(toNodes.get(0));
			} else if (toNodes.size() > 0) {
				toRefs = makeToFromAttribute(toNodes);
			}
			if (fromNodes.size() == 1) {
				fromRef = makeToFromAttribute(fromNodes.get(0));
			} else if (fromNodes.size() > 0) {
				fromRefs = makeToFromAttribute(fromNodes);
			}
			if ((toRef == null && toRefs == null) ||
				(fromRef == null && fromRefs == null)) {
				throw new RuntimeException("link requires toRef(s) and fromRef(s)");
			}
			CMLLink link = new CMLLink();
			setToRefRefs(toRef, toRefs, link);
			setFromRefRefs(fromRef, fromRefs, link);
			map.addLink(link);
		}
	}

	private void setFromRefRefs(String fromRef, String[] fromRefs, CMLLink link) {
		if (fromRefs != null) {
			link.setFromSet(fromRefs);
		} else if (fromRef != null) {
			link.setFrom(fromRef);
		} else {
			throw new RuntimeException("link requires fromRef(s)");
		}
	}

	private void setToRefRefs(String toRef, String[] toRefs, CMLLink link) {
		if (toRefs != null) {
			link.setToSet(toRefs);
		} else if (toRef != null) {
			link.setTo(toRef);
		} else {
			throw new RuntimeException("link requires toRef(s)");
		}
	}

	private String makeToFromAttribute(Node node) {
		String toFrom = null;
		if (value.equals(VALUE)) {
			toFrom = node.getValue();
		} else if (value.equals(ID)) {
			toFrom = ((Element)node).getAttributeValue(ID);
		}
		return toFrom;
	}

	private String[] makeToFromAttribute(Nodes nodes) {
		String[] refs = (nodes.size() == 0) ? null : new String[nodes.size()];
		for (int i = 0; i < nodes.size(); i++) {
			refs[i] = makeToFromAttribute(nodes.get(i));
		}
		return refs;
	}

	private void addNamespace() {
		assertRequired(XPATH, xpath);
		assertRequired(NAME, name);
		assertRequired(VALUE, value);
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			Element element = (Element)node;
			element.addNamespaceDeclaration(name, value);
		}
	}
	private void addUnits() {
		assertRequired(XPATH, xpath);
		assertRequired(VALUE, value);
		
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			if (node instanceof HasUnits) {
				Element element = (Element) node;
				element.addAttribute(new Attribute(UNITS, value));
			}
		}
	}

	private void createArray() {
		assertRequired(XPATH, xpath);
		assertRequired(FROM, from);
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			Element element = (Element)node;
			Nodes scalarNodes = TransformElement.queryUsingNamespaces(element, from);
			if (scalarNodes.size() > 0) {
				CMLArray array = null;
				CMLScalar scalar0 = (CMLScalar) scalarNodes.get(0);
				String dataTypex = (dataType !=  null) ? dataType : scalar0.getDataType();
				String dictRefx = (dictRef != null) ? dictRef : scalar0.getDictRef();
				String[] values = null;
				if (scalarNodes.size() == 1) {
					// splits scalar 
					String value = scalarNodes.get(0).getValue();
					if (value != null && value.trim().length() > 0) {
						String splitterx = (splitter == null) ? CMLConstants.S_WHITEREGEX : splitter;
						values = value.split(splitterx);
					}
				} else if (scalarNodes.size() > 0) {
					// collects all values into scalars
					values = new String[scalarNodes.size()];
					for (int j = 0; j < scalarNodes.size(); j++) {
						values[j] = scalarNodes.get(j).getValue();
					}
				}
				if (values != null && values.length > 1) {
					if (CMLConstants.XSD_INTEGER.equals(dataTypex)) {
						array = new CMLArray(new IntArray(values).getArray());
					} else if (CMLConstants.XSD_DOUBLE.equals(dataTypex)) {
						array = new CMLArray(new RealArray(values).getArray());
					} else { 
						array = new CMLArray(values, delimiter);
					}
					array.setDictRef(dictRefx);
					scalar0.getParent().replaceChild(scalar0, array);
					for (int j = 1; j < scalarNodes.size(); j++) {
						values[j] = scalarNodes.get(j).getValue();
						scalarNodes.get(j).detach();
					}
				}
			}
		}
	}

	private void createDate() {
		assertRequired(XPATH, xpath);
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			if (node instanceof CMLScalar) {
				CMLScalar scalar = (CMLScalar) node;
				String val = scalar.getValue();
				try {
					Object dateTimeDuration = null;
					if (DHMS.equals(format)) {
						dateTimeDuration = createDMHS(val);
//						System.out.println(dateTimeDuration);
					} else if (format != null) {
						dateTimeDuration = JodaDate.parseDate(val, format);
					} else {
						dateTimeDuration = JodaDate.parseDate(val);
					}
					scalar.setValue(dateTimeDuration.toString());
					if (dictRef != null) {
						scalar.setDictRef(dictRef);
					}
				} catch (Exception e) {
					LOG.error("Cannot parse/set date/duration: "+val+ " (format='"+format+"'); "+e);
				}
			}
		}
	}

	private Duration createDMHS(String val) {
		Pattern ELAPSED = Pattern.compile(
				"\\s*(\\d+)\\s*days\\s*(\\d+)\\s*hours\\s*(\\d+)\\s*minutes\\s*([\\d\\.]+)\\s*seconds\\.?\\s*");
		Matcher matcher = ELAPSED.matcher(val);
		Duration duration = null;
		if (matcher.matches()) {
			double secs = new Integer(matcher.group(1))*86400 +
			    new Integer(matcher.group(2))*3600 +
			    new Integer(matcher.group(3))*60 +
			    new Double(matcher.group(4));
			long millis = (long)(1000*secs);
			duration = new Duration(millis);
		}
		return duration;
	}

	private void createDouble() {
		assertRequired(XPATH, xpath);
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			if (node instanceof CMLScalar) {
				CMLScalar scalar = (CMLScalar) node;
				String val = scalar.getValue();
				String dictRefx = (dictRef != null) ? dictRef : scalar.getDictRef();
				try {
					Double d = Real.parseDouble(val);
					scalar.setValue(d);
					scalar.setDictRef(dictRefx);
				} catch (Exception e) {
					LOG.error("bad double: "+e);
				}
			}
		}
	}

	private void createFormula() {
		assertRequired(XPATH, xpath);
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			if (node instanceof CMLScalar) {
				CMLScalar scalar = (CMLScalar) node;
				String val = scalar.getValue();
				try {
					CMLFormula formula = CMLFormula.createFormula(val);
					scalar.getParent().replaceChild(scalar, formula);
				} catch (Exception e) {
					LOG.error("bad formula: "+e);
				}
			}
		}
	}

	private void createGroup() {
		assertRequired(XPATH, xpath);
		if (previousSiblingsAfter == null && followingSiblingsBefore == null) {
			CMLUtil.debug(this.transformElement, "Transform");
			LOG.warn("should give previousSiblingsAfter or followingSiblingsBefore");
		}
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			groupPreviousSiblings((Element)node);
			groupFollowingSiblings((Element)node);
		}
	}

	private void createInteger() {
		assertRequired(XPATH, xpath);
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
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

	private void createMatrix33() {
		assertRequired(DICT_REF, dictRef);
		assertRequired(FROM, from);
		assertRequired(XPATH, xpath);
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			Element element = (Element)node;
			Nodes nodes = TransformElement.queryUsingNamespaces(element, from);
			if (nodes.size() == 3*3 && (nodes.get(0) instanceof CMLScalar)) {
				addScalarsToMatrix33(nodes);
			} else if (nodes.size() == 3 && (nodes.get(0) instanceof CMLArray)) {
				addArraysToMatrix33(nodes);
			} else if (nodes.size() == 0) {
			} else {
				CMLUtil.debug(element, "PARENT");
				throw new RuntimeException("Cannot create matrix33 found "+nodes.size()+" nodes of type: "+nodes.get(0).getClass());
			}
		}
	}

	private void addScalarsToMatrix33(Nodes nodes) {
		double[] dd = new double[3*3];
		Node node0 = null;
		for (int i = 0; i < 3*3; i++) {
			Node nodex = nodes.get(i);
			if (!(nodex instanceof CMLScalar)) {
				throw new RuntimeException("Expected scalar in 'from', found: "+nodex.getClass());
			}
			CMLScalar scalar = (CMLScalar) nodex;
			dd[i] = scalar.getDouble();
			if (i == 0) {
				node0 = scalar;
			} else {
				scalar.detach();
			}
		}
		CMLMatrix mat = new CMLMatrix(3, 3, dd);
		mat.setDictRef(dictRef);
		node0.getParent().replaceChild(node0, mat);
	}

	private void addArraysToMatrix33(Nodes nodes) {
		CMLArray array0 = (CMLArray) nodes.get(0);
		for (int i = 1; i < 3; i++) {
			Node nodex = nodes.get(i);
			if (!(nodex instanceof CMLArray)) {
				throw new RuntimeException("Expected array in 'from', found: "+nodex.getClass());
			}
			CMLArray array = (CMLArray) nodex;
			if (array.getSize() != 3 || !(CMLConstants.XSD_DOUBLE.equals(array.getDataType()))) {
				throw new RuntimeException("createMatrix33 requires arrays of 3 doubles");
			}
			array0.append(array);
			array.detach();
		}
		CMLMatrix mat = new CMLMatrix(3, 3, array0.getDoubles());
		mat.setDictRef(dictRef);
		ParentNode parent = array0.getParent();
		parent.replaceChild(array0, mat);
	}

	private void addMap() {
		assertRequired(XPATH, xpath);
		assertRequired(ID, id);
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			CMLMap map = new CMLMap();
			map.setId(id);
			((ParentNode)node).appendChild(map);
		}
	}

	private void createMolecule() {
		assertRequired(XPATH, xpath);
		assertRequired(ID, id);
		createMethodNames();
		List<Node> nodeList = getXpathQueryResults();
		if (nodeList.size() > 0) {
			Element element = (Element) nodeList.get(0);
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
			try {
				addArrays(nodeList, natoms);
			} catch (Exception e) {
//				CMLUtil.debug(parsedElement, "PARSED ELEMENT");
				throw new RuntimeException(e);
			}
			addElementTypes();
			addFormulaAndProperties();
			removeExplicitHydrogenCounts(molecule);
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
			assertRequired(XPATH, xpath);
			Pattern pattern = null;
			if (regex == null) {
				assertRequired(NAME, name);
				assertRequired(VALUE, value);
			} else {
				pattern = Pattern.compile(regex);
			}
			List<Node> nodeList = getXpathQueryResults();
			for (Node node : nodeList) {
				Element element = (Element)node;
				if (pattern != null) {
					String value = node.getValue();
					Matcher matcher = pattern.matcher(value);
					if (matcher.matches() && matcher.groupCount() == 2) {
						String nam = matcher.group(1);
						String val = matcher.group(2);
						CMLScalar scalar = new CMLScalar(val);
						scalar.setDictRef("x:"+nam);
						element.getParent().replaceChild(element, scalar);
					}
				} else {
					Nodes nameNodes = TransformElement.queryUsingNamespaces(element, name);
					Nodes valueNodes = TransformElement.queryUsingNamespaces(element, value);
					if (nameNodes.size() == 1 && valueNodes.size() == 1) {
						CMLScalar nameScalar = (CMLScalar)nameNodes.get(0);
						CMLScalar valueScalar = (CMLScalar)valueNodes.get(0);
						createNameValue(nameScalar, valueScalar);
					}
				}
			}
		}

	private void createString() {
		assertRequired(XPATH, xpath);
		List<Node> nodeList = getXpathQueryResults();
		if (nodeList.size() > 0 && nodeList.get(0) instanceof CMLScalar) {
			StringBuilder sb = new StringBuilder();
			for (Node node : nodeList) {
				sb.append(node.getValue());
			}
			((CMLScalar)nodeList.get(0)).setValue(sb.toString());
			for (int i = 1; i < nodeList.size(); i++) {
				nodeList.get(i).detach();
			}
		}
	}

	private void createVector3() {
		/**
 <list templateRef="vv2">
   <list>
     <scalar dataType="xsd:double" dictRef="n:mo1">1.2</scalar> 
     <scalar dataType="xsd:double" dictRef="n:mo2">-0.061</scalar> 
     <scalar dataType="xsd:double" dictRef="n:mo3">-2.7E-15</scalar> 
  </list>
</list>
		 */
		assertRequired(DICT_REF, dictRef);
		assertRequired(FROM, from);
		assertRequired(XPATH, xpath);
		String[] dictRefNames = splitDictRef();
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			Element element = (Element)node;
			Nodes scalarNodes = TransformElement.queryUsingNamespaces(element, from);
			double[] dd = new double[3];
			Node node0 = null;
			if (scalarNodes.size() == 3) {
				for (int j = 0; j < 3; j++) {
					CMLScalar scalar = (CMLScalar) scalarNodes.get(j);
					dd[j] = scalar.getDouble();
					if (j == 0) {
						node0 = scalar;
					} else {
						scalar.detach();
					}
				}
				CMLVector3 v3 = new CMLVector3(dd);
//				double length = v3.getLength();
//				CMLProperty property = new CMLProperty();
//				property.setDictRef(DictRefAttribute.createValue(dictRefNames[0], dictRefNames[1]));
//				CMLScalar lengthScalar = new CMLScalar(length);
//				lengthScalar.setDictRef("cmlx:length");
//				property.appendChild(lengthScalar);
//				property.appendChild(v3);
				node0.getParent().replaceChild(node0, v3);
			} else if (scalarNodes.size() == 0) {
			} else {
				CMLUtil.debug(element, "PAR");
				throw new RuntimeException("No vector3 can be made");
			}
		}
	}

	private void createWrapper() {
		assertRequired(XPATH, xpath);
		assertRequired(ELEMENT_NAME, elementName);
		Element templateElement = createNewElement(elementName, id, dictRef);
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			ParentNode parent = node.getParent();
			CMLElement element = (CMLElement) templateElement.copy();
			if (parent != null) {
				parent.replaceChild(node, element);
			} else {
				element.copyNamespaces((CMLElement) node);
			}
			element.appendChild(node);
		}
	}

	private void createWrapperMetadata() {
		wrapWithPropertyOrParameter(new CMLMetadata());
	}

	private void createWrapperParameter() {
		wrapWithPropertyOrParameter(new CMLParameter());
	}

	private void createWrapperProperty() {
		wrapWithPropertyOrParameter(new CMLProperty());
	}

	private void createZMatrix() {
/*
- <list cmlx:templateRef="atom1">
  <scalar dataType="xsd:string" dictRef="cc:elementType">C</scalar> 
  </list>
- <list cmlx:templateRef="atom2">
  <scalar dataType="xsd:string" dictRef="cc:elementType">H</scalar> 
  <scalar dataType="xsd:integer" dictRef="cc:serial">1</scalar> 
  <scalar dataType="xsd:string" dictRef="cc:name">B1</scalar> 
  </list>
- <list cmlx:templateRef="atom3">
  <scalar dataType="xsd:string" dictRef="cc:elementType">H</scalar> 
  <scalar dataType="xsd:integer" dictRef="cc:serial">1</scalar> 
  <scalar dataType="xsd:string" dictRef="cc:name">B2</scalar> 
  <scalar dataType="xsd:integer" dictRef="cc:serial">2</scalar> 
  <scalar dataType="xsd:string" dictRef="cc:name">A1</scalar> 
  </list>
- <list cmlx:template="atom4">
  <scalar dataType="xsd:string" dictRef="cc:elementType">H</scalar> 
  <scalar dataType="xsd:integer" dictRef="cc:serial">1</scalar> 
  <scalar dataType="xsd:string" dictRef="cc:name">B3</scalar> 
  <scalar dataType="xsd:integer" dictRef="cc:serial">2</scalar> 
  <scalar dataType="xsd:string" dictRef="cc:name">A2</scalar> 
  <scalar dataType="xsd:integer" dictRef="cc:serial">3</scalar> 
  <scalar dataType="xsd:string" dictRef="cc:name">D1</scalar> 
  <scalar dataType="xsd:integer" dictRef="cc:serial">0</scalar> 
  </list>
 */
		assertRequired(XPATH, xpath);
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			Element element = (Element) node;
			ZMatrixElement zMatrixElement = new ZMatrixElement(element, value);
			element.getParent().appendChild(zMatrixElement.getMolecule());
		}
	}

	private void debugNodes() {
		assertRequired(XPATH, xpath);
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			debug(node);
		}
	}

	private void delete() {
		assertRequired(XPATH, xpath);
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			node.detach();
		}
	}

	private void joinArrays() {
		assertRequired(XPATH, xpath);
		List<Node> nodeList = getXpathQueryResults();
		if (nodeList.size() > 1 && nodeList.get(0) instanceof CMLArray) {
			CMLArray baseArray = (CMLArray) nodeList.get(0);
			String dictRef = baseArray.getDictRef();
			String dataType = baseArray.getDataType();
			List<String> stringList = new ArrayList<String>();
			RealArray realArray = new RealArray();
			IntArray intArray = new IntArray();
			for (int i = 0; i < nodeList.size(); i++) {
				CMLArray array = (CMLArray) nodeList.get(i);
				if (CMLConstants.XSD_INTEGER.equals(dataType)) {
					intArray.addArray(new IntArray(array.getInts()));
				} else if (CMLConstants.XSD_DOUBLE.equals(dataType)) {
					realArray.addArray(new RealArray(array.getDoubles()));
				} else {
					stringList.addAll(array.getStringValues());
				}
				if (i > 0) {
					array.detach();
				}
			}
			CMLArray newArray = null;
			if (CMLConstants.XSD_INTEGER.equals(dataType)) {
				newArray = new CMLArray(intArray.getArray());
			} else if (CMLConstants.XSD_DOUBLE.equals(dataType)) {
				newArray = new CMLArray(realArray.getArray());
			} else {
				newArray = new CMLArray(stringList.toArray(new String[0]), DEFAULT_DELIMITER);
			}
			newArray.setDictRef(dictRef);
			baseArray.getParent().replaceChild(baseArray, newArray);
		}
	}

	private void move() {
		assertRequired(XPATH, xpath);
		assertRequired(TO_XPATH, to);
		List<Node> nodeList = getXpathQueryResults();
		Nodes toParents = TransformElement.queryUsingNamespaces(parsedElement, to);
		ParentNode toParent = (toParents.size() == 1) ? (ParentNode) toParents.get(0) : null;
		if (toParent != null) {
			for (Node node : nodeList) {
				node.detach();
				toParent.appendChild(node);
			}
		} else {
			LOG.debug("null toXpath: "+to);
		}
	}

	private void pullup() {
		assertRequired(XPATH, xpath);
		int repeatCount = (repeat == null) ? 1 : new Integer(repeat);
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			for (int i = 0; i < repeatCount; i++) {
				Element element = (Element)node;
				ParentNode parent = element.getParent();
				ParentNode grandParent = parent.getParent();
				if (grandParent != null) {
					int idx = grandParent.indexOf(parent);
					element.detach();
					grandParent.insertChild(element, idx);
				}
			}
		}
	}


	private void pullupSingleton() {
		assertRequired(XPATH, xpath);
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			Element nameElement = (Element)node;
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

	private void rename() {
		throw new RuntimeException("rename NYI");
	}

	private void setValue() {
		assertRequired(XPATH, xpath);
		assertRequired(VALUE, value);
		List<Node> nodeList = getXpathQueryResults();
		if (value != null) {
			for (Node node : nodeList) {
				if (node instanceof Attribute) {
					Attribute att = (Attribute) node;
					att.setValue(value);
				} else if (node instanceof Element) {
					Element element = (Element) node;
					if (element.getChildElements().size() == 0) {
						for (int j = 0; j < element.getChildCount(); j++) {
							element.getChild(0).detach();
						}
						element.appendChild(value);
					}
				} else {
					throw new RuntimeException("Cannot set value on: "+node.getClass().getName());
				}
			}
		}
	}

	private void split() {
		assertRequired(XPATH, xpath);
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			if (node instanceof CMLArray) {
				splitArrayToScalars((CMLArray)node);
			} else if (node instanceof CMLScalar) {
				splitScalarValueToScalars((CMLScalar)node);
			}
		}
	}

	private void splitScalarValueToScalars(CMLScalar scalar) {
		if (splitter == null) {
			splitter = CMLConstants.S_WHITEREGEX;
		}
		String s = scalar.getValue();
		String[] ss = s.split(splitter);
		if (ss.length > 1) {
			CMLList list = new CMLList();
			list.setDictRef((dictRef != null) ? dictRef : scalar.getDictRef());
			scalar.getParent().replaceChild(scalar, list);
			for (String sss : ss) {
				CMLScalar scalarx = new CMLScalar(sss);
				if (dictRef != null) {
					scalarx.setDictRef(dictRef);
				}
				list.appendChild(scalarx);
			}
		}
	}

	private void splitArrayToScalars(CMLArray array) {
		CMLList list = new CMLList();
		int size = array.getSize();
		for (int j = 0; j < size; j++) {
			CMLScalar scalar = array.getElementAt(j);
			if (dictRef != null) {
				scalar.setDictRef(dictRef);
			}
			list.appendChild(scalar);
		}
		array.getParent().replaceChild(array, list);
	}

	private void wrapWithPropertyOrParameter(Element wrapperx) {
		assertRequired(XPATH, xpath);
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			if (node instanceof CMLScalar ||
					node instanceof CMLArray ||
					node instanceof CMLMatrix) {
				Element wrapper = (Element) wrapperx.copy();
				Element element = (Element) node;
				String dictRefx = (dictRef != null) ? dictRef : ((HasDictRef)element).getDictRef();
				if (dictRefx == null) {
					throw new RuntimeException("Cannot find dictRef for wrapper");
				}
				if (wrapper instanceof HasDictRef) {
					((HasDictRef)wrapper).setDictRef(dictRefx);
				} else if (wrapper instanceof CMLMetadata) {
					((CMLMetadata)wrapper).setName(dictRefx);
				}
				ParentNode parent = element.getParent();
				parent.replaceChild(element, ((Element)wrapper));
				((Element)wrapper).appendChild(element);
				Nodes dictRefNodes = element.query("./@dictRef");
				for (int i = 0; i < dictRefNodes.size(); i++) {
					dictRefNodes.get(i).detach();
				}
			}
		}
	}

	private void wrapPropertiesAndParameters() {
		assertRequired(XPATH, xpath);
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			Element pp = (Element) node;
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

// ==================== end of methods ============	

	private String evaluateValue(String valuex) {
		if (valuex != null) {
			String function = null;
			if (valuex.startsWith(STRING) && valuex.endsWith(CMLConstants.S_RBRAK)) {
				function = STRING;
			} else if (valuex.startsWith(NUMBER) && valuex.endsWith(CMLConstants.S_RBRAK)) {
				function = NUMBER;
			}
			if (function != null) {
				String xpath = valuex.substring(function.length(), valuex.length()-1);
				Nodes nodes = TransformElement.queryUsingNamespaces(parsedElement, xpath);
				valuex = (nodes.size() == 0) ? null : nodes.get(0).getValue();
			}
		}
		return valuex;
	}

	private void help(String processName) {
		Element element = getProcessElement(processName);
		if (!processExists(processName)) {
			throw new RuntimeException("no process with name: "+processName);
		}
		Elements descs = element.getChildElements(DESC);
		System.out.println("========"+processName+"========");
		for (int i = 0; i < descs.size(); i++) {
			System.out.println("description .. "+descs.get(i).getValue());
		}
		Elements nodes = element.getChildElements(NODE);
		for (int i = 0; i < nodes.size(); i++) {
			System.out.println("applies to .. "+nodes.get(i).getValue());
		}
		Elements args = element.getChildElements(ARG);
		for (int i = 0; i < args.size(); i++) {
			Element arg = args.get(i);
			String mandatory = arg.getAttributeValue(MANDATORY);
			System.out.println("     >> "+arg.getAttributeValue(NAME)+" "+((mandatory != null) ? MANDATORY : "")+" : "+arg.getValue());
		}
	}

	private Element getProcessElement(String processName) {
		Nodes elements = processListElement.query("*[@name='"+processName+"']");
		if (elements.size() != 1) {
			throw new RuntimeException("No help for: "+processName);
		}
		return (elements.size() == 1) ? (Element) elements.get(0) : null;
	}

	private boolean processExists(String processName) {
		boolean ok = true;
		try {
			this.getClass().getDeclaredMethod(processName);
		} catch (Exception e) {
			ok = false;
		}
		return ok;
	}
	
	private boolean invokeProcess(String processName) {
		boolean ok = true;
		try {
			Method method = this.getClass().getDeclaredMethod(processName);
			method.setAccessible(true);
			Object retobj = method.invoke(this, new Object[0]);
		} catch (Exception e) {
			ok = false;
			throw new RuntimeException("no method: "+process, e);
		}
		return ok;
	}
	
	private void checkProcessExists(String processName) {
		boolean ok = false;
//		for (String process : PROCESS_NAMES) {
//			if (process.equals(processName)) {
//				ok = true;
//				break;
//			}
//		}
		if (!ok) {
			if (!processExists(processName)) {
				throw new RuntimeException("Unknown process: "+processName);
			}
		}
	}

	private void help() {
		ensureProcessListElement();
		for (String processName : PROCESS_NAMES) {
			help(processName);
		}
	}

	private void ensureProcessListElement() {
		if (processListElement == null) {
			try {
				InputStream is = Util.getResourceUsingContextClassLoader(PROCESS_LIST_URI, TransformElement.class);
				processListElement = new Builder().build(is).getRootElement();
				CMLUtil.removeWhitespaceNodes(processListElement);
			} catch (Exception e) {
				throw new RuntimeException("cannot make help: "+PROCESS_LIST_URI, e);
			}
		}
	}

	private void checkDictionary() {
		xpath = "//*[@dictRef]";
		List<Node> nodeList = getXpathQueryResults();
		Map<String, List<Element>> dictRefNodes = new HashMap<String, List<Element>>();
		for (Node node : nodeList) {
			Element element = (Element) node;
			Attribute att = element.getAttribute(DICT_REF);
			String value = att.getValue();
			String prefix = DictRefAttribute.getPrefix(value);
			if (CMLConstants.CMLX_PREFIX.equals(prefix)) {
				continue;
			}
			String templateRef = element.getAttributeValue(Template.TEMPLATE_REF, CMLConstants.CMLX_NS);
			String id = element.getAttributeValue(ID);
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

	private Element createNewElement(String elementName, String id, String dictRef) {
		
		String[] names = elementName.split(CMLConstants.S_COLON);
		String prefix = (names.length == 2) ? names[0] : null;
		String local = (names.length == 2) ? names[1] : elementName;
		Element newElement = null;
		try {
			newElement = new Element(local);
		} catch (Exception e) {
			CMLUtil.debug(this.transformElement, "BAD ELEMENT");
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
		Template.copyNamespaces(this.transformElement, newElement);
		return newElement;
	}

	private List<Node> getXpathQueryResults() {
		assertRequired(XPATH, xpath);
		Nodes nodes = TransformElement.queryUsingNamespaces(parsedElement, xpath);
		List<Node> nodeList = new ArrayList<Node>();
		if (position != null) {
			int pos = 0;
			try {
				pos = Integer.parseInt(position);
			} catch (NumberFormatException nfe) {
				throw new RuntimeException("bad integer: "+position);
			}
			if (pos > 0 && pos <= nodes.size()) {
				nodeList.add(nodes.get(pos-1));
			}
		} else {
			for (int i = 0; i < nodes.size(); i++) {
				nodeList.add(nodes.get(i));
			}
		}
		return nodeList;
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
			CMLUtil.debug(this.transformElement, "BAD Transform");
			throw new RuntimeException("Must give "+attName+" attribute");
		}
	}

//	private Nodes createXpathNodes() {
//		if (xpath == null) {
//			throw new RuntimeException("xpath attribute must not be null");
//		}
//		try {
//			Nodes xpaths = TransformElement.queryUsingNamespaces(parsedElement, xpath);
//			return xpaths;
//		} catch (Exception e) {
//			throw new RuntimeException("error in name Xpath: "+xpath, e);
//		}
//	}

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
		if (xpath == null) {
			throw new RuntimeException("null xpath attribute");
		}
		Nodes nodes = null;
		try {
			nodes = element.query(xpath, Template.CML_CMLX_CONTEXT);
		} catch (Exception e) {
			throw new RuntimeException("bad query: "+xpath, e);
		}
		return nodes;
	}

	public static List<Node> queryUsingNamespaces(Element element, String xpath, Integer position) {
		List<Node> nodeList = new ArrayList<Node>();
		// if no query return element
		if (xpath == null) {
			nodeList.add(element);
		} else {
			Nodes nodes = null;
			try {
				nodes = element.query(xpath, Template.CML_CMLX_CONTEXT);
			} catch (Exception e) {
				throw new RuntimeException("bad query: "+xpath, e);
			}
			for (int i = 0; i < nodes.size(); i++) {
				// add +1 for difference in counting
				if (position == null || position == i+1) {
					nodeList.add(nodes.get(i));
				}
			}
		}
		return nodeList;
	}

	private void createNameValue(CMLScalar nameScalar, CMLScalar valueScalar) {
		String nameValue = nameScalar.getValue().trim();
		DictRefAttribute nameDictRef = (DictRefAttribute)((HasDictRef) nameScalar).getDictRefAttribute(); 
		String namePrefix = nameDictRef.getPrefix();
		nameScalar.setDictRef(DictRefAttribute.createValue(namePrefix, nameValue));
		String valueValue = valueScalar.getValue().trim();
		valueScalar.detach();
		nameScalar.setValue(valueValue);
	}

	private String[]  splitDictRef() {
		String[] dictRefNames = dictRef.split(CMLConstants.S_COLON);
		if (dictRefNames.length != 2) {
			throw new RuntimeException("dictRef must be qualified name; found: "+dictRef);
		}
		return dictRefNames;
	}
	
	private void createAndAddAtoms(int natoms) {
		for (int iatom = 0; iatom < natoms; iatom++) {
			id = "a"+(iatom+1);
			CMLAtom atom = new CMLAtom(id);
			molecule.addAtom(atom);
		}
	}

	private void addArrays(List<Node> nodeList, int natoms) {
		int i = 0;
		for (Node node : nodeList) {
			if (!(node instanceof CMLArray)) {
				CMLUtil.debug((Element) node, "ELEM:"+from);
				throw new RuntimeException("molecule only operates on arrays");
			}
			CMLArray array = (CMLArray) node;
			if (array.getSize() != natoms) {
				throw new RuntimeException("addArrays ("+i+")out of sync with atoms: "+array.getSize()+" != "+natoms);
			}
			processDictRef(array);
			array.detach();
			i++;
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
		double mwt = moleculeTool.getCalculatedMolecularMass(HydrogenControl.USE_HYDROGEN_COUNT);
//		double mwt = moleculeTool.getCalculatedMolecularMass();
		CMLScalar scmwt = new CMLScalar(mwt);
		scmwt.setUnits("unit", "dalton", UNIT_SI_URI);
		CMLProperty mmprop = new CMLProperty(); 
		mmprop.setDictRef("cml:molmass");
		mmprop.appendChild(scmwt);
		molecule.appendChild(mmprop);
	}

	private void removeExplicitHydrogenCounts(CMLMolecule molecule) {
		Nodes hydrogenCounts = molecule.query(".//@hydrogenCount");
//		if (hydrogenCounts.size() > 1) {
//			throw new RuntimeException("HYDROGEN "+hydrogenCounts.size());
//		}
		for (int i = 0; i < hydrogenCounts.size(); i++) {
			hydrogenCounts.get(i).detach();
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
		try {
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
		} catch (Exception e) {
//			CMLUtil.debug()
			System.err.println(""+localName+doubles);
			throw new RuntimeException("problem", e);
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
		String argx = process.substring(CREATE_MOLECULE.length()).trim();
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
	
	public void debug() {
//		CMLUtil.debug(this.element, "DEBUG");
	}
}
