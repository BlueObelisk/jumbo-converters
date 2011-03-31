package org.xmlcml.cml.converters.compchem.gaussian.logold;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.converters.compchem.gaussian.GaussianLink;
import org.xmlcml.cml.element.CMLMolecule;

/** reads a Gaussian log file
 * This is not deterministic as file depends on what version and machine
 * wrote the file.
 * @author pm286
 *
 */

public class GaussianLog implements CMLConstants {
	@SuppressWarnings("unused")
	private static Logger LOG = Logger.getLogger(GaussianLog.class);
//	Leave Link  101 at Mon Mar 30 15:42:29 2009, MaxMem=    6291456 cpu:       0.1	 * @param line
	private static Pattern END_PATTERN = Pattern.compile("^Leave Link  *(.*) at.*");
//	static Pattern startPattern = Pattern.compile("^\\(Enter /opt//g03/l(.*).exe\\)");
	private static Pattern START_PATTERN = Pattern.compile("^.*\\(Enter .*/l([0-9]+)\\.exe\\)");
//	 Entering Link 1 = /opt//g03/l1.exe PID=     14008.
	private static Pattern START_PATTERN1 = Pattern.compile("^.*Entering Link .*/l([0-9]+)\\.exe .*");

	private List<GaussianLink> glinkList = new ArrayList<GaussianLink>();
	public List<GaussianLink> getGlinkList() {
		return glinkList;
	}

	@SuppressWarnings("unused")
	private CMLMolecule molecule = null;

	public GaussianLog() {
//		CMLElement element = null;
	}
	
	public void readLines(List<String> lines, CMLMolecule molecule) {
//		this.molecule = molecule;
		GaussianLink glink = null;
		for (String line : lines) {
			String startLinkName = isStartLink(line);
			if (startLinkName != null) {
				glink = new GaussianLink(molecule);
				glink.setLinkName(startLinkName);
				glinkList.add(glink);
			}
			// no explicit link
			if (glink == null) {
				glink = new GaussianLink(molecule);
				glinkList.add(glink);
			}
			// add link
			glink.add(line);
			//
			String endLinkName = isEndLink(line);
			if (endLinkName != null) {
				if (!endLinkName.equals(startLinkName)) {
					System.out.println(startLinkName+" != "+endLinkName);
				}
				glink = null;
			}
		}
//		LOG.debug("GLINKS: "+glinkList.size());
//		for (GaussianLink glink0 : glinkList) {
//			glink0.convert2CML();
//		}
		
	}
	
	
	
	List<GaussianLink> getLinks(String link) {
		List<GaussianLink> linkList = new ArrayList<GaussianLink>();
		for (GaussianLink glink : glinkList) {
			if (glink.getLinkName().equals(link)) {
				linkList.add(glink);
			}
		}
		return linkList;
	}
	/**
Leave Link  101 at Mon Mar 30 15:42:29 2009, MaxMem=    6291456 cpu:       0.1	 * @param line
		 * @return
		 */
	private String isEndLink(String line) {
		String endLinkName = null;
		Matcher matcher = END_PATTERN.matcher(line);
		if (matcher.matches()) {
			endLinkName = matcher.group(1);
		}
		return endLinkName;
	}
	
	/**
(Enter /opt//g03/l103.exe)
		 * @return
		 */
	private String isStartLink(String line) {
		String startLinkName = null;
		Matcher matcher = START_PATTERN.matcher(line);
		Matcher matcher1 = START_PATTERN1.matcher(line);
		if (matcher.matches()) {
			startLinkName = matcher.group(1);
		} else if (matcher1.matches()) {
			startLinkName = matcher1.group(1);
		}
		return startLinkName;
	}
}
