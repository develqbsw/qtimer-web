package sk.qbsw.sed.panel.timestampGenerate;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import sk.qbsw.sed.client.model.IListBoxValueTypes;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.communication.service.IActivityClientService;
import sk.qbsw.sed.communication.service.IProjectClientService;
import sk.qbsw.sed.component.CJavascriptResources;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.fw.model.CPageResourceConfiguration;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CStringResourceReader;
import sk.qbsw.sed.panel.timestampGenerate.editable.CTimestampGenerateEditableTablePanel;
import sk.qbsw.sed.panel.timestampGenerate.tab.CTimestampGenerateTabPanel;

/**
 * SubPage: /timestampGenerate SubPage title: Generovanie výkazu práce
 * 
 * Panel TimestampGenerateContentPanel obsahuje panely:
 * 
 * - TimestampGenerateEditableTablePanel - Prehľady 
 * - TimestampGenerateTabPanel - Pridať
 */
public class CTimestampGenerateContentPanel extends CPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Label pageTitleSmall;

	@SpringBean
	private IActivityClientService activityService;

	@SpringBean
	private IProjectClientService projectService;

	private CTimestampGenerateTabPanel tabPanel;

	private CTimestampGenerateEditableTablePanel tablePanel;

	public CTimestampGenerateContentPanel(String id, Label pageTitleSmall) {
		super(id);
		this.pageTitleSmall = pageTitleSmall;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		List<CCodeListRecord> activityList = null;
		List<CCodeListRecord> projectList = null;

		try {
			activityList = getWorkingActivities(activityService.getValidRecordsForUser(getuser().getUserId()));
			projectList = projectService.getValidRecordsForUserCached(getuser().getUserId());
		} catch (CBussinessDataException e) {
			CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
		}

		LoadableDetachableModel<String> model = new LoadableDetachableModel<String>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected String load() {
				return CStringResourceReader.read("tabTitle.new");
			}

		};
		Label tabTitle = new Label("tabTitle", model);
		tabTitle.setOutputMarkupId(true);
		add(tabTitle);

		tablePanel = new CTimestampGenerateEditableTablePanel("timestampGenerateTable", tabTitle, pageTitleSmall, activityList, projectList);
		tablePanel.setOutputMarkupId(true);
		add(tablePanel);

		tabPanel = new CTimestampGenerateTabPanel("timestampGenerateTab", tablePanel, tabTitle, activityList, projectList);
		tabPanel.setModalPanelEditToRefreshAfterSubmit(tablePanel.getModalPanelEdit());
		tabPanel.setOutputMarkupId(true);
		add(tabPanel);

		tablePanel.setTabPanel(tabPanel);
	}

	@Override
	protected void initResources(CPageResourceConfiguration resourceConfiguration) {
		resourceConfiguration.addThemeScript(CJavascriptResources.SCRIPT_CRP_BEFORE);
	}

	/**
	 * 
	 * From the list of all activities returns list of only working activities
	 */
	private List<CCodeListRecord> getWorkingActivities(List<CCodeListRecord> activityList) {
		List<CCodeListRecord> working = new ArrayList<>();

		for (CCodeListRecord activity : activityList) {
			if (!IListBoxValueTypes.NON_WORKING.equals(activity.getType()))
				working.add(activity);
		}

		return working;
	}
}
