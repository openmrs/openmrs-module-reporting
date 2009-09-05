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
package org.openmrs.module.report;

import org.openmrs.BaseOpenmrsMetadata;

/**
 * Represents a resource which is used in the rendering of a report.
 * Such a resource could be an xml design file, and image, a File template, or
 * any particular resource that a renderer requires.
 */
public class ReportDesignResource extends BaseOpenmrsMetadata {
	
	//***** PROPERTIES *****
	
	private Integer id;
	private ReportDesign reportDesign;
	private String contentType;
	private String extension;
	private byte[] contents;

	//***** CONSTRUCTORS *****
	
	/**
	 * Default Constructor
	 */
	public ReportDesignResource() {}
	
	//***** PROPERTY ACCESS *****
	
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the reportDesign
	 */
	public ReportDesign getReportDesign() {
		return reportDesign;
	}

	/**
	 * @param reportDesign the reportDesign to set
	 */
	public void setReportDesign(ReportDesign reportDesign) {
		this.reportDesign = reportDesign;
	}

	/**
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * @param contentType the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * @return the extension
	 */
	public String getExtension() {
		return extension;
	}

	/**
	 * @param extension the extension to set
	 */
	public void setExtension(String extension) {
		this.extension = extension;
	}

	/**
	 * @return the contents
	 */
	public byte[] getContents() {
		return contents;
	}

	/**
	 * @param contents the contents to set
	 */
	public void setContents(byte[] contents) {
		this.contents = contents;
	}
}
