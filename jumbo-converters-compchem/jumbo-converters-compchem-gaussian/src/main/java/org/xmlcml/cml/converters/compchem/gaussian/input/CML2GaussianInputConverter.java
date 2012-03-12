package org.xmlcml.cml.converters.compchem.gaussian.input;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.ParsingException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.cml.CMLCommon;
import org.xmlcml.cml.converters.cml.CMLSelector;
import org.xmlcml.cml.converters.compchem.AbstractCompchem2CMLConverter;
import org.xmlcml.cml.converters.compchem.gaussian.GaussianCommon;
import org.xmlcml.cml.element.CMLMolecule;

public class CML2GaussianInputConverter extends AbstractCompchem2CMLConverter {

   private static final Logger LOG = Logger.getLogger(
           CML2GaussianInputConverter.class);

   static {
      LOG.setLevel(Level.INFO);
   }
   // really awful, but ant cannot pick up classpath
//   private final static String DICT_FILE =
//           "org/xmlcml/cml/converters/compchem/gaussian/gaussianArchiveDict.xml";

   private String controlFile = "org/xmlcml/cml/converters/compchem/controls/freq.xml";
   public static final String GAUSS_PREFIX = "gauss";
   public static final String GAUSS_URI = "http://wwmm.ch.cam.ac.uk/dict/gauss";

   public CML2GaussianInputConverter() {

   }

   @Override
   protected AbstractCommon getCommon() {
	   return new GaussianCommon();
   }
   public Type getInputType() {
      return Type.CML;
   }

   public Type getOutputType() {
      return Type.GAU_IN;
   }

   /**
    * converts an MDL object to CML. returns cml:cml/cml:molecule
    *
    * @param xml
    */
   public List<String> convertToText(Element xml) {
      List<String> stringList = null;
      CMLElement cml = CMLBuilder.ensureCML(xml);
      CMLMolecule molecule = new CMLSelector(cml).getToplevelMoleculeDescendant(
              true);
      if (molecule != null) {
         setMolecule(molecule);
         GaussianInputProcessor gaussianInputProcessor = new GaussianInputProcessor(
                 this);
         stringList = gaussianInputProcessor.makeInput();
      }
      return stringList;
   }

   public void addNamespaces(CMLElement cml) {
      addCommonNamespaces(cml);
      cml.addNamespaceDeclaration(GAUSS_PREFIX, GAUSS_URI);
   }

   /** reads auxiliary file into XML Element
    *
    * @return null if missing or not XML
    */
   @Override
   public Element getAuxElement() {
      InputStream in = null;
      try {
         in = getClass().getClassLoader().getResourceAsStream(controlFile);
         return new Builder().build(in).getRootElement();
      } catch (ParsingException ex) {
         throw new RuntimeException(ex);
      } catch (IOException ex) {
         throw new RuntimeException(ex);
      } finally {
         IOUtils.closeQuietly(in);
      }
   }

   public String getControlFile() {
      return controlFile;
   }

   public void setControlFile(String controlFile) {
      this.controlFile = controlFile;
   }

	@Override
	public String getRegistryInputType() {
		return CMLCommon.CML;

	}

	@Override
	public String getRegistryOutputType() {
		return GaussianCommon.INPUT;
	}

	@Override
	public String getRegistryMessage() {
		return "Create Gaussian input from CML";
	}

}
