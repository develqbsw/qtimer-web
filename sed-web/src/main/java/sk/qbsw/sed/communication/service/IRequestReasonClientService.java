package sk.qbsw.sed.communication.service;

import java.util.List;

import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.lock.CLockRecord;
import sk.qbsw.sed.client.model.restriction.CRequestReasonData;
import sk.qbsw.sed.client.model.restriction.CRequestReasonListsData;
import sk.qbsw.sed.fw.exception.CBussinessDataException;

public interface IRequestReasonClientService {
	/**
	 * Returns request reason data.
	 * 
	 * @param recordId - request reason identifier
	 * @return request reason data object
	 * @throws CBussinessDataException
	 */
	CRequestReasonData getDetail(Long recordId) throws CBussinessDataException;

	/**
	 * Saves request reason data. Used for adding new or updating existing record.
	 * 
	 * @param model - request reason data object
	 * @return request reason data object
	 * @throws CBussinessDataException
	 */
	CLockRecord save(CRequestReasonData model) throws CBussinessDataException;

	/**
	 * Returns object containing lists of request reason records of client specified
	 * by ID.
	 * 
	 * @param clientId - client identifier
	 * @return object containing lists of records
	 * @throws CBussinessDataException
	 */
	CRequestReasonListsData getReasonLists(Long clientId) throws CBussinessDataException;

	/**
	 * Returns list of request reason records for list box in client form.
	 * 
	 * @return list of records
	 * @throws CBussinessDataException
	 */
	List<CCodeListRecord> getReasonListsForListbox() throws CBussinessDataException;
}
