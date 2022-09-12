package sk.qbsw.sed.client.service.codelist;

import java.util.Date;
import java.util.List;

import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.codelist.CProjectRecord;
import sk.qbsw.sed.client.model.codelist.CResultProjectsGroups;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.framework.security.exception.CSecurityException;

/**
 * Service for managing projects
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 */
public interface IProjectService {

	/**
	 * Finds all valid records related to logged user's entity
	 * 
	 * @return codelist records
	 * @throws CSecurityException
	 */
	public List<CCodeListRecord> getValidRecords() throws CSecurityException;

	/**
	 * Finds all valid records related to logged user's entity and project groups
	 * 
	 * @return
	 * @throws CSecurityException
	 */
	public CResultProjectsGroups getAllRecordsWithGroups() throws CSecurityException;

	/**
	 * Finds all records related to logged user's entity
	 * 
	 * @return codelist records
	 * @throws CSecurityException
	 */
	public List<CCodeListRecord> getAllRecords() throws CSecurityException;

	/**
	 * Returns valid records related to logged entity ID
	 * 
	 * @param timesheetId additional record to read
	 * @return list of CodeListRecords
	 * @throws CSecurityException
	 */
	public List<CCodeListRecord> getValidRecords(Long timesheetId) throws CSecurityException;

	/**
	 * Reads detail of activity record
	 * 
	 * @param timeStampId
	 * @return
	 */
	public CProjectRecord getDetail(Long timeStampId);

	/**
	 * creates project
	 * 
	 * @param record
	 * @return
	 * @throws CBusinessException
	 */
	public Long add(CProjectRecord record) throws CBusinessException;

	/**
	 * updates old project or creates new one
	 * 
	 * @param record
	 * @param projectIds
	 * @return
	 * @throws CBusinessException
	 */
	public Long addOrUpdate(CProjectRecord record, List<Long> projectIds) throws CBusinessException;

	/**
	 * Modifies activity record
	 * 
	 * @param id
	 * @param newRecord
	 * @param timestamp
	 * @return
	 * @throws CBusinessException
	 */
	public void modify(Long id, CProjectRecord newRecord, Date timestamp) throws CBusinessException;

	/**
	 * 
	 * @return
	 * @throws CSecurityException
	 */
	public List<Long> invalidateLoggedClientProjects() throws CSecurityException;

	/**
	 * 
	 * @param projectIds
	 * @throws CSecurityException
	 */
	public void validateSelectedProjects(List<Long> projectIds) throws CSecurityException;

	/**
	 * 
	 * @return
	 * @throws CSecurityException
	 */
	CResultProjectsGroups getValidRecordsWithGroups() throws CSecurityException;

	/**
	 * Returns valid project records for user specified by ID.
	 * 
	 * @param userId user identifier
	 * @return list of projects
	 * @throws CSecurityException
	 */
	List<CCodeListRecord> getValidRecordsForUser(Long userId) throws CSecurityException;
}
