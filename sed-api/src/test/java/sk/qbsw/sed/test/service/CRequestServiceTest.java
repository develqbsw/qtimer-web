package sk.qbsw.sed.test.service;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.model.IRequestStates;
import sk.qbsw.sed.client.model.detail.CUserDetailRecord;
import sk.qbsw.sed.client.model.request.CMyRequestsBrwFilterCriteria;
import sk.qbsw.sed.client.model.request.CRequestRecord;
import sk.qbsw.sed.client.model.request.CRequestRecordForGraph;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.service.brw.IBrwRequestService;
import sk.qbsw.sed.client.ui.localization.CClientExceptionsMessages;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.server.dao.IHolidayDao;
import sk.qbsw.sed.server.dao.IRequestDao;
import sk.qbsw.sed.server.dao.IRequestReasonDao;
import sk.qbsw.sed.server.model.codelist.CHoliday;
import sk.qbsw.sed.server.model.codelist.CRequestReason;
import sk.qbsw.sed.server.service.business.IRequestTypeConstant;
import sk.qbsw.sed.server.util.CDateServerUtils;
import sk.qbsw.sed.test.dao.ADaoTest;

/***
 * Class for testing IBrwRequestService methods.
 * @author moravcik
 *
 */
public class CRequestServiceTest extends ADaoTest {

	private CLoggedUserRecord loggedUserRecord;
	
	@Autowired
	private IBrwRequestService brwRequestService;
	
	@Autowired
	private IRequestReasonDao requestReasonDao;
	
	@Autowired
	private IRequestDao requestDao;
	
	@Autowired
	private IHolidayDao holidayDao;
	
	//constants
	private static Long REASON_FUNERAL;


	@Before
	public void setUp() throws Exception {
		super.setUp();
		
		loggedUserRecord = super.loginAsTestUser();
		
		REASON_FUNERAL = 100L;
		List<CRequestReason> reasons = requestReasonDao.findAll();
		for (CRequestReason r : reasons) {
			if ("FUNERAL".equals(r.getCode())) {
				REASON_FUNERAL = r.getId();
				break;
			}
		}

	}

	@After
	public void tearDown() throws Exception {
	}
	
	/**
	 * Tests adding, getting detail, modifying and canceling request.
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	@Transactional
	public void testAddGetDetailModifyCancel() throws Exception {
		Long requestType = null;
		CRequestRecord model = null;
		boolean ignoreDuplicity = false;
		
		requestType = (long) IRequestTypeConstant.RQTYPE_VACATION_ID; 
		
		List<CHoliday> holidays = holidayDao.findAllValidClientsHolidays(loggedUserRecord.getClientInfo().getClientId(),null);
		boolean isHolidayToday = !CDateServerUtils.isWorkingDayWithCheckHolidays(new Date(), holidays);
		
		//set request model
		model = new CRequestRecord();
		model.setOwnerId(loggedUserRecord.getUserId());
		model.setDateFrom(new Date());
		model.setDateTo(new Date()); 
		model.setCreateDate(new Date());
		model.setNote("default note");
		model.setPlace("default place");
		model.setFullday(true); 
		model.setTypeId((long) IRequestTypeConstant.RQTYPE_VACATION_ID);
		model.setRequestReasonId(REASON_FUNERAL);

		//add
		String retVal = super.requestService.add(requestType, model, ignoreDuplicity);
		assertEquals("", retVal);
		
		//set filter criteria to find added request
		CMyRequestsBrwFilterCriteria criteria = new CMyRequestsBrwFilterCriteria();
		criteria.setDateFrom(model.getDateFrom());
		criteria.setDateTo(model.getDateTo());
		criteria.setTypeId(requestType);
		
		//find that request
		CRequestRecord addedRequestRecord = brwRequestService.fetch(0, 0, criteria).get(0);
		
		//getDetail
		addedRequestRecord = super.requestService.getDetail(addedRequestRecord.getId());
		assertEquals(IRequestStates.ID_CREATED, addedRequestRecord.getStatusId());
		assertEquals(model.getOwnerId(), addedRequestRecord.getOwnerId());
		assertEquals(model.getDateFrom().getDate(), addedRequestRecord.getDateFrom().getDate());
		assertEquals(model.getDateFrom().getMonth(), addedRequestRecord.getDateFrom().getMonth());
		assertEquals(model.getDateFrom().getYear(), addedRequestRecord.getDateFrom().getYear());
		assertEquals(model.getDateTo().getDate(), addedRequestRecord.getDateTo().getDate());
		assertEquals(model.getDateTo().getMonth(), addedRequestRecord.getDateTo().getMonth());
		assertEquals(model.getDateTo().getYear(), addedRequestRecord.getDateTo().getYear());
		assertEquals(false, addedRequestRecord.getHalfday());
		assertEquals("default note", addedRequestRecord.getNote());
		assertEquals(new Float(isHolidayToday ? 0 : 1), new Float(addedRequestRecord.getWorkDays()));
		assertEquals(IRequestTypeConstant.RQTYPE_VACATION_ID, addedRequestRecord.getTypeId().intValue());
		assertEquals(REASON_FUNERAL, addedRequestRecord.getRequestReasonId());

		
		//modify
		model.setFullday(false);
		model.setHours(4);
		model.setWorkDays(0.5f);
		model.setNote("modified note");
		super.requestService.modify(addedRequestRecord.getId(), model, ignoreDuplicity);
		CRequestRecord modifiedRequestRecord = super.requestService.getDetail(addedRequestRecord.getId());
		assertEquals(addedRequestRecord.getId(), modifiedRequestRecord.getId());
		assertEquals(true, modifiedRequestRecord.getHalfday());
		assertEquals(4, modifiedRequestRecord.getHours());
		assertEquals("modified note", modifiedRequestRecord.getNote());
		assertEquals(new Float(0.5), new Float(modifiedRequestRecord.getWorkDays()));
		
		//cancel
		super.requestService.cancel(addedRequestRecord.getId());
		CRequestRecord cancelledRequestRecord = super.requestService.getDetail(addedRequestRecord.getId());
		assertEquals(addedRequestRecord.getId(), cancelledRequestRecord.getId());
		assertEquals(IRequestStates.ID_CANCELLED, cancelledRequestRecord.getStatusId());
		
	}
	
	/**
	 * Tests request rejection.
	 * @throws Exception
	 */
	@Test
	@Transactional
	public void testReject() throws Exception {
		Long requestType = null;
		CRequestRecord model = null;
		boolean ignoreDuplicity = false;
		
		requestType = (long) IRequestTypeConstant.RQTYPE_VACATION_ID; 
		
		//set request model
		model = new CRequestRecord();
		model.setOwnerId(loggedUserRecord.getUserId());
		model.setDateFrom(new Date());
		model.setDateTo(new Date()); 
		model.setCreateDate(new Date());
		model.setNote("default note");
		model.setPlace("default place");
		model.setFullday(true); 
		model.setTypeId((long) IRequestTypeConstant.RQTYPE_VACATION_ID);
		model.setRequestReasonId(REASON_FUNERAL);

		//add
		String retVal = super.requestService.add(requestType, model, ignoreDuplicity);
		assertEquals("", retVal);
		
		//set filter criteria to find added request
		CMyRequestsBrwFilterCriteria criteria = new CMyRequestsBrwFilterCriteria();
		criteria.setDateFrom(model.getDateFrom());
		criteria.setDateTo(model.getDateTo());
		criteria.setTypeId(requestType);
		
		//find that request
		CRequestRecord addedRequestRecord = brwRequestService.fetch(0, 0, criteria).get(0);
		
		//reject
		super.loginAsAdmin();
		super.requestService.reject(addedRequestRecord.getId());
		
		//check
		CRequestRecord approvedRequestRecord = super.requestService.getDetail(addedRequestRecord.getId());
		assertEquals(IRequestStates.ID_REJECTED, approvedRequestRecord.getStatusId());
	}
	
	/**
	 * Tests request approval.
	 * @throws Exception
	 */
	@Test
	@Transactional
	public void testApprove() throws Exception {
		Long requestType = null;
		CRequestRecord model = null;
		boolean ignoreDuplicity = false;
		
		requestType = (long) IRequestTypeConstant.RQTYPE_VACATION_ID; 
		
		//set request model
		model = new CRequestRecord();
		model.setOwnerId(loggedUserRecord.getUserId());
		model.setDateFrom(new Date());
		model.setDateTo(new Date()); 
		model.setCreateDate(new Date());
		model.setNote("default note");
		model.setPlace("default place");
		model.setFullday(true); 
		model.setTypeId((long) IRequestTypeConstant.RQTYPE_VACATION_ID);
		model.setRequestReasonId(REASON_FUNERAL);

		//add
		String retVal = super.requestService.add(requestType, model, ignoreDuplicity);
		assertEquals("", retVal);
		
		//set filter criteria to find added request
		CMyRequestsBrwFilterCriteria criteria = new CMyRequestsBrwFilterCriteria();
		criteria.setDateFrom(model.getDateFrom());
		criteria.setDateTo(model.getDateTo());
		criteria.setTypeId(requestType);
		
		//find that request
		CRequestRecord addedRequestRecord = brwRequestService.fetch(0, 0, criteria).get(0);
		
		//approve
		super.loginAsAdmin();
		super.requestService.approve(addedRequestRecord.getId());
		
		//check
		CRequestRecord approvedRequestRecord = super.requestService.getDetail(addedRequestRecord.getId());
		assertEquals(IRequestStates.ID_APPROVED, approvedRequestRecord.getStatusId());		
	}
	
	
	
	/**
	 * Tests request rejection from email.
	 */
	@Test
	@Transactional
	public void testRejectFromEmail() throws Exception {
		Long requestType = null;
		CRequestRecord model = null;
		boolean ignoreDuplicity = false;
		
		requestType = (long) IRequestTypeConstant.RQTYPE_VACATION_ID; 
		
		//set request model
		model = new CRequestRecord();
		model.setOwnerId(loggedUserRecord.getUserId());
		model.setDateFrom(new Date());
		model.setDateTo(new Date()); 
		model.setCreateDate(new Date());
		model.setNote("default note");
		model.setPlace("default place");
		model.setFullday(true); 
		model.setTypeId((long) IRequestTypeConstant.RQTYPE_VACATION_ID);
		model.setRequestReasonId(REASON_FUNERAL);

		//add
		String retVal = super.requestService.add(requestType, model, ignoreDuplicity);
		assertEquals("", retVal);
		
		//set filter criteria to find added request
		CMyRequestsBrwFilterCriteria criteria = new CMyRequestsBrwFilterCriteria();
		criteria.setDateFrom(model.getDateFrom());
		criteria.setDateTo(model.getDateTo());
		criteria.setTypeId(requestType);
		
		//find that request
		CRequestRecord addedRequestRecord = brwRequestService.fetch(0, 0, criteria).get(0);
		String code = requestDao.findById(addedRequestRecord.getId()).getCode();
		
		//reject
		super.loginAsAdmin();
		super.requestService.rejectRequestFromEmail(addedRequestRecord.getId().toString(), code);
		
		//check
		CRequestRecord approvedRequestRecord = super.requestService.getDetail(addedRequestRecord.getId());
		assertEquals(IRequestStates.ID_REJECTED, approvedRequestRecord.getStatusId());
	}
	
	/**
	 * Tests request approval from email.
	 */
	@Test
	@Transactional
	public void testApproveFromEmail() throws Exception {
		Long requestType = null;
		CRequestRecord model = null;
		boolean ignoreDuplicity = false;
		
		requestType = (long) IRequestTypeConstant.RQTYPE_VACATION_ID; 
		
		//set request model
		model = new CRequestRecord();
		model.setOwnerId(loggedUserRecord.getUserId());
		model.setDateFrom(new Date());
		model.setDateTo(new Date()); 
		model.setCreateDate(new Date());
		model.setNote("default note");
		model.setPlace("default place");
		model.setFullday(true); 
		model.setTypeId((long) IRequestTypeConstant.RQTYPE_VACATION_ID);
		model.setRequestReasonId(REASON_FUNERAL);

		//add
		String retVal = super.requestService.add(requestType, model, ignoreDuplicity);
		assertEquals("", retVal);
		
		//set filter criteria to find added request
		CMyRequestsBrwFilterCriteria criteria = new CMyRequestsBrwFilterCriteria();
		criteria.setDateFrom(model.getDateFrom());
		criteria.setDateTo(model.getDateTo());
		criteria.setTypeId(requestType);
		
		//find that request
		CRequestRecord addedRequestRecord = brwRequestService.fetch(0, 0, criteria).get(0);
		String code = requestDao.findById(addedRequestRecord.getId()).getCode();
		
		//approve
		super.loginAsAdmin();
		super.requestService.approveRequestFromEmail(addedRequestRecord.getId().toString(), code);
		
		//check
		CRequestRecord approvedRequestRecord = super.requestService.getDetail(addedRequestRecord.getId());
		assertEquals(IRequestStates.ID_APPROVED, approvedRequestRecord.getStatusId());
	}
	
	/**
	 * Tests overlaying requests.
	 * @throws Exception
	 */
	@Test (expected=CBusinessException.class)
	@Transactional
	public void testAddOverlay() throws Exception {
		Long requestType = null;
		CRequestRecord model = null;
		boolean ignoreDuplicity = false;
		
		requestType = (long) IRequestTypeConstant.RQTYPE_VACATION_ID;
		
		model = new CRequestRecord();
		model.setOwnerId(loggedUserRecord.getUserId());
		model.setDateFrom(new Date());
		model.setDateTo(new Date()); 
		model.setCreateDate(new Date());
		model.setNote("default note");
		model.setPlace("default place");
		model.setFullday(true); 
		model.setResponsalisName("default responsalis");

		//add first request
		String retVal = super.requestService.add(requestType, model, ignoreDuplicity);
		assertEquals("", retVal);
		
		//add second request
		model.setNote("second request");		
		String retVal2 = null;
		try {
			retVal2 = super.requestService.add(requestType, model, ignoreDuplicity);
		} catch (CBusinessException e) {
			//must throw 
			assertEquals(CClientExceptionsMessages.REQUEST_DUPLICITY, e.getMessage());
			throw e;
		} finally {
			//only firt one must be saved
			CMyRequestsBrwFilterCriteria criteria = new CMyRequestsBrwFilterCriteria();
			criteria.setDateFrom(model.getDateFrom());
			criteria.setDateTo(model.getDateTo());
			criteria.setTypeId(requestType);
			CRequestRecord addedRequestRecord = brwRequestService.fetch(0, 0, criteria).get(0);
			//check
			assertEquals(1, brwRequestService.fetch(0, 0, criteria).size());
			assertEquals(IRequestStates.ID_CREATED, addedRequestRecord.getStatusId());
			assertEquals("default note", addedRequestRecord.getNote());
			assertEquals(null, retVal2);
		}
	}
	
	/**
	 * Test loadDataForGraph
	 * @throws Exception
	 */
	@Transactional
	@Test
	public void testLoadDataForGraph() throws Exception {
		Long requestType = null;
		CRequestRecord model = null;
		
		requestType = (long) IRequestTypeConstant.RQTYPE_VACATION_ID; 
		
		Calendar dateFrom = Calendar.getInstance(); // stvrtok
		dateFrom.set(Calendar.DAY_OF_MONTH, 9);
		dateFrom.set(Calendar.MONTH, 5);
		dateFrom.set(Calendar.YEAR, 2016);
		
		Calendar dateTo = (Calendar) dateFrom.clone(); // pondelok
		dateTo.add(Calendar.DAY_OF_YEAR, 4);		
		
		//set request model
		model = new CRequestRecord();
		model.setOwnerId(loggedUserRecord.getUserId());
		model.setDateFrom(dateFrom.getTime());
		model.setDateTo(dateTo.getTime()); 
		model.setCreateDate(new Date());
		model.setNote("default note");
		model.setPlace("default place");
		model.setFullday(true); 
		model.setTypeId((long) IRequestTypeConstant.RQTYPE_VACATION_ID);
		model.setStatusId(IRequestStates.ID_CREATED);

		//add
		super.requestService.add(requestType, model, false);
		
		//add another with different status
		model.setStatusId(IRequestStates.ID_CANCELLED);
		super.requestService.add(requestType, model, true);
		
		CMyRequestsBrwFilterCriteria criteria = new CMyRequestsBrwFilterCriteria();
		criteria.setDateFrom(dateFrom.getTime());
		criteria.setDateTo(dateTo.getTime());
		
		List<CRequestRecordForGraph> list = brwRequestService.loadDataForGraph(criteria);
		
		assertEquals(3, list.size());
		assertEquals(IRequestStates.ID_CREATED, list.get(0).getStatusId());
		assertEquals(9, list.get(0).getDateFrom().get(Calendar.DAY_OF_MONTH)); // stvrtok az pondelok
		assertEquals(13, list.get(0).getDateTo().get(Calendar.DAY_OF_MONTH));
		assertEquals(11, list.get(1).getDateFrom().get(Calendar.DAY_OF_MONTH)); // sobota, nedela
		assertEquals(12, list.get(1).getDateTo().get(Calendar.DAY_OF_MONTH));
		assertEquals(18, list.get(2).getDateFrom().get(Calendar.DAY_OF_MONTH)); // sobota, nedela
		assertEquals(19, list.get(2).getDateTo().get(Calendar.DAY_OF_MONTH));
	}
	
	/**
	 * Test testUpdateRemainingDays
	 * @throws Exception
	 */
	@Transactional
	@Test
	public void testUpdateRemainingDays() throws Exception {
		Long requestType = null;
		CRequestRecord model = null;
		
		requestType = (long) IRequestTypeConstant.RQTYPE_VACATION_ID; 
		
		Calendar dateFrom = Calendar.getInstance(); // stvrtok
		dateFrom.set(Calendar.DAY_OF_MONTH, 9);
		dateFrom.set(Calendar.MONTH, 5);
		dateFrom.set(Calendar.YEAR, 2016);
		
		Calendar dateTo = (Calendar) dateFrom.clone(); // pondelok
		dateTo.add(Calendar.DAY_OF_YEAR, 4);		
		
		//set request model
		model = new CRequestRecord();
		model.setOwnerId(loggedUserRecord.getUserId());
		model.setDateFrom(dateFrom.getTime());
		model.setDateTo(dateTo.getTime()); 
		model.setCreateDate(new Date());
		model.setNote("default note");
		model.setPlace("default place");
		model.setFullday(true); 
		model.setTypeId((long) IRequestTypeConstant.RQTYPE_VACATION_ID);
		model.setStatusId(IRequestStates.ID_CREATED);

		CUserDetailRecord user = userService.getUserDetails(loggedUserRecord.getUserId());
		user.setVacation(Double.valueOf(20));
		userService.modify(user);
		
		//add
		super.requestService.add(requestType, model, false);
		
		user = userService.getUserDetails(loggedUserRecord.getUserId());
		
		assertEquals(Double.valueOf(20 - 3), user.getVacation());
	}
}
