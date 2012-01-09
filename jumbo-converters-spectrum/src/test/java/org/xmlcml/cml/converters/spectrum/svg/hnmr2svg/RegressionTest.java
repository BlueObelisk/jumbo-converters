/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xmlcml.cml.converters.spectrum.svg.hnmr2svg;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.converters.spectrum.SpectrumCommon;
import org.xmlcml.cml.converters.testutils.RegressionSuite;

/**
 *
 * @author ojd20
 */
public class RegressionTest {

	@Test
	public void dummy() {
		
	}
	
    @Test
    @Ignore // no files availalble
    public void svg2cmlspect() {
        RegressionSuite.run(SpectrumCommon.SVG_HNMR2SVG_DIR, "cml", "svg",
                            new CMLHNMRSpect2SVGConverter());
    }

}
