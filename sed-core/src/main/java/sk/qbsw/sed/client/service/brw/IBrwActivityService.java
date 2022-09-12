package sk.qbsw.sed.client.service.brw;

import java.util.List;

import sk.qbsw.sed.client.model.codelist.CActivityRecord;
import sk.qbsw.sed.framework.security.exception.CBusinessException;

public interface IBrwActivityService {
	/**
	 * Loads data for table page. Returns list of activity entities for table rows.
	 * 
	 * @param first        first entity in ordered list
	 * @param count        number of entities in ordered list from 'first'
	 * @param sortProperty property for ordering
	 * @param sortAsc      direction of ordering
	 * @return
	 * @throws CBusinessException
	 */
	public List<CActivityRecord> loadData(Integer startRow, Integer endRow, String sortProperty, boolean sortAsc) throws CBusinessException;

	/**
	 * Returns number of all clients activity records.
	 * 
	 * @return number of activity records
	 * @throws CBusinessException
	 */
	public Long count() throws CBusinessException;
}
