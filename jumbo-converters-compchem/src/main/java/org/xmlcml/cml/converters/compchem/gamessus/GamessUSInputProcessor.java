package org.xmlcml.cml.converters.compchem.gamessus;

import java.util.List;


import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.AbstractBlock;
import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.LegacyProcessor;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.euclid.Util;

/**
 $CONTRL SCFTYP=RHF RUNTYP=OPTIMIZE EXETYP=RUN
         COORD=ZMT NZVAR=42
         QMTTOL=5.0E-6 NPRINT=-5 ICHARG=0 MULT=1
         ISPHER=-1
 $END
 $SYSTEM TIMLIM=14400 MWORDS=50 MEMDDI=0 PARALL=.T. $END
 $DFT DFTTYP=B3LYP $END
 $BASIS GBASIS=CCD $END
 $SCF DIRSCF=.T. FDIFF=.T. NPUNCH=0 DIIS=.T. CONV=1.0E-5 $END
 $GUESS GUESS=HUCKEL $END
 $STATPT NSTEP=100 OPTTOL=0.0001 NPRT=-2 NPUN=-2 $END
 $ZMAT DLC=.T. AUTO=.T.
       IFZMAT(1)=3,7,5,3,2, 3,13,7,5,3
       FVALUE(1)=-15, -15
 $END
 $DATA
HCO-L-Ala-NH2 - OPTIMIZE - B3LYP/cc-pVDZ - N_at = 16
C1
H1    
C2         1 rC2H1        
N3         2 rN3C2             1 aN3C2H1            
O4         2 rO4C2             3 aO4C2N3                 1 pO4C2N3H1                
C5         3 rC5N3             2 aC5N3C2                 1 dC5N3C2H1                
...
H16       13 rH16N13           7 aH16N13C7              15 pH16N13C7H15             

rC2H1         1.0889743331
rN3C2         1.3394412977
...
rH16N13       0.9941599400
aN3C2H1             110.9023260337
aO4C2N3             128.5418780807
...
aH16N13C7           119.3011272991
pO4C2N3H1                  179.1630605030
dC5N3C2H1                  178.0029069366
...
pH16N13C7H15              -146.5149511775
 $END

 * @author pm286
 *
 */
public class GamessUSInputProcessor extends LegacyProcessor {

	public static final String KEYWORD   = " $";
	public static final String END     = "END";
	public static final String CONTROL = "CONTROL";
	public static final String SYSTEM  = "SYSTEM";
	public static final String DFT     = "DFT";
	public static final String BASIS   = "BASIS";
	public static final String SCF     = "SCF";
	public static final String STATPT  = "STATPT";
	public static final String ZMAT    = "ZMAT";
	public static final String DATA    = "DATA";
	
	public GamessUSInputProcessor() {

	}
	
	@Override
	protected AbstractCommon getCommon() {
		return new GamessUSCommon();
	}

	
	/**
	 * @param lines
	 * @param lineCount
	 * @return
	 */
	@Override
	protected AbstractBlock readBlock(List<String> lines) {
		AbstractBlock block = null;
		String line = Util.rightTrim(lines.get(lineCount));
		if (!line.startsWith(KEYWORD)) {
			block = createAnonymousBlock();
		} else {
			block = createBlock();
		}
		block.convertToRawCML();
		return block;
	}

	protected AbstractBlock readBlock(CMLScalar scalar) {
		return null;
	}

	private AbstractBlock createAnonymousBlock() {
		AbstractBlock block = new GamessUSInputBlock(blockContainer);
		while (lineCount < lines.size()) {
			String line = Util.rightTrim(lines.get(lineCount));
			if (line.startsWith(KEYWORD)) {
				break;
			}
			block.add(line);
			lineCount++;
		}
		return block;
	}

	/**
	 * read block start , create name
	 * then read lines until next block start
	 * @return
	 */
	private AbstractBlock createBlock() {
		AbstractBlock block = new GamessUSInputBlock(blockContainer);
		String line = Util.rightTrim(lines.get(lineCount));
		if (!line.startsWith(KEYWORD)) {
			throw new RuntimeException("should start with :"+KEYWORD);
		}
		// behead $ABCDEF... at start
		line = line.substring(2);
		int idx = (line+" ").indexOf(" ");
		block.setBlockName(line.substring(0, idx));
		line = line.substring(idx);
		lineCount++;
		String terminator = KEYWORD+END;
		while (true) {
			if (line.endsWith(terminator)) {
				block.add(line.substring(0, line.length()-terminator.length()));
				break;
			}
			block.add(line);
			if (lineCount >= lines.size()) {
				break;
			}
			line = Util.rightTrim(lines.get(lineCount++));
		}
		return block;
	}
	
	@Override
	protected void preprocessBlocks(CMLElement element) {
		// not required
	}
	
	@Override
	protected void postprocessBlocks() {
		// not required
	}
	
}
