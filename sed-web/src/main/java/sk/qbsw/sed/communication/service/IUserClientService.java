package sk.qbsw.sed.communication.service;

import java.util.List;

import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.codelist.CNotifyOfApprovedRequestContainer;
import sk.qbsw.sed.client.model.codelist.CUserSystemEmailContainer;
import sk.qbsw.sed.client.model.codelist.CUserSystemEmailRecord;
import sk.qbsw.sed.client.model.detail.CUserDetailRecord;
import sk.qbsw.sed.client.model.lock.CLockRecord;
import sk.qbsw.sed.client.model.tree.org.CViewOrganizationTreeNodeRecord;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.framework.security.exception.CSecurityException;
import sk.qbsw.sed.fw.exception.CBussinessDataException;

/**
 * Service for management of users
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 * 
 */
public interface IUserClientService {

	/**
	 * Adds new User
	 * 
	 * @param userToAdd user to add
	 * @throws CBussinessDataException
	 */
	public Long add(CUserDetailRecord userToAdd) throws CBussinessDataException;

	/**
	 * Reads user details
	 * 
	 * @param id
	 */
	public CUserDetailRecord getUserDetails(Long id) throws CBussinessDataException;

	/**
	 * Modifies specified user
	 * 
	 * @param id
	 * @param toModify
	 * @return
	 * @throws CBusinessException
	 * @throws CSecurityException
	 */
	public CLockRecord modify(CUserDetailRecord toModify) throws CBussinessDataException;

	/**
	 * Gets subordinate users of entered user
	 * 
	 * @throws CBusinessException
	 * @throws CSecurityException
	 */
	public List<CCodeListRecord> listSubordinateUsers(Boolean showOnlyValid, Boolean includeMe) throws CBussinessDataException;

	/**
	 * Gets all valid users of logged client user
	 * 
	 * @throws CBusinessException
	 * @throws CSecurityException
	 */
	public List<CCodeListRecord> getAllValidEmployees() throws CBussinessDataException;

	/**
	 * Returns
	 * 
	 * @return
	 * @throws CSecurityException
	 */
	public List<CUserSystemEmailRecord> getAccounts4SystemEmail() throws CBussinessDataException;

	/**
	 * 
	 * @param userRequestID
	 * @return
	 * @throws CBussinessDataException
	 */
	public List<CUserSystemEmailRecord> getAccounts4Notification(Long userRequestID) throws CBussinessDataException;

	/**
	 * 
	 * @param data
	 * @throws CSecurityException
	 * @throws CBusinessException
	 */
	public void saveSystemEmailAccounts(CUserSystemEmailContainer data) throws CBussinessDataException;

	/**
	 * 
	 * @param entityID
	 * @param data
	 * @throws CBussinessDataException
	 */
	public void saveNotifyOfApprovedRequest(CNotifyOfApprovedRequestContainer data) throws CBussinessDataException;

	/**
	 * Sets the project as user's favorite project or not.
	 * 
	 * @param projectId     - project identifier
	 * @param flagMyProject - favorite flag
	 * @param userId        - user identifier
	 * @throws CBussinessDataException
	 */
	public void modifyMyProjects(Long projectId, boolean flagMyProject, Long userId) throws CBussinessDataException;

	/**
	 * Sets the user as logged user's favorite user or not.
	 * 
	 * @param favouriteUserId - user identifier
	 * @param flagMyFavourite - favorite flag
	 * @param userId          - logged user identifier
	 * @throws CBussinessDataException
	 */
	public void modifyMyFavorites(Long favouriteUserId, boolean flagMyFavourite, Long userId) throws CBussinessDataException;

	/**
	 * Sets the project as user's favorite activity or not.
	 * 
	 * @param activityId    - activity identifier
	 * @param flagMyActivty - favorite flag
	 * @param userId        - user identifier
	 * @throws CBussinessDataException
	 */
	public void modifyMyActivities(Long activityId, boolean flagMyActivty, Long userId) throws CBussinessDataException;

	/**
	 * Renews password for entered login and email. New password will be send to the
	 * email address
	 * 
	 * @param login
	 * @param email
	 * @throws CSecurityException
	 */
	public void renewPassword(String login, String email) throws CBussinessDataException;

	/**
	 * Changes password of user
	 * 
	 * @param login
	 * @param originalPwd
	 * @param newPwd
	 * @throws CSecurityException
	 */
	public void changePassword(String login, String originalPwd, String newPwd) throws CBussinessDataException;

	/**
	 * Changes pin of user identified by login
	 * 
	 * @param login  - user identifier
	 * @param newPin - new pin
	 * @throws CSecurityException
	 */
	public void changePin(String login, String newPin) throws CBussinessDataException;

	/**
	 * Changes pin of user identified by id
	 * 
	 * @param userId - user identifier
	 * @param newPin - new pin
	 * @throws CBussinessDataException
	 */
	public void changePin(Long userId, String newPin) throws CBussinessDataException;

	/**
	 * Changes users card code.
	 * 
	 * @param userId   - user identifier
	 * @param cardCode - card code
	 * @throws CBussinessDataException
	 */
	public void changeCardCode(Long userId, String cardCode) throws CBussinessDataException;

	/**
	 * Returns list of types of employment
	 * 
	 * @throws CBusinessDataException
	 */
	public List<CCodeListRecord> getAllTypesOfEmployment() throws CBussinessDataException;

	public List<CCodeListRecord> getAllTypesOfHomeOfficePermission() throws CBussinessDataException;

	List<CViewOrganizationTreeNodeRecord> listNotifiedUsers(Boolean withSubordiantes) throws CBussinessDataException;
}
