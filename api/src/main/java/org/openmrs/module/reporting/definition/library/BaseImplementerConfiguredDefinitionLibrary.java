package org.openmrs.module.reporting.definition.library;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.SerializationService;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.serializer.ReportingSerializer;
import org.openmrs.serialization.OpenmrsSerializer;
import org.openmrs.serialization.SerializationException;
import org.openmrs.util.OpenmrsUtil;

/**
 * Supports loading files with the extentions:
 * <ul>
 *     <li>.reportingserializerxml ... will be deserialized with {@link ReportingSerializer}</li>
 *     <li>.sql ... will producethe appropriate SqlXyzDefinition class</li>
 * </ul>
 * @see {@link ImplementerConfiguredDefinitionLibraryFactory}
 * @param <T>
 */
public abstract class BaseImplementerConfiguredDefinitionLibrary<T extends Definition> implements DefinitionLibrary<T> {

	protected static final Log log = LogFactory.getLog(BaseImplementerConfiguredDefinitionLibrary.class);

	private Class<? super T> definitionClass;
	private String libraryNameSuffix;
	private File directory;

	private Map<String, T> definitions = new LinkedHashMap<String, T>();
	private List<LibraryDefinitionSummary> summaries = new ArrayList<LibraryDefinitionSummary>();

	/**
	 * @param definitionClass the class that all definitions should be a subclass of
	 * @param libraryNameSuffix what name to give this library (and also subdirectory where definitions are)
	 */
	protected BaseImplementerConfiguredDefinitionLibrary(Class<? super T> definitionClass, String libraryNameSuffix,
	                                                     SerializationService serializationService, File directory) {
		this.definitionClass = definitionClass;
		this.libraryNameSuffix = libraryNameSuffix;
		this.directory = directory;

		OpenmrsSerializer serializer = serializationService.getSerializer(ReportingSerializer.class);
		for (File file : directory.listFiles()) {
			String filename = file.getName();
			String definitionName = filename.substring(0, filename.lastIndexOf('.'));
			String key = getKeyPrefix() + definitionName;
			try {
				log.info("Loading " + file.getAbsolutePath());
				Definition definition = null;
				if (filename.endsWith(".reportingserializerxml")) {
					try {
						definition = serializer.deserialize(OpenmrsUtil.getFileAsString(file), Definition.class);
					}
					catch (SerializationException ex) {
						log.warn("Invalid serialized definition at " + libraryNameSuffix + " in " + file.getAbsolutePath(), ex);
					}
				}
				else if (filename.endsWith(".sql")) {
					String sql = OpenmrsUtil.getFileAsString(file);
					definition = sqlDefinition(sql);
				}
				else {
					log.warn("Don't know how to handle " + file.getAbsolutePath() + " based on file extension");
				}
				if (definition != null) {
					if (definition.getName() == null) {
						definition.setName(key + ".name");
					}
					if (definition.getDescription() == null) {
						definition.setDescription(key + ".description");
					}
					definitions.put(key, (T) definition);
					summaries.add(summaryFor(key, definition));
				}
			}
			catch (IOException ex) {
				log.warn("Error reading " + file.getAbsolutePath(), ex);
			}
		}
	}

	@Override
	public Class<? super T> getDefinitionType() {
		return definitionClass;
	}

	@Override
	public String getKeyPrefix() {
		return "configuration.definitionlibrary." + libraryNameSuffix + ".";
	}

	@Override
	public T getDefinition(String uuid) {
		return definitions.get(uuid);
	}

	@Override
	public List<LibraryDefinitionSummary> getDefinitionSummaries() {
		return summaries;
	}

	public boolean hasAnyDefinitions() {
		return definitions != null && definitions.size() > 0;
	}

	protected abstract T sqlDefinition(String sql);

	private LibraryDefinitionSummary summaryFor(String key, Definition definition) {
		LibraryDefinitionSummary summary = new LibraryDefinitionSummary();
		summary.setKey(key);
		summary.setType(definition.getClass().getName());
		summary.setName(definition.getName());
		summary.setDescription(definition.getDescription());
		summary.setParameters(definition.getParameters());
		return summary;
	}

}
