package sk.qbsw.sed.client.service.brw;

import java.util.List;

import sk.qbsw.sed.client.model.restriction.CGroupsAIData;
import sk.qbsw.sed.framework.security.exception.CBusinessException;

public interface IBrwGroupsAIService {
	
	/**
	 * Loads data for table page. Returns list of activity restriction group
	 * entities for table rows.
	 * 
	 * @param first        first entity in ordered list
	 * @param count        number of entities in ordered list from 'first'
	 * @param sortProperty property for ordering
	 * @param sortAsc      direction of ordering
	 * @return list of groups
	 * @throws CBusinessException
	 */
	public List<CGroupsAIData> loadData(Integer startRow, Integer endRow, String sortProperty, boolean sortAsc) throws CBusinessException;

	/**
	 * Returns number of all clients activity restriction group records.
	 * 
	 * @return number of activity restriction group records
	 * @throws CBusinessException
	 */
	public Long count() throws CBusinessException;
}
