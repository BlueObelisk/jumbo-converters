 package org.xmlcml.cml.converters.compchem.rdf;

import gigadot.semsci.converters.chem.CompChemCML2RDFConverter;

import org.junit.Ignore;
import org.junit.Test;
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
	   @Ignore
	   public void gamessusPunch2XML() {
	      RegressionSuite.run("compchem/rdf", "cml", "rdf",
	                          new CompChemCML2RDFConverter());
	   }

}
