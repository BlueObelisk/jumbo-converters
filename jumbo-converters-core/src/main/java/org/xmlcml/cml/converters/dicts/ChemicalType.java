package org.xmlcml.cml.converters.dicts;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.element.CMLEntry;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLReaction;
import org.xmlcml.cml.element.CMLSubstance;
import org.xmlcml.euclid.Util;

public class ChemicalType implements CMLConstants {
	private static Logger LOG = Logger.getLogger(ChemicalType.class);

	public enum Type {
		ANALYTICAL("analytical"),
		BIO("bio"),
		COMPCHEM("compchem"),
		COMPOUND("compound"),
		GENERAL("general"),
		GROUP("group"),
		INSTRUMENT("instrument"),
		NONCHEMICAL("nonchemical"),
		PREPARATIVE("preparative"),
		QUANTITY("quantity"),
		REACTION("reaction"),
		REAGENT("reagent"),
		SOLVENT("solvent"),
		SPECTRAL("spectral"),
		;
		public String value;
		private Type(String s) {
			value = s;
		}
	};
	
	public final static String RESOURCE = "org/xmlcml/cml/converters/dicts";
//	public final static String RESOURCE = "org/xmlcml/cml/legacy2cml/resources";

	private static Map<String, ChemicalTypeMap> typeMapMap = null;
	public static ChemicalTypeMap getTypeMap(String type) {
		if (typeMapMap == null) {
			typeMapMap = new HashMap<String, ChemicalTypeMap>();
			makeMap(Type.ANALYTICAL.value, EntryType.class, RESOURCE);
			makeMap(Type.BIO.value, EntryType.class, RESOURCE);
			makeMap(Type.COMPCHEM.value, EntryType.class, RESOURCE);
			makeMap(Type.COMPOUND.value, MoleculeType.class, RESOURCE);
			makeMap(Type.GENERAL.value, EntryType.class, RESOURCE);
			makeMap(Type.GROUP.value, GroupType.class, RESOURCE);
			makeMap(Type.INSTRUMENT.value, EntryType.class, RESOURCE);
			makeMap(Type.NONCHEMICAL.value, EntryType.class, RESOURCE);
			makeMap(Type.PREPARATIVE.value, EntryType.class, RESOURCE);
			makeMap(Type.QUANTITY.value, UnitTypeType.class, RESOURCE);
			makeMap(Type.REACTION.value, ReactionType.class, RESOURCE);
			makeMap(Type.REAGENT.value, SubstanceType.class, RESOURCE);
			makeMap(Type.SOLVENT.value, SubstanceType.class, RESOURCE);
			makeMap(Type.SPECTRAL.value, EntryType.class, RESOURCE);
//			for (String s : typeMapMap.keySet()) {
//				LOG.debug("....... "+s);
//			}
		}
//		debugMap(Type.GROUP.value);
		ChemicalTypeMap map = typeMapMap.get(type);
		if (map == null) {
			LOG.debug("NULL map "+type);
		}
		return map;
	}
	
	public static void debugMap(String type) {
		Map<String, ChemicalType> map = typeMapMap.get(type);
		List<String> keyList = new ArrayList<String>();
		for (String key : map.keySet()) {
			keyList.add(key);
		}
		Collections.sort(keyList);
		for (String key : keyList) {
			LOG.debug(">> "+key);
		}
	}
	
	static void makeMap(String type, Class<?> classx, String resourceDir) {
		ChemicalTypeMap map = new ChemicalTypeMap();
		makeMap(type, classx, map, resourceDir);
		typeMapMap.put(type, map);
	}
	
	private static void makeMap(String type, Class<?> classx, ChemicalTypeMap map, String resource) {
		Element element = null;
		try {
			String res = resource+U_S+type+".xml";
			InputStream in = Util.getInputStreamFromResource(res);
			element =  new CMLBuilder().build(in).getRootElement();
			in.close();
		} catch (Exception ioe) {
			System.err.println("SERIOUS HYDROGEN COUNTING ERROR IN SMILES; NEED BUG FIX: "+ioe);
		}
		if (element != null) {
			for (int i = 0; i < element.getChildCount(); i++) {
				Node child = element.getChild(i);
				if (child instanceof Element) {
					createType(classx, (Element) child, map);
				}
			}
		}
	}
	
	protected Element element;
	protected ChemicalTypeMap map;
	protected String value;
	
	/** 
	 */
	protected ChemicalType() {
	}
	
	public ChemicalType(Element element) { 
		this.element = element;
	}
	
	public ChemicalType(Element element, ChemicalTypeMap map) {
		this.element = element;
		this.map = map;
	}
	
	public static ChemicalType createType(Class<?> classx, Element element, ChemicalTypeMap map) {
		ChemicalType type = null;
		if (classx == null) {
			throw new RuntimeException("Null class in Chemical Type");
		} else if(EntryType.class.equals(classx) && element instanceof CMLEntry) {
			type = new EntryType((CMLEntry)element, map);
		} else if(GroupType.class.equals(classx) && element instanceof CMLMolecule) {
			type = new GroupType((CMLMolecule) element, map);
		} else if(MoleculeType.class.equals(classx) && element instanceof CMLMolecule) {
			type = new MoleculeType((CMLMolecule)element, map);
		} else if(ReactionType.class.equals(classx) && element instanceof CMLReaction) {
			type = new ReactionType((CMLReaction)element, map);
		} else if(SubstanceType.class.equals(classx) && element instanceof CMLSubstance) {
			type = new SubstanceType((CMLSubstance)element, map);
		} else if(UnitTypeType.class.equals(classx)) {
			type = new UnitTypeType(element, map);
		} else {
			throw new RuntimeException("bad typename ("+classx+") or element ("+element.getClass()+") ("+element.getLocalName()+")");
		}
		return type;
	}

	protected void addToMap(Element element, ChemicalTypeMap map) {
		Nodes nodes = element.query("value");
		if (nodes.size() > 0) {
			value = nodes.get(0).getValue();
		}
		map.put(value, this);
	}
	
	public String getValue() {
		return value;
	}
	protected void addNamesToMap(Element element, ChemicalTypeMap map) {
		Nodes nodes = element.query("cml:name", CML_XPATH);
		for (int i = 0; i < nodes.size(); i++) {
			map.put(nodes.get(i).getValue(), this);
		}
	}
	
	public void debug(String msg) {
	}
}