package org.xmlcml.cml.converters.text;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PatternContainer {

	private List<Pattern> patternList = new ArrayList<Pattern>();
	private Integer offset = null;
	
	public PatternContainer(String patternS, String multipleS, Integer offset) {
		this.patternList = createPatternList(patternS, multipleS);
		if (patternList == null || patternList.size() == 0) {
//			throw new IllegalArgumentException("patternList must have elements");
		}
		this.offset = offset;
	}
		
	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	public int getOffset() {
		return (offset == null) ? 0 : offset;
	}

	public int size() {
		return patternList.size();
	}
	
	public Pattern get(int i) {
		return patternList.get(i);
	}

	public List<Pattern> getPatternList() {
		return patternList;
	}
	
	public boolean equals(Object o2) {
		boolean equals = false;
		if (o2 instanceof PatternContainer) {
			PatternContainer p2 = (PatternContainer) o2;
			if (this.offset != null && this.offset.equals(p2.offset)) {
				if (this.patternList.size() == p2.patternList.size()) {
					equals = true;
					for (int i = 0; i < this.patternList.size(); i++) {
						if (!this.patternList.get(i).equals(p2.patternList.get(i))) {
							equals = false;
							break;
						}
					}
				}
			}
		}
		return equals;
	}

	private static List<Pattern> createPatternList(String patternS, String multipleS) {
		List<Pattern> patterns = new ArrayList<Pattern>();
		if (patternS != null) {
			String[] pat = (multipleS == null) ? new String[]{patternS} : patternS.split(multipleS); 
			for (int i = 0; i < pat.length; i++) {
				try {
					patterns.add(Pattern.compile(pat[i], Pattern.DOTALL));
				} catch (Exception e) {
					throw new RuntimeException("Bad regex in: "+pat[i]);
				}
			}
		}
		return patterns;
	}

	public String toString() {
		String s = "";
		s += this.patternList+" | " + offset;
		return s;
	}
}
