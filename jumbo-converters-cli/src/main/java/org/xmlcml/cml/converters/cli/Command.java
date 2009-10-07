package org.xmlcml.cml.converters.cli;

import static org.xmlcml.euclid.EuclidConstants.S_APOS;
import static org.xmlcml.euclid.EuclidConstants.S_EMPTY;
import static org.xmlcml.euclid.EuclidConstants.S_LBRAK;
import static org.xmlcml.euclid.EuclidConstants.S_RBRAK;
import static org.xmlcml.euclid.EuclidConstants.S_SPACE;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.DisplaySetting;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.OptionException;
import org.apache.commons.cli2.builder.ArgumentBuilder;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.builder.SwitchBuilder;
import org.apache.commons.cli2.commandline.Parser;
import org.apache.commons.cli2.option.PropertyOption;
import org.apache.commons.cli2.util.HelpFormatter;
import org.apache.commons.cli2.validation.ClassValidator;
import org.apache.commons.cli2.validation.FileValidator;
import org.apache.commons.cli2.validation.InvalidArgumentException;
import org.apache.commons.cli2.validation.NumberValidator;
import org.apache.commons.cli2.validation.UrlValidator;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;

/**
 * 
 */
public abstract class Command extends org.xmlcml.cml.converters.Command {

	private static final Logger LOG = Logger.getLogger(Command.class);
	static {
		LOG.setLevel(Level.INFO);
	}

	/**
	 * 
	 */
	public enum Verbosity {
		DEBUG,
		INFO,
		QUIET,
		;
		/**
		 *
		 */
		private Verbosity() {
			
		}
	}

	public final static String ARG_SEPARATOR = "--";
	public final static String CLASS_SUFFIX = ".class";
	public final static String JAR_FILE_NAME = "./jumboconverters.jar";
	
	protected static FileValidator existingFileValidator;
	protected static FileValidator existingDirectoryValidator;
	protected static FileValidator fileValidator;
	protected static ClassValidator classValidator;
	protected static NumberValidator numberValidator;
	protected static UrlValidator urlValidator;

	static {
		makeValidators();
	}
	
	protected static void makeValidators() {
	
		classValidator = new ClassValidator();
		classValidator.setLoadable(true);
		classValidator.setInstance(true);
		
		fileValidator = new FileValidator();
		fileValidator.setHidden(false);
		fileValidator.setWritable(true);		
		
		existingFileValidator = FileValidator.getExistingFileInstance();
		existingFileValidator.setHidden(false);
		existingFileValidator.setWritable(true);	
		
		existingDirectoryValidator = FileValidator.getExistingDirectoryInstance();
		existingDirectoryValidator.setHidden(false);
		existingDirectoryValidator.setWritable(true);	
		
		numberValidator = NumberValidator.getNumberInstance();
		urlValidator = new UrlValidator();
				
	}

	protected Map<String, String> propertyMap = new HashMap<String, String>();
	protected Set<Class> converterClassSet;

	protected List<List<String>> argListList;
	protected Group rootCommandOptions;
	protected CommandLine commandLine;
	
	protected Option helpOption;
	protected Option propertyOption;
	protected Option debugOption;
	protected Option propertyfileOption;
	protected Option quietOption;
	protected Option verboseOption;
	protected Option versionOption;
	protected Option logfileOption;
	protected Option loggerOption;
	protected Option infileOption;
	protected Option outfileOption;
	protected Option selectOption;
	protected Option startDirectoryOption;
	protected Option includesDirectoryOption;
	protected Option setupDirectoryOption;
	protected Option outputDirectoryOption;
	protected Option fileFilterOption;
	protected Option dirFilterOption;
	protected Option fileIdOption;
	
	protected ArgumentBuilder argumentBuilder =
		new ArgumentBuilder();
	protected DefaultOptionBuilder defaultOptionBuilder =
		new DefaultOptionBuilder();
	protected GroupBuilder groupBuilder =
		new GroupBuilder();
	protected SwitchBuilder switchBuilder =
		new SwitchBuilder();
	
	private Verbosity verbosity = Verbosity.INFO;
	private String logfile;
	private Class<?> logger;
	private String propertyfile;
	protected String infile;
	protected String outfile;
	protected String selectXPath;
	protected String startDirectory;
	protected String includesDirectory;
	protected String setupDirectory;
	protected String outputDirectory;
	protected IOFileFilter fileFilter;
	protected IOFileFilter dirFilter ;
	protected String fileIdExtraction;

	protected Parser parser;
	public Command() {
		init();
	}
	protected void init() {
		
		try {
			assembleOptions();
		} catch (OptionException e) {
			e.printStackTrace();
		}
		createOptions();
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
					LOG.info("   "+s+"="+propertyMap.get(s));
				}
			}
		}
		LOG.info(  "LOGFILE:       "+logfile);
		LOG.info(  "LOGGER:        "+logger);
		LOG.info(  "VERBOSITY:     "+verbosity);
		LOG.info(  "INFILE:        "+infile);
		LOG.info(  "OUTFILE:       "+outfile);
		LOG.info(  "SELECT(XPATH): "+selectXPath);
		LOG.info(  "STARTDIR:      "+startDirectory);
		LOG.info(  "INCLUDEDIR:    "+includesDirectory);
		LOG.info(  "SETUPDIR:      "+setupDirectory);
		LOG.info(  "OUTPUTDIR:     "+outputDirectory);
		LOG.info(  "FILEFILTER:    "+fileFilter);
		LOG.info(  "FILEIDEXT:     "+fileIdExtraction);
	}

	protected void assembleOptions() throws OptionException {
	
		helpOption = defaultOptionBuilder
	    .withShortName("help")
	    .withShortName("h")
	    .withDescription("print this message")
	    .create();
		
		debugOption = defaultOptionBuilder
		    .withShortName("debug")
		    .withShortName("d")
		    .withDescription("print debugging information")
		    .create();
	
	
		logfileOption = defaultOptionBuilder
		    .withShortName("logfile")
		    .withShortName("l")
		    .withDescription("use given file for input")
		    .withArgument(
		        argumentBuilder
		            .withName("file")
		            .withMinimum(1)
		            .withMaximum(1)
		            .create())
		    .create();
		
		loggerOption = defaultOptionBuilder
			    .withShortName("logger")
			    .withDescription("the class which is to perform logging (default to enclosing subclass)")
			    .withArgument(
			        argumentBuilder
			            .withName("classname")
			            .withMinimum(1)
			            .withMaximum(1)
			            .create())
			    .create();
			
		propertyOption = new PropertyOption();
		
		propertyfileOption = defaultOptionBuilder
			    .withShortName("propertyfile")
			    .withDescription("load all properties from file with -D properties taking precedence")
			    .withArgument(
			        argumentBuilder
			            .withName("name")
			            .withMinimum(1)
			            .withMaximum(1)
			            .create())
			    .create();
			
		quietOption = defaultOptionBuilder
		    .withShortName("quiet")
		    .withShortName("q")
		    .withDescription("be extra quiet")
		    .create();
		
		verboseOption = defaultOptionBuilder
		    .withShortName("verbose")
		    .withShortName("v")
		    .withDescription("be extra verbose")
		    .create();
		
		
		versionOption = defaultOptionBuilder
		    .withShortName("version")
			    .withShortName("v")
			    .withDescription("print the version information and exit")
			    .create();

		outfileOption = defaultOptionBuilder
	    .withShortName("outfile")
	    .withShortName("o")
	    .withDescription("use given file for output")
	    .withArgument(
	        argumentBuilder
	            .withName("file")
	            .withMinimum(1)
	            .withMaximum(1)
	            .create())
	    .create();
		
		infileOption = defaultOptionBuilder
		    .withShortName("infile")
		    .withShortName("i")
		    .withDescription("use given file for input")
		    .withArgument(
		        argumentBuilder
		            .withName("file")
		            .withMinimum(1)
		            .withMaximum(1)
		            .create())
		    .create();
		
		selectOption = defaultOptionBuilder
	    .withShortName("select")
	    .withShortName("sx")
	    .withDescription("select node(s) by XPath")
	    .withArgument(
	        argumentBuilder
	            .withName("xpath")
	            .withMinimum(1)
	            .withMaximum(1)
	            .create())
	    .create();
		
		startDirectoryOption = defaultOptionBuilder
	    .withShortName("startDirectory")
	    .withShortName("sd")
	    .withDescription("start directory (traverses all descendants)")
	    .withArgument(
	        argumentBuilder
	            .withName("startDir")
	            .withMinimum(1)
	            .withMaximum(1)
	            .create())
	    .create();
		
		includesDirectoryOption = defaultOptionBuilder
	    .withShortName("includesDirectory")
	    .withShortName("incdir")
	    .withDescription("includes directory (must be at the same level or below the start directory)")
	    .withArgument(
	        argumentBuilder
	            .withName("incDir")
	            .withMinimum(1)
	            .withMaximum(1)
	            .create())
	    .create();

		setupDirectoryOption = defaultOptionBuilder
	    .withShortName("setupDirectory")
	    .withShortName("spdir")
	    .withDescription("C3DER setup directory (where the work-flow files reside; must be absolute)")
	    .withArgument(
	        argumentBuilder
	            .withName("spDir")
	            .withMinimum(1)
	            .withMaximum(1)
	            .create())
	    .create();
		
		outputDirectoryOption = defaultOptionBuilder
	    .withShortName("outputDirectory")
	    .withShortName("odir")
	    .withShortName("od")
	    .withDescription("output directory "+
	    				"\n\t\tabsolute filename (single output directory)"+
	    				"\n\tOR\trelative to each directory traversed")
	    .withArgument(
	        argumentBuilder
	            .withName("outputDir")
	            .withMinimum(1)
	            .withMaximum(1)
	            .create())
	    .create();
		
		fileFilterOption = defaultOptionBuilder
	    .withShortName("fileFilter")
	    .withShortName("ff")
	    .withDescription("file Filter "+
	    				"\n\t\tregular expression covering whole path name..."+
	    				"\n\t\te.g., */rawcml/*foo.cml"+
	    				"\n\t\tthis is split into a directory filter before the final file separator"+
	    				"\n\t\tand a name filter after the final fiole separator. If there is no file"+
	    				"\n\t\tthe directory filter is not invoked.")
	    .withArgument(
	        argumentBuilder
	            .withName("filter")
	            .withMinimum(1)
	            .create())
	    .create();
		
		dirFilterOption = defaultOptionBuilder
	    .withShortName("dirFilter")
	    .withShortName("df")
	    .withDescription("directory Filter \n\t\tregular expression covering individual directory names..." +
	    				"n\t\te.g., rawcml")
	    .withArgument(
	        argumentBuilder
	            .withName("dirfilter")
	            .withMinimum(1)
	            .create())
	    .create();
		
		fileIdOption = defaultOptionBuilder
	    .withShortName("fileId")
	    .withShortName("fid")
	    .withDescription("extract an id from a filename (root or name) using the following form " +
	    				"\n\t\t./a/b/root/name.suffix")
	    .withArgument(
	        argumentBuilder
	            .withName("part")
	            .withMinimum(1)
	            .withMaximum(1)
	            .create())
	    .create();
		
//-------------------
		
		groupBuilder
		    .withName("options")
		    .withOption(debugOption)
		    .withOption(helpOption)
		    .withOption(loggerOption)
		    .withOption(logfileOption)
		    .withOption(propertyfileOption)
		    .withOption(propertyOption)
		    .withOption(quietOption)
		    .withOption(verboseOption)
		    .withOption(versionOption)
		    .withOption(infileOption)
		    .withOption(outfileOption)
		    .withOption(selectOption)
		    .withOption(startDirectoryOption)
		    .withOption(includesDirectoryOption)
		    .withOption(setupDirectoryOption)
		    .withOption(outputDirectoryOption)
		    .withOption(fileFilterOption)
		    .withOption(dirFilterOption)
		    .withOption(fileIdOption)
		    ;
	}
	
	protected void createOptions() {
		rootCommandOptions = groupBuilder.create();
	}
	
	public void processCommandLine(String[] args) {
		LOG.debug("args "+args.length);
		this.argListList = splitArgs(args);
		String[] args0 = new String[0];
		if (argListList.size() > 0) {
			args0 = argListList.get(0).toArray(new String[0]);
			argListList.remove(0);
		}

		LOG.debug("args after packing "+args0.length);
		try {
			this.createCommandLine(args0);
		} catch (OptionException e) {
			e.printStackTrace();
		}
		try {
			this.createImplicitValues();
			this.summarize();
			this.processCommands();
		} catch (RuntimeException e) {
			e.printStackTrace();
			LOG.error("Cannot run converter: "+e.getMessage());
		}
	}
	protected void readClassesFromJar(String jarFileName) {
		
		converterClassSet = new HashSet<Class>();
		// list jar entries
		
//		if (false) {
			try {
				LOG.info("Listing classes - may take time");
				File file = new File(jarFileName);
				JarFile jarFile = new JarFile(file);
				Enumeration entries = jarFile.entries();
				Class abstractConverterClass = Class.forName("org.xmlcml.cml.converters.AbstractConverter");
				ClassLoader classLoader = this.getClass().getClassLoader();
				while (entries.hasMoreElements()) {
					JarEntry entry = (JarEntry) entries.nextElement();
					String filename = entry.getName();
					if (filename.endsWith(CLASS_SUFFIX)) {
						String classname = filename.substring(0, filename.length()-CLASS_SUFFIX.length());
						classname = classname.replace(CMLConstants.S_SLASH, CMLConstants.S_PERIOD);
						try {
							Class clazz = classLoader.loadClass(classname);
							if (abstractConverterClass.isAssignableFrom(clazz) &&
								!Modifier.isAbstract(clazz.getModifiers())) {
								converterClassSet.add(clazz);
							}
						} catch (IllegalAccessError iae) {
	//						System.out.println(iae);
						} catch (NoClassDefFoundError ncdfe) {
	//						System.out.println(ncdfe);
						} catch (ClassNotFoundException cnfe) {
	//						System.out.println(cnfe);
						}
					}
				}
			} catch (Exception e) {
				throw new RuntimeException("Cannot analyze jar file: "+e);
			}
//		} else {
//			LOG.warn("skipped reading classes");
//		}
	}

	/** split args at "--"
	 * 
	 * @param args
	 * @return
	 */
	public static List<List<String>> splitArgs(String[] args) {
		List<List<String>> argListList = new ArrayList<List<String>>();
		List<String> argList = new ArrayList<String>();
		for (String arg : args) {
			if (arg.trim() == S_EMPTY) {
				// skip
			} else if (arg.equals(ARG_SEPARATOR)) {
				argListList.add(argList);
				argList = new ArrayList<String>();
			} else {
				argList.add(arg);
			}
		}
		if (argList.size() > 0) {
			argListList.add(argList);
		}
		return argListList;
	}
	private CommandLine createCommandLine(String[] args) throws OptionException {
	
		propertyMap = new HashMap<String, String>();
		if (args == null || 
			args.length == 0) {
			this.help();
		} else {
			commandLine = parseGeneral(args);
		}
		return commandLine;
	}
	
	protected CommandLine parseGeneral(String[] args) throws OptionException {
		return parseGeneral(args, rootCommandOptions);
	}
	
	protected CommandLine parseGeneral(String[] args, Group optionsx) throws OptionException {
		LOG.debug("parseGeneral");
		parser = new Parser();
		if (optionsx == null) {
			throw new RuntimeException("MUST SET GROUP");
		}
		parser.setGroup(optionsx);
		commandLine = parser.parse(args);
		
@SuppressWarnings("unchecked")			
		Set<String> properties = commandLine.getProperties();
		for (String s : properties) {
			propertyMap.put(s, commandLine.getProperty(s));
		}

		if (commandLine.hasOption(helpOption)) {
		    help();
		} else if (commandLine.hasOption("-version")) {
			version();
		} else {
			parseGeneral();
			parseSpecific(commandLine);
		}
		return commandLine;
	}
	
	protected abstract void parseSpecific(CommandLine commandLine);
	
	protected abstract void processCommands();
	
	private void parseGeneral() {
		String s;
		if(commandLine.hasOption(logfileOption)) {
		    s = (String)commandLine.getValue(logfileOption);
		    LOG.debug("logfileOption: "+s);
		    setLogfile(s);
		}

		if(commandLine.hasOption(loggerOption)) {
		    s = (String)commandLine.getValue(loggerOption);
		    LOG.debug("loggerOption: "+s);
		    setLogger(s);
		}

		if(commandLine.hasOption(debugOption)) {
		    s = (String)commandLine.getValue(debugOption);
		    LOG.debug("debugOption: "+s);
		    setDebug(true);
		}

		if(commandLine.hasOption(propertyfileOption)) {
		    s = (String)commandLine.getValue(propertyfileOption);
		    LOG.debug("propertyFileOption: "+s);
		    setPropertyfile(s);
		}

		if(commandLine.hasOption(quietOption)) {
		    s = (String)commandLine.getValue(quietOption);
		    LOG.debug("quietOption: "+s);
		    setQuiet(true);
		}

		if(commandLine.hasOption(infileOption)) {
		    s = (String)commandLine.getValue(infileOption);
		    LOG.debug("infileOption: "+s);
		    setInfile(s);
		}

		if(commandLine.hasOption(outfileOption)) {
		    s = (String)commandLine.getValue(outfileOption);
		    LOG.debug("outfileOption: "+s);
		    setOutfile(s);
		}

		if(commandLine.hasOption(outputDirectoryOption)) {
		    s = (String)commandLine.getValue(outputDirectoryOption);
		    LOG.debug("outputDirectoryOption: "+s);
		    setOutputDirectory(s);
		}

		if(commandLine.hasOption(selectOption)) {
		    s = (String)commandLine.getValue(selectOption);
		    LOG.debug("selectOption: "+s);
		    setSelectXPath(s);
		}

		if(commandLine.hasOption(startDirectoryOption)) {
		    s = (String)commandLine.getValue(startDirectoryOption);
		    LOG.debug("startDirectoryOption: "+s);
		    setStartDirectory(s);
		}

		if(commandLine.hasOption(includesDirectoryOption)) {
		    s = (String)commandLine.getValue(includesDirectoryOption);
		    LOG.debug("includesDirectoryOption: "+s);
		    setIncludesDirectory(s);
		}

		if(commandLine.hasOption(setupDirectoryOption)) {
		    s = (String)commandLine.getValue(setupDirectoryOption);
		    LOG.debug("setupDirectoryOption: "+s);
		    setSetupDirectory(s);
		}

		if(commandLine.hasOption(fileFilterOption)) {
		    s = (String)commandLine.getValue(fileFilterOption);
		    LOG.debug("fileFilterOption: "+s);
		    setFileFilter(s);
		}

		if(commandLine.hasOption(dirFilterOption)) {
		    s = (String)commandLine.getValue(dirFilterOption);
		    LOG.debug("dirFilterOption: "+s);
		    setDirFilter(s) ;
		}

		if(commandLine.hasOption(fileIdOption)) {
		    s = (String)commandLine.getValue(fileIdOption);
		    LOG.debug("fileIdOption: "+s);
		    setFileIdExtraction(s) ;
		}
	}

	@SuppressWarnings("unchecked")
	protected void help() {
	//		To generate a help page for jumbo we first need to create a HelpFormatter
	//		and set some basic properties. The shell command is the command that the 
	//		used would have typed to invoke the application, and the group is the group
	//		of options that compose the model.

		
			HelpFormatter helpFormatter = new HelpFormatter("|", "|", "|", 150);
			helpFormatter.setShellCommand("jumbo");
			helpFormatter.setGroup(rootCommandOptions);
		
	//		The first section of help will display the full usage string for the application,
	//		the appearance of this line can be adjusted using the HelpFormatter's
	//		fullUsageSettings property:
		
			Set<DisplaySetting> displaySettingSet = helpFormatter.getFullUsageSettings();
			displaySettingSet.add(DisplaySetting.DISPLAY_GROUP_NAME);
			displaySettingSet.add(DisplaySetting.DISPLAY_GROUP_ARGUMENT);
			displaySettingSet.remove(DisplaySetting.DISPLAY_GROUP_EXPANDED);
		
	//		The main body of the help is based on a line or more of information about
	//		each option in the model. DisplaySettings can be used again to adjust 
	//		which items are included in this display and which aren't. In this case,
	//		we don't want to display any groups as the top one is the only one present 
	//		and can be inferred:
		
			displaySettingSet.remove(DisplaySetting.DISPLAY_GROUP_ARGUMENT);
		
	//		Each of the options identified by the displaySettings above has some usage information 
	//		displayed, usually this will be a minimal set of DisplaySettings but these can be 
	//		adjusted to get the desired effect:
		
			displaySettingSet.add(DisplaySetting.DISPLAY_PROPERTY_OPTION);
			displaySettingSet.add(DisplaySetting.DISPLAY_PARENT_ARGUMENT);
			displaySettingSet.add(DisplaySetting.DISPLAY_ARGUMENT_BRACKETED);
		
			if (helpFormatter.getGroup() == null) {
				LOG.error("Program logic wrong: No group set in HelpFormatter");
			} else {
				helpFormatter.print();
			}
		}

	protected void version() {
		LOG.info("version...");
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

	public void setLogger(String className) {
		Class<?> loggerClass = null;
		try {
			loggerClass = Class.forName(className);
			loggerClass.newInstance();
			this.setLogger(loggerClass);
		} catch (ClassNotFoundException e) {
			LOG.error("Cannot find logger class: "+className);
		} catch (IllegalAccessException e) {
			LOG.error("Cannot instantiate logger class: "+className);
		} catch (InstantiationException e) {
			LOG.error("Cannot instantiate logger class: "+className);
		}
	}

	public void setLogger(Class<?> logger) {
		List<Class<?>> loggers = new ArrayList<Class<?>>();
		loggers.add(logger);
		try {
			classValidator.validate(loggers);
		} catch (InvalidArgumentException e) {
			throw new RuntimeException("logger cannot be instantiated: "+logger);
		}
		this.logger = logger;
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
		try {
			fileValidator.validate(files);
		} catch (InvalidArgumentException e) {
			throw new RuntimeException("input file does not exist: "+infile);
		}
		this.infile = infile;
	}
	public String getSelectXPath() {
		return selectXPath;
	}
	public void setSelectXPath(String selectXPath) {
		this.selectXPath = selectXPath;
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
	public Group getRootCommandOptions() {
		return rootCommandOptions;
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
				argString += arg+S_SPACE;
			}
			argString = argString.trim();
		}
		return argString;
	}
	
	protected String getUnprotectedString(CommandLine commandLinex, Option option) {
		Object o = commandLinex.getValue(option);
		return getUnprotectedString(o);
	}
	
	protected Double getUnprotectedDouble(CommandLine commandLinex, Option option) {
		String s = getUnprotectedString(commandLinex, option);
		Double d = null;
		try {
			d = new Double(s);
		} catch (NumberFormatException e) {
			throw new RuntimeException("Bad double ", e);
		}
		return d;
	}
	
	protected List<Double> getUnprotectedDoubles(CommandLine commandLinex, Option option) {
		List<String> sList = getUnprotectedStrings(commandLinex, option);
		List<Double> dList = new ArrayList<Double>();
		for (String s : sList) {
			try {
				dList.add(Double.valueOf(s));
		    } catch (NumberFormatException e) {
		    	throw new RuntimeException("bad double "+s);
		    }
		}
		return dList;
	}
	
	
	protected Integer getUnprotectedInteger(CommandLine commandLinex, Option option) {
		String s = getUnprotectedString(commandLinex, option);
		Integer i = null;
		try {
			i = new Integer(s);
		} catch (NumberFormatException e) {
			throw new RuntimeException("Bad integer ", e);
		}
		return i;
	}
	
	protected List<Integer> getUnprotectedIntegers(CommandLine commandLinex, Option option) {
		List<String> sList = getUnprotectedStrings(commandLinex, option);
		List<Integer> iList = new ArrayList<Integer>();
		for (String s : sList) {
			try {
				iList.add(Integer.valueOf(s));
		    } catch (NumberFormatException e) {
		    	throw new RuntimeException("bad integer "+s);
		    }
		}
		return iList;
	}
	
	protected Boolean getUnprotectedBoolean(CommandLine commandLinex, Option option) {
		String s = getUnprotectedString(commandLinex, option);
		Boolean b = null;
		try {
			b = new Boolean(s);
		} catch (NumberFormatException e) {
			throw new RuntimeException("Bad boolean ", e);
		}
		return b;
	}
	
	@SuppressWarnings("unchecked")
	protected List<String> getUnprotectedStrings(CommandLine commandLinex, Option option) {
		List<Object> oo = commandLinex.getValues(option);
		List<String> sList = new ArrayList<String>();
		for (Object o : oo) {
			String sx = getUnprotectedString(o);
			sList.add(sx);
		}
		return sList;
	}
	
	private String getUnprotectedString(Object o) {
		String sx = (String) o;
		if (sx.startsWith(S_LBRAK) && sx.endsWith(S_RBRAK)) {
			sx = sx.substring(1, sx.length()-1);
		}
		return sx;
	}
	
	public IOFileFilter getFileFilter() {
		return fileFilter;
	}
	
	public void setFileFilter(String fileFilterS) {
		if (fileFilterS == null) {
			throw new RuntimeException("null file filter");
		}
		if(fileFilterS.startsWith(S_APOS)) {
			fileFilterS = fileFilterS.substring(1) ;			
		}
		if(fileFilterS.endsWith(S_APOS)) {
			fileFilterS = fileFilterS.substring(0,fileFilterS.length() - 1 ) ;			
		}
		int idx = fileFilterS.lastIndexOf(File.separator) ;
		if( idx != -1) {
//			String dirFilterS = fileFilterS.substring(0, idx) ;
			fileFilterS = fileFilterS.substring(idx + 1) ;
//			dirFilter = new WildcardFileFilter(dirFilterS) ;
//			this.setDirFilter(dirFilter) ;
		}
		if (fileFilterS.length() > 0 ) {
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
		if (dirFilterS.length() > 0 ) {
			LOG.info("Assuming wildcard directory filter " + dirFilterS );
			dirFilter = new WildcardFileFilter(dirFilterS) ;
			this.setDirFilter(dirFilter) ;
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
