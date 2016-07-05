package org.openmrs.module.reporting.definition.library.implementerconfigured;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.api.SerializationService;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.serializer.ReportingSerializer;
import org.openmrs.util.OpenmrsUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

@RunWith(PowerMockRunner.class)
@PrepareForTest(OpenmrsUtil.class)
public class ImplementerConfiguredCohortDefinitionLibraryTest {

	public static final String SQL_QUERY = "select person_id from person where gender = 'F'";

	private File directory;

	private ImplementerConfiguredCohortDefinitionLibrary library;

	@Before
	public void setUp() throws Exception {
		directory = mock(File.class);
		when(directory.exists()).thenReturn(true);
		when(directory.isDirectory()).thenReturn(true);

		library = new ImplementerConfiguredCohortDefinitionLibrary();
		library.setDirectory(directory);
	}

	@Test
	public void testSqlDefinition() throws Exception {
		when(directory.listFiles()).thenReturn(new File[] { new File("females.sql") });

		mockStatic(OpenmrsUtil.class);
		when(OpenmrsUtil.getFileAsString(any(File.class))).thenReturn(SQL_QUERY);

		assertThat(library.getDefinitionSummaries().size(), is(1));
		assertThat(library.getDefinitionSummaries().get(0).getKey(), is("configuration.definitionlibrary.cohort.females"));

		CohortDefinition definition = library.getDefinition("configuration.definitionlibrary.cohort.females");
		assertThat(definition, instanceOf(SqlCohortDefinition.class));
		assertThat(((SqlCohortDefinition) definition).getQuery(), is(SQL_QUERY));
	}

	@Test
	public void testSerializedDefinition() throws Exception {
		when(directory.listFiles()).thenReturn(new File[] { new File("females.reportingserializerxml") });

		mockStatic(OpenmrsUtil.class);
		when(OpenmrsUtil.getFileAsString(any(File.class))).thenReturn(
				"<org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition>"
						+ "<femaleIncluded>true</femaleIncluded>"
						+ "</org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition>");

		assertThat(library.getDefinitionSummaries().size(), is(1));
		assertThat(library.getDefinitionSummaries().get(0).getKey(), is("configuration.definitionlibrary.cohort.females"));

		CohortDefinition definition = library.getDefinition("configuration.definitionlibrary.cohort.females");
		assertThat(definition, instanceOf(GenderCohortDefinition.class));
		assertThat(((GenderCohortDefinition) definition).getFemaleIncluded(), is(true));
	}

	@Test
	public void testGroovyDefinition() throws Exception {
		when(directory.listFiles()).thenReturn(new File[] { new File("females.groovy") });
		library.setAutowireCapableBeanFactory(mock(AutowireCapableBeanFactory.class));

		assertThat(library.getDefinitionSummaries().size(), is(1));
		assertThat(library.getDefinitionSummaries().get(0).getKey(), is("configuration.definitionlibrary.cohort.females"));

		CohortDefinition definition = library.getDefinition("configuration.definitionlibrary.cohort.females");
		assertThat(definition, instanceOf(GenderCohortDefinition.class));
		assertThat(definition.getClass().getName(), is("FemalesCohortDefinition"));
	}

	@Test(expected = ClassCastException.class)
	public void testInvalidGroovyDefinition() throws Exception {
		when(directory.listFiles()).thenReturn(new File[] { new File("invalidCohortDefinition.groovy") });
		library.setAutowireCapableBeanFactory(mock(AutowireCapableBeanFactory.class));

		assertThat(library.getDefinitionSummaries().size(), is(1));
		assertThat(library.getDefinitionSummaries().get(0).getKey(), is("configuration.definitionlibrary.cohort.invalidCohortDefinition"));

		CohortDefinition definition = library.getDefinition("configuration.definitionlibrary.cohort.invalidCohortDefinition");
	}

}