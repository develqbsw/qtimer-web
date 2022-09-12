package sk.qbsw.sed.server.dao;

import java.util.List;

import sk.qbsw.core.dao.IDao;
import sk.qbsw.sed.server.model.restriction.CActivityRestrictionGroup;

/**
 * @author rosenberg
 * @version 1.0
 * @since 1.6.6.1
 */
public interface IActivityRestrictionGroupDao extends IDao<CActivityRestrictionGroup> {

	/**
	 * Returns list of group for selected client
	 * 
	 * @param clientId selected client
	 * @return returns list of entities
	 */
	List<CActivityRestrictionGroup> findByClient(Long clientId);

	/**
	 * Returns list of group entities
	 * 
	 * @param clientId   input client identifier (required)
	 * @param activityId activity identifier (can be null)
	 * @param validFloag valid glag value (can be null)
	 * @return
	 */
	List<CActivityRestrictionGroup> findByClientActivityValidFlag(Long clientId, Long activityId, Boolean validFloag);

	/**
	 * Removes selected entity
	 * 
	 * @param groupId entity identifier
	 */
	void delete(Long groupId);

	List<CActivityRestrictionGroup> findAllForTable(Long clientId, Integer startRow, Integer endRow, String sortProperty, boolean sortAsc);

	Long count(Long clientId);
}
