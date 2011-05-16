package org.xmlcml.cml.converters.compchem.nwchem.input;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.xmlcml.cml.converters.compchem.input.CML2CompchemInputConverter;
import org.xmlcml.cml.converters.text.FreemarkerTextConverter;


public class CML2NWChemInputConverter extends CML2CompchemInputConverter  {
	private static final String DEFAULT_TEMPLATE_DIRECTORY = 
		"src/main/resources/org/xmlcml/cml/converters/compchem/nwchem/in/";

	public static final String DEFAULT_TEMPLATE = "nwchem.ftl";
	
	public CML2NWChemInputConverter() {
		init();
	}
	
	protected void init() {
		directoryForTemplates = DEFAULT_TEMPLATE_DIRECTORY;
		templateFilename = DEFAULT_TEMPLATE;
		super.createConfiguration();
	}

	@Override
	protected File getTemplateDirectory() {
		return new File(directoryForTemplates);
	}
	
	public static void main(String[] args) throws Exception {
		String moleculeFile = (args.length > 0) ? args[0] : "src/test/resources/compchem/nwchem/input/testmol/ch3f_rot.xml";
		String methodBasisFile = (args.length > 1) ? args[1] : "src/test/resources/compchem/nwchem/input/methodBasis.xml";
		String componentsFile = (args.length > 2) ? args[2] : "src/test/resources/compchem/nwchem/input/calculationComponents.xml";
		String textFile = (args.length > 3) ? args[3] : "test/test.out";
		String title = (args.length > 4) ? args[4] : "test title";
		String id = (args.length > 5) ? args[5] : "abx127";
		CML2CompchemInputConverter converter = new CML2NWChemInputConverter();
		converter.setId(id);
		converter.setTitle(title);
		converter.setMethodBasis(new FileInputStream(methodBasisFile));
		converter.setCalculationComponents(new FileInputStream(componentsFile));
		List<String> outputLines = converter.convertToText(new FileInputStream(moleculeFile), textFile);
	}


}
