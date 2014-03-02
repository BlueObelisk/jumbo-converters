package org.xmlcml.cml.converters.metadata;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;

import org.xmlcml.cml.converters.util.ConverterUtils;

public class City {
	Coord2 coord2;
	public City() {
		
	}
	public  void setCoord2(Coord2 coord2) {
		this.coord2 = coord2;
	}
	
	
	public static City lookupInWikipedia(String name) {
		City city = null;
		name = name.trim().replaceAll(" ", "_");
		String url = "http://en.wikipedia.org/wiki/"+name;
		Document html = null;
		try {
			html = ConverterUtils.parseHtmlWithTagSoup(new URL(url).openStream());
		} catch (Exception e) {
			throw new RuntimeException("cannot parse/find city"+name, e);
		}
		if (html != null) {
			//<span class="latitude">40o26'30"N</span> <span class="longitude">80o00'00"W</span>
			Nodes latNodes = html.query("//*[local-name()='span' and @class='latitude']");
			System.out.println(latNodes.size());
			Coord latCoord = (latNodes.size() == 0) ? null : Coord.getCoord(latNodes.get(0).getValue());
			Nodes longNodes = html.query("//*[local-name()='span' and @class='longitude']");
			System.out.println(longNodes.size());
			Coord longCoord = (longNodes.size() == 0) ? null : Coord.getCoord(longNodes.get(0).getValue());
			if (latCoord != null && longCoord != null) {
				Coord2 coord2 = new Coord2(latCoord, longCoord, name);
				city = new City();
				city.setCoord2(coord2);
			}
		}
		return city;
		
	}
	public static void main(String[] args) {
		City city = City.lookupInWikipedia("Pittsburgh");
		System.out.println(city.getCoord2().getKML().toXML());
	}
	public Coord2 getCoord2() {
		return coord2;
	}
}
class Coord2 {
	Coord lat;
	Coord lng;
	String name;
	public Coord2(Coord lat, Coord lng, String name) {
		this.lat = lat;
		this.lng = lng;
		this.name = name;
	}
	static String KMLNS = "http://www.opengis.net/kml/2.2";
	public Element getKML() {
		/*
<kml xmlns="http://www.opengis.net/kml/2.2">
<Placemark>
  <name>New York City</name>
  <description>New York City</description>
  <Point>
    <coordinates>-74.006393,40.714172,0</coordinates>
  </Point>
</Placemark>
</kml>
		 */
		Element kmlElement = new Element("kml", KMLNS);
		Element placemarkElement = new Element("Placemark", KMLNS);
		kmlElement.appendChild(placemarkElement);
		Element nameElement = new Element("name", KMLNS);
		nameElement.appendChild(name);
		placemarkElement.appendChild(nameElement);
		Element pointElement = new Element("Point", KMLNS);
		placemarkElement.appendChild(pointElement);
		Element coordElement = new Element("coordinates", KMLNS);
		pointElement.appendChild(coordElement);
		coordElement.appendChild(""+lat.toDouble()+","+lng.toDouble()+",0");
		return kmlElement;
	}

}
class Coord {
	// actually has non-ascii chars 40o26'30"N
	static Pattern pattern = Pattern.compile("(\\d\\d)[^\\d]+(\\d\\d)[^\\d]+(\\d\\d)[^\\d]+([NEWS])");
	int deg;
	int min;
	int sec;
	String news;
	String name;
	double degf;
	
	public Coord(int deg, int min, int sec, String news) {
		this.deg = deg;
		this.min = min;
		this.sec = sec;
		this.news = news;
		int sgn = (news.equals("S") || news.equals("W")) ? -1 : 1;
		this.degf = (sgn) * deg+(double)min/60.+(double)sec/3600.;
	}
	
	public static Coord getCoord(String s) {
		Coord coord = null;
		Matcher m = pattern.matcher(s.trim());
		if (m.matches()) {
			coord = new Coord(
					Integer.parseInt(m.group(1)),Integer.parseInt(m.group(2)),Integer.parseInt(m.group(3)),m.group(3));
		}
		return coord;
	}
	public double toDouble() {
		return degf;
	}
	public String toString() {
		return deg+"o"+min+"'"+sec+"\""+news;
	}
}
