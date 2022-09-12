package sk.qbsw.sed.fw.component.form.input;

import org.apache.wicket.markup.repeater.RepeatingView;

import sk.qbsw.sed.fw.panel.IUnDisabled;

/**
 * Repeating view used for enabling within the detail panel processing (create,
 * modificate, view mode).
 * 
 * @author Ľubomír Grňo
 *
 */
public class CRepeatingView extends RepeatingView implements IUnDisabled {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CRepeatingView(String id) {
		super(id);
	}
}
