package sk.qbsw.sed.test.service;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.model.ILanguageConstant;
import sk.qbsw.sed.client.model.detail.CClientDetailRecord;
import sk.qbsw.sed.client.model.registration.CRegistrationClientRecord;
import sk.qbsw.sed.client.model.security.CClientInfo;
import sk.qbsw.sed.client.service.business.IClientService;
import sk.qbsw.sed.client.ui.localization.CClientExceptionsMessages;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.framework.security.exception.CSecurityException;
import sk.qbsw.sed.server.dao.IClientDao;
import sk.qbsw.sed.server.dao.ILegalFormDao;
import sk.qbsw.sed.server.model.domain.CClient;
import sk.qbsw.sed.test.dao.ADaoTest;

/**
 * Class for testing IClientService methods.
 * @author moravcik
 *
 */
public class CClientServiceTest extends ADaoTest {

	@Autowired
	private IClientService clientService;
	
	@Autowired
	public IClientDao clientDao;
	
	@Autowired
	private ILegalFormDao legalFormDao;
	
	private Long legalForm;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		super.loginAsAdmin();
		legalForm = legalFormDao.getAllValid().get(0).getId();
	}
	
	@After
	public void tearDown() throws Exception {}
	
	/**
	 * Tests adding new client.
	 * @throws CBusinessException
	 */
	@Test 
	@Transactional
	public void testAddClient() throws CBusinessException {
		//create new instance
		CRegistrationClientRecord rec = new CRegistrationClientRecord();
		rec.setCountry("estCountry68432");
		rec.setCity("TestCity5486");
		rec.setStreet("TestStreet5168");
		rec.setStreetNo("TestStreetNum46848");
		rec.setZip("TestZip98734");
		rec.setOrgName("TestOrgName455458763");
		rec.setIdNo("98989898");
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

	}
	
	/**
	 * Tests adding new client with identification number, tax number 
	 * or vat number of client already persistent in DB. 
	 * Adding must throw exception with specific message and not save the record.
	 * @throws CBusinessException
	 */
	@Test 
	@Transactional
	public void testAddClientWithUsedIdNoTaxVat() throws CBusinessException {
		//find record with ident tax and vat numbers
		CClient exsistingClient = null;
		List<CClient> list = clientDao.getApplicationClients();
		for (CClient c : list) {
			if (c.getIdentificationNumber() != null 
					&& c.getIdentificationNumber().length() > 0 
					&& c.getTaxNumber() != null 
					&& c.getTaxNumber().length() > 0 
					&& c.getTaxVatNumber() != null 
					&& c.getTaxVatNumber().length() > 0) {
				exsistingClient = c;
				break;
			}
		}

		//initialize new client 
		CRegistrationClientRecord rec = new CRegistrationClientRecord();
		rec.setCountry("estCountry68432");
		rec.setCity("TestCity5486");
		rec.setStreet("TestStreet5168");
		rec.setStreetNo("TestStreetNum46848");
		rec.setZip("TestZip98734");
		rec.setOrgName("TestOrgName455458763");
		rec.setLegacyForm(legalForm);

		//try adding with matching numbers. must throw exception.
		Long newId = null;
		try {
			rec.setIdNo(exsistingClient.getIdentificationNumber());
			rec.setTaxNo("8787878787");
			rec.setVatNo("878787878787");
			newId = clientService.add(rec);
		} catch (CBusinessException e) {
			assertEquals(CClientExceptionsMessages.CLIENT_ID_USED, e.getMessage());
		} finally {
			assertEquals(null, newId);
		}
		
		newId = null;
		try {
			rec.setIdNo("87878787");
			rec.setTaxNo(exsistingClient.getTaxNumber());
			rec.setVatNo("878787878787");
			newId = clientService.add(rec);
		} catch (CBusinessException e) {
			assertEquals(CClientExceptionsMessages.CLIENT_TAX_USED, e.getMessage());
		} finally {
			assertEquals(null, newId);
		}
		
		newId = null;
		try {
			rec.setIdNo("87878787");
			rec.setTaxNo("8787878787");
			rec.setVatNo(exsistingClient.getTaxVatNumber());
			newId = clientService.add(rec);
		} catch (CBusinessException e) {
			assertEquals(CClientExceptionsMessages.CLIENT_VAT_USED, e.getMessage());
		} finally {
			assertNull(newId);
		}
	}
	
	/**
	 * Tests method updateFlags.
	 * @throws CSecurityException
	 * @throws CBusinessException
	 */
	@Test
	@Transactional
	public void testUpdateFlags() throws CSecurityException, CBusinessException {
		//get client
		CClient client = clientDao.getApplicationClients().get(0);
		//make new client info. set flags inverted
		CClientInfo cInfo = new CClientInfo();
		cInfo.setClientId(client.getId());
		cInfo.setProjectRequired(!client.getProjectRequired());
		cInfo.setActivityRequired(!client.getActivityRequired());
		//update flags
		clientService.updateFlags(cInfo);
		
		//check
		client = clientDao.getApplicationClients().get(0);
		assertEquals(cInfo.getClientId(), client.getId());
		assertEquals(cInfo.getProjectRequired(), client.getProjectRequired());
		assertEquals(cInfo.getActivityRequired(), client.getActivityRequired());
	}
	
	/**
	 * Tests method getDetail.
	 * @throws CBusinessException 
	 */
	@Test
	@Transactional
	public void testGetDetail() throws CBusinessException {
		//get client
		CClient client = clientDao.getApplicationClients().get(0);
		//get detail
		CClientDetailRecord detail = clientService.getDetail(client.getId());
		//check
		assertEquals(client.getId(), detail.getClientId());
		assertEquals(client.getName(), detail.getName());
		assertEquals(client.getNameShort(), detail.getNameShort());
		assertEquals(client.getLegalForm().getId(), detail.getLegalForm().getId());
		assertEquals(client.getCity(), detail.getCity());
		assertEquals(client.getStreet(), detail.getStreet());
		assertEquals(client.getStreetNumber(), detail.getStreetNumber());
		assertEquals(client.getZip(), detail.getZipCode());
		assertEquals(client.getProjectRequired(), detail.getProjectRequiredFlag());
		assertEquals(client.getActivityRequired(), detail.getActivityRequiredFlag());
		assertEquals(client.getLanguage().equals("sk") ? ILanguageConstant.ID_SK : ILanguageConstant.ID_EN, detail.getLanguage());
		assertEquals(client.getGenerateMessages(), detail.getGenerateMessages());
	}
	
	/**
	 * Tests method updateDetail.
	 * @throws CBusinessException 
	 */
	@Test (expected=CBusinessException.class)
	@Transactional
	public void testUpdateDetail() throws CBusinessException {
		//get client detail, modify, update
		CClient client = clientDao.getApplicationClients().get(0);
		CClientDetailRecord detail = clientService.getDetail(client.getId());
		detail.setName("TestName684354");
		clientService.updateDetail(detail);
		//check
		CClientDetailRecord modifiedDetail = clientService.getDetail(client.getId());
		assertEquals(detail.getName(), modifiedDetail.getName());
		
		//client not found exception
		try {
			detail.setClientId(-46864L);
			clientService.updateDetail(detail);
		} catch (CBusinessException e) {
			assertEquals(CClientExceptionsMessages.CLIENT_NOT_FOUND, e.getMessage());
			throw e;
		} 
	}
}
