/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.definition.library.implementerconfigured;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.SerializationService;
import org.openmrs.module.reporting.common.GroovyHelper;
import org.openmrs.module.reporting.definition.library.DefinitionLibrary;
import org.openmrs.module.reporting.definition.library.LibraryDefinitionSummary;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.serializer.ReportingSerializer;
import org.openmrs.serialization.SerializationException;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

/**
 * Concrete subclasses should be defined as beans (e.g. with the @Component annotation)
 *
 * Supports loading files with the extentions:
 * <ul>
 *     <li>.reportingserializerxml ... will be deserialized with {@link ReportingSerializer}</li>
 *     <li>.sql ... will produce the appropriate SqlXyzDefinition class</li>
 *     <li>.groovy ... file will be parsed and dynamically loaded as a Groovy class (which should implement T)</li>
 * </ul>
 * @param <T>
 */
public abstract class BaseImplementerConfiguredDefinitionLibrary<T extends Definition> implements DefinitionLibrary<T> {

	public static final String BASE_DIR = File.separator + "configuration" + File.separator + "reporting" + File.separator +
			"definitionlibraries" + File.separator;

	protected static final Log log = LogFactory.getLog(BaseImplementerConfiguredDefinitionLibrary.class);

	private Class<? super T> definitionClass;
	private String libraryNameSuffix;
	private File directory;

	private Class<? extends T> sqlDefinitionClass;
	private String sqlDefinitionProperty = "sql";

	private Map<String, T> definitions = null;
	private List<LibraryDefinitionSummary> summaries = null;

	@Autowired
	private SerializationService serializationService;

	@Autowired
	private AutowireCapableBeanFactory autowireCapableBeanFactory;

	/**
	 * @param definitionClass the class that all definitions should be a subclass of
	 * @param libraryNameSuffix what name to give this library (and also subdirectory where definitions are)
	 */
	protected BaseImplementerConfiguredDefinitionLibrary(Class<? super T> definitionClass, String libraryNameSuffix) {
		this.definitionClass = definitionClass;
		this.libraryNameSuffix = libraryNameSuffix;
		this.directory = new File(OpenmrsUtil.getApplicationDataDirectory() + BASE_DIR + libraryNameSuffix);
	}

	/**
	 * Optionally override the default directory (${appData}/configuration/reporting/definitionLibraries/${libraryNameSuffix}
	 * @param directory
	 */
	public void setDirectory(File directory) {
		this.directory = directory;
	}

	/**
	 * Set this if you want to use the default implementation of {@link #sqlDefinition(String)}
	 * @param sqlDefinitionClass
	 */
	public void setSqlDefinitionClass(Class<? extends T> sqlDefinitionClass) {
		this.sqlDefinitionClass = sqlDefinitionClass;
	}

	/**
	 * Set this if you want to use the default implementation of {@link #sqlDefinition(String)}, and the bean property is
	 * not the default "sql"
	 * @param sqlDefinitionProperty
	 */
	public void setSqlDefinitionProperty(String sqlDefinitionProperty) {
		this.sqlDefinitionProperty = sqlDefinitionProperty;
	}

	private void ensureDefinitions() {
		if (definitions == null) {
			loadDefinitions();
		}
	}

	public void loadDefinitions() {
		Map<String, T> newDefinitions = new HashMap<String, T>();
		List<LibraryDefinitionSummary> newSummaries = new ArrayList<LibraryDefinitionSummary>();

		ReportingSerializer serializer;
		if (serializationService != null) {
			serializer = (ReportingSerializer) serializationService.getSerializer(ReportingSerializer.class);
		}
		else {
			// this branch is only for tests where serializationService isn't available
			try {
				serializer = new ReportingSerializer();
			}
			catch (SerializationException e) { serializer = null; }
		}

		if (directory.exists() && directory.isDirectory()) {
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
							log.warn("Invalid serialized definition at " + libraryNameSuffix + " in " + file
									.getAbsolutePath(), ex);
						}
					} else if (filename.endsWith(".groovy")) {
						definition = (Definition) new GroovyHelper().parseClassFromFileAndNewInstance(file);
						autowireCapableBeanFactory.autowireBean(definition);
						// this only handles @Autowired, not @PostConstruct. To handle that we'd need to also call
						// autowireCapableBeanFactory.initializeBean(definition, "bean name");
						// (but I'm not sure if this is needed, and I'm not sure what to name the bean)
					} else if (filename.endsWith(".sql")) {
						String sql = OpenmrsUtil.getFileAsString(file);
						definition = sqlDefinition(sql);
					} else {
						log.warn("Don't know how to handle " + file.getAbsolutePath() + " based on file extension");
					}
					if (definition != null) {
						if (definition.getName() == null) {
							definition.setName(key + ".name");
						}
						if (definition.getDescription() == null) {
							definition.setDescription(key + ".description");
						}
						newDefinitions.put(key, (T) definition);
						newSummaries.add(summaryFor(key, definition));
					}
				}
				catch (IOException ex) {
					log.warn("Error reading " + file.getAbsolutePath(), ex);
				}
			}
		}
		this.definitions = newDefinitions;
		this.summaries = newSummaries;
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
		ensureDefinitions();
		return definitions.get(uuid);
	}

	@Override
	public List<LibraryDefinitionSummary> getDefinitionSummaries() {
		ensureDefinitions();
		return summaries;
	}

	public boolean hasAnyDefinitions() {
		ensureDefinitions();
		return definitions != null && definitions.size() > 0;
	}

	/**
	 * Subclasses should either override this method, or else they should call {@link #setSqlDefinitionClass(Class)} (and
	 * optionally {@link #setSqlDefinitionProperty(String)}) to get a default implementation that uses reflection to
	 * create an instance of a bean, set a property, and return it.
	 *
	 * @param sql
	 * @return
	 */
	protected T sqlDefinition(String sql) {
		try {
			T definition = sqlDefinitionClass.newInstance();
			PropertyUtils.setProperty(definition, sqlDefinitionProperty, sql);
			return definition;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	};

	private LibraryDefinitionSummary summaryFor(String key, Definition definition) {
		LibraryDefinitionSummary summary = new LibraryDefinitionSummary();
		summary.setKey(key);
		summary.setType(definition.getClass().getName());
		summary.setName(definition.getName());
		summary.setDescription(definition.getDescription());
		summary.setParameters(definition.getParameters());
		return summary;
	}

	public void setAutowireCapableBeanFactory(AutowireCapableBeanFactory autowireCapableBeanFactory) {
		this.autowireCapableBeanFactory = autowireCapableBeanFactory;
	}

}
