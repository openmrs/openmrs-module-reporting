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
package org.openmrs.module.test;

import java.io.FileOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

/**
 * This class can be run like a junit test, but it is not actually a test. JUnit won't run it
 * because it does not have "Test" in its class name. The
 * {@link BaseContextSensitiveTest#INITIAL_DATA_SET_XML_FILENAME} file is overwritten by values in
 * the database defined by the runtime properties
 */
public class CreateInitialDataSet extends BaseModuleContextSensitiveTest {
	
	
	private static Log log = LogFactory.getLog(CreateInitialDataSet.class);

	
	/**
	 * This test creates an xml dbunit file from the current database connection information found
	 * in the runtime properties. This method has to "skip over the base setup" because it tries to
	 * do things (like initialize the database) that shouldn't be done to a standard mysql database.
	 * 
	 * @throws Exception
	 */
	@Test
	@SkipBaseSetup
	public void shouldCreateInitialTestDataSetXmlFile() throws Exception {
		
		
		log.info("Start");
		
		// only run this test if it is being run alone.
		// this allows the junit-report ant target and the "right-
		// click-on-/test/api-->run as-->junit test" methods to skip
		// over this whole "test"
		//if (getLoadCount() != 1)
		//	return;
		
		// database connection for dbunit
		IDatabaseConnection connection = new DatabaseConnection(getConnection());
	
		// partial database export
		QueryDataSet initialDataSet = new QueryDataSet(connection);
	
		final String PATIENTS = "20447, 20807, 14943";
		final String CONCEPTS = "21, 300, 657, 678, 679, 729, 730, 825, 851, 1015, 1016, 1017, 1018, 1019, 1021, 3646, 3059, 3060, 5497";
		final String ENCOUNTERS = "20447, 20807, 14943";
		final String ORDER_TYPES = "4";
		final String USERS = "8, 16461, 18804";
				
		// Static data
		initialDataSet.addTable("concept", "SELECT * FROM concept where concept_id in (" + CONCEPTS + ");");
		initialDataSet.addTable("concept_answer", "SELECT * FROM concept_answer where concept_id in (" + CONCEPTS + ");");
		initialDataSet.addTable("concept_class", "SELECT * FROM concept_class");
		initialDataSet.addTable("concept_datatype", "SELECT * FROM concept_datatype");
		initialDataSet.addTable("concept_name", "SELECT * FROM concept_name where concept_id in (" + CONCEPTS + ");");
		initialDataSet.addTable("concept_numeric", "SELECT * FROM concept_numeric where concept_id in (" + CONCEPTS + ");");
		initialDataSet.addTable("concept_set", "SELECT * FROM concept_set where concept_set in (" + CONCEPTS + ");");
		initialDataSet.addTable("concept_synonym", "SELECT * FROM concept_synonym where concept_id in (" + CONCEPTS + ");");
		initialDataSet.addTable("drug", "SELECT * FROM drug");
		//initialDataSet.addTable("drug_order", "SELECT * FROM drug_order");
		//initialDataSet.addTable("encounter", "SELECT * FROM encounter");
		//initialDataSet.addTable("encounter_type", "SELECT * FROM encounter_type");
		initialDataSet.addTable("location", "SELECT * FROM location");
		//initialDataSet.addTable("obs", "SELECT * FROM obs");
		initialDataSet.addTable("order_type", "SELECT * FROM order_type");
		//initialDataSet.addTable("orders", "SELECT * FROM orders");
		//initialDataSet.addTable("patient", "SELECT * FROM patient");
		//initialDataSet.addTable("patient_identifier", "SELECT * FROM patient_identifier");
		initialDataSet.addTable("patient_identifier_type", "SELECT * FROM patient_identifier_type");
		//initialDataSet.addTable("patient_program", "SELECT * FROM patient_program");
		//initialDataSet.addTable("patient_state", "SELECT * FROM patient_state");
		//initialDataSet.addTable("person", "SELECT * FROM person");
		//initialDataSet.addTable("person_address", "SELECT * FROM person_address");
		//initialDataSet.addTable("person_attribute", "SELECT * FROM person_attribute");
		initialDataSet.addTable("person_attribute_type", "SELECT * FROM person_attribute_type");
		//initialDataSet.addTable("person_name", "SELECT * FROM person_name");
		initialDataSet.addTable("privilege", "SELECT * FROM privilege");
		initialDataSet.addTable("program", "SELECT * FROM program");
		initialDataSet.addTable("program_workflow", "SELECT * FROM program_workflow");
		initialDataSet.addTable("program_workflow_state", "SELECT * FROM program_workflow_state");
		//initialDataSet.addTable("relationship", "SELECT * FROM relationship");
		initialDataSet.addTable("relationship_type", "SELECT * FROM relationship_type");
		initialDataSet.addTable("role", "SELECT * FROM role");
		initialDataSet.addTable("role_privilege", "SELECT * FROM role_privilege");
		initialDataSet.addTable("role_role", "SELECT * FROM role_role");
		initialDataSet.addTable("user_role", "SELECT * FROM user_role");
		//initialDataSet.addTable("users", "SELECT * FROM users");
		
		// Actual data
		initialDataSet.addTable("orders", "select * FROM orders where order_type_id in (" + ORDER_TYPES + ");");
		initialDataSet.addTable("encounter", "select * FROM encounter where encounter_id in (" + ENCOUNTERS + ");");		
		initialDataSet.addTable("patient", "select * FROM patient where patient_id in (" + PATIENTS + ");");
		initialDataSet.addTable("patient_identifier", "select * FROM patient where patient_id in (" + PATIENTS + ");");
		initialDataSet.addTable("person", "select * FROM person where person_id in (" + PATIENTS + ");");
		initialDataSet.addTable("person_name", "select * FROM person_name where person_id in (" + PATIENTS + ");");
		initialDataSet.addTable("person_address", "select * FROM person_address where person_id in (" + PATIENTS + ");");
		initialDataSet.addTable("person_attribute", "select * FROM person_attribute where person_id in (" + PATIENTS + ");");
		initialDataSet.addTable("patient_program", "SELECT * FROM patient_program where patient_id in (" + PATIENTS + ");");
		initialDataSet.addTable("patient_state", "SELECT patient_state.* FROM patient_state, patient_program where patient_state.patient_program_id = patient_program.patient_program_id and patient_program.patient_id in (" + PATIENTS + ");");
		initialDataSet.addTable("obs", "select * FROM obs where encounter_id in (" + ENCOUNTERS + ");");
		initialDataSet.addTable("users", "SELECT * FROM users where user_id in (" + USERS + ");");
		

		FlatXmlDataSet.write(initialDataSet, new FileOutputStream("test/api/org/openmrs/module/report/include/LabOrderDataSetTest.xml"));
		
		// full database export
		//IDataSet fullDataSet = connection.createDataSet();
		//FlatXmlDataSet.write(fullDataSet, new FileOutputStream("full.xml"));
		
		// dependent tables database export: export table X and all tables that
		// have a PK which is a FK on X, in the right order for insertion
		//String[] depTableNames = TablesDependencyHelper.getAllDependentTables(connection, "X");
		//IDataSet depDataset = connection.createDataSet( depTableNames );
		//FlatXmlDataSet.write(depDataSet, new FileOutputStream("dependents.xml")); 
		
		//TestUtil.printOutTableContents(getConnection(), "encounter_type", "encounter");
	
		log.info("Done");

	}

	/**
	 * Make sure we use the database defined by the runtime properties and not the hsql in-memory
	 * database
	 * 
	 * @see org.openmrs.test.BaseContextSensitiveTest#useInMemoryDatabase()
	 */
	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}
}
