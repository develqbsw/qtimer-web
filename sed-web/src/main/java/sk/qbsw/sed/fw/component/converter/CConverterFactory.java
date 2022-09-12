package sk.qbsw.sed.fw.component.converter;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.wicket.util.convert.IConverter;
import org.joda.time.DateTime;

import sk.qbsw.sed.fw.component.EDataType;

public class CConverterFactory {
	
	private CConverterFactory() {

	}

	public static IConverter getConverter(Class type, EDataType inputType, Boolean input) {
		EDataType inner = inputType;
		if (BooleanUtils.isTrue(input)) {
			inner = inner.getInput();
		}

		if (DateTime.class.equals(type)) {
			if (EDataType.DATE.equals(inner)) {
				return CJodaDateConverter.INSTANCE;
			}
			if (EDataType.DATE_TIME.equals(inner)) {
				return CJodaDateTimeConverter.INSTANCE;
			}
			if (EDataType.TIME.equals(inner)) {
				return CJodaTimeConverter.INSTANCE;
			}
			if (EDataType.DATE_TIME_FILTER_DATE.equals(inner)) {
				return CJodaDateTimeConverter.INSTANCE;
			}
		}
		if (Enum.class.isAssignableFrom(type)) {
			return CEnumConverter.INSTANCE;
		}
		if (EDataType.DATE.equals(inner)) {
			return CDateConverter.INSTANCE;
		}
		if (EDataType.TIME.equals(inner)) {
			return CTimeConverter.INSTANCE;
		}
		if (EDataType.DATE_TIME.equals(inner)) {
			return CDateTimeConverter.INSTANCE;
		}
		if (EDataType.BIG_DECIMAL.equals(inner)) {
			return CBigDecimalConverter.INSTANCE;
		}
		if (EDataType.BIG_DECIMAL3.equals(inner)) {
			return CBigDecimal3Converter.INSTANCE;
		}
		if (EDataType.PERCENTAGE.equals(inner)) {
			return CBigDecimalPercentageConverter.INSTANCE;
		}

		if (EDataType.NUMBER.equals(inner) && type.isAssignableFrom(Integer.class)) {
			return CIntegerConverter.INSTANCE;
		}
		if (EDataType.NUMBER_FILTER.equals(inner) && type.isAssignableFrom(Integer.class)) {
			return CIntegerConverterWithoutFormatter.INSTANCE;
		}
		if (EDataType.NUMBER.equals(inner) && type.isAssignableFrom(Long.class)) {
			return CLongConverter.INSTANCE;
		}
		if (EDataType.NUMBER_LONG.equals(inner) && type.isAssignableFrom(Long.class)) {
			return CLongConverterWithoutFormatter.INSTANCE;
		}
		if (EDataType.DOUBLE.equals(inner)) {
			return CDoubleConverter.INSTANCE;
		}
		if (EDataType.EXCHANGE_RATE.equals(inner)) {
			return CExchangeRateConverter.INSTANCE;
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public static IConverter getConverter(Class type, EDataType inputType) {
		return getConverter(type, inputType, null);
	}
}
