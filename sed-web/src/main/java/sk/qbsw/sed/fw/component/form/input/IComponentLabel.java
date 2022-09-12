package sk.qbsw.sed.fw.component.form.input;

import org.apache.wicket.model.IModel;

/**
 * this interface allows to access the label property value
 * 
 * @author Peter Bozik
 * @version 1.0.0
 * @since 1.0.0
 *
 */
public interface IComponentLabel {
	public String getComponentLabel();

	public void setComponentLabelKey(String labelId);

	public void setComponentLabelKey(IModel<String> model);
}
