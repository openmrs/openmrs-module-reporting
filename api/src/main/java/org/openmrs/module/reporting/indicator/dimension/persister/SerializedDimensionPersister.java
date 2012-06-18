package org.openmrs.module.reporting.indicator.dimension.persister;

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.definition.persister.SerializedDefinitionPersister;
import org.openmrs.module.reporting.indicator.dimension.Dimension;

/**
 * This class returns Dimensions that have been Serialized to the database
 * This class is annotated as a Handler that supports all Dimension classes
 * Specifying no order on this indicates that this is the default means of Persisting 
 * a Dimension.  To override this behavior, any additional DimensionPersister
 * should specify the order field on the Handler annotation.
 */
@Handler(supports={Dimension.class}, order=100)
public class SerializedDimensionPersister extends SerializedDefinitionPersister<Dimension> implements DimensionPersister {

	private SerializedDimensionPersister() { }

	/**
	 * @see SerializedDefinitionPersister#getBaseClass()
	 */
	@Override
	public Class<Dimension> getBaseClass() {
		return Dimension.class;
	}
}
