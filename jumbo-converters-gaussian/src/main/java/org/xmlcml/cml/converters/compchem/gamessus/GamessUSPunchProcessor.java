package org.xmlcml.cml.converters.compchem.gamessus;

import java.util.List;

import org.xmlcml.cml.converters.AbstractBlock;
import org.xmlcml.cml.converters.AnonymousBlock;
import org.xmlcml.cml.converters.LegacyProcessor;

/**
 *  $DATA  
HCO-L-Ala-NH2 - OPTIMIZE - B3LYP/cc-pVDZ - N_at = 16                            
C1       0
H1          1.0     -2.9805271364       .9147039208       .1059830464
   CCD     0
        
C2          6.0     -2.0999914660       .2907269834      -.0395326325
   CCD     0
        
...
H16         1.0      -.5854990105     -1.5842704744       .5426395725
   CCD     0
        
 $END      
 $ZMAT   IZMAT(1)=
         3,   2,   3,   5,   7,
         3,   3,   5,   7,  13,
         1,   1,   2,
         1,   2,   4,
         ...
         1,  13,  15,
         2,   1,   2,   3,
         2,   1,   2,   4,
...
         2,  15,  13,  16,
         3,   1,   2,   3,   6,
         3,   1,   2,   3,   5,
         ...
         3,  14,   7,  13,  16,
         3,  14,   7,  13,  15,
 $END

 * @author pm286
 *
 */
public class GamessUSPunchProcessor extends LegacyProcessor {

	public static final String KEYWORD = " $";
	private static final String END = " $END";
	
	public GamessUSPunchProcessor() {
	}
	
	/**
	 * @param lines
	 * @param lineCount
	 * @return
	 */
	@Override
	protected AbstractBlock readBlock(List<String> lines) {
		AbstractBlock block = null;
		String line = lines.get(lineCount);
		if (!line.startsWith(KEYWORD)) {
			block = createAnonymousBlock();
		} else {
			block = createBlock();
		}
		block.convertToRawCML();
		return block;
	}

	private AbstractBlock createAnonymousBlock() {
		AbstractBlock block = new AnonymousBlock();
		while (lineCount < lines.size()) {
			String line = lines.get(lineCount);
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
		AbstractBlock block = new GamessUSPunchBlock();
		String line = lines.get(lineCount);
		block.setBlockName(line.substring(2, 6).trim());
		lineCount++;
		while (lineCount < lines.size() && !lines.get(lineCount).startsWith(END)) {
			block.add(lines.get(lineCount++));
		}
		lineCount++;
		return block;
	}
	
}
