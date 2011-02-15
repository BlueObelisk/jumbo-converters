package org.xmlcml.cml.converters.compchem.gaussian.logold;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nu.xom.Element;
import nu.xom.Nodes;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.compchem.AbstractCompchem2CMLConverter;
import org.xmlcml.cml.converters.compchem.gaussian.GaussianCommon;
import org.xmlcml.cml.converters.compchem.gaussian.GaussianLink;
import org.xmlcml.cml.converters.compchem.gaussian.archive.GaussianArchive2CMLConverter;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLModule;
import org.xmlcml.cml.element.CMLMolecule;

public class GaussianLog2CMLConverterOld  extends AbstractCompchem2CMLConverter {
	private static final Logger LOG = Logger.getLogger(GaussianLog2CMLConverterOld.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	// really awful, but ant cannot pick up classpath
	public final static String DICT_FILE = 
		"D:/workspace/jumbo-converters/src/main/resources/org/xmlcml/cml/converters/compchem/gaussian/gaussianArchiveDict.xml";
	
	private CMLCml topCml;
	
	public GaussianLog2CMLConverterOld() {
		
	}

   @Override
   protected AbstractCommon getCommon() {
	   return new GaussianCommon();
   }

	public Type getInputType() {
		return Type.GAU_LOG;
	}

	public Type getOutputType() {
		return Type.CML;
	}

	/**
	 * reads a Gaussian log file and converts to CML
	 * @param lines Gaussian log line by line
	 * @return <cml> element containing all Gaussian links
	 * 
	 */
	public Element convertToXML(List<String> lines) {
		topCml = new CMLCml();
		GaussianLog gaussianLog = new GaussianLog();
		CMLMolecule molecule = getMolecule(lines);
		gaussianLog.readLines(lines, molecule);
		addModules(topCml, gaussianLog.getGlinkList());
		analyze();
		System.out.println("=======================================================");
		return topCml;
	}
	
	private void addModules(CMLCml topCml, List<GaussianLink> glinkList) {
		for (GaussianLink glink : glinkList) {
			CMLModule module = glink.convert2CML();
			topCml.appendChild(module);
		}
	}
	
	private void analyze() {
		Nodes links = topCml.query("./cml:module", CMLConstants.CML_XPATH);
		List<CMLModule> moduleList = new ArrayList<CMLModule>();
		for (int i = 0; i < links.size(); i++) {
			moduleList.add((CMLModule) links.get(i));
		}
		Map<String, List<Integer>> frequencies = new HashMap<String, List<Integer>>();
		for (int i = 0; i < moduleList.size(); i++) {
			CMLModule start = moduleList.get(i);
			String dictRefI = start.getDictRef();
			List<Integer> nextList = frequencies.get(dictRefI);
			if (nextList == null) {
				nextList = new ArrayList<Integer>();
				frequencies.put(dictRefI, nextList);
			}
			for (int j = i+1; j < moduleList.size(); j++) {
				CMLModule next = moduleList.get(j);
				if (start.getDictRef().equals(next.getDictRef())) {
					nextList.add(new Integer(j - i));
					break;
				}
			}
		}
//		for (String dictRef : frequencies.keySet()) {
//			List<Integer> deltaList = frequencies.get(dictRef);
//			System.out.print(">> "+dictRef+" .. ");
//			for (Integer i : deltaList) {
//				System.out.print(" "+i);
//			}
//			System.out.println();
//		}
	}
	
	private CMLMolecule getMolecule(List<String> lines) {
		GaussianArchive2CMLConverter gaussianArchiveConverter = new GaussianArchive2CMLConverter();
		Element element = gaussianArchiveConverter.convertToXML(lines);
		Nodes molecules = element.query(".//cml:molecule", CMLConstants.CML_XPATH);
		return (molecules.size() == 0) ? null : (CMLMolecule) molecules.get(molecules.size()-1);
	}

}
