package sk.qbsw.sed.common.utils;

import java.util.Calendar;

public class CDateRangeUtils {

	/**
	 * private constructor
	 */
	private CDateRangeUtils() {

	}

	/**
	 * return date range for this month
	 * 
	 * @return
	 */
	public static CDateRange getDateRangeForThisMonth() {
		Calendar lastDayOfThisMonth = (Calendar) getFirstDayOfThisMonth().clone();
		lastDayOfThisMonth.set(Calendar.DAY_OF_MONTH, lastDayOfThisMonth.getActualMaximum(Calendar.DAY_OF_MONTH));

		return new CDateRange(getFirstDayOfThisMonth(), lastDayOfThisMonth);
	}

	/**
	 * return date range for this week
	 * 
	 * @return
	 */
	public static CDateRange getDateRangeForThisWeek() {
		Calendar lastDayOfThisWeek = (Calendar) getFirstDayOfThisWeek().clone();
		lastDayOfThisWeek.add(Calendar.DAY_OF_WEEK, 6);

		return new CDateRange(getFirstDayOfThisWeek(), lastDayOfThisWeek);
	}

	/**
	 * return date range for previous week
	 * 
	 * @return
	 */
	public static CDateRange getDateRangeForPreviousWeek() {
		Calendar firstDayOfPreviousWeek = getFirstDayOfThisWeek();
		firstDayOfPreviousWeek.add(Calendar.WEEK_OF_YEAR, -1);

		Calendar lastDayOfPreviousWeek = (Calendar) firstDayOfPreviousWeek.clone();
		lastDayOfPreviousWeek.add(Calendar.DAY_OF_WEEK, 6);

		return new CDateRange(firstDayOfPreviousWeek, lastDayOfPreviousWeek);
	}

	/**
	 * return date range for previous month
	 * 
	 * @return
	 */
	public static CDateRange getDateRangeForPreviousMonth() {
		Calendar firstDayOfPreviousMonth = getFirstDayOfThisMonth();
		firstDayOfPreviousMonth.add(Calendar.MONTH, -1);

		Calendar lastDayOfPreviousMonth = (Calendar) firstDayOfPreviousMonth.clone();
		lastDayOfPreviousMonth.set(Calendar.DAY_OF_MONTH, lastDayOfPreviousMonth.getActualMaximum(Calendar.DAY_OF_MONTH));

		return new CDateRange(firstDayOfPreviousMonth, lastDayOfPreviousMonth);
	}

	/**
	 * return first day of this month
	 * 
	 * @return
	 */
	public static Calendar getFirstDayOfThisMonth() {
		Calendar cal1 = Calendar.getInstance();
		cal1.set(Calendar.DAY_OF_MONTH, 1);
		return cal1;
	}

	/**
	 * return first day of this week
	 * 
	 * @return
	 */
	public static Calendar getFirstDayOfThisWeek() {
		Calendar cal1 = Calendar.getInstance();
		cal1.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return cal1;
	}
}
