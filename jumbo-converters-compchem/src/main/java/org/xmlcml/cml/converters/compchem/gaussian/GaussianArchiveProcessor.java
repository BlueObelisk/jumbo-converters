package org.xmlcml.cml.converters.compchem.gaussian;

import static org.xmlcml.euclid.EuclidConstants.S_ATSIGN;
import static org.xmlcml.euclid.EuclidConstants.S_BACKSLASH;
import static org.xmlcml.euclid.EuclidConstants.S_EMPTY;
import static org.xmlcml.euclid.EuclidConstants.S_SLASH;
import static org.xmlcml.euclid.EuclidConstants.S_SPACE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.Command;
import org.xmlcml.cml.converters.ConverterLog;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.compchem.output.AbstractCompchemOutputProcessor;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLDictionary;

/**
 * converts Gaussian archive to molecule, metadata and properties
 * mainly reads lines and manages them. Main logic in GaussianArchive
 * 
 * @author Peter Murray-Rust
 * 
 */
public class GaussianArchiveProcessor extends AbstractCompchemOutputProcessor {

	private static Logger LOG = Logger.getLogger(GaussianArchiveProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	private GaussianArchive archive;
	private ConverterLog converterLog;
	private Command command;

	/** start of archive */
	final static String START = 
		"1"+S_BACKSLASH+"1"+S_BACKSLASH+"GINC";
	/** ends with \\@ */
	final static String TERMINATOR = 
		S_BACKSLASH+S_BACKSLASH+S_ATSIGN;

	private CMLDictionary dictionary;
	private StringArray stringArray;
	
	public Type getInputType() {
		return Type.GAU_ARC;
	}

	public Type getOutputType() {
		return Type.CML;
	}

	protected void init() {
	}
	
	public GaussianArchiveProcessor() {
		
	}
	/** constructor.
	 * 
	 * @param dictionary
	 */
	public GaussianArchiveProcessor(CMLDictionary dictionary, Command command) {
		this.dictionary = dictionary;
		this.command = command;
		init();
	}
	
	/**
	 * 
	 * @param lines
	 * @param topCml nonNull empty CMLCml to add GINC's to
	 */
    public List<CMLElement> readArchives(List<String> lines) {
    	List<CMLElement> cmlElementList = new ArrayList<CMLElement>();
    	stringArray = new StringArray(lines);
    	while (true) {
    		String line = stringArray.readLine();
    		if (line == null) {
    			break;
    		}
    		if (line.trim().startsWith(START)) {
    			CMLElement cml = readArchive();
    			cmlElementList.add(cml);
    		}
    	}
		if (cmlElementList.size() == 0) {
			throw new RuntimeException("Failed to find any Gaussian archive section");
		}
    	return cmlElementList;
    }

	public CMLElement readArchive() {
		// leading spaces are sometimes missing
		String archiveS = null;
		try {
			archiveS = readAndConcatenateArchiveLines();
		} catch (RuntimeException e) {
			command.warn("****DETECTED and SKIPPED: "+e);
		} catch (Throwable e) {
			command.warn("******SKIPPED: "+e);
		}
		CMLCml cml = null;
		if (archiveS != null) {
			try {
				archive = new GaussianArchive(dictionary, this.command);
				archive.setConverterLog(converterLog);
				cml = archive.parseArchiveToCML(archiveS);
				LOG.trace("CMLElement "+cml);
			} catch (RuntimeException e) {
				command.debug("Parse problem: "+e);
				converterLog.addToLog(Level.ERROR, "Detected and SKIPPED: "+e.getMessage()+S_SLASH+archive.getTitle());
			} catch (Throwable e) {
//    				e.printStackTrace();
				command.debug("Parse error: "+e);
				converterLog.addToLog(Level.ERROR, "*****failed parsetoCML *SKIPPED: "+e.getMessage()+S_SLASH+archive.getTitle());
			}
		}
		return cml;
	}
    	/**
    	 * 
    	 * @param lines
    	 * @param topCml nonNull empty CMLCml to add GINC's to
    	 * @deprecated - use readArchive to return list of archives
    	 */
    public void readArchive(List<String> lines, CMLCml topCml) {
    	stringArray = new StringArray(lines);
//    	List<CMLElement> cmlElementList = null;
		boolean start = false;
		CMLCml cml = null;
    	while (true) {
    		String line = stringArray.readLine();
//    		LOG.trace("??? "+line);
    		if (line == null) {
    			break;
    		}
    		// leading spaces are sometimes missing
    		if (line.trim().startsWith(START)) {
    			start = true;
    			String archiveS = null;
    			try {
    				archiveS = readAndConcatenateArchiveLines();
    			} catch (RuntimeException e) {
    				command.warn("****DETECTED and SKIPPED: "+e);
    				continue;
    			} catch (Throwable e) {
//    				e.printStackTrace();
    				command.warn("******SKIPPED: "+e);
    				continue;
    			}
    			try {
    				archive = new GaussianArchive(dictionary, this.command);
    				archive.setConverterLog(converterLog);
					cml = archive.parseArchiveToCML(archiveS);
    				LOG.trace("CMLElement "+cml);
//    	    		if (cmlElementList == null) {
//    	    			cmlElementList = new ArrayList<CMLElement>();
//    	    		}
//	    			cmlElementList.add(cml);
	    			topCml.appendChild(cml);
    			} catch (RuntimeException e) {
    				command.debug("Parse problem: "+e);
					converterLog.addToLog(Level.ERROR, "Detected and SKIPPED: "+e.getMessage()+S_SLASH+archive.getTitle());
    				continue;
    			} catch (Throwable e) {
//    				e.printStackTrace();
    				command.debug("Parse error: "+e);
					converterLog.addToLog(Level.ERROR, "*****failed parsetoCML *SKIPPED: "+e.getMessage()+S_SLASH+archive.getTitle());
    				continue;
    			}
    		}
    	}
		if (!start) {
			throw new RuntimeException("Failed to find any Gaussian archive section");
		}
    }
    
    private String readAndConcatenateArchiveLines() throws IOException {
    	StringBuffer sb = new StringBuffer();
    	String line = stringArray.getLines().get(stringArray.getLineCount()-1);
    	boolean startsWithSpace = line.startsWith(S_SPACE);
		// terminator may be split over two lines, so test buffer
    	while (true) {
    		if (startsWithSpace) {
    			line = line.substring(1);
    		}
    		int l = line.length();
			sb.append(line);
			if (sb.toString().endsWith(TERMINATOR)) {
				break;
			}
			if (l != 70) {
				throw new RuntimeException("Bad line length ("+l+") :"+line);
			}
    		line = stringArray.readLine();
    		if (line == null) {
    			break;
    		}
    		if (line.trim().equals(S_EMPTY)) {
    			String sbs = sb.toString();
    			throw new RuntimeException("missing terminator: "+
    					sbs.substring(Math.min(40, sbs.length())));
    		}
    	}
    	return sb.toString();
    }

	/**
     * 
     * @param args
     */
    public static void main(String[] args) {

//    	GaussianArchiveConverter gc = new GaussianArchiveConverter();
//    	try {
//    		gc.runCommands(args);
//    	} catch (Exception e) {
//    		e.printStackTrace();
//    		System.err.println("EXCEPTION in GaussianArchiveConverter "+e);
//    	}
    }

	public void setConverterLog(ConverterLog converterLog) {
		this.converterLog = converterLog;
	}

}
class StringArray {
	private int lineCount;
	private List<String> lines = new ArrayList<String>();
	
	public int getLineCount() {
		return lineCount;
	}

	public void setLineCount(int iline) {
		this.lineCount = iline;
	}

	public StringArray(List<String> lines) {
		for (String s : lines) {
			this.lines.add(new String(s));
		}
		lineCount = 0;
	}
	
	public String readLine() {
		String line = null;
		if (lineCount < lines.size()) {
			line = lines.get(lineCount++);
		}
		return line;
	}

	public List<String> getLines() {
		return lines;
	}

	public void setLines(List<String> lines) {
		this.lines = lines;
	}
	

}
