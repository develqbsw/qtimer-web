package sk.qbsw.sed.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import sk.qbsw.sed.fw.navigation.AMenu;
import sk.qbsw.sed.fw.navigation.CSideMenuItemModel;
import sk.qbsw.sed.fw.page.AAuthenticatedPage;
import sk.qbsw.sed.page.CAbsenceApprovalPage;
import sk.qbsw.sed.page.CJiraTokenGenerationPage;
import sk.qbsw.sed.page.CNotifyOfApprovedRequestPage;
import sk.qbsw.sed.page.CPinGenerationPage;
import sk.qbsw.sed.page.CRequestPage;
import sk.qbsw.sed.page.CUploadPage;
import sk.qbsw.sed.page.activities.CActivityPage;
import sk.qbsw.sed.page.activitylimits.CActivityREmployeePage;
import sk.qbsw.sed.page.activitylimits.CActivityRGroupPage;
import sk.qbsw.sed.page.activitylimits.CActivityRestrictionPage;
import sk.qbsw.sed.page.appinfo.CAppInfoPage;
import sk.qbsw.sed.page.clientdetail.CClientDetailPage;
import sk.qbsw.sed.page.codechange.CCardCodeChangePage;
import sk.qbsw.sed.page.codechange.CPinCodeChangePage;
import sk.qbsw.sed.page.holidays.CHolidayPage;
import sk.qbsw.sed.page.home.CHomePage;
import sk.qbsw.sed.page.home.CStatsPage;
import sk.qbsw.sed.page.myactivities.CUserActivityPage;
import sk.qbsw.sed.page.myprojects.CUserProjectPage;
import sk.qbsw.sed.page.passwordchange.CPasswordChangePage;
import sk.qbsw.sed.page.passwordchange.CPasswordChangeReceptionPage;
import sk.qbsw.sed.page.presenceemail.CPresenceEmailPage;
import sk.qbsw.sed.page.projects.CProjectPage;
import sk.qbsw.sed.page.requestreasons.CRequestReasonPage;
import sk.qbsw.sed.page.timesheet.CTimesheetPage;
import sk.qbsw.sed.page.timestampGenerate.CTimestampGeneratePage;
import sk.qbsw.sed.page.userdetail.CUserDetailPage;
import sk.qbsw.sed.page.users.COrgStructureTreePage;

/**
 * 
 * @author Peter Božík
 * @version 1.0.0
 * @since 1.0.0
 *
 */
public class CMenu extends AMenu {

	private CMenu() {
		super();
	}

	@Override
	protected void onInitialize(List<CSideMenuItemModel> topMenus, Set<Class<? extends AAuthenticatedPage>> notMenuPages) {
		CSideMenuItemModel dash = new CSideMenuItemModel("page.home.label.title", CHomePage.class, true, new ArrayList<CSideMenuItemModel>());
		CSideMenuItemModel timesheet = new CSideMenuItemModel("page.workreport.label.title", "", CTimesheetPage.class, dash, new ArrayList<CSideMenuItemModel>());
		CSideMenuItemModel users = new CSideMenuItemModel("page." + COrgStructureTreePage.KEY + ".label.title", "", COrgStructureTreePage.class, dash, new ArrayList<CSideMenuItemModel>());
		CSideMenuItemModel myProjects = new CSideMenuItemModel("page.userprojects.label.title", "", CUserProjectPage.class, dash, new ArrayList<CSideMenuItemModel>());
		CSideMenuItemModel myActivities = new CSideMenuItemModel("page.useractivities.label.title", "", CUserActivityPage.class, dash, new ArrayList<CSideMenuItemModel>());
		CSideMenuItemModel absenceApproval = new CSideMenuItemModel("page.absenceApproval.label.title", "", CAbsenceApprovalPage.class, dash, new ArrayList<CSideMenuItemModel>());
		CSideMenuItemModel requests = new CSideMenuItemModel("page.requests.label.title", "", CRequestPage.class, dash, new ArrayList<CSideMenuItemModel>());
		CSideMenuItemModel dialsList = new CSideMenuItemModel("menu.item.dialsList.title", "", createDialsList(), dash);
		CSideMenuItemModel orgSettingsList = new CSideMenuItemModel("menu.item.orgSettingsList.title", "", createOrgSettingsList(), dash);
		CSideMenuItemModel timestampGenerate = new CSideMenuItemModel("page.timestampGenerate.label.title", "", CTimestampGeneratePage.class, dash, new ArrayList<CSideMenuItemModel>());
		CSideMenuItemModel stats = new CSideMenuItemModel("page.stats.label.title", "", CStatsPage.class, dash, new ArrayList<CSideMenuItemModel>());

		topMenus.add(dash);
		topMenus.add(timesheet);
		topMenus.add(requests);
		topMenus.add(users);
		topMenus.add(myProjects);
		topMenus.add(myActivities);
		topMenus.add(dialsList);
		topMenus.add(orgSettingsList);
		topMenus.add(absenceApproval);
		topMenus.add(timestampGenerate);
		topMenus.add(stats);

		notMenuPages.add(CPasswordChangePage.class);
		notMenuPages.add(CUserDetailPage.class);
		notMenuPages.add(CClientDetailPage.class);
		notMenuPages.add(CAppInfoPage.class);
		notMenuPages.add(CPinGenerationPage.class);
		notMenuPages.add(CJiraTokenGenerationPage.class);

	}

	private List<CSideMenuItemModel> createDialsList() {
		List<CSideMenuItemModel> list = new ArrayList<>();

		list.add(new CSideMenuItemModel("page.upload.label.title", "", CUploadPage.class));
		list.add(new CSideMenuItemModel("page.projects.label.title", "", CProjectPage.class));
		list.add(new CSideMenuItemModel("page.activities.label.title", "", CActivityPage.class));
		list.add(new CSideMenuItemModel("page.holidays.label.title", "", CHolidayPage.class));
		list.add(new CSideMenuItemModel("page.requestReasons.label.title", "", CRequestReasonPage.class));
		return list;
	}

	private List<CSideMenuItemModel> createOrgSettingsList() {
		List<CSideMenuItemModel> list = new ArrayList<>();

		list.add(new CSideMenuItemModel("page.clientDetail.label.title", "", CClientDetailPage.class));
		list.add(new CSideMenuItemModel("page.passwordChangeReception.label.title", "", CPasswordChangeReceptionPage.class));
		list.add(new CSideMenuItemModel("page.pinCodeChange.label.title", "", CPinCodeChangePage.class));
		list.add(new CSideMenuItemModel("page.cardCodeChange.label.title", "", CCardCodeChangePage.class));
		list.add(new CSideMenuItemModel("page.presenceEmail.label.title", "", CPresenceEmailPage.class));
		list.add(new CSideMenuItemModel("page.notifyOfApprovedRequest.label.title", "", CNotifyOfApprovedRequestPage.class));
		list.add(new CSideMenuItemModel("page.activityRGroups.label.title", "", CActivityRGroupPage.class));
		list.add(new CSideMenuItemModel("page.activityRInterval.label.title", "", CActivityRestrictionPage.class));
		list.add(new CSideMenuItemModel("page.activityREmployee.label.title", "", CActivityREmployeePage.class));
		return list;
	}

	public static void create() {
		AMenu.setInstance(new CMenu());
	}
}
