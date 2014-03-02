package org.xmlcml.cml.semcompchem.tools;

import java.io.File;
import java.io.FileWriter;
import java.util.Map;

import nu.xom.Document;

import org.apache.commons.io.IOUtils;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.converters.antlr.CrystalEyeMoietyInputBuilder;
import org.xmlcml.cml.converters.freemarker.FreeMarkerWriter;
import org.xmlcml.cml.element.CMLMolecule;

public class MoietyCml2GaussianInputTool {
	
	public void convert(File inputFile, File outputFile) {
		CMLBuilder builder = new CMLBuilder();
		Map<String, Object> model = null;
		FileWriter fw = null;
		try {
			Document cml = builder.build(inputFile);
			CrystalEyeMoietyInputBuilder crystalInputBuilder = new CrystalEyeMoietyInputBuilder();
			model = crystalInputBuilder.buildInputModel((CMLMolecule) cml.getRootElement());
			FreeMarkerWriter writer = new FreeMarkerWriter();
			fw = new FileWriter(outputFile);
			writer.processTemplate("gauinput.ftl", model, fw);
		} catch (Exception ex) {
			System.out.println("Problem converting CML to Gaussian input: "+ex.getMessage());
		} finally {
			IOUtils.closeQuietly(fw);
		}
	}

}
