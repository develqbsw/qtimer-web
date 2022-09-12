package sk.qbsw.sed.client.service.business;

import java.util.List;

import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.restriction.CActivityIntervalData;
import sk.qbsw.sed.client.model.restriction.CEmployeeActivityLimitsData;
import sk.qbsw.sed.client.model.restriction.CGroupsAIData;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.framework.security.exception.CSecurityException;

public interface IActivityTimeLimitsService {

	/**
	 * Returns detail of selected group
	 * 
	 * @param groupId group identifier
	 * @return entity data for client side
	 * @throws CSecurityException in error case
	 * @throws CBusinessException in error case
	 */
	public CGroupsAIData loadGroupDetail(Long groupId) throws CBusinessException;

	/**
	 * Returns detail of currently saved group entity
	 * 
	 * @param data input data from client
	 * @return detail of selected group
	 * @throws CSecurityException in error case
	 * @throws CBusinessException in error case
	 */
	public CGroupsAIData saveGroup(CGroupsAIData data) throws CBusinessException;

	/**
	 * Removes persistent object associated with input identifier
	 * 
	 * @param groupId group identifier
	 * @return the flag of (successfully) finished operation
	 * @throws CSecurityException in error case
	 * @throws CBusinessException in error case
	 */
	public Boolean deleteGroup(Long groupId) throws CBusinessException;

	/**
	 * Returns list of clients groups entities by activity id and valid flag
	 * 
	 * @param clientId   client identifier
	 * @param activityId activity identifier
	 * @param validFlag  valid entity flag value
	 * @return list of groups data
	 * @throws CSecurityException in error case
	 * @throws CBusinessException in error case
	 */
	public List<CCodeListRecord> getValidActivityGroups(Long clientId, Long activityId, Boolean validFlag) throws CBusinessException;

	// --- intervals

	/**
	 * Returns interval entity (limit) data for client side
	 * 
	 * @param intervalId input interval identifier
	 * @return entity data for client side
	 * @throws CSecurityException in error case
	 * @throws CBusinessException in error case
	 */
	public CActivityIntervalData loadIntervalDetail(Long intervalId) throws CBusinessException;

	/**
	 * Returns interval entity (limit) data of the currently saved entity
	 * 
	 * @param data input entity data
	 * @return entity data for client side
	 * @throws CSecurityException in error case
	 * @throws CBusinessException in error case
	 */
	public CActivityIntervalData saveInterval(CActivityIntervalData data) throws CBusinessException;

	// - user associations

	/**
	 * Returns entity (employee limits) data of the currently saved entity
	 * 
	 * @param employeeId input employee identifier
	 * @return
	 * @throws CSecurityException in error case
	 * @throws CBusinessException in error case
	 */
	public CEmployeeActivityLimitsData loadEmployeeLimitsDetail(Long employeeId) throws CBusinessException;

	/**
	 * Returns entity (employee limits) data of the currently saved entity
	 * 
	 * @param data input employee limits data
	 * @return employee limits
	 * @throws CSecurityException in error case
	 * @throws CBusinessException in error case
	 */
	public CEmployeeActivityLimitsData saveEmployeeLimits(CEmployeeActivityLimitsData data) throws CBusinessException;
}
