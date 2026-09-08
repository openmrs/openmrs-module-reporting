/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.query;

import org.openmrs.OpenmrsObject;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;

import java.util.Map;

/**
 * Allows you to easily expose a query with different names for its parameters
 * (which typically must be the same as @ConfigurationProperty-annotated properties.
 */
public abstract class MappedParametersQuery<Q extends Query<T>, T extends OpenmrsObject> extends BaseQuery<T> {

    @ConfigurationProperty
    private Mapped<Q> wrapped;

	public MappedParametersQuery() {}

    public MappedParametersQuery(Q toWrap, Map<String, String> renamedParameters) {
		wrapped = ParameterizableUtil.copyAndMap(toWrap, this, renamedParameters);
    }

    public Mapped<Q> getWrapped() {
        return wrapped;
    }

    public void setWrapped(Mapped<Q> wrapped) {
        this.wrapped = wrapped;
    }

}
