package sk.qbsw.sed.fw.component.form;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.AbstractPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

import sk.qbsw.sed.fw.component.form.behaviour.CHelpTitleBehaviour;
import sk.qbsw.sed.fw.component.form.behaviour.CTextFieldFixerBehaviour;
import sk.qbsw.sed.fw.component.form.behaviour.CValidationMessageBehaviour;
import sk.qbsw.sed.fw.component.form.file.CFileUploadField;
import sk.qbsw.sed.fw.component.validator.CBeanPropertyValidator;

/**
 * @since 1.0.0
 * @version 1.0.0
 * @param <T>
 */
public class CUtilFormComponentVisitor implements IVisitor<FormComponent<?>, Void>, Serializable {

	private static final long serialVersionUID = 1L;
	private final Set<FormComponent<?>> visited = new HashSet<>();
	private final Form<?> form;

	public CUtilFormComponentVisitor(Form<?> form) {
		this.form = form;
	}

	public void bind() {
		this.form.visitFormComponents(this);
	}

	@Override
	public void component(final FormComponent<?> formComponent, final IVisit<Void> visit) {
		if (formComponent.getForm() != this.form) {
			return;
		}

		if ((!(formComponent instanceof Button)) && !this.visited.contains(formComponent)) {
			if (formComponent instanceof CFileUploadField) {
				return;
			}
			this.visited.add(formComponent);

			formComponent.add(new CValidationMessageBehaviour());
			formComponent.add(new CHelpTitleBehaviour());

			if (formComponent instanceof TextField<?>) {
				formComponent.add(new CTextFieldFixerBehaviour());
			}

			// Handle JSR 303: Bean Validation (Hibernate Validator in @Entity annotated classes).
			if (formComponent.getModel() instanceof AbstractPropertyModel<?>) {
				final AbstractPropertyModel<?> model = (AbstractPropertyModel<?>) formComponent.getModel();
				final IModel<?> chainedModel = model.getChainedModel();

				// We add a CBeanPropertyValidator only if the model owns a chained model with a model object != null
				if (chainedModel != null) {
					final Object modelObject = chainedModel.getObject();

					if (modelObject != null) {
						addValidators(formComponent, modelObject.getClass(), model.getPropertyExpression());
					}
				}
			}
		}
	}

	private void addValidators(final FormComponent<?> formComponent, Class<?> cls, String property) {
		formComponent.add(new CBeanPropertyValidator<Object>(cls, property));
	}
}
