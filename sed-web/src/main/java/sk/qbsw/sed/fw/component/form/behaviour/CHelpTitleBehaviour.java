package sk.qbsw.sed.fw.component.form.behaviour;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;

import sk.qbsw.sed.fw.CFrameworkConfiguration;

/**
 * generates popover when component has property with postfix .title
 * 
 * @author Peter Božík
 * @version 1.0.0
 * @since 1.0.0
 *
 */
public class CHelpTitleBehaviour extends Behavior {

	private static final long serialVersionUID = 1L;

	@Override
	public void onComponentTag(Component component, ComponentTag tag) {
		super.onComponentTag(component, tag);

		final String title = this.getTitle(component);

		if (title != null) {
			tag.put("placeholder", title);

			tag.append("class", "popovers", CFrameworkConfiguration.HTML_CLASS_ATTRIBUTE_SEPARATOR);
			tag.append("data-trigger", "hover", CFrameworkConfiguration.HTML_CLASS_ATTRIBUTE_SEPARATOR);
			tag.append("data-content", title, CFrameworkConfiguration.HTML_CLASS_ATTRIBUTE_SEPARATOR);
		}
	}

	private String getTitle(Component component) {
		final String title = component.getString(component.getId() + ".title", null, "");
		return StringUtils.defaultIfEmpty(title, null);
	}
}
