package sk.qbsw.sed.web.grid.column.editable;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

import com.inmethod.grid.column.editable.SubmitCancelColumn;

/**
 * 
 * @author moravcik
 *
 * @param <M>
 * @param <I>
 * @param <S>
 */
public class CEditableValidityColumn<M, I, S> extends SubmitCancelColumn<M, I, S> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private CEditableValidityPanel<M, I> cellPanel;

	public CEditableValidityPanel<M, I> getCellPanel() {
		return cellPanel;
	}

	public void setCellPanel(CEditableValidityPanel<M, I> cellPanel) {
		this.cellPanel = cellPanel;
	}

	public CEditableValidityColumn(String columnId, IModel<String> headerModel) {
		super(columnId, headerModel);
	}

	@Override
	public Component newCell(WebMarkupContainer parent, String componentId, final IModel<I> rowModel) {
		cellPanel = new CEditableValidityPanel<M, I>(componentId, rowModel, getGrid()) {
			private static final long serialVersionUID = 1L;

			private WebMarkupContainer getRowComponent() {
				return getGrid().findParentRow(this);
			}

			@Override
			protected void onClick(AjaxRequestTarget target) {
				CEditableValidityColumn.this.onClick(target, rowModel, getRowComponent());
			}
		};
		return cellPanel;
	}

	protected void onClick(AjaxRequestTarget target, IModel<I> rowModel, WebMarkupContainer rowComponent) {
		getGrid().update();
	}
}
