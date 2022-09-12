package sk.qbsw.sed.web.grid.column.editable;

import java.util.Date;

import org.apache.wicket.datetime.DateConverter;
import org.apache.wicket.model.IModel;

import com.inmethod.grid.column.editable.EditableCellPanel;
import com.inmethod.grid.column.editable.EditableDateColumn;

public class CEditableDateColumn<M, I, S> extends EditableDateColumn<M, I, S> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private DateConverter converter;

	public CEditableDateColumn(IModel<String> headerModel, String propertyExpression, S sortProperty, DateConverter dc) {
		super(headerModel, propertyExpression, sortProperty, dc);
		converter = dc;
	}

	@Override
	protected EditableCellPanel<M, I, Date, S> newCellPanel(String componentId, IModel<I> rowModel, IModel<Date> cellModel) {
		return new CDateTextFieldPanel<>(componentId, cellModel, rowModel, this, converter);
	}
}
