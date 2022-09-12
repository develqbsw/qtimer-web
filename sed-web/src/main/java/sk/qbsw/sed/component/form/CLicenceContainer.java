package sk.qbsw.sed.component.form;

import sk.qbsw.sed.fw.panel.CPanel;

public class CLicenceContainer extends CPanel {

	private static final long serialVersionUID = 1L;

	public CLicenceContainer() {
		super("cLicenceContainer");
		init();
	}

	private void init() {
		this.setOutputMarkupId(true);
	}
}
