package sk.qbsw.sed.server.service.business;

import java.util.List;

import sk.qbsw.sed.client.model.restriction.IDayTypeConstant;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.framework.security.exception.CSecurityException;
import sk.qbsw.sed.server.model.domain.CTimeSheetRecord;
import sk.qbsw.sed.server.model.restriction.CActivityInterval;

/**
 * @author rosenberg
 * @Version 1.0
 * @since 1.6.6.1
 */
public interface IActivityTimeLimitCheckBaseService {

	/**
	 * Checks date time limit of the input activity for selected user (create,
	 * modify report time stamps for activity)
	 * 
	 * @param userId   target user
	 * @param activity selected activity
	 * @throws CSecurityException in security error
	 * @throws CBusinessException in business error
	 */
	public void checkActivityLimit(Long userId, CTimeSheetRecord tsRecord) throws CBusinessException;

	/**
	 * Checks date time limit of the input activity for selected user (create,
	 * modify report time stamps for activity)
	 * 
	 * @param userId   target user
	 * @param activity selected activity
	 * @throws CSecurityException in security error
	 * @returns Boolean object
	 */
	public Boolean getCheckActivityLimitBooleanValue(Long userId, CTimeSheetRecord tsRecord);

	/**
	 * Returns all user limits for selected activity
	 * 
	 * @param userId   target user identifier
	 * @param activity target activity identifier
	 * @return list of restrictions
	 * @throws CSecurityException in security error
	 * @throws CBusinessException in business error
	 */
	public List<CActivityInterval> getAllUserActivityLimitIntervals(Long userId, Long activityId, String projectGroup);

	/**
	 * Returns all user limits for selected activity and day type (
	 * {@link IDayTypeConstant})
	 * 
	 * @param userId   target user identifier
	 * @param activity target activity identifier
	 * @param dayType  day type identifier
	 * @return list of restrictions
	 * @throws CSecurityException in security error
	 * @throws CBusinessException in business error
	 */
	public List<CActivityInterval> getAllUserActivityLimitIntervals(Long userId, Long activity, Long dayType, String projectGroup) throws CBusinessException;

	/**
	 * Returns true if timestamp is in work day
	 * 
	 * @param tsRecord input timestamp
	 * @return return Boolean object
	 */
	public Boolean timestampFallsToWorkingDay(final CTimeSheetRecord tsRecord);

	/**
	 * Returns true if input record has been updated, check existence of the correct
	 * interval for timestamp.timeFrom value
	 * 
	 * @param tsRecord           input/output record
	 * @param checkTimeFromvalue mandatory check for correct interval for
	 *                           timestamp.timeFrom value
	 * @return Boolean value
	 * @throws CBusinessException in business error: doesn't exists correct interval
	 */
	public Boolean updateClosingTimestampTimeToValueByUserActivityLimits(final CTimeSheetRecord tsRecord, Boolean checkTimeFromvalue) throws CBusinessException;
}
