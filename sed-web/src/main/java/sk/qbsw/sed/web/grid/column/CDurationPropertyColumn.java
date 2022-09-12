package sk.qbsw.sed.web.grid.column;

import org.apache.wicket.model.IModel;

import com.inmethod.grid.column.PropertyColumn;

import sk.qbsw.sed.common.utils.CDateUtils;

/**
 * 
 * @author Pavol Lobb
 *
 */
public class CDurationPropertyColumn<M, I, P, S> extends PropertyColumn<M, I, P, S> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CDurationPropertyColumn(IModel<String> headerModel, String propertyExpression, S sortProperty) {
		super(headerModel, propertyExpression, sortProperty);
		setInitialSize(80);
	}

	@Override
	public String getCellCssClass(IModel<I> rowModel, int rowNum) {
		return "imxt-cell-text-align-center";
	}

	@Override
	protected <C> CharSequence convertToString(C value) {
		if (value == null) {
			return "";
		}

		if (value instanceof Integer) {
			Integer iValue = (Integer) value;
			int h = iValue / 60;
			int m = iValue - h * 60;
			return CDateUtils.getLeadingZero(h, 1) + ":" + CDateUtils.getLeadingZero(m, 2);
		} else if (value instanceof String) {
			return (String) value;
		} else {
			return "unknown";
		}
	}
}
