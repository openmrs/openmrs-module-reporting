package org.openmrs.module.reporting.indicator.dimension.persister;

import java.util.List;

import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.definition.persister.DataSetDefinitionPersister;
import org.openmrs.module.reporting.definition.service.SerializedDefinitionService;
import org.openmrs.module.reporting.indicator.dimension.Dimension;

/**
 * This class returns Dimensions that have been Serialized to the database
 * This class is annotated as a Handler that supports all Dimension classes
 * Specifying no order on this indicates that this is the default means of Persisting 
 * a Dimension.  To override this behavior, any additional DimensionPersister
 * should specify the order field on the Handler annotation.
 */
@Handler(supports={Dimension.class}, order=100)
public class SerializedDimensionPersister implements DimensionPersister {

    //****************
    // Constructor
    //****************
	private SerializedDimensionPersister() { }
	
    //****************
    // Instance methods
    //****************
	
	/**
	 * Utility method that returns the SerializedDefinitionService
	 */
	public SerializedDefinitionService getService() {
		return Context.getService(SerializedDefinitionService.class);
	}
	
	/**
	 * @see DimensionPersister#getDimension(Integer)
	 */
	public Dimension getDimension(Integer id) {
		return getService().getDefinition(Dimension.class, id);
	}
	
	/**
	 * @see DimensionPersister#getDimensionByUuid(String)
	 */
	public Dimension getDimensionByUuid(String uuid) {
		return getService().getDefinitionByUuid(Dimension.class, uuid);
	}

	/**
	 * @see DimensionPersister#getAllDimensions(boolean)
	 */
	public List<Dimension> getAllDimensions(boolean includeRetired) {
		return getService().getAllDefinitions(Dimension.class, includeRetired);
	}
	
	/**
	 * @see DataSetDefinitionPersister#getNumberOfDimensions(boolean)
	 */
	public int getNumberOfDimensions(boolean includeRetired) {
    	return getService().getNumberOfDefinitions(Dimension.class, includeRetired);
	}
	
	/**
	 * @see DimensionPersister#getDimensions(String, boolean)
	 */
	public List<Dimension> getDimensions(String name, boolean exactMatchOnly) {
		return getService().getDefinitions(Dimension.class, name, exactMatchOnly);
	}
	
	/**
	 * @see DimensionPersister#purgeDimension(Dimension)
	 */
	public void purgeDimension(Dimension dimension) {
		getService().purgeDefinition(dimension);
	}
	
	/**
	 * @see DimensionPersister#saveDimension(Dimension)
	 */
	public Dimension saveDimension(Dimension dimension) {
		return getService().saveDefinition(dimension);
	}
}
