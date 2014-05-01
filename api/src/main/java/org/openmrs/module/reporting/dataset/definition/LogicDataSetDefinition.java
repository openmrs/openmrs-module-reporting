/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.reporting.dataset.definition;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.logic.result.Result;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetMetaData;
import org.openmrs.module.reporting.dataset.SimpleDataSetMetaData;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyAndParameterCachingStrategy;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * A data set definition where each column is a logic expression
 */
@Deprecated
@Caching(strategy=ConfigurationPropertyAndParameterCachingStrategy.class)
@Localized("reporting.LogicDataSetDefinition")
public class LogicDataSetDefinition extends BaseDataSetDefinition implements PageableDataSetDefinition {
	
	public static final long serialVersionUID = 1L;
	
	@ConfigurationProperty
	private List<Column> columns = new ArrayList<Column>();
	
	public LogicDataSetDefinition() {
		super();
	}
	
	/**
	 * Deletes all columns
	 */
	public void clearColumns() {
		getColumns().clear();
	}
	
	
	/**
	 * Adds a column
	 * @param name
	 * @param label
	 * @param logic
	 */
	public void addColumn(String name, String label, String logic, String format) {
		getColumns().add(new Column(name, label, logic, format));
	}
	
	
    /**
     * @return the columns
     */
    public List<Column> getColumns() {
    	return columns;
    }

	
    /**
     * @param columns the columns to set
     */
    public void setColumns(List<Column> columns) {
    	this.columns = columns;
    }

	/**
     * @see PageableDataSetDefinition#getDataSetMetadata()
     */
    public DataSetMetaData getDataSetMetadata() {
		SimpleDataSetMetaData ret = new SimpleDataSetMetaData();
		for (Column col : getColumns()) {
			ret.addColumn(col);
		}
		return ret;
    }

	
	// ----- helper class for column definitions ----------

    public class Column extends DataSetColumn {
		public static final long serialVersionUID = 1L;
		private String logic;
		private String format;
			
        public Column(String name, String label, String logic, String format) {
	        super(name, label, Result.class);
	        this.logic = logic;
	        this.format = format;
        }
		
        /**
         * @return the logic
         */
        public String getLogic() {
        	return logic;
        }
		
        /**
         * @param logic the logic to set
         */
        public void setLogic(String logic) {
        	this.logic = logic;
        }

        /**
         * @return the format
         */
        public String getFormat() {
        	return format;
        }

        /**
         * @param format the format to set
         */
        public void setFormat(String format) {
        	this.format = format;
        }
        
        /**
         * @return a formatter that can be used to format cells in this column
         */
        public ColumnFormatter getFormatter() {
        	if (format != null) {
        		if ("date".equals(format)) {
	        		return new DateFormatter();
        		}
	        	else if ("boolean".equals(format)) {
	        		return new BooleanFormatter("X", "");
	        	}
	        	else if (format.startsWith("concept:")) {
	        		return new ConceptFormatter(format);
	        	}
	        	else if (format.contains(":")) {
	        		return new DecodeFormatter(format);
	        	}
        	}
       		return new ValueFormatter();
        }
        
	}
    
    // helper classes for formatting columns
    public interface ColumnFormatter {
    	public Object format(Result input);
    }
    
    public class ConceptFormatter implements ColumnFormatter {
    	private String format;
    	public ConceptFormatter(String format) {
    		this.format = format;
    	}
    	public Object format(Result input) {
    		try {
	    		if (input != null && input.toConcept() != null && format != null) {
	    			Concept c = input.toConcept();
	    			String nameTag = format.split(":")[1];
    				for (ConceptName cn : c.getNames()) {
    					if (cn.hasTag(nameTag)) {
    						return cn.getName();
    					}
    				}
	    		}
    		}
    		catch (Exception e) {
    			log.warn("Unable to format column as a Concept with format: " + format);
    		}
    	    return input;
    	}
    }
    
    public class ValueFormatter implements ColumnFormatter {
    	public Object format(Result input) {
    	    return input;
    	}
    }
    
    public class DateFormatter implements ColumnFormatter {
    	public Object format(Result input) {
    		if (input.isEmpty()) {
    			return null;
    		} else if (input.size() == 1) {
    			return input.getResultDate();
    		} else {
    			List<Date> ret = new ArrayList<Date>();
    			for (Result result : input) {
    				ret.add(result.getResultDate());
    			}
    			return ret;
    		}
    	}
    }
    
    public class BooleanFormatter implements ColumnFormatter {
    	String ifTrue;
    	String ifFalse;
    	public BooleanFormatter(String ifTrue, String ifFalse) {
    		this.ifTrue = ifTrue;
    		this.ifFalse = ifFalse;
    	}
    	public Object format(Result input) {
    		return input.toBoolean() ? ifTrue : ifFalse;
    	}
    }
    
    public class DecodeFormatter implements ColumnFormatter {
    	Map<String, String> decodings;
    	/**
    	 * @param format like "opt1:value1;opt2:value2;defaultVal"
    	 */
    	public DecodeFormatter(String format) {
    		decodings = new HashMap<String, String>();
    		for (String s : format.split(";")) {
    			String key = null;
    			String val;
    			if (s.contains(":")) {
    				String[] keyAndVal = s.split(":");
    				key = keyAndVal[0];
    				val = keyAndVal[1];
    			} else {
    				val = s;
    			}
    			decodings.put(key, val);
    		}
    	}
        public Object format(Result input) {
	        String inputString = input.toString();
	        String ret = decodings.get(inputString);
	        if (ret == null)
	        	ret = inputString;
	        return ret;
        }
    }

}
