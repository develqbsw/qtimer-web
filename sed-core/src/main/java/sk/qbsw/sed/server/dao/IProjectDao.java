package sk.qbsw.sed.server.dao;

import java.util.List;

import sk.qbsw.core.dao.IDao;
import sk.qbsw.sed.client.model.table.IFilterCriteria;
import sk.qbsw.sed.server.model.codelist.CProject;
import sk.qbsw.sed.server.model.domain.CUser;

/**
 * DAO interface to CProject
 * 
 * @see CProject
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 * 
 */
public interface IProjectDao extends IDao<CProject> {
	
	/**
	 * Finds all valid projects for organization
	 * 
	 * @param orgId organization id
	 * @return list of projects
	 */
	public List<CProject> findAll(Long orgId, boolean valid);

	/**
	 * Finds all valid projects for organization
	 * 
	 * @param orgId organization id
	 * @return list of projects
	 */
	public List<CProject> findAll(Long orgId);

	/**
	 * Finds all valid projects for organization by criteria for table paging
	 * 
	 * @param orgId          selected client identifier
	 * @param filterCriteria filter criteria
	 * @return of entities
	 */
	public List<CProject> findAllByCriteria(Long clientId, IFilterCriteria criteria, CUser user, Integer startRow, Integer endRow, String sortProperty, boolean sortAsc);

	/**
	 * Finds all valid projects for organization
	 * 
	 * @param orgId
	 * @param valid
	 * @return
	 */
	public List<CProject> findAllByLastUsed(Long userId, boolean valid);

	/**
	 * Finds all projects by timesheet or validity
	 * 
	 * @param orgId
	 * @param valid
	 * @param timesheetId
	 * @return
	 */
	public List<CProject> findByValidityOrTimeSheet(Long orgId, boolean valid, Long timesheetId);

	/**
	 * Finds projects by organization and name
	 * 
	 * @param orgId
	 * @param name
	 * @return
	 */
	public List<CProject> findByName(Long orgId, String name);

	/**
	 * Finds projects by organization and combination group and projectId
	 * 
	 * @param orgId
	 * @param name
	 * @return
	 */
	public List<CProject> findByGroupAndProjectId(Long orgId, String group, String projectId);

	/**
	 * Finds default project
	 * 
	 * @param orgId
	 * @return default project or null if does not exists default project
	 */
	public CProject findDefaultProject(Long orgId);

	public Long count(Long clientId, IFilterCriteria criteria, CUser user);
}
