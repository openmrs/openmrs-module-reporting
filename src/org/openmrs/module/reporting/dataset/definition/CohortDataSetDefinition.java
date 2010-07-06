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

import org.openmrs.Cohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.MapDataSet;
import org.openmrs.module.reporting.dataset.definition.evaluator.CohortDataSetEvaluator;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;

/**
 * Metadata that defines a MapDataSet of Cohorts. (i.e. a table of cohorts, each of which 
 * is the cross product between a "row" Cohort and a "column" Cohort).
 * @see MapDataSet
 * @see CohortDataSetEvaluator
 */
public class CohortDataSetDefinition extends BaseDataSetDefinition {
	
	private static final long serialVersionUID = -658417752199413012L;

	private List<Mapped<? extends CohortDefinition>> rows;
	private List<Mapped<? extends CohortDefinition>> columns;
	
	/**
	 * Default constructor
	 */
	public CohortDataSetDefinition() { }
	
	/**
	 * @return DataSetColumns constructed from rows and column definitions
	 */
	public List<CohortDataSetColumn> getDataSetColumns() {
		List<CohortDataSetColumn> c = new ArrayList<CohortDataSetColumn>();
		if (getRows().isEmpty()) {
			int colNum = 1;
			for (Mapped<? extends CohortDefinition> def : getColumns()) {
				c.add(new CohortDataSetColumn(""+colNum, def.getParameterizable().getName(), Cohort.class, null, def));
				colNum++;
			}
		}
		if (getColumns().isEmpty()) {
			int rowNum = 1;
			for (Mapped<? extends CohortDefinition> def : getRows()) {
				c.add(new CohortDataSetColumn(""+rowNum, def.getParameterizable().getName(), Cohort.class, def, null));
				rowNum++;
			}
		}
		if (!getRows().isEmpty() && !getColumns().isEmpty()) {
			int rowNum = 1;
			for (Mapped<? extends CohortDefinition> rowDef : getRows()) {
				int colNum = 1;
				for (Mapped<? extends CohortDefinition> colDef : getColumns()) {
					String key = rowNum + "." + colNum;
					String label = rowDef.getParameterizable().getName() + " - " + colDef.getParameterizable().getName();
					c.add(new CohortDataSetColumn(key, label, Cohort.class, rowDef, colDef));
					colNum++;
				}
				rowNum++;
			}
		}
		return c;
	}

	/**
	 * @return the rows
	 */
	public List<Mapped<? extends CohortDefinition>> getRows() {
		if (rows == null) {
			rows = new ArrayList<Mapped<? extends CohortDefinition>>();
		}
		return rows;
	}
	
	/**
	 * @param rows the rows to set
	 */
	public void setRows(List<Mapped<? extends CohortDefinition>> rows) {
		this.rows = rows;
	}
	
	/**
	 * @param row to add
	 */
	public void addRow(Mapped<? extends CohortDefinition> row) {
		getRows().add(row);
	}

	/**
	 * @return the columns
	 */
	public List<Mapped<? extends CohortDefinition>> getColumns() {
		if (columns == null) {
			columns = new ArrayList<Mapped<? extends CohortDefinition>>();
		}
		return columns;
	}

	/**
	 * @param columns the columns to set
	 */
	public void setColumns(List<Mapped<? extends CohortDefinition>> columns) {
		this.columns = columns;
	}
	
	/**
	 * @param column to add
	 */
	public void addColumn(Mapped<? extends CohortDefinition> column) {
		getColumns().add(column);
	}
	
	/**
	 * Inner class representing a single Column in the DataSet
	 */
	public class CohortDataSetColumn extends DataSetColumn {
		
		private static final long serialVersionUID = 1L;
		
		//***** PROPERTIES *****
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
		public CohortDataSetColumn(String name, String label, Class<?> dataType, 
								   Mapped<? extends CohortDefinition> rowDefinition, 
								   Mapped<? extends CohortDefinition> columnDefinition) {
			super(name, label, dataType);
			this.rowDefinition = rowDefinition;
			this.columnDefinition = columnDefinition;
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
