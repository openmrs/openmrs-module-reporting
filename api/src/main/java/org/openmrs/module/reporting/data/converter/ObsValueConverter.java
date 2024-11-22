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

import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.ObjectUtil;

/**
 * Converts an Obs to it's value
 */
public class ObsValueConverter implements DataConverter {

    public ObsValueConverter() { }

    @Override
    public Object convert(Object original) {
		Obs o = (Obs) original;
		if (o == null) {
			return null;
		}
		if (o.getValueBoolean() != null) {
			return o.getValueBoolean();
		}
		if (o.getValueCoded() != null) {
			return ObjectUtil.format(o.getValueCoded());
		}
		if (o.getValueComplex() != null) {
			return o.getValueComplex();
		}
		if (o.getValueDatetime() != null) {
			return o.getValueDatetime();
		}
		if (o.getValueDate() != null) {
			return o.getValueDate();
		}
		if (o.getValueDrug() != null) {
			return ObjectUtil.format(o.getValueDrug());
		}
		if (o.getValueNumeric() != null) {
			return o.getValueNumeric();
		}
		if (o.getValueText() != null) {
			return o.getValueText();
		}
		if (o.getValueTime() != null) {
			return o.getValueTime();
		}
		return o.getValueAsString(Context.getLocale());
    }

    @Override
    public Class<?> getInputDataType() {
        return Obs.class;
    }

    @Override
    public Class<?> getDataType() {
        return Object.class;
    }
}
