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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.LocationService;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ObsValueTextAsCodedConverterTest extends BaseModuleContextSensitiveTest {

    @Autowired
    @Qualifier("locationService")
    private LocationService locationService;

    @Test
    public void shouldReturnLocationForObs() {

        Location expectedLocation = locationService.getLocation(2);
        Obs obs = new Obs();
        obs.setValueText("2");
        
        DataConverter converter = new ObsValueTextAsCodedConverter<Location>(Location.class);
        
        assertThat((Location) converter.convert(obs), is(expectedLocation));
    }

    @Test
    public void shouldReturnNullIfValueTextEmpty() {

        Obs obs = new Obs();
        obs.setValueText("");

        DataConverter converter = new ObsValueTextAsCodedConverter<Location>(Location.class);

        assertNull(converter.convert(obs));
    }

    // TODO we can remove test below once we support other OpenmrsObjects
    
    @Test
    public void shouldFailIfTypeOtherThanLocation() {
    	assertThrows(IllegalArgumentException.class, () -> {
	        ObsValueTextAsCodedConverter converter = new ObsValueTextAsCodedConverter<Patient>(Patient.class);
	        converter.convert(new Obs());
    	});
    }

}
