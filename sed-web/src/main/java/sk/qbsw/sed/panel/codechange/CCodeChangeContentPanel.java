package sk.qbsw.sed.panel.codechange;

import sk.qbsw.sed.fw.panel.CPanel;

/**
 * SubPage: /cardCodeChange SubPage title: Zmena kódu karty
 * 
 * Panel CodeChangeContentPanel obsahuje panel CodeChangePanel
 */
public class CCodeChangeContentPanel extends CPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String code;

	public CCodeChangeContentPanel(String id, String code) {
		super(id);
		this.code = code;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		CCodeChangePanel tablePanel = new CCodeChangePanel("codeChangePanel", code);
		tablePanel.setOutputMarkupId(true);
		add(tablePanel);
	}
}
