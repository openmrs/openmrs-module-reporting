/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
