package sk.qbsw.sed.web.grid.column;

import java.util.Date;

import org.apache.wicket.model.IModel;

import com.inmethod.grid.column.PropertyColumn;

public class CTimeColumn<M, I, P, S> extends PropertyColumn<M, I, P, S> {

	private static final long serialVersionUID = 1L;

	public CTimeColumn(IModel<String> headerModel, String propertyExpression, S sortProperty) {
		super(headerModel, propertyExpression, sortProperty);
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

		if (value instanceof Date) {
			int hour = ((Date) value).getHours();
			int mins = ((Date) value).getMinutes();
			String retVal = getLeadingZero(hour, 2) + ":" + getLeadingZero(mins, 2);
			return retVal;
		} else if (value instanceof String) {
			return (String) value;
		} else {
			return "unknown";
		}
	}

	private String getLeadingZero(int value, int count) {
		String retVal = "" + value;
		while (retVal.length() < count) {
			retVal = "0" + retVal;
		}
		return retVal;
	}
}
