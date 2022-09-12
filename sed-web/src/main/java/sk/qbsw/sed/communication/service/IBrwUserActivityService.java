package sk.qbsw.sed.communication.service;

import java.util.List;

import sk.qbsw.sed.client.model.brw.CUserActivityRecord;
import sk.qbsw.sed.client.model.table.IFilterCriteria;
import sk.qbsw.sed.fw.exception.CBussinessDataException;

/**
 * 
 * @author bado
 *
 */
public interface IBrwUserActivityService {

	/**
	 * @param first
	 * @param count
	 * @param sortProperty
	 * @param sortAsc
	 * @param criteria
	 * @return
	 * @throws CBussinessDataException
	 */
	public List<CUserActivityRecord> loadData(Long first, Long count, String sortProperty, boolean sortAsc, IFilterCriteria criteria) throws CBussinessDataException;

	/**
	 * @param criteria
	 * @return
	 * @throws CBussinessDataException
	 */
	public Long count(IFilterCriteria criteria) throws CBussinessDataException;
}
