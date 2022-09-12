package sk.qbsw.sed.client.service.brw;

import java.util.List;

import sk.qbsw.sed.client.model.codelist.CRequestReasonRecord;
import sk.qbsw.sed.framework.security.exception.CBusinessException;

public interface IBrwRequestReasonService {

	/**
	 * 
	 * @param startRow
	 * @param endRow
	 * @param sortProperty
	 * @param sortAsc
	 * @return
	 * @throws CBusinessException
	 */
	public List<CRequestReasonRecord> loadData(Integer startRow, Integer endRow, String sortProperty, boolean sortAsc) throws CBusinessException;

	/**
	 * 
	 * @return
	 * @throws CBusinessException
	 */
	public Long count() throws CBusinessException;
}
