package sk.qbsw.sed.server.service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

public class CTimeUtils {

	public static final BigDecimal BIGDECIMAL_HUNDRED = new BigDecimal(100);

	private static final BigDecimal MINUTE_IN_HOUR_CONSTANT = BigDecimal.valueOf(0.0166666667);

	private CTimeUtils() {

	}

	public static Calendar convertToStartTime(final Calendar retVal) {
		retVal.set(Calendar.SECOND, 0);
		retVal.set(Calendar.MILLISECOND, 0);

		return retVal;
	}

	public static Calendar convertToEndTime(final Calendar retVal) {
		retVal.set(Calendar.SECOND, 59);
		retVal.set(Calendar.MILLISECOND, 999);

		return retVal;
	}

	public static Calendar convertToEndDate(final Calendar retVal) {
		retVal.set(Calendar.HOUR_OF_DAY, 23);
		retVal.set(Calendar.MINUTE, 59);
		retVal.set(Calendar.SECOND, 59);
		retVal.set(Calendar.MILLISECOND, 999);

		return retVal;
	}

	public static Calendar convertToStartDate(final Calendar retVal) {
		retVal.set(Calendar.HOUR_OF_DAY, 0);
		retVal.set(Calendar.MINUTE, 0);
		retVal.set(Calendar.SECOND, 0);
		retVal.set(Calendar.MILLISECOND, 0);

		return retVal;
	}

	public static Calendar convertToEOD(final Calendar retVal) {
		retVal.set(Calendar.HOUR_OF_DAY, 23);
		retVal.set(Calendar.MINUTE, 59);
		retVal.set(Calendar.SECOND, 59);
		retVal.set(Calendar.MILLISECOND, 999);

		return retVal;
	}

	public static Calendar addMinute(final Calendar retVal) {
		retVal.add(Calendar.MINUTE, 1);

		return retVal;
	}

	public static Calendar decreaseMinute(final Calendar retVal) {
		retVal.add(Calendar.MINUTE, -1);

		return retVal;
	}

	public static void addMinute(final Date retVal) {
		retVal.setTime(retVal.getTime() + 60000);
	}

	public static BigDecimal convertToMinutes(final String duration) {
		final String[] durationGroup = duration.split(":");
		return new BigDecimal((Long.parseLong(durationGroup[0]) * 60) + Long.parseLong(durationGroup[1]));
	}

	public static BigDecimal getPercentTime(final BigDecimal partTime, final BigDecimal sumTime) {
		return (partTime.multiply(BIGDECIMAL_HUNDRED)).divide(sumTime, 2, BigDecimal.ROUND_HALF_UP);
	}

	public static BigDecimal convertToHours(final BigDecimal minutes) {
		return minutes.multiply(MINUTE_IN_HOUR_CONSTANT);
	}
}
