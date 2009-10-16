package org.xmlcml.cml.converters.compchem.gaussian;

import nu.xom.Element;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.rdf.cml.CML2OWLRDF;
import org.xmlcml.cml.element.CMLCml;

/**
 * @author ojd20
 */
public class GaussianCML2OWLRDFConverter extends AbstractConverter {

   private static final Logger LOG = Logger.getLogger(
           GaussianCML2OWLRDFConverter.class);
   private String AUX_FILENAME = "org/xmlcml/cml/converters/rdf/cml/ontologies/gaussianArchiveDict.owl";

   @Override
   public Element convertToXML(Element xml) {
      CMLElement cml = ensureCML(xml);
      CML2OWLRDF cml2owlrdf = new CML2OWLRDF(AUX_FILENAME);
      // only use one molecule
      if (cml instanceof CMLCml) {
         Nodes cmlChildNodes = cml.query("./cml:cml", CMLConstants.CML_XPATH);
         // get last molecule
         if (cmlChildNodes.size() > 0) {
            cml = ((CMLCml) cmlChildNodes.get(cmlChildNodes.size() - 1));
         }
      }
      Element rdf = cml2owlrdf.convertCMLElement(cml);
      return rdf;
   }

   @Override
   public int getConverterVersion() {
      return 0;
   }

   public Type getOutputType() {
      return Type.RDFXML;
   }

   public Type getInputType() {
      return Type.CML;
   }
}
