package sk.qbsw.sed.component.converter;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.apache.wicket.util.convert.IConverter;

public class CCalendarConverter implements IConverter<Calendar> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Converts String to Calendar
	 */
	@Override
	public Calendar convertToObject(String value, Locale locale) {
		try {
			DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT, locale);
			Date date = formatter.parse(value);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			return cal;
		} catch (ParseException ex) {
			Logger.getLogger(CCalendarConverter.class).error("Error parsing calendar text");
		}
		return null;
	}

	/**
	 * Converts calendar to String
	 */
	@Override
	public String convertToString(Calendar value, Locale locale) {
		if (value != null) {
			DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT, locale);
			return formatter.format(((Calendar) value).getTime());
		}
		return null;
	}
}
