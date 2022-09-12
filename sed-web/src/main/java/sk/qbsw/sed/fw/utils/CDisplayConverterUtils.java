package sk.qbsw.sed.fw.utils;

import java.io.Serializable;
import java.util.Date;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.convert.IConverter;
import org.joda.time.DateTime;

import sk.qbsw.sed.fw.component.EDataType;
import sk.qbsw.sed.fw.component.converter.CConverterFactory;
import sk.qbsw.sed.fw.component.converter.CDateConverter;
import sk.qbsw.sed.fw.component.converter.CJodaDateConverter;
import sk.qbsw.sed.fw.component.converter.CJodaDateTimeConverter;

/**
 * Converter for displayed values
 * 
 * @author Peter Bozik
 * @version 1.0.0
 * @since 1.0.0
 *
 */
public class CDisplayConverterUtils {

	private CDisplayConverterUtils() {
		// Auto-generated constructor stub
	}

	public static Serializable getConvertedDisplayValue(Object o, EDataType type) {
		return getConvertedDisplayValue(o, type, null);
	}

	public static Serializable getConvertedDisplayValue(Object o, EDataType type, Boolean input) {
		final Serializable result;

		if (o == null) {
			result = null;
		} else {
			IConverter converter = CConverterFactory.getConverter(o.getClass(), type);
			if (converter != null) {
				result = converter.convertToString(o, null);
			} else {
				if (EDataType.DATE.equals(type) || EDataType.DATE_RANGE.equals(type) || EDataType.DATE_TIME.equals(type) || EDataType.DATE_TIME_FILTER_DATE.equals(type)) {
					result = convertDateToString(o, type, input);
				} else if (o instanceof Enum<?>) {
					result = CEnumUtil.getValueText((Enum<?>) o);
				} else if (o instanceof Boolean) {
					result = CStringResourceReader.read(Boolean.TRUE.equals(o) ? "label.boolean.true" : "label.boolean.false");
				} else if (o instanceof Serializable) {
					result = (Serializable) o;
				}

				else {
					result = String.valueOf(o);
				}
			}

		}

		return result;
	}

	private static String convertDateToString(Object o, EDataType type, Boolean input) {
		if (o instanceof Date) {
			return CDateConverter.INSTANCE.convertToString((Date) o, null);
		}
		if (o instanceof DateTime) {
			if (EDataType.DATE_TIME.equals(type)) {
				return CJodaDateTimeConverter.INSTANCE.convertToString((DateTime) o, null);
			}
			return CJodaDateConverter.INSTANCE.convertToString((DateTime) o, null);
		}
		if (o instanceof String) {
			return (String) o;
		}
		return null;
	}

	public static IModel<Serializable> getDisplayModel(Object o, EDataType type, Boolean input) {
		return Model.of(CDisplayConverterUtils.getConvertedDisplayValue(o, type, input));
	}
}
