package sk.qbsw.sed.communication.service;

import java.util.List;

import sk.qbsw.sed.client.model.restriction.CActivityIntervalData;
import sk.qbsw.sed.fw.exception.CBussinessDataException;

public interface IBrwActivityRIntervalClientService {

	/**
	 * Loads data for table. Returns list of activity restriction interval entities
	 * for table rows.
	 * 
	 * @param first        first entity in ordered list
	 * @param count        number of entities in ordered list from 'first'
	 * @param sortProperty property for ordering
	 * @param sortAsc      direction of ordering
	 * @return list of intervals
	 * @throws CBussinessDataException in error case
	 */
	public List<CActivityIntervalData> loadData(Long first, Long count, String sortProperty, boolean sortAsc) throws CBussinessDataException;

	/**
	 * Returns number of all clients activity restriction intervals.
	 * 
	 * @return number of intervals
	 * @throws CBussinessDataExceptionin error case
	 */
	public Long count() throws CBussinessDataException;
}
