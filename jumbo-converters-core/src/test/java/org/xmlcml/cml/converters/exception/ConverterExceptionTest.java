package org.xmlcml.cml.converters.exception;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

public class ConverterExceptionTest {

    @Test(expected=ConverterException.class)
    public void testConverterException() throws ConverterException {
        throw new ConverterException();
    }

    @Test
    public void testConverterExceptionString() {
        try {
            throw new IOException("Test");
        } catch (IOException e) {
            try {
                throw new ConverterException(e.getMessage());
            } catch (ConverterException e1) {
                Assert.assertEquals("Test", e1.getMessage());
                return;
            }
        }
    }

    @Test
    public void testConverterExceptionStringThrowable() {
        String text="sdfgsdf";
        try{
            /*Double d = */Double.parseDouble(text);
        }catch(RuntimeException e){
            try {
                throw new ConverterException("Test", e);
            } catch (ConverterException e1) {
               Assert.assertTrue(e1.getStackTrace().length>=21);
            }
        }
    }

}
