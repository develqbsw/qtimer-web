package sk.qbsw.sed.test.service;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.model.IUserTypes;
import sk.qbsw.sed.client.model.detail.CUserDetailRecord;
import sk.qbsw.sed.client.model.registration.CRegistrationClientRecord;
import sk.qbsw.sed.client.model.registration.CRegistrationUserRecord;
import sk.qbsw.sed.client.model.security.CClientInfo;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.service.business.IClientService;
import sk.qbsw.sed.client.service.business.IUserService;
import sk.qbsw.sed.client.ui.localization.CClientExceptionsMessages;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.server.dao.IClientDao;
import sk.qbsw.sed.server.dao.ILegalFormDao;
import sk.qbsw.sed.server.dao.IUserDao;
import sk.qbsw.sed.server.model.domain.CClient;
import sk.qbsw.sed.server.model.domain.CUser;
import sk.qbsw.sed.test.dao.ADaoTest;

/**
 * Class tests IUserService methods.
 * @author moravcik
 *
 */
@Transactional
public class CUserServiceTest extends ADaoTest {

	@Autowired
	private IUserService userService;
	
	@Autowired
	public IClientDao clientDao;
	
	@Autowired
	public IUserDao userDao;
	
	@Autowired
	private ILegalFormDao legalFormDao;
	
	@Autowired
	private IClientService clientService;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}
	
	@Rollback 
	@After
	public void tearDown() throws Exception {}
	
	/**
	 * Tests method addByRegistration().
	 * @throws CBusinessException 
	 */
	@Test
	public void testAddByRegistration() throws CBusinessException {
		//create new client
		CRegistrationClientRecord rec = new CRegistrationClientRecord();
		rec.setCountry("estCountry68432");
		rec.setCity("TestCity5486");
		rec.setStreet("Test5168");
		rec.setStreetNo("46848");
		rec.setZip("98734");
		rec.setOrgName("Test455458763");
		rec.setIdNo("98989898");
		Long legalForm = legalFormDao.getAllValid().get(0).getId();
		rec.setLegacyForm(legalForm);
		rec.setTaxNo("9898989898");
		rec.setVatNo("989898989898");
		//add
		Long recId = clientService.add(rec);
		//find
		CClient addedClient = clientDao.findById(recId);
		//chceck
		assertNotNull(addedClient);
		assertEquals(rec.getOrgName(), addedClient.getName());
		assertEquals(rec.getIdNo(), addedClient.getIdentificationNumber());
		assertEquals(rec.getTaxNo(), addedClient.getTaxNumber());
		assertEquals(rec.getVatNo(), addedClient.getTaxVatNumber());
		
		//make client info
		CClientInfo clientInfo = new CClientInfo();
		clientInfo.setClientId(addedClient.getId());
		clientInfo.setClientName(addedClient.getName());
		
		//create new user record
		CRegistrationUserRecord newUser = new CRegistrationUserRecord();
		newUser.setLogin("Test41");
		newUser.setPassword("TestPassword7341");
		newUser.setEmail("Test@email.7341");
		newUser.setName("TestName7341");
		newUser.setSurname("TestSurname7341");
		newUser.setPhoneFix("Test7341");
		newUser.setClientInfo(clientInfo);
		newUser.setIsValid(true);
		newUser.setIsMain(true);
		newUser.setUserType(IUserTypes.ORG_MAN);
		
		//add user
		userService.addByRegistration(newUser);
		
		//check user
		CUser registeredUser = userDao.findClientAdministratorAccount(addedClient.getId());
		assertNotNull(registeredUser);
		assertEquals(newUser.getName(), registeredUser.getName());
		assertEquals(newUser.getSurname(), registeredUser.getSurname());
		assertEquals(newUser.getLogin().toLowerCase()+".admin", registeredUser.getLoginLong());
		
		//check created reception user
		CUser receptionUser = userDao.findClientReceptionAccount(addedClient.getId());
		assertNotNull(receptionUser);
		assertEquals("Recepcia", receptionUser.getName());
		assertEquals("", receptionUser.getSurname());
		assertEquals(registeredUser.getLoginLong()+"_r", receptionUser.getLoginLong());
	}
	
	/**
	 * Tests method addByRegistration() with used login.
	 * @throws CBusinessException 
	 */
	@Test (expected=CBusinessException.class)
	public void testAddByRegistrationUsedLogin() throws CBusinessException {
		//add client
		CRegistrationClientRecord rec = new CRegistrationClientRecord();
		rec.setCountry("estCountry68432");
		rec.setCity("TestCity5486");
		rec.setStreet("Test5168");
		rec.setStreetNo("46848");
		rec.setZip("98734");
		rec.setOrgName("Test455458763");
		rec.setIdNo("98989898");
		Long legalForm = legalFormDao.getAllValid().get(0).getId();
		rec.setLegacyForm(legalForm);
		rec.setTaxNo("9898989898");
		rec.setVatNo("989898989898");
		//add, find and check
		Long recId = clientService.add(rec);
		CClient addedClient = clientDao.findById(recId);
		assertNotNull(addedClient);
		assertEquals(rec.getOrgName(), addedClient.getName());
		assertEquals(rec.getIdNo(), addedClient.getIdentificationNumber());
		assertEquals(rec.getTaxNo(), addedClient.getTaxNumber());
		assertEquals(rec.getVatNo(), addedClient.getTaxVatNumber());
		
		//make client info
		CClientInfo clientInfo = new CClientInfo();
		clientInfo.setClientId(addedClient.getId());
		clientInfo.setClientName(addedClient.getName());
		
		//create new user record
		CRegistrationUserRecord newUser = new CRegistrationUserRecord();
		newUser.setLogin("qbsw");
		newUser.setPassword("TestPassword7341");
		newUser.setEmail("Test@email.7341");
		newUser.setName("TestName7341");
		newUser.setSurname("TestSurname7341");
		newUser.setPhoneFix("Test7341");
		newUser.setClientInfo(clientInfo);
		newUser.setIsValid(true);
		newUser.setIsMain(true);
		newUser.setUserType(IUserTypes.ORG_MAN);
		
		//add user with used login. must throw exception
		try {
			userService.addByRegistration(newUser);
		} catch (CBusinessException e) {
			assertEquals(CClientExceptionsMessages.LOGIN_USED, e.getMessage());
			throw e;
		} finally {
			CUser registeredUser = userDao.findClientAdministratorAccount(addedClient.getId());
			assertNull(registeredUser);
		}
	}
	
	/**
	 * Tests method add() with login not in domain.
	 * @throws CBusinessException 
	 */
	@Test (expected=CBusinessException.class)
	public void testAddLoginNotInDomain() throws CBusinessException {
		//create new user
		CUserDetailRecord newUser = new CUserDetailRecord();
		newUser.setLogin("test5448testdomain.dom"); 
		newUser.setName("test5448");
		newUser.setSurname("test5448");
		
		//add. must throw exception
		Long addedUserId = null;
		try {
			addedUserId = userService.add(newUser);
		} catch (CBusinessException e) {
			assertEquals(CClientExceptionsMessages.USER_NOT_IN_DOMAIN, e.getMessage());
			throw e;
		} finally {
			assertNull(addedUserId);
		}
		
	}
	
	/**
	 * Tests method add().
	 * @throws CBusinessException 
	 */
	@Test
	public void testAdd() throws CBusinessException {
		super.loginAsAdmin();
		//create new user
		CUserDetailRecord newUser = new CUserDetailRecord();
		newUser.setLogin("test5448@qbsw.sk"); 
		newUser.setName("test5448");
		newUser.setSurname("test5448");
		newUser.setUserType(IUserTypes.EMPLOYEE);
		newUser.setIsValid(true);
		newUser.setIsMain(false);
		newUser.setEmail(newUser.getLogin());
		newUser.setEditTime(true);
		newUser.setAllowedAlertnessWork(true);
		
		Long addedUserId = userService.add(newUser);
		assertNotNull(addedUserId);
		CUser addedUser = userDao.findById(addedUserId);
		assertEquals(newUser.getName(), addedUser.getName());
		assertEquals(newUser.getUserType(), addedUser.getType().getId());
		
		CUser userByLogin = userDao.findByLogin(newUser.getLogin());
		assertNotNull(userByLogin);
		assertEquals(addedUser.getId(), userByLogin.getId());
	}
	
	/**
	 * Tests method getUserDetails().
	 * @throws CBusinessException 
	 */
	@Test
	public void testGetUserDetails() throws CBusinessException {
		CLoggedUserRecord loggedUser = super.loginAsTestUser();
		CUser user = userDao.findById(loggedUser.getUserId());
		assertNotNull(user);
		CUserDetailRecord userDetails = userService.getUserDetails(loggedUser.getUserId());
		assertNotNull(userDetails);
		
		assertEquals(user.getId(), userDetails.getId());
		assertEquals(user.getName(), userDetails.getName());
		assertEquals(user.getSurname(), userDetails.getSurname());
		assertEquals(user.getClient().getId(), userDetails.getClientInfo().getClientId());
	}
	
	/**
	 * Tests method modify().
	 * @throws CBusinessException 
	 */
	@Test
	public void testModify() throws CBusinessException {
		CLoggedUserRecord loggedUser = super.loginAsTestUser();
		CUser user = userDao.findById(loggedUser.getUserId());
		assertNotNull(user);
		String formerName = user.getName();
		assertNotNull(formerName);
		CUserDetailRecord userDetails = userService.getUserDetails(loggedUser.getUserId());
		assertNotNull(userDetails);
		CUserDetailRecord modifiedUserDetails = userDetails;
		modifiedUserDetails.setName("TestName2");
		userService.modify(modifiedUserDetails);
		CUser modifiedUser = userDao.findById(loggedUser.getUserId());
		assertNotNull(modifiedUser);
		assertEquals(modifiedUserDetails.getId(), modifiedUser.getId());
		assertEquals(modifiedUserDetails.getName(), modifiedUser.getName());
		assertEquals(modifiedUserDetails.getSurname(), modifiedUser.getSurname());
		assertEquals(modifiedUserDetails.getClientInfo().getClientId(), modifiedUser.getClient().getId());
		assertFalse(formerName.equals(modifiedUser.getName()));

	}
	
	/**
	 * Tests method modify() with not existing user Id.
	 */
	@Test
	public void testModifyExceptions() throws CBusinessException {
		CLoggedUserRecord loggedUser = super.loginAsTestUser();
		CUser user = userDao.findById(loggedUser.getUserId());
		assertNotNull(user);
		CUserDetailRecord modifiedUserDetails = userService.getUserDetails(loggedUser.getUserId());
		assertNotNull(modifiedUserDetails);
		
		String modifiedName = "modifiedName";
		modifiedUserDetails.setName(modifiedName);
		
		//existing user
		Long fakeUserId = -48956L;
		boolean throwed = false;
		try {
			modifiedUserDetails.setId(fakeUserId);
			userService.modify(modifiedUserDetails);
		} catch (CBusinessException e) {
			assertEquals("User not found", e.getMessage());
			throwed = true;
		} finally {
			assertTrue(throwed);
		}
		
		CUser modifiedUser = userDao.findById(loggedUser.getUserId());
		assertNotNull(modifiedUser);
		assertEquals(user.getId(), modifiedUser.getId());
		assertEquals(user.getName(), modifiedUser.getName());
		assertEquals(user.getSurname(), modifiedUser.getSurname());
		assertEquals(user.getClient().getId(), modifiedUser.getClient().getId());
		assertFalse(modifiedName.equals(modifiedUser.getName()));

		//set back real id
		modifiedUserDetails.setId(user.getId());
		
		//unique login
		throwed = false;
		try {
			modifiedUserDetails.setLogin("qbsw.admin");
			userService.modify(modifiedUserDetails);
		} catch (CBusinessException e) {
			assertEquals(CClientExceptionsMessages.LOGIN_USED, e.getMessage());
			throwed = true;
		} finally {
			assertTrue(throwed);
		}
		
		modifiedUser = userDao.findById(loggedUser.getUserId());
		assertNotNull(modifiedUser);
		assertEquals(user.getId(), modifiedUser.getId());
		assertEquals(user.getName(), modifiedUser.getName());
		assertEquals(user.getSurname(), modifiedUser.getSurname());
		assertEquals(user.getClient().getId(), modifiedUser.getClient().getId());
		assertFalse(modifiedName.equals(modifiedUser.getName()));
		
		//login not in domain
		throwed = false;
		try {
			modifiedUserDetails.setLogin("test5448testdomain.dom");
			modifiedUserDetails.setIsValid(true);
			userService.modify(modifiedUserDetails);
		} catch (CBusinessException e) {
			assertEquals(CClientExceptionsMessages.USER_NOT_IN_DOMAIN, e.getMessage());
			throwed = true;
		} finally {
			assertTrue(throwed);
		}
		
		modifiedUser = userDao.findById(loggedUser.getUserId());
		assertNotNull(modifiedUser);
		assertEquals(user.getId(), modifiedUser.getId());
		assertEquals(user.getName(), modifiedUser.getName());
		assertEquals(user.getSurname(), modifiedUser.getSurname());
		assertEquals(user.getClient().getId(), modifiedUser.getClient().getId());
		assertFalse(modifiedName.equals(modifiedUser.getName()));
	}
	
	/**
	 * Tests modifying one main user in client. Must throw exception.
	 * @throws CBusinessException
	 */
	@Test (expected=CBusinessException.class)
	public void testModifyLastMainUser() throws CBusinessException {
		//create mew client with ORG_MAN user
		CRegistrationClientRecord rec = new CRegistrationClientRecord();
		rec.setCountry("estCountry68432");
		rec.setCity("TestCity5486");
		rec.setStreet("Test5168");
		rec.setStreetNo("46848");
		rec.setZip("98734");
		rec.setOrgName("Test455458763");
		rec.setIdNo("98989898");
		Long legalForm = legalFormDao.getAllValid().get(0).getId();
		rec.setLegacyForm(legalForm);
		rec.setTaxNo("9898989898");
		rec.setVatNo("989898989898");
		Long recId = clientService.add(rec);
		CClient addedClient = clientDao.findById(recId);
		assertNotNull(addedClient);
		assertEquals(rec.getOrgName(), addedClient.getName());
		assertEquals(rec.getIdNo(), addedClient.getIdentificationNumber());
		assertEquals(rec.getTaxNo(), addedClient.getTaxNumber());
		assertEquals(rec.getVatNo(), addedClient.getTaxVatNumber());
		
		CClientInfo clientInfo = new CClientInfo();
		clientInfo.setClientId(addedClient.getId());
		clientInfo.setClientName(addedClient.getName());
		
		CRegistrationUserRecord newUser = new CRegistrationUserRecord();
		newUser.setLogin("Test41");
		newUser.setPassword("TestPassword7341");
		newUser.setEmail("Test@email.7341");
		newUser.setName("TestName7341");
		newUser.setSurname("TestSurname7341");
		newUser.setPhoneFix("Test7341");
		newUser.setClientInfo(clientInfo);
		newUser.setIsValid(true);
		newUser.setIsMain(true);
		newUser.setUserType(IUserTypes.ORG_MAN);
		
		userService.addByRegistration(newUser);
		
		//try to modify that user to not be main. should throw exception
		CUser adminUser = userDao.findClientAdministratorAccount(addedClient.getId());
		assertNotNull(adminUser);
		CUserDetailRecord modifiedUserDetails = userService.getUserDetails(adminUser.getId());
		assertNotNull(modifiedUserDetails);
		modifiedUserDetails.setIsMain(false);
	
		boolean throwed = false;
		try {
			userService.modify(modifiedUserDetails);
		} catch (CBusinessException e) {
			assertEquals(CClientExceptionsMessages.USER_NO_OTHER_MAIN_USER, e.getMessage());
			throwed = true;
			throw e;
		} finally {
			assertTrue(throwed);
			CUser modifiedUser = userDao.findClientAdministratorAccount(addedClient.getId());
			assertEquals(adminUser.getId(), modifiedUser.getId());
			assertEquals(adminUser.getClient().getId(), modifiedUser.getClient().getId());
			assertEquals(adminUser.getMain(), modifiedUser.getMain());
		}
				
	}
	
}
