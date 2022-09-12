package sk.qbsw.sed.fw.component.converter;

import java.util.Locale;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

import sk.qbsw.sed.fw.utils.CEnumUtil;

/**
 * Date converter
 * 
 * @author Peter Bozik
 * @version 1.0.0
 * @since 1.0.0
 *
 * @param <T>
 */
public class CEnumConverter implements IConverter<Enum<?>> {
	private static final long serialVersionUID = 1L;
	public static final IConverter<Enum<?>> INSTANCE = new CEnumConverter();

	@Override
	public Enum<?> convertToObject(String value, Locale locale) throws ConversionException {
		throw new NotImplementedException("");
	}

	@Override
	public String convertToString(Enum<?> value, Locale locale) {
		return CEnumUtil.getValueText(value);
	}
}
