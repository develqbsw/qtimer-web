package sk.qbsw.sed.server.dao;

import java.util.List;

import sk.qbsw.core.dao.IDao;
import sk.qbsw.sed.framework.report.model.CReportModel;
import sk.qbsw.sed.server.model.codelist.CProject;
import sk.qbsw.sed.server.model.domain.CUser;

/**
 * DAO interface to CUser
 * 
 * @see CProject
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 * 
 */
public interface IUserDao extends IDao<CUser> {
	public static final Long SYSTEM_USER = 0l;

	/**
	 * Finds user by login
	 * 
	 * @param login login to search
	 * @return unique user (login should be unique)
	 */
	public CUser findByLogin(String login);

	public CUser findClientAdministratorAccount(Long clientId);

	public CUser findClientReceptionAccount(Long clientId);

	/**
	 * Fnds user by autologin token
	 * 
	 * @param token
	 * @return
	 */
	public CUser findByAutoLoginToken(String token);

	/**
	 * Finds users with similar login
	 * 
	 * @param login
	 * @return
	 */
	public List<CUser> findSimilarToLogin(String login);

	/**
	 * FInds other main users for specified entity, type of user and not the same as
	 * user
	 * 
	 * @param user user to exclude from list (his attributes will be used for
	 *             search)
	 * @return
	 */
	public List<CUser> findOtherMain(CUser user);

	/**
	 * Finds subordinate users
	 * 
	 * @param user          user user to search subordinate to
	 * @param includeMe     true if owner should be included
	 * @param showOnlyValid reads only valid users
	 * @param userTypeId    type of user to read
	 * @return
	 */
	public List<CUser> findSubordinate(CUser user, boolean includeMe, boolean showOnlyValid, Long userTypeId);

	/**
	 * Returns employees of the target type
	 * 
	 * @param user          user for organization identify
	 * @param userTypeId    type of user to read
	 * @param showOnlyValid if we want only valid employees
	 * @return list of users
	 */
	public List<CUser> findAllEmployees(CUser user, Long userTypeId, Boolean showOnlyValid);

	/**
	 * Returns employees of the target type with valid flag value
	 * 
	 * @param clientId      clientId for organization identify
	 * @param showOnlyValid reads only valid users
	 * @param userTypeId    type of user to read
	 * @return list of users
	 */
	public List<CUser> findAllEmployeesByValidFlag(Long clientId, boolean showOnlyValid, Long userTypeId);

	/**
	 * Returns pinCodes of all all employees of the client
	 * 
	 * @param clientId client identifier
	 * @return list of pins
	 */
	public List<String> getPinCodes(Long clientId);

	/**
	 * Returns pin salts of all all employees of the client
	 * 
	 * @param clientId client identifier
	 * @return list of pin salts
	 */
	public List<String> getPinCodeSalts(Long clientId);

	public List<String> getCardCodes(Long clientId);

	public List<String> getCardCodeSalts(Long clientId);

	/**
	 * 
	 * Returns all users for selected client
	 * 
	 * @return
	 */
	public List<CUser> getClientUsers(Long clientId);

	/**
	 * Returns all users with <code>true</code> value of the flag_system_email
	 * 
	 * @param clientId selected client
	 * @return list of users
	 */
	public List<CUser> findBySystemEmailFlag(Long clientId);

	/**
	 * Returns all client users of selected type and/or valid flags that has not
	 * lock info records yet.
	 * 
	 * @param clientId selected client
	 * @param userType selected user type
	 * @param valid    (allowed values: <code>true</code> - only valid user records,
	 *                 <code>false</code> - only invalid user entities,
	 *                 <code>null<code> - both record types)
	 * @return list of users
	 */
	public List<CUser> findClientUsersWithoutLockInfoRecords(Long clientId, Long userType, Boolean valid);

	public List<CUser> findAllEmployeesForRestrictions(Long clientId, boolean b, Long employee, Integer startRow, Integer endRow, String sortProperty, boolean sortAsc, String name);

	public Long count(Long clientId, boolean b, Long employee, String name);

	public List<CUser> findEmloyeesToNotified(CUser owner);

	public List<CReportModel> findUsersRecordsForWorkplaceReport(Long clientId, Boolean onlyValid);
}
