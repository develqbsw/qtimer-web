package sk.qbsw.sed.client.service.brw;

import java.util.List;

import sk.qbsw.sed.client.ui.screen.restriction.users.CEmployeeRecord;
import sk.qbsw.sed.framework.security.exception.CBusinessException;

public interface IBrwEmployeeService {
	
	/**
	 * 
	 * @param startRow
	 * @param endRow
	 * @param sortProperty
	 * @param sortAsc
	 * @param name
	 * @return
	 * @throws CBusinessException
	 */
	public List<CEmployeeRecord> loadData(Integer startRow, Integer endRow, String sortProperty, boolean sortAsc, String name) throws CBusinessException;

	/**
	 * 
	 * @param name
	 * @return
	 * @throws CBusinessException
	 */
	public Long count(String name) throws CBusinessException;
}
