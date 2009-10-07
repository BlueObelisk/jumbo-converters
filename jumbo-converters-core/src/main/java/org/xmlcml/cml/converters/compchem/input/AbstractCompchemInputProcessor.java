package org.xmlcml.cml.converters.compchem.input;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import nu.xom.Elements;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Command;
import org.xmlcml.cml.converters.compchem.AbstractCompchem2CMLConverter;
import org.xmlcml.cml.converters.compchem.AbstractCompchemProcessor;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLDictionary;
import org.xmlcml.cml.element.CMLModule;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLParameter;

public abstract class AbstractCompchemInputProcessor extends AbstractCompchemProcessor {
	private static Logger LOG = Logger.getLogger(AbstractCompchemInputProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public final static String CHECKPOINT = "#checkpoint";
	
	public final static String DIRECTIVES = "directives";
	public final static String GLOBAL     = "global";
	public final static String JOB        = "job";
	public final static String METHODS    = "methods";
	public final static String OPERATIONS = "operations";

	protected CMLDictionary dictionary;
	protected List<String> stringList;
	protected AbstractCompchem2CMLConverter converter;
	protected String jobname;
	protected Solvent solvent;
	protected int charge;
	protected int multiplicity;
	protected CMLMolecule inputMolecule;
	protected CMLModule globalModule;
	protected List<Job> jobList;
	protected Job currentJob;
	protected Command command;

	protected AbstractCompchemInputProcessor(AbstractCompchem2CMLConverter converter) {
		this.converter = converter;
		this.inputMolecule = converter.getMolecule();
	}
	/**
	 * 
	 * @return basis (e.g. 32-1G) without diffuseFunctions
	 */
	protected abstract String getDefaultBasis();
	
	/**
	 * 
	 * @return method (e.g. HF) or functional (B3LYP)
	 */
	protected abstract String getDefaultMethod();

	protected void processInputMolecule() {
		if (inputMolecule.getAtomCount() == 0) {
			throw new RuntimeException("Molecule has no atoms");
		}
		for (CMLAtom atom : inputMolecule.getAtoms()) {
			if (!atom.hasCoordinates(CoordinateType.CARTESIAN)) {
				throw new RuntimeException("At least one atom has no 3D coordinates");
			}
		}
		charge = (inputMolecule.getAttribute("formalCharge") == null) ? 0 : inputMolecule.getFormalCharge();
		multiplicity = (inputMolecule.getAttribute("spinMultiplicity") == null) ? 1 : inputMolecule.getSpinMultiplicity();
	}

	protected void readAndProcessControlInformation() {
		Element controlsElement = null;
		try {
			controlsElement = converter.getAuxElement();
		} catch (Exception e) {
			LOG.warn("cannot find/read auxiliary file", e);
		}
		jobList = new ArrayList<Job>();
		if (controlsElement != null) {
			CMLElement cmlControls = AbstractConverter.ensureCML(controlsElement);
			globalModule = null;
			Elements childElements = cmlControls.getChildElements();
			for (int i = 0; i < childElements.size(); i++) {
				if (childElements.get(i) instanceof CMLModule) {
					CMLModule childModule = (CMLModule) childElements.get(i);
					String role = childModule.getRole();
					if (role == null) {
						throw new RuntimeException("cml childModule has no role");
					} else if (role.equals(GLOBAL)) {
						if (globalModule == null) {
							globalModule = childModule;
						} else {
							throw new RuntimeException("only one gloabl module allowed");
						}
					} else if (role.equals(JOB)) {
						jobList.add(new Job(childModule, this));
					} else {
						throw new RuntimeException("unknown role: "+role);
					}
				}
			}
		} else {
			LOG.warn("Cannot find auxiliary file (did you include -aux? "+converter.getCommand().getAuxfileName());
		}
		processAbstractModule();
	}

	protected void processAbstractModule() {
		if (jobList == null || jobList.size() == 0) {
			throw new RuntimeException("No jobs specified: did you specify auxiliary file?");
		}
		for (int i = 0; i < jobList.size(); i++) {
			Job job = jobList.get(i);
			job.copyGlobalModule(globalModule);
			process(job, i);
		}
	}
	
	protected abstract void process(Job job, int serial);

	protected CMLDictionary findDictionary(String resourceS) {
		dictionary = null;
		URL url = getClass().getClassLoader().getResource(resourceS);
		if (command != null && !command.isQuiet()) {
			LOG.info("URL "+url);
		}
		try {
			InputStream inputStream = url.openStream();
			CMLCml cml = (CMLCml) new CMLBuilder().build(inputStream).getRootElement();
			// BUG
			dictionary = (CMLDictionary)cml.getFirstCMLChild(CMLDictionary.TAG);
			if (dictionary == null) {
				throw new IllegalStateException("Failed to find dictionary element in "+resourceS);
			}
		} catch (Exception e) {
			LOG.warn("Failed to read dictionary from resource: "+url);
//			e.printStackTrace();
			throw new RuntimeException("bad dictionary: ", e);
		}
		return dictionary;
	}
	/* should these be explicit (pull) or implicit (push)?
	protected abstract String getFrequency(CMLParameter parameter);
	protected abstract String getNMR(CMLParameter parameter);
	protected abstract String getOpt(CMLParameter parameter);
	protected abstract String getScrf(CMLParameter parameter);
*/	
	
	protected void makeDefaults() {
		charge = 0;
		multiplicity = 1;
	}
	
}
