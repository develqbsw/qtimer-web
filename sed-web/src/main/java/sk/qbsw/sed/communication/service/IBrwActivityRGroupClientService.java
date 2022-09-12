package sk.qbsw.sed.communication.service;

import java.util.List;

import sk.qbsw.sed.client.model.restriction.CGroupsAIData;
import sk.qbsw.sed.fw.exception.CBussinessDataException;

public interface IBrwActivityRGroupClientService {

	/**
	 * Loads data for table. Returns list of activity restriction group entities for
	 * table rows.
	 * 
	 * @param first        first entity in ordered list
	 * @param count        number of entities in ordered list from 'first'
	 * @param sortProperty property for ordering
	 * @param sortAsc      direction of ordering
	 * @return list of groups
	 * @return
	 * @throws CBussinessDataException
	 */
	public List<CGroupsAIData> loadData(Long first, Long count, String sortProperty, boolean sortAsc) throws CBussinessDataException;

	/**
	 * Returns number of all clients activity restriction groups.
	 * 
	 * @return number of groups
	 * @throws CBussinessDataExceptionin error case
	 */
	public Long count() throws CBussinessDataException;
}
