package org.xmlcml.cml.converters;

import static org.xmlcml.euclid.EuclidConstants.S_APOS;
import static org.xmlcml.euclid.EuclidConstants.S_EMPTY;
import static org.xmlcml.euclid.EuclidConstants.S_SPACE;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * 
 */
public class Command {

   private static final Logger LOG = Logger.getLogger(Command.class);

   private List<String> xPathList;

   /**
    *
    */
   public enum Verbosity {

      DEBUG,
      INFO,
      QUIET;
   }
   private Map<String, String> propertyMap = new HashMap<String, String>();
//   private Set<Class<?>> converterClassSet;
   private List<List<String>> argListList;
   private Verbosity verbosity = Verbosity.INFO;
   private String logfile;
   private Class<?> logger;
   private String propertyfile;
   private String infile;
   private String outfile;
   private String selectXPath;
   private String startDirectory;
   private String includesDirectory;
   private String setupDirectory;
   private String outputDirectory;
   private IOFileFilter fileFilter;
   private IOFileFilter dirFilter;
   private String fileIdExtraction;
   private String auxfileName;
   private String outSuffix;

   public String getOutSuffix() {
      return outSuffix;
   }

   public void setOutSuffix(String outSuffix) {
      this.outSuffix = outSuffix;
   }

   public String getAuxfileName() {
      return auxfileName;
   }

   public void setAuxfileName(String auxfileName) {
      this.auxfileName = auxfileName;
   }

   protected void createImplicitValues() {
      if (startDirectory != null) {
         if (infile != null) {
            LOG.warn("input file cannot be set with start Directory; cleared");
         }
      }
      if (outputDirectory != null) {
         if (outfile != null) {
            LOG.warn("output file cannot be set with output Directory; cleared");
         }
         if (startDirectory == null) {
            LOG.error("output directory cannot be set without startDirectory");
         }
      }
   }

   protected void summarize() {
      LOG.info("Command");

      if (Level.DEBUG.equals(LOG.getLevel())) {
         if (propertyMap.size() > 0) {
            LOG.info("Properties: ");
            for (String s : propertyMap.keySet()) {
               LOG.info("   " + s + "=" + propertyMap.get(s));
            }
         }
      }
      LOG.info("LOGFILE:       " + logfile);
      LOG.info("LOGGER:        " + logger);
      LOG.info("VERBOSITY:     " + verbosity);
      LOG.info("INFILE:        " + infile);
      LOG.info("OUTFILE:       " + outfile);
      LOG.info("SELECT(XPATH): " + selectXPath);
      LOG.info("STARTDIR:      " + startDirectory);
      LOG.info("INCLUDEDIR:    " + includesDirectory);
      LOG.info("SETUPDIR:      " + setupDirectory);
      LOG.info("OUTPUTDIR:     " + outputDirectory);
      LOG.info("FILEFILTER:    " + fileFilter);
      LOG.info("FILEIDEXT:     " + fileIdExtraction);
   }

   public boolean isDebug() {
      return verbosity == Verbosity.DEBUG;
   }

   public void setDebug(boolean debug) {
      this.verbosity = Verbosity.DEBUG;
   }

   public String getLogfile() {
      return logfile;
   }

   public void setLogfile(String logfile) {
      this.logfile = logfile;
   }

   public Class<?> getLogger() {
      return logger;
   }

   public String getPropertyfile() {
      return propertyfile;
   }

   public void setPropertyfile(String propertyfile) {
      this.propertyfile = propertyfile;
   }

   public boolean isQuiet() {
      return verbosity == Verbosity.QUIET;
   }

   public void setQuiet(boolean quiet) {
      this.verbosity = Verbosity.QUIET;
   }

   public String getOutfile() {
      return outfile;
   }

   public void setOutfile(String outfile) {
      this.outfile = outfile;
   }

   public String getInfile() {
      return infile;
   }

   public void setInfile(String infile) {
      List<String> files = new ArrayList<String>();
      files.add(infile);
      this.infile = infile;
   }

   public String getSelectXPath() {
      return selectXPath;
   }

   public void setSelectXPath(String selectXPath) {
      this.selectXPath = selectXPath;
   }

   public List<String> getXPathList() {
      return xPathList;
   }

   public void setXPathList(List<String> xPathList) {
      this.xPathList = xPathList;
   }

   public String getStartDirectory() {
      return startDirectory;
   }

   public void setStartDirectory(String startDirectory) {
      this.startDirectory = startDirectory;
   }

   public String getIncludesDirectory() {
      return includesDirectory;
   }

   public void setIncludesDirectory(String includesDirectory) {
      this.includesDirectory = includesDirectory;
   }

   public String getSetupDirectory() {
      return setupDirectory;
   }

   public void setSetupDirectory(String setupDirectory) {
      this.setupDirectory = setupDirectory;
   }

   public String getOutputDirectory() {
      return outputDirectory;
   }

   public void setOutputDirectory(String outputDirectory) {
      this.outputDirectory = outputDirectory;
   }

   public List<List<String>> getArgListList() {
      return argListList;
   }

   public String getArgString() {
      String argString = null;
      if (argListList != null && argListList.size() > 0) {
         List<String> argList = argListList.get(0);
         argString = S_EMPTY;
         for (String arg : argList) {
            argString += arg + S_SPACE;
         }
         argString = argString.trim();
      }
      return argString;
   }

//   private String getUnprotectedString(Object o) {
//      String sx = (String) o;
//      if (sx.startsWith(S_LBRAK) && sx.endsWith(S_RBRAK)) {
//         sx = sx.substring(1, sx.length() - 1);
//      }
//      return sx;
//   }

   public IOFileFilter getFileFilter() {
      return fileFilter;
   }

   public void setFileFilter(String fileFilterS) {
      if (fileFilterS == null) {
         throw new RuntimeException("null file filter");
      }
      if (fileFilterS.startsWith(S_APOS)) {
         fileFilterS = fileFilterS.substring(1);
      }
      if (fileFilterS.endsWith(S_APOS)) {
         fileFilterS = fileFilterS.substring(0, fileFilterS.length() - 1);
      }
      int idx = fileFilterS.lastIndexOf(File.separator);
      if (idx != -1) {
//			String dirFilterS = fileFilterS.substring(0, idx) ;
         fileFilterS = fileFilterS.substring(idx + 1);
//			dirFilter = new WildcardFileFilter(dirFilterS) ;
//			this.setDirFilter(dirFilter) ;
      }
      if (fileFilterS.length() > 0) {
         LOG.info("Assuming wildcard file filter");
         fileFilter = new WildcardFileFilter(fileFilterS);
         this.setFileFilter(fileFilter);
      }
   }

   public void setFileFilter(IOFileFilter fileFilter) {
      this.fileFilter = fileFilter;
   }

   /**
    * Sets the directory filter based on the wild card string for the individual
    * directory name, rather than the full path
    * @param dirFilterS
    */
   public void setDirFilter(String dirFilterS) {
      if (dirFilterS == null) {
         throw new RuntimeException("null directory filter");
      }
      if (dirFilterS.length() > 0) {
         LOG.info("Assuming wildcard directory filter " + dirFilterS);
         dirFilter = new WildcardFileFilter(dirFilterS);
         this.setDirFilter(dirFilter);
      }
   }

   public IOFileFilter getDirFilter() {
      return dirFilter;
   }

   public void setDirFilter(IOFileFilter dirFilter) {
      this.dirFilter = dirFilter;
   }

   public String getFileIdExtraction() {
      return fileIdExtraction;
   }

   public void setFileIdExtraction(String fileIdExtraction) {
      this.fileIdExtraction = fileIdExtraction;
   }

   public void info(String s) {
      if (!this.isQuiet()) {
         LOG.info(s);
      }
   }

   public void warn(String s) {
      if (!this.isQuiet()) {
         LOG.warn(s);
      }
   }

   public void debug(String s) {
      if (!this.isQuiet()) {
         LOG.debug(s);
      }
   }
}
