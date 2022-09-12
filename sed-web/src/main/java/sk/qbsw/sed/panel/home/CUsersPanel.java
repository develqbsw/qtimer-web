package sk.qbsw.sed.panel.home;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import sk.qbsw.sed.client.model.CEmployeesStatusNew;
import sk.qbsw.sed.client.model.IStatusConstants;
import sk.qbsw.sed.client.model.tree.org.CViewOrganizationTreeNodeRecord;
import sk.qbsw.sed.client.ui.component.panel.menu.IUserTypeCode;
import sk.qbsw.sed.communication.service.IBrwEmployeesStatusClientService;
import sk.qbsw.sed.communication.service.IUserClientService;
import sk.qbsw.sed.component.behaviour.CPlaceholderBehaviour;
import sk.qbsw.sed.framework.security.exception.CSecurityException;
import sk.qbsw.sed.fw.component.CImage;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.component.form.CDisableEnterKeyBehavior;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CStringResourceReader;
import sk.qbsw.sed.fw.utils.CTextUtils;
import sk.qbsw.sed.model.CSystemSettings;
import sk.qbsw.sed.web.ui.CSedSession;

/**
 * SubPage: /home SubPage title: Dashboard
 * 
 * Panel UsersPanel - Používatelia - Na pracovisku, Tím, Všetci
 */
public class CUsersPanel extends CPanel {
	/** serial uid */
	private static final long serialVersionUID = 1L;
	
	private static final String CLASS = "class";

	private static final String ACTIVE = "active";
	
	private static final String ONLINE = "ONLINE";
	
	private static final String STYLE = "style";
	
	private static final String DISPLAY_NONE = "display: none;";
	
	private static final String STATUS_TEXT = "statusText";
	
	@SpringBean
	private IBrwEmployeesStatusClientService employeesStatusService;

	@SpringBean
	private IUserClientService userService;

	@SpringBean
	private CSystemSettings settings;

	private List<CViewOrganizationTreeNodeRecord> organizationTree;

	private WebMarkupContainer favoriteContainer;
	private ListView<CEmployeesStatusNew> employeesListFavorite;
	private WebMarkupContainer favoriteTab;

	/**
	 * create new users panel
	 * 
	 * @throws CSecurityException
	 */
	public CUsersPanel(String id, CFeedbackPanel errorPanel, List<CViewOrganizationTreeNodeRecord> organizationTree) {
		super(id);
		setOutputMarkupId(true);
		registerFeedbackPanel(errorPanel);
		this.organizationTree = organizationTree;

	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		load();
	}

	private void load() {
		WebMarkupContainer onlineTab = new WebMarkupContainer("onlineTab");
		onlineTab.setOutputMarkupId(true);
		add(onlineTab);

		favoriteTab = new WebMarkupContainer("favoriteTab") {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return !getuser().getUserFavourites().isEmpty();
			}
		};
		favoriteTab.setOutputMarkupPlaceholderTag(true);
		add(favoriteTab);

		WebMarkupContainer onlineTabContent = new WebMarkupContainer("onlineTabContent");
		onlineTabContent.setOutputMarkupId(true);
		add(onlineTabContent);

		WebMarkupContainer favoriteTabContent = new WebMarkupContainer("favoriteTabContent");
		favoriteTabContent.setOutputMarkupId(true);
		add(favoriteTabContent);

		if (getuser().getUserFavourites().isEmpty()) {
			onlineTab.add(AttributeModifier.append(CLASS, ACTIVE));
			onlineTabContent.add(AttributeModifier.append(CLASS, ACTIVE));
		} else {
			favoriteTab.add(AttributeModifier.append(CLASS, ACTIVE));
			favoriteTabContent.add(AttributeModifier.append(CLASS, ACTIVE));
		}

		List<CEmployeesStatusNew> employees = null;
		List<CEmployeesStatusNew> team = null;
		List<CEmployeesStatusNew> favorite = null;
		try {
			// načítam si všetkých používateľov aktuálneho klienta a tmpday == aktuálny z
			// v_employees_status
			employees = employeesStatusService.fetch();
			Collections.sort(employees, new ComparatorByAlphabetical());
			// podľa používateľa si vyberiem jeho tím (aj nadriadeného)
			team = getTeam(employees);
			// vyberiem si používateľov ktorých má používateľ označených ako obľúbených
			favorite = getFavorite(employees);
		} catch (CBussinessDataException e) {
			CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
		}

		final ListView<CEmployeesStatusNew> employeesListAll = new ListViewOfEmployees("userListAll", employees, "ALL"); // vytvorím zoznam všetkých používateľov
		final ListView<CEmployeesStatusNew> employeesListOnline = new ListViewOfEmployees("userListOnline", employees, ONLINE); // vytvorím zoznam používateľov, ktorí sú online
		final ListView<CEmployeesStatusNew> employeesListTeam = new ListViewOfEmployees("userListTeam", team, "TEAM"); // vytvorím zoznam používateľov v tíme
		employeesListFavorite = new ListViewOfEmployees("userListFavorite", favorite, "FAVORITE"); // vytvorím zoznam obľúbených používateľov

		final WebMarkupContainer onlineContainer = new WebMarkupContainer("onlineContainer");
		onlineContainer.setOutputMarkupId(true);
		final WebMarkupContainer allContainer = new WebMarkupContainer("allContainer");
		allContainer.setOutputMarkupId(true);
		final WebMarkupContainer teamContainer = new WebMarkupContainer("teamContainer");
		teamContainer.setOutputMarkupId(true);
		favoriteContainer = new WebMarkupContainer("favoriteContainer");
		favoriteContainer.setOutputMarkupId(true);

		onlineContainer.add(employeesListOnline);
		allContainer.add(employeesListAll);
		teamContainer.add(employeesListTeam);
		favoriteContainer.add(employeesListFavorite);

		onlineTabContent.add(onlineContainer);
		add(allContainer);
		add(teamContainer);
		favoriteTabContent.add(favoriteContainer);

		String wsUrl = settings.getWsUrl();
		add(new HiddenField<String>("webSocketAddress", Model.of(wsUrl + "/usersPanelWebSocket")));

		final TextField<String> search = new TextField<>("search", new Model<String>());
		search.add(new AjaxFormComponentUpdatingBehavior("onkeyup") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget target) {
				String searchedText = search.getInput();
				List<CEmployeesStatusNew> employees;
				List<CEmployeesStatusNew> team;
				List<CEmployeesStatusNew> favorite;
				try {
					employees = employeesStatusService.fetch();
					Collections.sort(employees, new ComparatorByAlphabetical());
					team = getTeam(employees);
					favorite = getFavorite(employees);

					if ("".equals(searchedText)) {
						employeesListOnline.setModelObject(employees);
						employeesListAll.setModelObject(employees);
						employeesListTeam.setModelObject(team);
						employeesListFavorite.setModelObject(favorite);
					} else {
						List<CEmployeesStatusNew> foundFromAllUsers = searchEmployees(employees, searchedText);
						List<CEmployeesStatusNew> foundFromTeamUsers = searchEmployees(team, searchedText);
						List<CEmployeesStatusNew> foundFromFavoriteUsers = searchEmployees(favorite, searchedText);

						employeesListOnline.setModelObject(foundFromAllUsers);
						employeesListAll.setModelObject(foundFromAllUsers);
						employeesListTeam.setModelObject(foundFromTeamUsers);
						employeesListFavorite.setModelObject(foundFromFavoriteUsers);

						employeesListOnline.removeAll();
						employeesListTeam.removeAll();
						employeesListAll.removeAll();
						employeesListFavorite.removeAll();
					}

					target.add(onlineContainer);
					target.add(allContainer);
					target.add(teamContainer);
					target.add(favoriteContainer);
					target.appendJavaScript("Main.runPanelScroll();");

				} catch (CBussinessDataException e) {
					CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
				}

			}
		});
		add(search);
		search.add(new CPlaceholderBehaviour(getString("searchbar.placeholder")));
		search.add(new CDisableEnterKeyBehavior());

	}

	private List<CEmployeesStatusNew> getFavorite(List<CEmployeesStatusNew> allUsers) throws CBussinessDataException {
		List<CEmployeesStatusNew> favorite = new ArrayList<>();

		if (!getuser().getUserFavourites().isEmpty()) {

			for (CEmployeesStatusNew user : allUsers) {

				if (getuser().getUserFavourites().contains(user.getId())) {
					favorite.add(user);
				}
			}
		}

		return favorite;
	}

	private List<CEmployeesStatusNew> getTeam(List<CEmployeesStatusNew> allUsers) throws CBussinessDataException {
		Long userId = null;
		Long parentId = null;

		for (CViewOrganizationTreeNodeRecord treeUser : organizationTree) {
			if (CSedSession.get().getUser().getUserId().equals(treeUser.getUserId())) {
				userId = treeUser.getId();
				parentId = treeUser.getParentId();
				break;
			}
		}

		boolean searchSupervisor = parentId != null;
		List<CEmployeesStatusNew> team = new ArrayList<>();
		boolean withSub = CSedSession.get().getUser().containsRole(IUserTypeCode.ID_EMPLOYEE_WITH_SUB);

		for (CEmployeesStatusNew user : allUsers) {
			for (CViewOrganizationTreeNodeRecord treeUser : organizationTree) {
				if (user.getId().equals(treeUser.getUserId())) {

					if (withSub) {
						if (isUnder(userId, treeUser.getParentId()) || CSedSession.get().getUser().getUserId().equals(user.getId())) {
							team.add(user);
							break;
						}
					} else {
						// nema podriadenych
						if (searchSupervisor && parentId.equals(treeUser.getId())) {
							team.add(user); // pridaj nadriadeneho
							searchSupervisor = false;
							break;
						} else if (parentId != null && parentId.equals(treeUser.getParentId())) {
							team.add(user); // pridaj podriadenych nadriadeneho
							break;
						}
					}
				}
			}
		}
		return team;
	}

	private boolean isUnder(Long supervisorId, Long userId) {

		if (userId == null) {
			return false;
		}

		if (supervisorId.equals(userId)) {
			return true;
		}

		for (CViewOrganizationTreeNodeRecord treeUser : organizationTree) {

			if (userId.equals(treeUser.getId())) {
				return isUnder(supervisorId, treeUser.getParentId());
			}
		}
		return false;
	}

	private class ListViewOfEmployees extends ListView<CEmployeesStatusNew> {

		private static final long serialVersionUID = 1L;

		private String tabId;

		private ListViewOfEmployees(String id, List<CEmployeesStatusNew> employees, String tabId) {
			super(id, employees);
			this.tabId = tabId;
		}

		@Override
		protected void populateItem(ListItem<CEmployeesStatusNew> item) {

			final CEmployeesStatusNew user = (CEmployeesStatusNew) item.getModelObject();

			item.add(AttributeModifier.append("id", tabId + "_" + user.getId()));

			// v tabe "Na pracovisku" zobrazujem len určitých používateľov, ak nemá status IN_WORK (V práci) alebo WORK_BREAK (Prestávka) tak ho skryjem
			if (ONLINE.equals(tabId) && !IStatusConstants.IN_WORK.equals(user.getStatus()) && !IStatusConstants.WORK_BREAK.equals(user.getStatus())) {
				item.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
			}

			// v tabe "Na pracovisku" zobrazujem len určitých používateľov, ak má status MEETING (Mimo pracoviska) tak ho skryjem
			if (ONLINE.equals(tabId) && IStatusConstants.MEETING.equals(user.getStatus())) {
				item.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
			}

			// v tabe "Na pracovisku" zobrazujem len určitých používateľov, ak má status HOME_OFFICE (Práca z domu) tak ho skryjem
			if (ONLINE.equals(tabId) && IStatusConstants.HOME_OFFICE.equals(user.getStatus())) {
				item.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
			}

			item.add(new CImage("icon", user.getPhotoId()));
			item.add(new Label("email", "<a href=\"mailto:" + user.getEmail() + "\" class=\"text-light\">" + user.getEmail() + "</a>").setEscapeModelStrings(false));
			item.add(new Label("name", user.getSurname() + " " + user.getName()));

			if ("ALL".equals(tabId)) {
				String star = "fa-star-o";
				if (getuser().getUserFavourites() != null && getuser().getUserFavourites().contains(user.getId())) {
					star = "fa-star";
				}

				AjaxFallbackLink<Object> favourite = new AjaxFallbackLink<Object>("favourite") {

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(AjaxRequestTarget target) {

						try {
							List<CEmployeesStatusNew> employees = employeesStatusService.fetch();

							for (CEmployeesStatusNew employee : employees) {
								if (employee.getId().equals(user.getId())) {
									user.setStatus(employee.getStatus());
									break;
								}
							}

							if (getuser().getUserFavourites().contains(user.getId())) {
								userService.modifyMyFavorites(user.getId(), false, getuser().getUserId());
								setBody(Model.of("<i class='fa fa-star-o'></i>"));
								getuser().getUserFavourites().remove(user.getId());
								employeesListFavorite.getModelObject().remove(user);
							} else {
								userService.modifyMyFavorites(user.getId(), true, getuser().getUserId());
								setBody(Model.of("<i class='fa fa-star'></i>"));
								getuser().getUserFavourites().add(user.getId());
								employeesListFavorite.getModelObject().add(user);
								Collections.sort(employeesListFavorite.getModelObject(), new ComparatorByAlphabetical());
							}

							favoriteTab.add(AttributeModifier.remove(CLASS));

							target.add(favoriteTab);
							target.add(favoriteContainer);
							target.add(this);
							target.appendJavaScript("Main.runPanelScroll();");

						} catch (CBussinessDataException e) {
							CBussinessDataExceptionProcessor.process(e, getSession(), target, getPage());
						}
					}
				};
				favourite.setBody(Model.of("<i class='fa " + star + "'></i>"));
				favourite.setOutputMarkupId(true);
				favourite.setEscapeModelStrings(false);
				item.add(favourite);
			}

			WebMarkupContainer officeDiv = new WebMarkupContainer("officeDiv");
			officeDiv.add(new Label("office", user.getOfficeNumber()));
			if (StringUtils.isEmpty(user.getOfficeNumber())) {
				officeDiv.add(new AttributeModifier(STYLE, "display:none"));
			}
			item.add(officeDiv);
			WebMarkupContainer phoneDiv = new WebMarkupContainer("phoneDiv");
			phoneDiv.add(new Label("phone", user.getPhone()));
			if (StringUtils.isEmpty(user.getPhone())) {
				phoneDiv.add(new AttributeModifier(STYLE, "display:none"));
			}
			item.add(phoneDiv);

			WebMarkupContainer statusNotInWork = new WebMarkupContainer("statusNotInWorkDiv");
			statusNotInWork.add(new Label(STATUS_TEXT, CStringResourceReader.read("label.userStatus.notInWork")));
			statusNotInWork.add(new AttributeModifier(CLASS, "text-red"));
			statusNotInWork.add(AttributeModifier.append("id", tabId + "_STATUS_NOT_IN_WORK_" + user.getId()));
			item.add(statusNotInWork);

			WebMarkupContainer statusMeeting = new WebMarkupContainer("statusMeetingDiv");
			statusMeeting.add(new Label(STATUS_TEXT, CStringResourceReader.read("label.userStatus.meeting")));
			statusMeeting.add(new AttributeModifier(CLASS, "text-azure"));
			statusMeeting.add(AttributeModifier.append("id", tabId + "_STATUS_MEETING_" + user.getId()));
			item.add(statusMeeting);

			WebMarkupContainer statusInWork = new WebMarkupContainer("statusInWorkDiv");
			statusInWork.add(new Label(STATUS_TEXT, CStringResourceReader.read("label.userStatus.inWork")));
			statusInWork.add(new AttributeModifier(CLASS, "text-green"));
			statusInWork.add(AttributeModifier.append("id", tabId + "_STATUS_IN_WORK_" + user.getId()));
			item.add(statusInWork);

			WebMarkupContainer statusOutOfWork = new WebMarkupContainer("statusOutOfWorkDiv");
			statusOutOfWork.add(new Label(STATUS_TEXT, CStringResourceReader.read("label.userStatus.outOfWork")));
			statusOutOfWork.add(new AttributeModifier(CLASS, "text-blue"));
			statusOutOfWork.add(AttributeModifier.append("id", tabId + "_STATUS_OUT_OF_WORK_" + user.getId()));
			item.add(statusOutOfWork);

			WebMarkupContainer statusWorkBreak = new WebMarkupContainer("statusWorkBreakDiv");
			statusWorkBreak.add(new Label(STATUS_TEXT, CStringResourceReader.read("label.userStatus.break")));
			statusWorkBreak.add(new AttributeModifier(CLASS, "text-yellow"));
			statusWorkBreak.add(AttributeModifier.append("id", tabId + "_STATUS_WORK_BREAK_" + user.getId()));
			item.add(statusWorkBreak);

			WebMarkupContainer statusHoliday = new WebMarkupContainer("statusHolidayDiv");
			statusHoliday.add(new Label(STATUS_TEXT, CStringResourceReader.read("label.userStatus.holiday")));
			statusHoliday.add(new AttributeModifier(CLASS, "text-blue"));
			statusHoliday.add(AttributeModifier.append("id", tabId + "_STATUS_HOLIDAY_" + user.getId()));
			item.add(statusHoliday);

			WebMarkupContainer statusHomeOffice = new WebMarkupContainer("statusHomeOfficeDiv");
			statusHomeOffice.add(new Label(STATUS_TEXT, CStringResourceReader.read("label.userStatus.homeOffice")));
			statusHomeOffice.add(new AttributeModifier(CLASS, "text-azure"));
			statusHomeOffice.add(AttributeModifier.append("id", tabId + "_STATUS_HOME_OFFICE_" + user.getId()));
			item.add(statusHomeOffice);

			switch (user.getStatus()) {
			case IStatusConstants.HOLIDAY:
				statusNotInWork.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				statusMeeting.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				statusInWork.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				statusOutOfWork.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				statusWorkBreak.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				statusHomeOffice.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				break;
			case IStatusConstants.NOT_IN_WORK:
				statusMeeting.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				statusInWork.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				statusOutOfWork.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				statusWorkBreak.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				statusHoliday.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				statusHomeOffice.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				break;
			case IStatusConstants.MEETING:
				statusNotInWork.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				statusInWork.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				statusOutOfWork.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				statusWorkBreak.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				statusHoliday.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				statusHomeOffice.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				break;
			case IStatusConstants.HOME_OFFICE:
				statusNotInWork.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				statusMeeting.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				statusInWork.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				statusOutOfWork.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				statusHoliday.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				statusWorkBreak.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				break;
			case IStatusConstants.IN_WORK:
				statusNotInWork.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				statusMeeting.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				statusOutOfWork.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				statusWorkBreak.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				statusHoliday.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				statusHomeOffice.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				break;
			case IStatusConstants.OUT_OF_WORK:
				statusNotInWork.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				statusMeeting.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				statusInWork.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				statusWorkBreak.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				statusHoliday.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				statusHomeOffice.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				break;
			case IStatusConstants.WORK_BREAK:
				statusNotInWork.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				statusMeeting.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				statusInWork.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				statusOutOfWork.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				statusHoliday.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				statusHomeOffice.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				break;
			default:
				// ak by nahodou tam doslo nieco ine skovame vsetko
				statusNotInWork.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				statusMeeting.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				statusInWork.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				statusWorkBreak.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				statusOutOfWork.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				statusHoliday.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				statusHomeOffice.add(AttributeModifier.append(STYLE, DISPLAY_NONE));
				break;
			}
		}
	}

	private List<CEmployeesStatusNew> searchEmployees(final List<CEmployeesStatusNew> employees, String searchedText) {
		List<CEmployeesStatusNew> newList = new ArrayList<>();

		for (CEmployeesStatusNew e : employees) {
			if (CTextUtils.contains(e.getName(), searchedText) || CTextUtils.contains(e.getSurname(), searchedText) || 
					(e.getOfficeNumber() != null && CTextUtils.contains(e.getOfficeNumber(), searchedText))) {
				newList.add(e);
			}
		}
		return newList;
	}

	private class ComparatorByAlphabetical implements Comparator<CEmployeesStatusNew> {

		private final Locale locale = new Locale("sk");
		private Collator collator = Collator.getInstance(locale);

		@Override
		public int compare(CEmployeesStatusNew o1, CEmployeesStatusNew o2) {
			int surname = collator.compare(o1.getSurname(), o2.getSurname());

			if (surname != 0) {
				return surname;
			}

			return collator.compare(o1.getName(), o2.getName());
		}
	}
}
