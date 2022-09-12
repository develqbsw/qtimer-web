package sk.qbsw.sed.communication.service;

import java.util.List;

import sk.qbsw.sed.client.model.brw.CUserProjectRecord;
import sk.qbsw.sed.client.model.table.IFilterCriteria;
import sk.qbsw.sed.fw.exception.CBussinessDataException;

public interface IBrwUserProjectService {

	public List<CUserProjectRecord> loadData(Long first, Long count, String sortProperty, boolean sortAsc, IFilterCriteria criteria) throws CBussinessDataException;

	public Long count(IFilterCriteria criteria) throws CBussinessDataException;
}
