package org.xmlcml.cml.converters.text;

import java.io.FileInputStream;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParentNode;
import nu.xom.Text;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.Outputter;
import org.xmlcml.cml.converters.Outputter.OutputLevel;
import org.xmlcml.cml.converters.format.RecordReader;
import org.xmlcml.cml.element.CMLAngle;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLDictionary;
import org.xmlcml.cml.element.CMLEntry;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLLabel;
import org.xmlcml.cml.element.CMLLength;
import org.xmlcml.cml.element.CMLLink;
import org.xmlcml.cml.element.CMLList;
import org.xmlcml.cml.element.CMLMap;
import org.xmlcml.cml.element.CMLMap.Direction;
import org.xmlcml.cml.element.CMLMatrix;
import org.xmlcml.cml.element.CMLMetadata;
import org.xmlcml.cml.element.CMLModule;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLMolecule.HydrogenControl;
import org.xmlcml.cml.element.CMLParameter;
import org.xmlcml.cml.element.CMLProperty;
import org.xmlcml.cml.element.CMLPropertyList;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.element.CMLTable;
import org.xmlcml.cml.element.CMLTable.TableType;
import org.xmlcml.cml.element.CMLTorsion;
import org.xmlcml.cml.element.CMLVector3;
import org.xmlcml.cml.element.CMLZMatrix;
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
	
	private static final String CML_ERROR = "cml:error";
	public static final String TAG = "transform";

	// processList
	private static final String ARG                 = "arg";
	private static final String DESC                = "desc";
	private static final String MANDATORY           = "mandatory";
	private static final String NODE                = "node";

	// attributes
	protected static final String ARGS                = "args";
	private static final String ATOM_REFS           = "atomRefs";
	private static final String COLS                = "cols";
	private static final String DATA_TYPE           = "dataType";
	private static final String DELIMITER           = "delimiter";
	private static final String DICT_REF            = "dictRef";
	private static final String ELEMENT_NAME        = "elementName";
	private static final String FOLLOW_BEFORE       = "followingSiblingsBefore";
	private static final String FORMAT              = "format";
	private static final String FROM                = "from";
	private static final String FROM_POSITION       = "fromPosition";
	private static final String INPUT               = "input";
	private static final String KEY                 = "key";
	private static final String MAP                 = "map";
	private static final String NAME                = "name";
	private static final String OUTPUT              = "output";
	private static final String POSITION            = "position";
	protected static final String PROCESS             = "process";
	private static final String PREVIOUS_AFTER      = "previousSiblingsAfter";
	private static final String REGEX               = "regex";
	private static final String REGEX_PATH          = "regexPath";
	private static final String REPEAT              = "repeat";
	private static final String ROWS                = "rows";
	private static final String SPLITTER            = "splitter";
	private static final String TEST                = "test";
	private static final String TO                  = "to";
	protected static final String VALUE_XPATH         = "valueXpath";
	private static final String VALUE               = "value";
	protected static final String XPATH               = "xpath";

	public static String[] OPTIONS = 
		new String[]{
		ARGS,
		ATOM_REFS,
		COLS,
		DATA_TYPE, 
		DELIMITER, 
		DICT_REF, 
		ELEMENT_NAME, 
		FOLLOW_BEFORE,
		FORMAT, 
		FROM, 
		FROM_POSITION, 
		ID, 
		INPUT, 
		KEY,
		MAP,
		NAME,
		OUTPUT,
		POSITION,
		PREVIOUS_AFTER,
		REGEX,
		REGEX_PATH,
		ROWS,
		REPEAT,
		SPLITTER,
		TEST,
		TO,
		VALUE,
		VALUE_XPATH,
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
	private static final String ADD_ID                 = "addId";
	private static final String ADD_LABEL              = "addLabel";
	private static final String ADD_LINK               = "addLink";
	private static final String ADD_MAP                = "addMap";
	private static final String ADD_NAMESPACE          = "addNamespace";
	private static final String ADD_SIBLING            = "addSibling";
	private static final String ADD_UNITS              = "addUnits";
	private static final String CHECK_DICTIONARY       = "checkDictionary";
	private static final String COPY                   = "copy";
	private static final String CREATE_ANGLE           = "createAngle";
	private static final String CREATE_ARRAY           = "createArray";
	private static final String CREATE_ATOM            = "createAtom";
	private static final String CREATE_DATE            = "createDate";
	private static final String CREATE_DOUBLE          = "createDouble";
	private static final String CREATE_FORMULA         = "createFormula";
	private static final String CREATE_GROUP           = "createGroup";
	private static final String CREATE_INTEGER         = "createInteger";
	private static final String CREATE_LENGTH          = "createLength";
	private static final String CREATE_LIST            = "createList";
	private static final String CREATE_MATRIX          = "createMatrix";
	private static final String CREATE_MATRIX33        = "createMatrix33";
	private static final String CREATE_MOLECULE        = "createMolecule";
	private static final String CREATE_NAMEVALUE       = "createNameValue";
	private static final String CREATE_TABLE           = "createTable";
	private static final String CREATE_TORSION         = "createTorsion";
	private static final String GROUP_SIBLINGS         = "groupSiblings";
	private static final String CREATE_STRING          = "createString";
	private static final String CREATE_VECTOR3         = "createVector3";
	private static final String CREATE_WRAPPER         = "createWrapper";
	private static final String CREATE_WRAPPER_METADATA= "createWrapperMetadata";
	private static final String CREATE_WRAPPER_PARAMETER= "createWrapperParameter";
	private static final String CREATE_WRAPPER_PROPERTY= "createWrapperProperty";
	private static final String CREATE_ZMATRIX         = "createZMatrix";
	private static final String DEBUG_NODES            = "debugNodes";
	private static final String DELETE                 = "delete";
	private static final String IF                     = "if";
	private static final String JOIN_ARRAYS            = "joinArrays";
	private static final String MOVE                   = "move";
	private static final String MOVE_RELATIVE          = "moveRelative";
	private static final String PULLUP                 = "pullup";
	private static final String PULLUP_SINGLETON       = "pullupSingleton";
	private static final String READ                   = "read";
	private static final String REPARSE                = "reparse";
	private static final String RENAME                 = "rename";
	private static final String SET_VALUE              = "setValue";
	private static final String SET_DATATYPE           = "setDataType";
	private static final String SPLIT                  = "split";
	private static final String WRAP_PROPERTIES_AND_PARAMETERS = "wrapPropertiesAndParameters";
	private static final String WRITE                  = "write";
	private static final String HELP                   = "help";

	// molecule operations (move elsewhere later)

	private static final String UNIT_SI_URI = "http://www.xml-cml.org/unit/si/";
//	private static final String COMPCHEM_URI = "http://www.xml-cml.org/unit/dictionary/compchem";
//	private static final String COMPCHEM = "compchem";
	private static final String COMPCHEM_CC = "cc";

	private static final String PROCESS_LIST_URI = 
		"org/xmlcml/cml/converters/text/processList.xml";

	private static final String DEFAULT_DELIMITER = CMLConstants.S_PIPE;

	private static final String STRING     = "string";
	private static final String SUBSTRING  = "substring";
	private static final String NUMBER     = "number";

	private static final String UNITS = "units";

	private static final String DHMS = "DHMS";


	private static String[] PROCESS_NAMES = new String[] {
		ADD_ATTRIBUTE,
		ADD_CHILD,
		ADD_DICTREF,
		ADD_ID,
		ADD_LABEL,
		ADD_LINK,
		ADD_MAP,
		ADD_NAMESPACE,
		ADD_SIBLING,
		ADD_UNITS,
		CHECK_DICTIONARY,
		COPY,
		CREATE_ANGLE,
		CREATE_ARRAY,
		CREATE_ATOM,
		CREATE_DATE,
		CREATE_DOUBLE,
		CREATE_GROUP,
		CREATE_INTEGER,
		CREATE_LENGTH,
		CREATE_LIST,
		CREATE_MOLECULE,
		CREATE_MATRIX,
		CREATE_MATRIX33,
		CREATE_NAMEVALUE,
		CREATE_STRING,
		CREATE_TABLE,
		CREATE_TORSION,
		CREATE_VECTOR3,
		CREATE_WRAPPER,
		CREATE_WRAPPER_PARAMETER,
		CREATE_WRAPPER_PROPERTY,
		CREATE_ZMATRIX,
		DEBUG_NODES,
		DELETE,
		GROUP_SIBLINGS,
		HELP,
		IF,
		JOIN_ARRAYS,
		MOVE,
		MOVE_RELATIVE,
		PULLUP_SINGLETON,
		READ,
		REPARSE,
		RENAME,
		SET_VALUE,
		SET_DATATYPE,
		SPLIT,
		WRAP_PROPERTIES_AND_PARAMETERS,
		WRITE,
	};
	
	
	private Template template; // only used for dictionaries - rewrite
	private Element processListElement;
	protected Element transformElement;
	protected OutputLevel outputLevel;
	protected Element parsedElement;

	private CMLMolecule molecule;
	private List<CMLAtom> atoms;

	private String idMethod;
	private String elementMethod;
	
	// attributes
	protected String args;
	private String atomRefs;
	private String cols;
	private String delimiter;
	private String dataType;
	private String dictRef;
	private String elementName;
	private String followingSiblingsBefore;
	private String format;
	private String from;
	private String fromPosition;
	private String id;
	private String input;
	private String key;
	private String map;
	private String name;
	private String output;
	private String position;
	private String previousSiblingsAfter;
	protected String process;
	private String regex;
	private String regexPath;
	private String repeat;
	private String rows;
	private String splitter;
	private String test;
	private String to;
	private String value;
	protected String valueXpath;
	protected String xpath;
	

	public TransformElement() {
		
	}

	/**
	 * does not require a template
	 * use TransformElement.createTransformer factory in preference
	 * @param element
	 */
	public TransformElement(Element element) {
		this();
		this.transformElement = element;
		expandIncludes(transformElement);
		processChildElementsAndAttributes();
	}

	protected void expandIncludes(Element element) {
        try {
        	CMLUtil.ensureDocument(element);
            ClassPathXIncludeResolver.resolveIncludes(element.getDocument());
		} catch (Exception e) {
			throw new RuntimeException("Bad XInclude", e);
		}
	}

	/**
	 * // TODO
	 * template only required for dictionary analysis. Needs rewriting
	 * @param element
	 * @param template
	 */
	@Deprecated //use setTemplate
	public TransformElement(Element element, Template template) {
		this(element);
		this.template = template;
	}
	
	public void setTemplate(Template template) {
		this.template = template;
	}

	public static MarkupApplier createTransformer(Element element) {
		String name = element.getLocalName();
		MarkupApplier transformer = null;
		if (TransformElement.TAG.equals(name)) {
			transformer = new TransformElement(element);
		} else if (MoleculeTransformElement.TAG.equals(name)) {
			transformer = new MoleculeTransformElement(element);
		} else if (TransformListElement.TAG.equals(name)) {
			transformer = new TransformListElement(element);
		}
		return transformer;
	}


	public String getId() {
		return transformElement.getAttributeValue(ID);
	}
	
	protected void processChildElementsAndAttributes() {
		processAttributes();
	}

	protected void processAttributes() {
		Template.checkIfAttributeNamesAreAllowed(transformElement, OPTIONS);
				
		process = transformElement.getAttributeValue(PROCESS);
		if (process == null) {
			throw new RuntimeException("Must give process attribute");
		}
		
		args                   = addAndIndexAttribute(ARGS);
		atomRefs               = addAndIndexAttribute(ATOM_REFS);
		cols                   = addAndIndexAttribute(COLS);
		dataType               = addAndIndexAttribute(DATA_TYPE);
		delimiter              = addAndIndexAttribute(DELIMITER);
		dictRef                = addAndIndexAttribute(DICT_REF);
		elementName            = addAndIndexAttribute(ELEMENT_NAME);
		followingSiblingsBefore= addAndIndexAttribute(FOLLOW_BEFORE);
		format                 = addAndIndexAttribute(FORMAT);
		from                   = addAndIndexAttribute(FROM);
		fromPosition           = addAndIndexAttribute(FROM_POSITION);
		id                     = addAndIndexAttribute(ID);
		input                  = addAndIndexAttribute(INPUT);
		key                    = addAndIndexAttribute(KEY);
		map                    = addAndIndexAttribute(MAP);
		name                   = addAndIndexAttribute(NAME);
		output                 = addAndIndexAttribute(OUTPUT);
		position               = addAndIndexAttribute(POSITION);
		previousSiblingsAfter  = addAndIndexAttribute(PREVIOUS_AFTER);
		regex                  = addAndIndexAttribute(REGEX);
		regexPath              = addAndIndexAttribute(REGEX_PATH);
		repeat                 = addAndIndexAttribute(REPEAT);
		rows                   = addAndIndexAttribute(ROWS);
		splitter               = addAndIndexAttribute(SPLITTER);
		test                   = addAndIndexAttribute(TEST);
		to                     = addAndIndexAttribute(TO);
		value                  = addAndIndexAttribute(VALUE);
		valueXpath             = addAndIndexAttribute(VALUE_XPATH);
		xpath                  = addAndIndexAttribute(XPATH);
		
		manageOutputLevel(outputLevel, transformElement);
	}

	static void manageOutputLevel(OutputLevel outputLevel, Element markupElement) {
		outputLevel = Outputter.extractOutputLevel(markupElement);
		LOG.trace(outputLevel+"/"+markupElement.getAttributeValue(Outputter.OUTPUT));
		if (!OutputLevel.NONE.equals(outputLevel)) {
			LOG.debug("OUTPUT "+outputLevel);
		}
	}

	protected String addAndIndexAttribute(String attName) {
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
		} else if (ADD_ID.equals(process)) {
			addId();
		} else if (ADD_LABEL.equals(process)) {
			addLabel();
		} else if (ADD_LINK.equals(process)) {
			addLink();
		} else if (ADD_MAP.equals(process)) {
			addMap();
		} else if (ADD_NAMESPACE.equals(process)) {
			addNamespace();
		} else if (ADD_SIBLING.equals(process)) {
			addSibling();
		} else if (ADD_UNITS.equals(process)) {
			addUnits();
		} else if (CHECK_DICTIONARY.equals(process)) {
			checkDictionary();
		} else if (COPY.equals(process)) {
			copy();
		} else if (CREATE_ANGLE.equals(process)) {
			createAngle();
		} else if (CREATE_ARRAY.equals(process)) {
			createArray();
		} else if (CREATE_ATOM.equals(process)) {
			createAtom();
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
		} else if (CREATE_LENGTH.equals(process)) {
			createLength();
		} else if (CREATE_LIST.equals(process)) {
			createList();
		} else if (CREATE_MOLECULE.equals(process)) {
			createMolecule();
		} else if (CREATE_MATRIX.equals(process)) {
			createMatrix();
		} else if (CREATE_MATRIX33.equals(process)) {
			createMatrix33();
		} else if (CREATE_NAMEVALUE.equals(process)) {
			createNameValue();
		} else if (CREATE_STRING.equals(process)) {
			createString();
		} else if (CREATE_TABLE.equals(process)) {
			createTable();
		} else if (CREATE_TORSION.equals(process)) {
			createTorsion();
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
		} else if (GROUP_SIBLINGS.equals(process)) {
			groupSiblings();
		} else if (HELP.equals(process)) {
			help();
		} else if (IF.equals(process)) {
			ifTest();
		} else if (JOIN_ARRAYS.equals(process)) {
			joinArrays();
		} else if (MOVE.equals(process)) {
			move();
		} else if (MOVE_RELATIVE.equals(process)) {
			moveRelative();
		} else if (PULLUP.equals(process)) {
			pullup();
		} else if (PULLUP_SINGLETON.equals(process)) {
			pullupSingleton();
		} else if (READ.equals(process)) {
			read();
		} else if (RENAME.equals(process)) {
			rename();
		} else if (REPARSE.equals(process)) {
			reparse();
		} else if (SET_DATATYPE.equals(process)) {
			setDataType();
		} else if (SET_VALUE.equals(process)) {
			setValue();
		} else if (SPLIT.equals(process)) {
			split();
		} else if (WRAP_PROPERTIES_AND_PARAMETERS.equals(process)) {
			wrapPropertiesAndParameters();
		} else if (WRITE.equals(process)) {
			write();
		} else {
			unknownProcess();
		}
	}

	protected void unknownProcess() {
		if (processExists(process)) {
			invokeProcess(process);
		} else {
			help();
			throw new RuntimeException("Unknown process: "+process);
		}
	}
	
// ========================== methods ===============================
	
	
	protected static Integer getInteger(Map<String, String> nameValues, String name, Integer defalt) {
		Integer integer = defalt;
		try {
			integer = new Integer(nameValues.get(name));
		} catch (Exception e) {
			// fail silently as may not be present
		}
		return integer;
	}

	protected static Double getDouble(Map<String, String> nameValues, String name, Double defalt) {
		Double doub = defalt;
		try {
			doub = new Double(nameValues.get(name));
		} catch (Exception e) {
			// fail silently as may not be present
		}
		return doub;
	}

	protected static Map<String, String> splitArgs(String[] argList) {
		Map<String, String> nameValues = new HashMap<String, String>();
		for (String arg : argList) {
			String[] nv = arg.split(CMLConstants.S_EQUALS);
			if (nv.length != 2) {
				throw new RuntimeException("bad name value pair: "+arg);
			}
			nameValues.put(nv[0].trim(), nv[1].trim());
		}
		return nameValues;
	}

	private void addAttribute() {
		assertRequired(XPATH, xpath);
		assertRequired(NAME, name);
		assertRequired(VALUE, value);
		String[] attnames = name.split(CMLConstants.S_COLON);
		String prefix = null;
		String uri = null;
		Integer fromPos = (fromPosition == null) ? null : new Integer(fromPosition);
		if (attnames.length == 2) {
			prefix = attnames[0];
			if (prefix.equals(CMLConstants.CMLX_PREFIX)) {
				uri = CMLConstants.CMLX_NS;
			}
		}
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			Element element = (Element) node;
			List<Node> fromNodeList = TransformElement.queryUsingNamespaces(element, from, fromPos);
			for (Node fromNode : fromNodeList) {
				Element fromElement = (Element) fromNode;
				String valuex = (String) evaluateValue(fromElement, value);
				if (valuex != null) {
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
		assertRequired(XPATH, xpath);
		assertRequired(ELEMENT_NAME, elementName);
		List<Node> nodeList = getXpathQueryResults();
		Object pos = (position == null) ? LAST : getPosition(position);
		Element templateElement = createNewElement(elementName, id, dictRef);
		for (Node node : nodeList) {
			Element parent = (Element) node;
			transformElement = (Element) templateElement.copy();
			// make sure it's CML. CMLElements have a Document parent
			nu.xom.Element transformElementx = CMLElement.createCMLElement(transformElement);
			if (transformElementx == null) {
				transformElementx = transformElement;
			}
			if (value != null && transformElementx instanceof CMLScalar) {
				((CMLScalar) transformElementx).setValue(value);
			}
			
			if (LAST.equals(pos)) {
				CMLUtil.detach(transformElementx);
				parent.appendChild(transformElementx);
			} else if (pos instanceof Integer){
				parent.insertChild(transformElementx, (Integer)pos);
			}
		}
	}

	private void addDictRef() {
		assertRequired(XPATH, xpath);
		assertRequired(VALUE, value);
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			Element element = (Element) node;
			String valuex = (String) evaluateValue(element, value);
			if (valuex != null) {
				element.addAttribute(new Attribute(DICT_REF, valuex));
			}
		}
	}

	private void addId() {
		assertRequired(XPATH, xpath);
		assertRequired(VALUE, value);
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			Element element = (Element) node;
			String valuex = (String) evaluateValue(element, value);
			if (valuex != null) {
				element.addAttribute(new Attribute(ID, valuex));
			}
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
				Element element = (Element) node;
				assertRequired(DICT_REF, dictRef);
				assertRequired(VALUE, value);
				CMLLabel label = new CMLLabel();
				label.setDictRef(dictRef);
				String valuex = (String) evaluateValue(element, value);
				if (valuex != null) {
					label.setCMLValue(value);
					element.appendChild(label);
				}
			}
		}
	}

	private void addLink() {
		assertRequired(XPATH, xpath);
		assertRequired(FROM, from);
		assertRequired(TO, to);
		assertRequired(MAP, map);
		assertRequired(VALUE, value);
		List<Node> nodeList = getXpathQueryResults();
		Nodes mapNodes = TransformElement.queryUsingNamespaces(parsedElement, map);
		CMLMap cmlMap = (mapNodes.size() != 1) ? null : (CMLMap) mapNodes.get(0);
		if (cmlMap == null) {
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
			cmlMap.addLink(link);
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

	private void addMap() {
		assertRequired(XPATH, xpath);
		assertRequired(ID, id);
		assertRequired(TO, to);
		assertRequired(FROM, from);
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			Element element = (Element) node;
			CMLMap map = new CMLMap();
			map.setId(id);
			element.appendChild(map);
			Nodes toNodes = TransformElement.queryUsingNamespaces(element, to);
			Nodes fromNodes = TransformElement.queryUsingNamespaces(element, from);
			if (fromNodes.size() == toNodes.size()) {
				for (int i = 0; i < fromNodes.size(); i++) {
					String fromValue = fromNodes.get(i).getValue();
					String toValue = toNodes.get(i).getValue();
					CMLLink link = new CMLLink();
					link.setFrom(fromValue);
					link.setTo(toValue);
					map.addLink(link);
				}
			}
		}
	}

	private void addNamespace() {
		assertRequired(XPATH, xpath);
		assertRequired(NAME, name);
		assertRequired(VALUE, value);
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			Element element = (Element)node;
			String valuex = (String) evaluateValue(element, value);
			if (valuex != null) {
				element.addNamespaceDeclaration(name, valuex);
			}
		}
	}
	
	private void addSibling() {
		assertRequired(XPATH, xpath);
		assertRequired(ID, id);
		assertRequired(ELEMENT_NAME, elementName);
		assertRequired(POSITION, position);
		Integer pos = new Integer(position);
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			Element element = (Element)node;
			ParentNode parent = element.getParent();
			Elements childElements = ((Element)parent).getChildElements();
			int idx = -1;
			int nchild = childElements.size();
			List<Element> elementList = new ArrayList<Element>();
			for (int i = 0; i < nchild; i++) {
				Element childElement = (Element) childElements.get(i);
				elementList.add(childElement);
				if (childElement.equals(element)) {
					idx = i;
					break;
				}
			}
			int insertionPoint = idx + pos;
			if (insertionPoint >= 0 && insertionPoint <= nchild) {
				Element newElement = createNewElement(elementName, id, null);
				parent.insertChild(newElement, insertionPoint);
			}
		}
	}
	
	private void addUnits() {
		assertRequired(XPATH, xpath);
		assertRequired(VALUE, value);
		
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			if (node instanceof HasUnits) {
				Element element = (Element) node;
				String valuex = (String) evaluateValue(element, value);
				if (valuex != null) {
					element.addAttribute(new Attribute(UNITS, valuex));
				}
			}
		}
	}


	private void copy() {
		assertRequired(XPATH, xpath);
		assertRequired(TO, to);
		List<Node> nodeList = getXpathQueryResults();
		if (nodeList.size() > 0) {
			for (int i = 0; i < nodeList.size(); i++) {
				Element element = (Element)nodeList.get(i);
				Element elementCopy = (element instanceof CMLElement) ? 
						CMLElement.createCMLElement(element) : (Element) element.copy();
				String id = elementCopy.getAttributeValue(ID);
				if (id == null) {
					id = "copy."+i;
				} else {
					id = id+".copy";
				}
				Nodes toNodes = TransformElement.queryUsingNamespaces(element, to);
				ParentNode parent =  null;
				if (toNodes.size() == 1 && toNodes.get(0) instanceof ParentNode) {
					parent = (ParentNode) toNodes.get(0);
				} else if (toNodes.size() == nodeList.size()) {
					parent = (ParentNode) toNodes.get(i);
				}
				if (parent != null) {
					parent.appendChild(elementCopy);
					elementCopy.addAttribute(new Attribute(ID, id));
				}
			}
		}
	}

	private void createAngle() {
		assertRequired(XPATH, xpath);
		assertRequired(ATOM_REFS, atomRefs);
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			Element element = (Element)node;
			String[] aRefs = createAtomRefs(element, atomRefs, 3);
			if (aRefs != null) {
				CMLAngle angle = new CMLAngle();
				angle.setAtomRefs3(aRefs);
				element.appendChild(angle);
				setValue(element, angle);
			}
		}
	}

	private String[] createAtomRefs(Node refNode, String atomRefs, int nrefs) {
		String[] aRefs = atomRefs.split(CMLConstants.S_WHITEREGEX);
		if (aRefs.length != nrefs) {
			throw new RuntimeException("Require "+nrefs+" atomRefs");
		}
		for (int i = 0; i < aRefs.length; i++) {
			Object obj = evaluateValue(refNode, aRefs[i]);
			if (obj == null) {
				aRefs = null;
				break;
			}
			aRefs[i] = obj.toString();
			if (!aRefs[i].startsWith("a")) {
				aRefs[i] = "a"+aRefs[i];
			}
		}
		return aRefs;
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
						try {
							array = new CMLArray(new IntArray(values).getArray());
						} catch (Exception e) {
							LOG.debug("Bad integer array: "+Util.concatenate(values, " "));
						}
					} else if (CMLConstants.XSD_DOUBLE.equals(dataTypex)) {
						try {
							array = new CMLArray(new RealArray(values).getArray());
						} catch (Exception e) {
							debug();
							LOG.debug("Bad real array ("+transformElement.getAttributeValue("id")+") "+Util.concatenate(values, " "));
						}
					} else { 
						array = new CMLArray(values, delimiter);
					}
					if (array != null) {
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
	}

	private void createAtom() {
		assertRequired(XPATH, xpath);
		List<Node> nodeList = getXpathQueryResults();
		for (int i = 0; i < nodeList.size(); i++) {
			Node node = nodeList.get(i);
			if (node instanceof CMLScalar) {
				CMLScalar scalar = (CMLScalar) node;
				String val = scalar.getValue();
				try {
					CMLAtom atom = new CMLAtom();
					atom.setElementType(val);
					atom.setId("a"+(i+1));
					scalar.getParent().replaceChild(scalar, atom);
				} catch (Exception e) {
					LOG.warn("Cannot create atom: "+val+ ""+e);
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
					} else if (format != null) {
						dateTimeDuration = JodaDate.parseDate(val, format);
					} else {
						dateTimeDuration = JodaDate.parseDate(val);
					}
					scalar.setValue(dateTimeDuration.toString());
					if (dictRef != null) {
						scalar.setDictRef(dictRef);
					}
					// create new scalar and replace
					CMLScalar date = new CMLScalar((DateTime)dateTimeDuration);
					date.setDictRef(scalar.getDictRef());
					scalar.getParent().replaceChild(scalar, date);
				} catch (Exception e) {
					LOG.warn("Cannot parse/set date/duration: "+val+ " (format='"+format+"'); "+e);
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
					LOG.warn("bad double: "+e);
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
					LOG.warn("bad formula: "+e);
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

	private void groupSiblings() {
		assertRequired(XPATH, xpath);
		List<Node> nodeList = getXpathQueryResults();
		ParentNode parent = null;
		for (Node node : nodeList) {
			ParentNode parent0 = node.getParent();
			if (parent0 == null) {
				throw new RuntimeException("Siblings must have parents");
			}
			if (parent == null) {
				parent = parent0;
			} else if (!parent.equals(parent0)) {
				throw new RuntimeException("Can only group siblings (i.e. with same parent");
			}
		}
		// use nodes as fenceposts
		
		if (parent != null && nodeList.size() > 1) {
			int start = parent.indexOf(nodeList.get(0));
			int end = parent.indexOf(nodeList.get(nodeList.size()-1));
			List<Node> newNodeList = new ArrayList<Node>();
			for (int i = 0; i < parent.getChildCount(); i++) {
				newNodeList.add(parent.getChild(i));
			}
			int currentNode = 0;
			Element groupParent = null;
			for (int i = start; i < end; i++) {
				Node child = newNodeList.get(i);
				if (child instanceof Element) {
					if (child.equals(nodeList.get(currentNode))) {
						currentNode++;
						if (currentNode >= nodeList.size()) {
							break;
						}
						groupParent = (Element)child;
					} else {
						child.detach();
						groupParent.appendChild(child);
					}
				}
			}
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
					LOG.warn("bad integer: "+e);
				}
			}
		}
	}

	private void createMatrix() {
		assertRequired(DICT_REF, dictRef);
		assertRequired(FROM, from);
		assertRequired(XPATH, xpath);
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			Element element = (Element)node;
			Nodes nodes = TransformElement.queryUsingNamespaces(element, from);
			if (nodes.size() > 0) {
				if (nodes.get(0) instanceof CMLScalar) {
					throw new RuntimeException("Please create matrix from arrays, not scalars");
				} else if ((nodes.get(0) instanceof CMLArray)) {
					addArraysToMatrix(nodes.size(), ((CMLArray)nodes.get(0)).getSize(), nodes);
				} else if (nodes.size() == 0) {
				} else {
					CMLUtil.debug(element, "PARENT");
					throw new RuntimeException("Cannot create matrix found "+nodes.size()+" nodes of type: "+nodes.get(0).getClass());
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
				addScalarsToMatrix(3, 3, nodes);
			} else if (nodes.size() == 3 && (nodes.get(0) instanceof CMLArray)) {
				addArraysToMatrix(3, 3, nodes);
			} else if (nodes.size() == 0) {
			} else {
				CMLUtil.debug(element, "PARENT");
				throw new RuntimeException("Cannot create matrix33 found "+nodes.size()+" nodes of type: "+nodes.get(0).getClass());
			}
		}
	}

	private void addScalarsToMatrix(int rows, int cols, Nodes nodes) {
		int size = rows*cols;
		double[] dd = new double[size];
		Node node0 = null;
		for (int i = 0; i < size; i++) {
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
		CMLMatrix mat = new CMLMatrix(rows, cols, dd);
		mat.setDictRef(dictRef);
		node0.getParent().replaceChild(node0, mat);
	}

	private void addArraysToMatrix(int rows, int cols, Nodes nodes) {
		CMLArray array0 = (CMLArray) nodes.get(0);
		if (array0.getSize() != cols) {
			throw new RuntimeException("Unexpected array size: "+array0.getSize());
		}
		for (int i = 1; i < rows; i++) {
			Node nodex = nodes.get(i);
			if (!(nodex instanceof CMLArray)) {
				throw new RuntimeException("Expected array in 'from', found: "+nodex.getClass());
			}
			CMLArray array = (CMLArray) nodex;
			if (array.getSize() != cols || !(CMLConstants.XSD_DOUBLE.equals(array.getDataType()))) {
				((CMLArray)nodes.get(0)).debug("XXXXXXXXXX");
				((CMLArray)nodes.get(i)).debug("IIIIIIIIII");
				throw new RuntimeException("createMatrix requires arrays of "+cols+" doubles but had "+array.getSize()+" in row "+i);
			}
			array0.append(array);
			array.detach();
		}
		CMLMatrix mat = new CMLMatrix(rows, cols, array0.getDoubles());
		mat.setDictRef(dictRef);
		ParentNode parent = array0.getParent();
		parent.replaceChild(array0, mat);
	}

	private void createLength() {
		assertRequired(XPATH, xpath);
		assertRequired(ATOM_REFS, atomRefs);
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			Element element = (Element)node;
			String[] aRefs = createAtomRefs(element, atomRefs, 2);
			if (aRefs != null) {
				CMLLength length = new CMLLength();
				length.setAtomRefs2(aRefs);
				element.appendChild(length);
				setValue(element, length);
			}
		}
	}

	private void setValue(Element refElement, Element lat) {
		if (value != null) {
			Object valuex = evaluateValue(refElement, value);
			if (valuex != null) {
				// messy - these should be interfaces on the elements
				if (lat instanceof CMLAngle) {
					((CMLAngle)lat).setXMLContent(valuex.toString());
				} else if (lat instanceof CMLLength) {
					((CMLLength)lat).setXMLContent(valuex.toString());
				} else if (lat instanceof CMLTorsion) {
					((CMLTorsion)lat).setXMLContent(valuex.toString());
				}
			}
		}
	}

	private void createList() {
		assertRequired(XPATH, xpath);
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			if (node instanceof CMLModule) {
				CMLModule module = (CMLModule) node;
				CMLList list = new CMLList();
				module.getParent().replaceChild(module, list);
				CMLUtil.copyAttributes(module, list);
				CMLUtil.transferChildren(module, list);
			}
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
				"Molecule requires list of arrays, but found: "+element.getClass().getName()+";"+element.getLocalName());
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
				parent.replaceChild(molecule, array0);
				recordError(array0, parent, "cannot create molecule: "+e);
			}
			addElementTypes();
			addFormulaAndProperties();
			removeExplicitHydrogenCounts(molecule);
		}
	}

	private void recordError(CMLArray array0, ParentNode parent, String errorS) {
		LOG.warn(errorS);
		CMLScalar error = new CMLScalar(errorS);
		error.setDictRef(CML_ERROR);
		parent.appendChild(error);
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
				if (nameNodes.size() == valueNodes.size()) {
					for (int i = 0; i < nameNodes.size(); i++) {
						CMLScalar nameScalar = (CMLScalar)nameNodes.get(i);
						CMLScalar valueScalar = (CMLScalar)valueNodes.get(i);
						createNameValue(nameScalar, valueScalar);
					}
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
		} else if (nodeList.size() == 1 && nodeList.get(0) instanceof Text) {
			Node text = nodeList.get(0);
			CMLScalar scalar = new CMLScalar(text.getValue());
			if (id != null) {
				scalar.setId(id);
			}
			text.getParent().replaceChild(text, scalar);
			
		} else {
			for (Node node : nodeList) {
				if (node instanceof CMLArray) {
					CMLArray array = (CMLArray) node;
					CMLScalar scalar = new CMLScalar(array.getValue());
					scalar.setDictRef(array.getDictRef());
					array.getParent().replaceChild(array, scalar);
				}
			}
		}
	}

	private void createTable() {
		assertRequired(XPATH, xpath);
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			Element element = (Element)node;
			if (element instanceof CMLList) {
				CMLTable table = new CMLTable();
				table.setTableType(TableType.COLUMN_BASED);
				Elements elements = element.getChildElements();
				for (int i = 0; i < elements.size(); i++) {
					Element childElement = elements.get(i);
					if (!(childElement instanceof CMLArray)) {
						throw new RuntimeException("list must only have array children");
					}
					table.addArray(new CMLArray((CMLArray)childElement));
				}
				element.getParent().replaceChild(element, table);
			}
		}
	}

	private void createTorsion() {
		assertRequired(XPATH, xpath);
		assertRequired(ATOM_REFS, atomRefs);
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			Element element = (Element)node;
			String[] aRefs = createAtomRefs(element, atomRefs, 4);
			if (aRefs != null) {
				CMLTorsion torsion = new CMLTorsion();
				torsion.setAtomRefs4(aRefs);
				element.appendChild(torsion);
				setValue(element, torsion);
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
//		String[] dictRefNames = splitDictRef();
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
				v3.setDictRef(dictRef);
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
			Element element = (Element) templateElement.copy();
			if (parent != null) {
				parent.replaceChild(node, element);
			} else {
//				element.copyNamespaces((CMLElement) node);
			}
			if (node instanceof Text) {
				String normalized = Util.normaliseWhitespace(node.getValue().trim());
				String[] strings = normalized.split(CMLConstants.S_WHITEREGEX);
				CMLArray array = new CMLArray(strings);
				node = array;
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
  <module id="moleculeRoot">
    <list id="atom1">
      <atom elementType="C" id="a1"/>
    </list>
    <list type="molecule">
      <atom elementType="X" id="a2"/>
      <length atomRefs2="a2 a1">1.0</length>
    </list>
    <list type="molecule">
      <atom elementType="H" id="a3"/>
      <length atomRefs2="a3 a1">1.07046</length>
      <angle atomRefs3="a3 a1 a2">90.08384</angle>
    </list>
    <list type="molecule">
      <atom elementType="H" id="a4"/>
      <length atomRefs2="a4 a1">1.07046</length>
      <angle atomRefs3="a4 a1 a2">90.08384</angle>
      <torsion atomRefs4="a4 a1 a2 a3">120.0</torsion>
    </list>
    <list type="molecule">
      <atom elementType="H" id="a5"/>
      <length atomRefs2="a5 a1">1.07046</length>
      <angle atomRefs3="a5 a1 a2">90.08384</angle>
      <torsion atomRefs4="a5 a1 a2 a3">-120.0</torsion>
    </list>
  </module>
 */
		assertRequired(XPATH, xpath);
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			Element element = (Element) node;
			// only process molecules without cartesians
			Nodes childNodes = TransformElement.queryUsingNamespaces(element, 
			".//cml:atom[not(@x3) or not(@y3) or not(@z3)] | .//cml:angle | .//cml:length | .//cml:torsion");
			if (childNodes.size() > 0) {
				createZMatrix(element, childNodes);
			}
		}
	}

	private void createZMatrix(Element element, Nodes childNodes) {
		CMLZMatrix zMatrix = new CMLZMatrix();
		CMLMolecule molecule = new CMLMolecule();
		molecule.appendChild(zMatrix);
		for (int i = 0; i < childNodes.size(); i++) {
			Node childNode = childNodes.get(i);
			if (childNode instanceof CMLAtom) {
				CMLAtom atom = (CMLAtom)childNode;
				molecule.addAtom(new CMLAtom(atom));
			} else if (childNode instanceof CMLAngle) {
				CMLAngle angle = (CMLAngle) childNode;
				zMatrix.addAngle(new CMLAngle(angle));
			} else if (childNode instanceof CMLLength) {
				CMLLength length = (CMLLength) childNode;
				zMatrix.addLength(new CMLLength(length));
			} else if (childNode instanceof CMLTorsion) {
				CMLTorsion torsion = (CMLTorsion) childNode;
				zMatrix.addTorsion(new CMLTorsion(torsion));
			} 
		}
		zMatrix.addCartesiansTo(molecule);
		element.appendChild(molecule);
		molecule.setId(id);
	}

//	private ParentNode ensureParent(Element element) {
//		ParentNode parent = element.getParent();
//		if (parent == null) {
//			// this is a fudge - only required for test examples which have no parent
//			parent = element;
//		}
//		return parent;
//	}

	private void debugNodes() {
		assertRequired(XPATH, xpath);
		String title = (name == null) ? "debugNodes" : name;
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			debug(title, node);
		}
	}

	private void delete() {
		assertRequired(XPATH, xpath);
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			if (node.getParent() != null && node.getParent() instanceof Element) {
				node.detach();
			}
		}
	}

	private void joinArrays() {
		assertRequired(XPATH, xpath);
		List<Node> nodeList = getXpathQueryResults();
		if (key != null) {
			joinArrays(nodeList, key);
		} else if (from != null) {
			Integer fromPos = (fromPosition == null) ? null : new Integer(fromPosition);
			for (Node node : nodeList) {
				Element element = (Element) node;
				List<Node> fromNodeList = TransformElement.queryUsingNamespaces(element, from, fromPos);
				joinArrays(fromNodeList);
			}
		} else {
			joinArrays(nodeList);
		}
	}

	private void joinArrays(List<Node> nodeList, String key) {
		Map<String, List<Node>> nodeListByKey = new HashMap<String, List<Node>>();
		for (int i = 0; i < nodeList.size(); i++) {
			Node node = nodeList.get(i);
			String keyx = (String) evaluateValue(node, key);
			if (keyx != null) {
				List<Node> list = nodeListByKey.get(keyx);
				if (list == null) {
					list = new ArrayList<Node>();
					nodeListByKey.put(keyx,list);
				}
				list.add(node);
			}
		}
		Set<String> keySet = nodeListByKey.keySet();
		for (String keyz : keySet) {
			List<Node> list = nodeListByKey.get(keyz);
			joinArrays(list);
		}
	}

	private void joinArrays(List<Node> nodeList) {
		if (nodeList.size() > 1 && nodeList.get(0) instanceof CMLArray) {
			CMLArray baseArray = (CMLArray) nodeList.get(0);
			String dictRefx = (dictRef != null) ? dictRef : baseArray.getDictRef();
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
			newArray.setDictRef(dictRefx);
			baseArray.getParent().replaceChild(baseArray, newArray);
		}
	}

	
	private void move() {
		assertRequired(XPATH, xpath);
		assertRequired(TO, to);
		List<Node> nodeList = getXpathQueryResults();
		Nodes toParents = TransformElement.queryUsingNamespaces(parsedElement, to);
		ParentNode toParent = (toParents.size() == 1) ? (ParentNode) toParents.get(0) : null;
		if (toParent != null) {
			for (Node node : nodeList) {
				if (node instanceof Element && !(node.equals(toParent))) {
					// position counts from ONE
					Integer pos = (position == null) ? null : new Integer(position)-1;
					int toChildCount = toParent.getChildCount();
					if (position == null || pos == toChildCount) {
						node.detach();
						toParent.appendChild(node);
					} else if (pos >= 0 && pos <= toChildCount) {
						node.detach();
						toParent.insertChild(node, pos);
					} else {
						LOG.trace("move position out of range "+pos+"; toChildCount  "+toChildCount);
					}
				}
			}
		} else {
			LOG.debug("null toXpath: "+to);
		}
	}
	
	private void moveRelative() {
		assertRequired(XPATH, xpath);
		assertRequired(TO, to);
		Integer pos = (position == null) ? null : new Integer(position)-1;
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			if (node instanceof Element) {
				Element fromElement = (Element) node;
				Nodes toNodes = TransformElement.queryUsingNamespaces(fromElement, to);
				if (toNodes.size() == 1) {
					Element toElement = (Element) toNodes.get(0);
					// position counts from ONE
					if (position == null || pos == toElement.getChildCount()) {
						node.detach();
						toElement.appendChild(node);
					} else if (pos >= 0 && pos <= toElement.getChildCount()) {
						node.detach();
						toElement.insertChild(node, pos);
					} else {
						throw new RuntimeException("Cannot move ");
					}
				}
			}
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
				if (grandParent != null && !(grandParent instanceof Document)) {
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
				child.detach();
				nameElement.getParent().replaceChild(nameElement, child);
				if (nameElement instanceof CMLElement) {
					String templateRef = ((CMLElement)nameElement).getCMLXAttribute(Template.TEMPLATE_REF);
					if (templateRef != null) {
						CMLElement.addCMLXAttribute(child, Template.TEMPLATE_REF, templateRef);
					}
				}
			}
		}
	}

	private void read() {
		assertRequired(INPUT, input);
		assertRequired(TO, to);
		assertRequired(ID, id);
		Element element = readElementSomehow(input);
			if (element != null) {
				Nodes toNodes = TransformElement.queryUsingNamespaces(element, to);
				if (toNodes.size() == 1) {
					((ParentNode)toNodes.get(0)).appendChild(element);
				}
			}
	}

	private Element readElementSomehow(String inputx) {
		Element element = null;
		InputStream is = null;
		try {
			is = new FileInputStream(inputx);
		} catch (Exception e) {
			LOG.warn("Cannot read url "+input);
		}
		if (is == null) {
			try {
				is = new URL(input).openStream();
			} catch (Exception e) {
				LOG.warn("Cannot read url "+input);
			}
		}
		if (is == null) {
			try {
				is = Util.getResourceUsingContextClassLoader(input, TransformElement.class);
			} catch (Exception e) {
				LOG.warn("Cannot read resource "+input);
			}
		}
		try {
			element = new CMLBuilder().build(is).getRootElement();
		} catch (Exception e) {
			LOG.warn("Cannot parse as element "+input);
		}
		return element;
	}

	private void rename() {
		throw new RuntimeException("rename NYI");
	}

	private void reparse() {
		if (template == null) {
			System.err.println("*** Must have a template for reparse ***");
			return;
		}
		assertRequired(XPATH, xpath);
		String regexS = getRegex();
		Element recordReaderElement = new Element("record");
		recordReaderElement.addAttribute(new Attribute("id", "foo"));
		recordReaderElement.appendChild(regexS);
		RecordReader recordReader = new RecordReader(recordReaderElement, template);
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			if (node instanceof Element) {
				Element element = (Element) node;
				if (element.getChildCount() == 1 && element.getChild(0) instanceof Text) {
					String value = element.getValue();
					LineContainer lineContainer = new LineContainer(value);
					recordReader.applyMarkup(lineContainer);
					Element linesElement = lineContainer.getLinesElement();
					ParentNode parent = (Element) element.getParent();
					parent.replaceChild(element, linesElement);
				}
			}
		}
	}

	private String getRegex() {
		if (regex == null && regexPath == null) {
			throw new RuntimeException("Must give regex or regexPath");
		}
		if (regex != null && regexPath != null) {
			throw new RuntimeException("Must give one of regex or regexPath");
		}
		String regexS = regex;
		if (regexPath != null) {
			Element ancestorTemplateElement = (Element) transformElement.query("./ancestor::template[1]").get(0);
			Nodes nodes = ancestorTemplateElement.query(regexPath);
			if (nodes.size() != 1) {
				throw new RuntimeException("cannot find regexPath node");
			}
			regexS = nodes.get(0).getValue();
		}
		return regexS;
	}

	private void setDataType() {
		assertRequired(XPATH, xpath);
		assertRequired(VALUE, value);
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			Element element = (Element) node;
			String valuex = (String) evaluateValue(node, value);
			Element copyElement = null;
			if (CMLConstants.XSD_DOUBLE.equals(valuex)) {
				if (element instanceof CMLScalar) {
					CMLScalar scalar = (CMLScalar) element;
					copyElement = new CMLScalar(Real.parseDouble(scalar.getValue()));
				} else if (element instanceof CMLArray) {
					CMLArray array = (CMLArray) element;
					copyElement = new CMLArray(new RealArray(array.getStrings()));
				}
			} else if (CMLConstants.XSD_INTEGER.equals(valuex)) {
				if (element instanceof CMLScalar) {
					CMLScalar scalar = (CMLScalar) element;
					copyElement = new CMLScalar(Integer.parseInt(scalar.getValue()));
				} else if (element instanceof CMLArray) {
					CMLArray array = (CMLArray) element;
					copyElement = new CMLArray(new IntArray(array.getStrings()).getArray());
				}
			} else if (CMLConstants.XSD_STRING.equals(valuex)) {
				if (element instanceof CMLScalar) {
					CMLScalar scalar = (CMLScalar) element;
					copyElement = new CMLScalar(scalar.getValue());
				} else if (element instanceof CMLArray) {
					CMLArray array = (CMLArray) element;
					copyElement = new CMLArray(array.getStrings());
				}
			}
			if (copyElement != null) {
				((HasDictRef)copyElement).setDictRef(((HasDictRef)element).getDictRef());
				element.getParent().replaceChild(element, copyElement);
			}
		}
	}


	private void setValue() {
		assertRequired(XPATH, xpath);
		assertRequired(VALUE, value);
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			String valuex = (String) evaluateValue(node, value);
			if (map != null) {
				Nodes mapNodes = TransformElement.queryUsingNamespaces(node, map);
				CMLMap mapElement = (mapNodes.size() == 1) ? (CMLMap) mapNodes.get(0) : null;
				valuex = lookupInMap(mapElement, valuex);
			}
			if (valuex != null) {
				if (node instanceof Attribute) {
					Attribute att = (Attribute) node;
					att.setValue(valuex);
				} else if (node instanceof Element) {
					Element element = (Element) node;
					if (element.getChildElements().size() == 0) {
						// clean old text children
						for (int j = 0; j < element.getChildCount(); j++) {
							element.getChild(0).detach();
						}
						element.appendChild(valuex);
					}
				} else {
					throw new RuntimeException("Cannot set value on: "+node.getClass().getName());
				}
			}
		}
	}

	private String lookupInMap(CMLMap map, String valuex) {
		String value = valuex;
		if (map != null && valuex != null) {
			CMLLink link = map.getLink(valuex, Direction.FROM);
			value = (link != null) ? link.getTo() : valuex;
		}
		return value;
	}

	private void split() {
		assertRequired(XPATH, xpath);
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			if (node instanceof CMLArray) {
				splitArray((CMLArray)node);
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

	private void splitArray(CMLArray array) {
		CMLList list = null;
		if (cols != null || rows != null) {
			Integer icols = createInteger(array, cols);
			Integer irows = createInteger(array, rows);
			list = splitToArrays(array, irows, icols);
		} else {
			list = splitToScalars(array);
		}
		if (list != null) {
			array.getParent().replaceChild(array, list);
		}
	}

	private Integer createInteger(Node node, String val) {
		Integer ival = null;
		if (val != null && node != null) {
			Object oval = evaluateValue(node, val);
			if (oval != null) {
				try {
					ival = new Integer(oval.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return ival;
	}

	private CMLList splitToScalars(CMLArray array) {
		int size = array.getSize();
		CMLList list = new CMLList();
		for (int j = 0; j < size; j++) {
			CMLScalar scalar = array.getElementAt(j);
			if (dictRef != null) {
				scalar.setDictRef(dictRef);
			}
			list.appendChild(scalar);
		}
		return list;
	}

	private CMLList splitToArrays(CMLArray array, Integer irows, Integer icols) {
		if (irows == null && icols == null) {
			return null;
		}
		int size = array.getSize();
		CMLList list = new CMLList();
		if (icols != null) {
			if (icols <= 0 || size%icols != 0) {
				throw new RuntimeException("cols ("+icols+") must be factor of array ("+size+")");
			}
			if (irows == null) {
				irows = size/icols;
			} else if (icols * irows != size) {
				throw new RuntimeException("rows ("+irows+") * cols ("+cols+") != size ("+size+")");
			}
		} else {
			if (irows <= 0 || size%irows != 0) {
				throw new RuntimeException("rows ("+irows+") must be factor of array ("+size+")");
			}
			icols = size/irows;
		}
		for (int j = 0; j < size; j+=icols) {
			CMLArray subArray = new CMLArray(array.createSubArray(j, j+icols-1));
			list.appendChild(subArray);
		}
		return list;
	}

	
	
	private void wrapWithPropertyOrParameter(Element wrapperx) {
		assertRequired(XPATH, xpath);
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			if (node instanceof CMLScalar ||
					node instanceof CMLArray ||
					node instanceof CMLList ||
					node instanceof CMLTable ||
					node instanceof CMLMatrix) {
				Element wrapper = (Element) wrapperx.copy();
				Element element = (Element) node;
				String dictRefx = (dictRef != null) ? dictRef : element.getAttributeValue(DICT_REF);
				if (dictRefx == null) {
					throw new RuntimeException("Cannot find dictRef for wrapper "+element.toXML());
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
			Elements elements = pp.getChildElements();
			for (int j = 0; j < elements.size(); j++) {
				Element element = elements.get(j);
				if (
						element instanceof CMLArray ||
						element instanceof CMLScalar ||
						element instanceof CMLList ||
						element instanceof CMLMatrix ||
						element instanceof CMLTable
					) {
					CMLElement wrapper = (pp instanceof CMLPropertyList) ? new CMLProperty() : new CMLParameter();
					ParentNode parent = element.getParent();
					parent.replaceChild(element, wrapper);
					((HasDictRef)wrapper).setDictRef(element.getAttributeValue(DICT_REF));
					wrapper.appendChild(element);
				}
			}
		}
	}
	
	private void write() {
		// TODO Auto-generated method stub
		
	}



// ==================== end of methods ============	

	/**
	 * refNode is explicit reference node (usually from "from" attribute).
	 * if null uses parsedElement.
	 */
	private static Object evaluateValue(Node refNode, String valuex) {
		Object result = null;
		if (valuex == null ) {
			return result;
		}
		Pattern PATTERN = Pattern.compile("\\$((?:string)|(?:substring)|(?:number))\\(([^\\)]*)\\)");
		Matcher matcher = PATTERN.matcher(valuex);
		if (matcher.matches()) {
			Object obj =  evaluateValue0(refNode, matcher.group(1), matcher.group(2));
			return obj;
		}
		String ss = valuex;
		while (true) {
			matcher = PATTERN.matcher(ss);
			if (matcher.find()) {
				int start = matcher.start();
				int end = matcher.end();
				String g1 = matcher.group(1);
				String g2 = matcher.group(2);
				String value = (String) evaluateValue0(refNode, g1, g2);
				String prefix = ss.substring(0, start);
				String suffix = ss.substring(end);
				ss = prefix + value + suffix;
			} else {
				break;
			}
		}
		return ss;
	}

	private static Object evaluateValue0(Node refNode, String function, String xpath) {
		Object result = null;
		if (function != null) {
			if (function.equals(STRING)) {
				result = getValue(refNode, xpath);
			} else if (function.equals(NUMBER)) {
				String v = getValue(refNode, xpath);
				try {
					result = new Integer(v);
				} catch (Exception e) {
					try {
						if (result == null) {
							result = new Double(v);
						}
					} catch (Exception e1) {
						// fail silently
					}
				}
			} else if (function.equals(SUBSTRING)) {
				Integer end = null;
				Integer start = null;
				String[] ss = xpath.split(CMLConstants.S_COMMA);
				if (ss.length > 3 || ss.length < 2) {
					throw new RuntimeException("need 2 or 3 args for substring");
				}
				if (ss.length == 3) {
					end = new Integer(ss[2].trim());
				}
				start = new Integer(ss[1].trim());
				String v = getValue(refNode, ss[0]);
				if (end != null) {
					v = v.substring(start, end);
				} else {
					v = v.substring(start);
				}
				result = v;
			}
		}
		return result;
	}

	private static String getValue(Node refNode, String xpath) {
		Nodes nodes = TransformElement.queryUsingNamespaces(refNode, xpath);
		String v = (nodes.size() == 0) ? null : nodes.get(0).getValue();
		return v;
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
	
	protected void checkProcessExists(String processName) {
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

	private void ifTest() {
		assertRequired(TEST, test);
		Nodes testNodes = TransformElement.queryUsingNamespaces(transformElement, test);
		if (testNodes.size() > 0) {
			LOG.warn("IF NYI");
			Elements childElements = transformElement.getChildElements();
			for (int i = 0; i < childElements.size(); i++) {
				System.out.println("CH "+childElements.get(i)+" NYI");
			}
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
//			String id = element.getAttributeValue(ID);
	    	try {
	    		checkDictRefAttval(att);
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
//			for (Element element : dictRefNodes.get(s)) {
////				CMLUtil.debug(element, "QQQQQQQQ");
//			}
		}
	}

	private void checkDictRefAttval(Attribute att) {
		DictRefAttribute dictRefAtt = new DictRefAttribute(att);
		String prefix = dictRefAtt.getPrefix();
		String value = DictRefAttribute.getLocalName(dictRefAtt.getValue());
		String namespace = template == null ? null : template.templateElement1.getNamespaceURI(prefix);
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

	private void debug(String title, Node node) {
		if (node instanceof Text) {
			LOG.debug(title+": "+"text in "+getContextId()+": "+node.getValue());
		} else if (node instanceof Element){
			Element element = (Element) node;
			Nodes texts = element.query("./text()");
			Nodes nodes = element.query("./node()");
			Nodes elems = element.query("./*");
			LOG.debug("text: "+texts.size()+"; node: "+nodes.size()+"; elem: "+elems.size());
			if (texts.size() > 0 && nodes.size()>1) {
				for (int i = 0; i < nodes.size(); i++) {
					Node nodex = nodes.get(i);
					if (nodex instanceof Element) {
//						CMLUtil.debug((Element)nodex, title);
					}
				}
			}
			CMLUtil.debug(element, title+": "+xpath+" in "+getContextId());
		}
	}

	private String getContextId() {
		String idString = (template == null) ? null : template.getId();
		if (idString == null) {
			Nodes idNodes = transformElement.query("./ancestor::*"); 
			idString = "";
			for (int i = 0; i < idNodes.size(); i++) {
				String id = ((Element)idNodes.get(i)).getAttributeValue(ID);
				idString += "/"+ (id == null ? "" : id);
			}
		}
		return idString;
	}

	private Element createNewElement(String elementName, String id, String dictRef) {

		String[] names = elementName.split(CMLConstants.S_COLON);
		String prefix = (names.length == 2) ? names[0] : null;
		String local = (names.length == 2) ? names[1] : elementName;
		Element newElement = null;
		try {
			newElement = new Element(local);
		} catch (Exception e) {
//			CMLUtil.debug(this.transformElement, "BAD ELEMENT");
			throw new RuntimeException("Cannot create element: "+newElement, e);
		}
		if (prefix == null) {
			newElement = new Element(local);
		} else if (prefix.equals(CMLConstants.CML_PREFIX)) {
			newElement = new CMLElement(local);
		} else {
			String namespaceURI = parsedElement.getNamespaceURI(prefix);
			if (namespaceURI == null) {
				throw new RuntimeException("no namespace given for: "+prefix);
			}
			newElement = new Element(elementName, namespaceURI);
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

	protected List<Node> getXpathQueryResults() {
		assertRequired(XPATH, xpath);
		Nodes nodes = TransformElement.queryUsingNamespaces(parsedElement, xpath);
		List<Node> nodeList = new ArrayList<Node>();
		for (int i = 0; i < nodes.size(); i++) {
			nodeList.add(nodes.get(i));
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

	protected void assertRequired(String attName, String attVal) {
		if (attVal == null) {
//			CMLUtil.debug(this.transformElement, "BAD Transform, missing "+attName);
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

	public static Nodes queryUsingNamespaces(Node node, String xpath) {
		if (xpath == null) {
			throw new RuntimeException("null xpath attribute");
		}
		if (node == null) {
			throw new RuntimeException("null node in query");
		}
		xpath = (String) evaluateValue(node, xpath);
		Nodes nodes = null;
		try {
			nodes = node.query(xpath, Template.CML_CMLX_CONTEXT);
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

//	private String[]  splitDictRef() {
//		String[] dictRefNames = dictRef.split(CMLConstants.S_COLON);
//		if (dictRefNames.length != 2) {
//			throw new RuntimeException("dictRef must be qualified name; found: "+dictRef);
//		}
//		return dictRefNames;
//	}
	
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
				scalar.setDictRef(DictRefAttribute.createValue(COMPCHEM_CC, ATOMIC_NUMBER));
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
//		int size = array.getSize();
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
			throw new RuntimeException("problem in adding Scalar doubles", e);
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
		CMLUtil.debug(transformElement, "IN "+((Element)transformElement.getParent()).getAttributeValue("id"));
//		CMLUtil.debug(this.element, "DEBUG");
	}
}
