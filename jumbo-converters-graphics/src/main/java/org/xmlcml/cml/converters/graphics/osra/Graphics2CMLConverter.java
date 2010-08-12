package org.xmlcml.cml.converters.graphics.osra;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.cmllite.CML2CMLLiteConverter;
import org.xmlcml.cml.converters.molecule.mdl.MDL2CMLConverter;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLMolecule.HydrogenControl;
import org.xmlcml.cml.tools.GeometryTool;
import org.xmlcml.cml.tools.MoleculeTool;
import uk.ac.cam.ch.wwmm.osra.Osra;
import uk.ac.cam.ch.wwmm.osra.OsraException;
import uk.ac.cam.ch.wwmm.osra.OsraRunner;

/** Interprets images into CML using OSRA
 * 
 * @author pm286, dmj20 and OSRA
 *
 */
public class Graphics2CMLConverter extends AbstractConverter {
	private static Logger LOG = Logger.getLogger(Graphics2CMLConverter.class);
	
		
	private static final String OSRA_BAT = 
//		"C:\\download\\osra-mingw-1-3-6\\osra-mingw\\osra.bat";
		"do not use local BAT files";

	public Graphics2CMLConverter() {
		;
	}
	
	public Type getInputType() {
		return Type.IMAGE;
	}

	public Type getOutputType() {
		return Type.CML;
	}
	
	@Override
	public List<String> convertToText(File image) {
		List<String> smilesList = null;
		try {
			smilesList = createSmiles(image);
		} catch (Exception e) {
			throw new RuntimeException("Cannot read/interpret file: "+image, e);
		}
		return smilesList;
	}
		
	private List<String> createSmiles(File image) throws IOException, OsraException {
		List<String> smilesList = runOsra(image);
		return smilesList;
	}

	private List<String> createSDF(File image) throws IOException, OsraException {
		List<String> smilesList = runOsra(image, "-f", "sdf");
		return smilesList;
	}

	@Override
	public Element convertToXML(File image) {
		CMLCml cml = new CMLCml();
		List<String> sdfLines = null;
		try {
			sdfLines = createSDF(image);
			cml = (CMLCml) new MDL2CMLConverter().convertToXML(sdfLines);
		} catch (IOException e) {
			throw new RuntimeException("Cannot parse image "+image, e);
		} catch (OsraException e) {
			throw new RuntimeException("Cannot parse image "+image, e);
		}
		
		cml = addHydrogensAndConformToCMLLite(cml);
		return cml;
	}

	private CMLCml addHydrogensAndConformToCMLLite(CMLCml cml) {
		CML2CMLLiteConverter cmlLiteConverter = new CML2CMLLiteConverter();
		cml = (CMLCml) cmlLiteConverter.convertToXML(cml);
		List<CMLMolecule> moleculeList = CMLUtil.convertNodesToMoleculeList(
				cml.query("./*[local-name()='molecule']"));
		int i = 0;
		for (CMLMolecule molecule : moleculeList) {
			MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
			moleculeTool.adjustHydrogenCountsToValency(HydrogenControl.ADD_TO_EXPLICIT_HYDROGENS);
			GeometryTool geometryTool = new GeometryTool(molecule);
			geometryTool.addCalculatedCoordinatesForHydrogens(
					CoordinateType.TWOD, HydrogenControl.USE_EXPLICIT_HYDROGENS);
			((CMLMolecule) molecule).setId("m"+(++i));
		}
		return cml;
	}
	
	 /**
    *
    * Runs OSRA on the given file with the given arguments.
    *
    * @param inputfile - file to be converted, may be null if no conversion is required
    * @param arguments - arguments to be passed, may be null if no arguments to be passed
    * @return the output returned by OSRA
    * @throws FileNotFoundException if inputfile does not exist
    * @throws IOException
    */
     public static List<String> runOsra(File inputfile, String... arguments) throws IOException, OsraException {

         if (inputfile != null) {
             if (!inputfile.exists()) {
                 throw new FileNotFoundException(inputfile.getAbsolutePath() + " cannot be found");
             }
         }

         LOG.debug("executing osra");

         OsraRunner osra = Osra.getOsraRunner();

         List<String> lineList = osra.run(inputfile, arguments);
         return lineList;

     }
}
