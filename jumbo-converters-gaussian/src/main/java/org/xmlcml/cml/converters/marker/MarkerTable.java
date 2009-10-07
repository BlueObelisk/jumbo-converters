package org.xmlcml.cml.converters.marker;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class MarkerTable {
	private static Logger LOG = Logger.getLogger(MarkerTable.class);

	public final static Pattern TABLE_PATTERN = Pattern.compile("\\s*([A-Za-z]+)\\(([^\\)]+)\\)");
	private String title;
	private List<String> columnNames = new ArrayList<String>();
	
	public String getTitle() {
		return title;
	}

	public List<String> getColumnNames() {
		return columnNames;
	}
	
	private MarkerTable() {
		
	}

	private MarkerTable(String s) {
		Matcher matcher = TABLE_PATTERN.matcher(s);
		if (!matcher.matches()) {
			throw new RuntimeException("bug: cannot match: "+s+" ... "+TABLE_PATTERN.pattern());
		}
		LOG.trace(matcher.groupCount());
		title = matcher.group(1);
		String names = matcher.group(2);
		String[] nameArray = names.split("\\s+");
		for (String n : nameArray) {
			columnNames.add(n);
		}
	}
	
	static List<MarkerTable> createMarkerTablesFromTableAttribute(String tableAttribute) {
		List<MarkerTable> markerTableList = new ArrayList<MarkerTable>();
		if (tableAttribute != null) {
			// table names are in format tableName(col1 col2 ...)
			// example  frequencies(f.x f.y f.z)
			Matcher matcher = TABLE_PATTERN.matcher(tableAttribute);
			int start = 0;
			while (matcher.find(start)) {
				String tableS = matcher.group(0).trim();
				MarkerTable markerTable = new MarkerTable(tableS);
				markerTableList.add(markerTable);
				start = matcher.end();
			}
		}
		return markerTableList;
	}

	
	
}
