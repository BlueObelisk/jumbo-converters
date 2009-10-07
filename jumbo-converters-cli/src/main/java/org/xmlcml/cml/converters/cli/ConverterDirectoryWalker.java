package org.xmlcml.cml.converters.cli;

import org.xmlcml.cml.converters.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.euclid.Util;

/**
 * A {@link DirectoryWalker} that uses an {@link AbstractConverer} to convert files
 * it finds in the directory structure in-place.
 * 
 * @author 
 *
 */
public class ConverterDirectoryWalker extends DirectoryWalker {

   private static final Logger LOG = Logger.getLogger(ConverterDirectoryWalker.class);

   static {
      LOG.setLevel(Level.DEBUG);
   }

   /**
    *
    *
    * @author
    */
   public enum WalkerLog {

      FULL_NAME,
      SHORT_NAME,
      FILE_READ,
      FILE_WRITE,
      DIRECTORY_START,
      DIRECTORY_END,
      CONVERTER_START,
      CONVERTER_END,
      READ_EXCEPTION,
      PROCESS_EXCEPTION,
      WRITE_EXCEPTION,
   };
   public static final String METADATA_FILE = "metadata.cml";
   private volatile boolean cancelled = false;
   private Set<WalkerLog> walkerLog = null;
   private int maxFileCount;
   private FileManager fileManager;
   private List<FileManager> directoryReadFileList;
//	private List<FileManager> directoryProcessedFileList;
   private IOFileFilter dirFilter;
   private IOFileFilter fileFilter;
   private String fileFilterExtension;
   private Class<?> converterClass;
   private Converter converter;
   private File startDirectory;
   private File includesDirectory;
   /**
    * Path to the setup directory which contains, for example, style sheets and FreeMarker templates.
    */
   private String setupDirectory;
   private File outputDirectory;
   private File inputDirectory;
   private String inputSuffix;
   private String outputSuffix;
   private String indexfileName;
   private Index index;
   private ConverterList converterList;
   private boolean allowDuplicates = true;
   private boolean forceConvert = true;
   private Command command;
//    private static Set<String> morganSet = new HashSet<String>();
   private Aggregator converterAsAggregator;
   private AbstractIndexer converterAsIndexer;
   private AbstractSplitter converterAsSplitter;
   private File metadataFile;
   private CMLElement metadataCml;

   /** Construct an instance with a directory and a file filter.
    *  and an optional limit on the depth navigated to.
    */
   public ConverterDirectoryWalker(
           String inputSuffix,
           String outputSuffix,
           Class<?> converterClass,
           IOFileFilter directoryFilter,
           IOFileFilter fileFilter,
           int depthLimit,
           boolean forceConvert,
           Command command) {
      super(directoryFilter, fileFilter, depthLimit);
      setCommand(command);
      debug("Dir filter." + directoryFilter);
      debug("File filter." + fileFilter);
      this.inputSuffix = inputSuffix;
      this.outputSuffix = outputSuffix;
      this.dirFilter = directoryFilter;
      this.fileFilter = fileFilter;
      this.setConverterClass(converterClass);
      this.forceConvert = forceConvert;

      init();
   }

   /**
    *
    */
   private void init() {
      walkerLog = new HashSet<WalkerLog>();
      walkerLog.add(WalkerLog.CONVERTER_START);
      walkerLog.add(WalkerLog.DIRECTORY_START);
      walkerLog.add(WalkerLog.CONVERTER_END);
      walkerLog.add(WalkerLog.FULL_NAME);
      walkerLog.add(WalkerLog.READ_EXCEPTION);
      walkerLog.add(WalkerLog.PROCESS_EXCEPTION);
      walkerLog.add(WalkerLog.WRITE_EXCEPTION);
      maxFileCount = -1;	// indefinite
   }

   /** main entry point for debugging the ConverterDirectoryWalker class
    * @param args
    */
   public static void main(String[] args) throws IOException {
      LOG.info("<startDirectory> <insuff> <outputDirectory> <outsuff> <converter>");
      String dirS = args[0];
      String insuff = args[1];
      String outputDirS = args[2];
      String outsuff = args[3];
      String converterName = args[4];
      File dir = new File(dirS);
      if (dir != null) {
         try {
            ConverterDirectoryWalker walker = new ConverterDirectoryWalker(
                    insuff, outsuff, Class.forName(converterName), null, null, -1, true, null);
            walker.setStartDirectory(dir);
            walker.setOutputDirectory(new File(outputDirS));
            walker.start();
         } catch (Exception e) {
            throw new RuntimeException("bad class", e);
         }
      }
   }

   /**
    *
    * @return
    */
   public boolean isForceConvert() {
      return forceConvert;
   }

   /**
    *
    */
   private void ensureFileManager() {
      if (fileManager == null) {
//			fileManager = new FileManager(converterClass);
         fileManager = new FileManager();
      }
   }

// =================handlers=====================
   /** invoked at the start of processing.
    */
   @SuppressWarnings("unchecked")
   protected void handleStart(File startDirectory, Collection results, Converter converter) throws IOException {
      if (walkerLog.contains(WalkerLog.CONVERTER_START)) {
         debug("START DIRECTORY: " + getName(startDirectory));
      }
      if (command == null) {
         throw new RuntimeException("Null command");
      }
      if (converter == null) {
         throw new RuntimeException("Null converter");
      }
      initialiseIndexerAggregator();
   }

   /**
    *
    */
   private void initialiseIndexerAggregator() {
      converterAsAggregator = null;
      if (converter instanceof Aggregator) {
         converterAsAggregator = (Aggregator) converter;
         converterAsAggregator.setXPathList(((Command) converter.getCommand()).getXPathList());
      }
      if (converter instanceof AbstractIndexer) {
         index = new Index(allowDuplicates);
         converterAsIndexer = ((AbstractIndexer) converter);
         converterAsIndexer.setIndex(index);
      } else if (converter instanceof AbstractAggregator) {
         converterAsAggregator = ((AbstractAggregator) converter);
         converterList = new ConverterList();
         ((AbstractAggregator) converterAsAggregator).setConverterList(converterList);
      } else if (converter instanceof AbstractSplitter) {
         converterAsSplitter = ((AbstractSplitter) converter);
      }
   }

   /** invoked to determine if a directory should be processed.
    */
   @SuppressWarnings("unchecked")
   protected boolean handleDirectory(File directory, int depth,
           Collection results) throws IOException {
      boolean result = true;
      // skip algorithm goes here
      if (!result) {
         LOG.info("SKIPPED: " + directory.getName());
      }

      return result;
   }

   /** invoked at the start of processing each directory.
    * manages log file
    * reads metadata.cml
    */
   @SuppressWarnings("unchecked")
   protected void handleDirectoryStart(File directory, int depth, Collection results) {
      if (walkerLog.contains(WalkerLog.DIRECTORY_START) && !command.isQuiet()) {
         for (int i = 0; i < depth; i++) {
            Util.print("..");
         }
         LOG.debug(">> " + directory.getName());
      }
      directoryReadFileList = new ArrayList<FileManager>();
//    	directoryProcessedFileList = new ArrayList<FileManager>();

      if (Type.DIRECTORY.equals(converter.getInputType())) {
         ((AbstractConverter) converter).processDirectory(directory);
      }
      initialiseIndexerAggregator();

      addMetadata(directory);
   }

   /**
    * @param directory
    * @throws RuntimeException
    */
   private void addMetadata(File directory) throws RuntimeException {
      metadataFile = new File(directory, METADATA_FILE);
      metadataCml = null;
      if (!metadataFile.exists()) {
         metadataFile = null;
         info("No metadata file: " + METADATA_FILE);
      } else {
         try {
            metadataCml =
                    (CMLElement) new CMLBuilder().build(metadataFile).getRootElement();
            info("Created metadata from: " + METADATA_FILE);
            converter.setMetadataCml(metadataCml);
         } catch (Exception e) {
            throw new RuntimeException("Cannot parse/read " + METADATA_FILE + "; " + e.getMessage());
         }
      }
   }

   /**
    * This method is invoked for each (non-directory) file.
    *
    *  @param file
    *  @param depth the maximum nesting depth for directories that
    *  the {@link DirectoryWalker} can go
    *  @param results
    */
   @Override
   @SuppressWarnings("unchecked")
   protected void handleFile(File file, int depth, Collection results) throws IOException {

      inputDirectory = file.getParentFile();
      ConverterLog converterLog = converter.getConverterLog();
      List<Integer> fileLimits = ((ConverterCommandLine)converter.getCommand()).getFileLimits();

      // In the case that fileCount exceeded abort before processing.
      if (maxFileCount >= 0 && results.size() > maxFileCount) {
         info("Maximum count exceeded: " + results.size());
         cancel();
      }
      long insize = file.length();
      if (insize < fileLimits.get(0) || insize > fileLimits.get(1)) {
         converterLog.addToLog(Level.INFO, "did not read file (size = " + insize + ")");
         return;
      }
      String outputFilename = ensureConverterClassAndOutputFile(file, depth);
      String errString = null;
      File outfile = null;
      if (converter instanceof AbstractSplitter) {
         converterAsSplitter.setFiles(outputDirectory, file);
         // FIXME should not need this
         outfile = new File("dummy");
         converter.convert(file, outfile);
      } else if (outputFilename != null) {
         File eventualOutputFile = new File(outputFilename);

         // Do up to date check.
         long inLastModified = file.lastModified();
         long outLastModified = eventualOutputFile.lastModified();

         // If conversion needs doing
         if (forceConvert || (inLastModified > outLastModified)) {
//	    		debug("CONVERTER: "+converter+" ... "+converter.getCommand());
            converterLog.addToLog(
                    Level.INFO, "reading " + file.getAbsolutePath());
            outfile = eventualOutputFile;

            // Inject the correct relative directory so that html pages at different
            // levels can all refer to the same includes files
            if (includesDirectory != null) {
               String incsDir = includesDirectory.getAbsolutePath();
               String inputDir = inputDirectory.getAbsolutePath();

               String subStr = inputDir.substring(incsDir.length(), inputDir.length());

               String delimitter = File.separator;
               int delimOccurrences = 0;
               int start = subStr.indexOf(delimitter);
               while (start != -1) {
                  delimOccurrences++;
                  start = subStr.indexOf(delimitter, start + delimitter.length());
               }

               String incDirRel = "";
               for (int i = 0; i < delimOccurrences; i++) {
                  incDirRel += "../";
               }

               converter.setIncludesDirectoryRelative(incDirRel);
            }

            converter.setSetupDirectoryAbsolute(this.setupDirectory);

            try {
//	    			if (converter instanceof AbstractSplitter) {
//	    				converterAsSplitter.setFiles(outputDirectory, file);
//		    			converter.convert(file, outfile);
//	    			} else {
               try {
                  converter.convert(file, outfile);
                  if (outfile != null) {
                     if (!command.isQuiet()) {
                        info("wrote>> " + outfile);
                     }
                     converterLog.addToLog(
                             Level.INFO, "wrote " + outfile.getAbsolutePath());
                  }
               } catch (Exception e) {
                  e.printStackTrace();
                  LOG.error("Conversion failed: " + e.getMessage());
               }
            } catch (Exception ex) {
               ex.printStackTrace();
               errString = "Conversion FROM: " + file.getAbsolutePath() + " to " + outfile.getAbsolutePath() + " failed because [" + ex.getCause() + " ... " + ex.getMessage() + "]";
            }
         } else {
            converterLog.addToLog(
                    Level.INFO, "Skipped " + file.getAbsolutePath());
         }
         // delete strange length file
         if (outfile != null) {
            long outsize = outfile.length();
            if (outsize < fileLimits.get(2) || outsize > fileLimits.get(3)) {
               converterLog.addToLog(Level.INFO, "did not write file (size = " + outsize + ")");
               FileUtils.deleteQuietly(outfile);
            }

         }
      } else if (converter instanceof AbstractIndexer) {
         try {
            converter.convert(file, outfile);
            converter.getConverterLog().addToLog(
                    Level.INFO, "indexed " + file.getAbsolutePath());
         } catch (Exception ex) {
            errString = "Indexing failed on file: " + file.getAbsolutePath() + "[" + ex.getCause() + " ... " + ex.getMessage() + "]";
            converter.getConverterLog().addToLog(
                    Level.ERROR, "failed to index " + file.getAbsolutePath());
         }
      } else if (converter instanceof AbstractAggregator) {
         try {
            converter.convert(file, outfile);
            converter.getConverterLog().addToLog(
                    Level.INFO, "aggregated " + file.getAbsolutePath());
         } catch (Exception ex) {
            errString = "Aggregation failed on file: " + file.getAbsolutePath() + "[" + ex.getCause() + " ... " + ex.getMessage() + "]";
            converter.getConverterLog().addToLog(
                    Level.ERROR, "failed to aggregate " + file.getAbsolutePath());
         }
      }
      if (errString != null) {
         LOG.error(errString);
         results.add(errString);
      }
      converter.getConverterLog().addToLog(
              Level.INFO, logWrite(file, depth));
   }

   /**
    *
    * @param file
    * @param depth
    * @return
    */
   private String ensureConverterClassAndOutputFile(File file, int depth) {
      String fileId;
      info("<<reading: " + file);
//		fileManager = new FileManager(converterClass);
      fileManager = new FileManager();
      fileManager.setFileIdExtraction(command.getFileIdExtraction());
      fileManager.setInputFile(file);
      fileId = fileManager.getFileId();
      converter.setFileId(fileId);
      fileManager.setInputExtension(inputSuffix);
      fileManager.setOutputDirectory(outputDirectory);
      fileManager.setOutputExtension(outputSuffix);
      String outputFilename = fileManager.createOutputFileName(file);
      directoryReadFileList.add(fileManager);
      converter.getConverterLog().addToLog(Level.INFO, logRead(file, depth));
      return outputFilename;
   }

   /**
    *
    * @param outputFile
    * @param depth
    * @return
    */
   private String logWrite(File outputFile, int depth) {
      StringBuilder sb = new StringBuilder();
      sb.append("wrote ");
      for (int i = 0; i < depth + 1; i++) {
         sb.append("  ");
      }
      sb.append(" " + outputFile.getName());
      return sb.toString();
   }

   /**
    *
    * @param file
    * @param depth
    * @return
    */
   private String logRead(File file, int depth) {
      StringBuilder sb = new StringBuilder();
      sb.append("read  ");
      for (int i = 0; i < depth + 1; i++) {
         sb.append("  ");
      }
      sb.append(" " + file.getName());
      return sb.toString();
   }

   /** invoked at the end of processing each directory.
    */
   @SuppressWarnings("unchecked")
   protected void handleDirectoryEnd(File directory, int depth, Collection results)
           throws IOException {
      if (walkerLog.contains(WalkerLog.DIRECTORY_START) && !command.isQuiet()) {
         for (int i = 0; i < depth; i++) {
            Util.print("..");
         }
         Util.println(": " + directory.getName());
      }

      if (((ConverterCommandLine) converter.getCommand()).byDirectory) {
         writeIndexerAggregator();
      }

   }

   /** invoked at the end of processing.
    */
   @SuppressWarnings("unchecked")
   protected void handleEnd(Collection results) throws IOException {
//    	if (walkerLog.contains(WalkerLog.CONVERTER_END)) {
      info("results (errors): " + results.size());
      String logfile = ((Command) this.getCommand()).getLogfile();
      if (false) {

         // TODO: Remove this dead code!!
         if (results.size() > 0) {
            Element log = new Element("log");
            for (Object o : results) {
               Element entry = new Element("entry");
               String ss = (String) o;
               String sss = Util.replaceISOControlsByMnemonic(ss);
               debug("Error: " + ss + " ... " + sss);
               entry.appendChild(sss);
               log.appendChild(entry);
               entry.addAttribute(new Attribute("level", "error"));
            }
            if (logfile != null) {
               FileOutputStream fos = new FileOutputStream(logfile);
               CMLUtil.debug(log, fos, 1);
               fos.close();
               info("Wrote logfile: " + logfile);
            } else {
               warn("No logfile name given");
            }
         }
      } else {
         if (logfile != null) {
            FileOutputStream fos = new FileOutputStream(logfile);
            converter.getConverterLog().write(fos);
            IOUtils.closeQuietly(fos);
            info("Wrote logfile: " + logfile);
         } else {
            warn("No logfile name given");
         }
      }

//    	}

      if (!((ConverterCommandLine) converter.getCommand()).byDirectory) {
         writeIndexerAggregator();
      }

   }

   /**
    * @throws IOException
    */
   private void writeIndexerAggregator() throws IOException {

      if (inputDirectory != null) {

         String fullInputDirectoryName = inputDirectory.getAbsolutePath();
         String fullIndexfileName = FilenameUtils.concat(
                 fullInputDirectoryName,
                 indexfileName);

         if (index != null) {
            info("index: " + fullIndexfileName + " ... " + index);
            index.writeFile(fullIndexfileName);
         } else if (converterList != null) {
            info("index: " + fullIndexfileName + " ... " + converterList);
            converterList.writeFile(fullIndexfileName);
         }
      }
   }

   /**
    *
    */
   public void cancel() {
      cancelled = true;
   }

   /** invoked to determine if the entire walk operation should be immediately cancelled.
    * called for every file, I think
    */
   @SuppressWarnings("unchecked")
   protected boolean handleIsCancelled(File file, int depth, Collection results) throws IOException {
      return cancelled;
   }

   /** Checks whether the walk has been cancelled.
    * by calling handleIsCancelled(java.io.File, int, java.util.Collection),
    * throwing a CancelException if it has.
    */
   @SuppressWarnings("unchecked")
   protected void handleCancelled(File startDirectory, Collection results,
           DirectoryWalker.CancelException cancel) throws IOException {
      info("CANCEL " + cancel.getMessage() + " in " + cancel.getFile());
      handleDirectoryEnd(startDirectory, -1, results);
      handleEnd(results);
   }

   /**
   /** invoked for each restricted directory.
    */
   @SuppressWarnings("unchecked")
   protected void handleRestricted(File directory, int depth,
           Collection results) throws IOException {
      info("RESTRICTED " + directory);
   }

   /**
    * @param startDirectory
    * @return list of results
    * @throws IOException
    */
   @SuppressWarnings("unchecked")
   public List start() throws IOException {
      if (startDirectory == null) {
         throw new RuntimeException("no startDirectory given");
      }
      List<FileManager> results = new ArrayList<FileManager>();
      walk(startDirectory, results);
      return results;
   }

   // ========= getters and setters =============
   /**
    *
    * @param file
    * @return
    */
   private String getName(File file) {
      String s = "";
      if (walkerLog.contains(WalkerLog.FULL_NAME)) {
         s = file.getAbsolutePath();
      } else if (walkerLog.contains(WalkerLog.FULL_NAME)) {
         s = file.getName();
      } else {	// default
         s = file.getName();
      }
      return s;
   }

   /**
    *
    * @return
    */
   public Class<?> getConverterClass() {
      return converterClass;
   }

   /**
    *
    * @param converterClass
    */
   public void setLegacyConverter(Class<?> converterClass) {
      this.converterClass = converterClass;
   }

   /**
    *
    * @param converterClass
    */
   public void setConverterClass(Class<?> converterClass) {
      this.converterClass = converterClass;
      debug("CONVERTER CLASS: " + this.converterClass);
   }

   private void debug(String s) {
      if (command == null || !command.isQuiet()) {
         LOG.debug(s);
      }
   }

   private void info(String s) {
      if (command == null || !command.isQuiet()) {
         LOG.info(s);
      }
   }

   private void warn(String s) {
      if (command == null || !command.isQuiet()) {
         LOG.warn(s);
      }
   }

   /**
    *
    * @return
    */
   public String getInputSuffix() {
      return inputSuffix;
   }

   /**
    *
    * @param inputSuffix
    */
   public void setInputSuffix(String inputSuffix) {
      this.inputSuffix = inputSuffix;
   }

   /**
    *
    * @return
    */
   public String getOutputSuffix() {
      return outputSuffix;
   }

   /**
    *
    * @param outputSuffix
    */
   public void setOutputSuffix(String outputSuffix) {
      this.outputSuffix = outputSuffix;
   }

   /**
    *
    * @return
    */
   public String getIndexfileName() {
      return indexfileName;
   }

   /**
    *
    * @param indexfileName
    */
   public void setIndexfileName(String indexfileName) {
      this.indexfileName = indexfileName;
   }

   /**
    *
    * @return
    */
   public Command getCommand() {
      return command;
   }

   /**
    *
    * @param command
    */
   public void setCommand(Command command) {
      this.command = command;
   }

   /**
    *
    * @return
    */
   public Index getIndex() {
      return index;
   }

   /**
    *
    * @param index
    */
   public void setIndex(Index index) {
      debug("set index " + index);
      this.index = index;
   }

   /**
    *
    * @param forceConvert
    */
   public void setForceConvert(boolean forceConvert) {
      this.forceConvert = forceConvert;
   }

   /**
    *
    * @return
    */
   public File getStartDirectory() {
      return startDirectory;
   }

   /**
    *
    * @param startDirectory
    */
   public void setStartDirectory(File startDirectory) {
      this.startDirectory = startDirectory;
      debug("START DIRECTORY: " + this.startDirectory);
   }

   /**
    *
    * @return
    */
   public File getIncludesDirectory() {
      return includesDirectory;
   }

   /**
    *
    * @param includesDirectory
    */
   public void setIncludesDirectory(File includesDirectory) {
      this.includesDirectory = includesDirectory;
      debug("INCLUDES DIRECTORY: " + this.includesDirectory);
   }

   /**
    *
    * @param setupDirectory
    */
   public void setSetupDirectory(String setupDirectory) {
      this.setupDirectory = setupDirectory;
      debug("SETUP DIRECTORY: " + this.setupDirectory);
   }

   /**
    *
    * @return
    */
   public File getOutputDirectory() {
      return outputDirectory;
   }

   /**
    *
    * @param outputDirectory
    */
   public void setOutputDirectory(File outputDirectory) {
      this.outputDirectory = outputDirectory;
   }

   /**
    *
    * @return
    */
   public IOFileFilter getDirFilter() {
      return dirFilter;
   }

   /**
    *
    * @param dirFilter
    */
   public void setDirFilter(IOFileFilter dirFilter) {
      this.dirFilter = dirFilter;
   }

   /**
    *
    * @return
    */
   public IOFileFilter getFileFilter() {
      return fileFilter;
   }

   /*
   public void setFileFilter(IOFileFilter fileFilter) {
   this.fileFilter = fileFilter;
   }
    */
   /**
    *
    */
   public String getFileFilterExtension() {
      return fileFilterExtension;
   }

   /** set the fileFilterExtension (suffix).
    * also creates and sets the IOFileFilter
    * @param suffix
    */
   public void setFileFilterExtension(String suffix) {
      this.fileFilterExtension = suffix;
//		setFileFilter(FileFilterUtils.suffixFileFilter(suffix));
   }

   /**
    *
    * @return
    */
   public int getMaxFileCount() {
      return maxFileCount;
   }

   /**
    *
    * @param maxFileCount
    */
   public void setMaxFileCount(int maxFileCount) {
      this.maxFileCount = maxFileCount;
   }

   /**
    *
    * @return
    */
   public FileManager getFileManager() {
      ensureFileManager();
      return fileManager;
   }

   /**
    *
    * @param fileManager
    */
   public void setFileManager(FileManager fileManager) {
      this.fileManager = fileManager;
   }
}
