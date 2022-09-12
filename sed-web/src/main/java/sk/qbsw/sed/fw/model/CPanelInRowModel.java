package sk.qbsw.sed.fw.model;

import java.io.Serializable;

import org.apache.wicket.markup.html.WebMarkupContainer;

/**
 * 
 * @author Peter Bozik
 * @version 0.1
 * @since 0.1
 */
public class CPanelInRowModel implements Serializable {
	private static final long serialVersionUID = 1L;
	private final WebMarkupContainer panel;
	private final int width;

	public CPanelInRowModel(WebMarkupContainer panel, int width) {
		super();
		this.panel = panel;
		this.width = width;
	}

	public WebMarkupContainer getPanel() {
		return panel;
	}

	public int getWidth() {
		return width;
	}
}
