/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
