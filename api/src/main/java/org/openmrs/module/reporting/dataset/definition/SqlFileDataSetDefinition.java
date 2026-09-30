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

import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

/**
 * Evaluates a sql script found at the configured file path or resource path or with the sql contents specified
 * By default, this will utilize the database connection properties in the openmrs runtime properties
 * This can use an alternative database connection by specifying the file.  Property names follow those in openmrs runtime properties
 */
public class SqlFileDataSetDefinition extends BaseDataSetDefinition {

    public enum MetadataParameterConversion {
        ID, UUID, NAME
    }

	@ConfigurationProperty
	private String sqlFile;

	@ConfigurationProperty
    private String sqlResource;

	@ConfigurationProperty
    private String sql;

	@ConfigurationProperty
    private String connectionPropertyFile;

	@ConfigurationProperty
	private MetadataParameterConversion metadataParameterConversion = MetadataParameterConversion.ID;

	/**
	 * Constructor
	 */
	public SqlFileDataSetDefinition() {}

    public String getSqlFile() {
        return sqlFile;
    }

    public void setSqlFile(String sqlFile) {
        this.sqlFile = sqlFile;
    }

    public String getSqlResource() {
        return sqlResource;
    }

    public void setSqlResource(String sqlResource) {
        this.sqlResource = sqlResource;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getConnectionPropertyFile() {
        return connectionPropertyFile;
    }

    public void setConnectionPropertyFile(String connectionPropertyFile) {
        this.connectionPropertyFile = connectionPropertyFile;
    }

    public MetadataParameterConversion getMetadataParameterConversion() {
        return metadataParameterConversion;
    }

    public void setMetadataParameterConversion(MetadataParameterConversion metadataParameterConversion) {
        this.metadataParameterConversion = metadataParameterConversion;
    }
}
