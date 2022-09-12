package sk.qbsw.sed.communication.service;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;

import sk.qbsw.sed.client.model.codelist.CActivityRecord;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.lock.CLockRecord;
import sk.qbsw.sed.framework.security.exception.CSecurityException;
import sk.qbsw.sed.fw.exception.CBussinessDataException;

public interface IActivityClientService {

	/**
	 * Returns valid records related to logged entity ID
	 * 
	 * @param userId
	 * @return list of CodeListRecords
	 * @throws CBussinessDataException
	 */
	@Cacheable(value = "validActivitiesForUser", key = "#userId")
	public List<CCodeListRecord> getValidRecordsForUser(Long userId) throws CBussinessDataException;

	/**
	 * Returns valid working records related to logged entity ID
	 * 
	 * @param userId
	 * @return list of CodeListRecords
	 * @throws CBussinessDataException
	 */
	@Cacheable(value = "validWorkingActivitiesForUser", key = "#userId")
	public List<CCodeListRecord> getValidWorkingRecordsForUser(Long userId) throws CBussinessDataException;

	/**
	 * Returns valid records related to logged entity ID used for activity limit
	 * settings
	 * 
	 * @return list of CodeListRecords
	 * @throws CSecurityException
	 */
	public List<CCodeListRecord> getValidRecordsForLimits() throws CBussinessDataException;

	/**
	 * Finds all records related to logged user's entity
	 * 
	 * @return codelist records
	 * @throws CSecurityException
	 */
	public List<CCodeListRecord> getAllRecords() throws CBussinessDataException;

	/**
	 * Reads detail of activity record
	 * 
	 * @param activityId
	 * @return
	 * @throws CBussinessDataException
	 */
	public CActivityRecord getDetail(Long activityId) throws CBussinessDataException;

	/**
	 * creates activity
	 * 
	 * @param timeStampId
	 * @return
	 * @throws CSecurityException
	 */
	public CLockRecord add(CActivityRecord record) throws CBussinessDataException;

	/**
	 * Modifies activity record
	 * 
	 * @param newRecord
	 * @param invokerInfo
	 * @return
	 * @throws CSecurityException
	 */
	public CLockRecord modify(CActivityRecord newRecord) throws CBussinessDataException;

}
