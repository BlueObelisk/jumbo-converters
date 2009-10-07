package org.xmlcml.cml.converters.cli;

import org.xmlcml.cml.converters.*;
import org.xmlcml.cml.converters.cli.AbstractConverterFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarFile;

import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.OptionException;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * 
 * @author pm286
 *
 */
public class ConverterCommandLine extends Command {

	static final Logger LOG = Logger.getLogger(ConverterCommandLine.class);
    static {
    	LOG.setLevel(Level.INFO);
    };
	
	protected Option auxfileOption;
	protected String auxfileName;
	protected Option insuffixOption;
	protected String insuffix;
	protected Option outsuffixOption;
	protected String outsuffix;
	protected String directoryFilter = null;
	protected Option intypeOption;
	protected Type intype;
	protected Option outtypeOption;
	protected Type outtype;
	protected Option fileLimitsOption;
	protected List<Integer> fileLimits = new ArrayList<Integer>(); 
	
	protected Option converterOption;
	protected Option listConverterOption;
	protected Converter converterInstance;
	protected Class<?> converterClass;
	
	protected Option indexOption;
	protected Option indexfileNameOption;
	protected String indexfileName;
	protected Option copyIndexEntryOption;
	protected boolean copyIndexEntry = false;
	protected Option byDirectoryOption ;
	protected boolean byDirectory = false;
	protected Option allowDuplicateEntryOption;
	protected boolean allowDuplicateEntry = true;
	protected Option xpathOption;
	protected List<String> xPathList;
	protected Option forceConvertOption;
	protected boolean forceConvert = true;

	protected Option depthOption;
	private int depth = -1;
	private String jarFileName;
	
	public ConverterCommandLine() {
		init();
	}
	
	protected void init() {
		super.init();
		fileLimits = new ArrayList<Integer>();
		fileLimits.add(0);
		fileLimits.add(Integer.MAX_VALUE);
		fileLimits.add(0);
		fileLimits.add(Integer.MAX_VALUE);
	}

	public Converter getConverter() {
		return converterInstance;
	}

	public void setConverterClassName(String converterClassName) {
//		ensureConverterClass();
		try {
			converterClass = Class.forName(converterClassName);
			info("making converter "+converterClassName);
			setConverterClassAndCreateInstance(converterClass);
			((AbstractConverter) this.getConverter()).setCommand(this);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Cannot find converter class: "+converterClassName);
		}
	}

	public void setConverterClassAndCreateInstance(Class<?> converterClass) {
		List<Class<?>> converters = new ArrayList<Class<?>>();
		converters.add(converterClass);
		makeConverter(converterClass);
	}
	
	

	private void makeConverter(Class<?> converterClass) {
		try {
			debug("making converter: "+converterClass);
			this.converterInstance = (Converter) converterClass.newInstance();
			((AbstractConverter)this.converterInstance).setCommand(this);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("cannot find/instantiate class: "+converterClass, e);
		}
	}
	
	protected void createImplicitValues() {
		super.createImplicitValues();
		
		getIntypeAndSuffix();
		
		getOuttypeAndSuffix();
		
		if (converterClass == null) {
			if (intype != null && outtype != null) {
				converterClass = AbstractConverterFactory.createConverterClass(intype, outtype);
				debug("created converterClass for "+intype+"2"+outtype+
						" => "+converterClass.getName());
			}
			if (converterClass != null) {
				makeConverter(converterClass);
			}
		}
	}

	private void getOuttypeAndSuffix() {
		if (outsuffix == null) {
			if (outtype != null) {
				outsuffix = outtype.getDefaultExtension();
			}
		}
		if (outsuffix == null) {
			if (outfile != null) {
				outsuffix = FilenameUtils.getExtension(outfile);
			}
		}
		
		if (outtype == null) {
			outtype = Type.getTypeForExtension(outsuffix);
		}
	}

	private void getIntypeAndSuffix() {
		if (insuffix == null) {
			if (intype != null) {
				insuffix = intype.getDefaultExtension();
			}
		}
		if (insuffix == null) {
			if (infile != null) {
				insuffix = FilenameUtils.getExtension(infile);
			}
		}
		
		if (intype == null) {
			intype = Type.getTypeForExtension(insuffix);
		}
	}
	
	protected void summarize() {
		if (!isQuiet()) {
			info("Command:");
			super.summarize();
			info("  CONVERTER:      "+converterInstance);
			info("  INSUFFIX:       "+insuffix);
			info("  OUTSUFFIX:      "+outsuffix);
			info("  INTYPE:         "+intype);
			info("  OUTTYPE:        "+outtype);
			info("  FILELIMITS:     "+fileLimits.get(0)+" "+fileLimits.get(1)+" "+fileLimits.get(2)+" "+fileLimits.get(3));
			info("  INDEXFILE:      "+indexfileName);
			info("  ALLOWDUPLICATE: "+allowDuplicateEntry);
			info("  COPYINDEXENTRY: "+copyIndexEntry);
			info("  XPATHLIST:      "+xPathList);
			info("  XPATHSELECT:    "+selectXPath);
			info("  FORCECONVERT:   "+forceConvert);
		}
	}
	
	protected void processCommands() {
		if (converterInstance == null) {
			warn("Cannot find converter (null)");
		} else {
			if (startDirectory != null) {
				try {
					walkDirectories();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			} else if (infile != null && outfile != null) {
				convertFile();
			} else {
				warn("nothing to convert");
			}
		}
	}
	
	private void walkDirectories() throws IOException {
		if (outsuffix == null) {
			info("No output suffix; files will be listed but not processed");
		}
		info("insuffix "+insuffix);
		IOFileFilter insuffixFileFilter = null;
		if (insuffix != null) {
			insuffixFileFilter = new SuffixFileFilter(insuffix);
		}
		if (fileFilter == null) {
			fileFilter = insuffixFileFilter;
		} else {
			info("File filter: "+fileFilter);
			fileFilter = new AndFileFilter(fileFilter, insuffixFileFilter);
		}
		if (fileFilter == null) {
			throw new RuntimeException("No input filter; no walk");
		}
		if(dirFilter == null) {
			dirFilter = HiddenFileFilter.VISIBLE;			
		} else {
			dirFilter = new AndFileFilter(dirFilter, HiddenFileFilter.VISIBLE) ;
		}
		depth = -1;
		if (fileFilter == null) {
			fileFilter = intype.getSuffixFileFilter();
		}
		
		if (converterInstance != null) {
			converterClass = converterInstance.getClass();
		}
		
		ConverterDirectoryWalker walker = new
			ConverterDirectoryWalker(
				insuffix, outsuffix, converterClass, dirFilter, fileFilter, depth, forceConvert, this);
//		walker.setCommand(this);
		if (indexfileName != null) {
			walker.setIndexfileName(indexfileName);
		}
		walker.setStartDirectory(new File(startDirectory));
		if (includesDirectory != null) {
			walker.setIncludesDirectory(new File(includesDirectory));
		}
		if (setupDirectory != null) {
			walker.setSetupDirectory(setupDirectory);
		}
		if (outputDirectory != null) {
			walker.setOutputDirectory(new File(outputDirectory));
		}
		walker.start();
	}

	private void convertFile() {
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new FileInputStream(infile);
			debug("reading main input: " + infile);
			out = new FileOutputStream(outfile);
			debug("writing main output: " + outfile);
			converterInstance.convert(in, out);
			info("read: " + infile);
			info("wrote: " + outfile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
	}
	
	protected void assembleOptions() throws OptionException {
		debug("ASSEMBLE ");
		super.assembleOptions();
		
// =================index===================
		
		indexfileNameOption = defaultOptionBuilder
	    .withShortName("indexfileName")
	    .withShortName("if")
	    .withDescription("indexfile name (absolute or relative(NYI)")
	    .withArgument(
	        argumentBuilder
	            .withName("index")
	            .withMinimum(1)
	            .withMaximum(1)
	            .create())
	    .create();
		
		byDirectoryOption = defaultOptionBuilder
	    .withShortName("byDirectory")
	    .withShortName("bd")
	    .withDescription("If present create index for each directory otherwise index whole repository.")
	    .create();

		copyIndexEntryOption = defaultOptionBuilder
	    .withShortName("copyIndexEntry")
	    .withShortName("copy")
	    .withDescription("copy Entry (true/false)")
	    .withArgument(
	        argumentBuilder
	            .withName("copy")
	            .withMinimum(1)
	            .withMaximum(1)
	            .create())
	    .create();
		
		allowDuplicateEntryOption = defaultOptionBuilder
	    .withShortName("allowDuplicateEntry")
	    .withShortName("dup")
	    .withDescription("allow duplicate entries in index (true/false)")
	    .withArgument(
	        argumentBuilder
	            .withName("dup")
	            .withMinimum(1)
	            .withMaximum(1)
	            .create())
	    .create();
		
		xpathOption = defaultOptionBuilder
	    .withShortName("xpath")
	    .withDescription("xpath(s) for indexing and aggregating")
	    .withArgument(
	        argumentBuilder
	            .withName("xpath")
	            .withMinimum(1)
	            .create())
	    .create();
		
		Group indexChildrenOption = new GroupBuilder()
	    .withName("index")
	    .withDescription("index options")
	    .withOption(indexfileNameOption)
	    .withOption(byDirectoryOption)
	    .withOption(copyIndexEntryOption)
	    .withOption(allowDuplicateEntryOption)
	    .withOption(xpathOption)
	    .create();
		
		indexOption = defaultOptionBuilder
	    .withLongName("index")
	    .withDescription("index options")
	    .withChildren(indexChildrenOption)
	    .create();
		
// =================others===================

		
		converterOption = defaultOptionBuilder
	    .withShortName("converter")
	    .withDescription("run converter (use listConverters to find available)")
	    .withArgument(
	        argumentBuilder
	            .withName("class")
	            .withMinimum(1)
	            .withMaximum(1)
	            .create())
	    .create();
	
		listConverterOption = defaultOptionBuilder
	    .withShortName("list")
	    .withDescription("list available converters")
	    .withArgument(
	        argumentBuilder
	            .withName("jar name")
	            .withMinimum(0)
	            .withMaximum(1)
	            .create())
	    .create();
	
		auxfileOption = defaultOptionBuilder
	    .withShortName("auxfile")
	    .withShortName("aux")
	    .withDescription("auxiliary file")
	    .withArgument(
	        argumentBuilder
	            .withName("auxfile")
	            .withMinimum(1)
	            .withMaximum(1)
	            .create())
	    .create();

		insuffixOption = defaultOptionBuilder
	    .withShortName("insuffix")
	    .withShortName("is")
	    .withDescription("input file suffix")
	    .withArgument(
	        argumentBuilder
	            .withName("suffix")
	            .withMinimum(1)
	            .withMaximum(1)
	            .create())
	    .create();

		intypeOption = defaultOptionBuilder
	    .withShortName("intype")
	    .withShortName("it")
	    .withDescription("input file type")
	    .withArgument(
	        argumentBuilder
	            .withName("type")
	            .withMinimum(1)
	            .withMaximum(1)
	            .create())
	    .create();

		outsuffixOption = defaultOptionBuilder
	    .withShortName("outsuffix")
	    .withShortName("os")
	    .withDescription("output file suffix")
	    .withArgument(
	        argumentBuilder
	            .withName("suffix")
	            .withMinimum(1)
	            .withMaximum(1)
	            .create())
	    .create();
	
		outtypeOption = defaultOptionBuilder
	    .withShortName("outtype")
	    .withShortName("ot")
	    .withDescription("output file type")
	    .withArgument(
	        argumentBuilder
	            .withName("type")
	            .withMinimum(1)
	            .withMaximum(1)
	            .create())
	    .create();
	
		fileLimitsOption = defaultOptionBuilder
	    .withShortName("fileLimits")
	    .withShortName("fl")
	    .withDescription("limits of filesizes (exclusive)")
	    .withDescription("args(int): minRead maxRead minWrite maxWrite")
	    .withDescription("10 200 20 9999999 reads files of 11-199 ytes and writes 21-9999998")
	    .withDescription("default 0 MAXINT 0 MAXINT will not read or write zero length files")
	    .withArgument(
	        argumentBuilder
	            .withName("size")
	            .withMinimum(4)
	            .withMaximum(4)
	            .create())
	    .create();
	
		depthOption = defaultOptionBuilder
	    .withShortName("depth")
	    .withDescription("maximum depth of walk (-1 = unlimited = default)")
	    .withArgument(
	        argumentBuilder
	            .withName("depth")
	            .withMinimum(1)
	            .withMaximum(1)
	            .create())
	    .create();
	
		forceConvertOption = defaultOptionBuilder
	    .withShortName("onlyNewer")
	    .withShortName("newer")
	    .withDescription("only convert newer files (default = convert all)")
	    .withArgument(
	        argumentBuilder
	            .withName("newer")
	            .withMinimum(0)
	            .withMaximum(0)
	            .create())
	    .create();
	
//		cleanOption = defaultOptionBuilder
//	    .withShortName("clean")
//	    .withShortName("c")
//	    .withDescription("clean output folder")
//	    .withArgument(
//	        argumentBuilder
//	            .withName("depth")
//	            .withMinimum(1)
//	            .withMaximum(1)
//	            .create())
//	    .create();

		groupBuilder
			.withOption(converterOption)
		    .withOption(listConverterOption)
		    .withOption(auxfileOption)
		    .withOption(insuffixOption)
		    .withOption(intypeOption)
		    .withOption(outsuffixOption)
		    .withOption(outtypeOption)
		    .withOption(indexOption)
		    .withOption(depthOption)
		    .withOption(xpathOption)
		    .withOption(forceConvertOption)
		    .withOption(fileLimitsOption)
//		    .withOption(cleanOption)
		    ;
	}

	public List<MyClass> getSortedListOfClassesInJar() {
		readClassesFromJar(JAR_FILE_NAME);
		List<MyClass> clist = new ArrayList<MyClass>();
		if (converterClassSet != null) {
			for (Class clazz : converterClassSet) {
				clist.add(new MyClass(clazz));
			}
			Collections.sort(clist);
		}
		return clist;
	}
	
	public List<String> guessConverterClassFromInputAndOutputTypes(Type inputType, Type outputType) {
		List<String> classNameList = new ArrayList<String>();
		if (inputType != null && outputType != null) {
//			ConverterCommand converterCommand = ((ConverterCommand)converterInstance.getCommand()); 
			List<MyClass> converterClasses = this.getSortedListOfClassesInJar(); 
			for (MyClass myClass : converterClasses) {
				String className = myClass.getName();
				AbstractConverter ac = null;
				try {
					ac = (AbstractConverter) Class.forName(className).newInstance();
				} catch (Exception e) {
//					LOG.error("Something went wrong", e);
				}
				if (ac != null) {
					if (inputType.equals(ac.getInputType()) &&
						outputType.equals(ac.getOutputType())) {
						LOG.info("***********Found class: "+inputType+" || "+outputType+" = "+className);
						classNameList.add(className);
					}
				}
			}
		}
		return classNameList;
	}
	
	@SuppressWarnings("unchecked")
	protected void parseSpecific(CommandLine commandLine) {
		String s;
		debug("PARSE SPECIFIC");
		
		if(commandLine.hasOption(converterOption)) {
		    s = (String)commandLine.getValue(converterOption);
		    debug("converterOption: "+s);
		    setConverterClassName(s);
		}
		
		if(commandLine.hasOption(auxfileOption)) {
		    s = (String)commandLine.getValue(auxfileOption);
			debug("auxfileOption: "+s);
		    setAuxfileName(s);
		}

		if(commandLine.hasOption(listConverterOption)) {
		    s = (String)commandLine.getValue(listConverterOption);
		    if (s != null) {
		    	jarFileName = s;
		    }
		    ensureJarFileName();
		    List<MyClass> classList = getSortedListOfClassesInJar();
		    LOG.info("converter classes: ");
		    for (MyClass clazz : classList) {
		    	LOG.info("    | "+clazz);
		    }
		}

		if(commandLine.hasOption(insuffixOption)) {
		    s = (String)commandLine.getValue(insuffixOption);
			debug("insuffixOption: "+s);
		    setInsuffix(s);
		}

		if(commandLine.hasOption(intypeOption)) {
		    s = (String)commandLine.getValue(intypeOption);
			debug("intypeOption: "+s);
		    setIntype(Type.getType(s));
		}

		if(commandLine.hasOption(outsuffixOption)) {
		    s = (String)commandLine.getValue(outsuffixOption);
			debug("outsuffixOption: "+s);
		    setOutsuffix(s);
		}
		
		if(commandLine.hasOption(outtypeOption)) {
		    s = (String)commandLine.getValue(outtypeOption);
			debug("outtypeOption: "+s);
		    setOuttype(Type.getType(s));
		}

		if(commandLine.hasOption(fileLimitsOption)) {
			List<Integer> iList = getUnprotectedIntegers(commandLine, fileLimitsOption);
			debug("fileLimitsOption: "+iList);
		    setFileLimits(iList);
		}

// =============== index =============		
		if(commandLine.hasOption(indexfileNameOption)) {
		    s = (String)commandLine.getValue(indexfileNameOption);
			debug("indexfileOption: "+s);
		    setIndexfileName(s);
		}
		
		if(commandLine.hasOption(byDirectoryOption)) {
		    boolean b = true ;
			debug("byDirectoryOption: "+b);
		    setByDirectory(b);
		}

		if(commandLine.hasOption(xpathOption)) {
//@SuppressWarnings("unchecked")			
			List<String> xpathValList = commandLine.getValues(xpathOption);
			debug("xpathOption: "+xpathValList.toString());
			setXPathList(xpathValList) ;
		}
		
		if(commandLine.hasOption(allowDuplicateEntryOption)) {
		    boolean b = getUnprotectedBoolean(commandLine, allowDuplicateEntryOption);
			debug("allowDuplicateEntryOption: "+b);
		    setAllowDuplicateEntry(b);
		}
		
		if(commandLine.hasOption(copyIndexEntryOption)) {
			boolean b = getUnprotectedBoolean(commandLine, copyIndexEntryOption);
			debug("copyIndexEntryOption: "+b);
		    setCopyIndexEntry(b);
		}
		
		if(commandLine.hasOption(depthOption)) {
			int i  = getUnprotectedInteger(commandLine, depthOption);
			debug("depthOption: "+i);
		    setDepth(i);
		}
		
		if(commandLine.hasOption(forceConvertOption)) {
//			boolean b = getUnprotectedBoolean(commandLine, forceConvertOption);
//			debug("forceConvertOption: "+b);
		    setForceConvert(false);
		}

	}

	private void ensureJarFileName() {
		if (jarFileName == null) {
			jarFileName = JAR_FILE_NAME;
		}
	}

	public static void main(String[] args) {
		Command cl = new ConverterCommandLine();
		LOG.debug("Args "+args.length);
		Class clClass = cl.getClass();
		// testing
//		ClassLoader classLoader = clClass.getClassLoader();
//		Package pubchem = Package.getPackage("org.xmlcml.cml.converters.molecule.pubchem");
		// run program
		
		cl.processCommandLine(args);
		System.exit(0) ;
	}

	public String getIndexfileName() {
		return indexfileName;
	}

	public void setIndexfileName(String indexfileName) {
		this.indexfileName = indexfileName;
	}

	public boolean isCopyIndexEntry() {
		return copyIndexEntry;
	}

	public void setCopyIndexEntry(boolean copyIndexEntry) {
		this.copyIndexEntry = copyIndexEntry;
	}

	public boolean isAllowDuplicateEntry() {
		return allowDuplicateEntry;
	}

	public void setAllowDuplicateEntry(boolean allowDuplicateEntry) {
		this.allowDuplicateEntry = allowDuplicateEntry;
	}

	public Option getXpathOption() {
		return xpathOption;
	}

	public void setXpathOption(Option xpathOption) {
		this.xpathOption = xpathOption;
	}

	public List<String> getXPathList() {
		return xPathList;
	}

	public void setXPathList(List<String> xPathList) {
		this.xPathList = xPathList;
	}

	public boolean isForceConvert() {
		return forceConvert;
	}

	public void setForceConvert(boolean forceConvert) {
		this.forceConvert = forceConvert;
	}

	public boolean isByDirectory() {
		return byDirectory;
	}

	public void setByDirectory(boolean byDirectory) {
		this.byDirectory = byDirectory;
	}

	public List<Integer> getFileLimits() {
		return fileLimits;
	}

	public void setFileLimits(List<Integer> fileLimits) {
		this.fileLimits = fileLimits;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}
	
	public String getInsuffix() {
		return insuffix;
	}

	public void setInsuffix(String insuffix) {
		this.insuffix = insuffix;
	}

	public String getOutsuffix() {
		return outsuffix;
	}

	public void setOutsuffix(String outsuffix) {
		this.outsuffix = outsuffix;
	}

	public Type getIntype() {
		return intype;
	}

	public void setIntype(Type intype) {
		this.intype = intype;
	}

	public Type getOuttype() {
		return outtype;
	}

	public void setOuttype(Type outtype) {
		this.outtype = outtype;
	}

	public String getAuxfileName() {
		return auxfileName;
	}

	public void setAuxfileName(String auxfileName) {
		this.auxfileName = auxfileName;
	}
}
class MyClass implements Comparable {
	private Class clazz;
	public MyClass(Class clazz) {
		this.clazz = clazz;
	}
	public int compareTo(Object o) {
		return this.clazz.getName().compareTo(((MyClass)o).getName());
	}
	public String getName() {
		return clazz.getName();
	}
}
