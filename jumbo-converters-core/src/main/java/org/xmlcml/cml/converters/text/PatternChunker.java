package org.xmlcml.cml.converters.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class PatternChunker {

//	public enum Position {
//		START,
//		END
//	}
	private List<Pattern> patternList = new ArrayList<Pattern>();
	private Integer offset = null;
//	private Integer endOffset = null;
//	private Position position;
	
	public PatternChunker(List<Pattern> patternList, Integer offset) {
		this.patternList = patternList;
		if (patternList == null || patternList.size() == 0) {
//			throw new IllegalArgumentException("patternList must have elements");
		}
		this.offset = offset;
	}
	
	public PatternChunker(Pattern pattern, Integer offset) {
		this(Arrays.asList(new Pattern[]{pattern}), offset);
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
		if (o2 instanceof PatternChunker) {
			PatternChunker p2 = (PatternChunker) o2;
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

	public String toString() {
		String s = "";
		s += this.patternList+" | " + offset;
		return s;
	}
}
