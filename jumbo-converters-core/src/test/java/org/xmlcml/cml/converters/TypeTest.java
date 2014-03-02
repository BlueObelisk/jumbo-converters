package org.xmlcml.cml.converters;

import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cml.converters.Type.ObjectType;
import org.xmlcml.euclid.Util;

public class TypeTest {

	@Test
	public void testHashCodeAndEquals() {
		Util.println("=============TypeTest==============");
		Type t1 = new Type("thing/majob", ObjectType.TEXT, "doc");
		Type t2 = new Type("thing/majob", ObjectType.TEXT,
				new String[] { "doc" });
		Type t3 = new Type("thing/majob", ObjectType.TEXT, "docy");
		Assert.assertEquals(t1, t2);
		Assert.assertEquals(t1.hashCode(), t2.hashCode());
		Assert.assertFalse(t1.equals(t3));
		Assert.assertFalse(t1.hashCode() == t3.hashCode());
	}

	@Test
	public void noNulls() {
		try {
			/*Type t = */new Type(null, ObjectType.TEXT, "doc");
			fail();
		} catch (IllegalArgumentException e) {
			;
		}
		try {
			/*Type t = */new Type("text/plain", null, "doc");
			fail();
		} catch (IllegalArgumentException e) {
			;
		}
		try {
			/*Type t = */new Type("text/plain", ObjectType.TEXT, (String) null);
			fail();
		} catch (IllegalArgumentException e) {
			;
		}
		try {
			/*Type t = */new Type("text/plain", ObjectType.TEXT, "doc", null);
			fail();
		} catch (IllegalArgumentException e) {
			;
		}

	}
	
	@Test
	public void testGetDefaultExtension() {
		Assert.assertEquals("cml", Type.CML.getDefaultExtension(), "cml");
		Assert.assertEquals("mdl", Type.MDL.getDefaultExtension(), "mol");
		Assert.assertEquals("xyz", Type.XYZ.getDefaultExtension(), "xyz");
	}


}
