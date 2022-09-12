package sk.qbsw.sed.web.grid.column.editable;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

import com.inmethod.grid.column.editable.SubmitCancelColumn;
import com.inmethod.icon.Icon;

public class CEditDeleteSubmitCancelColumn<M, I, S> extends SubmitCancelColumn<M, I, S> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CEditDeleteSubmitCancelColumn(String columnId, IModel<String> headerModel) {
		super(columnId, headerModel);
	}

	@Override
	public Component newCell(WebMarkupContainer parent, String componentId, final IModel<I> rowModel) {
		return new CEditDeleteSubmitCancelPanel<M, I>(componentId, rowModel, getGrid()) {

			private static final long serialVersionUID = 1L;

			private WebMarkupContainer getRowComponent() {
				return getGrid().findParentRow(this);
			}

			@Override
			protected void onCancel(AjaxRequestTarget target) {
				CEditDeleteSubmitCancelColumn.this.onCancel(target, rowModel, getRowComponent());
			}

			@Override
			protected void onError(AjaxRequestTarget target) {
				CEditDeleteSubmitCancelColumn.this.onError(target, rowModel, getRowComponent());
			}

			@Override
			protected void onSubmitted(AjaxRequestTarget target) {
				CEditDeleteSubmitCancelColumn.this.onSubmitted(target, rowModel, getRowComponent());
			}

			@Override
			protected Icon getSubmitIcon() {
				return CEditDeleteSubmitCancelColumn.this.getSubmitIcon();
			}

			@Override
			protected Icon getCancelIcon() {
				return CEditDeleteSubmitCancelColumn.this.getCancelIcon();
			}

			@Override
			protected void onEdit(AjaxRequestTarget target) {
				CEditDeleteSubmitCancelColumn.this.onEdit(target, rowModel, getRowComponent());

			}

			@Override
			protected void onDelete(AjaxRequestTarget target) {
				CEditDeleteSubmitCancelColumn.this.onDelete(target, rowModel, getRowComponent());

			}
		};
	}

	protected void onEdit(AjaxRequestTarget target, IModel<I> rowModel, WebMarkupContainer rowComponent) {
		getGrid().setItemEdit(rowModel, true);
		getGrid().update();
	}

	protected void onDelete(AjaxRequestTarget target, IModel<I> rowModel, WebMarkupContainer rowComponent) {
		// do nothing
	}
}
