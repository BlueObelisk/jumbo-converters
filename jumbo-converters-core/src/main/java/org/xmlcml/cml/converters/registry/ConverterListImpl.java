package org.xmlcml.cml.converters.registry;

import java.util.ArrayList;
import java.util.List;


public class ConverterListImpl implements ConverterList {
	protected List<ConverterInfo> list = null;

   public ConverterListImpl() {
	   list = new ArrayList<ConverterInfo>();
   }

	public List<ConverterInfo> listConverters() {
		return list;
	}

}
