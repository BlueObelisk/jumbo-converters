package org.xmlcml.cml.converters.cif.filter;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.euclid.RealRange;


public class RFactorFilterTest {
	/*
<scalar dictRef="iucr:_refine_ls_r_factor_gt" dataType="xsd:string">0.0453</scalar>
<scalar dictRef="iucr:_refine_ls_wr_factor_ref" dataType="xsd:string">0.0507</scalar>
	 */
	@Test
	public void test() {
		RFactorFilter rFactorFilter = new RFactorFilter();
		rFactorFilter.setTest(RFactorFilter.CIF_R_FACTOR_GT, new RealRange(0.0, 0.1));
		CMLElement element = CrystalFixture.OK_CML;
		Assert.assertTrue(rFactorFilter.accept(element));
	}
	
	@Test
	public void test1() {
		RFactorFilter rFactorFilter = new RFactorFilter();
		rFactorFilter.setTest(RFactorFilter.CIF_R_FACTOR_GT, new RealRange(0.0, 0.04)); // fails
		CMLElement element = CrystalFixture.OK_CML;
		Assert.assertFalse(rFactorFilter.accept(element));
	}

	@Test
	public void test2() {
		RFactorFilter rFactorFilter = new RFactorFilter();
		rFactorFilter.setTest(RFactorFilter.CIF_WR_FACTOR_REF, new RealRange(0.0, 0.06)); // passes
		CMLElement element = CrystalFixture.OK_CML;
		Assert.assertTrue(rFactorFilter.accept(element));
	}

	@Test
	public void test3() {
		RFactorFilter rFactorFilter = new RFactorFilter();
		rFactorFilter.setTest(RFactorFilter.CIF_R_FACTOR_GT, new RealRange(0.0, 0.04)); // fails
		rFactorFilter.setTest(RFactorFilter.CIF_WR_FACTOR_REF, new RealRange(0.0, 0.06)); // passes
		CMLElement element = CrystalFixture.OK_CML;
		Assert.assertTrue(rFactorFilter.accept(element));
	}

}
