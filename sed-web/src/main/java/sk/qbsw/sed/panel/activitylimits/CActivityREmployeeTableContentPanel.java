package sk.qbsw.sed.panel.activitylimits;

import sk.qbsw.sed.fw.panel.CPanel;

/**
 * SubPage: /activityREmployee SubPage title: Limity aktivít - Priradenie
 * 
 * Panel ActivityREmployeeTableContentPanel obsahuje panely:
 * 
 * - ActivityREmployeeAssignPanel (tabuľka) 
 * - ActivityREmployeeTablePanel (tabuľka)
 */
public class CActivityREmployeeTableContentPanel extends CPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CActivityREmployeeTableContentPanel(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		// assign restrictions
		CActivityREmployeeAssignPanel tabPanel = new CActivityREmployeeAssignPanel("activityREmployeeAssign");
		tabPanel.setOutputMarkupId(true);
		add(tabPanel);

		// choose employee
		CActivityREmployeeTablePanel tablePanel = new CActivityREmployeeTablePanel("activityREmployeeTable", tabPanel);
		tablePanel.setOutputMarkupId(true);
		add(tablePanel);
	}
}
