package sk.qbsw.sed.client.service.codelist;

import java.util.List;

import sk.qbsw.sed.client.model.codelist.CCodeListRecord;

public interface ILegalFormService {
	
	/**
	 * 
	 * @return
	 */
	public List<CCodeListRecord> getValidRecords();
}
