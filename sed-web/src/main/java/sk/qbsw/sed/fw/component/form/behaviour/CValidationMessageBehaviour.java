package sk.qbsw.sed.fw.component.form.behaviour;

import java.io.Serializable;
import java.util.Iterator;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.FeedbackMessages;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.request.Response;

/**
 * Metronic theme requires "type" attribute in input element. CSS has rules
 * like: input.m-wrap[type="text"] {...}
 * 
 * @author Peter Bozik
 * @since 1.0.0
 * @version 1.0.0
 */
public class CValidationMessageBehaviour extends Behavior {

	private static final long serialVersionUID = 1L;

	@Override
	public void afterRender(Component component) {
		super.afterRender(component);

		if (component instanceof FormComponent<?>) {
			if (component.hasFeedbackMessage()) {
				final Response response = component.getResponse();
				final FeedbackMessages msgs = component.getFeedbackMessages();
				final Iterator<FeedbackMessage> iter = msgs.iterator();
				final StringBuilder sb = new StringBuilder(200);
				final StringBuilder sbMessage = new StringBuilder(100);
				int maxLevel = 0;

				while (iter.hasNext()) {
					final FeedbackMessage msg = iter.next();

					maxLevel = Math.max(maxLevel, msg.getLevel());

					sbMessage.append(msg.getMessage());

					if (iter.hasNext()) {
						sbMessage.append("\r\n");
					}
				}

				final Classes classes = this.getClassesByLevel(maxLevel);

				sb.append("<span class=\"").append(classes.spanClass).append(" help-block\">");
				sb.append(sbMessage);
				sb.append("</span>");

				response.write(sb);
			}
		}
	}

	protected Classes getClassesByLevel(int maxLevel) {
		final Classes result;

		switch (maxLevel) {
		case FeedbackMessage.ERROR:
		case FeedbackMessage.FATAL:
			result = new Classes("input-error", "icon-exclamation-sign");
			break;

		case FeedbackMessage.WARNING:
			result = new Classes("input-warning", "icon-warning-sign");
			break;

		case FeedbackMessage.UNDEFINED:
		case FeedbackMessage.DEBUG:
		case FeedbackMessage.INFO:
			result = new Classes("input-info", "icon-info-sign");
			break;

		case FeedbackMessage.SUCCESS:
			result = new Classes("input-success", "icon-ok");
			break;

		default:
			result = new Classes("input-error", "icon-exclamation-sign");
			break;
		}

		return result;
	}

	protected static class Classes implements Serializable {

		private static final long serialVersionUID = 1L;
		protected final String spanClass;
		protected final String iconClass;

		public Classes(String spanClass, String iconClass) {
			this.spanClass = spanClass;
			this.iconClass = iconClass;
		}
	}
}
