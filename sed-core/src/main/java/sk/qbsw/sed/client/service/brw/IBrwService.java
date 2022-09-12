package sk.qbsw.sed.client.service.brw;

import java.util.List;

import sk.qbsw.sed.client.model.table.IFilterCriteria;
import sk.qbsw.sed.framework.security.exception.CBusinessException;

public interface IBrwService<T> {

	/**
	 * 
	 * @param startRow
	 * @param endRow
	 * @param sortProperty
	 * @param sortAsc
	 * @param criteria
	 * @return
	 * @throws CBusinessException
	 */
	public List<T> loadData(Integer startRow, Integer endRow, String sortProperty, boolean sortAsc, IFilterCriteria criteria) throws CBusinessException;

	/**
	 * 
	 * @param criteria
	 * @return
	 * @throws CBusinessException
	 */
	public Long count(IFilterCriteria criteria) throws CBusinessException;
}
