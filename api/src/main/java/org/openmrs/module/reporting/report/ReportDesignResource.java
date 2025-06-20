/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.report;

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
	
	//***** INSTANCE METHODS *****
	
	/** @see Object#equals(Object) */
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof ReportDesignResource) {
			ReportDesignResource p = (ReportDesignResource) obj;
			if (this.getUuid() != null) {
				return (this.getUuid().equals(p.getUuid()));
			}
		}
		return this == obj;
	}
	
	/**
	 * Returns a file name for the resource
	 */
	public String getResourceFilename() {
		return getName() + (getExtension() == null ? "" : "." + getExtension());
	}
	
	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return (getUuid() == null ? 0 : 31 * getUuid().hashCode());
	}
	
	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return getResourceFilename();
	}
	
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
