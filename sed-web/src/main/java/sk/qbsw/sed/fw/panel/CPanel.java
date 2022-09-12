package sk.qbsw.sed.fw.panel;

import org.apache.wicket.Page;

import sk.qbsw.sed.fw.component.IComponentContainer;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.model.CPageResourceConfiguration;
import sk.qbsw.sed.fw.page.AAuthenticatedPage;

public class CPanel extends ABasePanel implements IComponentContainer {
	private static final long serialVersionUID = 1L;

	private CFeedbackPanel feedbackPanel = null;

	public CPanel(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		initResources();
	}

	private void initResources() {
		Page page = getPage();
		if (page instanceof AAuthenticatedPage) {
			AAuthenticatedPage aaPage = (AAuthenticatedPage) page;
			initResources(aaPage.getResourceConfiguration());
		}
	}

	/**
	 * Register this feedbackPanel as the main feedback for the page and adds the
	 * panel
	 * 
	 * @param feedbackPanel
	 */
	public final void registerFeedbackPanel(CFeedbackPanel feedbackPanel) {
		this.feedbackPanel = feedbackPanel;

	}

	@Override
	public final CFeedbackPanel getFeedbackPanel() {
		return feedbackPanel;
	}

	protected void initResources(CPageResourceConfiguration resourceConfiguration) {
		// do nothing
	}
}
