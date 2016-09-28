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
package org.openmrs.module.reporting.dataset.definition.evaluator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataExportDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.reporting.export.DataExportReportObject;
import org.openmrs.reporting.export.DataExportUtil;
import org.openmrs.util.OpenmrsUtil;

/**
 * The logic that evaluates a {@link DataExportDataSetDefinition} and produces a {@link DataSet}
 * @see DataExportDataSetDefinition
 * @see DataSet
 */
@Handler(supports={DataExportDataSetDefinition.class})
@SuppressWarnings("deprecation")
public class DataExportDataSetEvaluator implements DataSetEvaluator {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * Public constructor
	 */
	public DataExportDataSetEvaluator() {} 
	
    /**
     * @see DataSetEvaluator#evaluate(DataSetDefinition, EvaluationContext)
     * @should evaluate a DataExportDataSetDefinition
     */
	public DataSet evaluate(DataSetDefinition definition, EvaluationContext context) {
		
		context = ObjectUtil.nvl(context, new EvaluationContext());
		SimpleDataSet dataSet = new SimpleDataSet(definition, context);
		try {
			DataExportDataSetDefinition dataExportDefinition = (DataExportDataSetDefinition) definition;
			DataExportReportObject dataExport = dataExportDefinition.getDataExport();
			DataExportUtil.generateExport(dataExport, context.getBaseCohort(), null);
			
			File dataFile = DataExportUtil.getGeneratedFile(dataExportDefinition.getDataExport());

			// Get contents as a string 
			// TODO Test whether this is faster than another approach
			String contents = OpenmrsUtil.getFileAsString(dataFile);		
			String [] rows = contents.split("\\n");
	
			// Get column names 
			String [] columns = rows[0].split("\\t");
			Map<String, DataSetColumn> cols = new HashMap<String, DataSetColumn>();
			for (String s : columns) {
				DataSetColumn c = new DataSetColumn(s, s, String.class);
				cols.put(s, c);
				dataSet.getMetaData().addColumn(c);
			}
	
			// Iterate over remaining rows
			for (int i=1; i<rows.length;i++) { 
				DataSetRow row = new DataSetRow();
				String [] cells = rows[i].split("\\t");
				for (int j=0; j<cells.length; j++) { 	
					row.addColumnValue(cols.get(columns[j]), cells[j]);
				}
				dataSet.addRow(row);	
			}
		} 
		catch (Exception e) {
			log.error("An error occurred while generating a data export.", e);
			throw new RuntimeException("An error occurred while generating a data export.", e);
		}
		return dataSet;
	}
}
