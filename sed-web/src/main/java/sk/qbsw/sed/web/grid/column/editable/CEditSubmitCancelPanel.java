package sk.qbsw.sed.web.grid.column.editable;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

import com.inmethod.grid.common.AbstractGrid;
import com.inmethod.icon.Icon;

abstract class CEditSubmitCancelPanel<M, I> extends Panel {

	private static final long serialVersionUID = 1L;

	protected AbstractGrid<M, I, ?> getGrid() {
		return grid;
	}

	private final AbstractGrid<M, I, ?> grid;

	CEditSubmitCancelPanel(String id, final IModel<I> model, AbstractGrid<M, I, ?> grid) {
		super(id);

		this.grid = grid;

		AjaxSubmitLink edit = new AjaxSubmitLink("edit") {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return !getGrid().isItemEdited(model);
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				CEditSubmitCancelPanel.this.onEdit(target);
			}
		};
		edit.add(new AttributeAppender("title", getString("button.edit")));
		add(edit);

		AjaxSubmitLink submit = new SubmitLink("submit") {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return getGrid().isItemEdited(model);
			}
		};

		submit.setDefaultFormProcessing(false);
		submit.add(new AttributeAppender("title", getString("button.confirm")));
		add(submit);

		AjaxLink<Void> cancel = new AjaxLink<Void>("cancel") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				onCancel(target);
			}

			@Override
			public boolean isVisible() {
				return getGrid().isItemEdited(model);
			}
		};

		cancel.add(new AttributeAppender("title", getString("button.cancel")));
		add(cancel);
	}

	protected abstract void onEdit(AjaxRequestTarget target);

	protected abstract void onSubmitted(AjaxRequestTarget target);

	protected abstract void onError(AjaxRequestTarget target);

	protected abstract void onCancel(AjaxRequestTarget target);

	protected abstract Icon getSubmitIcon();

	protected abstract Icon getCancelIcon();

	private class SubmitLink extends AjaxSubmitLink {

		public SubmitLink(String id) {
			super(id, getGrid().getForm());
		}

		private static final long serialVersionUID = 1L;

		private boolean formComponentActive(FormComponent<?> formComponent) {
			return formComponent.isVisibleInHierarchy() && formComponent.isValid() && formComponent.isEnabled() && formComponent.isEnableAllowed();
		}

		@Override
		protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
			WebMarkupContainer gridRow = getGrid().findParentRow(CEditSubmitCancelPanel.this);
			final Boolean[] error = { false };

			// first iteration - validate components
			gridRow.visitChildren(FormComponent.class, new IVisitor<FormComponent<?>, Void>() {
				public void component(FormComponent<?> formComponent, IVisit<Void> visit) {

					if (formComponentActive(formComponent)) {
						formComponent.validate();
						if (formComponent.isValid()) {
							if (formComponent.processChildren()) {
								return;
							} else {
								visit.dontGoDeeper();
								return;
							}
						} else {
							error[0] = true;
							visit.dontGoDeeper();
						}
					}
				}
			});

			// second iteration - update models if the validation passed
			if (error[0] == false) {
				gridRow.visitChildren(FormComponent.class, new IVisitor<FormComponent<?>, Void>() {
					public void component(FormComponent<?> formComponent, IVisit<Void> visit) {

						if (formComponentActive(formComponent)) {

							formComponent.updateModel();

							if (formComponent.processChildren()) {
								return;
							} else {
								visit.dontGoDeeper();
								return;
							}
						}
					}
				});

				onSubmitted(target);
			} else {
				CEditSubmitCancelPanel.this.onError(target);
			}
		}

		@Override
		protected void onError(AjaxRequestTarget target, Form<?> form) {
			// do nothing
		}
	}
}
