package sk.qbsw.sed.common.utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import sk.qbsw.sed.client.exception.CParseException;
import sk.qbsw.sed.client.model.codelist.CHolidayRecord;

public class CDateUtils {
	public static final String DATE_FORMAT_DD_MM_YYYY = "dd.MM.yyyy";

	public static final String DATE_FORMAT_HH_mm = "HH:mm";

	private static final int SECONDS_PER_MINUTE = 60;

	private static final int MINUTES_PER_HOUR = 60;

	private static final int HOURS_PER_DAY = 24;

	private static final int SECONDS_PER_DAY = HOURS_PER_DAY * MINUTES_PER_HOUR * SECONDS_PER_MINUTE;

	private static final Pattern TIME_SEPARATOR_PATTERN = Pattern.compile(":");

	private CDateUtils() {
	}

	public static double convertTime(final String timeStr) throws CParseException {
		try {
			return convertTimeInternal(timeStr);
		} catch (final CParseException e) {
			final String msg = "Bad time format '" + timeStr + "' expected 'HH:MM' or 'HH:MM:SS' - " + e.getMessage();
			throw new CParseException(msg, e);
		}
	}

	private static double convertTimeInternal(final String timeStr) throws CParseException {
		final int len = timeStr.length();
		if ((len < 4) || (len > 8)) {
			throw new CParseException("Bad length");
		}
		final String[] parts = TIME_SEPARATOR_PATTERN.split(timeStr);

		String secStr;
		switch (parts.length) {
		case 2:
			secStr = "00";
			break;
		case 3:
			secStr = parts[2];
			break;
		default:
			throw new CParseException("Expected 2 or 3 fields but got (" + parts.length + ")");
		}
		final String hourStr = parts[0];
		final String minStr = parts[1];
		final int hours = parseInt(hourStr, "hour", HOURS_PER_DAY);
		final int minutes = parseInt(minStr, "minute", MINUTES_PER_HOUR);
		final int seconds = parseInt(secStr, "second", SECONDS_PER_MINUTE);

		final double totalSeconds = (double) (seconds + (minutes + (hours) * 60) * 60);
		return totalSeconds / (SECONDS_PER_DAY);
	}

	public static double convertDuration(final String timeStr) throws CParseException {
		try {
			return convertDurationInternal(timeStr);
		} catch (final CParseException e) {
			final String msg = "Bad duration format '" + timeStr + "' expected 'HH:MM' or 'HH:MM:SS' - " + e.getMessage();
			throw new CParseException(msg, e);
		}
	}

	private static double convertDurationInternal(final String timeStr) throws CParseException {
		final int len = timeStr.length();
		if ((len < 4) || (len > 8)) {
			throw new CParseException("Bad length");
		}
		final String[] parts = TIME_SEPARATOR_PATTERN.split(timeStr);

		String secStr;
		switch (parts.length) {
		case 2:
			secStr = "00";
			break;
		case 3:
			secStr = parts[2];
			break;
		default:
			throw new CParseException("Expected 2 or 3 fields but got (" + parts.length + ")");
		}
		final String hourStr = parts[0];
		final String minStr = parts[1];

		// HOURS_PER_DAY + 1 - aby bolo možné parsovať čas 24:00 (SED-772)
		final int hours = parseInt(hourStr, "hour", HOURS_PER_DAY + 1);
		final int minutes = parseInt(minStr, "minute", MINUTES_PER_HOUR);
		final int seconds = parseInt(secStr, "second", SECONDS_PER_MINUTE);

		final double totalSeconds = (double) (seconds + (minutes + (hours) * 60) * 60);
		return totalSeconds / (SECONDS_PER_DAY);
	}

	private static int parseInt(final String strVal, final String fieldName, final int rangeMax) throws CParseException {
		return parseInt(strVal, fieldName, 0, rangeMax - 1);
	}

	private static int parseInt(final String strVal, final String fieldName, final int lowerLimit, final int upperLimit) throws CParseException {
		int result;
		try {
			result = Integer.parseInt(strVal);
		} catch (final NumberFormatException e) {
			throw new CParseException("Bad int format '" + strVal + "' for " + fieldName + " field");
		}
		if ((result < lowerLimit) || (result > upperLimit)) {
			throw new CParseException(fieldName + " value (" + result + ") is outside the allowable range(0.." + upperLimit + ")");
		}
		return result;
	}

	public static String convertToDateString(Calendar cal) {
		String retVal = "";

		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);

		retVal += getLeadingZero(day, 2) + ".";
		retVal += getLeadingZero(month, 2) + ".";
		retVal += getLeadingZero(year, 4);

		return retVal;
	}

	public static String convertToDateStringYYYYMMDD(Calendar cal) {
		String retVal = "";

		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);

		retVal += getLeadingZero(year, 4);
		retVal += getLeadingZero(month, 2);
		retVal += getLeadingZero(day, 2);

		return retVal;
	}

	public static String convertToDateStringForJira(Calendar cal) {
		String retVal = "";

		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);

		retVal += getLeadingZero(year, 4) + "-";
		retVal += getLeadingZero(month, 2) + "-";
		retVal += getLeadingZero(day, 2);

		return retVal;
	}

	public static String convertToTimeString(Calendar cal) {
		String retVal = "";

		int hours = cal.get(Calendar.HOUR_OF_DAY);
		int minutes = cal.get(Calendar.MINUTE);

		retVal += getLeadingZero(hours, 2) + ":";
		retVal += getLeadingZero(minutes, 2);

		return retVal;
	}

	public static String convertToTimestampString(Calendar cal) {
		String retVal = "";

		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);

		retVal += getLeadingZero(year, 4) + ".";
		retVal += getLeadingZero(month, 2) + ".";
		retVal += getLeadingZero(day, 2) + " ";

		int hours = cal.get(Calendar.HOUR_OF_DAY);
		int minutes = cal.get(Calendar.MINUTE);
		int seconds = cal.get(Calendar.SECOND);

		retVal += getLeadingZero(hours, 2) + ":";
		retVal += getLeadingZero(minutes, 2) + ":";
		retVal += getLeadingZero(seconds, 2);

		return retVal;
	}

	public static String getLeadingZero(long value, int count) {
		String retVal = Long.toString(value);
		while (retVal.length() < count) {
			retVal = new StringBuilder().append("0").append(retVal).toString();
		}
		return retVal;
	}

	public static String getLeadingZero(int value, int count) {
		String retVal = Integer.toString(value);
		while (retVal.length() < count) {
			retVal = new StringBuilder().append("0").append(retVal).toString();
		}
		return retVal;
	}

	public static int getWorkingDays(final Date dateFrom, final Date dateTo) {
		if (dateFrom.before(dateTo)) {
			int days = 0;
			while (dateFrom.getTime() <= dateTo.getTime()) {
				if (isWorkingDay(dateFrom)) {
					days++;
				}
				addDayToDate(dateFrom);
			}
			return days;
		} else if (dateFrom.after(dateTo)) {
			return 0;
		} else {
			return isWorkingDay(dateFrom) ? 1 : 0;
		}
	}

	/**
	 * for Saturday and Sunday returns false
	 * 
	 * @param date
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static boolean isWorkingDay(final Date date) {
		final int day = date.getDay();
		return (day != 0) && (day != 6);
	}

	public static void addDayToDate(final Date date) {
		addDaysToDate(date, 1);
	}

	@SuppressWarnings("deprecation")
	public static void addDaysToDate(final Date date, final int days) {
		date.setDate(date.getDate() + days);
	}

	public static Date getCurrentDayAsStartDay() {
		Calendar c0 = Calendar.getInstance();

		Calendar c1 = Calendar.getInstance();
		c1.clear();
		c1.set(Calendar.DAY_OF_MONTH, c0.get(Calendar.DAY_OF_MONTH));
		c1.set(Calendar.MONTH, c0.get(Calendar.MONTH));
		c1.set(Calendar.YEAR, c0.get(Calendar.YEAR));

		return c1.getTime();
	}

	public static Date getNextDayAsStartDay() {
		Calendar c0 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c2.clear();
		c2.set(Calendar.DAY_OF_MONTH, c0.get(Calendar.DAY_OF_MONTH));
		c2.set(Calendar.MONTH, c0.get(Calendar.MONTH));
		c2.set(Calendar.YEAR, c0.get(Calendar.YEAR));
		c2.add(Calendar.DAY_OF_MONTH, 1);

		return c2.getTime();
	}

	public static String getDayName(Calendar c) {
		DateFormat f = new SimpleDateFormat("EE", new Locale("sk"));
		return f.format(c.getTime());
	}

	public static Calendar getDateOnly(Calendar dateTime) {
		Calendar retVal = (Calendar) dateTime.clone();
		retVal.set(Calendar.HOUR_OF_DAY, 0);
		retVal.set(Calendar.MINUTE, 0);
		retVal.set(Calendar.SECOND, 0);
		retVal.set(Calendar.MILLISECOND, 0);

		return retVal;
	}

	public static int countWeekendDays(int year, int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, 1);
		int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

		int count = 0;
		for (int day = 1; day <= daysInMonth; day++) {
			calendar.set(year, month, day);
			int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
			if (dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.SATURDAY) {
				count++;

			}
		}
		return count;
	}

	public static String formatDate(final Date date, final String formatPatter) {
		return new SimpleDateFormat(formatPatter).format(date);
	}

	/**
	 * Returns working days when holidays taken in account
	 * 
	 * @param dateFrom
	 * @param dateTo
	 * @return really working days
	 */
	public static int getWorkingDaysCheckHolidays(final Date from, final Date to, List<CHolidayRecord> holidays) {
		Date dateFrom = (Date) from.clone();
		Date dateTo = (Date) to.clone();

		if (dateFrom.before(dateTo)) {
			int days = 0;
			while (dateFrom.getTime() <= dateTo.getTime()) {
				if (isWorkingDayWithCheckHolidays(dateFrom, holidays)) {
					days++;
				}
				addDayToDate(dateFrom);
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
	public static boolean isWorkingDayWithCheckHolidays(final Date date, List<CHolidayRecord> holidays) {
		boolean isWorkDay = isWorkingDay(date);
		if (isWorkDay) {
			// check holiday also!
			isWorkDay = !containsValidDate(date, holidays);
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
	private static boolean containsValidDate(Date checkDate, List<CHolidayRecord> holidays) {
		if (checkDate == null) {
			return false;
		}

		for (CHolidayRecord holiday : holidays) {
			if (holiday.getDay().equals(checkDate)) {
				return true;
			}
		}

		return false;
	}

	private static String format(SimpleDateFormat formatter, Date value) {
		String result;
		synchronized (formatter) {
			result = formatter.format(value);
		}
		return result;
	}

	public static String createRange(Date from, Date to) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT_DD_MM_YYYY);
		return format(dateFormatter, from) + " - " + format(dateFormatter, to);
	}

	public static String formatDate(Date date) {
		return format(new SimpleDateFormat(DATE_FORMAT_DD_MM_YYYY), date);
	}

	private static Date parse(SimpleDateFormat formatter, String value) throws ParseException {
		Date result;
		synchronized (formatter) {
			result = formatter.parse(value);
		}
		return result;
	}

	public static CRange parseRange(String value) throws ParseException {
		String valueS = value.trim();
		String fromS = StringUtils.substringBefore(valueS, "-");
		String toS = StringUtils.substringAfter(valueS, "-");
		CRange result = new CRange();

		SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT_DD_MM_YYYY);

		Date from = parse(dateFormatter, fromS);
		Date to = parse(dateFormatter, toS);

		result.setFromDate(from);
		result.setToDate(to);
		return result;
	}

	public static Date parseDate(String date) throws ParseException {
		return parse(new SimpleDateFormat(DATE_FORMAT_DD_MM_YYYY), date);
	}

	public static BigDecimal getPercentAsMinutes(Long percent, Long totalTime) {
		if (percent == null || percent.equals(0L)) {
			return BigDecimal.ZERO;
		}

		MathContext mc = new MathContext(0, RoundingMode.HALF_DOWN);
		BigDecimal totalMinutes = new BigDecimal(totalTime);
		BigDecimal minutes = totalMinutes.multiply(new BigDecimal(percent)).divide(new BigDecimal(100), mc);
		minutes = minutes.setScale(0, RoundingMode.HALF_DOWN);
		return minutes;
	}

	public static Long getMinutesAsPercent(Long minutes, Long totalTime) {

		if (minutes == null || minutes.equals(0L) || totalTime.equals(0L)) {
			return new Long(0L);
		}

		BigDecimal totalMinutes = new BigDecimal(totalTime);

		MathContext mc = new MathContext(3, RoundingMode.HALF_UP);
		BigDecimal percentBigDecimal = new BigDecimal(minutes).divide(totalMinutes, mc).multiply(new BigDecimal(100), mc);
		percentBigDecimal = percentBigDecimal.setScale(0, RoundingMode.HALF_UP);
		return percentBigDecimal.longValue();
	}

	public static String getMinutesAsString(Long minutes) {

		Long sumMinutesLong = minutes;
		String prefix = "";

		if (sumMinutesLong < 0) {
			sumMinutesLong = sumMinutesLong * (-1);
			prefix = "- ";
		}

		long sumHours = sumMinutesLong / 60;
		long sumMinutes = sumMinutesLong % 60;

		return prefix + getLeadingZero(sumHours, 2) + ":" + getLeadingZero(sumMinutes, 2);
	}

	public static Long getStringAsMinutes(String value) {

		String pattern = "^[0-9]{1,2}\\:[0-9]{2}$";

		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(value);

		if (value != null && m.find()) {
			// take 02:00 and return 120
			String[] array = value.split(":");

			Long hours = Long.valueOf(array[0]);
			Long minutes = Long.valueOf(array[1]);

			Long totalMinutes = (hours * 60) + minutes;

			return new Long(totalMinutes);
		} else {
			return 0L;
		}
	}

	public static String convertToHourMinuteString(BigDecimal miliseconds) {
		BigDecimal minutes = miliseconds.divide(BigDecimal.valueOf(1000L * 60), RoundingMode.HALF_UP);
		Long minutesLong2 = minutes.longValue();
		long hours2 = minutesLong2 / 60;
		long minutes2 = minutesLong2 % 60;

		return getLeadingZero(hours2, 1) + ":" + getLeadingZero(minutes2, 2);
	}

	public static String formatTime(Date date) {
		return format(new SimpleDateFormat(DATE_FORMAT_HH_mm), date);
	}

	public static Date parseDateTime(String date) throws ParseException {
		return parse(new SimpleDateFormat("dd.MM.yyyy HH:mm"), date);
	}

	public static Date parseTime(String date) throws ParseException {
		return parse(new SimpleDateFormat(DATE_FORMAT_HH_mm), date);
	}

	public static String formatDateTime(Date date) {
		return format(new SimpleDateFormat("dd.MM.yyyy HH:mm"), date);
	}

	public static Boolean sameDate(Calendar cal1, Calendar cal2) {
		return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
	}

	public static Date addHours(Date date, int hours) {
		Calendar cal = Calendar.getInstance(); // creates calendar
		cal.setTime(date); // sets calendar time/date
		cal.add(Calendar.HOUR_OF_DAY, hours); // adds one hour
		return cal.getTime();
	}

	public static boolean isSameDay(Date date1, Calendar cal2) {
		if (date1 == null) {
			throw new IllegalArgumentException("The date must not be null");
		}
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);
		return DateUtils.isSameDay(cal1, cal2);
	}
}
