package org.xmlcml.cml.converters;

import java.util.ArrayList;
import java.util.List;

/** used for testing
 * 
 * @author pm286
 *
 */
public class SimpleConverter extends AbstractConverter {

	public Type getOutputType() {
		return Type.TXT;
	}

	public Type getInputType() {
		return Type.TXT;
	}
	
	@Override
	public List<String> convertToText(List<String> lines) {
		List<String> output = new ArrayList<String>();
		output.add("Converted to uppercase");
		for (String line : lines) {
			output.add(line.toUpperCase());
		}
		return output;
	}

}
