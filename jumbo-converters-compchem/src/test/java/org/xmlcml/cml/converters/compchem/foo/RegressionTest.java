 package org.xmlcml.cml.converters.compchem.foo;

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
	   public void foo2XML() {
//	      RegressionSuite.run("compchem/foo/punch", "foo", "xml",
//	                          new Foo2XMLConverter());
	                              
	   }
	   @Test
	   public void fooXML2CML() {
	      RegressionSuite.run("compchem/foo/complete", "xml", "cml",
	                          new FooXML2CMLConverter());
	                              
	   }
}
