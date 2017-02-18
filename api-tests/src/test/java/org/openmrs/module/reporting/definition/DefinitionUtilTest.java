/**
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
package org.openmrs.module.reporting.definition;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests {@code DefinitionUtil} methods
 */
public class DefinitionUtilTest extends BaseModuleContextSensitiveTest {

    @Test
    public void getAllLocationsAndChildLocations_shouldGetAllLocationsAndChildlocations() {
        Assert.assertEquals(2, getListOfTestLocations().size());
        Assert.assertEquals(5, DefinitionUtil.getAllLocationsAndChildLocations(getListOfTestLocations()).size());
    }

    private List<Location> getListOfTestLocations() {
        List<Location> testLocations = new ArrayList<Location>();
        Location location1 = new Location(1);
        location1.setName("test-location1");

        Location location2 = new Location(2);
        location2.setName("test-location2");

        Location location3 = new Location(3);
        location3.setName("test-location3");

        Location location4 = new Location(4);
        location4.setName("test-location4");

        Location location5 = new Location(5);
        location5.setName("test-location5");

        location1.addChildLocation(location2);
        location1.addChildLocation(location3);
        location3.addChildLocation(location4);

        testLocations.add(location1);
        testLocations.add(location5);

        return testLocations;
    }
}