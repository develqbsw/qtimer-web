package sk.qbsw.sed.client.service.codelist;

import java.util.List;

import sk.qbsw.sed.client.model.codelist.CCodeListRecord;

public interface IRequestTypeService {

	public List<CCodeListRecord> getValidRecords(boolean addNone);

	public List<CCodeListRecord> getValidRecordsForRequestReason();
}
