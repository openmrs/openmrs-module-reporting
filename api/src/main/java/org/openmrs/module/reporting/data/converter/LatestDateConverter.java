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

package org.openmrs.module.reporting.data.converter;

import org.openmrs.BaseOpenmrsObject;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.ReflectionUtil;

import java.util.Collection;

/**
 * Gets the most-recently element by encounterdatetime of a collection of Encounter.
 */
public class LatestDateConverter implements DataConverter {

    private Class<?> typeOfItem;
    private String property;

    public LatestDateConverter(Class<?> typeOfItem, String property) {
        this.typeOfItem = typeOfItem;
        this.property = property;
    }

    @Override
    public Object convert(Object original) {
        Collection c = (Collection) original;
        if (c == null) {
            return null;
        }
        BaseOpenmrsObject latest = null;
        for (Object o : c) {
            if (o instanceof BaseOpenmrsObject) {
                BaseOpenmrsObject candidate = (BaseOpenmrsObject) o;
                Object latestValue = null;
                if (latest != null ){
                    latestValue = ReflectionUtil.getPropertyValue(latest, property);
                }
                Object candidateValue = ReflectionUtil.getPropertyValue(candidate, property);
                if (latest == null || ObjectUtil.nullSafeCompare(latestValue, candidateValue) < 0) {
                    latest = candidate;
                }
            }
        }
        return latest;
    }

    /**
     * Should really be Collection<org.openmrs.Encounter>
     * @return
     */
    @Override
    public Class<?> getInputDataType() {
        return Collection.class;
    }

    @Override
    public Class<?> getDataType() {
        return typeOfItem;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }
}
