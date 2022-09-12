package sk.qbsw.sed.panel.userdetail;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.LoadableDetachableModel;

import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CStringResourceReader;
import sk.qbsw.sed.web.ui.CSedSession;

/**
 * SubPage: /userDetail SubPage title: Moje detaily
 * 
 * Panel UserDetailContentPanel obsahuje panel UserDetailPanel
 */
public class CUserDetailContentPanel extends CPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CUserDetailContentPanel(String id) {
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

		Long userId = CSedSession.get().getUser().getUserId();
		CUserDetailPanel detailPanel = new CUserDetailPanel("detailPanel", userId);
		add(detailPanel);
	}
}
