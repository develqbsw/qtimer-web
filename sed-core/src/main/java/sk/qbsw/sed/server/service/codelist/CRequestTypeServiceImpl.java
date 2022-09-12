package sk.qbsw.sed.server.service.codelist;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.model.ISearchConstants;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.service.codelist.IRequestTypeService;
import sk.qbsw.sed.server.dao.IRequestTypeDao;
import sk.qbsw.sed.server.model.codelist.CRequestType;

/**
 * Service for management of request types code list
 * 
 * @author Dalibor Rak
 * @since 0.1
 * @version 0.1
 * 
 */
@Service(value = "requestTypeService")
public class CRequestTypeServiceImpl implements IRequestTypeService {

	@Autowired
	private IRequestTypeDao requestTypeDao;

	@Transactional(readOnly = true)
	public List<CCodeListRecord> getValidRecords(boolean addNone) {
		List<CCodeListRecord> values = convert(requestTypeDao.findAllValid());
		if (addNone) {
			addNone(values);
		}

		return values;
	}

	@Transactional(readOnly = true)
	public List<CCodeListRecord> getValidRecordsForRequestReason() {
		List<CRequestType> inputRequestTypes = requestTypeDao.findRecordsForRequestReason();
		return convert(inputRequestTypes);
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
	 * @param entities
	 * @return
	 */
	private List<CCodeListRecord> convert(List<CRequestType> entities) {
		List<CCodeListRecord> retVal = new ArrayList<>();
		for (CRequestType entity : entities) {
			CCodeListRecord clientRecord = new CCodeListRecord();
			clientRecord.setId(entity.getId());
			clientRecord.setName(entity.getDescription());
			clientRecord.setDescription(entity.getDescription());
			clientRecord.setCode(entity.getCode());

			retVal.add(clientRecord);
		}
		return retVal;
	}
}
