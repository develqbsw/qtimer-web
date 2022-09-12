package sk.qbsw.sed.web.grid.column.editable;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

import com.inmethod.grid.column.editable.SubmitCancelColumn;
import com.inmethod.icon.Icon;

public class CDetailColumn<M, I, S> extends SubmitCancelColumn<M, I, S> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CDetailColumn(String columnId, IModel<String> headerModel) {
		super(columnId, headerModel);
	}

	@Override
	public Component newCell(WebMarkupContainer parent, String componentId, final IModel<I> rowModel) {
		return new CDetailPanel<M, I>(componentId, rowModel, getGrid()) {
			private static final long serialVersionUID = 1L;

			private WebMarkupContainer getRowComponent() {
				return getGrid().findParentRow(this);
			}

			@Override
			protected void onSelect(AjaxRequestTarget target) {
				CDetailColumn.this.onSelect(target, rowModel, getRowComponent());
			}

			@Override
			protected Icon getSelectIcon() {
				return CDetailColumn.this.getSubmitIcon();
			}
		};
	}

	protected void onSelect(AjaxRequestTarget target, IModel<I> rowModel, WebMarkupContainer rowComponent) {
		getGrid().update();
	}
}
