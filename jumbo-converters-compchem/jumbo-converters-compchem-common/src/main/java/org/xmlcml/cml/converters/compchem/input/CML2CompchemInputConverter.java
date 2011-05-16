package org.xmlcml.cml.converters.compchem.input;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nu.xom.Element;
import nu.xom.Nodes;

import org.apache.commons.io.FileUtils;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.text.FreemarkerTextConverter;
import org.xmlcml.cml.element.CMLMolecule;

import freemarker.template.Template;

public abstract class CML2CompchemInputConverter extends FreemarkerTextConverter {

	protected CMLMolecule molecule;
	protected MethodBasis methodBasis;
	protected CalculationComponents calculationComponents;
	protected JobInfo jobInfo;
	protected Map<String, Object> templateRoot;
	protected String templateFilename;
	
	public CML2CompchemInputConverter() {
		super();
		jobInfo = new JobInfo();
	    templateRoot = new HashMap<String, Object>();
	    templateRoot.put("jobInfo", jobInfo);
	}
	
	public void setTemplateFilename(String templateFilename) {
		this.templateFilename = templateFilename;
	}

	public String getTemplateFilename() {
		return templateFilename;
	}
	
	public void setTitle(String title) {
		jobInfo.setTitle(title);
	}

	public void setId(String id) {
		jobInfo.setId(id);
	}

	public void setMolecule(CMLMolecule molecule) {
		this.molecule = molecule;
		if (molecule.getFormalChargeAttribute() == null) {
			throw new RuntimeException("Molecule must have formal charge");
		}
		if (molecule.getSpinMultiplicityAttribute() == null) {
			throw new RuntimeException("Molecule must have spin multiplicity");
		}
		templateRoot.put("molecule", molecule);
	}
	
	public void setMolecule(InputStream inputStream) {
		CMLElement cml = CMLUtil.parseQuietlyIntoCML(inputStream);
		Nodes molecules = cml.query(".//*[local-name()='molecule']");
		if (molecules.size() > 0) {
			setMolecule((CMLMolecule) molecules.get(0));
		}
	}
	
	public void setMethodBasis(InputStream inputStream) {
		this.setMethodBasis(new MethodBasis(inputStream));
	}

	public void setMethodBasis(MethodBasis methodBasis) {
		this.methodBasis = methodBasis;
		templateRoot.put("methodBasis", methodBasis);
	}

	public MethodBasis getMethodBasis() {
		return methodBasis;
	}

	public void setCalculationComponents(InputStream inputStream) {
		setCalculationComponents(new CalculationComponents(inputStream));
	}

	public void setCalculationComponents(CalculationComponents calculationComponents) {
		this.calculationComponents = calculationComponents;
		templateRoot.put("calculationComponents", calculationComponents);
	}

	public CalculationComponents getCalculationComponents() {
		return calculationComponents;
	}
	
	public List<String> convertToText(InputStream inputStream, String outfileName) throws Exception {
		List<String> stringList = new ArrayList<String>();
		this.setMolecule(inputStream);
		StringWriter stringWriter = new StringWriter();
		convertXMLtoWriter(stringWriter);
		stringList.add(stringWriter.toString());
		if (outfileName != null) {
			FileUtils.writeLines(new File(outfileName), stringList);
		}
		return stringList;
	}

	@Override
	public List<String> convertToText(Element xmlInput) {
		List<String> stringList = new ArrayList<String>();
		StringWriter stringWriter = new StringWriter();
		convertXMLtoWriter(stringWriter);
		stringList.add(stringWriter.toString());
		return stringList;
	}

	protected void convertXMLtoWriter(Writer writer) {
	    Template ftlTemplate = null;
	    try {
			ftlTemplate = cfg.getTemplate(getTemplateFilename());
		} catch (IOException e) {
			throw new RuntimeException("cannot create template", e);
		}
	
	    if (molecule != null) {
	        templateRoot.put("molecule", molecule);
	    }
	    
	    if (methodBasis != null) {
	        templateRoot.put("methodBasis", methodBasis);
	    }
	    
	    if (calculationComponents != null) {
	        templateRoot.put("calculationComponents", calculationComponents);
	    }
	    
	    try {
			ftlTemplate.process(templateRoot, writer);
	        writer.flush();
		} catch (Exception e) {
			throw new RuntimeException("cannot run template", e);
		}
		
	}

}
