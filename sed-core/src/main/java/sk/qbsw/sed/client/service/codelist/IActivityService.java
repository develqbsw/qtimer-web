package sk.qbsw.sed.client.service.codelist;

import java.util.List;

import sk.qbsw.sed.client.model.codelist.CActivityRecord;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.framework.security.exception.CSecurityException;

/**
 * Service for managing activities
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 */
public interface IActivityService {

	/**
	 * Returns valid records related to logged entity ID
	 * 
	 * @return list of CodeListRecords
	 * @throws CSecurityException
	 */
	public List<CCodeListRecord> getValidRecords() throws CSecurityException;

	/**
	 * Returns valid records related to logged entity ID used for activity limit
	 * settings
	 * 
	 * @return list of CodeListRecords
	 * @throws CSecurityException
	 */
	public List<CCodeListRecord> getValidRecordsForLimits() throws CSecurityException;

	/**
	 * Returns valid working records related to logged entity ID
	 * 
	 * @return list of CodeListRecords
	 * @throws CSecurityException
	 */
	public List<CCodeListRecord> getValidWorkingRecords() throws CSecurityException;

	/**
	 * Returns valid non working records related to logged entity ID
	 * 
	 * @return list of CodeListRecords
	 * @throws CSecurityException
	 */
	public List<CCodeListRecord> getValidNonWorkingRecords() throws CSecurityException;

	/**
	 * Returns valid records related to logged entity ID
	 * 
	 * @param timesheetId additional record to read
	 * @return list of CodeListRecords
	 * @throws CSecurityException
	 */
	public List<CCodeListRecord> getValidRecords(Long timesheetId) throws CSecurityException;

	/**
	 * Finds all records related to logged user's entity
	 * 
	 * @return codelist records
	 * @throws CSecurityException
	 */
	public List<CCodeListRecord> getAllRecords() throws CSecurityException;

	/**
	 * Reads detail of activity record
	 * 
	 * @param timeStampId
	 * @return
	 */
	public CActivityRecord getDetail(Long timeStampId);

	/**
	 * creates activity
	 * 
	 * @param record
	 * @return
	 * @throws CSecurityException
	 */
	public Long add(CActivityRecord record) throws CBusinessException;

	/**
	 * creates activity
	 * 
	 * @param record
	 * @return
	 * @throws CSecurityException
	 */
	public Long addOrUpdate(CActivityRecord record) throws CBusinessException;

	/**
	 * Modifies activity record
	 * 
	 * @param id
	 * @param newRecord
	 * @return
	 * @throws CSecurityException
	 */
	public void modify(Long id, CActivityRecord newRecord) throws CBusinessException;

	/**
	 * Initializes acitvities for application creation
	 * 
	 * @param clientId
	 */
	public void initialize(Long clientId);

	/**
	 * 
	 * @return
	 * @throws CSecurityException
	 */
	public List<CCodeListRecord> getValidWorkingRecordsForUser() throws CSecurityException;
}
