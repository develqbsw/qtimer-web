package sk.qbsw.sed.fw.component.form;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.FormComponent;

public class CDisableEnterKeyBehavior extends Behavior {
	private static final long serialVersionUID = 7123908800158111365L;
	private FormComponent formComp;
	private static final String JS_SUPPRESS_ENTER = "if(event.keyCode==13 || window.event.keyCode==13){return false;}else{return true;}";

	@Override
	public final void bind(Component component) {
		if (component == null)
			throw new IllegalArgumentException("Argument component must be not null");

		if (formComp != null)
			throw new IllegalStateException(
					"This behavior cannot be attached to multiple components; it is already attached to component " + formComp + ", but component " + 
							component + " wants to be	attached too");

		if (!(component instanceof FormComponent))
			throw new IllegalArgumentException("This behavior can only be attached to a FormComponent.");

		formComp = (FormComponent) component;
	}

	@Override
	public void onComponentTag(Component component, ComponentTag tag) {

		if (formComp.isEnabled() && formComp.isEnableAllowed()) {
			tag.put("onkeypress", JS_SUPPRESS_ENTER);
		}
	}
}
