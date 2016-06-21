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
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDatetimeDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.SqlEncounterDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.SqlPatientDataDefinition;
import org.openmrs.util.OpenmrsUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(OpenmrsUtil.class)
public class ImplementerConfiguredEncounterDataDefinitionLibraryTest {

	public static final String SQL_QUERY = "select encounter_id, patient_id from encounter";

	private File directory;

	private ImplementerConfiguredEncounterDataDefinitionLibrary library;

	@Before
	public void setUp() throws Exception {
		directory = mock(File.class);
		when(directory.exists()).thenReturn(true);
		when(directory.isDirectory()).thenReturn(true);

		library = new ImplementerConfiguredEncounterDataDefinitionLibrary();
		library.setDirectory(directory);
	}

	@Test
	public void testSqlDefinition() throws Exception {
		when(directory.listFiles()).thenReturn(new File[] { new File("patientId.sql") });

		mockStatic(OpenmrsUtil.class);
		when(OpenmrsUtil.getFileAsString(any(File.class))).thenReturn(SQL_QUERY);

		assertThat(library.getDefinitionSummaries().size(), is(1));
		assertThat(library.getDefinitionSummaries().get(0).getKey(), is("configuration.definitionlibrary.encounterData.patientId"));

		EncounterDataDefinition definition = library.getDefinition("configuration.definitionlibrary.encounterData.patientId");
		assertThat(definition, instanceOf(SqlEncounterDataDefinition.class));
		assertThat(((SqlEncounterDataDefinition) definition).getSql(), is(SQL_QUERY));
	}

	@Test
	public void testSerializedDefinition() throws Exception {
		when(directory.listFiles()).thenReturn(new File[] { new File("encounterDatetime.reportingserializerxml") });

		mockStatic(OpenmrsUtil.class);
		when(OpenmrsUtil.getFileAsString(any(File.class))).
				thenReturn("<org.openmrs.module.reporting.data.encounter.definition.EncounterDatetimeDataDefinition/>");

		assertThat(library.getDefinitionSummaries().size(), is(1));
		assertThat(library.getDefinitionSummaries().get(0).getKey(), is("configuration.definitionlibrary.encounterData.encounterDatetime"));

		EncounterDataDefinition definition = library.getDefinition("configuration.definitionlibrary.encounterData.encounterDatetime");
		assertThat(definition, instanceOf(EncounterDatetimeDataDefinition.class));
	}

}