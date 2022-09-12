package sk.qbsw.sed.panel.stats;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;

import sk.qbsw.sed.component.CJavascriptResources;
import sk.qbsw.sed.component.stats.CMultiBarChart;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.model.CPageResourceConfiguration;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.web.ui.components.CCSSResources;

/**
 * SubPage: /stats SubPage title: Štatistiky
 * 
 * Panel MultiStatsPanel obsahuje MultiBarChart
 */
public class CMultiStatsPanel extends CPanel {

	private static final long serialVersionUID = 1L;

	private Label pageTitleSmall;

	public CMultiStatsPanel(String id, final Label pageTitleSmall) {
		super(id);
		this.pageTitleSmall = pageTitleSmall;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		CFeedbackPanel errorPanel = new CFeedbackPanel("errorPanel");
		registerFeedbackPanel(errorPanel);
		add(errorPanel);

		final CPanel barChart = new CMultiBarChart("barChart", errorPanel, pageTitleSmall);
		barChart.setOutputMarkupId(true);
		add(barChart);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CCSSResources.NVD3);
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
		resourceConfiguration.addThemeScript("assets/plugins/nvd3/nv.d3.js");
		resourceConfiguration.addThemeScript("assets/plugins/nvd3/src/models/historicalBar.js");
		resourceConfiguration.addThemeScript("assets/plugins/nvd3/src/models/historicalBarChart.js");
		resourceConfiguration.addThemeScript("assets/plugins/nvd3/src/models/stackedArea.js");
		resourceConfiguration.addThemeScript("assets/plugins/nvd3/src/models/stackedAreaChart.js");
		resourceConfiguration.addThemeScript("assets/plugins/jquery.sparkline/jquery.sparkline.js");
		resourceConfiguration.addThemeScript("assets/plugins/easy-pie-chart/dist/jquery.easypiechart.min.js");
		resourceConfiguration.addThemeScript("assets/js/index.js");

		resourceConfiguration.addInitializationCommand("SVExamples.init();");
		resourceConfiguration.addInitializationCommand("Index.init();");
	}
}
