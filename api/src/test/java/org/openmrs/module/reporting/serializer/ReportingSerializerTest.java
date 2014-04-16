package org.openmrs.module.reporting.serializer;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.PatientStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.Indicator;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ReportingSerializerTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * @see {@link ReportingSerializer#ReportingSerializer()}
	 * 
	 */
	@Test
	@Verifies(value = "should serialize a cohort definition", method = "ReportingSerializer()")
	public void ReportingSerializer_shouldSerializeACohortDefinition() throws Exception {
		AgeCohortDefinition cd = new AgeCohortDefinition();
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		cd.setMaxAge(15);
		String xml = new ReportingSerializer().serialize(cd);
		Assert.assertNotNull(xml);
	}
	
	/**
	 * @see {@link ReportingSerializer#ReportingSerializer()}
	 * 
	 */
	@Test
	@Verifies(value = "should serialize workflow state by uuid", method = "ReportingSerializer()")
	@SuppressWarnings("deprecation")
	public void ReportingSerializer_shouldSerializeWorkflowStateByUuid() throws Exception {
		PatientStateCohortDefinition pscd = new PatientStateCohortDefinition();
		ProgramWorkflowState pws = Context.getProgramWorkflowService().getState(2);
		List<ProgramWorkflowState> states = new ArrayList<ProgramWorkflowState>();
		states.add(pws);
		pscd.setStates(states);
		String xml = new ReportingSerializer().serialize(pscd);
		Assert.assertTrue(xml.contains("<programWorkflowState id=\"4\" uuid=\"e938129e-248a-482a-acea-f85127251472\"/>"));
	}

	/**
     * @see {@link ReportingSerializer#ReportingSerializer()}
     * 
     */
    @Test
    @Verifies(value = "should serialize an indicator that contains an unsaved cohort definition", method = "ReportingSerializer()")
    public void ReportingSerializer_shouldSerializeAnIndicatorThatContainsAnUnsavedCohortDefinition() throws Exception {

		GenderCohortDefinition males = new GenderCohortDefinition();
		males.setUuid(UUID.randomUUID().toString());
		males.setMaleIncluded(true);
		
		CohortIndicator numMales = CohortIndicator.newCountIndicator("numMales", new Mapped<CohortDefinition>(males, null), null);
		
		ReportingSerializer s = new ReportingSerializer();
		
		String serialization = s.serialize(numMales);
		CohortIndicator hydrated = s.deserialize(serialization, CohortIndicator.class);
		Assert.assertNotNull(hydrated.getCohortDefinition());
		Assert.assertNotNull(hydrated.getCohortDefinition().getParameterizable());
		Assert.assertTrue(((GenderCohortDefinition)hydrated.getCohortDefinition().getParameterizable()).getMaleIncluded());
		Assert.assertFalse(((GenderCohortDefinition)hydrated.getCohortDefinition().getParameterizable()).getFemaleIncluded());
    }
    
	/**
     * @see {@link ReportingSerializer#ReportingSerializer()}
     * 
     */
    @Test
    @Verifies(value = "should serialize an indicator that contains a persisted cohort definition", method = "ReportingSerializer()")
    public void ReportingSerializer_shouldSerializeAnIndicatorThatContainsAPersistedCohortDefinition() throws Exception {
    	AgeCohortDefinition age = new AgeCohortDefinition();
		age.addParameter(new Parameter("onDate", "On Date", Date.class));
		age.setMaxAge(15);
		age.setName("Age on Date");
		Context.getService(CohortDefinitionService.class).saveDefinition(age);
		
		CohortIndicator ind = new CohortIndicator();
		ind.setCohortDefinition(age, ParameterizableUtil.createParameterMappings("onDate=07/08/2009"));
		ind.setName("Age on some random date");
		
		String xml = new ReportingSerializer().serialize(ind);
		
		// now edit the age cohort definition to make sure the indicator has a reference to it, and not a copy
		CohortDefinition reloaded = Context.getService(CohortDefinitionService.class).getDefinitionByUuid(age.getUuid());
		reloaded.setName("Name has changed");
		Context.getService(CohortDefinitionService.class).saveDefinition(reloaded);
		
		Indicator out = new ReportingSerializer().deserialize(xml, Indicator.class);
		Assert.assertTrue(out instanceof CohortIndicator);
		Assert.assertEquals("Age on some random date", out.getName());
		Assert.assertEquals("Name has changed", ((CohortIndicator) out).getCohortDefinition().getParameterizable().getName());
		Assert.assertEquals("07/08/2009", ((CohortIndicator) out).getCohortDefinition().getParameterMappings().get("onDate"));
    }

	@Test
	public void testMapConverters() throws Exception {
		ReportingSerializer rs = new ReportingSerializer();
		List<Map<String, String>> maps = new ArrayList<Map<String, String>>();
		maps.add(ObjectUtil.toMap("cat=gato,dog=perro"));
		maps.add(ObjectUtil.toMap("cat=meow,dog=woof"));
		String serialized = rs.serialize(maps);
		List<Map<String, String>> newMaps = rs.deserialize(serialized, List.class);
		Assert.assertEquals("cat=gato,dog=perro", ObjectUtil.toString(newMaps.get(0), "=", ","));
		Assert.assertEquals("cat=meow,dog=woof", ObjectUtil.toString(newMaps.get(1), "=", ","));
	}

    @Test
    public void testSerializeToStream() throws Exception {
        Object object = "Test";
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ReportingSerializer rs = new ReportingSerializer();
        rs.serializeToStream(object, out);

        assertThat(out.toString("UTF-8"), is("<string>Test</string>"));
    }
}