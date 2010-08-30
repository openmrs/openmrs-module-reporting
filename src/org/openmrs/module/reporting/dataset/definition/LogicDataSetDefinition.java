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
import java.util.List;

import org.openmrs.logic.result.Result;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetMetaData;
import org.openmrs.module.reporting.dataset.SimpleDataSetMetaData;

/**
 * A data set definition where each column is a logic expression
 * TODO specify how to render each cell (value, datetime, or boolean)
 */
public class LogicDataSetDefinition extends BaseDataSetDefinition implements PageableDataSetDefinition {
	
	private static final long serialVersionUID = 1L;
	
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
	public void addColumn(String name, String label, String logic) {
		getColumns().add(new Column(name, label, logic));
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
	@Override
    public DataSetMetaData getDataSetMetadata() {
		SimpleDataSetMetaData ret = new SimpleDataSetMetaData();
		for (Column col : getColumns()) {
			ret.addColumn(col);
		}
		return ret;
    }

	
	// ----- helper class for column definitions ----------

    public class Column extends DataSetColumn {
		private static final long serialVersionUID = 1L;
		private String logic;
			
        public Column(String name, String label, String logic) {
	        super(name, label, Result.class);
	        this.logic = logic;
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
        
	}

}
