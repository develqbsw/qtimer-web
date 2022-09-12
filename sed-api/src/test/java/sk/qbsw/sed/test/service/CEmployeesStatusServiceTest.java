package sk.qbsw.sed.test.service;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.model.CEmployeesStatusNew;
import sk.qbsw.sed.client.model.IStatusConstants;
import sk.qbsw.sed.client.model.lock.CLockRecord;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.model.timestamp.CPredefinedInteligentTimeStamp;
import sk.qbsw.sed.client.model.timestamp.CTimeStampAddRecord;
import sk.qbsw.sed.client.model.timestamp.CTimeStampRecord;
import sk.qbsw.sed.client.service.business.IEmployeesStatusService;
import sk.qbsw.sed.client.service.business.ITimesheetService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.framework.security.exception.CSecurityException;
import sk.qbsw.sed.server.service.codelist.IActivityConstant;
import sk.qbsw.sed.test.dao.ADaoTest;

public class CEmployeesStatusServiceTest extends ADaoTest {

	@Autowired
	private IEmployeesStatusService statusService;
	
	@Autowired
	private ITimesheetService timesheetService;
	
	
	private CLoggedUserRecord loggedUserRecord;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		
		loggedUserRecord = super.loginAsTestUser();
	}
	
	@After
	public void tearDown() throws Exception {}
	
	
	/**
	 * Tests changing status.
	 * @throws CBusinessException
	 */
	@Test
	@Transactional
	public void testCheckStatusById() throws CBusinessException {
		
		statusService.clear();
		
		List<CEmployeesStatusNew> list = statusService.fetch();
		
		for (CEmployeesStatusNew s : list) {
			if (s.getId().equals(loggedUserRecord.getUserId())) {
				assertEquals(IStatusConstants.NOT_IN_WORK, s.getStatus());
			}
		}
		
		String status = statusService.checkStatus(loggedUserRecord.getUserId());
		assertEquals(null, status);		
		
		CPredefinedInteligentTimeStamp predefinedInteligentTimeStamp = timesheetService.loadPredefinedInteligentValueForUserTimerPanel(loggedUserRecord.getUserId(), new Date());
		CTimeStampAddRecord record = predefinedInteligentTimeStamp.getModel();
		record.setEmployeeId(loggedUserRecord.getUserId());
		
		timesheetService.startWorking(record);
		String statusStartWork = statusService.checkStatus(loggedUserRecord.getUserId());
		assertEquals(IStatusConstants.IN_WORK, statusStartWork);
		
		timesheetService.startNonWorking(record);
		String statusNotWork = statusService.checkStatus(loggedUserRecord.getUserId());
		assertEquals(IStatusConstants.WORK_BREAK, statusNotWork);
		
		timesheetService.stopNonWorking(record);
		String statusContinueWork = statusService.checkStatus(loggedUserRecord.getUserId());
		assertEquals(IStatusConstants.IN_WORK, statusContinueWork);
		
		timesheetService.stopWorking(record);
		String statusStoppedWork = statusService.checkStatus(loggedUserRecord.getUserId());
		assertEquals(IStatusConstants.OUT_OF_WORK, statusStoppedWork);
	}
	
	/**
	 * Tests changing status.
	 * @throws CSecurityException 
	 * @throws CBusinessException
	 */
	@Test
	@Transactional
	public void testCheckStatusByTimeStamp() throws CSecurityException, CBusinessException {
		
		statusService.clear();
		
		List<CEmployeesStatusNew> list = statusService.fetch();
		
		for (CEmployeesStatusNew s : list) {
			if (s.getId().equals(loggedUserRecord.getUserId())) {
				assertEquals(IStatusConstants.NOT_IN_WORK, s.getStatus());
			}
		}
		
		CTimeStampRecord record = new CTimeStampRecord();
		record.setEmployeeId(loggedUserRecord.getUserId());
		record.setActivityId(41L);
		record.setDateFrom(new Date());
		record.setNote("note");
		
		CLockRecord lockRecord = timesheetService.add(record);
		String status = statusService.checkStatus(record);
		assertEquals(IStatusConstants.IN_WORK, status);
		
		lockRecord = timesheetService.delete(lockRecord.getId());
		
		status = statusService.checkStatus(record);
		assertEquals(IStatusConstants.NOT_IN_WORK, status);
		
		record = new CTimeStampRecord();
		record.setEmployeeId(loggedUserRecord.getUserId());
		record.setActivityId(IActivityConstant.BREAK);
		record.setDateFrom(new Date());
		record.setNote("note");
		
		lockRecord = timesheetService.add(record);
		status = statusService.checkStatus(record);
		assertEquals(IStatusConstants.WORK_BREAK, status);
		
		lockRecord = timesheetService.delete(lockRecord.getId());
		
		status = statusService.checkStatus(record);
		assertEquals(IStatusConstants.NOT_IN_WORK, status);
	}
	
}
