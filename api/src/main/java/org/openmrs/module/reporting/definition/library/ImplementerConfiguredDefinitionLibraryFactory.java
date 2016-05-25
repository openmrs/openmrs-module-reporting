package org.openmrs.module.reporting.definition.library;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.openmrs.api.SerializationService;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * At module startup, this factory will register definition libraries based on <em>files</em> that the implementer has
 * configured on the file system.
 *
 * For example if the implementer puts a "females.sql" file at "$APPDATA/configuration/reporting/definitionlibraries/cohort",
 * then there will be a SqlCohortDefinition available from {@link AllDefinitionLibraries} with the key
 * "configuration.definitionlibrary.cohort.females".
 *
 * See {@link BaseImplementerConfiguredDefinitionLibrary} and all its concrete subclasses.
 */
@Component
public class ImplementerConfiguredDefinitionLibraryFactory implements DefinitionLibraryFactory {

	public static final String DIRECTORY = File.separator + "configuration" + File.separator + "reporting" + File.separator + "definitionlibraries" + File.separator;

	@Autowired
	private SerializationService serializationService;

	@Override
	public Collection<DefinitionLibrary<?>> getDefinitionLibraries() {
		String appData = OpenmrsUtil.getApplicationDataDirectory();
		List<DefinitionLibrary<?>> list = new ArrayList<DefinitionLibrary<?>>();
		maybeAdd(list, new ImplementerConfiguredCohortDefinitionLibrary(serializationService,
				new File(appData + DIRECTORY + "cohort")));
		return list;
	}

	private void maybeAdd(List<DefinitionLibrary<?>> list,
	                      BaseImplementerConfiguredDefinitionLibrary<?> library) {
		if (library.hasAnyDefinitions()) {
			list.add(library);
		}
	}

}
