/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
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
import org.openmrs.Drug;

import org.openmrs.module.reporting.common.Match;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

import java.util.Date;
import java.util.List;

@Caching(strategy = ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.StartStopDrugOrderCohortDefinition")
public class StartStopDrugOrderCohortDefinition extends BaseCohortDefinition {

    public static final long serialVersionUID = 1L;

    @ConfigurationProperty(group = "state")
    private Match state;

    @ConfigurationProperty(value = "drugConcepts")
    private List<Concept> drugConcepts;

    @ConfigurationProperty(value = "drugSets")
    private List<Concept> drugSets;

    @ConfigurationProperty(value = "onOrBefore")
    private Date onOrBefore;

    @ConfigurationProperty(value = "onOrAfter")
    private Date onOrAfter;

    @ConfigurationProperty(value = "careSetting")
    private CareSetting careSetting;

    @ConfigurationProperty(value = "drugs")
    private List<Drug> drugs;    
    
    public StartStopDrugOrderCohortDefinition() {
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Patients ");

        if (this.state != null) {
            builder.append(" who " + this.state.toString() + " drug(s) ");
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

        if (this.getDrugs() != null && this.getDrugs().size() > 0) {
        	for (Drug drug : this.getDrugs()) {
                builder.append(drug.getDisplayName() + " ");
            }
        	
        }

        if (this.getOnOrAfter() != null) {
            builder.append(" on or after " + this.getOnOrAfter() + " ");
        }

        if (this.getOnOrBefore() != null) {
            builder.append(" on or before " + this.getOnOrBefore() + " ");
        }
        
        if (this.careSetting != null) {
            builder.append("with care setting of " + this.careSetting + " ");
        }
        return builder.toString();
    }

    public Match getState() {
        return this.state;
    }

    public void setState(Match state) {
        this.state = state;
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

    public List<Drug> getDrugs() {
        return drugs;
    }

    public void setDrugs(List<Drug> drugs) {
        this.drugs = drugs;
    }
    
    public Date getOnOrBefore() {
        return onOrBefore;
    }

    public void setOnOrBefore(Date onOrBefore) {
        this.onOrBefore = onOrBefore;
    }

    public Date getOnOrAfter() {
        return onOrAfter;
    }

    public void setOnOrAfter(Date onOrAfter) {
        this.onOrAfter = onOrAfter;
    }

    public CareSetting getCareSetting() {
        return careSetting;
    }

    public void setCareSetting(CareSetting careSetting) {
        this.careSetting = careSetting;
    }

}