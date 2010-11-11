package org.xmlcml.cml.converters.compchem.gamessuk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.AbstractBlock;
import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.LegacyProcessor;
import org.xmlcml.cml.converters.compchem.gamessus.GamessUSCommon;
import org.xmlcml.cml.element.CMLScalar;

public class GamessUKPunchProcessor extends LegacyProcessor {

	private static final String ELEMENTS = "elements";
	private static final String UNIT = "unit";
	private static final String INDEX = "index";
	private static final String RECORDS = "records";
	private static final String BLOCK = "block";
	
	List<AbstractBlock> blockList = new ArrayList<AbstractBlock>();
	public GamessUKPunchProcessor() {
		
	}
	@Override
	protected AbstractCommon getCommon() {
		return new GamessUKCommon();
	}


	/**
block = fragment records = 0
block = coordinates records =     4 unit = au
  c             0.0000000      0.0000000      1.0764853
  o             0.0000000      0.0000000     -1.3801584
  h             0.0000000      1.4476141      2.2911778
  h             0.0000000     -1.4476141      2.2911778
block = connectivity records =    3
    1    2
    1    3
    1    4
block=data records= 0 index =   1
block=grid_title records= 1 index =   1
formaldehyde total charge density                                               
block=grid_axes records=   2 index=  1
  50   0.00000   10.00000  0  au xaxis
  50   0.00000   10.00000  0  au yaxis
block=grid_mapping records=   2 index=  1
  0.00000  -5.00000  -5.00000   0.00000   5.00000  -5.00000
  0.00000  -5.00000  -5.00000   0.00000  -5.00000   5.00000
block=grid_data records=    2500 index=  1 elements =   1
   0.00000
   0.00000
   0.00000
   0.00000

	 * @param lines
	 * @param lineCount
	 * @return
	 */
	@Override
	protected AbstractBlock readBlock(List<String> lines) {
		String line = lines.get(lineCount).trim();
		line = line.replaceAll("\\s*=\\s*", "=");	// remove ws round equals
		if (!line.startsWith("block=")) {
			throw new RuntimeException("expected block at line "+lineCount+"; found: "+line);
		}
		AbstractBlock block = createBlock(lines, line);
		block.convertToRawCML();
		return block;
	}
	
	protected AbstractBlock readBlock(CMLScalar scalar) {
		return null;
	}

	private AbstractBlock createBlock(List<String> lines, String line) {
		GamessUKPunchBlock block = new GamessUKPunchBlock(blockContainer);
		String[] tokens = line.split("\\s+");
		Map<String, String> nvMap = createNVMap(tokens);
		for (String name : nvMap.keySet()) {
			String value = nvMap.get(name);
			if (name.equals(BLOCK)) {
				block.setBlockName(value);
			} else if (name.equals(RECORDS)) {
				block.records = Integer.parseInt(value);
			} else if (name.equals(INDEX)) {
				block.index = Integer.parseInt(value);
			} else if (name.equals(UNIT)) {
				block.unit = value;
			} else if (name.equals(ELEMENTS)) {
				block.elements = Integer.parseInt(value);
			} else {
				throw new RuntimeException("unknown keyword: "+name);
			}
		}
		if (block.records == null) {
			throw new RuntimeException("no records count given");
		}
		lineCount++;
		for (int i = 0; i < block.records; i++) {
			block.add(lines.get(lineCount++));
		}
		return block;
	}
	
	public static Map<String, String> createNVMap(String[] tokens) {
		Map<String, String> nvMap = new HashMap<String, String>();
		for (String token : tokens) {
			String[] bits = token.split("=");
			if (bits.length != 2) {
				throw new RuntimeException("bad block heading "+token);
			}
			String name = bits[0];
			String value = bits[1];
			if (nvMap.containsKey(name)) {
				throw new RuntimeException("Map already contains name: "+name);
			}
			nvMap.put(name, value);
		}
		return nvMap;
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
