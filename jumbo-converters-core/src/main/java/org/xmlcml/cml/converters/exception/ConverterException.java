package org.xmlcml.cml.converters.exception;

public class ConverterException extends Exception{
    
    /**ConverterException is a checked exception which should be thrown by any
     * Converter when the converter cannot output a file for any reason.
     * @author nwe23
     */
    
    private static final long serialVersionUID = 4833127343362598256L;
    
    public ConverterException(){
        super();
    }
    public ConverterException(String message){
        super(message);
    }
    public ConverterException(String message, Throwable e){
        super(message,e);
    }
}
