package sk.qbsw.sed.fw.utils;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import sk.qbsw.sed.fw.CFrameworkConfiguration;

/**
 * 
 * @author Peter Božík
 * @version 1.0.0
 * @since 1.0.0
 *
 */
public class CApplicationUtils {

	private CApplicationUtils() {
		// Auto-generated constructor stub
	}

	private static final String FORMAT_DATE_MEDIUM = "dd.MM.yyyy";
	private static final String FORMAT_DATETIME_MEDIUM = "dd.MM.yyyy HH:mm";
	private static final String FORMAT_TIME_FULL = "HH:mm:ss";
	private static final String FORMAT_TIME = "HH:mm";
	private static final String FORMAT_DATETIME_MEDIUM_FULL = CApplicationUtils.FORMAT_DATE_MEDIUM + " " + CApplicationUtils.FORMAT_TIME_FULL;
	private static final String FORMAT_YEARMONTH = "MM/yyyy";

	public static final DateTimeFormatter LOCALDATE_MEDIUM_FORMATTER = DateTimeFormat.forPattern(CApplicationUtils.FORMAT_DATE_MEDIUM);
	public static final DateTimeFormatter DATETIME_MEDIUM_FULL_FORMATTER = DateTimeFormat.forPattern(CApplicationUtils.FORMAT_DATETIME_MEDIUM_FULL);
	public static final DateTimeFormatter DATETIME_MEDIUM_FORMATTER = DateTimeFormat.forPattern(CApplicationUtils.FORMAT_DATETIME_MEDIUM);
	public static final DateTimeFormatter YEARMONTH_FORMATTER = DateTimeFormat.forPattern(CApplicationUtils.FORMAT_YEARMONTH);
	public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormat.forPattern(CApplicationUtils.FORMAT_TIME);

	public static int getRowsPerTable() {
		return CFrameworkConfiguration.ROWS_PER_TABLE;
	}

	public static boolean isPropertyEmpty(String property) {
		if (StringUtils.isBlank(property)) {
			return true;
		}
		
		if ("none".equals(property)) {
			return true;
		}
		
		return false;
	}

	public static DateTimeFormatter getLocalDateMediumFullFormatter(Locale locale) {
		return CApplicationUtils.LOCALDATE_MEDIUM_FORMATTER.withLocale(locale == null ? Locale.getDefault() : locale);
	}

	public static DateTimeFormatter getDateTimeMediumFullFormatter(Locale locale) {
		return CApplicationUtils.DATETIME_MEDIUM_FULL_FORMATTER.withLocale(locale == null ? Locale.getDefault() : locale);
	}

	public static DateTimeFormatter getTimeFormatter(Locale locale) {
		return CApplicationUtils.TIME_FORMATTER.withLocale(locale == null ? Locale.getDefault() : locale);
	}

	public static DateTimeFormatter getDateTimeMediumFormatter(Locale locale) {
		return CApplicationUtils.DATETIME_MEDIUM_FORMATTER.withLocale(locale == null ? Locale.getDefault() : locale);
	}

	public static DateTimeFormatter getYearMonthFormatter(Locale locale) {
		return CApplicationUtils.YEARMONTH_FORMATTER.withLocale(locale == null ? Locale.getDefault() : locale);
	}
}
