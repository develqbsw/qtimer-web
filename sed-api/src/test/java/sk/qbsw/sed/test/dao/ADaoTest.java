package sk.qbsw.sed.test.dao;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.service.business.IRequestService;
import sk.qbsw.sed.client.service.business.ISendEmailService;
import sk.qbsw.sed.client.service.business.IUserService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.framework.security.exception.CSecurityException;
import sk.qbsw.sed.server.service.security.IAuthenticateService;

@RunWith (SpringJUnit4ClassRunner.class)
@ContextConfiguration (locations = {"file:src/main/webapp/WEB-INF/spring/test-api-context.xml"})
@TransactionConfiguration (transactionManager = "transactionManager", defaultRollback = true)
public abstract class ADaoTest
{
	
	@Autowired
	protected IAuthenticateService authenticateSevice;

	@Autowired
	protected IRequestService requestService;
	
	@Autowired
	protected IUserService userService;
	
	@Autowired
	protected ISendEmailService sendEmailService;
	
	@Autowired
	private CMockHelper mockHelper;
	
	@Autowired
	private CNotificationDaoMock notificationDaoMock;
	
	@Before
	public void setUp() throws Exception {
		ReflectionTestUtils.setField(mockHelper.unwrapSpringProxyObject(requestService), "notificationDao", notificationDaoMock);
		ReflectionTestUtils.setField(mockHelper.unwrapSpringProxyObject(userService), "notificationDao", notificationDaoMock);
		ReflectionTestUtils.setField(mockHelper.unwrapSpringProxyObject(sendEmailService), "notificationDao", notificationDaoMock);
		
		MockHttpSession httpSession = new MockHttpSession();
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setSession(httpSession);
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
	}
	
	protected CLoggedUserRecord loginAsAdmin() throws CSecurityException, CBusinessException {
		return authenticateSevice.authenticate("qbsw.admin", "Qwert1", false, null);
	}
	
	protected CLoggedUserRecord loginAsTestUser() throws CSecurityException, CBusinessException {
		return authenticateSevice.authenticate("turing@qbsw.sk", "Heslo1", false, null);
	}
}
