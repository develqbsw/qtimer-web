package sk.qbsw.sed.fw.component.validator;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.validation.ConstraintViolation;
import javax.validation.Path.Node;
import javax.validation.Validation;
import javax.validation.Validator;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Peter Božík
 * @version 1.0.0
 * @since 1.0.0
 *
 */
public class CBeanValidator implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(CBeanValidator.class);

	private static final Pattern MESSAGE_TEMPLATE_FIXATING_PATTERN = Pattern.compile("[{}]");

	private final Class<?>[] groups;

	public CBeanValidator(final Class<?>... groups) {
		this.groups = groups;
	}

	public void validate(final Form<?> form) {
		final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

		form.visitChildren(Component.class, new IVisitor<Component, Void>() {
			@Override
			public void component(Component component, IVisit<Void> visit) {
				if ((component instanceof Form<?>) && component.isVisibleInHierarchy()) {
					if (((Form<?>) component).getModel() instanceof CompoundPropertyModel) {
						final Object bean = ((Form<?>) component).getModelObject();
						if (bean != null) {
							final Set<?> violations = validator.validate(bean, CBeanValidator.this.groups);

							for (final Object v : violations) {
								final ConstraintViolation<?> violation = (ConstraintViolation<?>) v;
								final Iterator<Node> iter = violation.getPropertyPath().iterator();

								if (iter.hasNext()) {
									final Map<String, Object> atts = violation.getConstraintDescriptor().getAttributes();
									final String fixedMessageTemplate = CBeanValidator.MESSAGE_TEMPLATE_FIXATING_PATTERN.matcher(violation.getMessageTemplate()).replaceAll("");
									final String message = form.getString(fixedMessageTemplate);

									form.error(message, atts);
									CBeanValidator.LOGGER.error("Violation = " + bean.getClass() + " " + violation.getMessage());
								}
							}
						}
					}
				}
			}
		});
	}
}
