package sk.qbsw.sed.web.table;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.model.IModel;
import org.joda.time.DateTime;

public class CDatePropertyColumn<T> extends PropertyColumn {
	// **********************************************************************************************************************
	// Fields
	// **********************************************************************************************************************

	private static final long serialVersionUID = -1285893946332340828L;
	private final String format;

	// **********************************************************************************************************************
	// Constructors
	// **********************************************************************************************************************

	public CDatePropertyColumn(IModel<String> displayModel, String propertyExpression, String format) {
		super(displayModel, propertyExpression);
		this.format = format;
	}

	public CDatePropertyColumn(IModel iModel, String sortProperty, String propertyExpression, String format) {
		super(iModel, sortProperty, propertyExpression);
		this.format = format;
	}

	// **********************************************************************************************************************
	// Other Methods
	// **********************************************************************************************************************

	@Override
	protected IModel createLabelModel(IModel iModel) {
		return new DateTimeModel(super.getDataModel(iModel));
	}

	// **********************************************************************************************************************
	// Inner Classes
	// **********************************************************************************************************************

	private class DateTimeModel implements IModel<String> {
		private final IModel inner;
		private static final long serialVersionUID = 190887916985140272L;

		private DateTimeModel(IModel inner) {
			this.inner = inner;
		}

		public void detach() {
			inner.detach();
		}

		public String getObject() {
			Calendar dateTime = (Calendar) inner.getObject();
			if (dateTime == null) {
				return "";
			}
			final Date date = dateTime.getTime();
			SimpleDateFormat dateFormatter = new SimpleDateFormat(format);
			return dateFormatter.format(date);
		}

		public void setObject(String s) {
			SimpleDateFormat dateFormatter = new SimpleDateFormat(format);
			try {
				Date date = dateFormatter.parse(s);
				inner.setObject(new DateTime(date.getTime()));
			} catch (ParseException e) {
				throw new WicketRuntimeException("Unable to parse date.", e);
			}
		}
	}
}