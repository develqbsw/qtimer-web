package sk.qbsw.sed.panel.stats;

import org.apache.wicket.markup.head.IHeaderResponse;

import sk.qbsw.sed.component.stats.CBarChart;
import sk.qbsw.sed.component.stats.CPieChart;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.web.ui.components.CCSSResources;

/**
 * SubPage: /home SubPage title: Dashboard
 * 
 * StatsPanel obsahuje:
 * 
 * - BarChart - Odpracovaný čas - PieChart - Rozdelenie projektov
 */
public class CStatsPanel extends CPanel {

	private static final long serialVersionUID = 1L;

	private CFeedbackPanel errorPanel;

	public CStatsPanel(String id, CFeedbackPanel errorPanel) {
		super(id);
		this.errorPanel = errorPanel;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		final CPanel barChart = new CBarChart("barChart", errorPanel);
		barChart.setOutputMarkupId(true);
		add(barChart);

		final CPanel pieChart = new CPieChart("pieChart", errorPanel);
		pieChart.setOutputMarkupId(true);
		add(pieChart);

		((CBarChart) barChart).setPairedElement(pieChart);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CCSSResources.NVD3);
	}
}
