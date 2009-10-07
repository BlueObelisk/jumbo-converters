package org.xmlcml.cml.converters.cif;

import static org.xmlcml.cml.converters.cif.CrystalEyeConstants.COMPLETE_CML_MIME;
import static org.xmlcml.cml.converters.cif.CrystalEyeConstants.RAW_CML_MIME;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.cif.CrystalEyeUtils.DisorderType;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Generates a structure summary page as XHTML.
 * 
 * @author axiomsofchoice
 */
public class SingleCifSummary {

	private static Logger LOG = Logger.getLogger(SingleCifSummary.class);
	
	@SuppressWarnings("unused")
	private String publisherTitle;
	@SuppressWarnings("unused")
	private String journalTitle;
	@SuppressWarnings("unused")
	private String year;
	@SuppressWarnings("unused")
	private String issueNum;
	private String title;
	private String id;
	private String contactAuthor;
	private String authorEmail;
	private String doi;
	private String compoundClass;
	private String dateRecorded;
	private String formulaSum;
	private String formulaMoi;
	private String cellSetting;
	private String groupHM;
	@SuppressWarnings("unused")
	private String groupHall;
	private String temp;
	private String rObs;
	private String rAll;
	private String wRObs;
	private String wRAll;
	@SuppressWarnings("unused")
	private String crystComp;
	private String inchi;
	private String smiles;
	private DisorderType disordered;
	boolean polymeric;
	private String originalCifUrl;
	private String includesDirectoryRelative ;
	boolean notAllowedToRecommunicateCif;
	// TODO: Allow this to be set on the command-line
	private static String completecmlPath = "../completecml/" ;
	private String freemarkerTemplatePath ;
	private String cellLengtha ;
	private String cellLengthb ;
	private String cellLengthc ;
	private String cellAnglea ;
	private String cellAngleb ;
	private String cellAnglec ;
	private String zed ;
	private String zPrime ;	

	/**
	 * Construct a CIF structure summary page.
	 * 
	 * @param publisherTitle
	 * @param journalTitle
	 * @param year
	 * @param issueNum
	 * @param title
	 * @param id
	 * @param contactAuthor
	 * @param authorEmail
	 * @param doi
	 * @param compoundClass
	 * @param dateRecorded
	 * @param formulaSum
	 * @param formulaMoi
	 * @param cellSetting
	 * @param groupHM
	 * @param groupHall
	 * @param temp
	 * @param obs
	 * @param all
	 * @param obs2
	 * @param all2
	 * @param crystComp
	 * @param inchi
	 * @param smiles
	 * @param disordered
	 * @param polymeric
	 * @param originalCifUrl
	 * @param notAllowedToRecommunicateCif
	 * @param includesDirectoryRelative
	 * @param freemarkerTemplatePath
	 * @param cellLengtha
	 * @param cellLengthb
	 * @param cellLengthc
	 * @param cellAnglea
	 * @param cellAngleb
	 * @param cellAnglec
	 * @param zed
	 * @param zPrime
	 */
	public SingleCifSummary(String publisherTitle, String journalTitle, String year, String issueNum,
							String title, String id, String contactAuthor, String authorEmail, String doi, 
							String compoundClass, String dateRecorded, String formulaSum, String formulaMoi, 
							String cellSetting, String groupHM, String groupHall, String temp, String obs, 
							String all, String obs2, String all2, String crystComp, String inchi, 
							String smiles, DisorderType disordered, boolean polymeric, String originalCifUrl, 
							boolean notAllowedToRecommunicateCif, String includesDirectoryRelative,
							String freemarkerTemplatePath,
							String cellLengtha, String cellLengthb, String cellLengthc,
							String cellAnglea, String cellAngleb, String cellAnglec,
							String zed, String zPrime) {
		super();
		this.publisherTitle = publisherTitle;
		this.journalTitle = journalTitle;
		this.year = year;
		this.issueNum = issueNum;
		this.title = title;
		this.id = id;
		this.contactAuthor = contactAuthor;
		this.authorEmail = authorEmail;
		this.doi = doi;
		this.compoundClass = compoundClass;
		this.dateRecorded = dateRecorded;
		this.formulaSum = formulaSum;
		this.formulaMoi = formulaMoi;
		this.cellSetting = cellSetting;
		this.groupHM = groupHM;
		this.groupHall = groupHall;
		this.temp = temp;
		rObs = obs;
		rAll = all;
		wRObs = obs2;
		wRAll = all2;
		this.crystComp = crystComp;
		this.inchi = inchi;
		this.smiles = smiles;
		this.disordered = disordered;
		this.polymeric = polymeric;
		this.originalCifUrl = originalCifUrl;
		this.notAllowedToRecommunicateCif = notAllowedToRecommunicateCif;
		this.includesDirectoryRelative = includesDirectoryRelative;
		this.freemarkerTemplatePath = freemarkerTemplatePath ;
		this.cellLengtha = cellLengtha;
		this.cellLengthb = cellLengthb;
		this.cellLengthc = cellLengthc;
		this.cellAnglea = cellAnglea;
		this.cellAngleb = cellAngleb;
		this.cellAnglec = cellAnglec;
		this.zed = zed ;
		this.zPrime = zPrime;
		
	}

	/**
	 * Default constructor; does nothing.
	 *  
	 */
	public SingleCifSummary() {
		;
	}
	
	/**
	 * Generates an XHTML {@link String} fragment for the disordered section of the structure page.
	 * 
	 * @return 		the (well-formed) XHTML &lt;div&gt; fragment for the disordered section
	 */
	private String getDisorderedSection() {
		
		String output = "";
		if (disordered.equals(DisorderType.UNPROCESSED)) {
			output = "<div style=\"border: 1px dashed red; text-align: center; font-size: 12px; background: #ffbbbb;\">"+
					"<p style=\"color: red;\">We could not resolve the disorder in this crystal structure.</p>"+
				"</div>";
		} else if (disordered.equals(DisorderType.PROCESSED)) {
			output = "<div style=\"border: 1px dashed green; text-align: center; font-size: 12px; background: #99ff99;\">"+
					"<p style=\"color: green;\">The structure displayed is the major occupied structure from the crystal.</p>"+
				"</div>";
		}
		return output;
	}
	
	/**
	 * Generates an XHTML {@link String} fragment for the polymeric section of the structure page.
	 * 
	 * @return		the (well-formed) XHTML &lt;div&gt; fragment for the polymeric section
	 */
	private String getPolymericSection() {
	
		if (polymeric) {
			return "<div style=\"border: 1px dashed blue; text-align: center; font-size: 12px; background: #bbccff;\">"+
				"<p style=\"color: blue;\">This structure is polymeric.</p>"+
				"</div>";
		} else {
			return "";
		}
	}
	
	/**
	 * Generates an XHTML for the structure page using a 
	 * <a href="http://freemarker.sourceforge.net/">FreeMarker</a> template.
	 * 
	 * @return		the (well-formed) XHTML {@link String} for the entire structure page
	 */
	public String getWebpage() {
		
		freemarkerTemplatePath = "D:/workspace/jumbo-converters/main/resources/C3DeR.ftl";
		if (freemarkerTemplatePath == null) {
			LOG.error("Must set freemakerTemplatePath before writing webpage");
			return null;
		}
		String disorderedSection = getDisorderedSection();
		String polymericSection = getPolymericSection();
				
		// make smiles td
		String smilesSection = "";
		
		if (!"".equals(smiles) && smiles != null) {
			smilesSection = "<tr><td><span class=\"title\">SMILES:</span></td>"+
								"<td>"+smiles+"</td></tr>";
		}
		String inchiSection = "";
		if (!"".equals(inchi) && inchi != null) {
			inchiSection = "<tr><td><span class=\"title\">InChI:</span></td>"+
								"<td>"+inchi+"</td></tr>";
		}
		String cifLinkSection = "";
		if (!notAllowedToRecommunicateCif) {
			cifLinkSection = "<tr><td>CIF (<a href=\"./"+id+".cif\">cached</a> / <a href=\""+originalCifUrl+"\">original</a>)</td></tr>";
		}
		
		//Freemarker template stuff
		// Create the root hash
		Map<String,String> root = new HashMap<String,String>();
		// Put string ``title'' into the root
		root.put("title", title);			//title of the page
		if (includesDirectoryRelative != null) {
			root.put("jmoldir", includesDirectoryRelative);			//prefix for the location of the jmol files
		} else {
			LOG.warn("includesDirectory must be set for Jmol");
		}
		root.put("id", id);			//Used for structure ID in javascript
		root.put("COMPLETE_CML_MIME", COMPLETE_CML_MIME);	//Used for structure ID in javascript
		root.put("RAW_CML_MIME", RAW_CML_MIME);		//Used for structure ID in javascript
		root.put("COMPLETE_CML_MIME", COMPLETE_CML_MIME);	//Used for structure ID in javascript
		root.put("doi", doi);			//DOI string
		root.put("compoundClass", compoundClass);		//Class of compound
		root.put("dateRecorded", dateRecorded);	//date Recorded
		root.put("contactAuthor", contactAuthor);	//contact author
		root.put("authorEmail", authorEmail.toLowerCase());		//
		root.put("formulaSum", formulaSum);		//Chemical formula sum
		root.put("formulaMoi", formulaMoi);		//Chemical formula moiety
		root.put("cellSetting", cellSetting);		//Crystal system
		root.put("groupHM", groupHM);			//H-M space group
		root.put("collectionTemp", temp);
		root.put("rObs", rObs);
		root.put("rAll", rAll);
		root.put("wRObs", wRObs);
		root.put("wRAll", wRAll);
//		root.put("crystComp", crystComp);		//?
		root.put("cifLinkSection", cifLinkSection);
		root.put("disorderedSection", disorderedSection);
		root.put("polymericSection", polymericSection);
		root.put("completecmlPath", completecmlPath);		//Directs jmol to the correct file
		root.put("inchiSection", inchiSection);
		root.put("smilesSection", smilesSection);
		root.put("cellLengtha", cellLengtha);
		root.put("cellLengthb", cellLengthb);
		root.put("cellLengthc", cellLengthc);
		root.put("cellAnglea", cellAnglea);
		root.put("cellAngleb", cellAngleb);
		root.put("cellAnglec", cellAnglec);
		root.put("groupLeader", "");
		root.put("chemist", "");
		root.put("crystallographer", "");
		root.put("instrument", "");
		root.put("zPrime", zPrime);
		root.put("zed", zed);
		

		Configuration cfg = new Configuration();
		// Specify the data source where the template files come from.
		// Here I set a file directory for it:
		try {
			cfg.setDirectoryForTemplateLoading(new File(freemarkerTemplatePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Specify how templates will see the data-model.
		cfg.setObjectWrapper(new DefaultObjectWrapper());  
		
		//Get the template
		Template temp = null;
		try {
			// FIXME: get this file name + relative path from a .properties file or the command-line args
			temp = cfg.getTemplate("/main/resources/C3DeR.ftl");
		} catch (IOException e) {
			throw new RuntimeException("Cannot read template file", e);
		}
		
		Writer out = new StringWriter() ;
		try {
			temp.process(root, out);
		} catch (TemplateException e) {
			throw new RuntimeException("TemplateException occurred", e);
		} catch (IOException e) {
			throw new RuntimeException("IOException occurred", e);
		}
		try {
			out.flush();
		} catch (IOException e) {
			throw new RuntimeException("IOException occurred", e);
		}  		
		
		String page = out.toString() ;
		
		return page;
	}
		
}
