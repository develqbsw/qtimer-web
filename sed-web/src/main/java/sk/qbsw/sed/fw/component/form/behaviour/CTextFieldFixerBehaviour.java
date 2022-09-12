package sk.qbsw.sed.fw.component.form.behaviour;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.TextField;

import sk.qbsw.sed.fw.component.EDataType;
import sk.qbsw.sed.fw.component.form.input.CTextField;

/**
 * Metronic theme requires "type" attribute in input element. CSS has rules
 * like: input.m-wrap[type="text"] {...}
 * 
 * @author Peter Bozik
 * @since 1.0.0
 * @version 1.0.0
 */
public class CTextFieldFixerBehaviour extends Behavior {

	private static final long serialVersionUID = 1L;

	@Override
	public void onComponentTag(Component component, ComponentTag tag) {
		super.onComponentTag(component, tag);

		// Must check, if attribute is not present. EmailTextField will provide type="email" attribute (must not overwrite!).
		if ((component instanceof TextField<?>) && (!tag.getAttributes().containsKey("type"))) {
			tag.put("type", "text");
		}
		
		if ((component instanceof CTextField<?>)) {
			CTextField<?> cmp = (CTextField<?>) component;
			if (EDataType.DATE.equals(cmp.getEDataType()) && (!tag.getAttributes().containsKey("data-date-format"))) {
				tag.put("data-date-format", "dd.mm.yyyy");
			}
			if (EDataType.DATE_RANGE.equals(cmp.getEDataType()) && (!tag.getAttributes().containsKey("data-date-format"))) {
				tag.put("data-date-format", "dd.mm.yyyy");
			}
			if (EDataType.DATE_TIME.equals(cmp.getEDataType()) && (!tag.getAttributes().containsKey("data-date-format"))) {
				tag.put("data-date-format", "dd.mm.yyyy hh:ii");
			}
			if (EDataType.DATE_TIME_FILTER_DATE.equals(cmp.getEDataType()) && (!tag.getAttributes().containsKey("data-date-format"))) {
				tag.put("data-date-format", "dd.mm.yyyy");
			}

			if (EDataType.DATE.equals(cmp.getEDataType()) || EDataType.DATE_RANGE.equals(cmp.getEDataType()) || EDataType.DATE_TIME.equals(cmp.getEDataType())
					|| EDataType.DATE_TIME_FILTER_DATE.equals(cmp.getEDataType())) {
				if ((!tag.getAttributes().containsKey("data-date-language"))) {

					tag.put("data-date-language", "sk");
				}
				if ((!tag.getAttributes().containsKey("data-date-weekstart"))) {
					tag.put("data-date-week-start", "1");
				}
			}
		}
	}
}
