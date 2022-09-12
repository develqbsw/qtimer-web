package sk.qbsw.sed.test.service;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.restriction.CRequestReasonData;
import sk.qbsw.sed.client.model.restriction.CRequestReasonListsData;
import sk.qbsw.sed.client.service.business.IRequestReasonService;
import sk.qbsw.sed.client.ui.localization.CClientExceptionsMessages;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.framework.security.exception.CSecurityException;
import sk.qbsw.sed.server.dao.IRequestReasonDao;
import sk.qbsw.sed.server.model.codelist.CRequestReason;
import sk.qbsw.sed.server.service.business.IRequestTypeConstant;
import sk.qbsw.sed.test.dao.ADaoTest;

/**
 * Class tests IRequestReasonService methods.
 * @author moravcik
 *
 */
public class CRequestReasonServiceTest extends ADaoTest {
	
	@Autowired
	private IRequestReasonService requestReasonService;
	
	@Autowired
	private IRequestReasonDao requestReasonDao;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		
		super.loginAsAdmin();
	}
	
	@After
	public void tearDown() throws Exception {}
	
	/**
	 * Tests adding new request reason.
	 * @throws CBusinessException 
	 * @throws CSecurityException 
	 */
	@Test
	@Transactional
	public void testAdd() throws CSecurityException, CBusinessException {
		//make new object request reason
		CRequestReasonData model = new CRequestReasonData();
		model.setCode("TEST654");
		model.setName("test name");
		model.setRequestTypeId((long) IRequestTypeConstant.RQTYPE_SICKNESS_ID);
		model.setValid(true);
		
		//save
		CRequestReasonData saved = requestReasonService.save(model);
		
		//check if saved by id
		CRequestReason found = requestReasonDao.findById(saved.getId());
		assertEquals(model.getCode(), found.getCode());
		assertEquals(model.getName(), found.getReasonName());
		assertEquals(model.getRequestTypeId(), found.getRequestType().getId());
		assertEquals(model.getValid(), found.getValid());
	}
	
	/**
	 * Tests method getDetail.
	 */
	@Test
	@Transactional
	public void testGetDetail() {
		CRequestReason record = requestReasonDao.findAll().get(0);
		
		CRequestReasonData detail = requestReasonService.getDetail(record.getId());
		assertEquals(record.getId(), detail.getId());
		assertEquals(record.getClient().getId(), detail.getClientId());
		assertEquals(record.getCode(), detail.getCode());
		assertEquals(record.getReasonName(), detail.getName());
		assertEquals(record.getRequestType().getId(), detail.getRequestTypeId());
		assertEquals(record.getValid(), detail.getValid());
		assertEquals(record.getSystem(), detail.getFlagSystem());

	}
	
	/**
	 * Tests throwing exception on modifying system record.
	 * @throws CSecurityException
	 * @throws CBusinessException
	 */
	@Test (expected=CBusinessException.class)
	@Transactional
	public void testModifyExistingSystemRecord() throws CSecurityException, CBusinessException {
		//find system request reason
		List<CRequestReason> list = requestReasonDao.findAll();
		CRequestReason systemReason = null;
		for (CRequestReason r : list) {
			if (r.getSystem()) {
				systemReason = r;
				break;
			}
		}
		
		//make it data object for save function
		CRequestReasonData model = new CRequestReasonData();
		model.setId(systemReason.getId());
		model.setName("new name");
		
		try {
			//save
			requestReasonService.save(model);
		} catch (CBusinessException e) {
			//check exception msg
			assertEquals(CClientExceptionsMessages.REQUEST_REASON_NOT_EDITABLE_RECORD, e.getMessage());
			throw e;
		} finally {
			//check not changed original
			CRequestReason original = requestReasonDao.findById(systemReason.getId());
			assertNotNull(original);
			assertFalse(original.getReasonName().equals(model.getName()));
		}	
	}

	/**
	 * Tests method getReasonLists.
	 * @throws CSecurityException
	 * @throws CBusinessException
	 */
	@Test
	@Transactional
	public void testGetReasonLists() throws CSecurityException, CBusinessException {
		CRequestReasonListsData listsBefore = requestReasonService.getReasonLists(null); //argument not used in function

		//make new object request reason
		CRequestReasonData modelS = new CRequestReasonData();
		modelS.setCode("TEST987");
		modelS.setName("test name sickness");
		modelS.setRequestTypeId((long) IRequestTypeConstant.RQTYPE_SICKNESS_ID);
		modelS.setValid(true);
		requestReasonService.save(modelS);
		
		//chceck if added
		CRequestReasonListsData listsAfterS = requestReasonService.getReasonLists(null); //argument not used in function
		assertEquals(listsBefore.getSicknessReasonList().size() + 1, listsAfterS.getSicknessReasonList().size());
		assertEquals(listsBefore.getWorkbreakReasonList().size(), listsAfterS.getWorkbreakReasonList().size());
		
		//make new object request reason
		CRequestReasonData modelW = new CRequestReasonData();
		modelW.setCode("TEST987987");
		modelW.setName("test name workbreak");
		modelW.setRequestTypeId((long) IRequestTypeConstant.RQTYPE_WORKBREAK_ID);
		modelW.setValid(true);
		requestReasonService.save(modelW);
		
		//chceck if added
		CRequestReasonListsData listsAfterW = requestReasonService.getReasonLists(null); //argument not used in function
		assertEquals(listsBefore.getSicknessReasonList().size() + 1, listsAfterW.getSicknessReasonList().size());
		assertEquals(listsBefore.getWorkbreakReasonList().size() + 1, listsAfterW.getWorkbreakReasonList().size());
		
		assertEquals(modelS.getName(), listsAfterW.getSicknessReasonList().get(listsAfterW.getSicknessReasonList().size() - 1).getName());	
		assertEquals(modelW.getName(), listsAfterW.getWorkbreakReasonList().get(listsAfterW.getWorkbreakReasonList().size() - 1).getName());	

	}
	
	/**
	 * Tests method getReasonListsForListbox.
	 * @throws CBusinessException 
	 */
	@Test
	@Transactional
	public void testGetReasonListsForListbox() throws CBusinessException {
		List<CCodeListRecord> listBefore = requestReasonService.getReasonListsForListbox(); 

		//make new object request reason
		CRequestReasonData modelS = new CRequestReasonData();
		modelS.setCode("TEST987");
		modelS.setName("test name sickness");
		modelS.setRequestTypeId((long) IRequestTypeConstant.RQTYPE_SICKNESS_ID);
		modelS.setValid(true);
		requestReasonService.save(modelS);
		
		//chceck if added
		List<CCodeListRecord> listAfterS = requestReasonService.getReasonListsForListbox(); 
		assertEquals(listBefore.size() + 1, listAfterS.size());
		
		//make new object request reason
		CRequestReasonData modelW = new CRequestReasonData();
		modelW.setCode("TEST987987");
		modelW.setName("test name workbreak");
		modelW.setRequestTypeId((long) IRequestTypeConstant.RQTYPE_WORKBREAK_ID);
		modelW.setValid(true);
		requestReasonService.save(modelW);
		
		//chceck if added
		List<CCodeListRecord> listAfterW = requestReasonService.getReasonListsForListbox(); 
		assertEquals(listBefore.size() + 2, listAfterW.size());
		
		boolean inListS = false;
		boolean inListW = false;
		for (CCodeListRecord r : listAfterW) {
			if (r.getName().contains(modelS.getName())) {
				inListS = true;
			}
			if (r.getName().contains(modelW.getName())) {
				inListW = true;
			}
		}
		assertEquals(true, inListS);
		assertEquals(true, inListW);
	}
}
