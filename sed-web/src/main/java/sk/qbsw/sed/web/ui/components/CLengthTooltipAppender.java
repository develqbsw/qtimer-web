package sk.qbsw.sed.web.ui.components;

import java.io.Serializable;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

/**
 * implementation of composite behavior. adds to parent - tooltip that informs
 * about maximal lenght of field. - adds maxlenght attribute to Textfield.
 * 
 * @author Marek Martinkovic
 * @version 2.3.0
 * @since 2.3.0
 */
public class CLengthTooltipAppender extends CCompositeBehavior {
	private static final long serialVersionUID = 1L;

	public CLengthTooltipAppender(int len) {
		super();

		// class="m-wrap large tooltips" data-placement="right"
		// data-original-title="Tooltip in right"
		addBehaviour(new AttributeAppender("class", " tooltips "));
		addBehaviour(new AttributeAppender("data-placement", "right"));
		// message with one parameter.
		StringResourceModel tooltipMessageModel = new StringResourceModel("property.TooltipMax", new Model<Serializable>(), len);
		addBehaviour(new AttributeModifier("data-original-title", tooltipMessageModel));
		addBehaviour(new AttributeModifier("maxlength", len));
	}

	@Override
	public void renderHead(Component component, IHeaderResponse response) {
		super.renderHead(component, response);
	}
}
