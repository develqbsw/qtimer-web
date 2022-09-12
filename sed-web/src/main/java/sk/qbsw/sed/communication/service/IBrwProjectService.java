package sk.qbsw.sed.communication.service;

import java.util.List;

import sk.qbsw.sed.client.model.codelist.CProjectRecord;
import sk.qbsw.sed.client.model.table.IFilterCriteria;
import sk.qbsw.sed.fw.exception.CBussinessDataException;

public interface IBrwProjectService {

	/**
	 * 
	 * @param first
	 * @param count
	 * @param sortProperty
	 * @param sortAsc
	 * @param criteria
	 * @return
	 * @throws CBussinessDataException
	 */
	public List<CProjectRecord> loadData(Long first, Long count, String sortProperty, boolean sortAsc, IFilterCriteria criteria) throws CBussinessDataException;

	/**
	 * 
	 * @param criteria
	 * @return
	 * @throws CBussinessDataException
	 */
	public Long count(IFilterCriteria criteria) throws CBussinessDataException;
}
