package sk.qbsw.sed.communication.service;

import java.util.List;

import sk.qbsw.sed.client.model.codelist.CHolidayRecord;
import sk.qbsw.sed.client.model.lock.CLockRecord;
import sk.qbsw.sed.fw.exception.CBussinessDataException;

public interface IHolidayClientService {

	/**
	 * Reads detail of holiday record
	 * 
	 * @param projectId record identifier
	 * @return holiday record object
	 */
	public CHolidayRecord getDetail(Long projectId) throws CBussinessDataException;

	/**
	 * Modifies holiday record, but not clientId!!!
	 * 
	 * @param id        record identifier
	 * @param newRecord record object
	 * @return lock record
	 * @throws CBussinessDataException in error case
	 */
	public CLockRecord modify(CHolidayRecord newRecord) throws CBussinessDataException;

	/**
	 * Creates holiday record.
	 * 
	 * @param record identifier
	 * @return lock record
	 * @throws CBussinessDataException in error case
	 */
	public CLockRecord add(CHolidayRecord record) throws CBussinessDataException;

	/**
	 * Returns list of cloned holidays for the selected year if available
	 * 
	 * @param clientId client identifier
	 * @return cloned records
	 * @throws CBussinessDataException in error case
	 */
	public List<CHolidayRecord> cloneRecordsForNextYear(Long clientId) throws CBussinessDataException;

	/**
	 * Returns list of holidays for the selected year, if selectedYearDate is null
	 * returns holidays for all years
	 * 
	 * @param clientId         client identifier
	 * @param selectedYearDate selected year
	 * @return list of records
	 * @throws CBussinessDataException in error case
	 */
	public List<CHolidayRecord> getClientRecordsForTheYear(Long clientId, Integer selectedYearDate) throws CBussinessDataException;
}
