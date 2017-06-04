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
package org.openmrs.module.reporting.cohort.definition;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

public class StartedDrugOrderCohortDefinition extends BaseCohortDefinition {

  public static final long serialVersionUID = 1L;

	//***** PROPERTIES *****

  @ConfigurationProperty(value="groupMethod")
  private String groupMethod = "ANY";

  @ConfigurationProperty(value="genericDrugList")
  private List<Concept> genericDrugList;

  @ConfigurationProperty(value="drugList")
  private List<Drug> drugList;

  @ConfigurationProperty(value="stoppedOnOrBefore")
  private Date stoppedOnOrBefore;

  @ConfigurationProperty(value="stoppedOnOrAfter")
  private Date stoppedOnOrAfter;

  @ConfigurationProperty(value="activeOnOrBefore")
  private Date activeOnOrBefore;

  @ConfigurationProperty(value="activeOnOrAfter")
  private Date activeOnOrAfter;

  /**
   * Default constructor
   */

  public StartedDrugOrderCohortDefinition(){}

  /**
   * @see java.lang.Object#toString()
   */
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Patients ");
    if (this.genericDrugList != null && this.genericDrugList.size() > 0) {
      builder.append("taking generic drugs ");
      for (Concept c : this.genericDrugList) {
        builder.append(c.getName() + " ");
      }
    }
    if (this.drugList != null && this.drugList.size() > 0) {
      builder.append("taking drugs ");
      for (Drug d : this.drugList) {
        builder.append(d.getName() + " ");
      }
    }
    if (this.groupMethod != null) {
      builder.append(" with group method" + this.groupMethod + " ");
    }
    if (this.activeOnOrBefore != null) {
      builder.append("activeon or before " + this.activeOnOrBefore + " ");
    }
    if (this.activeOnOrAfter != null) {
      builder.append("active on or after " + this.activeOnOrAfter + " ");
    }
    if (this.stoppedOnOrBefore != null) {
      builder.append("stopped on or before " + this.stoppedOnOrBefore + " ");
    }
    if (this.stoppedOnOrAfter != null) {
      builder.append("stopped on or after " + this.stoppedOnOrAfter + " ");
    }
    return builder.toString();
  }

  /**
   * @return group method
   */
  public String getGroupMethod() {
    return this.groupMethod;
  }

  /**
   * @param gm set the group method
   */
  public void setGroupMethod(String gm) {
    this.groupMethod = gm;
  }

  /**
   * @return the drug generic drug list
   */
  public List<Concept> getGenericDrugList() {
    return this.genericDrugList;
  }

  /**
   * @param list the generic drug list to set
   */
  public void setGenericDrugList(List<Concept> list) {
    this.genericDrugList = list;
  }

  /**
   * @param gd the generic drug to add
   */
  public void addGenericDrug(Concept gd) {
    if (this.genericDrugList == null) {
      this.genericDrugList = new ArrayList<Concept>();
    }
    this.genericDrugList.add(gd);
  }



  /**
   * @return the drug list
   */
  public List<Drug> getDrugList() {
    return this.drugList;
  }

  /**
   * @param list the drug list
   */
  public void setDrugList(List<Drug> list) {
    this.drugList = list;
  }

  /**
   * @param drug the drug to add to the drug list
   */
  public void addDrug(Drug drug) {
    if (this.drugList == null) {
      this.drugList = new ArrayList<Drug>();
    }
    this.drugList.add(drug);
  }
  
  /**
   * @return the activeOnOrBefore Date
   */
  public Date getActiveOnOrBefore() {
    return this.activeOnOrBefore;
  }

  /**
   * @param date set the activatedOnOrBefore Date
   */
  public void setActiveOnOrBefore(Date date) {
    this.activeOnOrBefore = date;
  }

  /**
   * @return the activeOnOrAfter Date
   */
  public Date getActiveOnOrAfter() {
    return this.activeOnOrAfter;
  }

  /**
   * @param date set the activatedOnOrAfter Date
   */
  public void setActiveOnOrAfter(Date date) {
    this.activeOnOrAfter = date;
  }

  /**
   * @param date set the stoppedOnOrAfter Date
   */
  public void setStoppedOnOrAfter(Date date) {
    this.stoppedOnOrAfter = date;
  }

  /**
   * @return the stoppedOnOrBefore Date
   */
  public Date getStoppedOnOrBefore() {
    return this.stoppedOnOrBefore;
  }

  /**
   * @param date set the stoppedOnOrBefore Date
   */
  public void setStoppedOnOrBefore(Date date) {
    this.stoppedOnOrBefore = date;
  }

  /**
   * @return the stoppedOnOrAfter Date
   */
  public Date getStoppedOnOrAfter() {
    return this.stoppedOnOrAfter;
  }

}