package sk.qbsw.sed.test.dao;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.server.dao.ITimesheetRecordDao;
import sk.qbsw.sed.server.model.domain.CTimeSheetRecord;

public class CTimesheetDaoTest extends ADaoTest {

	@Autowired
	protected ITimesheetRecordDao timesheetRecordDao;

	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
	}
	

	@Test
	@Transactional
	public void testFindById() {
		CTimeSheetRecord record = timesheetRecordDao.findById(230L);

		assertNotNull(record);
		
		record.setNote("note");
		
		timesheetRecordDao.saveOrUpdate(record);
		
		record = timesheetRecordDao.findById(230L);
		
		assertEquals(record.getNote(), "note");

	}


}
