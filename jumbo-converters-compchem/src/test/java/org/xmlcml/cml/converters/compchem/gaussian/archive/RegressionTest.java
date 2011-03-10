package org.xmlcml.cml.converters.compchem.gaussian.archive;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.converters.testutils.RegressionSuite;

/**
 *
 * @author ojd20
 */
@Ignore // till we have templates
public class RegressionTest {

	   @Test
	   public void gaussianArchive2XML() {
//	      RegressionSuite.run("compchem/gaussian/archive", "arc", "xml",
//	                          new GaussianArchive2XMLConverter());
	   }

	   @Test
	   public void gaussianArchiveXML2CML() {
	      RegressionSuite.run("compchem/gaussian/archive", "xml", "cml",
	                          new GaussianArchiveXML2CMLConverter());
	   }


}
