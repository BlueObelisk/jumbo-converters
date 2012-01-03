package org.xmlcml.cml.converters.reaction;

import java.util.Collections;


import org.xmlcml.cml.converters.registry.ConverterInfo;
import org.xmlcml.cml.converters.registry.ConverterListImpl;

/**
 * @author Sam Adams
 */
public class ReactionConverters extends ConverterListImpl {

    public ReactionConverters() {
//        list.add(new ConverterInfo(ReactionConverter.class));
        this.list = Collections.unmodifiableList(list);
    }

}
