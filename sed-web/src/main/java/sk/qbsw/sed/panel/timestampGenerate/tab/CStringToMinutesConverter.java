package sk.qbsw.sed.panel.timestampGenerate.tab;

import java.util.Locale;

import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

import sk.qbsw.sed.common.utils.CDateUtils;

public class CStringToMinutesConverter implements IConverter<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Long convertToObject(String value, Locale locale) throws ConversionException {

		return CDateUtils.getStringAsMinutes(value);
	}

	@Override
	public String convertToString(Long minutes, Locale locale) {

		return CDateUtils.getMinutesAsString(minutes);
	}
}
