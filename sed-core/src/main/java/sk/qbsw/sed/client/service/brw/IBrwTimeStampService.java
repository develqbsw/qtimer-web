package sk.qbsw.sed.client.service.brw;

import java.util.Date;

import sk.qbsw.sed.client.model.timestamp.CTimeStampRecord;
import sk.qbsw.sed.client.request.CBrwTimeStampMassChangeRequest;
import sk.qbsw.sed.framework.security.exception.CBusinessException;

public interface IBrwTimeStampService extends IBrwService<CTimeStampRecord> {
	
	/**
	 * 
	 * @param timeStampRecord
	 * @return
	 * @throws CBusinessException
	 */
	public CTimeStampRecord add(final CTimeStampRecord timeStampRecord) throws CBusinessException;

	/**
	 * 
	 * @param record
	 * @return
	 * @throws CBusinessException
	 */
	public CTimeStampRecord update(CTimeStampRecord record) throws CBusinessException;

	/**
	 * Returns work time for the selected user in input date interval
	 * 
	 * @param userId   selected user identifier
	 * @param dateFrom start border of selected date interval (inclusive)
	 * @param dateTo   stop of selected date interval (inclusive)
	 * @return
	 */
	public long getWorkTimeInInterval(Long userId, Date dateFrom, Date dateTo);

	/**
	 * Reads detail of timestamp record from v_timestamps
	 * 
	 * @param timeStampId
	 * @return
	 */
	public CTimeStampRecord getDetail(Long timeStampId);

	public Long massChangeTimestamps(CBrwTimeStampMassChangeRequest request) throws CBusinessException;
}
