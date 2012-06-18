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
package org.openmrs.module.reporting.common;

/**
 * ContentType constants
 */
public enum ContentType {
	
	EXCEL("xls", "application/vnd.ms-excel"),
	HTML("html", "text/html"),
	XML("xml", "text/xml"),
	CSV("csv", "text/csv"),
	TEXT("text", "text/plain");
	
	//***** PROPERTIES *****
	
	private final String extension;
	private final String contentType;
	
	//***** CONSTRUCTOR *****
	
	ContentType(String extension, String contentType) {
		this.extension = extension;
		this.contentType = contentType;
		
	}
	
	//***** PROPERTY ACCESS *****

	/**
	 * @return the extension
	 */
	public String getExtension() {
		return extension;
	}

	/**
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}
}