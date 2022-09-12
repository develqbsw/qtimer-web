package sk.qbsw.sed.web.ui.components.panel;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import sk.qbsw.sed.web.ui.components.CEventActionPayload;

/**
 * border class wrapping any panel. It creates "modal" window with that panel.
 * methods hide(), show() are important.
 * 
 * you can change title by changing model of title - getTitleModel() OR you can
 * override getTitleModel() by yourself.
 * 
 * @author martinkovic
 * @since 2.1.0
 * @version 2.1.0
 */
public class CModalBorder extends Border {
	private static final long serialVersionUID = 1L;

	/** modal div container */
	private final WebMarkupContainer modal;

	/** close button container */
	private final WebMarkupContainer close;

	/** label with title */
	private final Label title;

	/** model with title */
	private final Model<String> titleModel;

	/**
	 * draggable property for modal window
	 */
	private boolean isDraggable = true;

	/**
	 * creates new modal border panel
	 * 
	 * @param id
	 */
	public CModalBorder(String id) {
		super(id);
		titleModel = new Model<>();

		modal = new WebMarkupContainer("modalWindow");
		modal.setOutputMarkupId(true);

		close = new WebMarkupContainer("closeBtn");
		close.add(new AjaxEventBehavior("click") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				hide(target);
			}

		});
		title = new Label("title", getTitleModel());
		title.setOutputMarkupId(true);
		getBodyContainer().setOutputMarkupId(true);
		modal.add(title);
		modal.add(close);
		modal.add(getBodyContainer());
		addToBorder(modal);
	}

	/**
	 * 
	 * @return title model of this modal window.
	 */
	public IModel<String> getTitleModel() {
		return titleModel;
	}

	/**
	 * hides this modal window
	 * 
	 * @param target
	 */
	public void hide(AjaxRequestTarget target) {
		target.appendJavaScript("$(\'#" + modal.getMarkupId() + "\').modal(\'hide\');");
	}

	/**
	 * show this modal window
	 * 
	 * @param target
	 */
	public void show(AjaxRequestTarget target) {
		target.add(title);
		target.add(modal);
		target.appendJavaScript("$(\'#" + modal.getMarkupId() + "\').modal();");
		if (isDraggable) {
			target.appendJavaScript("$(\'#" + modal.getMarkupId() + "\').draggable({handle: \".modal-header\"});");
		}
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
	}

	@Override
	public void onEvent(IEvent<?> event) {
		if (event.getPayload() instanceof CEventActionPayload) {
			CEventActionPayload p = (CEventActionPayload) event.getPayload();
			if ("hide".equals(p.getEvent())) {
				// if it is hide
				this.hide(p.getTarget());
				event.dontBroadcastDeeper();
			}

		}
		super.onEvent(event);
	}

	public boolean isDraggable() {
		return isDraggable;
	}

	public void setDraggable(boolean isDraggable) {
		this.isDraggable = isDraggable;
	}
}
