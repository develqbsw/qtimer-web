package sk.qbsw.sed.component.behaviour;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;

public class CPlaceholderBehaviour extends Behavior {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String placeholder;

	public CPlaceholderBehaviour(String placeholder) {
		super();
		this.placeholder = placeholder;
	}

	@Override
	public void onComponentTag(Component component, ComponentTag tag) {
		tag.put("placeholder", this.placeholder);
		super.onComponentTag(component, tag);
	}
}
