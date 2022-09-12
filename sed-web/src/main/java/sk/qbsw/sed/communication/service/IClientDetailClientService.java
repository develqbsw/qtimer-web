package sk.qbsw.sed.communication.service;

import sk.qbsw.sed.client.model.detail.CClientDetailRecord;
import sk.qbsw.sed.client.model.lock.CLockRecord;
import sk.qbsw.sed.fw.exception.CBussinessDataException;

public interface IClientDetailClientService {

	/**
	 * Returns some client detail
	 * 
	 * @param clientId target client identifier
	 * @return client detail object
	 * @throws CBussinessDataException in error case
	 */
	public CClientDetailRecord getDetail(Long clientId) throws CBussinessDataException;

	/**
	 * Updates client record by clienDetail data
	 * 
	 * @param clientDetail input data
	 * @throws CBussinessDataException in error case
	 */
	public CLockRecord updateDetail(CClientDetailRecord clientDetail) throws CBussinessDataException;
}
