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
package org.openmrs.module.dataset.definition;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.ProgramWorkflow;
import org.openmrs.api.context.Context;
import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.dataset.column.LogicDataSetColumn;
import org.openmrs.module.dataset.column.SimpleDataSetColumn;
import org.openmrs.module.dataset.definition.evaluator.PatientDataSetEvaluator;
import org.openmrs.util.DatabaseUpdater;

/**
 * Definition of a dataset that produces one-row-per-patient table. 
 * @see PatientDataSetEvaluator
 */
public class SqlDataSetDefinition extends BaseDataSetDefinition {

	// Serial version UID
	private static final long serialVersionUID = 6405583324151111487L;

	private String sqlQuery;
	
	/**
	 * Constructor
	 */
	public SqlDataSetDefinition() {
		super();
	}
	
	/**
	 * Public constructor
	 * 
	 * @param name
	 * @param description
	 * @param questions
	 */
	public SqlDataSetDefinition(String name, String description, String sqlQuery) {
		this();
		this.setName(name);
		this.setDescription(description);
		this.setSqlQuery(sqlQuery);
	}

	public String getSqlQuery() {
		return sqlQuery;
	}

	public void setSqlQuery(String sqlQuery) {
		this.sqlQuery = sqlQuery;
	}

	public List<DataSetColumn> getColumns()  {

		List<DataSetColumn> columns = new LinkedList<DataSetColumn>();
		Connection connection = null;
		try { 
		
			connection = DatabaseUpdater.getConnection();
			ResultSet resultSet = null;
			Statement statement = connection.createStatement();
			boolean result = statement.execute(sqlQuery);
			if (result) { 
				resultSet = statement.getResultSet();
			}
			
			ResultSetMetaData rsmd = resultSet.getMetaData();
			for (int i=1; i<=rsmd.getColumnCount();i++) {
				SimpleDataSetColumn column = new SimpleDataSetColumn();
				column.setColumnKey(rsmd.getColumnName(i));
				column.setDataType(Context.loadClass(rsmd.getColumnClassName(i)));
				column.setDisplayName(rsmd.getColumnLabel(i));
				column.setDescription(rsmd.toString());				
				columns.add(column);				
			}			
		} catch (Exception e) { 
			log.error("Error while getting connection ", e);			
		} finally { 
			try { 
				if (connection != null) 
					connection.close();
			} catch (Exception e) { log.error("Error while closing connection", e); } 			
		}
		return columns;
	}
	

	
}
