/*
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

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.User;
import org.openmrs.VisitType;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

import java.util.Date;
import java.util.List;

/**
 * Eventually we will move this to a more broadly-shared module.
 */
@Localized("reporting.VisitCohortDefinition")
public class VisitCohortDefinition extends BaseCohortDefinition {

    @ConfigurationProperty(group = "which")
    private List<VisitType> visitTypeList;

    @ConfigurationProperty(group = "where")
    private List<Location> locationList;

    @ConfigurationProperty(group = "which")
    private List<Concept> indicationList;

    @ConfigurationProperty(group = "when")
    private Date startedOnOrAfter;

    @ConfigurationProperty(group = "when")
    private Date startedOnOrBefore;

    @ConfigurationProperty(group = "when")
    private Date stoppedOnOrAfter;

    @ConfigurationProperty(group = "when")
    private Date stoppedOnOrBefore;

    @ConfigurationProperty(group = "when")
    private Date activeOnOrAfter;

    @ConfigurationProperty(group = "when")
    private Date activeOnOrBefore;

    @ConfigurationProperty(group = "when")
    private Boolean active;

    @ConfigurationProperty(group = "other")
    private Boolean returnInverse = Boolean.FALSE;

    @ConfigurationProperty(group = "other")
    private User createdBy;

    @ConfigurationProperty(group = "other")
    private Date createdOnOrBefore;

    @ConfigurationProperty(group = "other")
    private Date createdOnOrAfter;

    @ConfigurationProperty(group = "where")
    private boolean includeChildLocations = false;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Patients with visits");
        if (startedOnOrAfter != null) {
            sb.append(" started on or after " + startedOnOrAfter);
        }
        if (startedOnOrBefore != null) {
            sb.append(" started on or before " + startedOnOrBefore);
        }
        if (stoppedOnOrAfter != null) {
            sb.append(" stopped on or after " + stoppedOnOrAfter);
        }
        if (stoppedOnOrBefore != null) {
            sb.append(" stopped on or before " + stoppedOnOrBefore);
        }
        if (activeOnOrAfter != null) {
            sb.append(" active on or after " + activeOnOrAfter);
        }
        if (activeOnOrBefore != null) {
            sb.append(" active on or before " + activeOnOrBefore);
        }
        if (active != null) {
            if (active) {
                sb.append(" and is active");
            } else {
                sb.append(" and is completed");
            }
        }
        if (locationList != null) {
            sb.append(" at " + locationList);
        }
        if (visitTypeList != null) {
            sb.append(" of type " + visitTypeList);
        }
        if (indicationList != null) {
            sb.append(" for reason " + indicationList);
        }
        if (returnInverse != null && returnInverse) {
            sb.append(" AND INVERT THIS");
        }
        if (createdBy != null) {
            sb.append(" created by " + createdBy);
        }
        if (createdOnOrAfter != null) {
            sb.append(" created on or after " + createdOnOrAfter);
        }
        if (createdOnOrBefore != null) {
            sb.append(" created on or before " + createdOnOrBefore);
        }
        return sb.toString();
    }

    public List<VisitType> getVisitTypeList() {
        return visitTypeList;
    }

    public void setVisitTypeList(List<VisitType> visitTypeList) {
        this.visitTypeList = visitTypeList;
    }

    public List<Location> getLocationList() {
        return locationList;
    }

    public void setLocationList(List<Location> locationList) {
        this.locationList = locationList;
    }

    public List<Concept> getIndicationList() {
        return indicationList;
    }

    public void setIndicationList(List<Concept> indicationList) {
        this.indicationList = indicationList;
    }

    public Date getStartedOnOrAfter() {
        return startedOnOrAfter;
    }

    public void setStartedOnOrAfter(Date startedOnOrAfter) {
        this.startedOnOrAfter = startedOnOrAfter;
    }

    public Date getStartedOnOrBefore() {
        return startedOnOrBefore;
    }

    public void setStartedOnOrBefore(Date startedOnOrBefore) {
        this.startedOnOrBefore = startedOnOrBefore;
    }

    public Date getStoppedOnOrAfter() {
        return stoppedOnOrAfter;
    }

    public void setStoppedOnOrAfter(Date stoppedOnOrAfter) {
        this.stoppedOnOrAfter = stoppedOnOrAfter;
    }

    public Date getStoppedOnOrBefore() {
        return stoppedOnOrBefore;
    }

    public void setStoppedOnOrBefore(Date stoppedOnOrBefore) {
        this.stoppedOnOrBefore = stoppedOnOrBefore;
    }

    public Date getActiveOnOrAfter() {
        return activeOnOrAfter;
    }

    public void setActiveOnOrAfter(Date activeOnOrAfter) {
        this.activeOnOrAfter = activeOnOrAfter;
    }

    public Date getActiveOnOrBefore() {
        return activeOnOrBefore;
    }

    public void setActiveOnOrBefore(Date activeOnOrBefore) {
        this.activeOnOrBefore = activeOnOrBefore;
    }

    public Boolean getReturnInverse() {
        return returnInverse;
    }

    public void setReturnInverse(Boolean returnInverse) {
        this.returnInverse = returnInverse;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedOnOrBefore() {
        return createdOnOrBefore;
    }

    public void setCreatedOnOrBefore(Date createdOnOrBefore) {
        this.createdOnOrBefore = createdOnOrBefore;
    }

    public Date getCreatedOnOrAfter() {
        return createdOnOrAfter;
    }

    public void setCreatedOnOrAfter(Date createdOnOrAfter) {
        this.createdOnOrAfter = createdOnOrAfter;
    }

    public Boolean isActive() {
        return active;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public boolean isIncludeChildLocations() {
        return includeChildLocations;
    }

    public boolean getIncludeChildLocations() {
        return isIncludeChildLocations();
    }

    public void setIncludeChildLocations(boolean includeChildLocations) {
        this.includeChildLocations = includeChildLocations;
    }
}
