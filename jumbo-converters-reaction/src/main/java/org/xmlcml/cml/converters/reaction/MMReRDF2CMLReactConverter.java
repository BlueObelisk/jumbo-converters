package org.xmlcml.cml.converters.reaction;

import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.ParentNode;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Converter;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.rdf.rdf.RDFDescription;
import org.xmlcml.cml.converters.rdf.rdf.RDFRdf;
import org.xmlcml.cml.converters.rdf.rdf.RDFTriple;
import org.xmlcml.cml.converters.reaction.properties.CMLRDFObject;
import org.xmlcml.cml.converters.reaction.properties.MMReConstants;
import org.xmlcml.cml.converters.reaction.properties.MMReConstants.Predicate;
import org.xmlcml.cml.element.CMLActionList;
import org.xmlcml.cml.element.CMLAmount;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLParameter;
import org.xmlcml.cml.element.CMLParameterList;
import org.xmlcml.cml.element.CMLProductList;
import org.xmlcml.cml.element.CMLReactant;
import org.xmlcml.cml.element.CMLReactantList;
import org.xmlcml.cml.element.CMLReaction;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.element.CMLSubstance;
import org.xmlcml.cml.element.CMLSubstanceList;

/** reads reactions in MMreRDF format.
 * converts to CMLReact
 * @author pm286
 *
 */
public class MMReRDF2CMLReactConverter extends AbstractConverter implements
        Converter {

   private static final Logger LOG = Logger.getLogger(MMReRDF2CMLReactConverter.class);

   static {
      LOG.setLevel(Level.DEBUG);
   }

   private RDFRdf rdf;
   private CMLElement cmlElement;

   public Type getInputType() {
      return Type.XML;
   }

   public Type getOutputType() {
      return Type.CML;
   }

   /**
    * converts an XYZ object to CML. returns cml:cml/cml:molecule
    *
    * @param xml
    */
   public Element convertToXML(Element xml) {
      cmlElement = null;
      rdf = new RDFRdf(xml);
      if (rdf.getTopNodes().size() == 1) {
         RDFDescription topNode = rdf.getTopNodes().get(0);
         /**
         <rdf:Description rdf:about="http://www.polymerinformatics.com/RecipeRepository.owl#ArticleA">
         <_3:hasPreparation rdf:resource="http://www.polymerinformatics.com/RecipeRepository.owl#ArticleA-preparation-40"/>
         </rdf:Description>
          */
         Element description = topNode.getDescription();
//			String ns = RDFRdf.getAboutNamespace(description);
         // is it MMRe RDF?
         if (MMReConstants.MMRE_NS.equals(RDFRdf.getAboutNamespace(description))) {
            expandTopNode(topNode, cmlElement);
            tidyCML();
         }
      } else {
         LOG.error("Cannot find PREPARATION");
      }
      return cmlElement;
   }

   private void expandTopNode(RDFDescription topNode, CMLElement element) {
      List<RDFTriple> tripleList = topNode.getTripleList();
      if (tripleList.size() == 1) {
         Predicate predicate = Predicate.getPredicate(tripleList.get(0).getLocalName());
         if (predicate == null) {
         } else if (predicate.equals(Predicate.HAS_PREPARATION)) {
            cmlElement = new CMLCml();
            cmlElement.addNamespaceDeclaration(MMReConstants.UNIT_NS_PREFIX, MMReConstants.UNIT_NS);
         }
      }
      if (cmlElement != null) {
         CMLRDFObject preparation = new CMLRDFObject(rdf);
         preparation.expandDescription(topNode, cmlElement);
      }
   }
   private void tidyCML() {
      tidyScalars();
      tidyReagents();
      tidyParameters();
      tidyAmounts();
      tidyReactions();
   }

   private void tidyScalars() {
      Nodes scalars = cmlElement.query(".//cml:scalar", CMLConstants.CML_XPATH);
      for (int i = 0; i < scalars.size(); i++) {
         tidy((CMLScalar) scalars.get(i));
      }
   }

   private void tidy(CMLScalar scalar) {
      /**
      <scalar unit="unit:ml" id="rrpPeTyd252" value="20"/>
       */
      Attribute value = scalar.getAttribute("value");
      if (value != null) {
         String valueS = value.getValue();
         Double d = null;
         try {
            d = new Double(valueS);
         } catch (NumberFormatException nfe) {
            //
         }
         if (d != null) {
            scalar.setValue(d.doubleValue());
         } else {
            scalar.setXMLContent(valueS);
         }
         scalar.removeAttribute(value);
      }
   }

   private void tidyReagents() {
      Nodes reagents = cmlElement.query(".//cml:substance[@role='reagent']", CMLConstants.CML_XPATH);
      for (int i = 0; i < reagents.size(); i++) {
         tidyReagent((CMLSubstance) reagents.get(i));
      }
   }

   private void tidyReagent(CMLSubstance substance) {
      ParentNode parent = substance.getParent();
      substance.detach();
      CMLReactant reactant = new CMLReactant();
      CMLUtil.transferChildren(substance, reactant);
      reactant.copyAttributesFrom(substance);
      parent.appendChild(reactant);
   }

   private void tidyAmounts() {
      Nodes amounts = cmlElement.query(".//cml:amount", CMLConstants.CML_XPATH);
      for (int i = 0; i < amounts.size(); i++) {
         tidy((CMLAmount) amounts.get(i));
      }
   }

   private void tidy(CMLAmount amount) {
      Nodes scalars = amount.query("./cml:scalar", CMLConstants.CML_XPATH);
      for (int i = 0; i < scalars.size(); i++) {
         scalars.get(i).detach();
      }
      if (scalars.size() > 0) {
         merge(amount, (CMLScalar) scalars.get(0));
         for (int i = 1; i < scalars.size(); i++) {
            CMLAmount amount1 = new CMLAmount(amount);
//				amount1.appendChild(scalars.get(i));
            merge(amount1, (CMLScalar) scalars.get(i));
            ParentNode parent = amount.getParent();
            parent.appendChild(amount1);
         }
      }
   }

   private void merge(CMLAmount amount, CMLScalar scalar) {
      Attribute units = scalar.getUnitsAttribute();
      if (units != null) {
         units.detach();
         amount.addAttribute(units);
      }
      Attribute dataType = scalar.getDataTypeAttribute();
      String dataTypeValue = (dataType == null) ? null : dataType.getValue();
      if (dataTypeValue == null || dataTypeValue.equals(CMLConstants.XSD_STRING)) {
         String value = scalar.getValue();
         amount.setXMLContent(value);
      } else if (dataTypeValue.equals(CMLConstants.XSD_DOUBLE) ||
              dataType.equals(CMLConstants.XSD_FLOAT)) {
         double d = scalar.getDouble();
         amount.setXMLContent(d);
      } else {
         throw new RuntimeException("unknown datatype: " + dataType);
      }
      Attribute dictRef = scalar.getDictRefAttribute();
      if (dictRef != null) {
         dictRef.detach();
         amount.addAttribute(dictRef);
      }
      Attribute id = scalar.getIdAttribute();
      if (id != null) {
         id.detach();
         amount.addAttribute(id);
      }

   }

   private void tidyParameters() {
      Nodes parameters = cmlElement.query(".//cml:parameter", CMLConstants.CML_XPATH);
      for (int i = 0; i < parameters.size(); i++) {
         tidy((CMLParameter) parameters.get(i));
      }
   }

   private void tidy(CMLParameter parameter) {
      CMLScalar scalar = new CMLScalar();
      parameter.appendChild(scalar);
      Attribute value = parameter.getAttribute("value");
      if (value != null) {
         value.detach();
         scalar.setXMLContent(value.getValue());
      }
      Attribute unit = parameter.getAttribute("unit");
      if (unit != null) {
         unit.detach();
         scalar.addAttribute(unit);
      }
      Attribute dataType = parameter.getAttribute("dataType");
      if (dataType != null) {
         dataType.detach();
         scalar.setDataType(dataType.getValue());
      }
   }

   private void tidyReactions() {
      Nodes reactions = cmlElement.query(".//cml:reaction", CMLConstants.CML_XPATH);
      for (int i = 0; i < reactions.size(); i++) {
         CMLReaction reaction = (CMLReaction) reactions.get(i);
         tidyReactants(reaction);
         tidyProducts(reaction);
         tidySubstances(reaction);
//			tidyActions(reaction);
         tidyParameters(reaction);
      }
   }

   private void tidyParameters(CMLReaction reaction) {
      Nodes parameters = cmlElement.query(".//cml:parameter", CMLConstants.CML_XPATH);
      if (parameters.size() > 0) {
         CMLParameterList parameterList = new CMLParameterList();
         reaction.appendChild(parameterList);
         for (int i = 0; i < parameters.size(); i++) {
            parameters.get(i).detach();
            parameterList.appendChild(parameters.get(i));
         }
      }
   }

   @SuppressWarnings("unused")
   private void tidyActions(CMLReaction reaction) {
      Nodes actions = cmlElement.query(".//cml:action", CMLConstants.CML_XPATH);
      if (actions.size() > 0) {
         CMLActionList actionList = new CMLActionList();
         reaction.appendChild(actionList);
         for (int i = 0; i < actions.size(); i++) {
            actions.get(i).detach();
            actionList.appendChild(actions.get(i));
         }
      }
   }

   private void tidySubstances(CMLReaction reaction) {
      Nodes substances = cmlElement.query(".//cml:substance", CMLConstants.CML_XPATH);
      if (substances.size() > 0) {
         CMLSubstanceList substanceList = new CMLSubstanceList();
         reaction.appendChild(substanceList);
         for (int i = 0; i < substances.size(); i++) {
            substances.get(i).detach();
            substanceList.appendChild(substances.get(i));
         }
      }
   }

   private void tidyProducts(CMLReaction reaction) {
      Nodes products = cmlElement.query(".//cml:product", CMLConstants.CML_XPATH);
      if (products.size() > 0) {
         CMLProductList productList = new CMLProductList();
         reaction.appendChild(productList);
         for (int i = 0; i < products.size(); i++) {
            products.get(i).detach();
            productList.appendChild(products.get(i));
         }
      }
   }

   private void tidyReactants(CMLReaction reaction) {
      Nodes reactants = cmlElement.query(".//cml:reactant", CMLConstants.CML_XPATH);
      if (reactants.size() > 0) {
         CMLReactantList reactantList = new CMLReactantList();
         reaction.appendChild(reactantList);
         for (int i = 0; i < reactants.size(); i++) {
            reactants.get(i).detach();
            reactantList.appendChild(reactants.get(i));
         }
      }
   }
   
	@Override
	public String getRegistryInputType() {
		return null;
	}
	
	@Override
	public String getRegistryOutputType() {
		return null;
	}
	
	@Override
	public String getRegistryMessage() {
		return "null";
	}

}

