package sk.qbsw.sed.client.service.business;

import java.util.List;

import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.codelist.CNotifyOfApprovedRequestContainer;
import sk.qbsw.sed.client.model.codelist.CUserSystemEmailContainer;
import sk.qbsw.sed.client.model.codelist.CUserSystemEmailRecord;
import sk.qbsw.sed.client.model.detail.CUserDetailRecord;
import sk.qbsw.sed.client.model.registration.CRegistrationUserRecord;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.model.tree.org.CViewOrganizationTreeNodeRecord;
import sk.qbsw.sed.client.ui.screen.restriction.users.CEmployeeRecord;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.framework.security.exception.CSecurityException;

/**
 * Service for management of users
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 * 
 */
public interface IUserService {
	
	/**
	 * Adds new User
	 * 
	 * @param userToAdd user to add
	 * @throws CBusinessException
	 */
	public void addByRegistration(CRegistrationUserRecord userToAdd) throws CBusinessException;

	/**
	 * Adds new User
	 * 
	 * @param userToAdd user to add
	 * @throws CBusinessException
	 * @throws CSecurityException
	 */
	public Long add(CUserDetailRecord userToAdd) throws CBusinessException;

	/**
	 * Reads user details
	 * 
	 * @param id
	 */
	public CUserDetailRecord getUserDetails(Long id) throws CBusinessException;

	/**
	 * Generated login
	 * 
	 * @param login login to check and use as tempalte
	 */
	public String generateLogin(String login) throws CBusinessException;

	/**
	 * Modifies specified user
	 * 
	 * @param id
	 * @param toModify
	 * @throws CBusinessException
	 * @throws CSecurityException
	 */
	public void modify(CUserDetailRecord toModify) throws CBusinessException;

	/**
	 * Gets subordinate users of entered user
	 * 
	 * @throws CBusinessException
	 * @throws CSecurityException
	 */
	public List<CCodeListRecord> listSubordinateUsers(boolean showOnlyValid, boolean includeMe) throws CBusinessException, CSecurityException;

	/**
	 * Gets logged user data as content of listbox
	 * 
	 * @throws CBusinessException
	 * @throws CSecurityException
	 */
	public List<CCodeListRecord> listLoggedUser() throws CSecurityException;

	/**
	 * Gets all valid users of logged client user
	 * 
	 * @throws CBusinessException
	 * @throws CSecurityException
	 */
	public List<CCodeListRecord> getAllValidEmployees() throws CSecurityException;

	/**
	 * Gets sorted all valid users of logged client user as specific list
	 * 
	 * @return
	 * @throws CSecurityException
	 */
	public List<CEmployeeRecord> getAllValidEmployeesList() throws CSecurityException;

	/**
	 * Returns
	 * 
	 * @return
	 * @throws CSecurityException
	 */
	public List<CUserSystemEmailRecord> getAccounts4SystemEmail() throws CSecurityException;

	/**
	 * 
	 * @return
	 * @throws CSecurityException
	 */
	public List<CUserSystemEmailRecord> getAccounts4Notification(Long UserId) throws CSecurityException;

	/**
	 * 
	 * @param data
	 * @throws CSecurityException
	 * @throws CBusinessException
	 */
	public void saveSystemEmailAccounts(CUserSystemEmailContainer data) throws CSecurityException, CBusinessException;

	/**
	 * 
	 * @param data
	 * @throws CSecurityException
	 * @throws CBusinessException
	 */
	public void saveNotifyOfApprovedRequest(CNotifyOfApprovedRequestContainer data) throws CSecurityException, CBusinessException;

	/**
	 * Sets the project as user's favorite project or not.
	 * 
	 * @param projectId     - project identifier
	 * @param flagMyProject - favorite flag
	 * @param userId        - user identifier
	 */
	void modifyMyProjects(Long projectId, boolean flagMyProject, Long userId);

	/**
	 * Sets the activity as user's favorite project or not.
	 * 
	 * @param activityId     - activity identifier
	 * @param flagMyActivity - favorite flag
	 * @param userId         - user identifier
	 */
	void modifyMyActivities(Long activityId, boolean flagMyActivity, Long userId);

	/**
	 * Sets the user as logged user's favorite user or not.
	 * 
	 * @param favouriteUserId - user identifier
	 * @param flagMyFavourite - favorite flag
	 * @param userId          - logged user identifier
	 */
	void modifyMyFavourites(Long favouriteUserId, boolean flagMyFavourite, Long userId);

	/**
	 * Renews password for entered login and email. New password will be send to the
	 * email address
	 * 
	 * @param login
	 * @param email
	 * @throws CSecurityException
	 */
	public void renewPassword(String login, String email) throws CSecurityException, CBusinessException;

	/**
	 * Changes password of user
	 * 
	 * @param login
	 * @param originalPwd
	 * @param newPwd
	 * @throws CSecurityException
	 */
	public void changePassword(String login, String originalPwd, String newPwd) throws CSecurityException, CBusinessException;

	/**
	 * Changes pin of user
	 * 
	 * @param login
	 * @param newPin
	 * @throws CSecurityException
	 */
	public void changePin(String login, String newPin) throws CBusinessException;

	/**
	 * Changes pin of user identified by id
	 * 
	 * @param userId - user identifier
	 * @param newPin - new pin
	 * @throws CBusinessException
	 */
	public void changePin(Long userId, String newPin) throws CBusinessException;

	/**
	 * Changes users card code.
	 * 
	 * @param userId   - user identifier
	 * @param cardCode - card code
	 * @throws CBusinessException
	 */
	public void changeCardCode(Long userId, String cardCode) throws CBusinessException;

	/**
	 * Gets information regarding logged user
	 * 
	 * @param pinCode - pin code
	 * @return CLoggedUserRecord
	 * @throws CSecurityException
	 * @throws CBusinessException
	 */
	public CLoggedUserRecord getUserInfo(String pinCode) throws CSecurityException, CBusinessException;

	/**
	 * Gets information about user defined by card code
	 * 
	 * @param cardCode
	 * @return CLoggedUserRecord
	 * @throws CSecurityException
	 * @throws CBusinessException
	 */
	public CLoggedUserRecord getUserInfoByCardCode(String cardCode) throws CSecurityException, CBusinessException;

	/**
	 * Gets list of users from array of pins
	 * 
	 * @return CLoggedUserRecord
	 */
	public List<CLoggedUserRecord> getUsersInfo(List<String> pins) throws CSecurityException, CBusinessException;

	/**
	 * Returns list of user informations
	 * 
	 * @param pins - list of pin codes
	 * @return list of CLoggedUserRecord
	 * @throws CSecurityException
	 * @throws CBusinessException
	 */
	public List<CLoggedUserRecord> getUsersInfoByCardCodes(List<String> pins) throws CSecurityException, CBusinessException;

	/**
	 * Gets all valid type of employment
	 * 
	 * @throws CSecurityException
	 */
	public List<CCodeListRecord> getAllTypeEmployment() throws CSecurityException;

	public List<CCodeListRecord> getAllTypesOfHomeOfficePermission() throws CSecurityException;

	List<CViewOrganizationTreeNodeRecord> listNotifiedUsers(Boolean withSubordiantes) throws CBusinessException;
}
