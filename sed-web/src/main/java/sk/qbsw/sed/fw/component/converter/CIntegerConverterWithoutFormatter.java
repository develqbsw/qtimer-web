package sk.qbsw.sed.fw.component.converter;

import java.math.BigDecimal;
import java.util.Locale;

import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

/**
 * 
 * @author Ľubomír Grňo
 *
 */
public class CIntegerConverterWithoutFormatter implements IConverter<Integer> {

	private static final long serialVersionUID = 1L;
	public static final IConverter<Integer> INSTANCE = new CIntegerConverterWithoutFormatter();

	@Override
	public Integer convertToObject(String value, Locale locale) throws ConversionException {
		try {
			return Integer.parseInt(value);
		} catch (Exception e) {
			ConversionException ex = new ConversionException(e).setTargetType(Integer.class).setConverter(this).setResourceKey("IConverter." + BigDecimal.class.getSimpleName());
			throw ex;
		}
	}

	@Override
	public String convertToString(Integer value, Locale locale) {
		return value.toString();
	}
}
