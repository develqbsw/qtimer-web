package sk.qbsw.sed.web.grid.column.editable;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.inmethod.grid.common.AbstractGrid;

import sk.qbsw.sed.client.model.abstractclass.ADataForEditableColumn;

/**
 * 
 * @author moravcik
 *
 * @param <M>
 * @param <I>
 */
public abstract class CEditableValidityPanel<M, I> extends Panel {

	private static final long serialVersionUID = 1L;

	private AjaxSubmitLink valid;

	private AjaxSubmitLink invalid;

	public AjaxSubmitLink getValid() {
		return valid;
	}

	public void setValid(AjaxSubmitLink valid) {
		this.valid = valid;
	}

	public AjaxSubmitLink getInvalid() {
		return invalid;
	}

	public void setInvalid(AjaxSubmitLink invalid) {
		this.invalid = invalid;
	}

	protected AbstractGrid<M, I, ?> getGrid() {
		return grid;
	}

	private final AbstractGrid<M, I, ?> grid;

	public CEditableValidityPanel(String id, final IModel<I> model, AbstractGrid<M, I, ?> grid) {
		super(id);

		this.grid = grid;

		valid = new AjaxSubmitLink("valid") {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return ((ADataForEditableColumn) model.getObject()).isActive();
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				CEditableValidityPanel.this.onClick(target);
			}
		};

		add(valid);

		invalid = new AjaxSubmitLink("invalid") {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return !((ADataForEditableColumn) model.getObject()).isActive();
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				CEditableValidityPanel.this.onClick(target);
			}
		};

		add(invalid);
	}

	protected abstract void onClick(AjaxRequestTarget target);
}
