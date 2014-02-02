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

package org.openmrs.module.reporting.dataset.definition;

import org.openmrs.module.reporting.common.TimePeriod;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

/**
 * Will dynaically generate a list of iterations, and delegate to
 * {@link org.openmrs.module.reporting.dataset.definition.MultiParameterDataSetDefinition}
 *
 * Specifying repeatPerTimePeriod requires you to set <em>startDate</em> and <em>endDate</em> parameters on this DSD,
 * and will produce one iteration for each (repeatPerTimePeriod, e.g. DAILY) between those dates. These iterations will
 * cover the entire time from startDate to endDate, even if the last iteration is shorter than the others (e.g. repeating
 * WEEKLY from Dec 1 to Dec 31 will produce four 7-day iterations and one 3-day iteration).
 */
public class RepeatPerTimePeriodDataSetDefinition extends BaseDataSetDefinition implements DataSetDefinition {

    @ConfigurationProperty
    private DataSetDefinition baseDefinition;

    @ConfigurationProperty
    private TimePeriod repeatPerTimePeriod;

    public DataSetDefinition getBaseDefinition() {
        return baseDefinition;
    }

    public void setBaseDefinition(DataSetDefinition baseDefinition) {
        this.baseDefinition = baseDefinition;
    }

    public TimePeriod getRepeatPerTimePeriod() {
        return repeatPerTimePeriod;
    }

    public void setRepeatPerTimePeriod(TimePeriod repeatPerTimePeriod) {
        this.repeatPerTimePeriod = repeatPerTimePeriod;
    }

}
