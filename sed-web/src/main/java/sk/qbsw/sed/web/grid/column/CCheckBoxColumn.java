package sk.qbsw.sed.web.grid.column;

import org.apache.wicket.model.IModel;

import com.inmethod.grid.column.PropertyColumn;

import sk.qbsw.sed.fw.utils.CStringResourceReader;

public class CCheckBoxColumn<M, I, P, S> extends PropertyColumn<M, I, P, S> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CCheckBoxColumn(IModel<String> headerModel, String propertyExpression, S sortProperty) {
		super(headerModel, propertyExpression, sortProperty);
	}

	@Override
	protected <C> CharSequence convertToString(C object) {
		if (object != null && object instanceof Boolean) {
			if ((Boolean) object) {
				return CStringResourceReader.read("checkbox.value.yes");
			} else {
				return CStringResourceReader.read("checkbox.value.no");
			}
		}
		return "";
	}
}
