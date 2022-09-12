package sk.qbsw.sed.fw.component.converter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Ľubomír Grňo
 *
 */
public class CExchangeRateConverter implements IConverter<BigDecimal> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final IConverter<BigDecimal> INSTANCE = new CExchangeRateConverter();
	private static final Logger log = LoggerFactory.getLogger(CExchangeRateConverter.class);

	private static DecimalFormat formatter;
	private static DecimalFormatSymbols symbols;

	static {
		formatter = (DecimalFormat) NumberFormat.getInstance();
		symbols = formatter.getDecimalFormatSymbols();
		symbols.setGroupingSeparator(' ');
		symbols.setDecimalSeparator(',');
		formatter = new DecimalFormat("###,##0.0000", symbols);
	}

	@Override
	public BigDecimal convertToObject(String value, Locale locale) throws ConversionException {
		DecimalFormat df = new DecimalFormat();
		df.setParseBigDecimal(true);
		BigDecimal result = null;
		try {
			result = (BigDecimal) df.parse(value);
		} catch (ParseException e) {
			log.error("convertToObject", e);
			throw new ConversionException(e).setTargetType(BigDecimal.class).setConverter(this).setResourceKey("IConverter." + BigDecimal.class.getSimpleName());
		}
		return result.setScale(2, RoundingMode.CEILING);
	}

	@Override
	public String convertToString(BigDecimal value, Locale locale) {
		return formatter.format(value);
	}
}
