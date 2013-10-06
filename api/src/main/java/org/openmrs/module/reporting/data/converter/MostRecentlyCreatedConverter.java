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

import org.openmrs.Auditable;
import org.openmrs.util.OpenmrsUtil;

import java.util.Collection;

/**
 * Gets the most-recently-created element of a collection of Auditable.
 */
public class MostRecentlyCreatedConverter implements DataConverter {

    private Class<?> typeOfItem;

    public MostRecentlyCreatedConverter(Class<?> typeOfItem) {
        this.typeOfItem = typeOfItem;
    }

    @Override
    public Object convert(Object original) {
        Collection c = (Collection) original;
        if (c == null) {
            return null;
        }
        Auditable latest = null;
        for (Object o : c) {
            if (o instanceof Auditable) {
                Auditable candidate = (Auditable) o;
                if (latest == null || OpenmrsUtil.compare(latest.getDateCreated(), candidate.getDateCreated()) < 0) {
                    latest = candidate;
                }
            }
        }
        return latest;
    }

    /**
     * Should really be Collection<org.openmrs.Auditable>
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
}
