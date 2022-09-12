package sk.qbsw.sed.fw.component.form.input;

import java.io.Serializable;

/**
 * 
 * @author Peter Bozik
 * @version 1.0.0
 * @since 1.0.0
 *
 */
public class CRequiredTextArea<T extends Serializable> extends CTextArea<T> implements IComponentLabel {
	private static final long serialVersionUID = 1L;

	public CRequiredTextArea(String id) {
		super(id);
		setRequired(true);
	}
}
