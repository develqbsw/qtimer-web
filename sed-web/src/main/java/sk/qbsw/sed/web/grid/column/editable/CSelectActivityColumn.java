package sk.qbsw.sed.web.grid.column.editable;

import java.util.List;

import org.apache.wicket.model.IModel;

import com.inmethod.grid.column.editable.EditableCellPanel;
import com.inmethod.grid.column.editable.EditablePropertyColumn;

import sk.qbsw.sed.client.model.codelist.CCodeListRecord;

public class CSelectActivityColumn<M, I, P, S> extends EditablePropertyColumn<M, I, P, S> {

	private static final long serialVersionUID = 1L;

	private List<CCodeListRecord> activityList;

	public CSelectActivityColumn(IModel<String> headerModel, String propertyExpression, S sortProperty, List<CCodeListRecord> activityList) {
		super(headerModel, propertyExpression, sortProperty);

		this.activityList = activityList;

		this.setInitialSize(150);
	}

	public CSelectActivityColumn(IModel<String> headerModel, String propertyExpression, List<CCodeListRecord> activityList) {
		super(headerModel, propertyExpression);

		this.activityList = activityList;

		this.setInitialSize(150);
	}

	@Override
	protected EditableCellPanel<M, I, P, S> newCellPanel(String componentId, IModel<I> rowModel, IModel<P> cellModel) {
		return new CSelectActivityPanel<>(componentId, cellModel, rowModel, this, activityList);
	}
}
