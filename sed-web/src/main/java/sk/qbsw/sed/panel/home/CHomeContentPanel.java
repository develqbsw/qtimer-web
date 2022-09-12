package sk.qbsw.sed.panel.home;

import java.util.List;

import org.apache.wicket.spring.injection.annot.SpringBean;

import sk.qbsw.sed.client.model.tree.org.CViewOrganizationTreeNodeRecord;
import sk.qbsw.sed.communication.service.IOrganizationTreeClientService;
import sk.qbsw.sed.component.CJavascriptResources;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.fw.model.CPageResourceConfiguration;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.panel.stats.CStatsPanel;
import sk.qbsw.sed.web.ui.CSedSession;

/**
 * SubPage: /home SubPage title: Dashboard
 * 
 * Panel obsahuje panely:
 * 
 * - CFeedbackPanel (error panel) - CTimerPanel (Q-Timer) - CTimerButtonsPanel
 * (panel pre tlačidlá) - CUsersPanel (Používatelia) - CStatsPanel (Odpracovaný
 * čas, Rozdelenie projektov) - CRequestsAndMessagesPanel (Žiadosti)
 */
public class CHomeContentPanel extends CPanel {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	/** error panel */
	private CFeedbackPanel errorPanel;

	@SpringBean
	private IOrganizationTreeClientService organizationTreeService;

	/**
	 * create new home content panel for administration
	 */
	public CHomeContentPanel(String id) {
		super(id);

		errorPanel = new CFeedbackPanel("errorPanel");
		registerFeedbackPanel(errorPanel);
		add(errorPanel);

		List<CViewOrganizationTreeNodeRecord> organizationTree = null;

		try {
			organizationTree = organizationTreeService.loadTreeByClient(CSedSession.get().getUser().getClientInfo().getClientId(), Boolean.TRUE);
		} catch (CBussinessDataException e) {
			CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
		}

		CTimerPanel timerPanel = new CTimerPanel("timerPanel", errorPanel);
		CTimerButtonsPanel timerButtonsPanel = new CTimerButtonsPanel("timerButtonsPanel", timerPanel);
		timerPanel.setTimerButtonsPanel(timerButtonsPanel);

		add(timerPanel);
		add(new CUsersPanel("usersPanel", errorPanel, organizationTree));
		add(new CStatsPanel("statsPanel", errorPanel));
		add(new CRequestsAndMessagesPanel("requestsAndMessagesPanel", errorPanel, organizationTree, timerButtonsPanel));
	}

	@Override
	protected void initResources(CPageResourceConfiguration resourceConfiguration) {
		resourceConfiguration.addPluginScript(CJavascriptResources.OWL_CAROUSEL);
		resourceConfiguration.addPluginScript(CJavascriptResources.JQUERY_MOCKJAX);
		resourceConfiguration.addPluginScript(CJavascriptResources.BOOTSTRAP_SELECT);
		resourceConfiguration.addPluginScript(CJavascriptResources.JQUERY_DATATABLES);
		resourceConfiguration.addPluginScript(CJavascriptResources.FULLCALENDAR);

		resourceConfiguration.addPluginScript("assets/plugins/toastr/toastr.js");
		resourceConfiguration.addPluginScript("assets/plugins/bootstrap-switch/dist/js/bootstrap-switch.min.js");
		resourceConfiguration.addPluginScript("assets/plugins/jquery-validation/dist/jquery.validate.min.js");
		resourceConfiguration.addPluginScript("assets/plugins/truncate/jquery.truncate.js");
		resourceConfiguration.addPluginScript("assets/plugins/summernote/dist/summernote.min.js");
		resourceConfiguration.addPluginScript("assets/js/subview.js");
		resourceConfiguration.addPluginScript("assets/js/subview-examples.js");

		resourceConfiguration.addThemeScript("assets/plugins/bootstrap-progressbar/bootstrap-progressbar.min.js");
		resourceConfiguration.addThemeScript("assets/plugins/nvd3/lib/d3.v3.js");
		resourceConfiguration.addThemeScript("assets/plugins/nvd3/nv.d3.min.js");
		resourceConfiguration.addThemeScript("assets/plugins/nvd3/src/models/historicalBar.js");
		resourceConfiguration.addThemeScript("assets/plugins/nvd3/src/models/historicalBarChart.js");
		resourceConfiguration.addThemeScript("assets/plugins/nvd3/src/models/stackedArea.js");
		resourceConfiguration.addThemeScript("assets/plugins/nvd3/src/models/stackedAreaChart.js");
		resourceConfiguration.addThemeScript("assets/plugins/jquery.sparkline/jquery.sparkline.js");
		resourceConfiguration.addThemeScript("assets/plugins/easy-pie-chart/dist/jquery.easypiechart.min.js");
		resourceConfiguration.addThemeScript("assets/js/index.js");

		resourceConfiguration.addThemeScript(CJavascriptResources.SCRIPT_CRP_BEFORE);

		resourceConfiguration.addInitializationCommand("SVExamples.init();");
		resourceConfiguration.addInitializationCommand("Index.init();");
	}
}
