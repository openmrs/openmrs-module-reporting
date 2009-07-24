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
package org.openmrs.module.dataset.definition.evaluator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.module.dataset.DataExportDataSet;
import org.openmrs.module.dataset.DataSet;
import org.openmrs.module.dataset.definition.DataExportDataSetDefinition;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.reporting.export.DataExportReportObject;
import org.openmrs.reporting.export.DataExportUtil;
import org.openmrs.util.OpenmrsUtil;

/**
 * The logic that evaluates a {@link DataExportDataSetDefinition} and produces a {@link DataSet}
 * 
 * @see DataExportDataSetDefinition
 * @see DataSet
 */
@Handler(supports={DataExportDataSetDefinition.class})
public class DataExportDataSetEvaluator implements DataSetEvaluator {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * Public constructor
	 */
	public DataExportDataSetEvaluator() {} 
	
    /**
     * @see DataSetEvaluator#evaluate(DataSetDefinition, EvaluationContext)
     */
	public DataSet<?> evaluate(DataSetDefinition definition, EvaluationContext context) {
		
		DataExportDataSetDefinition dataExportDefinition = (DataExportDataSetDefinition) definition;

		DataExportReportObject dataExport = dataExportDefinition.getDataExportReportObject();
		
		// TODO We probably need to handle the case where a cohort is specified by the data export 
		/*
		Cohort patients = context.getBaseCohort();
		
		// If the data export already has a DataSet defined, we intersect it with the 
		if (dataExportDefinition.getFilter() != null) {
			if (patients != null)
				patients = Cohort.intersect(patients, Context.getCohortService().evaluate(dataExportDefinition.getFilter(),
				    evalContext));
			else
				patients = Context.getCohortService().evaluate(dataExportDefinition.getFilter(), evalContext);
		}
		*/
		DataExportDataSet dataset = null;
		try { 
			// We write out the data export
			DataExportUtil.generateExport(dataExport, context.getBaseCohort(), null);
			
			// ... and then convert the export to a DataSet
			dataset = getDataExportDataSet(dataExportDefinition);
		} 
		catch (Exception e) { 
			// TODO Probably should throw an exception here
			//throw new DataSetException(e.getMessage());
		}

		return dataset;
	}

	
	/**
	 * Returns a list of rows read from the given data export.
	 * 
	 * @param xlsFile
	 */
	public DataExportDataSet getDataExportDataSet(DataExportDataSetDefinition dataSetDefinition) { 
		
		DataExportDataSet dataset = new DataExportDataSet(dataSetDefinition);
		
		File dataFile = DataExportUtil.getGeneratedFile(dataSetDefinition.getDataExportReportObject());			
		
		List<Map<String,Object>> dataSetRows = new ArrayList<Map<String,Object>>();
		log.error("getting data export data set for data export " + dataFile.getAbsolutePath());
		try { 
			// Get contents as a string 
			// TODO Test whether this is faster than another approach
			String contents = OpenmrsUtil.getFileAsString(dataFile);		
			String [] rows = contents.split("\\n");
	
			// Get column names 
			String [] columns = rows[0].split("\\t");
	
			// Iterate over remaining rows
			for (int i=1; i<rows.length;i++) { 
				
				Map<String,Object> dataSetRow = new HashMap<String,Object>();
				String [] cells = rows[i].split("\\t");
				for (int j=0; j<cells.length; j++) { 	
					
					log.error("column=" + columns[j] + " value=" + cells[j]);
					dataSetRow.put(columns[j], cells[j]);
				}
				
				dataSetRows.add(dataSetRow);	
			}
			dataset.setData(dataSetRows);
			
			log.info("Dataset: " + dataset);
		} 
		catch (IOException e) { 
			log.error("Exception " + e.getMessage());
			
		}
		return dataset;
	}
	
	
}
