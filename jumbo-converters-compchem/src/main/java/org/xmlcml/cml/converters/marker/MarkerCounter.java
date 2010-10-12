package org.xmlcml.cml.converters.marker;

import nu.xom.Element;

import org.apache.log4j.Logger;

/**
 * provides a counter for a Marker, such as a Regex or Template
 * if counts the number of times THIS Marker can be replicated.
 * 
 * The 
 * @author pm286
 *
 */
public abstract class MarkerCounter {
	private static Logger LOG = Logger.getLogger(MarkerCounter.class);

	protected Marker marker;
	
	protected boolean markerMatched;
	protected int markerCount;
	private int minCount;
	private int maxCount;
	protected boolean markerSkip;
	private int moduleStart;
	private int moduleEnd;

	protected MarkerCounter(Marker marker) {
		this.marker = marker;
		extractMaxMinSkip();
	}
	
	private void extractMaxMinSkip() {
		interpretMinAndMaxCountAttributes();
		interpretSkipAttribute();
	}
	private void interpretMinAndMaxCountAttributes() {
		Element markerDelegate = marker.getXMLElement();
		int min = ParserUtil.parseAttribute(markerDelegate, ParserUtil.MIN_COUNT, 1);
		setMinCount(min);
		int max = ParserUtil.parseAttribute(markerDelegate, ParserUtil.MAX_COUNT, 1);
		setMaxCount(max);
	}

	private void interpretSkipAttribute() {
		String skipValue = marker.getXMLElement().getAttributeValue(Marker.SKIP);
		setSkip(skipValue != null);
		if (Marker.STAR.equals(skipValue)) {
			setMinCount(0);
			setMaxCount(Integer.MAX_VALUE);
		}
	}
	
	public boolean isMatched() {
		return markerMatched;
	}

	public boolean canDecrementCount() {
		return markerCount > 0;
	}

	public boolean canIncrementCount() {
		return markerCount < this.maxCount;
	}

	public void decrementCount() {
		if (!this.canDecrementCount()) {
			throw new RuntimeException("Cannot decrement count in matcher: "+markerCount);
		}
		markerCount--;
	}

	public void incrementCount() {
		if (!this.canIncrementCount()) {
			throw new RuntimeException("Cannot increment count in matcher: "+markerCount);
		}
		markerCount++;
	}

	public void setCount(int count) {
		this.markerCount = count;
	}

	public int getCount() {
		return markerCount;
	}

	boolean isWithinCount() {
		return isWithinCount(markerCount);
	}

	boolean isWithinCount(int count) {
		return (count >= minCount && count <= maxCount);
	}

	public void setSkip(boolean skip) {
		this.markerSkip = skip;
	}

	public boolean isSkip() {
		return markerSkip;
	}

	public void setMatched(boolean matched) {
		this.markerMatched = matched;
	}

	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
	}

	public void setMinCount(int minCount) {
		this.minCount = minCount;
	}

	public int getMaxCount() {
		return maxCount;
	}

	public int getMinCount() {
		return minCount;
	}

	public int getModuleEnd() {
		return moduleEnd;
	}

	public int getModuleStart() {
		return moduleStart;
	}

	public void setModuleEnd(int moduleEnd) {
		LOG.debug("moduleEnd: "+getName()+" "+moduleEnd);
		this.moduleEnd = moduleEnd;
	}

	public void setModuleStart(int moduleStart) {
		LOG.debug("moduleStart: "+getName()+" "+moduleStart);
		this.moduleStart = moduleStart;
	}

	public String getString() {
		String s = "";
	       s += "minCount "+minCount+"\n";
	       s += "maxCount "+maxCount+"\n";
	       s += "skip "+markerSkip+"\n";
	    return s;
	}

	protected int forceIncrementCount() {
		return markerCount++;
	}
	
	protected String getName() {
		return ParserUtil.getLastDotField(""+this.getClass());
	}

	protected abstract void matchGreedilyAndPointToNextUnmatchedLegacyElement(			
			LegacyStore legacyStore);

	public String toString() {
		String sp = marker.getIndentForFormatting();
		String s = sp+"COUNTER "+ParserUtil.getLastDotField(""+this.getClass())+" "+marker.getId()+"   ";
		s += "owner "+marker.hashCode()+" markMatch: "+markerMatched+"   ";
		s += "count: "+markerCount+"   ";
		s += "min: "+minCount+"   ";
		s += "max: "+maxCount+"   ";
		s += "skip: "+markerSkip+"   ";
		s += "modStart: "+moduleStart+"   ";
		s += "modEnd: "+moduleEnd;
		return s;
	}



}
