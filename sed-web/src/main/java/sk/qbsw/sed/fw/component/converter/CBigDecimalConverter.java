package sk.qbsw.sed.fw.component.converter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

/**
 * Date converter
 * 
 * @author Peter Bozik
 * @version 1.0.0
 * @since 1.0.0
 *
 * @param <T>
 */
public class CBigDecimalConverter implements IConverter<BigDecimal> {
	private static final long serialVersionUID = 1L;
	public static final IConverter<BigDecimal> INSTANCE = new CBigDecimalConverter();
	private static DecimalFormat formatter;
	private static DecimalFormatSymbols symbols;

	static {
		formatter = (DecimalFormat) NumberFormat.getInstance();
		symbols = formatter.getDecimalFormatSymbols();
		symbols.setGroupingSeparator(' ');
	}

	@Override
	public BigDecimal convertToObject(String value, Locale locale) throws ConversionException {
		try {
			return new BigDecimal(value);
		} catch (Exception e) {
			ConversionException ex = new ConversionException(e)//
					.setTargetType(BigDecimal.class)//
					.setConverter(this)//
					.setResourceKey("IConverter." + BigDecimal.class.getSimpleName());
			throw ex;
		}
	}

	@Override
	public String convertToString(BigDecimal value, Locale locale) {
		return formatter.format(value.longValue());
	}
}
