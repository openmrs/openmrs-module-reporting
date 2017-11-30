/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.reporting.cohort.definition;

import org.openmrs.Concept;
import org.openmrs.CareSetting;

import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

import java.util.Date;
import java.util.List;

@Caching(strategy = ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.EncounterCohortDefinition")
public class DrugOrderCohortDefinition extends BaseCohortDefinition {

    public static final long serialVersionUID = 1L;

    @ConfigurationProperty(group = "which")
    private String which;

    @ConfigurationProperty(value = "drugConcepts")
    private List<Concept> drugConcepts;

    @ConfigurationProperty(value = "drugSets")
    private List<Concept> drugSets;

    @ConfigurationProperty(value = "activatedOnOrBefore")
    private Date activatedOnOrBefore;

    @ConfigurationProperty(value = "activatedOnOrAfter")
    private Date activatedOnOrAfter;

    @ConfigurationProperty(value = "activeWithinLastMonths")
    private Integer activeWithinLastMonths;

    @ConfigurationProperty(value = "activeWithinLastDays")
    private Integer activeWithinLastDays;

    @ConfigurationProperty(value = "onlyCurrentlyActive")
    private Boolean onlyCurrentlyActive;

    @ConfigurationProperty(value = "careSetting")
    private CareSetting careSetting;

    public DrugOrderCohortDefinition() {
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Patients ");

        if (this.which != null) {
            builder.append(" taking " + this.which + " of the drugs ");
        }

        if (this.getDrugConcepts() != null && this.getDrugConcepts().size() > 0) {
            builder.append("taking generic drugs, or drugs with ingredients ");
            for (Concept concept : this.getDrugConcepts()) {
                builder.append(concept.getDisplayString() + " ");
            }
        }
        if (this.getDrugSets() != null && this.getDrugSets().size() > 0) {
            for (Concept concept : this.getDrugSets()) {
                builder.append(concept.getDisplayString() + " ");
            }
        }

        if (this.getActivatedOnOrBefore() != null) {
            builder.append("activated on or before " + this.getActivatedOnOrBefore() + " ");
        }
        if (this.getActivatedOnOrAfter() != null) {
            builder.append("activated on or after " + this.getActivatedOnOrAfter() + " ");
        }

        if (this.getActiveWithinLastMonths() != null) {
            builder.append("been active within the last " + this.getActiveWithinLastMonths() + " months ");
        }
        if (this.getActiveWithinLastDays() != null) {
            builder.append("been active within the last " + this.getActiveWithinLastDays() + " days ");
        }
        if (this.getOnlyCurrentlyActive() != null) {
            if (this.getOnlyCurrentlyActive() == true) {
                builder.append("currently active ");
            } else {
                builder.append("not currently active ");
            }
        }
        if (this.careSetting != null) {
            builder.append("with care setting of " + this.careSetting + " ");
        }
        return builder.toString();
    }

    public String getWhich() {
        return this.which;
    }

    public void setWhich(String which) {
        this.which = which;
    }

    public List<Concept> getDrugConcepts() {
        return drugConcepts;
    }

    public void setDrugConcepts(List<Concept> drugConcepts) {
        this.drugConcepts = drugConcepts;
    }

    public List<Concept> getDrugSets() {
        return drugSets;
    }

    public void setDrugSets(List<Concept> drugSets) {
        this.drugSets = drugSets;
    }

    public Date getActivatedOnOrBefore() {
        return activatedOnOrBefore;
    }

    public void setActivatedOnOrBefore(Date activatedOnOrBefore) {
        this.activatedOnOrBefore = activatedOnOrBefore;
    }

    public Date getActivatedOnOrAfter() {
        return activatedOnOrAfter;
    }

    public void setActivatedOnOrAfter(Date activatedOnOrAfter) {
        this.activatedOnOrAfter = activatedOnOrAfter;
    }

    public Integer getActiveWithinLastMonths() {
        return activeWithinLastMonths;
    }

    public void setActiveWithinLastMonths(Integer activeWithinLastMonths) {
        this.activeWithinLastMonths = activeWithinLastMonths;
    }

    public Integer getActiveWithinLastDays() {
        return activeWithinLastDays;
    }

    public void setActiveWithinLastDays(Integer activeWithinLastDays) {
        this.activeWithinLastDays = activeWithinLastDays;
    }

    public Boolean getOnlyCurrentlyActive() {
        return onlyCurrentlyActive;
    }

    public void setOnlyCurrentlyActive(Boolean onlyCurrentlyActive) {
        this.onlyCurrentlyActive = onlyCurrentlyActive;
    }

    public CareSetting getCareSetting() {
        return careSetting;
    }

    public void setCareSetting(CareSetting careSetting) {
        this.careSetting = careSetting;
    }

}