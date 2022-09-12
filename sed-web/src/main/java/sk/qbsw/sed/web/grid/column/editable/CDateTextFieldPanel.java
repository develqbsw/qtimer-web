package sk.qbsw.sed.web.grid.column.editable;

import java.util.Date;

import org.apache.wicket.datetime.DateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.model.IModel;

import com.inmethod.grid.column.AbstractColumn;
import com.inmethod.grid.column.editable.DateTextFieldPanel;

public class CDateTextFieldPanel<M, I, S> extends DateTextFieldPanel<M, I, S> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CDateTextFieldPanel(String id, IModel<Date> date, IModel<I> rowModel, AbstractColumn<M, I, S> column, DateConverter dc) {
		super(id, date, rowModel, column, dc);

		DateTextField tf = (DateTextField) getEditComponent();

		DatePicker dateFromPicker = new DatePicker();
		dateFromPicker.setShowOnFieldClick(true);
		dateFromPicker.setAutoHide(true);
		tf.add(dateFromPicker);
	}
}
