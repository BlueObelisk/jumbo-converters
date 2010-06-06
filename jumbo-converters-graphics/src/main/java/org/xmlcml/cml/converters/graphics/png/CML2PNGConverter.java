package org.xmlcml.cml.converters.graphics.png;

import java.io.ByteArrayOutputStream;
import java.io.File;

import nu.xom.Element;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.CMLSelector;
import org.xmlcml.cml.converters.Converter;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.graphics.CDKUtils;
import org.xmlcml.cml.element.CMLMolecule;

public class CML2PNGConverter extends AbstractConverter implements
        Converter {

   private static final Logger LOG = Logger.getLogger(CML2PNGConverter.class);
   public final static String[] typicalArgsForConverterCommand = {
      "-sd", "D:/projects/cost/gaussian/gaussian.cml",
      "-odir", "../gaussian.cml",
      "-is", "gau.cml",
      "-os", "png",
      "-converter", "org.xmlcml.cml.converters.graphics.png.CML2PNGConverter"
   };
   public final static String[] testArgs = {
      "-quiet",
      "-sd", "src/test/resources/gaussian.cml",
      "-odir", "../gaussian.cml",
      "-is", "gau.cml",
      "-os", "png",
      "-converter", "org.xmlcml.cml.converters.graphics.png.CML2PNGConverter"
   };

   public Type getInputType() {
      return Type.CML;
   }

   public Type getOutputType() {
      return Type.PNG;
   }

   @Override
   public void convert(Element moleculeElement, File pngFile) {
      CMLMolecule molecule = (CMLMolecule) moleculeElement;
      molecule = ensureCoordinates(molecule);
      if (molecule != null) {
         Cml2Png cp = new Cml2Png(molecule);
         cp.renderMolecule(pngFile);
      }
   }

   /**
    * converts a CML object to PNG. assumes a single CMLMolecule as descendant
    * of root with 2D coordinates
    *
    * @param in input stream
    */
   @Override
   public byte[] convertToBytes(Element xml) {
      byte[] output = null;
      CMLElement cml = CMLBuilder.ensureCML(xml);
      CMLMolecule molecule = new CMLSelector(cml).getToplevelMoleculeDescendantOrSelf(true);
      if (molecule != null) {
         output = convertToBytes(molecule);
      } else {
         warn("Cannot find molecule to draw png");
      }
      return output;
   }

   /** only works for single molecule.
    *
    * @param molecule
    * @return
    */
   private byte[] convertToBytes(CMLMolecule molecule) {
      byte[] output = null;
      molecule = ensureCoordinates(molecule);
      if (molecule != null) {
         Cml2Png cp = new Cml2Png(molecule);
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         cp.renderMolecule(baos);
         output = baos.toByteArray();
         IOUtils.closeQuietly(baos);
         LOG.trace("Output size: " + output.length);
      }

      return output;
   }

   /**
    * @param molecule
    * @return
    */
   private CMLMolecule ensureCoordinates(CMLMolecule molecule) {
      if (!molecule.hasCoordinates(CoordinateType.TWOD)) {
         LOG.trace("Trying to add 2D coordinates.");
         try {
            molecule = CDKUtils.add2DCoords(molecule);
         } catch (Exception ex) {
            warn("Cannot create 2D coordinates for diagram" + ex.getMessage());
            molecule = null;
         }
      }
      return molecule;
   }


   @Override
   public int getConverterVersion() {
      return 0;
   }
}
