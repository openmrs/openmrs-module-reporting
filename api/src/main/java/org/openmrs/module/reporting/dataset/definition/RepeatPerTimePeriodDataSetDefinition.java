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
import org.openmrs.module.reporting.evaluation.parameter.Mapped;

/**
 * Will dynaically generate a list of iterations, and delegate to
 * {@link org.openmrs.module.reporting.dataset.definition.MultiParameterDataSetDefinition}
 *
 * Specifying repeatPerTimePeriod requires you to add two Date parameters to this DSD, named <em>startDate</em> and
 * <em>endDate</em>, and will produce one iteration for each (repeatPerTimePeriod, e.g. DAILY) between the start and end
 * date parameter values.
 *
 * These iterations will cover the entire time from startDate to endDate, even if the last iteration is shorter than the
 * others (e.g. repeating WEEKLY from Dec 1 to Dec 31 will produce four 7-day iterations and one 3-day iteration).
 *
 * Like in the rest of the reporting framework, if the endDate parameter value has no time component, it is interpreted
 * as signifying the last instant of that day (e.g. endDate = 2013-12-31 means "2013-12-31 23:59:59.999"
 *
 * Example usage:
 * <code>
 * RepeatPerTimePeriodDataSetDefinition dsd = new RepeatPerTimePeriodDataSetDefinition();
 * dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
 * dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
 * dsd.setRepeatPerTimePeriod(TimePeriod.DAILY);
 *
 * // simplest case, base DSD also has startDate and endDate parameters
 * dsd.setBaseDefinition(Mapped.mapStraightThrough(otherDsd));
 *
 * // different parameter names on base DSD
 * dsd.setBaseDefinition(Mapped.map(otherDsd, "onOrAfter=${startDate},onOrBefore=${endDate}"));
 *
 * // mapping parameters in a way that only uses startDate (e.g. this is 9am-5pm every day)
 * dsd.setBaseDefinition(Mapped.map(otherDsd, "start=${startDate+9h},end=${startDate+17h}"));
 * </code>
 */
public class RepeatPerTimePeriodDataSetDefinition extends BaseDataSetDefinition implements DataSetDefinition {

    @ConfigurationProperty
    private Mapped<? extends DataSetDefinition> baseDefinition;

    @ConfigurationProperty
    private TimePeriod repeatPerTimePeriod;

    public Mapped<? extends DataSetDefinition> getBaseDefinition() {
        return baseDefinition;
    }

    public void setBaseDefinition(Mapped<? extends DataSetDefinition> baseDefinition) {
        this.baseDefinition = baseDefinition;
    }

    public TimePeriod getRepeatPerTimePeriod() {
        return repeatPerTimePeriod;
    }

    public void setRepeatPerTimePeriod(TimePeriod repeatPerTimePeriod) {
        this.repeatPerTimePeriod = repeatPerTimePeriod;
    }

}
