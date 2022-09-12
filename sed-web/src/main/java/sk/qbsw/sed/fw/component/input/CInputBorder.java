package sk.qbsw.sed.fw.component.input;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.FormComponentLabel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;

import sk.qbsw.sed.fw.component.form.input.IComponentLabel;
import sk.qbsw.sed.fw.panel.IUnDisabled;

/**
 * Problems with ajax: use only {@link CInputBorder#setAjaxified()},
 * {@link CInputBorder#changeVisibility(AjaxRequestTarget, boolean)} and
 * {@link CInputBorder#addToTarget(AjaxRequestTarget)}
 * 
 * @author Peter Bozik
 * @version 1.0.0
 * @since 1.0.0
 *
 */
public class CInputBorder extends Border {
	private static final long serialVersionUID = 1L;
	protected final Component component;
	protected final Component[] components;
	private boolean addRequiredLabel = false;
	private final String labelId;
	public static final String PREFIX = "label.";
	private WebMarkupContainer container;
	private boolean ajaxified = false;
	private Boolean hasBeenShown = false;
	private int labelWidth = 1;
	private int componentContainerWidth = 5;
	private boolean visibilityBeforeLoad = true;
	private WebMarkupContainer requiredLabel;
	private boolean visible = false;
	private IModel<String> labelModel;

	public CInputBorder(String id, Component component, Component... components) {
		super(id);
		this.component = component;
		this.components = components;
		this.labelId = PREFIX + this.component.getId();
		initLabel(component);
		setOutputMarkupId(true);
	}

	public CInputBorder(String id, Component component, boolean visible, Component... components) {
		this(id, component, components);
		this.setAjaxified();
		this.changeVisibility(null, visible);
	}

	public CInputBorder(String id, int labelWidth, int componentContainerWidth, Component component, Component... components) {
		this(id, component, components);
		this.labelWidth = labelWidth;
		this.componentContainerWidth = componentContainerWidth;
	}

	public CInputBorder(String id, String labelId, Component component, Component... components) {
		super(id);
		this.component = component;
		this.components = components;
		this.labelId = labelId;
		initLabel(component);
		setOutputMarkupId(true);
	}

	public CInputBorder(String id, IModel<String> labelModel, Component component, Component... components) {
		super(id);
		this.component = component;
		this.components = components;
		this.labelId = null;
		this.labelModel = labelModel;
		initLabel(component);
		setOutputMarkupId(true);
	}

	public CInputBorder(String id, String labelId, int labelWidth, int componentContainerWidth, Component component, Component... components) {
		this(id, labelId, component, components);
		this.labelWidth = labelWidth;
		this.componentContainerWidth = componentContainerWidth;
	}

	public CInputBorder(Component component) {
		this(component.getId(), component);
	}

	private void initLabel(Component component) {
		if (component instanceof IComponentLabel) {
			if (labelModel != null) {
				((IComponentLabel) component).setComponentLabelKey(this.labelModel);
			} else {
				((IComponentLabel) component).setComponentLabelKey(this.labelId);
			}
		}
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		container = new WebMarkupContainer("container");

		final WebMarkupContainer formComponentLabel;
		final Label labelMessage;
		final WebMarkupContainer componentContainer = new WebMarkupContainer("componentContainer");

		if (this.component instanceof FormComponent<?>) {
			formComponentLabel = new FormComponentLabel("label", (FormComponent<?>) this.component);
			labelMessage = new Label("labelMessage", getLabelModel());
		} else {
			formComponentLabel = new WebMarkupContainer("label");
			labelMessage = new Label("labelMessage", getLabelModel());
		}

		formComponentLabel.add(AttributeAppender.append("class", " col-lg-" + labelWidth + " "));
		componentContainer.add(AttributeAppender.append("class", " col-lg-" + componentContainerWidth + " "));

		this.add(this.component);

		formComponentLabel.add(labelMessage);

		requiredLabel = new WebMarkupContainer("requiredMessage") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return isRequired();

			}
		};
		requiredLabel.setOutputMarkupId(true);
		formComponentLabel.add(requiredLabel);
		if (!isRequired()) {
			requiredLabel.setVisible(false);
		}

		container.add(formComponentLabel);
		container.add(componentContainer);
		componentContainer.add(getBodyContainer());
		this.addToBorder(container);

		resolveAjaxified();

		for (final Component c : this.components) {
			this.add(c);
		}
		setVisibilityAllowed(true);
		if (!visibilityBeforeLoad) {
			setVisible(false);
		} else {
			visible = true;
		}
	}

	private IModel<String> getLabelModel() {
		if (labelModel == null) {
			return new StringResourceModel(labelId, this.component, null);
		} else {
			return labelModel;
		}
	}

	private boolean isRequired() {
		boolean result = getAddRequiredLabel();
		if (component instanceof FormComponent<?>) {
			if (!result && ((FormComponent) component).isRequired()) {
				result = true;
			}
		}
		return result;
	}

	public boolean getAddRequiredLabel() {
		return this.addRequiredLabel;
	}

	public void setAjaxified() {
		ajaxified = true;
		if (container != null) {
			resolveAjaxified();
		}
	}

	private void resolveAjaxified() {
		if (ajaxified) {
			container.setOutputMarkupId(true);
			container.setOutputMarkupPlaceholderTag(true);
			component.setOutputMarkupId(true);
			component.setOutputMarkupPlaceholderTag(true);
			requiredLabel.setOutputMarkupId(true);
			setOutputMarkupId(true);
			setOutputMarkupPlaceholderTag(true);
		}
	}

	/**
	 * There is a problem with hiding a border after it has been rendered and added
	 * to a page
	 * 
	 * @param target
	 */
	public void changeVisibility(AjaxRequestTarget target, boolean visible) {
		this.visible = visible;
		if (target != null) {
			if (hasBeenShown) {
				if (visible) {
					target.appendJavaScript("showElement('" + container.getMarkupId() + "');");
				} else {
					target.appendJavaScript("hideElement('" + container.getMarkupId() + "');");
				}
			} else {
				setVisible(visible);
				if (visible) {
					hasBeenShown = true;
				}
				target.add(this);
			}

		} else {
			visibilityBeforeLoad = visible;

		}

	}

	@Override
	public boolean isVisible() {
		return visible || hasBeenShown;
	}

	public void changeRequired(AjaxRequestTarget target, boolean required) {
		if (requiredLabel != null) {
			if (target != null) {
				target.add(requiredLabel);
			}
			requiredLabel.setVisible(isRequired());
		}
		setAddRequiredLabel(required);

	}

	public void setAddRequiredLabel(boolean addRequiredLabel) {
		this.addRequiredLabel = addRequiredLabel;
	}

	public boolean canBeDisabled() {
		if (component instanceof IUnDisabled) {
			return false;
		}
		return true;
	}

	private WebMarkupContainer getAjaxContainer() {
		return container;
	}

	@Override
	protected void onAfterRender() {
		super.onAfterRender();
		if (isVisible()) {
			hasBeenShown = true;
		}
	}

	@Override
	protected void onBeforeRender() {
		super.onBeforeRender();
		hasBeenShown = false;
		if (isVisible()) {
			hasBeenShown = true;
		}
	}

	public void addToTarget(AjaxRequestTarget target) {
		if (hasBeenShown) {
			target.add(getAjaxContainer());
		} else {
			target.add(this);
		}
	}
}
