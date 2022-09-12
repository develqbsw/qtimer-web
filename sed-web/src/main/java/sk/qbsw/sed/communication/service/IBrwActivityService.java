package sk.qbsw.sed.communication.service;

import java.util.List;

import sk.qbsw.sed.client.model.codelist.CActivityRecord;
import sk.qbsw.sed.fw.exception.CBussinessDataException;

public interface IBrwActivityService {

	/**
	 * Loads data for table page. Returns list of activity entities for table rows.
	 * 
	 * @param first        first entity in ordered list
	 * @param count        number of entities in ordered list from 'first'
	 * @param sortProperty property for ordering
	 * @param sortAsc      direction of ordering
	 * @return list of activities
	 * @throws CBussinessDataException
	 */
	public List<CActivityRecord> loadData(Long first, Long count, String sortProperty, boolean sortAsc) throws CBussinessDataException;

	/**
	 * Returns number of all clients activity records.
	 * 
	 * @return number of activity records
	 * @throws CBussinessDataException
	 */
	public Long count() throws CBussinessDataException;
}
