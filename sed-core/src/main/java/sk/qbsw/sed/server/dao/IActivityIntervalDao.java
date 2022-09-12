package sk.qbsw.sed.server.dao;

import java.util.List;

import sk.qbsw.core.dao.IDao;
import sk.qbsw.sed.server.model.restriction.CActivityInterval;

/**
 * Interface for accessing CActivityInterval object
 * 
 * @author rosenberg
 * @version 1.0
 * @since 1.6.6.1
 */
public interface IActivityIntervalDao extends IDao<CActivityInterval> {

	/**
	 * Returns activities restriction intervals by selected group restriction
	 * identifier
	 * 
	 * @param groupRestrictionId input group identifier
	 * @return list of entities
	 */
	public List<CActivityInterval> findByGroupRestriction(Long groupRestrictionId);

	/**
	 * Deletes interval entity
	 * 
	 * @param intervalId identifier of the selected interval
	 */
	public void delete(Long intervalId);

	public List<CActivityInterval> findForTable(Long clientId, Integer startRow, Integer endRow, String sortProperty, boolean sortAsc);

	public Long count(Long clientId);
}
