package sk.qbsw.sed.client.service.business;

import java.util.List;

import sk.qbsw.sed.client.model.CEmployeesStatusNew;
import sk.qbsw.sed.client.model.brw.CEmployeesStatus;
import sk.qbsw.sed.client.model.timestamp.CEmployeesStatusBrwFilterCriteria;
import sk.qbsw.sed.client.model.timestamp.CTimeStampRecord;
import sk.qbsw.sed.framework.security.exception.CBusinessException;

public interface IEmployeesStatusService {

	public List<CEmployeesStatusNew> fetch() throws CBusinessException;

	public List<CEmployeesStatus> fetchByCriteria(CEmployeesStatusBrwFilterCriteria criteria) throws CBusinessException;

	/**
	 * returns new status if status was changed, if not returns null
	 * 
	 */
	public String checkStatus(Long userId) throws CBusinessException;

	/**
	 * returns new status if status was changed, if not returns null
	 * 
	 * @param record
	 * @return
	 * @throws CBusinessException
	 */
	public String checkStatus(CTimeStampRecord record) throws CBusinessException;

	/**
	 * sets new status for user
	 * 
	 * @param userId
	 * @param status
	 * @throws CBusinessException
	 */
	public void setStatus(Long userId, String status) throws CBusinessException;

	/**
	 * clear all cached employees statuses
	 */
	public void clear();
}
