package sk.qbsw.sed.client.service.business;

import java.util.Date;

import sk.qbsw.sed.client.model.request.CRequestRecord;
import sk.qbsw.sed.framework.security.exception.CBusinessException;

public interface IRequestService {

	/**
	 * Adds request
	 * 
	 * @param type            type of the request (D, NV, SD, WB, (WT))
	 * @param model           data related to the request
	 * @param ignoreDuplicity define if ignore already existing request in same time
	 * @return REQUEST_EXIST_APPROVED_IN_TIME if already exists approve request in
	 *         same time, else null
	 * @throws CBusinessException
	 */
	public String add(Long type, CRequestRecord model, boolean ignoreDuplicity) throws CBusinessException;

	/**
	 * Modifies request stored in DB
	 * 
	 * @param id              id of request to store
	 * @param model           model to store
	 * @param ignoreDuplicity define if ignore already existing request in same time
	 * @throws CBusinessException
	 */
	public void modify(Long id, CRequestRecord model, boolean ignoreDuplicity) throws CBusinessException;

	/**
	 * Returns detail of the request
	 * 
	 * @param requestId
	 * @return
	 * @throws CBusinessException
	 */
	public CRequestRecord getDetail(Long requestId) throws CBusinessException;

	/**
	 * Cancels request
	 * 
	 * @param requestId
	 * @throws CBusinessException
	 */
	public void cancel(Long requestId) throws CBusinessException;

	/**
	 * Approves request
	 * 
	 * @param requestId
	 * @throws CBusinessException
	 */
	public void approve(Long requestId) throws CBusinessException;

	/**
	 * Rejects request
	 * 
	 * @param requestId
	 * @throws CBusinessException
	 */
	public void reject(Long requestId) throws CBusinessException;

	/**
	 * Approves request
	 * 
	 * @param requestId
	 * @param requestCode - the unique security request code
	 * @return true if approval was successful
	 * @throws CBusinessException
	 */
	public Boolean approveRequestFromEmail(final String requestId, final String requestCode);

	/**
	 * Rejects request
	 * 
	 * @param requestId
	 * @param requestCode - the unique security request code
	 * @return true if rejection was successful
	 * @throws CBusinessException
	 */
	public Boolean rejectRequestFromEmail(final String requestId, final String requestCode);

	public Boolean isAllowedHomeOfficeForToday(Long userId, Date date) throws CBusinessException;

	public Boolean isAllowedHomeOfficeInInterval(Long userId, Long clientId, Date dateFrom, Date dateTo) throws CBusinessException;
}
