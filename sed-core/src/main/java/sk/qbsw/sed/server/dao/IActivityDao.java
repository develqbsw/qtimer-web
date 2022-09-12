package sk.qbsw.sed.server.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import sk.qbsw.core.dao.IDao;
import sk.qbsw.sed.client.model.table.IFilterCriteria;
import sk.qbsw.sed.server.model.codelist.CActivity;
import sk.qbsw.sed.server.model.domain.CUser;

/**
 * Interface for accessing Activity object
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 */
@Repository
public interface IActivityDao extends IDao<CActivity> {

	public List<CActivity> findAll(Long orgId, boolean withSystem);

	/**
	 * finds all activities by organization id and validity
	 * 
	 * @param orgId
	 * @param valid
	 * @return
	 */
	public List<CActivity> findAll(Long orgId, boolean valid, boolean withSystem);

	/**
	 * Finds all activities by organization i, validity and working
	 * 
	 * @param orgId
	 * @param valid
	 * @param working
	 * @return
	 */
	public List<CActivity> findAll(Long orgId, boolean valid, boolean working, boolean withSystem);

	/**
	 * Finds all activities for table, paged by rows, sorted by property
	 * 
	 * @param orgId
	 * @param startRow
	 * @param endRow
	 * @param sortProperty
	 * @param sortAsc
	 * @return
	 */
	public List<CActivity> findAll(Long orgId, Integer startRow, Integer endRow, String sortProperty, boolean sortAsc);

	/**
	 * Find all records by name
	 * 
	 * @param orgId
	 * @param name
	 * @return
	 */
	public List<CActivity> findByName(Long orgId, String name);

	/**
	 * Finds all activities by validity or timesheet
	 * 
	 * @param orgId
	 * @param valid
	 * @param timesheetId
	 * @return
	 */
	public List<CActivity> findByValidityOrTimeSheet(Long orgId, boolean valid, Long timesheetId);

	/**
	 * Initializes activities - clones default values during registration
	 */
	public void initialize(Long clientId);

	/**
	 * Finds default activity
	 * 
	 * @param orgId
	 * @return default activity
	 */
	public CActivity findDefaultActivity(Long orgId, final boolean withSystem);

	public List<CActivity> findAllForLimits(Long orgId, boolean valid, boolean withSystem);

	public Long count(Long clientId);

	public Long count(Long orgId, IFilterCriteria filterCriteria, CUser user);

	public List<CActivity> findAllByCriteria(Long clientId, IFilterCriteria criteria, CUser user, Integer startRow, Integer endRow, String sortProperty, boolean sortAsc);

	public List<CActivity> findAllByLastUsed(Long userId, boolean b);
}
