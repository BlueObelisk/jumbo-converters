package org.xmlcml.cml.converters;

import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.element.CMLArray;

/**
 * container for lines which cannot be parsed/interpreted at this stage
 * @author pm286
 *
 */
public class AnonymousBlock extends AbstractBlock {

	public static final String ANONYMOUS = "anonymous";
	private String delimiter = CMLConstants.S_PIPE;
	public AnonymousBlock(BlockContainer blockContainer) {
		super(blockContainer);
		setBlockName(ANONYMOUS);
	}
	
	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	@Override
	public void convertToRawCML() {
		CMLArray array = new CMLArray();
		array.setDelimiter(delimiter);
		array.setArray(lines.toArray(new String[0]));
		element = array;
	}

}
