package org.xmlcml.cml.converters.reaction.properties;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nu.xom.XPathContext;

import org.xmlcml.cml.converters.rdf.rdf.RDFConstants;

public class MMReConstants {

	public final static String MICRO = "&#956;";
	public final static String UNIT_NS = "http://www.xml-cml.org/units";
	public final static String UNIT_NS_PREFIX = "unit";
	public final static String UNIT_ATTNAME = "units";
	
	public enum Unit {
		CELSIUS("unit:celsius"),
		
		MILLIGRAM("unit:mg"),
		GRAM("unit:gram"),
		
		MILLILITRE("unit:ml"),
		MICROLITRE("unit:microl"),
		
		MICROMOL("unit:micromol"),
		MMOL("unit:mmol"),
		MOL("unit:mol"),
		
		PERCENT("unit:percent"),
		
		MIN("unit:min"),
		HOUR("unit:hour"),
		UNKNOWN("unit:unknown"),
		;
		public String value;
		private Unit(String s) {
			this.value = s;
		}
		public static Unit getUnit(String v) {
			Unit p = null;
			for (Unit pp : Unit.values()) {
				if (pp.value.equals(v)) {
					p = pp;
					break;
				}
			}
			return p;
		}
	}

	public enum DictRef {
		COLOR("cml:color"),
		PRESSURE("cml:pressure"),
		TEMPERATURE("cml:temp"),
		TIME("cml:time")
		;
		public String value;
		private DictRef(String s) {
			this.value = s;
		}
	}

	public enum Predicate {
		HAS_MASS("hasGram", "cml:mass"),
		HAS_VOL("hasVol", "cml:volume"),
		HAS_MOLAR_AMOUNT("hasMol", "cml:molarAmount"),
		HAS_PERCENT("hasPercent", "cml:percent"),
		
		HAS_AMOUNT("hasAmount"),
		HAS_COLOR("hasColor", "cml:color"),
		HAS_CHROMATOGRAPHY("hasChromatography", "cml:chromatography"),
		IS_EXTRACTED_BY("isExtractedBy", "cml:extraction"),
		HAS_FILTER_PHRASE("hasFilter-Phrase", "cml:filter"),
		IS_FILTERED_BY("isFilteredBy", "cml:filter"),
		HAS_NAME("hasName"),
		HAS_NUMBER("hasNumber", "cml:serialNumber"),
		HAS_PREPARATION("hasPreparation"),
		HAS_PRODUCT("hasProduct"),
		IS_CONCENTRATED_BY("isConcentrateedBy", "cml:concentrated"),
		IS_PURIFIED_BY("isPurifiedBy", "cml:purified"),
		IS_WASHED_BY("isWashedBy", "cml:washed"),
		HAS_REACTANT("hasReactant"),
		HAS_ROLE("hasRole"),
		HAS_STATE("hasState", "cml:state"),
		HAS_SUBSTANCE("hasSubstance"),
		HAS_PRESSURE("hasPressure", "cml:pressure"),
		HAS_TEMP("hasTemp", "cml:temp"),
		HAS_TIME("hasTime", "cml:time"),
		HAS_UNIT("hasUnit"),
		HAS_VALUE("hasValue"),
		;
		public final String value;
		public final String dictRef;
		private Predicate(String s) {
			this.value = s;
			this.dictRef = null;
		}
		private Predicate(String s, String dictRef) {
			this.value = s;
			this.dictRef = dictRef;
		}
		public static Predicate getPredicate(String v) {
			Predicate p = null;
			for (Predicate pp : Predicate.values()) {
				if (pp.value.equals(v)) {
					p = pp;
					break;
				}
			}
			return p;
		}
		public boolean isAmount() {
			return (this.equals(HAS_MASS) || 
					this.equals(HAS_MOLAR_AMOUNT) ||
					this.equals(HAS_VOL) ||
				this.equals(HAS_PERCENT));
		}
	}
	
	public final static Map<Predicate, Set<Unit>> UNIT_TYPE_MAP = new HashMap<Predicate, Set<Unit>>();
	static {
		Set<Unit> unitSet = null;
		unitSet = new HashSet<Unit>();
		unitSet.add(Unit.GRAM);
		UNIT_TYPE_MAP.put(Predicate.HAS_MASS, unitSet);
		unitSet = new HashSet<Unit>();
		unitSet.add(Unit.MILLILITRE);
		UNIT_TYPE_MAP.put(Predicate.HAS_VOL, unitSet);
		unitSet = new HashSet<Unit>();
		unitSet.add(Unit.MOL);
		UNIT_TYPE_MAP.put(Predicate.HAS_MOLAR_AMOUNT, unitSet);
		unitSet = new HashSet<Unit>();
		unitSet.add(Unit.PERCENT);
		UNIT_TYPE_MAP.put(Predicate.HAS_PERCENT, unitSet);
	}
	
	public final static Map<String, Unit> UNIT_MAP = new HashMap<String, Unit>();
	static {
		UNIT_MAP.put("ml", Unit.MILLILITRE);
		UNIT_MAP.put("mL", Unit.MILLILITRE);
		UNIT_MAP.put("µL", Unit.MICROLITRE);
		UNIT_MAP.put(MICRO+"L", Unit.MICROLITRE);
		UNIT_MAP.put(MICRO+"l", Unit.MICROLITRE);
		
		UNIT_MAP.put("mg", Unit.MILLIGRAM);
		UNIT_MAP.put("g", Unit.GRAM);
		UNIT_MAP.put("gram", Unit.GRAM);
		
		UNIT_MAP.put("hr", Unit.HOUR);
		UNIT_MAP.put("hour", Unit.HOUR);
		UNIT_MAP.put("hours", Unit.HOUR);
		
		UNIT_MAP.put("mmol", Unit.MMOL);
		UNIT_MAP.put("mol", Unit.MOL);
		UNIT_MAP.put("?mol", Unit.MICROMOL);
		UNIT_MAP.put("&#956;mol", Unit.MICROMOL);
		
		UNIT_MAP.put("Celsius", Unit.CELSIUS);
		UNIT_MAP.put("celsius", Unit.CELSIUS);
		UNIT_MAP.put("%", Unit.PERCENT);
	}
	
	
	public final static String MMRE_NS = "http://www.polymerinformatics.com/RecipeRepository.owl#";
	public final static XPathContext MMRE_XPATH= new XPathContext("mmre", MMRE_NS);
	public final static XPathContext RDF_MMRE_XPATH = new XPathContext("rdf", RDFConstants.RDF_NS);
	static {
		RDF_MMRE_XPATH.addNamespace("mmre", MMRE_NS);
	};
	

}
