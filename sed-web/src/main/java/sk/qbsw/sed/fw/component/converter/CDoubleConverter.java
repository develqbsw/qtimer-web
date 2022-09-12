package sk.qbsw.sed.fw.component.converter;

import java.util.Locale;

import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

/**
 * Double converter
 * 
 * @author farkas.roman
 * @version 1.0.0
 * @since 1.0.0
 *
 * @param <T>
 */
public class CDoubleConverter implements IConverter<Double> {
	private static final long serialVersionUID = 1L;
	public static final IConverter<Double> INSTANCE = new CDoubleConverter();

	@Override
	public Double convertToObject(String value, Locale locale) throws ConversionException {
		try {
			return new Double(value);
		} catch (Exception e) {
			ConversionException ex = new ConversionException(e).setTargetType(Double.class).setConverter(this).setResourceKey("IConverter." + Double.class.getSimpleName());
			throw ex;
		}
	}

	@Override
	public String convertToString(Double value, Locale locale) {
		return value.toString();
	}
}
