package sk.qbsw.sed.web.grid.column.editable;

import org.apache.wicket.model.IModel;

import com.inmethod.grid.column.editable.EditableCellPanel;
import com.inmethod.grid.column.editable.EditableCheckBoxColumn;

import sk.qbsw.sed.fw.utils.CStringResourceReader;

public class CEditableCheckBoxColumn<M, I, S> extends EditableCheckBoxColumn<M, I, S> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CEditableCheckBoxColumn(IModel<String> headerModel, String propertyExpression, S sortProperty) {
		super(headerModel, propertyExpression, sortProperty);
	}

	public CEditableCheckBoxColumn(IModel<String> headerModel, String propertyExpression) {
		super(headerModel, propertyExpression);
	}

	@Override
	protected <C> CharSequence convertToString(C object) {
		if (object != null && object instanceof Boolean) {
			if ((Boolean) object) {
				return CStringResourceReader.read("checkbox.value.yes");
			} else {
				return CStringResourceReader.read("checkbox.value.no");
			}
		}
		return "";
	}

	@Override
	protected EditableCellPanel<M, I, Boolean, S> newCellPanel(String componentId, IModel<I> rowModel, IModel<Boolean> cellModel) {
		return new CCheckBoxPanel<>(componentId, cellModel, rowModel, this);
	}
}
