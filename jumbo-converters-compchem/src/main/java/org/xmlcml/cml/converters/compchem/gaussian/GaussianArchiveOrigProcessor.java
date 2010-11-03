package org.xmlcml.cml.converters.compchem.gaussian;

import static org.xmlcml.euclid.EuclidConstants.S_ATSIGN;
import static org.xmlcml.euclid.EuclidConstants.S_BACKSLASH;
import static org.xmlcml.euclid.EuclidConstants.S_EMPTY;
import static org.xmlcml.euclid.EuclidConstants.S_SPACE;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.AbstractBlock;
import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.BlockContainer;
import org.xmlcml.cml.converters.LegacyProcessor;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.compchem.gamessus.GamessUSCommon;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLDictionary;
import org.xmlcml.euclid.Util;

/**
 * converts Gaussian archive to molecule, metadata and properties
 * mainly reads lines and manages them. Main logic in GaussianArchive
 * 
 * @author Peter Murray-Rust
 * 
 */
public class GaussianArchiveOrigProcessor extends /*AbstractCompchemOutputProcessor*/ LegacyProcessor {

	private static Logger LOG = Logger.getLogger(GaussianArchiveOrigProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public final static String DICT_RESOURCE = 
		"org/xmlcml/cml/converters/compchem/gaussian/gaussianArchiveDict.xml";
	
	private GaussianArchiveOrigBlock archive;

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
		this.dictionary = findDictionary();
	}
	
	/** constructor.
	 * 
	 * @param dictionary
	 */
	public GaussianArchiveOrigProcessor() {
		init();
	}
	
	@Override
	protected AbstractCommon getCommon() {
		return new GaussianCommon();
	}

	
//	/** constructor.
//	 * 
//	 * @param dictionary
//	 */
//	public GaussianArchiveProcessor(BlockContainer blockContainer) {
//		super(blockContainer);
//		init();
//	}
	
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
		String archiveS = readAndConcatenateArchiveLines();
		CMLCml cml = null;
		if (archiveS != null) {
			archive = new GaussianArchiveOrigBlock(blockContainer);
			cml = archive.parseArchiveToCML(archiveS);
			LOG.trace("CMLElement "+cml);
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
		boolean start = false;
		CMLCml cml = null;
    	while (true) {
    		String line = stringArray.readLine();
    		if (line == null) {
    			break;
    		}
    		// leading spaces are sometimes missing
    		if (line.trim().startsWith(START)) {
    			start = true;
    			String archiveS = readAndConcatenateArchiveLines();
				archive = new GaussianArchiveOrigBlock(blockContainer);
				cml = archive.parseArchiveToCML(archiveS);
				LOG.trace("CMLElement "+cml);
    			topCml.appendChild(cml);
    		}
    	}
		if (!start) {
			throw new RuntimeException("Failed to find any Gaussian archive section");
		}
    }
    
    private String readAndConcatenateArchiveLines() {
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


	public static CMLDictionary findDictionary() {
		CMLDictionary dictionary = null;
		try {
			InputStream inputStream = Util.getInputStreamFromResource(DICT_RESOURCE);
			CMLCml cml = (CMLCml) new CMLBuilder().build(inputStream).getRootElement();
			dictionary = (CMLDictionary)cml.getFirstCMLChild(CMLDictionary.TAG);
			if (dictionary == null) {
				throw new RuntimeException("Failed to find dictionary element in "+DICT_RESOURCE);
			}
		} catch (Exception e) {
			throw new RuntimeException("Cannot read dictionary", e);
		}
		return dictionary;
	}

	@Override
	// TODO
	protected AbstractBlock readBlock(List<String> lines) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void preprocessBlocks() {
		// not required
	}
	
	@Override
	protected void postprocessBlocks() {
		// not required
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
