package sk.qbsw.sed.test.service;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import sk.qbsw.sed.client.model.codelist.CActivityRecord;
import sk.qbsw.sed.client.model.lock.CLockRecord;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.model.timestamp.CPredefinedInteligentTimeStamp;
import sk.qbsw.sed.client.model.timestamp.CTimeStampAddRecord;
import sk.qbsw.sed.client.model.timestamp.CTimeStampRecord;
import sk.qbsw.sed.client.model.timestamp.ITimestampScreenType;
import sk.qbsw.sed.client.service.business.ITimesheetService;
import sk.qbsw.sed.client.service.codelist.IActivityService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.test.dao.ADaoTest;

public class CTimesheetServiceTest extends ADaoTest {

	@Autowired
	private ITimesheetService timesheetService;

	private CLoggedUserRecord loggedUserRecord;
	
	@Autowired
	private IActivityService activityService;
	
	@Before
	public void setUp() throws Exception {
		MockHttpSession httpSession = new MockHttpSession();
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setSession(httpSession);
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
		
		loggedUserRecord = authenticateSevice.authenticate("turing@qbsw.sk", "Heslo1", false, null);		
	}

	@After
	public void tearDown() throws Exception {
	}
	

	@Test
	@Transactional
	public void testAddModifyDetailDelete() throws Exception {
		CTimeStampRecord record = new CTimeStampRecord();
		record.setEmployeeId(loggedUserRecord.getUserId());
		record.setActivityId(-1L);
		record.setDateFrom(new Date());
		record.setNote("note");
		
		CLockRecord lockRecord = timesheetService.add(record);
		assertNotNull(lockRecord.getId());		
		record = timesheetService.getDetail(lockRecord.getId());		
		assertEquals(record.getNote(), "note");
		
		record.setNote("edited note");		
		lockRecord = timesheetService.modify(record);
		record = timesheetService.getDetail(lockRecord.getId());		
		assertEquals(record.getNote(), "edited note");
		
		lockRecord = timesheetService.delete(lockRecord.getId());
	}

	@Test
	@Transactional
	public void testConfirmCancel() throws Exception {
		CTimeStampRecord record = new CTimeStampRecord();
		record.setEmployeeId(loggedUserRecord.getUserId());
		record.setActivityId(-1L);
		record.setDateFrom(new Date());
		record.setDateTo(new Date());
		
		timesheetService.add(record);
		
		Set<Long> users = new HashSet<Long>();
		users.add(loggedUserRecord.getUserId());
		timesheetService.confirmTimesheetRecords(ITimestampScreenType.MY_TIMESHEET_SCREEN, users, new Date(), new Date(), loggedUserRecord.getUserId(), false, false);
		
		timesheetService.cancelTimesheetRecords(ITimestampScreenType.MY_TIMESHEET_SCREEN, users, new Date(), new Date(), loggedUserRecord.getUserId(), false, false);
	}
	
	
	@Test(expected=CBusinessException.class)
	@Transactional
	public void testConfirmNothingToConfirm() throws Exception {
		Set<Long> users = new HashSet<Long>();
		users.add(loggedUserRecord.getUserId());
		// sam sebe potvrdzovat nemozem
		timesheetService.confirmTimesheetRecords(ITimestampScreenType.MY_TIMESHEET_SCREEN, users, new Date(), new Date(), loggedUserRecord.getUserId(), false, false);
	}
	
	@Test
	@Transactional
	public void testStartStop() throws Exception {
		CPredefinedInteligentTimeStamp predefinedInteligentTimeStamp = timesheetService.loadPredefinedInteligentValueForUserTimerPanel(loggedUserRecord.getUserId(), new Date());
		
		CTimeStampAddRecord record = predefinedInteligentTimeStamp.getModel();
		record.setEmployeeId(loggedUserRecord.getUserId());
		
		timesheetService.startWorking(record);
		
		timesheetService.startNonWorking(record);
		
		timesheetService.stopNonWorking(record);
		
		timesheetService.stopWorking(record);
	}
	
	/**
	 * test for activity time min check
	 * 
	 * @throws Exception
	 */
	@Test(expected=CBusinessException.class)
	@Transactional
	public void testCheckActivityTimeMin() throws Exception {
		CActivityRecord activityRecord = activityService.getDetail(Long.valueOf(129));
		Calendar timeMin = Calendar.getInstance();
		timeMin.set(Calendar.HOUR_OF_DAY, 9);
		timeMin.set(Calendar.MINUTE, 0);
		activityRecord.setTimeMin(timeMin.getTime());
		activityService.modify(activityRecord.getId(), activityRecord);
		
		CTimeStampRecord record = new CTimeStampRecord();
		record.setEmployeeId(loggedUserRecord.getUserId());
		record.setActivityId(activityRecord.getId());
		
		Calendar timeFrom = Calendar.getInstance();
		timeFrom.set(Calendar.HOUR_OF_DAY, 9);
		timeFrom.set(Calendar.MINUTE, 0);		
		record.setDateFrom(timeFrom.getTime());		
		CLockRecord lockRecord = timesheetService.add(record);	
		
		record = timesheetService.getDetail(lockRecord.getId());	
		
		timeFrom.set(Calendar.HOUR_OF_DAY, 8);
		timeFrom.set(Calendar.MINUTE, 59);		
		record.setDateFrom(timeFrom.getTime());

		lockRecord = timesheetService.modify(record);
	}
	
	/**
	 * test for activity time max check
	 * 
	 * @throws Exception
	 */
	@Test(expected=CBusinessException.class)
	@Transactional
	public void testCheckActivityTimeMax() throws Exception {
		CActivityRecord activityRecord = activityService.getDetail(Long.valueOf(129));
		Calendar timeMax = Calendar.getInstance();
		timeMax.set(Calendar.HOUR_OF_DAY, 14);
		timeMax.set(Calendar.MINUTE, 59);
		activityRecord.setTimeMax(timeMax.getTime());
		activityService.modify(activityRecord.getId(), activityRecord);
		
		CTimeStampRecord record = new CTimeStampRecord();
		record.setEmployeeId(loggedUserRecord.getUserId());
		record.setActivityId(activityRecord.getId());
		
		Calendar timeFrom = Calendar.getInstance();
		timeFrom.set(Calendar.HOUR_OF_DAY, 9);
		timeFrom.set(Calendar.MINUTE, 0);		
		record.setDateFrom(timeFrom.getTime());		
		
		Calendar timeTo = Calendar.getInstance();
		timeTo.set(Calendar.HOUR_OF_DAY, 14);
		timeTo.set(Calendar.MINUTE, 59);		
		record.setDateTo(timeTo.getTime());		
		CLockRecord lockRecord = timesheetService.add(record);	
		
		record = timesheetService.getDetail(lockRecord.getId());	
		
		timeTo.set(Calendar.HOUR_OF_DAY, 15);
		timeTo.set(Calendar.MINUTE, 00);		
		record.setDateTo(timeTo.getTime());		

		lockRecord = timesheetService.modify(record);
	}
	
	/**
	 * test for activity hours max check
	 * 
	 * @throws Exception
	 */
	@Test(expected=CBusinessException.class)
	@Transactional
	public void testCheckActivityHoursMax() throws Exception {
		CActivityRecord activityRecord = activityService.getDetail(Long.valueOf(129));
		activityRecord.setHoursMax(6);
		activityService.modify(activityRecord.getId(), activityRecord);
		
		CTimeStampRecord record = new CTimeStampRecord();
		record.setEmployeeId(loggedUserRecord.getUserId());
		record.setActivityId(activityRecord.getId());
		
		Calendar timeFrom = Calendar.getInstance();
		timeFrom.set(Calendar.HOUR_OF_DAY, 9);
		timeFrom.set(Calendar.MINUTE, 0);		
		record.setDateFrom(timeFrom.getTime());		
		
		Calendar timeTo = Calendar.getInstance();
		timeTo.set(Calendar.HOUR_OF_DAY, 14);
		timeTo.set(Calendar.MINUTE, 59);		
		record.setDateTo(timeTo.getTime());		
		CLockRecord lockRecord = timesheetService.add(record);	
		
		record = timesheetService.getDetail(lockRecord.getId());	
		
		timeTo.set(Calendar.HOUR_OF_DAY, 15);
		timeTo.set(Calendar.MINUTE, 00);		
		record.setDateTo(timeTo.getTime());		

		lockRecord = timesheetService.modify(record);
	}
}
