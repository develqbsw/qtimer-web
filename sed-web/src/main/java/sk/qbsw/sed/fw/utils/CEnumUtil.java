package sk.qbsw.sed.fw.utils;

import java.io.Serializable;

/**
 * 
 * @author Peter Bozik
 * @version 1.0.0
 * @since 1.0.0
 *
 */
public class CEnumUtil implements Serializable {

	private static final long serialVersionUID = 1L;

	public static String getPropertyKeyForValue(Enum<?> e) {
		return "enum." + e.getClass().getSimpleName() + "." + e.name();
	}

	public static String getValueText(Enum<?> e) {
		final String key = CEnumUtil.getPropertyKeyForValue(e);

		return CStringResourceReader.read(key);
	}
}
