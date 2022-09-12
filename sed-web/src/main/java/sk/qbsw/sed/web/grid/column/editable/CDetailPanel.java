package sk.qbsw.sed.web.grid.column.editable;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.inmethod.grid.common.AbstractGrid;
import com.inmethod.icon.Icon;

abstract class CDetailPanel<M, I> extends Panel {

	private static final long serialVersionUID = 1L;

	protected AbstractGrid<M, I, ?> getGrid() {
		return grid;
	}

	private final AbstractGrid<M, I, ?> grid;

	public CDetailPanel(String id, final IModel<I> model, AbstractGrid<M, I, ?> grid) {
		super(id);

		this.grid = grid;

		AjaxSubmitLink select = new AjaxSubmitLink("select") {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return true;
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				CDetailPanel.this.onSelect(target);
			}
		};

		add(select);
	}

	protected abstract void onSelect(AjaxRequestTarget target);

	protected abstract Icon getSelectIcon();
}
