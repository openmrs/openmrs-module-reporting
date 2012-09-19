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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.Cohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.MapDataSet;
import org.openmrs.module.reporting.dataset.definition.evaluator.CohortDataSetEvaluator;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;

/**
 * Metadata that defines a MapDataSet of Cohorts. (i.e. a table of cohorts, each of which 
 * is the cross product between a "row" Cohort and a "column" Cohort).
 * @see MapDataSet
 * @see CohortDataSetEvaluator
 */
@Localized("reporting.CohortCrossTabDataSetDefinition")
public class CohortCrossTabDataSetDefinition extends BaseDataSetDefinition {
	
	public static final long serialVersionUID = -658417752199413012L;

	@ConfigurationProperty
	private Map<String, Mapped<? extends CohortDefinition>> rows;
	
	@ConfigurationProperty
	private Map<String, Mapped<? extends CohortDefinition>> columns;
	
	/**
	 * Default constructor
	 */
	public CohortCrossTabDataSetDefinition() { }
	
	/**
	 * @return DataSetColumns constructed from rows and column definitions
	 */
	public List<CohortDataSetColumn> getDataSetColumns() {
		List<CohortDataSetColumn> c = new ArrayList<CohortDataSetColumn>();
		if (getRows().isEmpty()) {
			for (String colName : getColumns().keySet()) {
				c.add(new CohortDataSetColumn(colName, Cohort.class, null, colName, null, getColumns().get(colName)));
			}
		}
		if (getColumns().isEmpty()) {
			for (String rowName : getRows().keySet()) {
				c.add(new CohortDataSetColumn(rowName, Cohort.class, rowName, null, getRows().get(rowName), null));
			}
		}
		if (!getRows().isEmpty() && !getColumns().isEmpty()) {
			for (String rowName : getRows().keySet()) {
				for (String colName : getColumns().keySet()) {
					String key = rowName + "." + colName;
					c.add(new CohortDataSetColumn(key, Cohort.class, rowName, colName, getRows().get(rowName), getColumns().get(colName)));
				}
			}
		}
		return c;
	}
	
	/**
	 * @return the data set columns by key
	 */
	public Map<String, CohortDataSetColumn> getDataSetColumnsByKey() {
		Map<String, CohortDataSetColumn> ret = new LinkedHashMap<String, CohortDataSetColumn>();
		for (CohortDataSetColumn c : getDataSetColumns()) {
			ret.put(c.getName(), c);
		}
		return ret;
	}

	/**
	 * @return the rows
	 */
	public Map<String, Mapped<? extends CohortDefinition>> getRows() {
		if (rows == null) {
			rows = new LinkedHashMap<String, Mapped<? extends CohortDefinition>>();
		}
		return rows;
	}
	
	/**
	 * @param rows the rows to set
	 */
	public void setRows(Map<String, Mapped<? extends CohortDefinition>> rows) {
		this.rows = rows;
	}
	
	/**
	 * @param row to add
	 */
	public void addRow(String rowName, Mapped<? extends CohortDefinition> row) {
		getRows().put(rowName, row);
	}
	
	/**
	 * @param row to add
	 */
	public void addRow(String rowName, CohortDefinition row, Map<String, Object> mappings) {
		getRows().put(rowName, new Mapped<CohortDefinition>(row, mappings));
	}

	/**
	 * @return the columns
	 */
	public Map<String, Mapped<? extends CohortDefinition>> getColumns() {
		if (columns == null) {
			columns = new LinkedHashMap<String, Mapped<? extends CohortDefinition>>();
		}
		return columns;
	}

	/**
	 * @param columns the columns to set
	 */
	public void setColumns(Map<String, Mapped<? extends CohortDefinition>> columns) {
		this.columns = columns;
	}
	
	/**
	 * @param column to add
	 */
	public void addColumn(String columnName, Mapped<? extends CohortDefinition> column) {
		getColumns().put(columnName, column);
	}
	
	/**
	 * @param column to add
	 */
	public void addColumn(String columnName, CohortDefinition column, Map<String, Object> mappings) {
		getColumns().put(columnName, new Mapped<CohortDefinition>(column, mappings));
	}
	
	/**
	 * Inner class representing a single Column in the DataSet
	 */
	public class CohortDataSetColumn extends DataSetColumn {
		
		public static final long serialVersionUID = 1L;
		
		//***** PROPERTIES *****
		private String rowName;
		private String columnName;
		private Mapped<? extends CohortDefinition> rowDefinition;
		private Mapped<? extends CohortDefinition> columnDefinition;
		
		//***** CONSTRUCTORS *****
		
		/**
		 * Default Constructor
		 */
		public CohortDataSetColumn() {}
		
		/**
		 * Full Constructor
		 */
		public CohortDataSetColumn(String name, Class<?> dataType, String rowName, String columnName,
								   Mapped<? extends CohortDefinition> rowDefinition, 
								   Mapped<? extends CohortDefinition> columnDefinition) {
			super(name, name, dataType);
			String label = "";
			if (ObjectUtil.notNull(rowName)) {
				label += rowName;
			}
			if (ObjectUtil.notNull(columnName)) {
				label += (ObjectUtil.notNull(rowName) ? " - " : "") + columnName;
			}
			setLabel(label);
			this.rowName = rowName;
			this.columnName = columnName;
			this.rowDefinition = rowDefinition;
			this.columnDefinition = columnDefinition;
		}

		/**
		 * @return the rowName
		 */
		public String getRowName() {
			return rowName;
		}

		/**
		 * @param rowName the rowName to set
		 */
		public void setRowName(String rowName) {
			this.rowName = rowName;
		}

		/**
		 * @return the columnName
		 */
		public String getColumnName() {
			return columnName;
		}

		/**
		 * @param columnName the columnName to set
		 */
		public void setColumnName(String columnName) {
			this.columnName = columnName;
		}

		/**
		 * @return the rowDefinition
		 */
		public Mapped<? extends CohortDefinition> getRowDefinition() {
			return rowDefinition;
		}

		/**
		 * @param rowDefinition the rowDefinition to set
		 */
		public void setRowDefinition(Mapped<? extends CohortDefinition> rowDefinition) {
			this.rowDefinition = rowDefinition;
		}

		/**
		 * @return the columnDefinition
		 */
		public Mapped<? extends CohortDefinition> getColumnDefinition() {
			return columnDefinition;
		}

		/**
		 * @param columnDefinition the columnDefinition to set
		 */
		public void setColumnDefinition(Mapped<? extends CohortDefinition> columnDefinition) {
			this.columnDefinition = columnDefinition;
		}
	}
}
