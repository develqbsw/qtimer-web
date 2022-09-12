package sk.qbsw.sed.web.ui.components.panel;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class CContentHeaderPanel extends Panel {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param id    wicket id
	 * @param title model with title
	 */
	public CContentHeaderPanel(String id, IModel<String> title) {
		super(id);
		add(new Label("title", title));
	}
}
