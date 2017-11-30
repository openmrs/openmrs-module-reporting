/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.cohort.definition;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.openmrs.CareSetting;
import org.openmrs.Drug;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

@Caching(strategy = ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.StartedDrugOrderCohortDefinition")
public class StartedDrugOrderCohortDefinition extends BaseCohortDefinition {
	
	public static final long serialVersionUID = 1L;
	
	@ConfigurationProperty(value = "genericDrugList")
	private List<Drug> genericDrugList;
	
	@ConfigurationProperty(value = "drugSets")
	private List<Drug> drugSet;
	
	@ConfigurationProperty(value = "startedOnOrBefore")
	private Date startedOnOrBefore;
	
	@ConfigurationProperty(value = "startedOnOrAfter")
	private Date startedOnOrAfter;
	
	@ConfigurationProperty(value = "activeOnOrBefore")
	private Date activeOnOrBefore;
	
	@ConfigurationProperty(value = "activeOnOrAfter")
	private Date activeOnOrAfter;
	
	@ConfigurationProperty(value = "careSetting")
	private CareSetting careSetting;
	
	public StartedDrugOrderCohortDefinition() {
		super();
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Patients ");
		if (this.genericDrugList != null && this.genericDrugList.size() > 0) {
			builder.append("taking generic drugs ");
			for (Drug drug : this.genericDrugList) {
				builder.append(drug.getName() + " ");
			}
		}
		if (this.drugSet != null && this.drugSet.size() > 0) {
			builder.append("taking drugs ");
			for (Drug drugsetItem : this.drugSet) {
				builder.append(drugsetItem.getName() + " ");
			}
		}
		if (this.activeOnOrBefore != null) {
			builder.append("active on or before " + this.activeOnOrBefore + " ");
		}
		if (this.activeOnOrAfter != null) {
			builder.append("active on or after " + this.activeOnOrAfter + " ");
		}
		if (this.startedOnOrBefore != null) {
			builder.append("stopped on or before " + this.startedOnOrBefore + " ");
		}
		if (this.startedOnOrAfter != null) {
			builder.append("stopped on or after " + this.startedOnOrAfter + " ");
		}
		return builder.toString();
	}
	
	public List<Drug> getGenericDrugList() {
		return this.genericDrugList;
	}
	
	public void setGenericDrugList(List<Drug> list) {
		this.genericDrugList = list;
	}
	
	public void addGenericDrug(Drug gd) {
		if (this.genericDrugList == null) {
			this.genericDrugList = new ArrayList<Drug>();
		}
		this.genericDrugList.add(gd);
	}
	
	public List<Drug> getDrugSet() {
		return this.drugSet;
	}
	
	public void setDrugSet(List<Drug> list) {
		this.drugSet = list;
	}
	
	public void addDrugSet(Drug drugSet) {
		if (this.drugSet == null) {
			this.drugSet = new ArrayList<Drug>();
		}
		this.drugSet.add(drugSet);
	}
	
	public Date getActiveOnOrBefore() {
		return this.activeOnOrBefore;
	}
	
	public void setActiveOnOrBefore(Date date) {
		this.activeOnOrBefore = date;
	}
	
	public Date getActiveOnOrAfter() {
		return this.activeOnOrAfter;
	}
	
	public void setActiveOnOrAfter(Date date) {
		this.activeOnOrAfter = date;
	}
	
	public void setStoppedOnOrAfter(Date date) {
		this.startedOnOrAfter = date;
	}
	
	public Date getStartedOnOrBefore() {
		return this.startedOnOrBefore;
	}
	
	public void setStartedOnOrBefore(Date date) {
		this.startedOnOrBefore = date;
	}
	
	public void setStartedOnOrAfter(Date date) {
		this.startedOnOrAfter = date;
	}
	
	public Date getStartedOnOrAfter() {
		return this.startedOnOrAfter;
	}

	
	public CareSetting getCareSetting() {
		return careSetting;
	}

	
	public void setCareSetting(CareSetting careSetting) {
		this.careSetting = careSetting;
	}
	
}
