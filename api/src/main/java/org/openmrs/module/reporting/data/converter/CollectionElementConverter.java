/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.converter;

import java.util.Collection;
import java.util.List;

/**
 * Takes a collection of objects and returns valueIfPresent
 * if the valueToCheck is in the collection, valueIfNotPresent otherwise
 */
public class CollectionElementConverter implements DataConverter {

	Object valueToCheck;
	Object valueIfPresent;
	Object valueIfNotPresent;

    public CollectionElementConverter() {}

    public CollectionElementConverter(Object valueToCheck, Object valueIfPresent, Object valueIfNotPresent) {
        this.valueToCheck = valueToCheck;
		this.valueIfPresent = valueIfPresent;
		this.valueIfNotPresent = valueIfNotPresent;
    }

    @Override
    public Object convert(Object original) {
		Collection<?> c = (Collection<?>)original;
		if (c != null && c.contains(valueToCheck)) {
			return valueIfPresent;
		}
		return valueIfNotPresent;
	}

    @Override
    public Class<?> getInputDataType() {
        return List.class;
    }

    @Override
    public Class<?> getDataType() {
        return Object.class;
    }
}
