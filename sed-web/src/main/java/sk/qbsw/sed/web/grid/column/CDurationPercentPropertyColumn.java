package sk.qbsw.sed.web.grid.column;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.wicket.model.IModel;

import com.inmethod.grid.column.editable.EditablePropertyColumn;

import sk.qbsw.sed.panel.timestampGenerate.editable.CTimestampGenerateEditableTablePanel;

public class CDurationPercentPropertyColumn<M, I, P, S> extends EditablePropertyColumn<M, I, P, S> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private CTimestampGenerateEditableTablePanel panel;

	public CDurationPercentPropertyColumn(IModel<String> headerModel, String propertyExpression, S sortExpression, CTimestampGenerateEditableTablePanel panel) {
		super(headerModel, propertyExpression, sortExpression);
		setInitialSize(80);
		this.panel = panel;
	}

	public CDurationPercentPropertyColumn(IModel<String> headerModel, String propertyExpression, CTimestampGenerateEditableTablePanel panel) {
		super(headerModel, propertyExpression);
		setInitialSize(80);
		this.panel = panel;
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

		if (value instanceof Long) {
			Long val = (Long) value;

			if (panel.usingPercent()) {

				return val.toString() + "%";
			} else if (panel.usingMinutes()) { // vypocitaj minuty z percent

				return getMinutesAsString(new BigDecimal(val));
			} else {
				return "unknown";
			}

		} else if (value instanceof String) {
			return (String) value;
		} else {
			return "unknown";
		}
	}

	private String getMinutesAsString(BigDecimal minutes) {
		final BigDecimal SIXTY = new BigDecimal(60);

		BigDecimal bdValue = minutes;
		BigDecimal h = (bdValue).divide(SIXTY, 0, RoundingMode.DOWN);
		BigDecimal m = bdValue.subtract(h.multiply(SIXTY));

		String retVal = getLeadingZero(h.intValue(), 1) + ":" + getLeadingZero(m.intValue(), 2);

		return retVal;
	}

	private String getLeadingZero(int value, int count) {
		String retVal = "" + value;
		while (retVal.length() < count) {
			retVal = "0" + retVal;
		}
		return retVal;
	}
}
