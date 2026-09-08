/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting;

/*
 * This ReportingException is thrown when any SQL queries tries to  modify the Database.   
 */

public class IllegalDatabaseAccessException extends ReportingException {

	public IllegalDatabaseAccessException(String message) {
		super(message);
	}
	
	public IllegalDatabaseAccessException() {
		super("Insert/Update/Delete/Drop/Alter/Create  queries  are not allowed");
	}
	public static final long serialVersionUID = 1L;

	

}
