package sk.qbsw.sed.communication.service;

import java.util.Date;

import sk.qbsw.sed.client.model.lock.CLockRecord;
import sk.qbsw.sed.client.model.request.CRequestRecord;
import sk.qbsw.sed.fw.exception.CBussinessDataException;

public interface IRequestClientService {

	/**
	 * Adds request record.
	 * 
	 * @param requestRecord   request entity
	 * @param ignoreDuplicity ignore duplicity flag
	 * @return lock record
	 * @throws CBussinessDataException in error case
	 */
	public CLockRecord add(CRequestRecord requestRecord, boolean ignoreDuplicity) throws CBussinessDataException;

	/**
	 * Returns request entity.
	 * 
	 * @param requestId request identifier
	 * @return request entity
	 * @throws CBussinessDataException in error case
	 */
	public CRequestRecord getDetail(Long requestId) throws CBussinessDataException;

	/**
	 * Modifies request record.
	 * 
	 * @param requestRecord   request entity
	 * @param ignoreDuplicity ignore duplicity flag
	 * @return lock record
	 * @throws CBussinessDataException in error case
	 */
	public CLockRecord modify(CRequestRecord requestRecord, boolean ignoreDuplicity) throws CBussinessDataException;

	/**
	 * Cancels created request record.
	 * 
	 * @param requestId request identifier
	 * @return lock record
	 * @throws CBussinessDataException in error case
	 */
	public CLockRecord cancel(Long requestId) throws CBussinessDataException;

	/**
	 * Approves created request record.
	 * 
	 * @param requestId request identifier
	 * @return lock record
	 * @throws CBussinessDataException
	 */
	public CLockRecord approve(Long requestId) throws CBussinessDataException;

	/**
	 * Rejects created request record.
	 * 
	 * @param requestId request identifier
	 * @return lock record
	 * @throws CBussinessDataException in error case
	 */
	public CLockRecord reject(Long requestId) throws CBussinessDataException;

	/**
	 * Approves created request record from received email with code.
	 * 
	 * @param requestId   request identifier
	 * @param requestCode request code
	 * @return success flag
	 * @throws CBussinessDataException in error case
	 */
	public Boolean approveRequestFromEmail(String requestId, String requestCode) throws CBussinessDataException;

	/**
	 * Rejects created request record from received email with code.
	 * 
	 * @param requestId   request identifier
	 * @param requestCode request code
	 * @return success flag
	 * @throws CBussinessDataException in error case
	 */
	public Boolean rejectRequestFromEmail(String requestId, String requestCode) throws CBussinessDataException;

	public Boolean isAllowedHomeOfficeForToday(Long userId, Date date) throws CBussinessDataException;

	public Boolean isAllowedHomeOfficeInInterval(Long userId, Long clientId, Date dateFrom, Date dateTo) throws CBussinessDataException;
}
