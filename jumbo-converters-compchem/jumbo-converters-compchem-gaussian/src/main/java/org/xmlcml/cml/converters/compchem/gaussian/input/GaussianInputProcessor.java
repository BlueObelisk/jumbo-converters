package org.xmlcml.cml.converters.compchem.gaussian.input;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Nodes;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.attribute.NamespaceRefAttribute;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.compchem.input.AbstractCompchemInputProcessor;
import org.xmlcml.cml.converters.compchem.input.Job;
import org.xmlcml.cml.converters.compchem.input.Operations;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLParameter;
import org.xmlcml.euclid.Point3;

/**
 * converts Gaussian archive to molecule, metadata and properties
 * mainly reads lines and manages them. Main logic in GaussianArchive
 * 
 * @author Peter Murray-Rust
 * 
 */
public class GaussianInputProcessor extends AbstractCompchemInputProcessor {

	public static Logger LOG = Logger.getLogger(GaussianInputProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	private static String NPROC = "NProcShared";
	private static String MEM = "mem";
	
	private static String BRIEF = "T";
	private static String NORMAL = "N";
	private static String FULL = "P";

	@SuppressWarnings("unused")
	private static String NOSYMMETRY = "nosymmetry";

	@SuppressWarnings("unused")
	private boolean hasCarbonyl;

	public GaussianInputProcessor(CML2GaussianInputConverter converter) {
		super(converter);
	}
	
	public String getDefaultMethod() {
		return "HF";
	}
	
	public String getDefaultBasis() {
		return "32-1G";
	}
	
	List<String> makeInput() {
		makeDefaults();
		dictionary = findDictionary("org/xmlcml/cml/converters/compchem/gaussian/gaussianArchiveDict.xml");
		stringList = new ArrayList<String>();
		jobname = converter.getFileId();
		readAndProcessControlInformation();
		return stringList;
	}

	protected void makeDefaults() {
		super.makeDefaults();
		solvent = Solvent.WATER;
	}

/*
	stringList.add("%chk="+jobname+".chk");
	stringList.add("#N RHF/STO-3G opt(loose) ");
	stringList.add("");
	stringList.add("Optimisation using  UFF coordinates and  STO-3G (more stable than  PM3)");
	stringList.add("");
	stringList.add("0 1");
	getConnectionTableString();
	stringList.add("");
	
	<cml>
  	  <module title="computationalDirectives">
  	    <parameter dictRef="ccml:verbosity">
  	      <scalar>verbose</scalar>
  	    </parameter>
  	    <parameter dictRef="ccml:processorCount">
  	      <scalar dataType="xsd:integer">8</scalar>
  	    </parameter>
  	    <parameter dictRef="ccml:maximumMemory">
  	      <scalar dataType="xsd:integer" units="units:megaword">500</scalar>
  	    </parameter>
  	  </module>
  	  <module title="methods">
  	    <parameter dictRef="ccml:method">
  	      <scalar>RHF</scalar>
  	    </parameter>
  	    <parameter dictRef="ccml:basis">
  	      <scalar>STO-3G</scalar>
  	    </parameter>
  	    <parameter dictRef="ccml:diffuseFunctions">
  	      <scalar>d,p</scalar>
  	    </parameter>
  	  </module>
  	  <module title="operations">
  	    <parameter dictRef="ccml:optimization">
  	      <scalar convention="cmlx:gaussian">loose</scalar>
  	    </parameter>
	  </module>
	</cml>
*/	

	public void process(Job job, int serial) {
		this.currentJob = job;
		if (serial > 0) {
			stringList.add("--Link1--");
		}
		addComputationalDirectives();
		stringList.add(makeCommandLine(job));
		stringList.add(CMLConstants.S_SPACE);
		String title = job.getTitle();
		stringList.add((title == null) ? "no title" : title);
		stringList.add(CMLConstants.S_SPACE);
		outputMolecule(job, serial);
		stringList.add(CMLConstants.S_SPACE);
	}

	private void outputMolecule(Job job, int serial) {
		stringList.add(""+charge+" "+multiplicity);
		String moleculeRef = job.getMoleculeRef();
		if (!CHECKPOINT.equals(moleculeRef)) {
			getConnectionTableString();
		}
	}

	private void addComputationalDirectives() {
		
		writeProcessorConfig();
		writeMaximumMemory();
		// first lines
		if (jobname != null) {
			stringList.add("%chk="+jobname+".chk");
		}
	}

	private void writeProcessorConfig() {
		String processorCount = currentJob.getDirectives().outputProcessorCount();
		if (processorCount != null) {
			stringList.add("%"+NPROC+"="+processorCount);
		}
	}
	
	private void writeMaximumMemory() {
		String maximumMemory = currentJob.getDirectives().outputMaximumMemory();
		if (maximumMemory != null) {
			stringList.add("%"+MEM+"="+maximumMemory+currentJob.getDirectives().outputMemoryUnits());
		}
	}

	private String makeCommandLine(Job job) {
		String s = "#";
		String v = outputVerbosity();
		s += v;
		s += " "+currentJob.getQm().makeMethodAndBasis();
		if (CHECKPOINT.equals(job.getMoleculeRef())) {
			s += " "+"geom=checkpoint"+" "+"guess=read";
		}
		s += outputOperations();
		return s;
	}

	private String outputVerbosity() {
		String v = currentJob.getDirectives().outputVerbosity();
		if (v == null) {
			v = this.getDefaultVerbosity();
		} else if (v.toUpperCase().equals("BRIEF")) {
			v = BRIEF;
		} else if (v.toUpperCase().equals("FULL")) {
			v = FULL;
		} else if (v.toUpperCase().equals("NORMAL")) {
			v = NORMAL;
		}
		return v;
	}
	
	private String getDefaultVerbosity() {
		return "N";
	}

	// this is independent of other options
	private void getConnectionTableString() {
		for (CMLAtom atom : inputMolecule.getAtoms()) {
			StringBuilder sb = new StringBuilder();
			sb.append(" "+atom.getElementType()+" ");
			Point3 p3 = atom.getXYZ3();
			double[] a = p3.getArray();
			sb.append(a[0]+" ");
			sb.append(a[1]+" ");
			sb.append(a[2]);
			stringList.add(sb.toString());
		}
	}
	
	public String outputOperations() {
		String s = "";
		for (CMLParameter parameter : currentJob.getOperations().getParameterList()) {
			String dictRef = parameter.getDictRef();
			if (dictRef == null) {
				throw new RuntimeException("No dictRef on parameter/operation");
			} else if (dictRef.equals("JUNK")) {
				// fill this with any special commands
			} else if (Operations.keywordSet.contains(dictRef)) {
				s += " "+getOutput(parameter);
			} else {
				throw new RuntimeException("UNKNOWN Operation "+dictRef);
			}
		}
		return s;
	}
	
	protected String getOutput(CMLParameter parameter) {
		String s = NamespaceRefAttribute.getLocalName(parameter.getDictRef());
		s += outputQualifiers(parameter);
		return s;
	}

	private String outputQualifiers(CMLParameter parameter) {
		String s = "";
		String scalarValue = CMLUtil.getSingleValue(parameter, "./cml:scalar", CMLConstants.CML_XPATH);
		Nodes childNodes = parameter.query("./cml:parameter", CMLConstants.CML_XPATH);
		if (scalarValue != null) {
			s += CMLConstants.S_LBRAK;
			s += scalarValue;
			s += CMLConstants.S_RBRAK;
		} else if (childNodes.size() > 0) {
			s += CMLConstants.S_LBRAK;
			for (int i = 0; i < childNodes.size(); i++) {
				CMLParameter childParameter = (CMLParameter) childNodes.get(i);
				String dictRef = childParameter.getDictRef();
				dictRef = NamespaceRefAttribute.getLocalName(dictRef);
				scalarValue = CMLUtil.getSingleValue(childParameter, "./cml:scalar", CMLConstants.CML_XPATH);
				if (i > 0) {
					s += CMLConstants.S_COMMA;
				}
				s += dictRef;
				if (scalarValue != null) {
					s += CMLConstants.S_EQUALS+scalarValue;
				}
			}
			s += CMLConstants.S_RBRAK;
		}
		return s;
	}
	
	/**
	public void getWorkflowInput(boolean useHSRExtraBasisSets) {
		if (useHSRExtraBasisSets) {
			checkForCarbonyl();
		}

		
		addComputationalDirectives();
		stringList.add("#N RHF/STO-3G opt(loose) ");
		stringList.add("");
		stringList.add(title);
		stringList.add("");
		stringList.add("0 1");
		getConnectionTableString();
		stringList.add("");
		stringList.add("--Link1--");
		addComputationalDirectives();
		stringList.add("#N rmpw1pw91/6-31g(d,p) geom=checkpoint opt freq guess=read");
		stringList.add("");
		stringList.add("Optimisation using STO-3G coordinates and DFT");
		stringList.add("");
		stringList.add("0 1");
		stringList.add("");
		stringList.add("--Link1--");
		addComputationalDirectives();

		if (!useHSRExtraBasisSets) {
			stringList.add("#P rmpw1pw91/6-31g(d,p) geom=checkpoint guess=read");
			stringList.add("#P NMR scrf(cpcm,solvent="+solvent.getName()+")");
		} else {
			stringList.add("# rmpw1pw91/6-31g(d,p) NMR scrf(cpcm,solvent="+
					solvent.getName()+") ExtraBasis");
		}

		stringList.add("");
		stringList.add("Calculating  GIAO-shifts.  ");
		stringList.add("");
		stringList.add("0 1");

		if (useHSRExtraBasisSets) {
			stringList.add("C     0");
			stringList.add("SP     1     1.00");
			stringList.add("             0.05             1.00000000             1.00000000");
			stringList.add("****");
			if (hasCarbonyl) {
				stringList.add("O     0");
				stringList.add("SP     1     1.00");
				stringList.add("             0.070000              1.0000000              1.0000000");
				stringList.add("****");
			}
		}

		stringList.add("");
	}
	*/

//	private void checkForCarbonyl() {
//		for (CMLBond bond : inputMolecule.getBonds()) {
//			if (!CMLBond.isDouble(bond.getOrder())) {
//				continue;
//			}
//			boolean hasC = false;
//			boolean hasO = false;
//			for (CMLAtom atom : bond.getAtoms()) {
//				if ("C".equals(atom.getElementType())) {
//					hasC = true;
//				} else if ("O".equals(atom.getElementType())) {
//					hasO = true;
//				}
//			}
//			if (hasC && hasO) {
//				hasCarbonyl = true;
//				break;
//			}
//		}
//	}
}
