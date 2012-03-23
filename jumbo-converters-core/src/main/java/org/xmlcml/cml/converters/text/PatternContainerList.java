package org.xmlcml.cml.converters.text;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PatternContainerList {
    
    private List<PatternContainer> patternContainerList = new ArrayList<PatternContainer>();
    private Integer currentIndex = null;
    
    public PatternContainerList() {};
    
    public PatternContainerList( PatternContainer patternContainer ) {
        patternContainerList.add(patternContainer);
        currentIndex=0;
    }
    
    public boolean add( PatternContainer patternContainer ) {
        if (patternContainer == null) return false;
        if (patternContainerList.size() == 0) currentIndex=0;
        return patternContainerList.add(patternContainer);
    }

    public boolean isOnlyPattern( String pattern ) {
        if (size() != 1) return false;
        return containsPattern( pattern );
    }
    
    public boolean containsPattern( String pattern ) {
        for (PatternContainer patternContainer : patternContainerList ) {
            List<Pattern> patterns = patternContainer.getPatternList();
            if ( patterns.size() == 1 && pattern.equals(patterns.get(0).toString()) )
                return true;
        }
        return false;
    }
    
    public PatternContainer get(Integer index) {
        if ( index == null ) throw new RuntimeException("PatterContainerList get - bad index");
        currentIndex=index;
        return patternContainerList.get(index);
    }
    
    public int size() {
        return patternContainerList.size();
    }
    
    public int getCurrentOffset() {
        if (currentIndex == null || currentIndex > patternContainerList.size())
            throw new RuntimeException("PatterContainerList - bad currentIndex");
        return patternContainerList.get(currentIndex).getOffset();
    }
    
    public PatternContainer getCurrentPatternContainer() {
        if (currentIndex == null || currentIndex > patternContainerList.size())
            throw new RuntimeException("PatterContainerList - bad currentIndex");
        return patternContainerList.get(currentIndex);
    }

}
