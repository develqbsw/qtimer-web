package sk.qbsw.sed.web.grid.column.editable;

import java.util.Date;

import org.apache.wicket.datetime.DateConverter;
import org.apache.wicket.model.IModel;

import com.inmethod.grid.column.AbstractColumn;
import com.inmethod.grid.column.editable.DateTextFieldPanel;

public class CTimeTextFieldPanel<M, I, S> extends DateTextFieldPanel<M, I, S> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CTimeTextFieldPanel(String id, IModel<Date> date, IModel<I> rowModel, AbstractColumn<M, I, S> column, DateConverter dc) {
		super(id, date, rowModel, column, dc);
	}
}
