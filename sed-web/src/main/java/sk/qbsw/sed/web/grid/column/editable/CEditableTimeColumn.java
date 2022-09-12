package sk.qbsw.sed.web.grid.column.editable;

import java.util.Date;

import org.apache.wicket.datetime.DateConverter;
import org.apache.wicket.model.IModel;

import com.inmethod.grid.column.editable.EditableCellPanel;
import com.inmethod.grid.column.editable.EditableDateColumn;

public class CEditableTimeColumn<M, I, S> extends EditableDateColumn<M, I, S> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	DateConverter converter;

	public CEditableTimeColumn(IModel<String> headerModel, String propertyExpression, S sortProperty, DateConverter converter) {
		super(headerModel, propertyExpression, sortProperty, converter);
		this.converter = converter;

		this.setInitialSize(40);
	}

	@Override
	protected EditableCellPanel<M, I, Date, S> newCellPanel(String componentId, IModel<I> rowModel, IModel<Date> cellModel) {
		return new CTimeTextFieldPanel<>(componentId, cellModel, rowModel, this, this.converter);
	}
}
