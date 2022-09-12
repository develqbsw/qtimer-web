package sk.qbsw.sed.server.service.codelist;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.model.ISearchConstants;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.service.codelist.IRequestStatusService;
import sk.qbsw.sed.server.dao.IRequestStateDao;
import sk.qbsw.sed.server.model.codelist.CRequestStatus;

/**
 * Service for management of request status code list
 * 
 * @author Dalibor Rak
 * @since 0.1
 * @version 0.1
 * 
 */
@Service(value = "requestStatusService")
public class CRequestStatusServiceImpl implements IRequestStatusService {
	
	@Autowired
	private IRequestStateDao requestStateDao;

	@Transactional(readOnly = true)
	public List<CCodeListRecord> getValidRecords(boolean addNone) {
		List<CCodeListRecord> values = convert(requestStateDao.findAllValid());
		if (addNone) {
			addNone(values);
		}

		return values;
	}

	/**
	 * Adds value for codelists with no description and NONE idenfier in search
	 * 
	 * @param list list where to add constant
	 * @return
	 */
	private List<CCodeListRecord> addNone(List<CCodeListRecord> list) {
		CCodeListRecord newRec = new CCodeListRecord();
		newRec.setId(ISearchConstants.NONE);
		newRec.setName("");
		newRec.setDescription("");
		list.add(newRec);

		return list;
	}

	/**
	 * Converts db model to client model
	 * 
	 * @param input
	 * @return
	 */
	private List<CCodeListRecord> convert(List<CRequestStatus> input) {
		List<CCodeListRecord> retVal = new ArrayList<>();
		for (CRequestStatus project : input) {
			CCodeListRecord newRec = new CCodeListRecord();
			newRec.setId(project.getId());
			newRec.setName(project.getDescription());
			newRec.setDescription(project.getDescription());
			retVal.add(newRec);
		}
		return retVal;
	}
}
