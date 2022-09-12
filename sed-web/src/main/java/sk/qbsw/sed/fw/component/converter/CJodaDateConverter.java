package sk.qbsw.sed.fw.component.converter;

import java.util.Locale;

import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.convert.converter.AbstractConverter;
import org.apache.wicket.util.string.Strings;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;
import org.slf4j.LoggerFactory;

import sk.qbsw.sed.fw.utils.CApplicationUtils;

/**
 * 
 * @author bozik
 *
 */
public class CJodaDateConverter extends AbstractConverter<DateTime> {

	private static final long serialVersionUID = 1L;
	public static final IConverter<DateTime> INSTANCE = new CJodaDateConverter();

	@Override
	public DateTime convertToObject(final String value, final Locale locale) {
		final DateTime result;

		if (Strings.isEmpty(value)) {
			result = null;
		} else {
			DateTimeParser[] parsers = { CApplicationUtils.getLocalDateMediumFullFormatter(locale).getParser() };
			DateTimeFormatter formatter = new DateTimeFormatterBuilder().append(null, parsers).toFormatter();

			try {
				result = formatter.parseDateTime(value);
			} catch (final Exception e) {
				LoggerFactory.getLogger(CJodaDateConverter.class).error("convertToObject", e);
				throw this.newConversionException("Cannot parse '" + value + "' using format " + formatter, value, locale);
			}
		}

		return result;
	}

	@Override
	public String convertToString(final DateTime value, final Locale locale) {
		return CApplicationUtils.getLocalDateMediumFullFormatter(locale).print(value);
	}

	@Override
	protected Class<DateTime> getTargetType() {
		return DateTime.class;
	}
}
