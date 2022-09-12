package sk.qbsw.sed.fw.component.converter;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

import sk.qbsw.sed.common.utils.CDateUtils;

/**
 * Date converter
 * 
 * @author Peter Bozik
 * @version 1.0.0
 * @since 1.0.0
 *
 * @param <T>
 */
public class CDateTimeConverter implements IConverter<Date> {
	private static final long serialVersionUID = 1L;
	public static final IConverter<Date> INSTANCE = new CDateTimeConverter();

	@Override
	public Date convertToObject(String value, Locale locale) throws ConversionException {
		try {
			return CDateUtils.parseDateTime(value);
		} catch (ParseException e) {
			throw new ConversionException(e);
		}
	}

	@Override
	public String convertToString(Date value, Locale locale) {
		return CDateUtils.formatDateTime(value);
	}
}
