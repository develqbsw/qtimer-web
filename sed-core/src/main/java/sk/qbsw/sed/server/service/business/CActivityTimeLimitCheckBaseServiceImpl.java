package sk.qbsw.sed.server.service.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sk.qbsw.sed.client.model.restriction.IDayTypeConstant;
import sk.qbsw.sed.client.ui.localization.CClientExceptionsMessages;
import sk.qbsw.sed.common.utils.CDateUtils;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.server.dao.IActivityIntervalDao;
import sk.qbsw.sed.server.dao.IHolidayDao;
import sk.qbsw.sed.server.dao.IUserDao;
import sk.qbsw.sed.server.dao.IXUserActivityRestrictionDao;
import sk.qbsw.sed.server.model.codelist.CHoliday;
import sk.qbsw.sed.server.model.domain.CTimeSheetRecord;
import sk.qbsw.sed.server.model.restriction.CActivityInterval;
import sk.qbsw.sed.server.model.restriction.CXUserActivityRestriction;
import sk.qbsw.sed.server.service.CTimeUtils;

/**
 * Service for checking the user activity limits. Contains only not transaction
 * methods.
 * 
 * @author rosenberg
 * @version 1.0
 * @since 1.6.6.1
 */
@Service(value = "activityTimeLimitCheckBaseService")
public class CActivityTimeLimitCheckBaseServiceImpl implements IActivityTimeLimitCheckBaseService {

	@Autowired
	IUserDao userDao;

	@Autowired
	private IActivityIntervalDao activityIntervalDao;

	@Autowired
	IHolidayDao holidayDao;

	@Autowired
	private IXUserActivityRestrictionDao xUserActivityRestrictionDao;

	/**
	 * @see IActivityTimeLimitCheckBaseService#checkActivityLimit(Long,CTimeSheetRecord)
	 */
	public void checkActivityLimit(Long userId, CTimeSheetRecord tsRecord) throws CBusinessException {
		// tsRecord: is the working day?
		Boolean isWorkingDay = timestampFallsToWorkingDay(tsRecord);
		Long dayType = (isWorkingDay ? IDayTypeConstant.DAY_TYPE_WORKDAY : IDayTypeConstant.DAY_TYPE_FREEDAY);

		Long activityId = tsRecord.getActivity().getId();

		String projectGroup = tsRecord.getProject() == null ? null : tsRecord.getProject().getGroup();

		// find all user activity restrictions
		List<CActivityInterval> userRestrictionIntervals = getAllUserActivityLimitIntervals(userId, activityId, projectGroup);

		// do check date + time activity restrictions
		Boolean partiallyCheck1 = Boolean.FALSE;
		List<CActivityInterval> userDateTimeRestrictionIntervals = getUserDateTimeRestrictionIntervals(userRestrictionIntervals, activityId, dayType);
		if (!userDateTimeRestrictionIntervals.isEmpty()) {
			for (CActivityInterval ai : userDateTimeRestrictionIntervals) {
				// check it
				if (getCheckUserDateTimeRestrictionBooleanValue(userId, ai, tsRecord)) {
					partiallyCheck1 = Boolean.TRUE;
					break;
				}
			}
		} else {
			partiallyCheck1 = Boolean.TRUE;
		}

		// do check date + time activity restrictions
		Boolean partiallyCheck2 = Boolean.FALSE;
		List<CActivityInterval> userDateRestrictionIntervals = getUserDateRestrictionIntervals(userRestrictionIntervals, activityId, dayType);
		if (!userDateRestrictionIntervals.isEmpty()) {
			for (CActivityInterval ai : userDateRestrictionIntervals) {
				// check it
				if (getCheckUserDateOnlyRestrictionBooleanValue(userId, ai, tsRecord)) {
					partiallyCheck2 = Boolean.TRUE;
					break;
				}
			}
		} else {
			partiallyCheck2 = Boolean.TRUE;
		}

		// do check date + time activity restrictions
		Boolean partiallyCheck3 = Boolean.FALSE;
		List<CActivityInterval> userTimeRestrictionIntervals = getUserTimeRestrictionIntervals(userRestrictionIntervals, activityId, dayType);
		if (!userTimeRestrictionIntervals.isEmpty()) {
			for (CActivityInterval ai : userTimeRestrictionIntervals) {
				// check it
				if (getCheckUserTimeOnlyRestrictionBooleanValue(userId, ai, tsRecord)) {
					partiallyCheck3 = Boolean.TRUE;
					break;
				}
			}
		} else {
			partiallyCheck3 = Boolean.TRUE;
		}

		Boolean checkIsOK = partiallyCheck1 && partiallyCheck2 && partiallyCheck3;

		if (!checkIsOK) {
			throw new CBusinessException(CClientExceptionsMessages.TIMESTAMP_USER_ACTIVITY_RESTRICTION_ERROR);
		}

	}

	public Boolean getCheckActivityLimitBooleanValue(Long userId, CTimeSheetRecord tsRecord) {
		// tsRecord: is the working day?
		Boolean isWorkingDay = timestampFallsToWorkingDay(tsRecord);
		Long dayType = (isWorkingDay ? IDayTypeConstant.DAY_TYPE_WORKDAY : IDayTypeConstant.DAY_TYPE_FREEDAY);

		Long activityId = tsRecord.getActivity().getId();

		String projectGroup = tsRecord.getProject() == null ? null : tsRecord.getProject().getGroup();

		// find all user activity restrictions
		List<CActivityInterval> userRestrictionIntervals = getAllUserActivityLimitIntervals(userId, activityId, projectGroup);

		// do check date + time activity restrictions
		Boolean partiallyCheck1 = Boolean.FALSE;
		List<CActivityInterval> userDateTimeRestrictionIntervals = getUserDateTimeRestrictionIntervals(userRestrictionIntervals, activityId, dayType);
		if (!userDateTimeRestrictionIntervals.isEmpty()) {
			for (CActivityInterval ai : userDateTimeRestrictionIntervals) {
				// check it
				if (getCheckUserDateTimeRestrictionBooleanValue(userId, ai, tsRecord)) {
					partiallyCheck1 = Boolean.TRUE;
					break;
				}
			}
		} else {
			partiallyCheck1 = Boolean.TRUE;
		}

		// do check date + time activity restrictions
		Boolean partiallyCheck2 = Boolean.FALSE;
		List<CActivityInterval> userDateRestrictionIntervals = getUserDateRestrictionIntervals(userRestrictionIntervals, activityId, dayType);
		if (!userDateRestrictionIntervals.isEmpty()) {
			for (CActivityInterval ai : userDateRestrictionIntervals) {
				// check it
				if (getCheckUserDateOnlyRestrictionBooleanValue(userId, ai, tsRecord)) {
					partiallyCheck2 = Boolean.TRUE;
					break;
				}
			}
		} else {
			partiallyCheck2 = Boolean.TRUE;
		}

		// do check date + time activity restrictions
		Boolean partiallyCheck3 = Boolean.FALSE;
		List<CActivityInterval> userTimeRestrictionIntervals = getUserTimeRestrictionIntervals(userRestrictionIntervals, activityId, dayType);
		if (!userTimeRestrictionIntervals.isEmpty()) {
			for (CActivityInterval ai : userTimeRestrictionIntervals) {
				// check it
				if (getCheckUserTimeOnlyRestrictionBooleanValue(userId, ai, tsRecord)) {
					partiallyCheck3 = Boolean.TRUE;
					break;
				}
			}
		} else {
			partiallyCheck3 = Boolean.TRUE;
		}

		return partiallyCheck1 && partiallyCheck2 && partiallyCheck3;
	}

	/**
	 * @see IActivityTimeLimitCheckBaseService#getAllUserActivityLimitIntervals(Long,
	 *      Long)
	 */
	public List<CActivityInterval> getAllUserActivityLimitIntervals(Long userId, Long activityId, String projectGroup) {
		List<CActivityInterval> userRestrictionIntervals = new ArrayList<>();

		List<CXUserActivityRestriction> userRestrictions = xUserActivityRestrictionDao.findByProjectGroupAndUser(userId, projectGroup);
		for (CXUserActivityRestriction ur : userRestrictions) {
			List<CActivityInterval> limits = this.activityIntervalDao.findByGroupRestriction(ur.getRestrictionGroup().getId());
			for (CActivityInterval limit : limits) {
				if (limit.getActivity().getId().equals(activityId)) {
					userRestrictionIntervals.add(limit);
				}
			}
		}

		return userRestrictionIntervals;
	}

	/**
	 * @see IActivityTimeLimitCheckBaseService#getAllUserActivityLimitIntervals(Long,
	 *      Long, Long)
	 */
	public List<CActivityInterval> getAllUserActivityLimitIntervals(Long userId, Long activityId, Long dayType, String projectGroup) throws CBusinessException {
		List<CActivityInterval> userRestrictionIntervals = new ArrayList<>();

		List<CXUserActivityRestriction> userRestrictions = xUserActivityRestrictionDao.findByProjectGroupAndUser(userId, projectGroup);
		for (CXUserActivityRestriction ur : userRestrictions) {
			List<CActivityInterval> limits = this.activityIntervalDao.findByGroupRestriction(ur.getRestrictionGroup().getId());
			for (CActivityInterval limit : limits) {
				if (limit.getActivity().getId().equals(activityId) && limit.getDateType().equals(dayType)) {
					userRestrictionIntervals.add(limit);
				}
			}
		}

		return userRestrictionIntervals;
	}

	/**
	 * Return boolean value of the check is timestamp falls into work day (with
	 * holiday check, also)
	 * 
	 * @param tsRecord input timestamp
	 * @return Boolean object
	 */
	public Boolean timestampFallsToWorkingDay(final CTimeSheetRecord tsRecord) {
		Calendar tsTimeFrom = tsRecord.getTimeFrom();
		Boolean isWorkingDay = new Boolean(CDateUtils.isWorkingDay(tsTimeFrom.getTime()));
		if (isWorkingDay) {
			// check clients holidays
			List<CHoliday> clientHolidays = holidayDao.findAllValidClientsHolidays(tsRecord.getClient().getId(), tsTimeFrom.get(Calendar.YEAR));
			for (CHoliday holiday : clientHolidays) {
				if (holiday.getDay().get(Calendar.YEAR) == tsTimeFrom.get(Calendar.YEAR) && holiday.getDay().get(Calendar.MONTH) == tsTimeFrom.get(Calendar.MONTH)
						&& holiday.getDay().get(Calendar.DAY_OF_MONTH) == tsTimeFrom.get(Calendar.DAY_OF_MONTH)) {
					isWorkingDay = Boolean.FALSE;
					break;
				}
			}
		}

		return isWorkingDay;
	}

	/**
	 * Checks date and time restrictions for time stamp, returns true if tsRecord
	 * falls into interval
	 * 
	 * @param userId   selected user
	 * @param interval selected interval
	 * @param tsRecord selected time stamp
	 * @return Boolean object
	 */
	private Boolean getCheckUserDateTimeRestrictionBooleanValue(Long userId, CActivityInterval interval, CTimeSheetRecord tsRecord) {
		Calendar tsRecordDay = CDateUtils.getDateOnly(tsRecord.getTimeFrom());

		if (interval.getDate_from() != null && interval.getDate_to() != null && interval.getTime_from() != null && interval.getTime_to() != null) {
			// full date time interval
			// DATE CHECK
			boolean dateValid = (interval.getDate_from().before(tsRecordDay) || interval.getDate_from().equals(tsRecordDay))
					&& (interval.getDate_to().after(tsRecordDay) || interval.getDate_to().equals(tsRecordDay));
			if (dateValid) {
				// TIME CHECK
				int intervalStart = interval.getTime_from().get(Calendar.HOUR_OF_DAY) * 60 + interval.getTime_from().get(Calendar.MINUTE);
				int intervalEnd = interval.getTime_to().get(Calendar.HOUR_OF_DAY) * 60 + interval.getTime_to().get(Calendar.MINUTE) + 1;

				int tsStart = tsRecord.getTimeFrom().get(Calendar.HOUR_OF_DAY) * 60 + tsRecord.getTimeFrom().get(Calendar.MINUTE);
				boolean timeValid = false;
				if (tsRecord.getTimeTo() != null) {
					int tsEnd = tsRecord.getTimeTo().get(Calendar.HOUR_OF_DAY) * 60 + tsRecord.getTimeTo().get(Calendar.MINUTE) + 1;
					timeValid = (intervalStart <= tsStart && intervalEnd >= tsEnd);
				} else {
					timeValid = (intervalStart <= tsStart && intervalEnd >= tsStart);
				}
				if (!timeValid) {
					return Boolean.FALSE;
				}

			} else {
				return Boolean.FALSE;
			}

		} else if (interval.getDate_from() != null && interval.getDate_to() == null && interval.getTime_from() != null && interval.getTime_to() != null) {
			// unclosed date interval from, full time interval
			// DATE CHECK
			boolean dateValid = (interval.getDate_from().before(tsRecordDay) || interval.getDate_from().equals(tsRecordDay));
			if (dateValid) {
				// TIME CHECK
				int intervalStart = interval.getTime_from().get(Calendar.HOUR_OF_DAY) * 60 + interval.getTime_from().get(Calendar.MINUTE);
				int intervalEnd = interval.getTime_to().get(Calendar.HOUR_OF_DAY) * 60 + interval.getTime_to().get(Calendar.MINUTE) + 1;

				int tsStart = tsRecord.getTimeFrom().get(Calendar.HOUR_OF_DAY) * 60 + tsRecord.getTimeFrom().get(Calendar.MINUTE);
				boolean timeValid = false;
				if (tsRecord.getTimeTo() != null) {
					int tsEnd = tsRecord.getTimeTo().get(Calendar.HOUR_OF_DAY) * 60 + tsRecord.getTimeTo().get(Calendar.MINUTE) + 1;
					timeValid = (intervalStart <= tsStart && intervalEnd >= tsEnd);
				} else {
					timeValid = (intervalStart <= tsStart && intervalEnd >= tsStart);
				}
				if (!timeValid) {
					return Boolean.FALSE;
				}

			} else {
				return Boolean.FALSE;
			}
		} else if (interval.getDate_from() != null && interval.getDate_to() != null && interval.getTime_from() != null && interval.getTime_to() == null) {
			// full date time interval, unclosed time interval from
			// DATE CHECK
			boolean dateValid = (interval.getDate_from().before(tsRecordDay) || interval.getDate_from().equals(tsRecordDay))
					&& (interval.getDate_to().after(tsRecordDay) || interval.getDate_to().equals(tsRecordDay));
			if (dateValid) {
				// TIME CHECK
				int intervalStart = interval.getTime_from().get(Calendar.HOUR_OF_DAY) * 60 + interval.getTime_from().get(Calendar.MINUTE);
				int tsStart = tsRecord.getTimeFrom().get(Calendar.HOUR_OF_DAY) * 60 + tsRecord.getTimeFrom().get(Calendar.MINUTE);

				boolean timeValid = (intervalStart <= tsStart);
				if (!timeValid) {
					return Boolean.FALSE;
				}

			} else {
				return Boolean.FALSE;
			}
		} else if (interval.getDate_from() != null && interval.getDate_to() == null && interval.getTime_from() != null && interval.getTime_to() == null) {
			// unclosed date interval from, unclosed time interval from
			// DATE CHECK
			boolean dateValid = (interval.getDate_from().before(tsRecordDay) || interval.getDate_from().equals(tsRecordDay));
			if (dateValid) {
				// TIME CHECK
				int intervalStart = interval.getTime_from().get(Calendar.HOUR_OF_DAY) * 60 + interval.getTime_from().get(Calendar.MINUTE);
				int tsStart = tsRecord.getTimeFrom().get(Calendar.HOUR_OF_DAY) * 60 + tsRecord.getTimeFrom().get(Calendar.MINUTE);

				boolean timeValid = (intervalStart <= tsStart);
				if (!timeValid) {
					return Boolean.FALSE;
				}

			} else {
				return Boolean.FALSE;
			}
		}
		return Boolean.TRUE;
	}

	/**
	 * Checks date only restrictions for time stamp, returns true if tsRecord falls
	 * into interval
	 * 
	 * @param userId   selected user
	 * @param interval selected interval
	 * @param tsRecord selected time stamp
	 * @return Boolean object
	 */
	private Boolean getCheckUserDateOnlyRestrictionBooleanValue(Long userId, CActivityInterval interval, CTimeSheetRecord tsRecord) {
		Calendar tsRecordDay = CDateUtils.getDateOnly(tsRecord.getTimeFrom());

		if (interval.getDate_from() != null && interval.getDate_to() != null) {
			// full date time interval
			// DATE CHECK
			boolean dateValid = ((interval.getDate_from().before(tsRecordDay) || interval.getDate_from().equals(tsRecordDay))
					&& (interval.getDate_to().after(tsRecordDay) || interval.getDate_to().equals(tsRecordDay)));
			if (!dateValid) {
				return Boolean.FALSE;
			}
		} else {
			// unclosed date interval from
			// DATE CHECK
			boolean dateValid = (interval.getDate_from().before(tsRecordDay) || interval.getDate_from().equals(tsRecordDay));
			if (!dateValid) {
				return Boolean.FALSE;
			}
		}
		return Boolean.TRUE;
	}

	/**
	 * Checks time only restrictions for time stamp, returns true if tsRecord falls
	 * into interval
	 * 
	 * @param userId   selected user
	 * @param interval selected interval
	 * @param tsRecord selected time stamp
	 * @return Boolean object
	 */
	private Boolean getCheckUserTimeOnlyRestrictionBooleanValue(Long userId, CActivityInterval interval, CTimeSheetRecord tsRecord) {

		if (interval.getTime_from() != null && interval.getTime_to() != null) {
			boolean timeValid = false;
			// full time interval
			// TIME CHECK
			int intervalStart = interval.getTime_from().get(Calendar.HOUR_OF_DAY) * 60 + interval.getTime_from().get(Calendar.MINUTE);
			int intervalEnd = interval.getTime_to().get(Calendar.HOUR_OF_DAY) * 60 + interval.getTime_to().get(Calendar.MINUTE) + 1;

			int tsStart = tsRecord.getTimeFrom().get(Calendar.HOUR_OF_DAY) * 60 + tsRecord.getTimeFrom().get(Calendar.MINUTE);
			if (tsRecord.getTimeTo() != null) {
				int tsEnd = tsRecord.getTimeTo().get(Calendar.HOUR_OF_DAY) * 60 + tsRecord.getTimeTo().get(Calendar.MINUTE) + 1;
				timeValid = (intervalStart <= tsStart && intervalEnd >= tsEnd);
			} else {
				timeValid = (intervalStart <= tsStart && intervalEnd > tsStart);
			}

			if (!timeValid) {
				return Boolean.FALSE;
			}
		} else {
			// unclosed time interval from
			// TIME CHECK
			int intervalStart = interval.getTime_from().get(Calendar.HOUR_OF_DAY) * 60 + interval.getTime_from().get(Calendar.MINUTE);
			int tsStart = tsRecord.getTimeFrom().get(Calendar.HOUR_OF_DAY) * 60 + tsRecord.getTimeFrom().get(Calendar.MINUTE);

			boolean timeValid = (intervalStart <= tsStart);
			if (!timeValid) {
				return Boolean.FALSE;
			}
		}
		return Boolean.TRUE;
	}

	/**
	 * Checks date and time restrictions for time stamp
	 * 
	 * @param userId   selected user
	 * @param interval selected interval
	 * @param tsRecord selected time stamp
	 * @throws CBusinessException in business error case
	 */
	@SuppressWarnings("unused")
	private void checkUserDateTimeRestriction(Long userId, CActivityInterval interval, CTimeSheetRecord tsRecord) throws CBusinessException {

		Boolean retval = getCheckUserDateTimeRestrictionBooleanValue(userId, interval, tsRecord);
		if (!retval) {
			throw new CBusinessException(CClientExceptionsMessages.TIMESTAMP_USER_ACTIVITY_RESTRICTION_ERROR);
		}
	}

	/**
	 * Checks date only restrictions for time stamp
	 * 
	 * @param userId   selected user
	 * @param interval selected interval
	 * @param tsRecord selected time stamp
	 * @throws CBusinessException in business error case
	 */
	@SuppressWarnings("unused")
	private void checkUserDateOnlyRestriction(Long userId, CActivityInterval interval, CTimeSheetRecord tsRecord) throws CBusinessException {
		Boolean retVal = getCheckUserDateOnlyRestrictionBooleanValue(userId, interval, tsRecord);
		if (!retVal) {
			throw new CBusinessException(CClientExceptionsMessages.TIMESTAMP_USER_ACTIVITY_RESTRICTION_ERROR);
		}

	}

	/**
	 * Checks time only restrictions for time stamp
	 * 
	 * @param userId   selected user
	 * @param interval selected interval
	 * @param tsRecord selected time stamp
	 * @throws CBusinessException in business error case
	 */
	@SuppressWarnings("unused")
	private void checkUserTimeOnlyRestriction(Long userId, CActivityInterval interval, CTimeSheetRecord tsRecord) throws CBusinessException {
		Boolean retVal = getCheckUserTimeOnlyRestrictionBooleanValue(userId, interval, tsRecord);
		if (!retVal) {
			throw new CBusinessException(CClientExceptionsMessages.TIMESTAMP_USER_ACTIVITY_RESTRICTION_ERROR);
		}
	}

	/**
	 * Returns intervals where date and time items are filled
	 * 
	 * @param intervals all input intervals
	 * @return interval entities
	 */
	private List<CActivityInterval> getUserDateTimeRestrictionIntervals(List<CActivityInterval> intervals, Long activityId, Long dayType) {
		List<CActivityInterval> retVal = new ArrayList<>();

		for (CActivityInterval interval : intervals) {
			if (interval.getDate_from() != null && interval.getTime_from() != null && interval.getActivity().getId().equals(activityId) && interval.getDateType().equals(dayType))

			{
				retVal.add(interval);
			}
		}

		return retVal;
	}

	/**
	 * Returns intervals where date items only are filled
	 * 
	 * @param intervals all input intervals
	 * @return interval entities
	 */
	private List<CActivityInterval> getUserDateRestrictionIntervals(List<CActivityInterval> intervals, Long activityId, Long dayType) {
		List<CActivityInterval> retVal = new ArrayList<>();

		for (CActivityInterval interval : intervals) {
			if (interval.getDate_from() != null && interval.getTime_from() == null && interval.getTime_to() == null && interval.getActivity().getId().equals(activityId)
					&& interval.getDateType().equals(dayType)) {
				retVal.add(interval);
			}
		}

		return retVal;
	}

	/**
	 * Returns intervals where time items only are filled
	 * 
	 * @param intervals all input intervals
	 * @return interval entities
	 */
	private List<CActivityInterval> getUserTimeRestrictionIntervals(List<CActivityInterval> intervals, Long activityId, Long dayType) {
		List<CActivityInterval> retVal = new ArrayList<>();

		for (CActivityInterval interval : intervals) {
			if (interval.getDate_from() == null && interval.getDate_to() == null && interval.getTime_from() != null && interval.getActivity().getId().equals(activityId)
					&& interval.getDateType().equals(dayType)) {
				retVal.add(interval);
			}
		}

		return retVal;
	}

	/**
	 * @see IActivityTimeLimitCheckBaseService#updateClosingTimestampTimeToValueByUserActivityLimits(CTimeSheetRecord,
	 *      Boolean)
	 */
	public Boolean updateClosingTimestampTimeToValueByUserActivityLimits(CTimeSheetRecord tsRecord, Boolean checkTimeFromvalue) throws CBusinessException {
		Boolean updated = Boolean.FALSE;

		Boolean lastActivityInWorkDay = timestampFallsToWorkingDay(tsRecord);
		Long dayType = (lastActivityInWorkDay ? IDayTypeConstant.DAY_TYPE_WORKDAY : IDayTypeConstant.DAY_TYPE_FREEDAY);
		final List<CActivityInterval> userLimits = getAllUserActivityLimitIntervals(tsRecord.getOwner().getId(), tsRecord.getActivity().getId(), dayType,
				tsRecord.getProject() == null ? null : tsRecord.getProject().getGroup());
		final Boolean checkUserActivityTimeLimits = userLimits.size() > 0 ? Boolean.TRUE : Boolean.FALSE;
		// exists some limits?
		if (checkUserActivityTimeLimits) {
			Boolean existsCorrectInterval = Boolean.FALSE;

			// yes: check all intervals
			for (CActivityInterval interval : userLimits) {
				// exists correct interval?
				if (dateInInterval(tsRecord.getTimeFrom(), interval)) {
					// yes: needs to check?
					if (tsRecord.getTimeTo() != null) {
						// yes: timeTo is not in interval?
						if (!dateInInterval(tsRecord.getTimeTo(), interval)) {
							// yes: needs to update
							Calendar intervalTimeTo = interval.getTime_to();
							if (intervalTimeTo != null) {
								// update to end of interval
								Calendar newTimeTo = (Calendar) tsRecord.getTimeTo().clone();
								newTimeTo.set(Calendar.HOUR_OF_DAY, intervalTimeTo.get(Calendar.HOUR_OF_DAY));
								newTimeTo.set(Calendar.MINUTE, intervalTimeTo.get(Calendar.MINUTE));
								newTimeTo.set(Calendar.SECOND, 59);
								newTimeTo.set(Calendar.MILLISECOND, 999);
								tsRecord.setTimeTo(newTimeTo);
							} else {
								// update to end of day
								Calendar newTimeTo = (Calendar) tsRecord.getTimeTo().clone();
								newTimeTo = CTimeUtils.convertToEOD(newTimeTo);
								tsRecord.setTimeTo(newTimeTo);
							}
							updated = Boolean.TRUE;
						}
					}
					existsCorrectInterval = Boolean.TRUE;
					break;
				}
			}

			if (checkTimeFromvalue && !existsCorrectInterval) {
				throw new CBusinessException(CClientExceptionsMessages.TIMESTAMP_USER_ACTIVITY_RESTRICTION_ERROR);
			}

		}

		return updated;
	}

	/**
	 * Checks if input date falls into interval (by date and/or by time)
	 * 
	 * @param tsRecordDay input date
	 * @param interval    input interval
	 * @return Boolean object
	 */
	private Boolean dateInInterval(Calendar tsRecordDay, CActivityInterval interval) {
		Calendar iDateFrom = interval.getDate_from();
		Calendar iDateTo = interval.getDate_to();
		Calendar iTimeFrom = interval.getTime_from();
		Calendar iTimeTo = interval.getTime_to();

		if (iDateFrom != null) {
			if (iDateTo != null) {
				boolean dateValid = ((iDateFrom.before(tsRecordDay) || iDateFrom.equals(tsRecordDay)) && (iDateTo.after(tsRecordDay) || iDateTo.equals(tsRecordDay)));

				if (iTimeFrom != null) {
					int intervalStart = iTimeFrom.get(Calendar.HOUR_OF_DAY) * 60 + iTimeFrom.get(Calendar.MINUTE);
					int tsStart = tsRecordDay.get(Calendar.HOUR_OF_DAY) * 60 + tsRecordDay.get(Calendar.MINUTE);

					if (iTimeTo != null) {
						// full date limit + full time limit
						int intervalEnd = iTimeTo.get(Calendar.HOUR_OF_DAY) * 60 + iTimeTo.get(Calendar.MINUTE) + 1;

						boolean timeValid = (intervalStart <= tsStart && intervalEnd >= tsStart);

						dateValid = dateValid && timeValid;

						if (dateValid) {
							return Boolean.TRUE;
						}
					} else {
						// full date limit + open time limit
						boolean timeValid = (intervalStart <= tsStart);

						dateValid = dateValid && timeValid;

						if (dateValid) {
							return Boolean.TRUE;
						}
					}
				} else {
					// full date limit + no time limit
					if (dateValid) {
						return Boolean.TRUE;
					}
				}
			} else {
				boolean dateValid = (iDateFrom.before(tsRecordDay) || iDateFrom.equals(tsRecordDay));

				if (iTimeFrom != null) {
					int intervalStart = iTimeFrom.get(Calendar.HOUR_OF_DAY) * 60 + iTimeFrom.get(Calendar.MINUTE);
					int tsStart = tsRecordDay.get(Calendar.HOUR_OF_DAY) * 60 + tsRecordDay.get(Calendar.MINUTE);

					if (iTimeTo != null) {
						// start from date limit + full time limit
						int intervalEnd = iTimeTo.get(Calendar.HOUR_OF_DAY) * 60 + iTimeTo.get(Calendar.MINUTE) + 1;

						boolean timeValid = (intervalStart <= tsStart && intervalEnd >= tsStart);

						dateValid = dateValid && timeValid;

						if (dateValid) {
							return Boolean.TRUE;
						}
					} else {
						// start from date limit + open time limit
						boolean timeValid = (intervalStart <= tsStart);

						dateValid = dateValid && timeValid;

						if (dateValid) {
							return Boolean.TRUE;
						}
					}
				} else {
					// start from date limit + no time limit
					if (dateValid) {
						return Boolean.TRUE;
					}
				}
			}
		} else {
			// no date limit, only time
			if (iTimeFrom != null) {
				int intervalStart = iTimeFrom.get(Calendar.HOUR_OF_DAY) * 60 + iTimeFrom.get(Calendar.MINUTE);
				int tsStart = tsRecordDay.get(Calendar.HOUR_OF_DAY) * 60 + tsRecordDay.get(Calendar.MINUTE);

				if (iTimeTo != null) {
					// no date limit + full time limit
					int intervalEnd = iTimeTo.get(Calendar.HOUR_OF_DAY) * 60 + iTimeTo.get(Calendar.MINUTE) + 1;

					boolean timeValid = (intervalStart <= tsStart && intervalEnd >= tsStart);

					if (timeValid) {
						return Boolean.TRUE;
					}

				} else {
					// no date limit + open time limit
					boolean timeValid = (intervalStart <= tsStart);

					if (timeValid) {
						return Boolean.TRUE;
					}
				}
			}
		}

		return Boolean.FALSE;
	}
}
