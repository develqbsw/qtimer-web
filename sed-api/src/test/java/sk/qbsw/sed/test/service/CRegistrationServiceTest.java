package sk.qbsw.sed.test.service;

import java.util.Calendar;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.model.registration.CRegistrationClientRecord;
import sk.qbsw.sed.client.model.registration.CRegistrationUserRecord;
import sk.qbsw.sed.client.service.business.IRegistrationService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.test.dao.ADaoTest;

/**
 * registration test
 * @author lobb
 *
 */
public class CRegistrationServiceTest extends ADaoTest {

	@Autowired
	protected IRegistrationService registrationService;
	
	/**
	 * registration test
	 * @throws Exception
	 */
	@Test
	@Transactional
	public void testRegister() throws Exception {
		CRegistrationClientRecord org = new CRegistrationClientRecord();
		org.setOrgName("Test");
		org.setLegacyForm(1L);
		org.setCity("Bratislava");
		org.setStreet("Prievozská");
		org.setStreetNo("6");
		org.setZip("821 09");
		
		CRegistrationUserRecord user = new CRegistrationUserRecord();
		user.setEmail("demo@q-timer.com"); //password pre prod: Ryjbpw
		user.setLogin("admin" + Calendar.getInstance().getTimeInMillis());
		user.setName("Admin");
		user.setSurname("Admin");
		user.setPassword("Heslo1");
		user.setUserType(2L);
		user.setIsValid(true);
		user.setIsMain(true);
		
		registrationService.register(org, user);
	}
	
	/**
	 * login already used in registration test
	 * @throws Exception
	 */
	@Test(expected=CBusinessException.class)
	@Transactional
	public void testRegisterLoginUsed() throws Exception {
		CRegistrationClientRecord org = new CRegistrationClientRecord();
		org.setOrgName("Test");
		org.setLegacyForm(1L);
		org.setCity("Bratislava");
		org.setStreet("Prievozská");
		org.setStreetNo("6");
		org.setZip("821 09");
		
		CRegistrationUserRecord user = new CRegistrationUserRecord();
		user.setEmail("lobb@qbsw.sk");
		user.setLogin("qbsw");
		user.setName("Admin");
		user.setSurname("Admin");
		user.setPassword("Heslo1");
		user.setUserType(2L);
		user.setIsValid(true);
		user.setIsMain(true);
		
		registrationService.register(org, user);
	}
}
