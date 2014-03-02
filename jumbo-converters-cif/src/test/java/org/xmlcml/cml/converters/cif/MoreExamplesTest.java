package org.xmlcml.cml.converters.cif;

import org.junit.Test;

/** mainly for consuming by JumboConvertersMolecule.
 * 
 * @author pm286
 *
 */
public class MoreExamplesTest {

    @Test
    public void testMoreFile() {
    	CIF2CMLConverter converter = new CIF2CMLConverter();
    	String[] args = {"-i", "src/test/resources/more/", "-o", "target/more/"};
    	converter.runArgs(args);
    }

}
