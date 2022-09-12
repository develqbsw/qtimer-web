package sk.qbsw.sed.server.service.business;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCache;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.model.ILanguageConstant;
import sk.qbsw.sed.client.model.IUserTypes;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.codelist.CNotifyOfApprovedRequestContainer;
import sk.qbsw.sed.client.model.codelist.CUserSystemEmailContainer;
import sk.qbsw.sed.client.model.codelist.CUserSystemEmailRecord;
import sk.qbsw.sed.client.model.detail.CUserDetailRecord;
import sk.qbsw.sed.client.model.registration.CRegistrationUserRecord;
import sk.qbsw.sed.client.model.security.CClientInfo;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.model.tree.org.CViewOrganizationTreeNodeRecord;
import sk.qbsw.sed.client.service.business.IUserService;
import sk.qbsw.sed.client.ui.component.panel.menu.IUserTypeCode;
import sk.qbsw.sed.client.ui.localization.CClientExceptionsMessages;
import sk.qbsw.sed.client.ui.screen.restriction.users.CEmployeeRecord;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.framework.security.exception.CSecurityException;
import sk.qbsw.sed.server.ISessionParametersKeys;
import sk.qbsw.sed.server.dao.IActivityDao;
import sk.qbsw.sed.server.dao.IClientDao;
import sk.qbsw.sed.server.dao.IEmploymentTypeDao;
import sk.qbsw.sed.server.dao.IHomeOfficePermissionDao;
import sk.qbsw.sed.server.dao.INotificationDao;
import sk.qbsw.sed.server.dao.IOrganizationTreeDao;
import sk.qbsw.sed.server.dao.IProjectDao;
import sk.qbsw.sed.server.dao.IUserDao;
import sk.qbsw.sed.server.dao.IUserPhotoDao;
import sk.qbsw.sed.server.dao.IUserTypeDao;
import sk.qbsw.sed.server.dao.IViewOrganizationTreeDao;
import sk.qbsw.sed.server.dao.IZoneDao;
import sk.qbsw.sed.server.exception.CServletSessionUtils;
import sk.qbsw.sed.server.ldap.CLdapAuthentication;
import sk.qbsw.sed.server.ldap.ILdapAuthenticationConfigurator;
import sk.qbsw.sed.server.model.codelist.CActivity;
import sk.qbsw.sed.server.model.codelist.CHomeOfficePermission;
import sk.qbsw.sed.server.model.codelist.CProject;
import sk.qbsw.sed.server.model.domain.CClient;
import sk.qbsw.sed.server.model.domain.CEmploymentType;
import sk.qbsw.sed.server.model.domain.COrganizationTree;
import sk.qbsw.sed.server.model.domain.CUser;
import sk.qbsw.sed.server.model.domain.CUserPhoto;
import sk.qbsw.sed.server.model.tree.org.CViewOrganizationTreeNode;
import sk.qbsw.sed.server.service.CEncryptUtils;
import sk.qbsw.sed.server.service.security.IPinCodeGeneratorBaseService;
import sk.qbsw.sed.server.service.system.IAppCodeGenerator;
import sk.qbsw.sed.server.util.CStringUtils;

/**
 * Service for management of users
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 */
@Service(value = "userService")
public class CUserServiceImpl implements IUserService {
	
	@Autowired
	private IClientDao clientDao;

	@Autowired
	private IUserPhotoDao userPhotoDao;

	@Autowired
	private IAppCodeGenerator appCodeGenerator;

	@Autowired
	private INotificationDao notificationDao;

	@Autowired
	private IOrganizationTreeDao organizationTreeDao;

	@Autowired
	private IUserDao userDao;

	@Autowired
	private IZoneDao zoneDao;

	@Autowired
	private IEmploymentTypeDao employmentTypeDao;

	@Autowired
	private IHomeOfficePermissionDao homeOfficePermissionDao;

	@Autowired
	private IProjectDao projectDao;

	@Autowired
	private IActivityDao activityDao;

	@Autowired
	private IUserTypeDao userTypeDao;

	@Autowired
	private IPinCodeGeneratorBaseService pinCodeGenerator;

	@Autowired
	private ILdapAuthenticationConfigurator ldapAuthenticationConfigurator;

	@Autowired
	private CacheManager cacheManager;

	@Autowired
	private IViewOrganizationTreeDao viewOrgTreeDao;
	
	private static final String RECEPCIA = "Recepcia";

	@Transactional(rollbackForClassName = "CBusinessException")
	public Long add(final CUserDetailRecord userToAdd) throws CBusinessException {

		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();

		if (userToAdd == null) {
			return null;
		}

		final CProject defaultProject = this.projectDao.findDefaultProject(loggedUser.getClientInfo().getClientId());
		final CActivity defaultActivity = this.activityDao.findDefaultActivity(loggedUser.getClientInfo().getClientId(), false);

		if (defaultActivity == null || defaultProject == null) { // neexistuje predvolená aktivita alebo projekt
			throw new CBusinessException(CClientExceptionsMessages.CANNOT_ADD_USER);
		} else if (defaultProject.getValid().booleanValue() == false || defaultActivity.getValid().booleanValue() == false) { // predvolená aktivita alebo projekt nie je platná
			throw new CBusinessException(CClientExceptionsMessages.CANNOT_ADD_USER);
		}
		// checks
		this.checkLoginUnique(userToAdd.getLogin());

		if (!userToAdd.getLogin().contains("@") && !existsUserInDomain(userToAdd.getLogin())) {
			throw new CBusinessException(CClientExceptionsMessages.USER_NOT_IN_DOMAIN);
		}

		// prepare parameters
		final CUser newUser = new CUser();

		final CUser changedBy = this.userDao.findById(loggedUser.getUserId());
		final Calendar changeTime = Calendar.getInstance();

		newUser.setChangedBy(changedBy);
		newUser.setChangeTime(changeTime);

		newUser.setLogin("" + Calendar.getInstance().getTime().getTime());

		newUser.setLoginLong(userToAdd.getLogin().toLowerCase());
		newUser.setName(userToAdd.getName());
		newUser.setSurname(userToAdd.getSurname());
		newUser.setPhone(userToAdd.getPhoneFix());
		newUser.setMobile(userToAdd.getPhoneMobile());
		newUser.setEmail(userToAdd.getEmail());

		newUser.setEmployeeCode(userToAdd.getEmployeeCode());
		newUser.setValid(userToAdd.getIsValid());
		newUser.setMain(userToAdd.getIsMain());
		newUser.setEditTime(userToAdd.getEditTime());
		newUser.setAbsentCheck(userToAdd.getAbsentCheck());
		newUser.setCriminalRecords(userToAdd.getCriminalRecords());
		newUser.setRecMedicalCheck(userToAdd.getRecMedicalCheck());
		newUser.setMultisportCard(userToAdd.getMultisportCard());
		newUser.setJiraTokenGeneration(userToAdd.getJiraTokenGeneration());
		newUser.setAllowedAlertnessWork(userToAdd.getAllowedAlertnessWork());

		newUser.setStreet(userToAdd.getStreet());
		newUser.setStreetNumber(userToAdd.getStreetNumber());

		if (userToAdd.getCity() != null) {
			newUser.setCity(userToAdd.getCity());
		}

		newUser.setZip(userToAdd.getZip());
		newUser.setCountry(userToAdd.getCountry());
		newUser.setNote(userToAdd.getNote());

		if (userToAdd.getPhoto() != null) {
			CUserPhoto userPhoto = new CUserPhoto();
			userPhoto.setPhoto(userToAdd.getPhoto());
			userPhotoDao.saveOrUpdate(userPhoto);
			newUser.setPhoto(userPhoto);
		} else {
			CUserPhoto userPhoto = userPhotoDao.findById(-1L); // default photo
			newUser.setPhoto(userPhoto);
		}

		if (userToAdd.getTableRows() == null) {
			newUser.setTableRows(10);
		} else {
			newUser.setTableRows(userToAdd.getTableRows());
		}

		COrganizationTree treeNode = null;
		if (userToAdd.getSuperiorId() != null) {
			treeNode = this.organizationTreeDao.findParentTree(userToAdd.getSuperiorId()); // get node of superior
		}

		// referencies
		newUser.setType(this.userTypeDao.findById(userToAdd.getUserType()));
		if (loggedUser.getRoles().contains(IUserTypeCode.ID_ORG_ADMIN)) {
			// if we can read from the tree use client from tree
			if (treeNode != null) {
				newUser.setClient(treeNode.getClient());
			}
			// otherwise read client from user
			else {
				newUser.setClient(this.clientDao.findById(loggedUser.getClientInfo().getClientId()));
			}
		} else {
			newUser.setClient(this.clientDao.findById(loggedUser.getClientInfo().getClientId()));
		}

		if (userToAdd.getLanguage() == null) {
			// Pri zakladani pouzivatela nebol vybrany jazyk
			// SED-370 Pri zakladaní používatela jazyk nastavovat default podla organizácie
			newUser.setLanguage(newUser.getClient().getLanguage());
		} else {
			// Ak pri zakladani bol vybrany jazyk tak nastavim ten
			newUser.setLanguage(convertCodeToLanguage(userToAdd.getLanguage()));
		}

		String pinCode = generateUserPIN(newUser);

		if (userToAdd.getLogin().contains("@")) {
			// generates new password
			final String newPassword = generateUserPassword(newUser);

			// send the password
			this.notificationDao.sendRenewPassword(newUser, newPassword, pinCode, true);
		} else {
			this.notificationDao.sendNewUserWithoutPassword(newUser, pinCode);
		}

		// creates new position
		final COrganizationTree newPosition = new COrganizationTree();
		newPosition.setChangedBy(changedBy);
		newPosition.setChangeTime(changeTime);
		newPosition.setClient(newUser.getClient());
		newPosition.setOwner(newUser);
		newPosition.setPossition(userToAdd.getPosition());
		newPosition.setSuperior(treeNode);

		// just needed for check
		newUser.getOrganizationTreesOwned().add(newPosition);

		// for add - use default value
		newUser.setReceiverSystemEmail(Boolean.FALSE);

		// check of hierarchy
		this.checkDisbaleOfUserAndParent(newUser);

		newUser.setZone(userToAdd.getZone() == null ? null : zoneDao.findById(userToAdd.getZone()));
		newUser.setOfficeNumber(userToAdd.getOfficeNumber());

		if (userToAdd.getBirthDate() != null) {
			Calendar birthDate = Calendar.getInstance();
			birthDate.setTime(userToAdd.getBirthDate());
			newUser.setBirthDate(birthDate);
		}

		if (userToAdd.getWorkStartDate() != null) {
			Calendar workStartDate = Calendar.getInstance();
			workStartDate.setTime(userToAdd.getWorkStartDate());
			newUser.setWorkStartDate(workStartDate);
		}

		newUser.setVacation(userToAdd.getVacation(), changedBy);
		newUser.setVacationNextYear(userToAdd.getVacationNextYear(), changedBy);

		newUser.setIdentificationNumber(userToAdd.getPersonalIdNumber());
		newUser.setCrn(userToAdd.getCrn());
		newUser.setVatin(userToAdd.getVatin());

		if (userToAdd.getTypeOfEmployment() != null) {
			CEmploymentType empType = this.employmentTypeDao.findById(userToAdd.getTypeOfEmployment());
			newUser.setEmploymentType(empType);
		} else {
			CEmploymentType empType = new CEmploymentType();
			empType.setId(Long.valueOf(1)); // ak je employment type null tak prednastavím 1 = Zamestnanec
			newUser.setEmploymentType(empType);
		}

		if (userToAdd.getWorkEndDate() != null) {
			Calendar workEndDate = Calendar.getInstance();
			workEndDate.setTime(userToAdd.getWorkEndDate());
			newUser.setWorkEndDate(workEndDate);
		} else {
			newUser.setWorkEndDate(null);
		}

		newUser.setTitle(userToAdd.getDegTitle());
		newUser.setResidentIdentityCardNumber(userToAdd.getResidentIdCardNum());
		newUser.setHealthInsuranceCompany(userToAdd.getHealthInsurComp());
		newUser.setBankAccountNumber(userToAdd.getBankAccountNumber());
		newUser.setBankInstitution(userToAdd.getBankInstitution());
		newUser.setCountry(userToAdd.getCountry());
		newUser.setBirthPlace(userToAdd.getBirthPlace());

		newUser.setPositionName(userToAdd.getPosition());

		if (userToAdd.getHomeOfficePermission() != null) {
			CHomeOfficePermission homeOfficePermission = this.homeOfficePermissionDao.findById(userToAdd.getHomeOfficePermission());
			newUser.setHomeOfficePermission(homeOfficePermission);
		} else {
			CHomeOfficePermission homeOfficePermission = new CHomeOfficePermission();
			homeOfficePermission.setId(Long.valueOf(2)); // ak je home office permission null tak
			// prednastavím 2 = Len so žiadosťou
			newUser.setHomeOfficePermission(homeOfficePermission);
		}

		// changes position
		this.userDao.saveOrUpdate(newUser);
		this.organizationTreeDao.saveOrUpdate(newPosition);

		return newUser.getId();
	}

	@Transactional(rollbackForClassName = "CBusinessException")
	public void addByRegistration(final CRegistrationUserRecord userToAdd) throws CBusinessException {
		if (userToAdd == null) {
			return;
		}

		// checks
		this.checkLoginUnique(userToAdd.getLogin() + ".admin"); // check by login, this is
		// user that register
		// the new client

		// registration: new user
		final CUser newUser = new CUser();

		newUser.setReceiverSystemEmail(Boolean.FALSE);
		newUser.setAllowedAlertnessWork(Boolean.FALSE);

		newUser.setChangedBy(this.userDao.findById(IUserDao.SYSTEM_USER));
		newUser.setChangeTime(Calendar.getInstance());

		newUser.setLogin("" + Calendar.getInstance().getTime().getTime());
		newUser.setLoginLong(userToAdd.getLogin().toLowerCase() + ".admin");

		// admin password:
		String adminPasswordSalt = this.appCodeGenerator.generatePasswordSalt();
		String adminPassword = userToAdd.getPassword();
		String encyptedAdminPassword = CEncryptUtils.getHash(adminPassword, adminPasswordSalt);
		newUser.setPassword(encyptedAdminPassword);
		newUser.setPasswordSalt(adminPasswordSalt);

		newUser.setEmail(userToAdd.getEmail());
		newUser.setName(userToAdd.getName());
		newUser.setSurname(userToAdd.getSurname());
		newUser.setPhone(userToAdd.getPhoneFix());
		newUser.setClient(this.clientDao.findById(userToAdd.getClientInfo().getClientId()));
		newUser.setValid(userToAdd.getIsValid());
		newUser.setMain(userToAdd.getIsMain());
		newUser.setType(this.userTypeDao.findById(userToAdd.getUserType()));
		newUser.setEditTime(Boolean.FALSE);
		// SED-794
		newUser.setAbsentCheck(Boolean.FALSE);
		newUser.setCriminalRecords(Boolean.FALSE);
		newUser.setRecMedicalCheck(Boolean.FALSE);
		newUser.setMultisportCard(Boolean.FALSE);
		newUser.setJiraTokenGeneration(Boolean.FALSE);
		
		// SED-370 Pri zakladani pouzivatela jazyk nastavovat default podla organizacie
		newUser.setLanguage(newUser.getClient().getLanguage());
		newUser.setTableRows(Integer.valueOf(10));

		// SED-823
		CHomeOfficePermission adminHomeOfficePermission = new CHomeOfficePermission();
		adminHomeOfficePermission.setId(Long.valueOf(3));
		newUser.setHomeOfficePermission(adminHomeOfficePermission);

		CUserPhoto photo = new CUserPhoto();
		photo.setId(-1L);
		newUser.setPhoto(photo);
		newUser.setEmployeeCode("0");

		this.userDao.saveOrUpdate(newUser);

		// creation a new user for reception
		final CUser receptionUser = new CUser();

		receptionUser.setReceiverSystemEmail(Boolean.FALSE);
		receptionUser.setAllowedAlertnessWork(Boolean.FALSE);
		receptionUser.setChangedBy(this.userDao.findById(IUserDao.SYSTEM_USER));
		receptionUser.setChangeTime(Calendar.getInstance());
		receptionUser.setLogin("" + Calendar.getInstance().getTime().getTime() + 1);
		receptionUser.setLoginLong(generateReceptionLogin(newUser));

		String receptionPasswordSalt = this.appCodeGenerator.generatePasswordSalt();
		String receptionPassword = this.appCodeGenerator.generateMessage(8);
		String encyptedReceptionPassword = CEncryptUtils.getHash(receptionPassword, receptionPasswordSalt);
		receptionUser.setPasswordSalt(receptionPasswordSalt);
		receptionUser.setPassword(encyptedReceptionPassword);
		receptionUser.setEmail(userToAdd.getEmail());// use the same email as currently registered client!
		receptionUser.setName(RECEPCIA);
		receptionUser.setSurname("");
		receptionUser.setPhone(userToAdd.getPhoneFix());
		receptionUser.setClient(this.clientDao.findById(userToAdd.getClientInfo().getClientId()));
		receptionUser.setValid(Boolean.TRUE);
		receptionUser.setMain(Boolean.FALSE);
		receptionUser.setType(this.userTypeDao.findById(5l)); // USERTYPE_RECEPTION
		receptionUser.setEditTime(Boolean.FALSE);
		// SED-794
		receptionUser.setAbsentCheck(Boolean.FALSE);
		receptionUser.setCriminalRecords(Boolean.FALSE);
		receptionUser.setRecMedicalCheck(Boolean.FALSE);
		receptionUser.setMultisportCard(Boolean.FALSE);
		receptionUser.setJiraTokenGeneration(Boolean.FALSE);

		// SED-823
		CHomeOfficePermission homeOfficePermission = new CHomeOfficePermission();
		homeOfficePermission.setId(Long.valueOf(3));
		receptionUser.setHomeOfficePermission(homeOfficePermission);

		// SED-370 Pri zakladani pouzivatela jazyk nastavovat default podla organizacie
		receptionUser.setLanguage(receptionUser.getClient().getLanguage());
		receptionUser.setTableRows(Integer.valueOf(10));

		receptionUser.setPhoto(photo);

		this.userDao.saveOrUpdate(receptionUser);

		// send the password
		this.notificationDao.sendGeneratedPassword(newUser, receptionUser, adminPassword, receptionPassword);
	}

	private String generateReceptionLogin(CUser user) {
		// This is the simplest way. If necessary, try another one.
		return user.getLoginLong() + "_r";
	}

	/**
	 * Converts db model to client model;
	 * 
	 * @param item item from db
	 * @return user model
	 */
	private CCodeListRecord convert(final CUser item) {
		final CCodeListRecord newRec = new CCodeListRecord();
		newRec.setId(item.getId());
		newRec.setName(item.getSurname() + " " + item.getName());
		newRec.setDescription(item.getSurname() + " " + item.getName());

		final List<COrganizationTree> org = item.getOrganizationTreesOwned();
		if ((org != null) && (!org.isEmpty())) {
			newRec.setType(org.get(0).getPossition());
		} else {
			newRec.setType("");
		}

		return newRec;
	}

	/**
	 * Converts db model to client model
	 * 
	 * @param input
	 * @return Converted CodeList values
	 */
	private List<CCodeListRecord> convert(final List<CUser> input) {
		final List<CCodeListRecord> retVal = new ArrayList<>();
		for (final CUser item : input) {
			final CCodeListRecord newRec = this.convert(item);
			retVal.add(newRec);
		}
		return retVal;
	}

	/**
	 * Converts user to client representation
	 * 
	 * @param user
	 * @return
	 */
	private CUserDetailRecord convertToDetailRecord(final CUser user) {
		// user registration record
		final CUserDetailRecord userDetail = new CUserDetailRecord();
		final CClientInfo clientInfo = new CClientInfo();

		clientInfo.setClientId(user.getClient().getId());
		clientInfo.setClientName(user.getClient().getNameShort());
		clientInfo.setProjectRequired(user.getClient().getProjectRequired());
		clientInfo.setActivityRequired(user.getClient().getActivityRequired());

		userDetail.setClientInfo(clientInfo);
		userDetail.setEmail(user.getEmail());
		userDetail.setIsMain(user.getMain());
		userDetail.setIsValid(user.getValid());
		userDetail.setLogin(user.getLoginLong());
		userDetail.setName(user.getName());
		userDetail.setPhoneFix(user.getPhone());
		userDetail.setSurname(user.getSurname());
		userDetail.setUserType(user.getType().getId());
		userDetail.setHomeOfficePermission(user.getHomeOfficePermission().getId());

		// detail
		if (user.getCity() != null) {
			userDetail.setCity(user.getCity());
		} else {
			userDetail.setCity(null);
		}

		userDetail.setPhoneMobile(user.getMobile());

		// defaultne hodnoty
		userDetail.setSuperiorName("");
		userDetail.setSuperiorSurname("");

		userDetail.setSuperiorPosition("");
		userDetail.setPosition(user.getPositionName());

		// nacitaj zo stromovej struktury
		final List<COrganizationTree> trees = user.getOrganizationTreesOwned();
		if ((trees != null) && (!trees.isEmpty())) {
			final COrganizationTree treeNode = user.getOrganizationTreesOwned().get(0);

			/*
			 * SED-802 Pozícia sa nemá setovať z tabuľky t_organization_tree ale z tabuľky
			 * t_user, čo sa už robí vyššie userDetail.setPosition(treeNode.getPossition())
			 */

			final COrganizationTree superiorNode = treeNode.getSuperior();
			if (superiorNode != null) {
				final CUser superUser = superiorNode.getOwner();

				if (superUser != null) {
					userDetail.setSuperiorPosition(superUser.getPositionName());
					userDetail.setSuperiorName(superUser.getName());
					userDetail.setSuperiorSurname(superUser.getSurname());
					userDetail.setSuperiorId(superUser.getId());
				}
			}
		}

		userDetail.setUserTypeString(user.getType().getDescription());
		userDetail.setEmployeeCode(user.getEmployeeCode());
		userDetail.setNote(user.getNote());
		userDetail.setPhoto(user.getPhoto().getPhoto());
		userDetail.setPhotoId(user.getPhoto().getId());
		userDetail.setStreet(user.getStreet());
		userDetail.setStreetNumber(user.getStreetNumber());
		if (user.getCity() != null) {
			userDetail.setCity(user.getCity());
		} else {
			userDetail.setCity(null);
		}

		userDetail.setCountry(user.getCountry());
		userDetail.setZip(user.getZip());

		userDetail.setEditTime(user.getEditTime());
		userDetail.setAllowedAlertnessWork(user.getAllowedAlertnessWork());
		userDetail.setLanguage(convertLanguageToCode(user.getLanguage()));
		userDetail.setZone(user.getZone() == null ? null : user.getZone().getId());
		userDetail.setOfficeNumber(user.getOfficeNumber());

		userDetail.setId(user.getId());
		userDetail.setTableRows(user.getTableRows());
		userDetail.setBirthDate(user.getBirthDate() == null ? null : user.getBirthDate().getTime());
		userDetail.setWorkStartDate(user.getWorkStartDate() == null ? null : user.getWorkStartDate().getTime());
		userDetail.setVacation(user.getVacation());
		userDetail.setVacationNextYear(user.getVacationNextYear());

		userDetail.setPersonalIdNumber(user.getIdentificationNumber());
		userDetail.setCrn(user.getCrn());
		userDetail.setVatin(user.getVatin());
		userDetail.setTypeOfEmployment(user.getEmploymentType() == null ? null : user.getEmploymentType().getId());
		userDetail.setWorkEndDate(user.getWorkEndDate() == null ? null : user.getWorkEndDate().getTime());
		userDetail.setDegTitle(user.getTitle());
		userDetail.setResidentIdCardNum(user.getResidentIdentityCardNumber());
		userDetail.setHealthInsurComp(user.getHealthInsuranceCompany());
		userDetail.setBankAccountNumber(user.getBankAccountNumber());
		userDetail.setBankInstitution(user.getBankInstitution());
		userDetail.setCountry(user.getCountry());
		userDetail.setBirthPlace(user.getBirthPlace());
		userDetail.setAbsentCheck(user.getAbsentCheck());
		userDetail.setCriminalRecords(user.getCriminalRecords());
		userDetail.setRecMedicalCheck(user.getRecMedicalCheck());
		userDetail.setMultisportCard(user.getMultisportCard());
		userDetail.setJiraTokenGeneration(user.getJiraTokenGeneration());

		return userDetail;
	}

	private Long convertLanguageToCode(String language) {
		if (ILanguageConstant.SK.equals(language)) {
			return ILanguageConstant.ID_SK;
		} else if (ILanguageConstant.EN.equals(language)) {
			return ILanguageConstant.ID_EN;
		} else {
			return -1L;
		}
	}

	/**
	 * Gets user's detail
	 */
	@Transactional(readOnly = true)
	public CUserDetailRecord getUserDetails(final Long id) throws CBusinessException {
		final CUser user = this.userDao.findById(id);

		return this.convertToDetailRecord(user);
	}

	/**
	 * Checks if children users are active (checks only 1.lower level)
	 * 
	 * @param userToCheck
	 * @throws CBusinessException
	 */
	private void checkDisbaleOfUserAndChildren(final CUser userToCheck) throws CBusinessException {
		final Boolean userValidity = userToCheck.getValid();
		if (!userValidity) {
			final List<COrganizationTree> trees = userToCheck.getOrganizationTreesOwned();
			for (final COrganizationTree tree : trees) {
				final List<COrganizationTree> treeSubordinates = tree.getOrganizationTreesSubordinate();

				for (final COrganizationTree checkNode : treeSubordinates) {
					if (checkNode.getOwner().getValid()) {
						throw new CBusinessException(CClientExceptionsMessages.USER_CHILDREN_DISABLED);
					}
				}
			}
		}
	}

	/**
	 * Checks if parent users are not blocked and I am unblocked (checks only
	 * 1.higher level)
	 * 
	 * @param userToCheck
	 * @throws CBusinessException
	 */
	private void checkDisbaleOfUserAndParent(final CUser userToCheck) throws CBusinessException {
		final Boolean userValidity = userToCheck.getValid();
		if (userValidity) {
			final List<COrganizationTree> positions = userToCheck.getOrganizationTreesOwned();
			for (final COrganizationTree position : positions) {
				final COrganizationTree nodeSuperior = position.getSuperior();

				if ((nodeSuperior != null) && !nodeSuperior.getOwner().getValid()) {
					throw new CBusinessException(CClientExceptionsMessages.USER_PARENT_DISABLED);
				}
			}
		}
	}

	/**
	 * Checks existence of user on DB
	 * 
	 * @param login
	 */
	@Transactional(readOnly = true)
	public void checkLoginUnique(String login) throws CBusinessException {
		if (login != null) {
			login = login.toLowerCase();
		}

		final CUser user = this.userDao.findByLogin(login);

		if (user != null) {
			throw new CBusinessException(CClientExceptionsMessages.LOGIN_USED);
		}
	}

	/**
	 * Checks existence of user on DB if the user with entered ID has the same
	 * login, it's ok.
	 * 
	 * @param login  login which will be checked
	 * @param userId id of user who's data is being edited
	 */
	private void checkLoginUnique(String login, final Long userId) throws CBusinessException {
		if (login != null) {
			login = login.toLowerCase();
		}

		final CUser userByLogin = this.userDao.findByLogin(login);

		// user exists and is not equals to userId
		if ((userByLogin != null) && (!userByLogin.getId().equals(userId))) {
			throw new CBusinessException(CClientExceptionsMessages.LOGIN_USED);
		}
	}

	/**
	 * Checks if except entered user exists user in the system other main user
	 * 
	 * @param user user to check
	 */
	private void checkOtherMainExistence(final CUser user, final CUserDetailRecord userToModify) throws CBusinessException {
		// check just if the user want to be not main
		if (!userToModify.getIsMain()) {
			if (user.getType().getId().equals(IUserTypes.ORG_MAN) || user.getType().getId().equals(IUserTypes.SYS_MAN)) {
				if (this.userDao.findOtherMain(user).isEmpty()) {
					throw new CBusinessException(CClientExceptionsMessages.USER_NO_OTHER_MAIN_USER);
				}
			}
		}
	}

	/**
	 * Reads current user to listbox
	 * 
	 * @throws CSecurityException
	 * 
	 * @see IUserService#listLoggedUser()
	 */
	@Transactional(readOnly = true)
	public List<CCodeListRecord> listLoggedUser() throws CSecurityException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		final CCodeListRecord record = this.convert(this.userDao.findById(loggedUser.getUserId()));

		final ArrayList<CCodeListRecord> retVal = new ArrayList<>();
		retVal.add(record);
		return retVal;
	}

	/**
	 * Reads subordinate employees to current user
	 * 
	 * @see IUserService#listSubordinateUsers()
	 */
	@Override
	@Transactional(readOnly = true)
	public List<CCodeListRecord> listSubordinateUsers(boolean showOnlyValid, boolean includeMe) throws CBusinessException {
		Logger.getLogger(this.getClass()).info("Reading users");
		final CLoggedUserRecord loggedUserRecord = CServletSessionUtils.getLoggedUser();
		final CUser loggedUser = this.userDao.findById(loggedUserRecord.getUserId());
		final CUser adminUser = this.userDao.findClientAdministratorAccount(loggedUser.getClient().getId());

		List<CUser> subordinates;
		// check administrator request
		if (loggedUser.getId().equals(adminUser.getId())) {
			// udaje ziada administratoer - dostane vsetkych zamestnancov
			subordinates = this.userDao.findAllEmployeesByValidFlag(loggedUserRecord.getClientInfo().getClientId(), showOnlyValid, IUserTypes.EMPLOYEE);
		} else {
			subordinates = this.userDao.findSubordinate(loggedUser, includeMe, showOnlyValid, IUserTypes.EMPLOYEE);
		}
		Logger.getLogger(this.getClass()).info("Read " + subordinates.size() + " users");
		return this.convert(subordinates);
	}

	/**
	 * @throws CSecurityException
	 * @see IUserService#modify(Long, CUserDetailRecord)
	 */
	@Transactional(rollbackForClassName = "CBusinessException")
	public void modify(final CUserDetailRecord toModify) throws CBusinessException {

		final CUser user = this.userDao.findById(toModify.getId());

		if (user == null) {
			throw new CBusinessException("User not found");
		}

		// vyhod staru fotku z cache
		Cache cache = cacheManager.getCache("photoFindCache");
		if (cache instanceof EhCacheCache) {
			EhCacheCache cacheE = (EhCacheCache) cache;
			cacheE.getNativeCache().remove(user.getPhoto().getId());
		}

		// checks
		this.checkLoginUnique(toModify.getLogin(), toModify.getId());

		// ak je neplatny nekontrolujem
		if (toModify.getIsValid() && !toModify.getLogin().contains("@") && !toModify.getLogin().contains("admin") && !existsUserInDomain(toModify.getLogin())) {
			throw new CBusinessException(CClientExceptionsMessages.USER_NOT_IN_DOMAIN);
		}

		// ak idem zneplatnit usera, potrebujem vymazat veci zo vztaovej tabulky
		// SED-849
		if (!toModify.getIsValid()) {
			user.getAddNotified().clear();
			user.getNotified().clear();
		}

		this.checkOtherMainExistence(user, toModify);

		// common modification values
		final Calendar changeTime = Calendar.getInstance();
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		final CUser changedBy = this.userDao.findById(loggedUser.getUserId());

		final String oldLogin = user.getLoginLong();

		// modification of user
		user.setLogin("" + Calendar.getInstance().getTime().getTime());
		user.setLoginLong(toModify.getLogin().toLowerCase());
		user.setName(toModify.getName());
		user.setSurname(toModify.getSurname());
		user.setPhone(toModify.getPhoneFix());
		user.setMobile(toModify.getPhoneMobile());
		user.setEmail(toModify.getEmail());
		user.setEmployeeCode(toModify.getEmployeeCode());
		user.setValid(toModify.getIsValid());
		user.setNote(toModify.getNote());
		user.setStreet(toModify.getStreet());
		user.setStreetNumber(toModify.getStreetNumber());

		if (toModify.getCity() != null) {
			user.setCity(toModify.getCity());
		} else {
			user.setCity(null);
		}
		user.setZip(toModify.getZip());
		user.setCountry(toModify.getCountry());
		user.setMain(toModify.getIsMain());
		user.setTableRows(toModify.getTableRows());

		CUserPhoto oldUserPhoto = user.getPhoto();

		boolean newPhotoWasSelected = null != toModify.getPhoto(); // foto sa
		// zmenila

		if (newPhotoWasSelected) {
			// vytvorim novu lebo chcem nove id
			CUserPhoto userPhoto = new CUserPhoto();
			userPhoto.setPhoto(toModify.getPhoto());
			userPhotoDao.saveOrUpdate(userPhoto);
			user.setPhoto(userPhoto);
			// zmazem stare photo ak nemal default foto
			if (oldUserPhoto.getId() != -1L) {
				userPhotoDao.deleteById(oldUserPhoto.getId());
			}
		}

		user.setChangedBy(changedBy);
		user.setChangeTime(changeTime);

		// modification of tree
		final List<COrganizationTree> treeNodes = user.getOrganizationTreesOwned();

		// na zaklade modelu treeNodes nema ako byt null...
		COrganizationTree orgNode;
		if (treeNodes.isEmpty()) {
			orgNode = new COrganizationTree();
			orgNode.setClient(user.getClient());
			orgNode.setOwner(user);

			user.getOrganizationTreesOwned().add(orgNode);
		} else {
			orgNode = treeNodes.get(0);
		}
		orgNode.setPossition(toModify.getPosition());

		orgNode.setChangeTime(Calendar.getInstance());
		orgNode.setChangedBy(changedBy);

		// check ofUser childrens
		this.checkDisbaleOfUserAndChildren(user);
		this.checkDisbaleOfUserAndParent(user);

		// regenerate password
		if (toModify.getIsValid() && !oldLogin.equals(toModify.getLogin())) {
			if (toModify.getLogin().contains("@")) {
				// generates new password
				final String newPassword = generateUserPassword(user);

				// send the password
				this.notificationDao.sendRenewPassword(user, newPassword, null, true);
			} else {
				this.notificationDao.sendNewUserWithoutPassword(user, null);
			}
		}

		user.setEditTime(toModify.getEditTime());
		user.setAllowedAlertnessWork(toModify.getAllowedAlertnessWork());

		user.setLanguage(convertCodeToLanguage(toModify.getLanguage()));

		user.setZone(toModify.getZone() == null ? null : zoneDao.findById(toModify.getZone()));
		user.setOfficeNumber(toModify.getOfficeNumber());

		if (toModify.getBirthDate() != null) {
			Calendar birthDate = Calendar.getInstance();
			birthDate.setTime(toModify.getBirthDate());
			user.setBirthDate(birthDate);
		} else {
			user.setBirthDate(null);
		}

		if (toModify.getWorkStartDate() != null) {
			Calendar workStartDate = Calendar.getInstance();
			workStartDate.setTime(toModify.getWorkStartDate());
			user.setWorkStartDate(workStartDate);
		} else {
			user.setWorkStartDate(null);
		}

		user.setVacation(toModify.getVacation(), changedBy);
		user.setVacationNextYear(toModify.getVacationNextYear(), changedBy);

		// SED-590
		if (!toModify.getIsValid()) {
			user.setPinCode(null);
			user.setCardCode(null);
		}

		user.setIdentificationNumber(toModify.getPersonalIdNumber());
		user.setCrn(toModify.getCrn());
		user.setVatin(toModify.getVatin());

		if (toModify.getTypeOfEmployment() != null) {
			CEmploymentType empType = this.employmentTypeDao.findById(toModify.getTypeOfEmployment());
			user.setEmploymentType(empType);
		} else {
			CEmploymentType empType = new CEmploymentType();
			empType.setId(Long.valueOf(1)); // ak je employment type null tak prednastavím 1 = Zamestnanec
			user.setEmploymentType(empType);
		}

		if (toModify.getWorkEndDate() != null) {
			Calendar workEndDate = Calendar.getInstance();
			workEndDate.setTime(toModify.getWorkEndDate());
			user.setWorkEndDate(workEndDate);
		} else {
			user.setWorkEndDate(null);
		}

		user.setTitle(toModify.getDegTitle());
		user.setResidentIdentityCardNumber(toModify.getResidentIdCardNum());
		user.setHealthInsuranceCompany(toModify.getHealthInsurComp());
		user.setBankAccountNumber(toModify.getBankAccountNumber());
		user.setBankInstitution(toModify.getBankInstitution());
		user.setCountry(toModify.getCountry());
		user.setBirthPlace(toModify.getBirthPlace());
		user.setAbsentCheck(toModify.getAbsentCheck());
		user.setCriminalRecords(toModify.getCriminalRecords());
		user.setRecMedicalCheck(toModify.getRecMedicalCheck());
		user.setMultisportCard(toModify.getMultisportCard());
		user.setJiraTokenGeneration(toModify.getJiraTokenGeneration());
		
		user.setPositionName(toModify.getPosition());

		if (toModify.getHomeOfficePermission() != null) {
			CHomeOfficePermission homeOfficePermission = this.homeOfficePermissionDao.findById(toModify.getHomeOfficePermission());
			user.setHomeOfficePermission(homeOfficePermission);
		} else {
			CHomeOfficePermission homeOfficePermission = new CHomeOfficePermission();
			homeOfficePermission.setId(Long.valueOf(2)); // ak je home office permission null tak
			// prednastavím 2 = Len so žiadosťou
			user.setHomeOfficePermission(homeOfficePermission);
		}

		this.organizationTreeDao.saveOrUpdate(orgNode);
		this.userDao.saveOrUpdate(user);
	}

	private String convertCodeToLanguage(Long code) {
		if (ILanguageConstant.ID_SK.equals(code)) {
			return ILanguageConstant.SK;
		} else if (ILanguageConstant.ID_EN.equals(code)) {
			return ILanguageConstant.EN;
		} else {
			return "";
		}
	}

	@Transactional
	public String generateLogin(String login) throws CBusinessException {
		login = CStringUtils.convertNonAscii(login);
		final List<CUser> users = this.userDao.findSimilarToLogin(login);

		final List<String> usedLogins = new ArrayList<>();
		for (final CUser cUser : users) {
			usedLogins.add(cUser.getLoginLong());
		}

		int i = 0;
		String newLogin = login;
		boolean shouldSearch;
		do {
			if (i > 0) {
				newLogin = login + i;
			}
			shouldSearch = usedLogins.contains(newLogin);
		} while ((i++ <= 100) && shouldSearch);

		return shouldSearch ? "" : newLogin;
	}

	// look to CUserServiceImpl.generateUserPassword
	private String generateUserPassword(CUser user) throws CBusinessException {
		String newPasswordSalt = user.getPasswordSalt();
		if (newPasswordSalt == null) {
			newPasswordSalt = this.appCodeGenerator.generatePasswordSalt();
			user.setPasswordSalt(newPasswordSalt);
		}
		final String newPassword = this.appCodeGenerator.generatePassword();

		String encryptedNewPassword = CEncryptUtils.getHash(newPassword, newPasswordSalt);
		user.setPassword(encryptedNewPassword);

		return newPassword;
	}

	private String generateUserPIN(CUser user) throws CBusinessException {
		String newPinCodeSalt = user.getPinCodeSalt();
		if (newPinCodeSalt == null) {
			newPinCodeSalt = this.appCodeGenerator.generatePinSalt();
			user.setPinCodeSalt(newPinCodeSalt);
		}
		final String newPIN = this.pinCodeGenerator.generatePinCode(4, user, this.userDao.getPinCodes(user.getClient().getId()), this.userDao.getPinCodeSalts(user.getClient().getId()));

		String encryptedNewPIN = CEncryptUtils.getHash(newPIN, newPinCodeSalt);
		user.setPinCode(encryptedNewPIN);

		return newPIN;
	}

	@Transactional(readOnly = true)
	public List<CCodeListRecord> getAllValidEmployees() throws CSecurityException {
		final CLoggedUserRecord loggedUserInfo = CServletSessionUtils.getLoggedUser();
		List<CUser> users = this.userDao.findAllEmployeesByValidFlag(loggedUserInfo.getClientInfo().getClientId(), true, IUserTypes.EMPLOYEE);
		return convert4Listbox(users);
	}

	@Transactional(readOnly = true)
	public List<CEmployeeRecord> getAllValidEmployeesList() throws CSecurityException {
		final CLoggedUserRecord loggedUserInfo = CServletSessionUtils.getLoggedUser();
		List<CUser> users = this.userDao.findAllEmployeesByValidFlag(loggedUserInfo.getClientInfo().getClientId(), true, IUserTypes.EMPLOYEE);
		return convert4employeeList(users);
	}

	@Transactional(readOnly = true)
	public List<CUserSystemEmailRecord> getAccounts4SystemEmail() throws CSecurityException {
		final CLoggedUserRecord loggedUserInfo = CServletSessionUtils.getLoggedUser();

		List<CUser> users = this.userDao.findAllEmployeesByValidFlag(loggedUserInfo.getClientInfo().getClientId(), true, IUserTypes.EMPLOYEE);
		CUser receptionUser = this.userDao.findClientReceptionAccount(loggedUserInfo.getClientInfo().getClientId());
		users.add(receptionUser);

		return convert4SystemEmail(users);
	}

	@Transactional(readOnly = true)
	public List<CUserSystemEmailRecord> getAccounts4Notification(Long userRequestID) throws CSecurityException {
		final CLoggedUserRecord loggedUserInfo = CServletSessionUtils.getLoggedUser();

		List<CUser> users = this.userDao.findAllEmployeesByValidFlag(loggedUserInfo.getClientInfo().getClientId(), true, IUserTypes.EMPLOYEE);

		List<CUser> listNotification;
		if (userRequestID != null) {
			CUser u = this.userDao.findById(userRequestID);
			listNotification = u.getAddNotified();
		} else {
			listNotification = new ArrayList<>();
		}

		return convert4Notifyx(users, listNotification);
	}

	@Transactional
	public void saveSystemEmailAccounts(CUserSystemEmailContainer data) throws CBusinessException {
		final CLoggedUserRecord loggedUserInfo = CServletSessionUtils.getLoggedUser();
		List<CUser> oldEmailedUsers = this.userDao.findBySystemEmailFlag(loggedUserInfo.getClientInfo().getClientId());

		List<CUserSystemEmailRecord> list = data.getSelectedUsers();
		// save selected
		for (CUserSystemEmailRecord record : list) {
			CUser user = this.userDao.findById(record.getUserId());
			user.setReceiverSystemEmail(Boolean.TRUE);
			if (listContansRecord(oldEmailedUsers, user)) {
				oldEmailedUsers.remove(user);
			}
			this.userDao.saveOrUpdate(user);
		}

		if (oldEmailedUsers != null && !oldEmailedUsers.isEmpty()) {
			for (CUser user : oldEmailedUsers) {
				user.setReceiverSystemEmail(Boolean.FALSE);
				this.userDao.saveOrUpdate(user);
			}
		}
	}

	@Transactional
	public void saveNotifyOfApprovedRequest(CNotifyOfApprovedRequestContainer data) throws CBusinessException {
		CUser user = this.userDao.findById(data.getUserRequestId());
		user.getAddNotified().clear();

		List<CUserSystemEmailRecord> list = data.getSelectedUsers();
		for (CUserSystemEmailRecord record : list) {
			CUser u = this.userDao.findById(record.getUserId());
			user.getAddNotified().add(u);
		}
		this.userDao.saveOrUpdate(user);

	}

	private Boolean listContansRecord(List<CUser> list, CUser record) {
		if (list != null && !list.isEmpty()) {
			for (CUser tmpRecord : list) {
				if (tmpRecord.getId().equals(record.getId())) {
					return Boolean.TRUE;
				}
			}
			return Boolean.FALSE;
		} else {
			return Boolean.FALSE;
		}
	}

	/**
	 * Converts db model to client model
	 * 
	 * @param input
	 * @return
	 */
	private List<CCodeListRecord> convert4Listbox(List<CUser> input) {
		List<CCodeListRecord> retVal = new ArrayList<>();
		for (CUser user : input) {
			CCodeListRecord newRec = new CCodeListRecord();
			newRec.setId(user.getId());
			newRec.setName(user.getSurname() + " " + user.getName());
			newRec.setDescription(user.getSurname() + " " + user.getName());
			retVal.add(newRec);
		}
		return retVal;
	}

	/**
	 * Converts server model to client model
	 * 
	 * @param input list of users
	 * @return client list records
	 */
	private List<CUserSystemEmailRecord> convert4SystemEmail(List<CUser> input) {

		// - take user names
		List<String> uNames = new ArrayList<>();
		Map<String, CUser> tmp = new HashMap<>();
		for (CUser user : input) {
			String name;
			if (RECEPCIA.equals(user.getName())) {
				name = user.getName();
			} else {
				name = user.getSurname() + " " + user.getName();
			}
			uNames.add(name);
			tmp.put(name, user);
		}

		// - sort ones
		sortByLocale(uNames);

		// - convert to result
		List<CUserSystemEmailRecord> retVal = new ArrayList<>();
		long idx = 0l;
		for (String userName : uNames) {
			CUserSystemEmailRecord newRec = new CUserSystemEmailRecord();
			newRec.setOrderId(idx++);
			newRec.setName(userName);
			newRec.setSelected(tmp.get(userName).getReceiverSystemEmail());
			newRec.setUserId(tmp.get(userName).getId());
			retVal.add(newRec);
		}
		return retVal;
	}

	private List<CUserSystemEmailRecord> convert4Notifyx(List<CUser> input, List<CUser> notifyList) {
		List<CUserSystemEmailRecord> retVal = new ArrayList<>();

		for (CUser user : input) {
			boolean wasAdded = false;

			for (CUser markedUser : notifyList) {
				if (user.getId().equals(markedUser.getId())) {
					String name;
					if (RECEPCIA.equals(user.getName())) {
						name = user.getName();
					} else {
						name = user.getSurname() + " " + user.getName();
					}

					CUserSystemEmailRecord newRec = new CUserSystemEmailRecord();

					newRec.setName(name);
					newRec.setSelected(true);
					newRec.setUserId(user.getId());
					retVal.add(newRec);
					wasAdded = true;
				}
			}

			if (!wasAdded) {
				String name;
				if (RECEPCIA.equals(user.getName())) {
					name = user.getName();
				} else {
					name = user.getSurname() + " " + user.getName();
				}

				CUserSystemEmailRecord newRec = new CUserSystemEmailRecord();

				newRec.setName(name);
				newRec.setSelected(false);
				newRec.setUserId(user.getId());
				retVal.add(newRec);
			}
		}

		return retVal;
	}

	private List<CEmployeeRecord> convert4employeeList(List<CUser> input) {

		// - take user names
		List<String> uNames = new ArrayList<>();
		Map<String, CUser> tmp = new HashMap<>();
		for (CUser user : input) {
			String name;
			if (RECEPCIA.equals(user.getName())) {
				name = user.getName();
			} else {
				name = user.getSurname() + " " + user.getName();
			}
			uNames.add(name);
			tmp.put(name, user);
		}

		// - sort ones
		sortByLocale(uNames);

		// - convert to result
		List<CEmployeeRecord> retVal = new ArrayList<>();
		long idx = 0l;
		for (String userName : uNames) {
			CEmployeeRecord newRec = new CEmployeeRecord();
			newRec.setOrderId(idx++);
			newRec.setName(userName);
			newRec.setUserId(tmp.get(userName).getId());
			retVal.add(newRec);
		}
		return retVal;
	}

	private void sortByLocale(List<String> list) {
		String sLocale = (String) CServletSessionUtils.getHttpSession().getAttribute("locale");
		Collator collator;
		if (sLocale != null) {
			collator = Collator.getInstance(new Locale(sLocale));
		} else {
			collator = Collator.getInstance(new Locale("sk"));
			collator.setStrength(Collator.TERTIARY);
		}
		Collections.sort(list, collator);

	}

	@Transactional(readOnly = false)
	public void modifyMyProjects(Long projectId, boolean flagMyProject, Long userId) {
		CUser user = this.userDao.findById(userId);
		CProject project = this.projectDao.findById(projectId);
		if (flagMyProject) {
			if (!user.getUserProjects().contains(project)) {
				user.getUserProjects().add(project);
			}
		} else {
			if (user.getUserProjects().contains(project)) {
				user.getUserProjects().remove(project);
			}
		}

		this.userDao.saveOrUpdate(user);
	}

	@Transactional(readOnly = false)
	public void modifyMyActivities(Long activityId, boolean flagMyActivity, Long userId) {
		CUser user = this.userDao.findById(userId);
		CActivity activity = this.activityDao.findById(activityId);
		if (flagMyActivity) {
			if (!user.getUserActivities().contains(activity)) {
				user.getUserActivities().add(activity);
			}
		} else {
			if (user.getUserActivities().contains(activity)) {
				user.getUserActivities().remove(activity);
			}
		}

		this.userDao.saveOrUpdate(user);

	}

	@Transactional(readOnly = false)
	public void modifyMyFavourites(Long favouriteUserId, boolean flagMyFavourite, Long userId) {
		CUser user = this.userDao.findById(userId);
		CUser favouriteUser = this.userDao.findById(favouriteUserId);
		if (flagMyFavourite) {
			if (!user.getUserFavourites().contains(favouriteUser)) {
				user.getUserFavourites().add(favouriteUser);
			}
		} else {
			if (user.getUserFavourites().contains(favouriteUser)) {
				user.getUserFavourites().remove(favouriteUser);
			}
		}

		this.userDao.saveOrUpdate(user);
	}

	public boolean existsUserInDomain(String login) {
		try {
			if (login != null) {
				login = login.toLowerCase();
			}
			return CLdapAuthentication.existsInDomain(login, ldapAuthenticationConfigurator);
		} catch (CBusinessException be) {
			Logger.getLogger(this.getClass()).info("User " + login + " does not exists in domain", be);
			return false;
		}
	}

	@Transactional(rollbackForClassName = "CSecurityException")
	public void renewPassword(final String login, final String email) throws CBusinessException {
		Logger.getLogger(this.getClass()).debug("Renewing password for login: " + login);
		final CUser user = this.userDao.findByLogin(login);

		// checks email
		if ((user == null) || !user.getEmail().equals(email)) {
			throw new CSecurityException(CClientExceptionsMessages.LOGIN_BY_EMAIL_FAILED);
		}

		// generates new password
		final String newPassword = generateUserPassword(user);

		// sends password
		this.notificationDao.sendRenewPassword(user, newPassword, null, false);

		Logger.getLogger(this.getClass()).debug("Password renewed for login: " + login);
	}

	@Transactional(rollbackForClassName = "CSecurityException")
	public void changePassword(final String login, final String originalPwd, final String newPassword) throws CBusinessException {
		Logger.getLogger(this.getClass()).debug("Changing password for login: " + login);
		final CUser user = this.userDao.findByLogin(login);
		boolean userIsReception = user.getType().getId().equals(IUserTypes.RECEPTION);
		boolean userPasswordMatchs;

		// ked sa meni heslo recepcii, tak sa overuje heslo admina a nie povodne heslo
		if (userIsReception) {

			final CUser admin = this.userDao.findClientAdministratorAccount(user.getClient().getId());
			String encryptedAdminPassword = admin.getPassword();
			String encryptedEnteredOriginalPasswordWithAdminSalt = CEncryptUtils.getHash(originalPwd, admin.getPasswordSalt());
			userPasswordMatchs = encryptedAdminPassword.equals(encryptedEnteredOriginalPasswordWithAdminSalt);

		} else {

			String encryptedUserPassword = user.getPassword();
			String encryptedEnteredOriginalPassword = CEncryptUtils.getHash(originalPwd, user.getPasswordSalt());
			userPasswordMatchs = encryptedUserPassword.equals(encryptedEnteredOriginalPassword);
		}

		// checks email
		if (!userPasswordMatchs) {
			throw new CSecurityException(CClientExceptionsMessages.LOGIN_BY_PWD_FAILED);
		}

		String newPasswordSalt = user.getPasswordSalt();
		if (newPasswordSalt == null) {
			newPasswordSalt = this.appCodeGenerator.generatePasswordSalt();
			user.setPasswordSalt(newPasswordSalt);
		}
		String encryptedNewPassword;

		encryptedNewPassword = CEncryptUtils.getHash(newPassword, newPasswordSalt);
		user.setPassword(encryptedNewPassword);
		this.userDao.saveOrUpdate(user);

		Logger.getLogger(this.getClass()).debug("Password hanged for login: " + login);
	}

	private void changePinCore(CUser user, String newPin) throws CBusinessException {
		String pinCodeSalt = user.getPinCodeSalt();
		if (pinCodeSalt == null) {
			pinCodeSalt = this.appCodeGenerator.generatePinSalt();
			user.setPinCodeSalt(pinCodeSalt);
		}
		String encryptedNewPin;
		// check pin unique
		List<String> pinHashs = this.userDao.getPinCodes(user.getClient().getId());
		List<String> pinSalts = this.userDao.getPinCodeSalts(user.getClient().getId());
		for (String salt : pinSalts) {
			String testPinHash = CEncryptUtils.getHash(newPin, salt);
			if (pinHashs.contains(testPinHash)) {
				throw new CSecurityException(CClientExceptionsMessages.PIN_ALREADY_IN);
			}
		}

		encryptedNewPin = CEncryptUtils.getHash(newPin, pinCodeSalt);
		user.setPinCode(encryptedNewPin);

		this.userDao.saveOrUpdate(user);

	}

	private void changeCardCodeCore(CUser user, String newCardCode) throws CBusinessException {
		String cardCodeSalt = user.getCardCodeSalt();
		if (cardCodeSalt == null) {
			cardCodeSalt = this.appCodeGenerator.generatePinSalt();
			user.setCardCodeSalt(cardCodeSalt);
		}
		String encryptedNewCardCode;
		// check card code unique
		List<String> cardCodeHashs = this.userDao.getCardCodes(user.getClient().getId());
		List<String> cardCodeSalts = this.userDao.getCardCodeSalts(user.getClient().getId());
		for (String salt : cardCodeSalts) {
			String testCardCodeHash = CEncryptUtils.getHash(newCardCode, salt);
			if (cardCodeHashs.contains(testCardCodeHash)) {
				throw new CSecurityException(CClientExceptionsMessages.CARD_CODE_ALREADY_IN);
			}
		}

		encryptedNewCardCode = CEncryptUtils.getHash(newCardCode, cardCodeSalt);
		user.setCardCode(encryptedNewCardCode);

		this.userDao.saveOrUpdate(user);

	}

	@Transactional(rollbackForClassName = "CBusinessException")
	public void changePin(Long userId, String newPin) throws CBusinessException {
		Logger.getLogger(this.getClass()).debug("Changing pin for login: " + userId);
		final CUser user = this.userDao.findById(userId);

		changePinCore(user, newPin);

		Logger.getLogger(this.getClass()).debug("Pin changed for id: " + userId);
	}

	@Transactional(rollbackForClassName = "CBusinessException")
	public void changeCardCode(Long userId, String cardCode) throws CBusinessException {
		Logger.getLogger(this.getClass()).debug("Changing cardCode for login: " + userId);
		final CUser user = this.userDao.findById(userId);

		changeCardCodeCore(user, cardCode);

		Logger.getLogger(this.getClass()).debug("cardCode changed for id: " + userId);
	}

	@Transactional(rollbackForClassName = "CBusinessException")
	public void changePin(String login, String newPin) throws CBusinessException {
		Logger.getLogger(this.getClass()).debug("Changing pin for login: " + login);
		final CUser user = this.userDao.findByLogin(login);

		changePinCore(user, newPin);

		Logger.getLogger(this.getClass()).debug("Pin changed for login: " + login);
	}

	private CLoggedUserRecord convertUserToLoggedUser(final CUser user, boolean checkUserValidFlag) throws CSecurityException {
		if (checkUserValidFlag && !user.getValid()) {
			throw new CSecurityException(CClientExceptionsMessages.USER_BLOCKED);
		}

		// stores info about logged user

		final CClient client = user.getClient();
		final CClientInfo clientInfo = new CClientInfo();
		clientInfo.setClientId(client.getId());
		clientInfo.setClientName(client.getNameShort());
		clientInfo.setProjectRequired(client.getProjectRequired());
		clientInfo.setActivityRequired(client.getActivityRequired());
		clientInfo.setLanguage(client.getLanguage());

		final CLoggedUserRecord loggedRecord = new CLoggedUserRecord();
		loggedRecord.setClientInfo(clientInfo);
		loggedRecord.setName(user.getName());

		final Long role = this.identifyUsersRole(user);
		loggedRecord.addRole(role);
		loggedRecord.setRoleName(user.getType().getDescription());
		loggedRecord.setSurname(user.getSurname());
		loggedRecord.setUserId(user.getId());
		loggedRecord.setLogin(user.getLoginLong());
		loggedRecord.setAutoLoginToken(user.getAutoLoginToken());
		loggedRecord.setAllowedAlertnessWork(user.getAllowedAlertnessWork());
		loggedRecord.setHomeOfficePermission(user.getHomeOfficePermission().getId());
		loggedRecord.setEditTime(user.getEditTime());
		loggedRecord.setTableRows(user.getTableRows());
		loggedRecord.setVacation(user.getVacation());
		loggedRecord.setVacationNextYear(user.getVacationNextYear());

		loggedRecord.setJiraAccessToken(user.getJiraAccessToken());

		loggedRecord.setRoleCode(this.identifyRoleCode(role));

		loggedRecord.setJiraTokenGeneration(user.getJiraTokenGeneration());
		
		final List<COrganizationTree> positions = user.getOrganizationTreesOwned();

		if ((positions != null) && (!positions.isEmpty())) {
			loggedRecord.setPosition(positions.get(0).getPossition());
		}

		// when logged user is reception, don't change the logged user record in
		// session!
		// reception hasn't the rights to modify the time records
		CLoggedUserRecord currentLoggedUser = (CLoggedUserRecord) CServletSessionUtils.getHttpSession().getAttribute(ISessionParametersKeys.SECURITY_LOGGED_USER);
		if (currentLoggedUser != null) {
			if (!"RE".equals(currentLoggedUser.getRoleCode())) {
				// logged user isn't reception
				CServletSessionUtils.getHttpSession().setAttribute(ISessionParametersKeys.SECURITY_LOGGED_USER, loggedRecord);
			}
		} else {
			// this is the first user for this session!
			CServletSessionUtils.getHttpSession().setAttribute(ISessionParametersKeys.SECURITY_LOGGED_USER, loggedRecord);
		}

		Logger.getLogger(this.getClass()).debug("Authenticated: " + user.getLoginLong());
		return loggedRecord;
	}

	@Transactional(readOnly = true)
	public CLoggedUserRecord getUserInfo(String pinCode) throws CBusinessException {
		CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();

		CUser foundedUser = null;

		// komplikacia: pre sifrovany pin - viem len porovnavat
		List<CUser> users = this.userDao.getClientUsers(loggedUser.getClientInfo().getClientId());
		for (CUser user : users) {
			if (user.getPinCode() != null && user.getPinCodeSalt() != null) {
				String pinCodeSalt = user.getPinCodeSalt();
				String encryptedPinCodeString = CEncryptUtils.getHash(pinCode, pinCodeSalt);

				if (user.getPinCode().equals(encryptedPinCodeString)) {
					foundedUser = user;
					break;
				}
			}
		}

		if (foundedUser == null) {
			Logger.getLogger(this.getClass()).debug("Entered wrong pin code: " + pinCode);
			throw new CSecurityException(CClientExceptionsMessages.LOGIN_BY_PIN_FAILED);
		}
		return convertUserToLoggedUser(foundedUser, false);
	}

	@Transactional(readOnly = true)
	public CLoggedUserRecord getUserInfoByCardCode(String cardCode) throws CBusinessException {
		CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();

		CUser foundedUser = null;

		// komplikacia: pre sifrovany card code - viem len porovnavat
		List<CUser> users = this.userDao.getClientUsers(loggedUser.getClientInfo().getClientId());
		for (CUser user : users) {
			if (user.getCardCode() != null && user.getCardCodeSalt() != null) {
				String cardCodeSalt = user.getCardCodeSalt();
				String encryptedCardCodeString = CEncryptUtils.getHash(cardCode, cardCodeSalt);

				if (user.getCardCode().equals(encryptedCardCodeString)) {
					foundedUser = user;
					break;
				}
			}
		}

		if (foundedUser == null) {
			Logger.getLogger(this.getClass()).debug("Entered wrong card code: " + cardCode);
			throw new CSecurityException(CClientExceptionsMessages.LOGIN_BY_CARD_CODE_FAILED);
		}
		return convertUserToLoggedUser(foundedUser, false);
	}

	@Transactional(readOnly = true)
	public List<CLoggedUserRecord> getUsersInfo(List<String> pins) throws CBusinessException {
		List<CLoggedUserRecord> result = new ArrayList<>();
		CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();

		for (String pinCode : pins) {
			CUser foundedUser = null;

			// komplikacia: pre sifrovany pin - viem len porovnavat
			List<CUser> users = this.userDao.getClientUsers(loggedUser.getClientInfo().getClientId());
			for (CUser user : users) {
				if (user.getPinCode() != null && user.getPinCodeSalt() != null) {
					String pinCodeSalt = user.getPinCodeSalt();
					String encryptedPinCodeString = CEncryptUtils.getHash(pinCode, pinCodeSalt);

					if (user.getPinCode().equals(encryptedPinCodeString)) {
						foundedUser = user;
						break;
					}
				}
			}

			// ak sa user nenasiel, tak PIN bol zli a ignorujeme ho
			if (foundedUser != null) {
				result.add(convertUserToLoggedUser(foundedUser, false));
			}
		}

		if (result.isEmpty()) {
			Logger.getLogger(this.getClass()).debug("All entered pins are wrong: " + pins);
			throw new CSecurityException(CClientExceptionsMessages.LOGIN_BY_PIN_FAILED);
		}

		return result;
	}

	@Transactional(readOnly = true)
	public List<CLoggedUserRecord> getUsersInfoByCardCodes(List<String> cardCodes) throws CBusinessException {
		List<CLoggedUserRecord> result = new ArrayList<>();
		CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();

		for (String cardCode : cardCodes) {
			CUser foundedUser = null;

			// komplikacia: pre sifrovany pin - viem len porovnavat
			List<CUser> users = this.userDao.getClientUsers(loggedUser.getClientInfo().getClientId());
			for (CUser user : users) {
				if (user.getCardCode() != null && user.getCardCodeSalt() != null) {
					String cardCodeSalt = user.getCardCodeSalt();
					String encryptedCardCodeString = CEncryptUtils.getHash(cardCode, cardCodeSalt);

					if (user.getCardCode().equals(encryptedCardCodeString)) {
						foundedUser = user;
						break;
					}
				}
			}

			// ak sa user nenasiel, tak PIN bol zly a ignorujeme ho
			if (foundedUser != null) {
				result.add(convertUserToLoggedUser(foundedUser, false));
			}
		}

		if (result.isEmpty()) {
			Logger.getLogger(this.getClass()).debug("All entered card codes are wrong: " + cardCodes);
			throw new CSecurityException(CClientExceptionsMessages.LOGIN_BY_CARD_CODE_FAILED);
		}

		return result;
	}

	private Long identifyUsersRole(final CUser user) {
		if (IUserTypeCode.ID_EMPLOYEE.equals(user.getType().getId())) {
			final List<COrganizationTree> trees = user.getOrganizationTreesOwned();

			if (trees != null) {
				for (final COrganizationTree cOrganizationTree : trees) {
					if (!cOrganizationTree.getOrganizationTreesSubordinate().isEmpty()) {
						return IUserTypeCode.ID_EMPLOYEE_WITH_SUB;
					}
				}
			}
			return IUserTypeCode.ID_EMPLOYEE_WITHOUT_SUB;
		} else {
			return user.getType().getId();
		}
	}

	private String identifyRoleCode(final Long roleId) {
		if (IUserTypeCode.ID_EMPLOYEE.equals(roleId)) {
			return IUserTypeCode.EMPLOYEE;
		} else if (IUserTypeCode.ID_EMPLOYEE_WITH_SUB.equals(roleId)) {
			return IUserTypeCode.EMPLOYEE;
		} else if (IUserTypeCode.ID_EMPLOYEE_WITHOUT_SUB.equals(roleId)) {
			return IUserTypeCode.EMPLOYEE;
		} else if (IUserTypeCode.ID_ACCOUNTANT.equals(roleId)) {
			return IUserTypeCode.ACCOUNTANT;
		} else if (IUserTypeCode.ID_RECEPTION.equals(roleId)) {
			return IUserTypeCode.RECEPTION;
		} else if (IUserTypeCode.ID_SYSTEM_ADMIN.equals(roleId)) {
			return IUserTypeCode.SYSTEM_ADMIN;
		} else if (IUserTypeCode.ID_ORG_ADMIN.equals(roleId)) {
			return IUserTypeCode.ORG_ADMIN;
		} else {
			return "";
		}
	}

	@Override
	@Transactional
	public List<CCodeListRecord> getAllTypeEmployment() throws CSecurityException {
		List<CEmploymentType> employmentTypes = this.employmentTypeDao.findAllEmploymentTypes();
		return convert4Combobox(employmentTypes);
	}

	/**
	 * Converts db model to client model
	 * 
	 * @param input
	 * @return
	 */
	private List<CCodeListRecord> convert4Combobox(List<CEmploymentType> input) {
		List<CCodeListRecord> retVal = new ArrayList<>();
		for (CEmploymentType empType : input) {
			CCodeListRecord newRec = new CCodeListRecord();
			newRec.setId(empType.getId());
			newRec.setName(empType.getDescription());
			retVal.add(newRec);
		}
		return retVal;
	}

	@Override
	@Transactional
	public List<CCodeListRecord> getAllTypesOfHomeOfficePermission() throws CSecurityException {
		List<CHomeOfficePermission> homeOfficePermissions = this.homeOfficePermissionDao.findAllPermissionTypes();
		return convertPermTypes4Combobox(homeOfficePermissions);
	}

	/**
	 * Converts db model to client model
	 * 
	 * @param input
	 * @return
	 */
	private List<CCodeListRecord> convertPermTypes4Combobox(List<CHomeOfficePermission> input) {
		List<CCodeListRecord> retVal = new ArrayList<>();
		for (CHomeOfficePermission empType : input) {
			CCodeListRecord newRec = new CCodeListRecord();
			newRec.setId(empType.getId());
			newRec.setName(empType.getDescription());
			retVal.add(newRec);
		}
		return retVal;
	}

	@Override
	@Transactional(readOnly = true)
	public List<CViewOrganizationTreeNodeRecord> listNotifiedUsers(Boolean withSubordiantes) throws CBusinessException {
		Logger.getLogger(this.getClass()).info("Reading users");
		final CLoggedUserRecord loggedUserRecord = CServletSessionUtils.getLoggedUser();
		final CUser loggedUser = this.userDao.findById(loggedUserRecord.getUserId());

		List<CUser> listNotified = loggedUser.getNotified();
		List<CUser> newListNotified = new ArrayList<>();

		for (CUser notifiedUser : listNotified) {
			newListNotified.add(notifiedUser);
		}

		if (withSubordiantes) {
			List<CUser> subordinates = this.userDao.findSubordinate(loggedUser, true, true, IUserTypes.EMPLOYEE);
			for (CUser subordinateUser : subordinates) {
				if (!newListNotified.contains(subordinateUser)) {
					newListNotified.add(subordinateUser);
				}
			}
		}

		List<CViewOrganizationTreeNode> list = viewOrgTreeDao.findNotified(newListNotified);
		List<CViewOrganizationTreeNodeRecord> retVal = new ArrayList<>();

		for (CViewOrganizationTreeNode org : list) {
			retVal.add(org.convertWithoutParentId());
		}

		return retVal;
	}
}
