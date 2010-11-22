package org.xmlcml.cml.converters.compchem.gaussian.archive;

import static org.xmlcml.euclid.EuclidConstants.S_ATSIGN;
import static org.xmlcml.euclid.EuclidConstants.S_BACKSLASH;
import static org.xmlcml.euclid.EuclidConstants.S_EMPTY;
import static org.xmlcml.euclid.EuclidConstants.S_SPACE;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.AbstractBlock;
import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.BlockContainer;
import org.xmlcml.cml.converters.LegacyProcessor;
import org.xmlcml.cml.converters.compchem.gamessus.GamessUSCommon;
import org.xmlcml.cml.converters.compchem.gaussian.GaussianCommon;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLScalar;

/**
1\1\GINC-PIRX\FOpt\UB3LYP\Aug-CC-pVDZ\C4H8Cl1(2)\BERND\19-Feb-2002\0\\
#P B3LYP/AUG-CC-PVDZ OPT=TIGHT GEOM=CHECK GUESS=READINT=ULTRAFINE\\Be
D001 with INT=ULTRAFINE\\0,2\C,0.1063168353,0.3005635652,-0.5502851935
\C,0.1053918322,-1.157928312,-0.5404967856\C,1.3891660007,0.9682412707
,-1.0097749893\H,-0.7786669558,0.7139466375,-1.0458655312\H,1.55655799
58,0.73320935,-2.0718423113\H,1.327841017,2.0566771132,-0.8980161267\H
,2.2479630589,0.603209853,-0.4322274841\Cl,-0.2178927161,0.8953362005,
1.2758334388\C,-1.1453929968,-1.9640369835,-0.4846598299\H,1.059275967
3,-1.6602096716,-0.361576248\H,-1.0103688266,-2.9449592768,-0.96235794
85\H,-1.4450387789,-2.1570821331,0.5624777398\H,-1.9862773313,-1.44654
45235,-0.9684597597\\Version=x86-Linux-G98RevA.7\HF=-617.4354366\S2=0.
755661\S2-1=0.\S2A=0.750023\RMSD=7.998e-09\RMSF=8.394e-07\Dipole=0.129
3808,-0.5162435,-0.9249031\PG=C01 [X(C4H8Cl1)]\\@
 * @author pm286
 *
 */
public class GaussianArchiveProcessor extends LegacyProcessor {

	private static Logger LOG = Logger.getLogger(LegacyProcessor.class);
	/** start of archive */
	final static String START = 
		"1"+S_BACKSLASH+"1"+S_BACKSLASH+"GINC";
	/** separated by \ */
	final static String BLOCK_SEPARATOR = S_BACKSLASH+S_BACKSLASH;
	/** separated by \\ */
	final static String DOUBLE_BLOCK_SEPARATOR = BLOCK_SEPARATOR+BLOCK_SEPARATOR;
	/** ends with \\@ */
	final static String TERMINATOR = BLOCK_SEPARATOR+S_ATSIGN;
	private String archiveString;
	private CMLMolecule lastMolecule;
	
	public GaussianArchiveProcessor() {
	}
	
	@Override
	protected AbstractCommon getCommon() {
		return new GaussianCommon();
	}

	@Override
	protected String getTemplateResourceName() {
		return "org/xmlcml/cml/converters/compchem/gaussian/archive/template.xml";
	}
	
	/**
	 * @param lines
	 * @param lineCount
	 * @return
	 */
	@Override
	protected AbstractBlock readBlock(List<String> lines) {
		// read one line and split it into the block
		String line = lines.get(lineCount++);
		AbstractBlock block = createBlock();
		block.setBlockName("block"+blockContainer.getBlockList().size());
		String[] sublines = line.split(BLOCK_SEPARATOR);
		for (String subline : sublines) {
			block.add(subline);
		}
		block.convertToRawCML();
		CMLMolecule molecule = block.getMolecule();
		if (molecule != null) {
			this.lastMolecule = molecule;
		}
		return block;
	}

	protected AbstractBlock readBlock(CMLScalar scalar) {
		return null;
	}

	/**
	 * read block start , create name
	 * then read lines until next block start
	 * @return
	 */
	private AbstractBlock createBlock() {
		return new GaussianArchiveBlock(blockContainer);
	}
	
	@Override
	protected void preprocessBlocks(CMLElement element) {
		archiveString = readAndConcatenateArchiveLines();
		archiveString = archiveString.substring(0, archiveString.length()-TERMINATOR.length());
		String[] archiveLines = archiveString.split(DOUBLE_BLOCK_SEPARATOR);
		if (archiveLines.length < 5) {
	         throw new RuntimeException(
	                 "Expected 5 or more fields in archive; found: " + archiveLines.length);
	    }
		lines = new ArrayList<String>(archiveLines.length);
		for (String archiveLine : archiveLines) {
			lines.add(archiveLine);
		}
	}
	
	@Override
	protected void postprocessBlocks() {
		// not required
	}
	
	
    private String readAndConcatenateArchiveLines() {
    	lineCount = 0;
    	StringBuffer sb = new StringBuffer();
    	String line = lines.get(lineCount);
    	boolean startsWithSpace = line.startsWith(S_SPACE);
		// terminator may be split over two lines, so test buffer
    	while (true) {
        	line = lines.get(lineCount++);
    		if (startsWithSpace) {
    			line = line.substring(1);
    		}
    		int l = line.length();
			sb.append(line);
			if (sb.toString().endsWith(TERMINATOR)) {
				break;
			}
			if (l != 70) {
				System.out.println(":"+line+":"+line.length());
				throw new RuntimeException("Bad line length ("+l+") :"+line);
			}
    		if (line.trim().equals(S_EMPTY)) {
    			String sbs = sb.toString();
    			throw new RuntimeException("missing terminator: "+
    					sbs.substring(Math.min(40, sbs.length())));
    		}
    	}
    	return sb.toString();
    }

	@Override
	protected AbstractBlock createAbstractBlock(BlockContainer blockContainer) {
		return new GaussianArchiveOrigBlock(blockContainer);
	}
}
