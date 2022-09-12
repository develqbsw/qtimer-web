package sk.qbsw.sed.server.dao;

import java.util.List;

import sk.qbsw.core.dao.IDao;
import sk.qbsw.sed.server.model.restriction.CXUserActivityRestriction;

/**
 * @author rosenberg
 * @version 1.0
 * @since 1.6.6.1
 */
public interface IXUserActivityRestrictionDao extends IDao<CXUserActivityRestriction> {
	
	/**
	 * Returns list of entities for the selected user
	 * 
	 * @param userId target user identifier
	 * @return list of entities
	 */
	public List<CXUserActivityRestriction> findByUserId(Long userId);

	/**
	 * Returns list of valid entities for the selected user
	 * 
	 * @param userId       target user identifier
	 * @param projectGroup
	 * @return list of entities
	 */
	public List<CXUserActivityRestriction> findByProjectGroupAndUser(Long userId, String projectGroup);

	/**
	 * Removes all entities associated to user
	 * 
	 * @param userId user identifier
	 */
	public void deleteByUserId(Long userId);

	/**
	 * Removes selected entity
	 * 
	 * @param id record identifier
	 */
	public void deleteById(Long id);
}
