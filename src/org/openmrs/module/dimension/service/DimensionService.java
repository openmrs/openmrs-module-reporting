package org.openmrs.module.dimension.service;

import java.util.List;

import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.indicator.dimension.Dimension;
import org.springframework.transaction.annotation.Transactional;


public interface DimensionService extends OpenmrsService {

	public List<Class<? extends Dimension>> getDimensionTypes();
	
	/**
	 * Gets a dimension given its type and primary key
	 * 
	 * @param <T>
	 * @param type
	 * @param id
	 * @return
	 * @throws APIException
	 */
	@Transactional(readOnly = true)
	public <T extends Dimension> T getDimension(Class<T> type, Integer id) throws APIException;
	
	/**
	 * Gets a dimension given its UUID
	 * 
	 * @param uuid
	 * @return
	 * @throws APIException
	 */
	@Transactional(readOnly=true)
	public Dimension getDimensionByUuid(String uuid) throws APIException;
	
	/**
	 * Gets all dimensions (possibly including retired ones)
	 * 
	 * @param includeRetired
	 * @return
	 * @throws APIException
	 */
	@Transactional(readOnly = true)
	public List<Dimension> getAllDimensions(boolean includeRetired) throws APIException;
	
	/**
	 * Persists a dimension (either as a save or an update)
	 * 
	 * @param dimension
	 * @return
	 * @throws APIException
	 */
	@Transactional
	public Dimension saveDimension(Dimension dimension) throws APIException;
	
	/**
	 * Deletes a dimension from the database
	 * 
	 * @param dimension
	 */
	@Transactional
	public void purgeDimension(Dimension dimension);

}
