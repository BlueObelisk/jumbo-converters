package org.xmlcml.cml.converters.metadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Nodes;

import org.xmlcml.euclid.Util;

public class Country {

	private static final String ID = "id";
	public static String COUNTRY_FILE = "org/xmlcml/cml/converters/metadata/countries.xml";
	public static List<Country> countryList;
	public static Map<String, Country> countryByName;
	static {
		countryList = new ArrayList<Country>();
		countryByName = new HashMap<String, Country>();
		readCountries(COUNTRY_FILE);
	}
	private static void readCountries(String filename) {
		
		Element root = null;
		try {
			root = new Builder().build(Util.getInputStreamFromResource(filename)).getRootElement();
		} catch (Exception e) {
			throw new RuntimeException("Cannot read/parse resource: "+filename, e);
		}
		Elements countries = root.getChildElements();
		for (int i = 0; i < countries.size(); i++) {
			Country country = createCountry(countries.get(i));
			addCountry(country);
		}
	}
	
	private static void addCountry(Country country) {
		countryList.add(country);
		List<String> nameList = country.nameList;
		for (String name : nameList) {
			countryByName.put(name, country);
		}
	}

	/*
	  <country id="UK">
	    <name>UK</name>
	    <name>United Kingdom</name>
	    <name>Great Britain</name>
	    <regionList>
		  <region id="camb">
		    <name>Cambridgeshire</name>
		    <name>Cambs</name>
		  </region>
	    </regionList>
	    <postcode>(GIR 0AA|[A-PR-UWYZ]([0-9]{1,2}|([A-HK-Y][0-9]|[A-HK-Y][0-9]([0-9]|[ABEHMNPRV-Y]))|[0-9][A-HJKPS-UW]) [0-9][ABD-HJLNP-UW-Z]{2})</postcode>
	  </country>
		 */
	private String id;
	private List<String> nameList;
	private List<Region> regionList;
	private Pattern postcodePattern;

	public Country() {
		
	}
	public Country(String id) {
		this.id = id;
	}
	
	private static Country createCountry(Element countryElement) {
		String id = countryElement.getAttributeValue(ID);
		Country country = new Country(id);
		country.addNames(countryElement);
		country.addRegionList(countryElement);
		country.addPostcode(countryElement);
		return country;
	}

	void addRegionList(Element countryElement) {
		Nodes regionNodes = countryElement.query("./regionList/region");
		regionList = new ArrayList<Region>();
		for (int i = 0; i < regionNodes.size(); i++) {
			Region region = Region.createRegion((Element) regionNodes.get(i));
			regionList.add(region);
		}
	}

	void addNames(Element countryElement) {
		Nodes nameNodes = countryElement.query("./name");
		nameList = new ArrayList<String>();
		for (int i = 0; i < nameNodes.size(); i++) {
			nameList.add(nameNodes.get(i).getValue());
		}
	}

	private void addPostcode(Element countryElement) {
		Nodes postcodeNodes = countryElement.query("./postcode");
		if (postcodeNodes.size() == 1) {
			postcodePattern = Pattern.compile(postcodeNodes.get(0).getValue());
		}
	}

}
