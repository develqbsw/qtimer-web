package sk.qbsw.sed.fw.component.renderer;

import java.io.Serializable;

import org.apache.wicket.markup.html.form.ChoiceRenderer;

import sk.qbsw.sed.fw.utils.CDisplayConverterUtils;

/**
 * 
 * @author Peter Božík
 * @version 1.0.0
 * @since 1.0.0
 *
 * @param <T>
 */
public class CChoiceRenderer<T> extends ChoiceRenderer<T> {

	private static final long serialVersionUID = 1L;

	@Override
	public Object getDisplayValue(T o) {
		final String result;
		final Serializable tmp = CDisplayConverterUtils.getConvertedDisplayValue(o, null);

		if (tmp instanceof String) {
			result = (String) tmp;
		} else {
			result = String.valueOf(tmp);
		}

		return result;
	}

	@Override
	public String getIdValue(T o, int index) {
		final String result;

		if (o == null) {
			result = "";
		} else if (o instanceof Enum) {
			result = ((Enum<?>) o).name();
		} else {
			result = o.toString();
		}
		return result;
	}
}
