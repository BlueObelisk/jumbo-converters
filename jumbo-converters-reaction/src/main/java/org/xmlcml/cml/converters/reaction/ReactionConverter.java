package org.xmlcml.cml.converters.reaction;

import static org.xmlcml.cml.base.CMLConstants.CML_XPATH;
import static org.xmlcml.euclid.EuclidConstants.S_COMMA;
import static org.xmlcml.euclid.EuclidConstants.S_EMPTY;
import static org.xmlcml.euclid.EuclidConstants.S_SPACE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Attribute;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.element.CMLConditionList;
import org.xmlcml.cml.element.CMLLabel;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLParameter;
import org.xmlcml.cml.element.CMLProduct;
import org.xmlcml.cml.element.CMLProductList;
import org.xmlcml.cml.element.CMLProperty;
import org.xmlcml.cml.element.CMLPropertyList;
import org.xmlcml.cml.element.CMLReactant;
import org.xmlcml.cml.element.CMLReactantList;
import org.xmlcml.cml.element.CMLReaction;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.element.CMLSubstance;
import org.xmlcml.cml.element.CMLSubstanceList;
import org.xmlcml.cml.units.Pressure;
import org.xmlcml.cml.units.Quantity;
import org.xmlcml.cml.units.Temperature;
import org.xmlcml.cml.units.Time;
import org.xmlcml.euclid.EuclidRuntimeException;
import org.xmlcml.euclid.Real2Range;
/**
 * @author pm286
 *
 */
public class ReactionConverter {
	
	private static final Logger LOG = Logger.getLogger(ReactionConverter.class);
	
	private CMLReaction reaction;
	private CMLElement scopeElement;
	private CMLConditionList conditionList;
	private CMLPropertyList propertyList;
	private CMLSubstanceList substanceList;
	private CMLReactantList reactantList;
	private CMLProductList productList;

	/** constructor
	 */
	public ReactionConverter() {
	}
	
	void processAfterParsing() {
		conditionList = null;
		substanceList = null;
		reactantList = new CMLReactantList();
		reaction.addReactantList(reactantList);
		addMoleculeRefsToReactants(reactantList);
		
		productList = new CMLProductList();
		reaction.addProductList(productList);
		addMoleculeRefsToProducts(productList);
		this.processReactionStepArrowsAndText();
	}
	/**
	 * @param parent
	 * @param reactionStepArrows
	 * @throws EuclidRuntimeException
	 * @throws NumberFormatException
	 * @throws RuntimeException
	 */
	void processReactionStepArrowsAndText() {
		// will use attributes on reaction later
//		process(parent);
//		String reactionStepArrows = reaction.getAttributeValue("ReactionStepArrows", CDX_NAMESPACE);
		String reactionStepArrows = reaction.getAttributeValue("ReactionStepArrows");
		if (reactionStepArrows != null) {
			reactionStepArrows = reactionStepArrows.trim();
			if (reactionStepArrows.indexOf(S_SPACE) != -1) {
				LOG.debug("Cannot yet deal with multiple reaction step arrows");
			} else {
				Nodes nodes = scopeElement.query("./cml:scalar[@id='"+reactionStepArrows+"']", CML_XPATH);
				if (nodes.size() == 1) {
					CMLScalar arrow = (CMLScalar) nodes.get(0);
					// FIXME
					Real2Range boundingBox = null;
//					Real2Range boundingBox = ChemDrawConverter.getNormalizedBoundingBox(arrow);
					if (boundingBox == null) {
						
						LOG.error("null boundingBox");
						throw new RuntimeException("NOT YET IMPLEMENTED");
					} else {
//						Nodes labelNodes = scopeElement.query("./cml:label", CML_XPATH);
//						List<CMLLabel> labels = ChemDrawConverter.getVerticalLabels(
//								scopeElement, labelNodes, boundingBox, 50, -50,
//								MIN_REACTION_LABEL_FONT_SIZE,
//								MAX_REACTION_LABEL_FONT_SIZE
//								);
						List<CMLLabel> labels = null;
						if (true) throw new RuntimeException("FIXME labels");
//						labels = ChemDrawConverter.sortLabelsByY(labels);
						for (CMLLabel label : labels) {
		//							LOG.debug("L "+label.getCMLValue());
							// conditions?
							// DCM, 0{{169}}C, 0.5h 
							addTextSplitAtCommas(label.getCMLValue(), reaction);
							label.detach();
						}
					}
					arrow.detach();
				} else {
					LOG.error("Cannot find graphic arrow");
				}
			}
		} else {
			LOG.info("no reactionStepArrows");
		}
	}
	
	/**
	 * @param text
	 * @param reaction
	 * @throws RuntimeException
	 */
	private void addTextSplitAtCommas(String value, CMLReaction reaction) {
		this.reaction = reaction;
		if (value != null && !value.trim().equals(S_EMPTY)) {
			// assume commas separate info
			String[] vv = value.split(S_COMMA);
			for (String v : vv) {
				v = v.trim();
				// the order of these may be heuristic
				if (addSolvent(v)) {
					continue;
				}
				if (addTime(v)) {
					continue;
				}
				if (addPressure(v)) {
					continue;
				}
				if (addReagent(v)) {
					continue;
				}
				if (addTemperature(v)) {
					continue;
				}
				if (addYield(v)) {
					continue;
				}
				ensureConditionList();
				CMLParameter parameter = new CMLParameter();
				parameter.setDictRef("cml:reactionCondition");
				CMLScalar scalar = new CMLScalar();
				conditionList.appendChild(parameter);
				parameter.addScalar(scalar);
				scalar.setValue(v);
			}
		}
	}
	private void ensureConditionList() {
		if (conditionList == null) {
			conditionList = new CMLConditionList();
			reaction.addConditionList(conditionList);
		}
	}
	
	private void ensurePropertyList() {
		if (propertyList == null) {
			propertyList = new CMLPropertyList();
			reaction.addPropertyList(propertyList);
		}
	}
	
	private void ensureSubstanceList() {
		if (substanceList == null) {
			substanceList = new CMLSubstanceList();
			reaction.addSubstanceList(substanceList);
		}
	}
	
	private boolean addReagent(String s) {
		boolean isReagent = false;
		String reag = reagentMap.get(s.toLowerCase());
		if (reag != null) {
			CMLReactant reagent = new CMLReactant();
			reagent.addAttribute(new Attribute("title", reag));
			reagent.setRole("cml:reagent");
			reagent.setDictRef("cmlreag:"+s);
			reaction.addReactant(reagent);
			isReagent = true;
		}
		return isReagent;
	}

	static Pattern yieldPattern = Pattern.compile("(\\d+(\\.\\d+)?) *\\%");
	private boolean addYield(String s) {
		Matcher matcher = yieldPattern.matcher(s);
		boolean isYield = matcher.matches();
		if (isYield) {
			CMLProperty yield = new CMLProperty();
			yield.addAttribute(new Attribute("title", "yield"));
			yield.setDictRef("cml:"+"yield");
			CMLScalar scalar = new CMLScalar();
			scalar.setValue(new Double(matcher.group(1)));
			scalar.setUnits("units:percent");
			yield.addScalar(scalar);
			yield.setRole("yield");
			ensurePropertyList();
			propertyList.appendChild(yield);
		}
		return isYield;
	}

	private boolean addSolvent(String s) {
		boolean isSolv = false;
		String solv = solventMap.get(s.toLowerCase());
		if (solv != null) {
			ensureSubstanceList();
			CMLSubstance solvent = new CMLSubstance();
			solvent.addAttribute(new Attribute("title", solv));
			solvent.setRole("cml:solvent");
			solvent.setDictRef("cmlrepo:"+s);
			substanceList.addSubstance(solvent);
			isSolv = true;
		}
		return isSolv;
	}

	/**
	 * @param v
	 * @param parameter
	 * @param scalar
	 * @return time
	 * @throws RuntimeException
	 */
	private boolean addTime(String v) {
		boolean foundTime = false;
		Quantity t = Time.getTime(v);
		if (t != null) {
			CMLParameter parameter = new CMLParameter();
			CMLScalar scalar = new CMLScalar();
			scalar.setValue(t.getValue());
			scalar.setUnits("units:"+t.getUnits().getId());
			parameter.setRole("duration");
			parameter.addScalar(scalar);
			ensureConditionList();
			conditionList.appendChild(parameter);
			foundTime = true;
		}
		return foundTime;
	}

	/**
	 * @param v
	 * @param parameter
	 * @param scalar
	 * @return pressure
	 * @throws RuntimeException
	 */
	private boolean addPressure(String v) {
		boolean foundPressure = false;
		Quantity p = Pressure.getPressure(v);
		if (p != null) {
			CMLParameter parameter = new CMLParameter();
			CMLScalar scalar = new CMLScalar();
			scalar.setValue(p.getValue());
			scalar.setUnits("units:"+p.getUnits().getId());
			parameter.setRole("pressure");
			parameter.addScalar(scalar);
			ensureConditionList();
			conditionList.appendChild(parameter);
			foundPressure = true;
		}
		return foundPressure;
	}

	/**
	 * @param v
	 * @param parameter
	 * @param scalar
	 * @return temperature
	 * @throws RuntimeException
	 */
	private boolean addTemperature(String v) {
		boolean foundTemp = false;
		Temperature t = Temperature.getTemperature(v);
		if (t != null) {
			CMLParameter parameter = new CMLParameter();
			CMLScalar scalar = new CMLScalar();
			scalar.setValue(t.getValue());
			scalar.setUnits("units:"+t.getUnits().getId());
			parameter.setRole("temperature");
			parameter.addScalar(scalar);
			ensureConditionList();
			conditionList.appendChild(parameter);
			foundTemp = true;
		}
		return foundTemp;
	}

	/**
	 * @param element
	 * @param reactantList
	 * @param reactantS
	 * @throws RuntimeException
	 */
	private void addMoleculeRefsToReactants(CMLReactantList reactantList) {
//		String reactantS = reaction.getAttributeValue("ReactionStepReactants", CDX_NAMESPACE);
		String reactantS = reaction.getAttributeValue("ReactionStepReactants");
		if (reactantS == null) {
			throw new RuntimeException("Null reactant String");
		}
		String[] reactantIds = reactantS.split(S_SPACE);
		for (String id : reactantIds) {
			Nodes nodes = findNodesWithIds(scopeElement, id);
			if (nodes.size() == 0) {
				continue;
			}
			if (nodes.get(0) instanceof CMLMolecule) {
				CMLMolecule refMolecule = new CMLMolecule();
				refMolecule.setRef(id);
				CMLReactant reactant = new CMLReactant();
				reactantList.addReactant(reactant);
				reactant.addMolecule(refMolecule);
				addRoleAndLabel((CMLMolecule) nodes.get(0), refMolecule);
			} else if (nodes.get(0) instanceof CMLLabel) {
				LOG.error("Cannot use label as reactant: "+id);
			} else {
				throw new RuntimeException("unexpected reactant: "+id);
			}
		}
	}

	/**
	 * @param element
	 * @param id
	 * @return nodes
	 */
	private Nodes findNodesWithIds(CMLElement element, String id) {
		Nodes nodes = element.query("//*[@id='"+id+"']");
		if (nodes.size() ==0) {
			LOG.error("******************Cannot find molecule or label: "+id);
		}
		return nodes;
	}

	/**
	 * @param nodes
	 * @param refMolecule
	 * @throws RuntimeException
	 */
	private void addRoleAndLabel(CMLMolecule molecule, CMLMolecule refMolecule) {
		if ("cdx:fragment".equals(molecule.getRole())) {
			refMolecule.setRole("reagent");
		}
		CMLElements<CMLLabel> labelList = molecule.getLabelElements();
		if (labelList.size() > 0) {
			CMLLabel label = new CMLLabel(labelList.get(0));
			label.removeAttribute("id");
			refMolecule.addLabel(label);
		}
	}
	
	/**
	 * @param element
	 * @param productList
	 * @param productS
	 * @throws RuntimeException
	 */
	private void addMoleculeRefsToProducts(CMLProductList productList) {
//		String productS = reaction.getAttributeValue("ReactionStepProducts", CDX_NAMESPACE);
		String productS = reaction.getAttributeValue("ReactionStepProducts");
		if (productS == null) {
			throw new RuntimeException("Null product String");
		}
		String[] productIds = productS.split(S_SPACE);
		for (String id : productIds) {
			Nodes nodes = findNodesWithIds(scopeElement, id);
			if (nodes.size() == 0) {
				continue;
			}
			if (nodes.get(0) instanceof CMLMolecule) {
				CMLMolecule refMolecule = new CMLMolecule();
				refMolecule.setRef(id);
				CMLProduct product = new CMLProduct();
				productList.addProduct(product);
				product.addMolecule(refMolecule);
				addRoleAndLabel((CMLMolecule) nodes.get(0), refMolecule);
			} else if (nodes.get(0) instanceof CMLLabel) {
				LOG.warn("Cannot use label as product: "+id);
			} else {
				throw new RuntimeException("unexpected product: "+id);
			}
		}
	}
	
	final static Map<String, String> reagentMap;
	static final String[] reagents = {
		"(-)DET",
		"18-crown-6",
		"4-pentenoyl chloride",
		"Ac2O",
		"Al2O3",
		"allylstannane",
		"allyltributyltin",
		"BF3OEt2",
		"BF3{{149}}OEt2",
		"BnBr",
		"(C6H5)3P=CHO2CH3",
		"(COCl)2",
		"Cp2TiMe2",
		"D-(-)DIPT",
		"DABCO",
		"DIBAL-H",
		"DMAP",
		"Et3N",
		"Fe(CO)5",
		"Fe2(CO)9",
		"H2",
		"(H2C=O)n",
		"HFpyr",
		"HF{{149}}pyr",
		"(iBu)3Al",
		"imidazole",
		"iPr2NH",
		"iPr2NEt",
		"K2CO3",
		"KOH",
		"Lindlar catalyst",
		"Li",
		"MEMCl",
		"Na2CO3",
		"NaBH3CN",
		"NaH",
		"Naphthalene",
		"nBu4NI",
		"nBuLi",
		"NEt3",
		"p-NO2C6H4CHO",
		"OsO4",
		"Pd/C",
		"PhCHO",
		"PhNH2",
		"(Ph)3PRuCl2",
		"Quinoline",
		"(Pd/CaCO3/PbO)",
		"(Ph3P)3RuCl2",
		"SO2Cl2",
		"TBSCl",
		"TBSOTf",
		"tBuOK",
		"tBuOOH",
		"Ti(OiPr)4",
		"TMSOTf",
		"p-TsOH",
		"VO(acac)2",
		
	};
	
	final static Map<String, String> solventMap;
	static final String[] solvents = {
		"benzene",
		"Celite",
		"CH2Cl2",
		"CHCl3",
		"DCM",
		"DMF",
		"DMSO",
		"Et2O",
		"EtOAc",
		"MeCN",
		"MeOH",
		"petrol",
		"PhH",
		"Pyr",
		"THF",
		"toluene"
	};
	
	static final Pattern[] temps = {
		Pattern.compile("(\\-?\\d+(\\.\\d+)?) *C"),
	};
	static {
		solventMap = new HashMap<String, String>();
		for (String s : solvents) {
			solventMap.put(s.toLowerCase(), s);
		}
		reagentMap = new HashMap<String, String>();
		for (String s : reagents) {
			reagentMap.put(s.toLowerCase(), s);
		}
	}
	/**
	 * @return the reaction
	 */
	public CMLReaction getReaction() {
		return reaction;
	};
}



