package sk.qbsw.sed.web.grid.column;

import org.apache.wicket.model.IModel;

import com.inmethod.grid.column.PropertyColumn;

import sk.qbsw.sed.fw.utils.CStringResourceReader;

/**
 * 
 * @author moravcik
 *
 * @param <M>
 * @param <I>
 * @param <P>
 * @param <S>
 */
public class CDayTypeColumn<M, I, P, S> extends PropertyColumn<M, I, P, S> {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param headerModel
	 * @param propertyExpression
	 * @param sortProperty
	 */
	public CDayTypeColumn(IModel<String> headerModel, String propertyExpression, S sortProperty) {
		super(headerModel, propertyExpression, sortProperty);
	}

	@Override
	protected <C> CharSequence convertToString(C object) {
		if (object != null && object instanceof Long) {
			if ((Long) object == 0) {
				return CStringResourceReader.read("daytype.value.workday");
			} else {
				return CStringResourceReader.read("daytype.value.dayoff");
			}
		}

		return "";
	}
}
