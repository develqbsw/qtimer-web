package sk.qbsw.sed.web.grid.column.editable;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

import com.inmethod.grid.column.editable.SubmitCancelColumn;
import com.inmethod.icon.Icon;

public class CSelectFavoriteColumn<M, I, S> extends SubmitCancelColumn<M, I, S> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CSelectFavoriteColumn(String columnId, IModel<String> headerModel) {
		super(columnId, headerModel);
	}

	@Override
	public Component newCell(WebMarkupContainer parent, String componentId, final IModel<I> rowModel) {
		return new CSelectFavoritePanel<M, I>(componentId, rowModel, getGrid()) {
			private static final long serialVersionUID = 1L;

			private WebMarkupContainer getRowComponent() {
				return getGrid().findParentRow(this);
			}

			@Override
			protected void onSelect(AjaxRequestTarget target) {
				CSelectFavoriteColumn.this.onSelect(target, rowModel, getRowComponent());
			}

			@Override
			protected void onDeSelect(AjaxRequestTarget target) {
				CSelectFavoriteColumn.this.onDeSelect(target, rowModel, getRowComponent());
			}

			@Override
			protected Icon getSelectIcon() {
				return CSelectFavoriteColumn.this.getSubmitIcon();
			}

			@Override
			protected Icon getDeSelectIcon() {
				return CSelectFavoriteColumn.this.getCancelIcon();
			}
		};
	}

	protected void onSelect(AjaxRequestTarget target, IModel<I> rowModel, WebMarkupContainer rowComponent) {
		getGrid().update();

	}

	protected void onDeSelect(AjaxRequestTarget target, IModel<I> rowModel, WebMarkupContainer rowComponent) {
		getGrid().update();
	}
}
