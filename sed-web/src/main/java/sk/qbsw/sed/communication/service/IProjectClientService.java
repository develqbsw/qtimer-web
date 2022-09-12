package sk.qbsw.sed.communication.service;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;

import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.codelist.CProjectRecord;
import sk.qbsw.sed.client.model.codelist.CResultProjectsGroups;
import sk.qbsw.sed.client.model.lock.CLockRecord;
import sk.qbsw.sed.framework.security.exception.CSecurityException;
import sk.qbsw.sed.fw.exception.CBussinessDataException;

/**
 * Service for managing projects
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 */
public interface IProjectClientService {

	/**
	 * Finds all valid records related to logged user
	 * 
	 * @param userId
	 * @return codelist records
	 * @throws CBussinessDataException
	 */
	@Cacheable(value = "validProjectsForUser", key = "#userId")
	public List<CCodeListRecord> getValidRecordsForUserCached(Long userId) throws CBussinessDataException;

	/**
	 * Finds all valid records related to logged user's entity and project groups
	 * 
	 * @return
	 * @throws CSecurityException
	 */
	public CResultProjectsGroups getAllRecordsWithGroups() throws CBussinessDataException;

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
	 * @param timeStampId
	 * @return
	 */
	public CProjectRecord getDetail(Long projectId) throws CBussinessDataException;

	/**
	 * creates project
	 * 
	 * @param timeStampId
	 * @return
	 * @throws CSecurityException
	 */
	public CLockRecord add(CProjectRecord record) throws CBussinessDataException;

	/**
	 * Modifies activity record
	 * 
	 * @param newRecord
	 * @param invokerInfo
	 * @return
	 * @throws CSecurityException
	 */
	public CLockRecord modify(CProjectRecord newRecord) throws CBussinessDataException;

}
