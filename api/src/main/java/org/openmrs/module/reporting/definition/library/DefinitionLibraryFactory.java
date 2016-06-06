package org.openmrs.module.reporting.definition.library;

import java.util.Collection;

/**
 * Implementing a bean of this interface allows you to dynamically create zero or more {@link DefinitionLibrary}s.
 */
public interface DefinitionLibraryFactory {

	/**
	 * An implementation of this interface can count on this method being invoked once by the Reporting module's activator.
	 * @return 0 or more definition libraries you want to register
	 */
	Collection<DefinitionLibrary<?>> getDefinitionLibraries();
	
}
