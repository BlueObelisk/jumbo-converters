package org.xmlcml.cml.converters.compchem.input;

import java.util.HashMap;
import java.util.Map;

import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Nodes;

import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.converters.text.FreemarkerTextConverter;
import org.xmlcml.cml.element.CMLMolecule;

public abstract class CML2CompchemInputConverter extends FreemarkerTextConverter {

	public CML2CompchemInputConverter(String templatePath, String templateName) {
		super(templatePath, templateName);
	}
	
	protected Builder getBuilder() {
		return new CMLBuilder();
	}
	
	@Override
	protected Object getDataModel(Element xmlInput) {
		Map<String,Object> templateRoot = new HashMap<String, Object>();
	    templateRoot.put("jobInfo", getJobInfo(xmlInput));
	    
	    CMLMolecule molecule = getMolecule(xmlInput);
	    if (molecule == null) {
	    	throw new RuntimeException("Must give molecule");
	    }
	    if (molecule.getFormalChargeAttribute() == null) {
			throw new RuntimeException("Molecule must have formal charge");
		}
		if (molecule.getSpinMultiplicityAttribute() == null) {
			System.err.println("**** Molecule must have spin multiplicity ****");
			molecule.setSpinMultiplicity(1);
		}
		templateRoot.put("molecule", molecule);
	    
        MethodBasis methodBasis = getMethodBasis();
	    if (methodBasis != null) {
	        templateRoot.put("methodBasis", methodBasis);
	    }
	    
	    CalculationComponents calculationComponents = getCalculationComponents();
	    if (calculationComponents != null) {
	        templateRoot.put("calculationComponents", calculationComponents);
	    }
	    
	    return templateRoot;
	}
	
	
	private JobInfo getJobInfo(Element xmlInput) {
		JobInfo jobInfo = new JobInfo();
		configureJobInfo(jobInfo, xmlInput);
		return jobInfo;
	}
	
	protected void configureJobInfo(JobInfo jobInfo, Element xmlInput) {
		String id = getId(xmlInput);
		jobInfo.setId(id);
		String title = getTitle(xmlInput);
		jobInfo.setTitle(title);
	}

	protected String getId(Element xmlInput) {
		return "calc1";
	}
	
	protected String getTitle(Element xmlInput) {
		return "untitled calculation";
	}
	
	protected abstract MethodBasis getMethodBasis();
	
	protected abstract CalculationComponents getCalculationComponents();

	protected CMLMolecule getMolecule(Element xmlInput) {
		Nodes moleculeNodes = xmlInput.query("descendant-or-self::*[local-name()='molecule']");
		if (moleculeNodes.size() > 0) {
			CMLMolecule molecule = (CMLMolecule) moleculeNodes.get(0);
			return molecule;
		} else {
			throw new RuntimeException("no molecule found");
		}
	}
	
}
