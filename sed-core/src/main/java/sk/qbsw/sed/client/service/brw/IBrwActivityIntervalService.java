package sk.qbsw.sed.client.service.brw;

import java.util.List;

import sk.qbsw.sed.client.model.restriction.CActivityIntervalData;
import sk.qbsw.sed.framework.security.exception.CBusinessException;

public interface IBrwActivityIntervalService {

	/**
	 * Loads data for table. Returns list of activity restriction interval entities
	 * for table rows.
	 * 
	 * @param first        first entity in ordered list
	 * @param count        number of entities in ordered list from 'first'
	 * @param sortProperty property for ordering
	 * @param sortAsc      direction of ordering
	 * @return list of intervals
	 * @throws CBusinessException in error case
	 */
	public List<CActivityIntervalData> loadData(Integer startRow, Integer endRow, String sortProperty, boolean sortAsc) throws CBusinessException;

	/**
	 * Returns number of all clients activity restriction intervals.
	 * 
	 * @return number of intervals
	 * @throws CBusinessException error case
	 */
	public Long count() throws CBusinessException;
}
