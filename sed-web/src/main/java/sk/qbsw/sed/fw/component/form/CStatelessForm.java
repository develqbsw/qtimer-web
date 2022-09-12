package sk.qbsw.sed.fw.component.form;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

import sk.qbsw.sed.fw.component.IComponentContainer;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.component.validator.CBeanValidator;

/**
 * 
 * @author Peter Božík
 * @version 1.0.0
 * @since 1.0.0
 *
 * @param <T>
 */
public class CStatelessForm<T> extends StatelessForm<T> implements IComponentContainer {

	private static final long serialVersionUID = 1L;
	public static final String WICKET_ID_FORM_COMPONENTS = "formComponents";
	private final CUtilFormComponentVisitor utilFormComponentVisitor = new CUtilFormComponentVisitor(this);
	private CFeedbackPanel feedbackPanel;
	private boolean refuseSubmitOnEnter = true;

	public CStatelessForm(String id, IModel<T> model) {
		this(id, model, null);
	}

	public CStatelessForm(String id, IModel<T> model, boolean refuseSubmitOnEnter) {
		this(id, model, null);
		this.refuseSubmitOnEnter = refuseSubmitOnEnter;
	}

	public CStatelessForm(String id, IModel<T> model, CFeedbackPanel feedback) {
		super(id, model);
		this.feedbackPanel = feedback;
	}

	public CStatelessForm(String id) {
		super(id);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onInitialize() {
		super.onInitialize();

		if (refuseSubmitOnEnter) {
			// SED-559 zakazanie potvrdenia formulara po stlaceni enter
			visitChildren(FormComponent.class, new IVisitor() {
				@Override
				public void component(Object object, IVisit visit) {
					if (!(object instanceof Button) && !(object instanceof TextArea))
						((Component) object).add(new CDisableEnterKeyBehavior());
				}
			});
		}
	}

	@Override
	protected void onValidateModelObjects() {
		super.onValidateModelObjects();

		final CBeanValidator validator = new CBeanValidator();

		validator.validate(this);
	}

	@Override
	public void onBeforeRender() {
		super.onBeforeRender();

		this.utilFormComponentVisitor.bind();
	}

	@Override
	public CFeedbackPanel getFeedbackPanel() {
		return feedbackPanel;
	}

	/**
	 * Sets the feedbeck and add it to the component
	 * 
	 * @param feedbackPanel
	 */
	public void setFeedbackPanel(CFeedbackPanel feedbackPanel) {
		this.feedbackPanel = feedbackPanel;
	}

	/**
	 * Use with caution! Clears all feedback messages from all components attached
	 * to the form.
	 */
	public void clearFeedbackMessages() {
		visitChildren(Component.class, new IVisitor<Component, Boolean>() {
			@Override
			public void component(final Component component, final IVisit<Boolean> visit) {
				component.getFeedbackMessages().clear();
			}
		});
	}
}
