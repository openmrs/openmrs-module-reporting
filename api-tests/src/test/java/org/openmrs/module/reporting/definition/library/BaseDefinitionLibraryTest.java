package org.openmrs.module.reporting.definition.library;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.library.BuiltInCohortDefinitionLibrary;
import org.openmrs.module.reporting.common.MessageUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 */
public class BaseDefinitionLibraryTest extends BaseModuleContextSensitiveTest {

	@Autowired
	BuiltInCohortDefinitionLibrary builtInCohorts;

    @Autowired
	TestDefinitionLibrary testLibrary;

    @Test
    public void shouldReturnMessageCodeForAnnotatedValueIfExists() throws Exception {
		CohortDefinition cd = builtInCohorts.getDefinition("males");
		Assert.assertEquals(BuiltInCohortDefinitionLibrary.PREFIX + "males.name", cd.getName());
		Assert.assertEquals(BuiltInCohortDefinitionLibrary.PREFIX + "males.description", cd.getDescription());
	}

	@Test
	public void shouldReturnAnnotatedNameIfSpecified() throws Exception {
		CohortDefinition cd = testLibrary.getDefinition(TestDefinitionLibrary.PREFIX + "females");
		Assert.assertEquals("Female patients", cd.getName());
		Assert.assertEquals("Patients whose gender is F", cd.getDescription());
	}

	@Test
	public void shouldReturnMethodNameAsDisplayStringByDefault() throws Exception {
		CohortDefinition cd = testLibrary.getDefinition(TestDefinitionLibrary.PREFIX + "UnknownGender");
		Assert.assertEquals("Unknown Gender", cd.getName());
		Assert.assertEquals("", cd.getDescription());

		cd = testLibrary.getDefinition(TestDefinitionLibrary.PREFIX + "PatientsAged0To15");
		Assert.assertEquals("Patients Aged 0 To 15", cd.getName());
		Assert.assertEquals("", cd.getDescription());
	}

	@Test
	public void shouldUseMethodNameAsCodeIfNoValueSpecified() throws Exception {
		CohortDefinition cd = testLibrary.getDefinition(TestDefinitionLibrary.PREFIX + "UnknownGender");
		Assert.assertNotNull(cd);
	}

	/**
	 * Basic set of cohort definitions
	 */
	@Component
	public static class TestDefinitionLibrary extends BaseDefinitionLibrary<CohortDefinition> {

		public static final String PREFIX = "reporting.library.cohortDefinition.test.";

		@Override
		public Class<? super CohortDefinition> getDefinitionType() {
			return CohortDefinition.class;
		}

		@Override
		public String getKeyPrefix() {
			return PREFIX;
		}

		@DocumentedDefinition(value = "males")
		public GenderCohortDefinition getMales() {
			GenderCohortDefinition cd = new GenderCohortDefinition();
			cd.setMaleIncluded(true);
			return cd;
		}

		@DocumentedDefinition(value = "females", name = "Female patients", definition = "Patients whose gender is F")
		public GenderCohortDefinition getFemales() {
			GenderCohortDefinition cd = new GenderCohortDefinition();
			cd.setFemaleIncluded(true);
			return cd;
		}

		@DocumentedDefinition
		public GenderCohortDefinition getUnknownGender() {
			GenderCohortDefinition cd = new GenderCohortDefinition();
			cd.setUnknownGenderIncluded(true);
			return cd;
		}

		@DocumentedDefinition
		public AgeCohortDefinition getPatientsAged0To15() {
			AgeCohortDefinition cd = new AgeCohortDefinition();
			cd.setMinAge(0);
			cd.setMaxAge(15);
			return cd;
		}
	}
}
