package org.xmlcml.cml.converters.compchem.gaussian.archive;

import org.junit.Ignore;

import org.junit.Test;
import org.xmlcml.cml.converters.compchem.gaussian.archive.GaussianArchive2XMLConverter;
import org.xmlcml.cml.converters.compchem.gaussian.archive.GaussianArchiveXML2CMLConverter;
import org.xmlcml.cml.converters.compchem.gaussian.input.CML2GaussianInputConverter;
import org.xmlcml.cml.converters.compchem.gaussian.logold.GaussianLog2CMLConverter;
import org.xmlcml.cml.converters.compchem.gaussian.logold.GaussianLog2XMLConverter;
import org.xmlcml.cml.converters.testutils.RegressionSuite;

/**
 *
 * @author ojd20
 */
public class RegressionTest {

	   @Test
	   public void gaussianArchive2XML() {
	      RegressionSuite.run("compchem/gaussian/archive", "arc", "xml",
	                          new GaussianArchive2XMLConverter());
	   }

	   @Test
	   public void gaussianArchiveXML2CML() {
	      RegressionSuite.run("compchem/gaussian/archive", "xml", "cml",
	                          new GaussianArchiveXML2CMLConverter());
	   }


}
