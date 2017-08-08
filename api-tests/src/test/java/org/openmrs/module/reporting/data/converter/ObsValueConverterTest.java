package org.openmrs.module.reporting.data.converter;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.openmrs.Obs;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ObsValueConverterTest extends BaseModuleContextSensitiveTest {

    @Autowired
    @Qualifier("conceptService")
    private ConceptService conceptService;
    
    @Autowired
    @Qualifier("obsService")
    private ObsService obsService;

    @Test
    public void shouldHandleListOfObs() {

    	ObsValueConverter converter = new ObsValueConverter();
    	
    	List<Obs> obses = new ArrayList<Obs>();
    	obses.add(obsService.getObs(11));
    	obses.add(obsService.getObs(12));
    	obses.add(obsService.getObs(13));
    	
    	List<Object> results = ((List) converter.convert(obses));
    	
    	assertEquals(3, results.size());

    	assertEquals(new Double(175.0), (Double) results.get(0));
    	assertEquals("PB and J", (String) results.get(1));
    	assertEquals(true, (Boolean) results.get(2));
    	
    }
    
    @Test
    public void shouldHandleNullInput() {
    	
    	ObsValueConverter converter = new ObsValueConverter();
    	assertNull(converter.convert(null));
    	assertNull(converter.convert(new Object()));
    }


}
