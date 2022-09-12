package sk.qbsw.sed.web.grid.column.editable;

import java.util.List;

import org.apache.wicket.model.IModel;

import com.inmethod.grid.column.editable.EditableCellPanel;
import com.inmethod.grid.column.editable.EditablePropertyColumn;

import sk.qbsw.sed.client.model.codelist.CCodeListRecord;

public class CSelectProjectColumn<M, I, P, S> extends EditablePropertyColumn<M, I, P, S> {

	private static final long serialVersionUID = 1L;

	private List<CCodeListRecord> projectList;

	public CSelectProjectColumn(IModel<String> headerModel, String propertyExpression, S sortProperty, List<CCodeListRecord> projectList) {
		super(headerModel, propertyExpression, sortProperty);

		this.projectList = projectList;

		this.setInitialSize(300);
	}

	public CSelectProjectColumn(IModel<String> headerModel, String propertyExpression, List<CCodeListRecord> projectList) {
		super(headerModel, propertyExpression);

		this.projectList = projectList;
		this.setInitialSize(300);
	}

	@Override
	protected EditableCellPanel<M, I, P, S> newCellPanel(String componentId, IModel<I> rowModel, IModel<P> cellModel) {
		return new CSelectProjectPanel<>(componentId, cellModel, rowModel, this, projectList);
	}
}
