package org.openmrs.module.indicator.persister;

import java.util.List;

import org.openmrs.annotation.Handler;
import org.openmrs.api.db.SerializedObjectDAO;
import org.openmrs.module.indicator.dimension.Dimension;
import org.openmrs.serialization.OpenmrsSerializer;

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
    // Properties
    //****************

	private SerializedObjectDAO dao = null;
	private OpenmrsSerializer serializer = null;

	/**
	 * Sets the DAO
	 * 
	 * @param dao
	 */
    public void setDao(SerializedObjectDAO dao) {
    	this.dao = dao;
    }

	/**
	 * Sets the serializer
	 * 
	 * @param serializer
	 */
    public void setSerializer(OpenmrsSerializer serializer) {
    	this.serializer = serializer;
    }

    //****************
    // Instance methods
    //****************

	/**
	 * @see DimensionPersister#getAllDimensions(boolean)
	 */
	public List<Dimension> getAllDimensions(boolean includeRetired) {
		return dao.getAllObjects(Dimension.class, includeRetired);
	}
	
	/**
	 * @see org.openmrs.module.indicator.persister.DimensionPersister#getDimension(java.lang.Integer)
	 */
	public Dimension getDimension(Integer id) {
		return dao.getObject(Dimension.class, id);
	}
	
	/**
	 * @see org.openmrs.module.indicator.persister.DimensionPersister#getDimensionByUuid(java.lang.String)
	 */
	public Dimension getDimensionByUuid(String uuid) {
		return dao.getObjectByUuid(Dimension.class, uuid);
	}
	
	/**
	 * @see org.openmrs.module.indicator.persister.DimensionPersister#getDimensions(java.lang.String, boolean)
	 */
	public List<Dimension> getDimensions(String name, boolean exactMatchOnly) {
		return dao.getAllObjectsByName(Dimension.class, name, exactMatchOnly);
	}
	
	/**
	 * @see org.openmrs.module.indicator.persister.DimensionPersister#purgeDimension(org.openmrs.module.indicator.dimension.Dimension)
	 */
	public void purgeDimension(Dimension dimension) {
		dao.purgeObject(dimension.getId());
	}
	
	public Dimension saveDimension(Dimension dimension) {
		return dao.saveObject(dimension, serializer);
	}
	
}
