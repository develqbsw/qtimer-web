package sk.qbsw.sed.communication.service;

import java.util.List;

import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.lock.CLockRecord;
import sk.qbsw.sed.client.model.restriction.CActivityIntervalData;
import sk.qbsw.sed.client.model.restriction.CEmployeeActivityLimitsData;
import sk.qbsw.sed.client.model.restriction.CGroupsAIData;
import sk.qbsw.sed.fw.exception.CBussinessDataException;

public interface IActivityRestrictionClientService {

	/**
	 * Returns detail of selected group.
	 * 
	 * @param groupId group identifier
	 * @return entity data for client side
	 * @throws CBussinessDataException in error case
	 */
	public CGroupsAIData getGroupDetail(Long groupId) throws CBussinessDataException;

	/**
	 * Saves group entity.
	 * 
	 * @param record group record to save
	 * @return lock record
	 * @throws CBussinessDataException in error case
	 */
	public CLockRecord saveGroup(CGroupsAIData record) throws CBussinessDataException;

	/**
	 * Returns interval entity (limit) data.
	 * 
	 * @param intervalId input interval identifier
	 * @return activity interval entity data
	 * @throws CBussinessDataException in error case
	 */
	public CActivityIntervalData getIntervalDetail(Long intervalId) throws CBussinessDataException;

	/**
	 * Saves interval entity (limit) data of entity.
	 * 
	 * @param data input entity data
	 * @return lock record
	 * @throws CBussinessDataException in error case
	 */
	public CLockRecord saveInterval(CActivityIntervalData activityGroupData) throws CBussinessDataException;

	/**
	 * Returns list of clients groups entities by activity id and valid flag.
	 * 
	 * @param clientId   client identifier
	 * @param activityId activity identifier
	 * @param validFlag  valid entity flag value
	 * @return list of groups data
	 * @throws CBussinessDataException in error case
	 */
	public List<CCodeListRecord> getValidActivityGroups(Long clientId, Long activityId, Boolean validFlag) throws CBussinessDataException;

	/**
	 * Returns entity (employee limits) data.
	 * 
	 * @param employeeId input employee identifier
	 * @return
	 * @throws CBussinessDataException in error case
	 */
	public CEmployeeActivityLimitsData getEmployeeLimitsDetail(Long employeeId) throws CBussinessDataException;

	/**
	 * Saves entity (employee limits) data.
	 * 
	 * @param data input employee limits data
	 * @return lock record
	 * @throws CBussinessDataException in error case
	 */
	public CLockRecord saveEmployeeLimits(CEmployeeActivityLimitsData data) throws CBussinessDataException;
}
