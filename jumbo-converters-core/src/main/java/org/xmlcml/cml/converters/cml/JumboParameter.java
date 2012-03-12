package org.xmlcml.cml.converters.cml;

/** supports commandline parameters
 * a class can prompt for its parameters on the commandline
 * 
 * @author pm286
 *
 */
public class JumboParameter {
	public static final String COMMAND = "command";
	public static final String XPATH = "xpath";
	public static final String TRANSFORMER = "transformer";

	private String name;
	private String value;
	private Boolean mandatory;
	
	public JumboParameter(String name, String value, Boolean mandatory) {
		this.name = name;
		this.value = value;
		this.mandatory = mandatory;
	}
	
	public String getName() { return name;}
	public String getValue() { return value;}
	public Boolean getMandatory() { return mandatory;}
	
	public void setName(String name) { this.name = name;}
	public void setValue(String value) { this.value = value;}
	public void setMandatory(Boolean mandatory) { this.mandatory = mandatory;}
	
	
	
}
