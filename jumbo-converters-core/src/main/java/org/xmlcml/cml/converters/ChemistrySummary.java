package org.xmlcml.cml.converters;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Generates a structure summary page as XHTML.
 * 
 * @author pm286
 */
public abstract class ChemistrySummary {

   private static Logger LOG = Logger.getLogger(ChemistrySummary.class);
//	@SuppressWarnings("unused")
//	String jmoldir = "../";
   protected String title = "none";
   protected String id = "none";
   protected String author = "none";
   protected String email = "none";
   protected String website = "none";
   protected String rights = "none";
   protected String doi = "none";
   protected String dateRecorded = "none";
   protected String formulaSum = "none";
   protected String formulaInline = "none";
   protected String basisSet = "none";
   protected String method = "none";
   protected String dipole = "none";
   protected String inchi = "none";
   protected String smiles = "none";
   protected String hfvalue = "none";
   protected String pointGroup = "none";
   protected String includesDirectoryRelative = "../";
   // TODO: Allow this to be set on the command-line
   static protected String completecmlPath = ".";

   /**
    */
   public ChemistrySummary() {
   }

   /**
    * Generates an XHTML for the structure page using a
    * <a href="http://freemarker.sourceforge.net/">FreeMarker</a> template.
    *
    * @return		the (well-formed) XHTML {@link String} for the entire structure page
    */
   public Map<String, String> toMap() {
      // make smiles td
      @SuppressWarnings("unused")
      String smilesSection = "";

      if (!"".equals(smiles) && smiles != null) {
         smilesSection = "<tr><td><span class=\"title\">SMILES:</span></td>" +
                 "<td>" + smiles + "</td></tr>";
      }
      @SuppressWarnings("unused")
      String inchiSection = "";
      if (!"".equals(inchi) && inchi != null) {
         inchiSection = "<tr><td><span class=\"title\">InChI:</span></td>" +
                 "<td>" + inchi + "</td></tr>";
      }

      Map<String, String> root = new HashMap<String, String>();
      // Put string ``title'' into the root
      root.put("title", title);			//title of the page
      if (includesDirectoryRelative != null) {
         root.put("jmoldir", includesDirectoryRelative);			//prefix for the location of the jmol files
      } else {
         LOG.warn("includesDirectory must be set for Jmol");
      }
      root.put("id", id);			//Used for structure ID in javascript
      root.put("doi", doi);			//DOI string
      root.put("author", author);
      root.put("email", email);
      root.put("website", website);
      root.put("rights", rights);
      root.put("dateRecorded", "");	//date Recorded
      root.put("formulaSum", formulaSum);		//author formula
      root.put("formulaInline", formulaInline);		//author formula
      root.put("basisSet", basisSet);
      root.put("method", method);
      root.put("dipole", dipole);
      root.put("hfvalue", hfvalue);
      root.put("pointGroup", pointGroup);
      root.put("completecmlPath", completecmlPath);		//Directs jmol to the correct file
      root.put("inchiSection", "");
      root.put("smilesSection", "");

      return root;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getAuthor() {
      return author;
   }

   public void setAuthor(String author) {
      this.author = author;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public String getWebsite() {
      return website;
   }

   public void setWebsite(String website) {
      this.website = website;
   }

   public String getRights() {
      return rights;
   }

   public void setRights(String rights) {
      this.rights = rights;
   }

   public String getDoi() {
      return doi;
   }

   public void setDoi(String doi) {
      this.doi = doi;
   }

   public String getDateRecorded() {
      return dateRecorded;
   }

   public void setDateRecorded(String dateRecorded) {
      this.dateRecorded = dateRecorded;
   }

   public String getFormulaSum() {
      return formulaSum;
   }

   public void setFormulaSum(String formulaSum) {
      this.formulaSum = formulaSum;
   }

   public String getFormulaInline() {
      return formulaInline;
   }

   public void setFormulaInline(String formulaInline) {
      this.formulaInline = formulaInline;
   }

   public String getBasisSet() {
      return basisSet;
   }

   public void setBasisSet(String basisSet) {
      this.basisSet = basisSet;
   }

   public String getMethod() {
      return method;
   }

   public void setMethod(String method) {
      this.method = method;
   }

   public String getDipole() {
      return dipole;
   }

   public void setDipole(String dipole) {
      this.dipole = dipole;
   }

   public String getInchi() {
      return inchi;
   }

   public void setInchi(String inchi) {
      this.inchi = inchi;
   }

   public String getSmiles() {
      return smiles;
   }

   public void setSmiles(String smiles) {
      this.smiles = smiles;
   }

   public String getHfvalue() {
      return hfvalue;
   }

   public void setHfvalue(String hfvalue) {
      this.hfvalue = hfvalue;
   }

   public String getPointGroup() {
      return pointGroup;
   }

   public void setPointGroup(String pointGroup) {
      this.pointGroup = pointGroup;
   }

   public String getIncludesDirectoryRelative() {
      return includesDirectoryRelative;
   }

   public void setIncludesDirectoryRelative(String includesDirectoryRelative) {
      this.includesDirectoryRelative = includesDirectoryRelative;
   }

   public static String getCompletecmlPath() {
      return completecmlPath;
   }

   public static void setCompletecmlPath(String completecmlPath) {
      ChemistrySummary.completecmlPath = completecmlPath;
   }
}
