package sk.qbsw.sed.test.service;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.response.CUploadResponseContent;
import sk.qbsw.sed.client.ui.screen.codelist.upload.IUploadConstant;
import sk.qbsw.sed.server.service.upload.CProjectUploadProcess;
import sk.qbsw.sed.test.dao.ADaoTest;

/**
 * upload test
 * @author lobb
 *
 */
public class CProjectUploadProcessTest extends ADaoTest {

	@Autowired
	private CProjectUploadProcess projectUploadProcess;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		loginAsAdmin();	
	}
	
	/**
	 * upload test
	 * @throws Exception
	 */
	@Test
	@Transactional
	public void testUpload() throws Exception {
		
		String[] fileRows = new String [3];
		
		fileRows[0] = "3686¤ŠP.81.91 - ŠP-Riadenie výdavkov (2012/1Q)¤nie¤ŠP¤81.91";
		fileRows[1] = "3750¤ŠP.81.92 - ŠP-Riadenie výdavkov (2012/2Q)¤nie¤ŠP¤81.92";
		fileRows[2] = "3751¤ŠP.81.93 - ŠP-Riadenie výdavkov (2012/3Q)¤nie¤ŠP¤81.93";
		
		CUploadResponseContent response = projectUploadProcess.upload(fileRows);
		
		assertEquals(response.getResult(), IUploadConstant.UPLOAD_RESULT_OK);
	}
	
	/**
	 * upload wrong structure test
	 * @throws Exception
	 */
	@Test
	@Transactional
	public void testUploadWrongStructure() throws Exception {
		
		String[] fileRows = new String [3];
		
		fileRows[0] = "3686¤ŠP.81.91 - ŠP-Riadenie výdavkov (2012/1Q)¤nie¤ŠP¤81.91";
		fileRows[1] = "3750¤ŠP.81.92 - ŠP-Riadenie výdavkov";
		fileRows[2] = "3751¤ŠP.81.93 - ŠP-Riadenie výdavkov (2012/3Q)¤nie¤ŠP¤81.93";
		
		CUploadResponseContent response = projectUploadProcess.upload(fileRows);
		
		assertEquals(response.getResult(), IUploadConstant.UPLOAD_RESULT_ERR2);
		assertEquals(response.getAdditionalInfo(), fileRows[1]);
	}
}
