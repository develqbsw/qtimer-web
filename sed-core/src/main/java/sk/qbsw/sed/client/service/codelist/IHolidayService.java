package sk.qbsw.sed.client.service.codelist;

import java.util.List;

import sk.qbsw.sed.client.model.codelist.CHolidayRecord;
import sk.qbsw.sed.client.request.CModifyHolidayRequest;
import sk.qbsw.sed.framework.security.exception.CBusinessException;

public interface IHolidayService {

	/**
	 * Reads detail of holiday record
	 * 
	 * @param id record identifier
	 * @return
	 */
	public CHolidayRecord getDetail(Long id);

	/**
	 * creates holiday record
	 * 
	 * @param record
	 * @return added record id
	 * @throws CBusinessException in error case
	 */
	public Long add(CHolidayRecord record) throws CBusinessException;

	/**
	 * Modifies holiday record, but not clientId!!!
	 * 
	 * @param id        record identifier
	 * @param newRecord record object
	 * @throws CBusinessException in error case
	 */
	public void modify(CModifyHolidayRequest newRecord) throws CBusinessException;

	/**
	 * Returns list of holidays for the selected year, if selectedYearDate is null
	 * returns holidays for all years
	 * 
	 * @param clientId         client identifier
	 * @param selectedYearDate selected year
	 * @return list of records
	 * @throws CBusinessException in error case
	 */
	public List<CHolidayRecord> getClientRecordsForTheYear(Long clientId, Integer selectedYearDate) throws CBusinessException;

	/**
	 * Returns list of cloned holidays for the selected year if available
	 * 
	 * @param clientId client identifier
	 * @return cloned records
	 * @throws CBusinessException in error case
	 */
	public List<CHolidayRecord> cloneCurrentYearClientRecordsForNextYear(Long clientId) throws CBusinessException;
}
