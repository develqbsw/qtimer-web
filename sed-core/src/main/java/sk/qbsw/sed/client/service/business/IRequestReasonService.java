package sk.qbsw.sed.client.service.business;

import java.util.List;

import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.restriction.CRequestReasonData;
import sk.qbsw.sed.client.model.restriction.CRequestReasonListsData;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.framework.security.exception.CSecurityException;

public interface IRequestReasonService {
	/**
	 * Returns request reason data.
	 * 
	 * @param recordId - request reason identifier
	 * @return request reason data object
	 */
	CRequestReasonData getDetail(Long recordId);

	/**
	 * Saves request reason data. Used for adding new or updating existing record.
	 * 
	 * @param model - request reason data object
	 * @return request reason data object
	 * @throws CSecurityException
	 * @throws CBusinessException
	 */
	CRequestReasonData save(CRequestReasonData model) throws CSecurityException, CBusinessException;

	/**
	 * Returns object containing lists of request reason records of client specified
	 * by ID.
	 * 
	 * @param clientId - client identifier
	 * @return object containing lists of records
	 * @throws CSecurityException
	 */
	CRequestReasonListsData getReasonLists(Long clientId) throws CSecurityException;

	/**
	 * Returns list of request reason records for list box in client form.
	 * 
	 * @return list of records
	 * @throws CSecurityException
	 */
	List<CCodeListRecord> getReasonListsForListbox() throws CSecurityException;
}