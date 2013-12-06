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

package org.openmrs.module.reporting.common;

import org.openmrs.Auditable;
import org.openmrs.Retireable;
import org.openmrs.User;
import org.openmrs.Voidable;

import java.util.Date;

/**
 * Convenience class that holds audit information for an OpenMRS data or metadata object
 */
public class AuditInfo {

    private Date dateCreated;

    private User creator;

    private Date dateChanged;

    private User changedBy;

    private Boolean voided;

    private Date dateVoided;

    private User voidedBy;

    private String voidReason;

    private Boolean retired;

    private Date dateRetired;

    private User retiredBy;

    private String retireReason;

    public AuditInfo() {
    }

    /**
     * Will copy appropriate fields from {@link Auditable}, {@link Voidable}, and {@link Retireable}
     * @param dataOrMetadata
     */
    public AuditInfo(Object dataOrMetadata) {
        if (dataOrMetadata instanceof Auditable) {
            copyFieldsFrom((Auditable) dataOrMetadata);
        }
        if (dataOrMetadata instanceof Voidable) {
            copyFieldsFrom((Voidable) dataOrMetadata);
        }
        if (dataOrMetadata instanceof Retireable) {
            copyFieldsFrom((Retireable) dataOrMetadata);
        }
    }

    private void copyFieldsFrom(Auditable auditable) {
        this.creator = auditable.getCreator();
        this.dateCreated = auditable.getDateCreated();
        this.changedBy = auditable.getChangedBy();
        this.dateChanged = auditable.getDateChanged();
    }

    private void copyFieldsFrom(Voidable voidable) {
        this.voided = voidable.isVoided();
        this.voidedBy = voidable.getVoidedBy();
        this.dateVoided = voidable.getDateVoided();
        this.voidReason = voidable.getVoidReason();
    }

    private void copyFieldsFrom(Retireable retireable) {
        this.retired = retireable.isRetired();
        this.retiredBy = retireable.getRetiredBy();
        this.dateRetired = retireable.getDateRetired();
        this.retireReason = retireable.getRetireReason();
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public Date getDateChanged() {
        return dateChanged;
    }

    public void setDateChanged(Date dateChanged) {
        this.dateChanged = dateChanged;
    }

    public User getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(User changedBy) {
        this.changedBy = changedBy;
    }

    public Boolean getVoided() {
        return voided;
    }

    public void setVoided(Boolean voided) {
        this.voided = voided;
    }

    public Date getDateVoided() {
        return dateVoided;
    }

    public void setDateVoided(Date dateVoided) {
        this.dateVoided = dateVoided;
    }

    public User getVoidedBy() {
        return voidedBy;
    }

    public void setVoidedBy(User voidedBy) {
        this.voidedBy = voidedBy;
    }

    public String getVoidReason() {
        return voidReason;
    }

    public void setVoidReason(String voidReason) {
        this.voidReason = voidReason;
    }

    public Boolean getRetired() {
        return retired;
    }

    public void setRetired(Boolean retired) {
        this.retired = retired;
    }

    public Date getDateRetired() {
        return dateRetired;
    }

    public void setDateRetired(Date dateRetired) {
        this.dateRetired = dateRetired;
    }

    public User getRetiredBy() {
        return retiredBy;
    }

    public void setRetiredBy(User retiredBy) {
        this.retiredBy = retiredBy;
    }

    public String getRetireReason() {
        return retireReason;
    }

    public void setRetireReason(String retireReason) {
        this.retireReason = retireReason;
    }
}
