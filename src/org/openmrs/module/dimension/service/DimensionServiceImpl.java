package org.openmrs.module.dimension.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.dataset.definition.persister.DataSetDefinitionPersister;
import org.openmrs.module.dimension.persister.DimensionPersister;
import org.openmrs.module.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.indicator.dimension.Dimension;
import org.openmrs.util.HandlerUtil;

public class DimensionServiceImpl extends BaseOpenmrsService implements DimensionService {

	protected Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * Returns the DimensionPersister for the passed Dimension
	 * @param dimension
	 * @return the DimensionPersister for the passed Dimension
	 * @throws APIException if no matching persister is found
	 */
	protected DimensionPersister getPersister(Class<? extends Dimension> dimension) {
		DimensionPersister persister = HandlerUtil.getPreferredHandler(DimensionPersister.class, dimension);
		if (persister == null) {
			throw new APIException("No DimensionPersister found for <" + dimension + ">");
		}
		return persister;
	}
	
	/**
	 * @see org.openmrs.module.dimension.service.DimensionService#getAllDimensions(boolean)
	 */
	public List<Dimension> getAllDimensions(boolean includeRetired) throws APIException {
		List<Dimension> ret = new ArrayList<Dimension>();
		for (DimensionPersister persister : HandlerUtil.getHandlersForType(DimensionPersister.class, null)) {
			if (log.isDebugEnabled())
				log.debug("Persister: " + persister.getClass().getName());			
			if (persister != null) { 
				ret.addAll(persister.getAllDimensions(includeRetired));
			}
		}
		return ret;
	}
	
	/**
	 * @see org.openmrs.module.dimension.service.DimensionService#getDimension(java.lang.Class, java.lang.Integer)
	 */
	public <T extends Dimension> T getDimension(Class<T> type, Integer id) throws APIException {
		DimensionPersister persister = getPersister(type);
		if (log.isDebugEnabled()) {
			log.debug("Persister: " + persister.getClass().getName());
		}
		return (T) persister.getDimension(id);
	}
	
	/**
	 * @see org.openmrs.module.dimension.service.DimensionService#getDimensionByUuid(java.lang.String)
	 */
	public Dimension getDimensionByUuid(String uuid) throws APIException {
		for (DimensionPersister p : HandlerUtil.getHandlersForType(DimensionPersister.class, null)) {
			Dimension dimension = p.getDimensionByUuid(uuid);
			if (dimension != null) {
				return dimension;
			}
		}
		return null;
	}
	
	/**
	 * @see org.openmrs.module.dimension.service.DimensionService#getDimensionTypes()
	 */
	public List<Class<? extends Dimension>> getDimensionTypes() {
		List<Class<? extends Dimension>> ret = new ArrayList<Class<? extends Dimension>>();
		ret.add(CohortDefinitionDimension.class);
		return ret;
	}
	
	/**
	 * @see org.openmrs.module.dimension.service.DimensionService#purgeDimension(org.openmrs.module.indicator.dimension.Dimension)
	 */
	public void purgeDimension(Dimension dimension) {
		getPersister(dimension.getClass()).purgeDimension(dimension);
	}
	
	/**
	 * @see org.openmrs.module.dimension.service.DimensionService#saveDimension(org.openmrs.module.indicator.dimension.Dimension)
	 */
	public Dimension saveDimension(Dimension dimension) throws APIException {
		return getPersister(dimension.getClass()).saveDimension(dimension);
	}
	
}
