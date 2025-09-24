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

/**
 * Evaluates a sql script found at the configured file path or resource path or with the sql contents specified
 * By default, this will utilize the database connection properties in the openmrs runtime properties
 * This can use an alternative database connection by specifying the file.  Property names follow those in openmrs runtime properties
 */
public class IterableSqlDataSetDefinition extends SqlFileDataSetDefinition {
}
