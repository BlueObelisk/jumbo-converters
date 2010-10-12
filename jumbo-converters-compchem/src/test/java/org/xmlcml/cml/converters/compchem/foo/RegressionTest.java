 package org.xmlcml.cml.converters.compchem.foo;

import org.junit.Test;
import org.xmlcml.cml.converters.compchem.gamessuk.GamessUKPunch2XMLConverter;
import org.xmlcml.cml.converters.testutils.RegressionSuite;

/**
 *
 * @author ojd20
 */
public class RegressionTest {

	   @Test
	   public void gamessuk2XML() {
	      RegressionSuite.run("compchem/foo/punch", "foo", "xml",
	                          new Foo2XMLConverter());
	                              
	   }
	   @Test
	   public void gamessukXML2CML() {
	      RegressionSuite.run("compchem/foo/complete", "xml", "cml",
	                          new FooXML2CMLConverter());
	                              
	   }
}
