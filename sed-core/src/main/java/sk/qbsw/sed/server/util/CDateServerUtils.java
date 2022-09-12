package sk.qbsw.sed.server.util;

import java.util.Date;
import java.util.List;

import sk.qbsw.sed.common.utils.CDateUtils;
import sk.qbsw.sed.server.model.codelist.CHoliday;

public class CDateServerUtils {

	private CDateServerUtils() {

	}

	/**
	 * Returns working days when holidays taken in account
	 * 
	 * @param dateFrom
	 * @param dateTo
	 * @return really working days
	 */
	public static int getWorkingDaysCheckHolidays(final Date dateFromInput, final Date dateToInput, List<CHoliday> holidays) {
		final Date dateFrom = (Date) dateFromInput.clone();
		final Date dateTo = (Date) dateToInput.clone();

		if (dateFrom.before(dateTo)) {
			int days = 0;
			while (dateFrom.getTime() <= dateTo.getTime()) {
				if (isWorkingDayWithCheckHolidays(dateFrom, holidays)) {
					days++;
				}
				CDateUtils.addDayToDate(dateFrom);
			}
			return days;
		} else if (dateFrom.after(dateTo)) {
			return 0;
		} else {
			return isWorkingDayWithCheckHolidays(dateFrom, holidays) ? 1 : 0;
		}
	}

	/**
	 * return true for working day
	 * 
	 * @param date
	 * @param holidays
	 * @return
	 */
	public static boolean isWorkingDayWithCheckHolidays(final Date date, List<CHoliday> holidays) {
		boolean isWorkDay = CDateUtils.isWorkingDay(date);
		if (isWorkDay) {
			// check holiday also!
			boolean isHoliday = containsValidDate(date, holidays);
			isWorkDay = !isHoliday;
		}
		return isWorkDay;
	}

	/**
	 * check if checkDate is holiday
	 * 
	 * @param checkDate
	 * @param holidays
	 * @return
	 */
	private static boolean containsValidDate(Date checkDate, List<CHoliday> holidays) {
		if (checkDate == null) {
			return false;
		}

		for (CHoliday holiday : holidays) {
			if (CDateUtils.isSameDay(checkDate, holiday.getDay())) {
				return true;
			}
		}

		return false;
	}
}
