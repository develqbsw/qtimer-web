package sk.qbsw.sed.web.grid.column.editable;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

import com.inmethod.grid.column.editable.SubmitCancelColumn;
import com.inmethod.icon.Icon;

public class CEditSubmitCancelColumn<M, I, S> extends SubmitCancelColumn<M, I, S> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CEditSubmitCancelColumn(String columnId, IModel<String> headerModel) {
		super(columnId, headerModel);
	}

	@Override
	public Component newCell(WebMarkupContainer parent, String componentId, final IModel<I> rowModel) {
		return new CEditSubmitCancelPanel<M, I>(componentId, rowModel, getGrid()) {

			private static final long serialVersionUID = 1L;

			private WebMarkupContainer getRowComponent() {
				return getGrid().findParentRow(this);
			}

			@Override
			protected void onCancel(AjaxRequestTarget target) {
				CEditSubmitCancelColumn.this.onCancel(target, rowModel, getRowComponent());
			}

			@Override
			protected void onError(AjaxRequestTarget target) {
				CEditSubmitCancelColumn.this.onError(target, rowModel, getRowComponent());
			}

			@Override
			protected void onSubmitted(AjaxRequestTarget target) {
				CEditSubmitCancelColumn.this.onSubmitted(target, rowModel, getRowComponent());
			}

			@Override
			protected Icon getSubmitIcon() {
				return CEditSubmitCancelColumn.this.getSubmitIcon();
			}

			@Override
			protected Icon getCancelIcon() {
				return CEditSubmitCancelColumn.this.getCancelIcon();
			}

			@Override
			protected void onEdit(AjaxRequestTarget target) {
				CEditSubmitCancelColumn.this.onEdit(target, rowModel, getRowComponent());
			}
		};
	}

	protected void onEdit(AjaxRequestTarget target, IModel<I> rowModel, WebMarkupContainer rowComponent) {
		getGrid().setItemEdit(rowModel, true);
		getGrid().update();
	}
}
