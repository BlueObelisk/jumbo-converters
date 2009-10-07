package org.xmlcml.cml.converters.reaction;

import java.io.StringReader;
import java.util.List;

import nu.xom.Element;
import nu.xom.Nodes;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Command;
import org.xmlcml.cml.converters.Command;
import org.xmlcml.cml.converters.MergeDiff;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.element.CMLActionList;
import org.xmlcml.cml.element.CMLParameterList;
import org.xmlcml.cml.element.CMLProductList;
import org.xmlcml.cml.element.CMLReactantList;
import org.xmlcml.cml.element.CMLReaction;
import org.xmlcml.cml.element.CMLSubstanceList;

/** reads reactions in MMreRDF format.
 * converts to CMLReact
 * @author pm286
 *
 */
public class ReactionMergeDiff extends AbstractConverter implements MergeDiff {
	private static final Logger LOG = Logger.getLogger(MMReRDF2CMLReactConverter.class);
	static {
		LOG.setLevel(Level.DEBUG);
	};
	
	public int getConverterVersion() {
		return 0;
	}
	
	public final static String[] typicalArgsForConverterCommand = {
		"-i",
		"D:/projects1/mmre/rdfFiles/cml/Harter-preparation-1.cml", 
		"-aux", 
		"D:/projects1/thesis/Harter7/mols/chem120.cml",
		"-is", 
		"cml",
		"-it", 
		"CML",
		"-o", 
		"D:/projects1/mmre/rdfFiles/cml/Harter-preparation-1.diff.cml",
		"-converter", 
		"org.xmlcml.cml.converters.reaction.ReactionMergeDiff",
		};
	private CMLElement cmlElement;
	private List<String> xpathList;
	private Element mergedElement;
	
	public Type getInputType() {
		return Type.XML;
	}

	public Type getOutputType() {
		return Type.CML;
	}

	/**
	 * converts an XYZ object to CML. returns cml:cml/cml:molecule
	 * 
	 * @param in input stream
	 */
	public Element convertToXML(Element xml) {
		if (xml == null) {
			throw new RuntimeException("null xml input");
		}
		String xmlS = CMLUtil.getCanonicalString(xml);
		Element element = null;
		try {
			element = new CMLBuilder().build(new StringReader(xmlS)).getRootElement();
		} catch (Exception e) {
			throw new RuntimeException("cannot parse xml? BUG?");
		}
		Element auxElement = getAuxElement();
		if (auxElement == null) {
			throw new RuntimeException("Null or corrupt auxiliary file ");
		}
		cmlElement = null;
		diff(element, auxElement);
		return cmlElement;
	}
	
	public Element merge(Element element1, Element element2) {
		Element element = null;
		return element;
	}
	
	public Element diff(Element element1, Element element2) {
		CMLReaction reaction1 = getSingleReaction(element1);
		CMLReaction reaction2 = getSingleReaction(element2);
		Element element = diff(reaction1, reaction2);
		return element;
	}
	
	private CMLReaction getSingleReaction(Element element) {
		if (!(element instanceof CMLElement)) {
			throw new RuntimeException("element1 is not CML: "+element.getLocalName());
		}
		Nodes reactions1 = element.query(".//cml:reaction", CMLConstants.CML_XPATH);
		if (reactions1.size() != 1) {
			throw new RuntimeException("Need exactly one reaction in element1");
		}
		return (CMLReaction) reactions1.get(0);
	}

/**
 <reaction>
  <reactantList>
   <reactant>
    <molecule ref="37">
     <label value="172" cdx:BoundingBox="153.3052 67.6844 168.3052 76.7344" cdx:ydelta="-3.702999999999996"/>
    </molecule>
   </reactant>
   <reactant title="DIBAL-H" role="cml:reagent" dictRef="cmlreag:DIBAL-H"/>
  </reactantList>
  <productList>
   <product>
    <molecule ref="70">
     <label value="173" cdx:BoundingBox="337.1213 70.6814 352.1213 79.7314" cdx:ydelta="-5.200000000000003"/>
    </molecule>
   </product>
  </productList>
  <substanceList>
   <substance title="THF" role="cml:solvent" dictRef="cmlrepo:THF"/>
  </substanceList>
  <conditionList>
   <parameter role="temperature">
    <scalar dataType="xsd:double" units="units:c">-78.0</scalar>
   </parameter>
   <parameter role="duration">
    <scalar dataType="xsd:double" units="units:hour">1.0</scalar>
   </parameter>
  </conditionList>
 </reaction>

  <reaction id="Harter-preparation-1">
    <action role="cml:washed" id="rrpPeTyd897">
      <amount units="unit:ml" dictRef="cml:volume" id="rrpPeTyd899">150.0</amount>
      <name>Et2O</name>
    </action>
    <action role="cml:purified" id="rrpPeTyd901">
      <action role="cml:chromatography">flash column chromatography</action>
    </action>
    <reactantList>
      <reactant id="Harter_172">
        <amount units="unit:mmol" dictRef="cml:molarAmount" id="rrpPeTyd876">7.68</amount>
        <label role="cml:serialNumber">172</label>
        <name>epoxide ester</name>
        <amount units="unit:gram" dictRef="cml:mass" id="rrpPeTyd875">1.2</amount>
      </reactant>
      <reactant role="reagent" id="rrpPeTyd877">
        <amount units="unit:mmol" dictRef="cml:molarAmount" id="rrpPeTyd880">17.7</amount>
        <name>Diisobutylaluminium hydride</name>
        <amount units="unit:ml" dictRef="cml:volume" id="rrpPeTyd879">17.7</amount>
      </reactant>
      <reactant role="reagent" id="rrpPeTyd887">
        <amount units="unit:ml" dictRef="cml:volume" id="rrpPeTyd889">8.0</amount>
        <name>Triethanolamine</name>
      </reactant>
      <reactant role="reagent" id="rrpPeTyd884">
        <amount units="unit:ml" dictRef="cml:volume" id="rrpPeTyd886">10.0</amount>
        <name>methanol</name>
      </reactant>
    </reactantList>
    <productList>
      <product id="Harter_173">
        <property dictRef="cml:state">oil</property>
        <amount units="unit:percent" dictRef="cml:percent" id="rrpPeTyd895">82.0</amount>
        <label role="cml:serialNumber">173</label>
        <name>alcohol</name>
        <name>(2E,4R*,5R*)-4,5-epoxy-hex-2-en-1-ol</name>
        <amount units="unit:mmol" dictRef="cml:molarAmount" id="rrpPeTyd894">6.29</amount>
        <amount units="unit:mg" dictRef="cml:mass" id="rrpPeTyd893">718.0</amount>
      </product>
    </productList>
    <substanceList>
      <substance role="solvent" id="rrpPeTyd881">
        <amount units="unit:ml" dictRef="cml:volume" id="rrpPeTyd883">10.0</amount>
        <name>THF</name>
      </substance>
    </substanceList>
    <parameterList>
      <parameter dictRef="cml:time" units="unit:hour" id="rrpPeTyd890">
        <scalar dataType="xsd:double">18.0</scalar>
      </parameter>
      <parameter dictRef="cml:temp" units="unit:celsius" id="rrpPeTyd891">
        <scalar dataType="xsd:double">7.0</scalar>
      </parameter>
    </parameterList>
  </reaction>

  <reactant id="Harter_172">
    <amount units="unit:mmol" dictRef="cml:molarAmount" id="rrpPeTyd876">7.68</amount>
    <label role="cml:serialNumber">172</label>
    <name>epoxide ester</name>
    <amount units="unit:gram" dictRef="cml:mass" id="rrpPeTyd875">1.2</amount>
  </reactant>
      
  <reactant>
   <molecule ref="37">
    <label value="172" cdx:BoundingBox="153.3052 67.6844 168.3052 76.7344" cdx:ydelta="-3.702999999999996"/>
   </molecule>
  </reactant>
   
      reactantProductXPathList = 
          "./cml:label" ... "./cml:molecule/cml:label/@value"
          
      <substance role="solvent" id="rrpPeTyd881">
        <amount units="unit:ml" dictRef="cml:volume" id="rrpPeTyd883">10.0</amount>
        <name>THF</name>
      </substance>
      
   <substance title="THF" role="cml:solvent" dictRef="cmlrepo:THF"/>

      solventXPathList = 
          "./cml:name" ... "./@title"
          
 * 
 * 
 * @param reaction1
 * @param reaction2
 * @return
 */	
	private Element diff(CMLReaction reaction1, CMLReaction reaction2) {
		xpathList = getCommand().getXPathList();
		String rootXpath = ".//cml:reactant";
		compareElements(reaction1, reaction2, rootXpath, 0, 1);
		rootXpath = ".//cml:product";
		compareElements(reaction1, reaction2, rootXpath, 0, 1);
		rootXpath = ".//cml:substance";
		compareElements(reaction1, reaction2, rootXpath, 2, 3);
		makeMergedReaction();
		return mergedElement;
	}
	
	private void makeMergedReaction() {
		mergedElement = new CMLReaction();
		CMLReactantList reactantList = new CMLReactantList();
		CMLProductList productList = new CMLProductList();
		CMLSubstanceList substanceList = new CMLSubstanceList();
		CMLActionList actionList = new CMLActionList();
		CMLParameterList parameterList = new CMLParameterList();
		mergedElement.appendChild(reactantList);
		mergedElement.appendChild(productList);
		mergedElement.appendChild(substanceList);
		mergedElement.appendChild(parameterList);
		mergedElement.appendChild(actionList);
	}

private void compareElements(CMLReaction reaction1, CMLReaction reaction2,
		String rootXpath, int xPath1, int xPath2) {
	
	List<CMLElement> reactants1 = CMLUtil.getCMLElements(
			reaction1, rootXpath, CMLConstants.CML_XPATH);
	List<CMLElement> reactants2 = CMLUtil.getCMLElements(
			reaction2, rootXpath, CMLConstants.CML_XPATH);
	
	for (CMLElement element1 : reactants1) {
		for (CMLElement element2 : reactants2) {
			Double agreement = getAgreementWith(element1, xpathList.get(xPath1), element2, xpathList.get(xPath2));
			if (agreement > 0.99) {
//				element1.mergeElement(element2, Merge.OVERWRITE);
			}
		}
	}
}
	
	private Double getAgreementWith(CMLElement element1, String xpath1, 
			CMLElement element2, String xpath2) {
		Nodes nodes1 = element1.query(xpath1, CMLConstants.CML_XPATH);
		String s1 = (nodes1.size() > 0) ? nodes1.get(0).getValue() : null;
		Nodes nodes2 = element2.query(xpath2, CMLConstants.CML_XPATH);
		String s2 = (nodes2.size() > 0) ? nodes2.get(0).getValue() : null;
		LOG.debug(""+s1 + " ... "+s2);
		Double d = 0.0;
		if (s1 != null && s1.equals(s2)) {
			d = 1.0;
		}
		return d;
	}
}

