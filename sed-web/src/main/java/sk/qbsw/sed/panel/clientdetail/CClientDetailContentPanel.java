package sk.qbsw.sed.panel.clientdetail;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.LoadableDetachableModel;

import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CStringResourceReader;
import sk.qbsw.sed.web.ui.CSedSession;

/**
 * SubPage: /clientDetail SubPage title: Detaily organizácie
 * 
 * Panel ClientDetailContentPanel obsahuje panel CClientDetailPanel - Detail /
 * Editovať
 */
public class CClientDetailContentPanel extends CPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CClientDetailContentPanel(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		LoadableDetachableModel<String> model = new LoadableDetachableModel<String>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected String load() {
				return CStringResourceReader.read("tabTitle.detail");
			}

		};
		Label tabTitle = new Label("tabTitle", model);
		tabTitle.setOutputMarkupId(true);
		add(tabTitle);

		Long clientId = CSedSession.get().getUser().getClientInfo().getClientId();
		CClientDetailPanel detailPanel = new CClientDetailPanel("detailPanel", clientId, tabTitle);
		add(detailPanel);
	}
}
