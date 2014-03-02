package org.xmlcml.cml.converters.compchem.input;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import nu.xom.Element;
import nu.xom.Elements;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.attribute.NamespaceRefAttribute;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.converters.compchem.AbstractCompchem2CMLConverter;
import org.xmlcml.cml.converters.compchem.AbstractCompchemProcessor;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLDictionary;
import org.xmlcml.cml.element.CMLEntry;
import org.xmlcml.cml.element.CMLModule;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLParameter;
import org.xmlcml.cml.interfacex.HasDictRef;

public class AbstractCompchemInputProcessorNew extends AbstractCompchemProcessor {
	private static Logger LOG = Logger.getLogger(AbstractCompchemInputProcessorNew.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public final static String CHECKPOINT = "#checkpoint";

	/*
	public final static String DIRECTIVES = "directives";
	public final static String GLOBAL     = "global";
	public final static String JOB        = "job";
	public final static String METHODS    = "methods";
	public final static String OPERATIONS = "operations";
*/
	
	protected List<String> stringList;
	protected AbstractCompchem2CMLConverter converter;
	protected String jobname;
	protected Solvent solvent;
	protected CMLModule globalModule;
//	protected List<Job> jobList;
//	protected Job currentJob;
//	protected Command command;

	/** metadata */
	public final static String MD = "md"; 
	/** qm */
	public final static String QM = "qm"; 
	/** command */
	public final static String CMD = "cmd"; 
	/** compchem dictionary */
	public final static String COMPCHEM_DICT_NAME = "org/xmlcml/cml/converters/compchem/compchemDict.xml";
	
	protected CMLDictionary compchemDict;
	protected CMLCml jobCml;
	protected CMLCml abstractJob;
	protected CMLModule mdModule;
	protected CMLModule qmModule;
	protected CMLModule cmdModule;
	protected int charge;
	protected int multiplicity;
	protected CMLMolecule inputMolecule;

	public AbstractCompchemInputProcessorNew() {
		
	}

	protected AbstractCompchemInputProcessorNew(AbstractCompchem2CMLConverter converter) {
		this.converter = converter;
		this.inputMolecule = converter.getMolecule();
	}
	/**
	 * 
	 * @return basis (e.g. 32-1G) without diffuseFunctions
	 */
	protected String getDefaultBasis() {
		throw new RuntimeException("NYI");
	}
	
	/**
	 * 
	 * @return method (e.g. HF) or functional (B3LYP)
	 */
	protected String getDefaultMethod() {
		throw new RuntimeException("NYI");
	}

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

	
	protected void process(Job job, int serial) {
		throw new RuntimeException("NYI");
	}
	
	protected void process() {
		Elements moduleList = jobCml.getChildCMLElements(CMLModule.TAG);
		for (int i = 0; i < moduleList.size(); i++) {
			CMLModule module = (CMLModule) moduleList.get(i);
			LOG.debug(module.getNamespaceURI("compchem"));
			String id = getIdFromDictRef(module);
			if (id == null) {
				throw new RuntimeException("No id for entry dictRef");
			}
			if (id.equals(MD)) {
				mdModule = module;
			} else if (id.equals(QM)) {
				qmModule = module;
			} else if (id.equals(CMD)) {
				cmdModule = module;
			} else {
				throw new RuntimeException("unknown module: "+id);
			}
			processModule(module);
		}
		abstractJob = jobCml;
	}

	private String getIdFromDictRef(HasDictRef element) {
		String id = null;
		String dictRef = element.getDictRef();
		if (dictRef == null) {
			throw new RuntimeException("Missing dictRef");
		}
		String prefix = NamespaceRefAttribute.getPrefix(dictRef);
		String namespaceS = ((Element)element).getNamespaceURI(prefix);
		if (!compchemDict.getNamespace().equals(namespaceS)) {
			throw new RuntimeException("Not a compchem entry");
		} else {
			id = NamespaceRefAttribute.getLocalName(dictRef);
		}
		return id;
	}

	private void processModule(CMLModule module) {
		List<CMLElement> childElements = module.getChildCMLElements();
		for (CMLElement childElement : childElements) {
			if (childElement instanceof CMLParameter) {
				processParameter((CMLParameter) childElement);
			} else {
				throw new RuntimeException("unknown module child: "+childElement.getLocalName());
			}
		}
	}

	private void processParameter(CMLParameter parameter) {
		String id = getIdFromDictRef(parameter);
		CMLEntry entry = compchemDict.getCMLEntry(id);
		if (entry == null) {
			throw new RuntimeException("Missing dictionary entry: "+id);
		}
	}

	protected CMLDictionary findDictionary(String resourceS) {
		compchemDict = null;
		URL url = getClass().getClassLoader().getResource(resourceS);
		if (url == null) {
			throw new RuntimeException("cannot find dictionary: "+resourceS);
		}
		/*
		if (command != null && !command.isQuiet()) {
			LOG.info("URL "+url);
		}
		*/
		try {
			InputStream inputStream = url.openStream();
			CMLCml cml = (CMLCml) new CMLBuilder().build(inputStream).getRootElement();
			// BUG
			compchemDict = (CMLDictionary)cml.getFirstCMLChild(CMLDictionary.TAG);
			if (compchemDict == null) {
				throw new IllegalStateException("Failed to find dictionary element in "+resourceS);
			}
		} catch (Exception e) {
			LOG.warn("Failed to read dictionary from resource: "+url);
//			e.printStackTrace();
			throw new RuntimeException("bad dictionary: ", e);
		}
		return compchemDict;
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
	public void ensureCompchemDictionary() {
		compchemDict = findDictionary(COMPCHEM_DICT_NAME);
	}
	
	public void setInputMolecule(CMLMolecule molecule) {
		this.inputMolecule = molecule;
	}
	public void setJobCml(CMLCml jobCml) {
		this.jobCml = jobCml;
	}
	
	public CMLCml getAbstractJob() {
		return abstractJob;
	}

}
