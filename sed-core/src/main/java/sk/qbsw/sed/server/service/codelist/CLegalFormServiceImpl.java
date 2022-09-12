package sk.qbsw.sed.server.service.codelist;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.service.codelist.ILegalFormService;
import sk.qbsw.sed.server.dao.ILegalFormDao;
import sk.qbsw.sed.server.model.codelist.CLegalForm;

/**
 * Service for management of Legacy form
 * 
 * @author Dalibor Rak
 * @since 0.1
 * @version 0.1
 * 
 */
@Service(value = "legalFormService")
public class CLegalFormServiceImpl implements ILegalFormService {
	
	@Autowired
	private ILegalFormDao legalFormDao;

	@Transactional(readOnly = true)
	public List<CCodeListRecord> getValidRecords() {
		List<CLegalForm> list = legalFormDao.getAllValid();
		return convert(list);
	}

	/**
	 * Converts db model to client model
	 * 
	 * @param input
	 * @return
	 */
	private List<CCodeListRecord> convert(List<CLegalForm> input) {
		List<CCodeListRecord> retVal = new ArrayList<>();
		for (CLegalForm legalForm : input) {
			CCodeListRecord newRec = new CCodeListRecord();
			newRec.setId(legalForm.getId());
			newRec.setCode(legalForm.getMsgCode());
			newRec.setName(legalForm.getDescription());
			retVal.add(newRec);
		}
		return retVal;
	}
}
