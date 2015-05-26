 package org.xmlcml.cml.converters.compchem.qchem.log;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.converters.testutils.RegressionSuite;

/**
 *
 * @author ojd20
 */
@Ignore // till we have templates
public class RegressionTest {

	
	   /** note: writes to target\test\compchem\qchem\log\out\test1.xml
	    * 
	    */
	   @Test
	   public void qchemLog2XML() {
//	      RegressionSuite.run("compchem/qchem/log", "log", "xml",
//	                          new QchemLog2XMLConverter());
	   }

	   /** note: writes to target\test\compchem\qchem\log\out\test1.xml
	    * 
	    */
	   @Test
	   @Ignore
	   public void qchemXML2CML() {
	      RegressionSuite.run("compchem/qchem/log", "xml", "cml",
	                          new QchemLogXML2CMLConverter());
	   }
}
