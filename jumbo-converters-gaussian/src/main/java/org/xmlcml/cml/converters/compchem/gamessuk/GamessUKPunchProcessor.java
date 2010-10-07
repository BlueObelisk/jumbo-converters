package org.xmlcml.cml.converters.compchem.gamessuk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nu.xom.Attribute;

import org.xmlcml.cml.base.CMLElement;

public class GamessUKPunchProcessor {

	List<Block> blockList = new ArrayList<Block>();
	public GamessUKPunchProcessor() {
		
	}
	public void read(List<String> lines) {
		int lineCount = 0;
		while (lineCount < lines.size()) {
			Block block = readBlock(lines, lineCount);
			blockList.add(block);
			lineCount += block.lines.size() + 1;
		}
	}
	
	public List<CMLElement> getBlockList() {
		List<CMLElement> cmlList = new ArrayList<CMLElement>();
		for (Block block : blockList) {
			cmlList.add(block.element);
		}
		return cmlList;
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
	private Block readBlock(List<String> lines, int lineCount) {
		String line = lines.get(lineCount).trim();
		line = line.replaceAll("\\s*=\\s*", "=");	// remove ws round equals
		System.out.println(line);
		if (!line.startsWith("block=")) {
			throw new RuntimeException("expected block at line "+lineCount+"; found: "+line);
		}
		Block block = createBlock(lines, lineCount, line);
		block.convertToRawCML();
		return block;
	}
	
	private Block createBlock(List<String> lines, int lineCount, String line) {
		Block block = new Block();
		String[] tokens = line.split("\\s+");
		Map<String, String> nvMap = createNVMap(tokens);
		for (String name : nvMap.keySet()) {
			String value = nvMap.get(name);
			if (name.equals("block")) {
				block.blockName = value;
			} else if (name.equals("records")) {
				block.records = Integer.parseInt(value);
			} else if (name.equals("index")) {
				block.index = Integer.parseInt(value);
			} else if (name.equals("unit")) {
				block.unit = value;
			} else if (name.equals("elements")) {
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

	

}
