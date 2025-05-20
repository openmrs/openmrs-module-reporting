/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
	TEXT("text", "text/plain"),
	ZIP("zip", "application/zip");
	
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