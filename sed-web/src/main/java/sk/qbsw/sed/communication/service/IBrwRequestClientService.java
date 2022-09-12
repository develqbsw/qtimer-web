package sk.qbsw.sed.communication.service;

import java.util.List;

import sk.qbsw.sed.client.model.request.CRequestRecord;
import sk.qbsw.sed.client.model.request.CRequestRecordForGraph;
import sk.qbsw.sed.client.model.table.IFilterCriteria;
import sk.qbsw.sed.fw.exception.CBussinessDataException;

public interface IBrwRequestClientService {

	public List<CRequestRecord> loadData(Long first, Long count, String sortProperty, boolean sortAsc, IFilterCriteria criteria) throws CBussinessDataException;

	public Long count(IFilterCriteria criteria) throws CBussinessDataException;

	public List<CRequestRecordForGraph> loadDataForGraph(IFilterCriteria criteria) throws CBussinessDataException;
}
