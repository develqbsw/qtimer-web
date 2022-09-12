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
public class CLongConverter implements IConverter<Long> {
	private static final long serialVersionUID = 1L;
	public static final IConverter<Long> INSTANCE = new CLongConverter();
	private static DecimalFormat formatter;
	private static DecimalFormatSymbols symbols;

	static {
		formatter = (DecimalFormat) NumberFormat.getInstance();
		symbols = formatter.getDecimalFormatSymbols();
		symbols.setGroupingSeparator(' ');
		formatter = new DecimalFormat("###,###", symbols);
	}

	@Override
	public Long convertToObject(String value, Locale locale) throws ConversionException {
		try {
			return Long.parseLong(value);
		} catch (Exception e) {
			ConversionException ex = new ConversionException(e).setTargetType(Long.class).setConverter(this).setResourceKey("IConverter." + BigDecimal.class.getSimpleName());
			throw ex;
		}
	}

	@Override
	public String convertToString(Long value, Locale locale) {
		return formatter.format(value.longValue());
	}
}
