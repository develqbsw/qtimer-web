package sk.qbsw.sed.test.service;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.service.business.IPinCodeGeneratorService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.test.dao.ADaoTest;

/**
 * Class for testing IPinCodeGeneratorService methods.
 * @author moravcik 
 *
 */
public class CPinCodeGeneratorServiceTest extends ADaoTest {
	
	private CLoggedUserRecord loggedUserRecord;
	
	private static final int PIN_LENGTH = 4;
	
	@Autowired
	private IPinCodeGeneratorService generatorService;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();	
		
		loggedUserRecord = super.loginAsTestUser();
	}
	
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	@Transactional
	public void generatePinTest() throws CBusinessException {
		String newPin = generatorService.getGeneratedPIN(loggedUserRecord.getLogin(), null);
		assertNotNull(newPin);
		assertEquals(PIN_LENGTH, newPin.length());
	}
	
}
