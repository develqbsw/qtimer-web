package sk.qbsw.sed.client.service.codelist;

import java.util.List;

import sk.qbsw.sed.client.model.codelist.CCodeListRecord;

public interface IRequestStatusService {
	
	/**
	 * Returns list of valid request status records.
	 * 
	 * @param addNone - add 'none' record to list flag
	 * @return list of records
	 */
	public List<CCodeListRecord> getValidRecords(boolean addNone);
}
