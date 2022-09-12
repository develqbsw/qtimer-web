package sk.qbsw.sed.panel.timesheet.editable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import sk.qbsw.sed.client.model.ISubordinateBrwFilterCriteria;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.tree.org.CViewOrganizationTreeNodeRecord;
import sk.qbsw.sed.client.ui.component.panel.menu.IUserTypeCode;
import sk.qbsw.sed.communication.service.IOrganizationTreeClientService;
import sk.qbsw.sed.communication.service.IUserClientService;
import sk.qbsw.sed.component.stats.CMultiBarChart;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CStringResourceReader;
import sk.qbsw.sed.panel.requests.CRequestTablePanel;
import sk.qbsw.sed.web.ui.CSedSession;
import sk.qbsw.sed.web.ui.components.panel.CEmployeesTreeModalPanel;
import sk.qbsw.sed.web.ui.components.panel.CModalBorder;

public class CChooseEmployeesForRequestsPanel extends CPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private CModalBorder employeesModal;

	private WebMarkupContainer employeesModalPanel;

	@SpringBean
	private IUserClientService userService;

	@SpringBean
	private IOrganizationTreeClientService organizationTreeService;

	private CFeedbackPanel feedbackPanel = null;

	private enum SelectOptions {
		CHOOSE_EMPLOYEES, ALL_EMPLOYEES, ME, ALL_SUBORDINATE, ALL_SUBORDINATE_WITHOUT_ME, SHOW_TEAM
	}

	private Boolean onlyValid = Boolean.TRUE;

	public CChooseEmployeesForRequestsPanel(String id, final ISubordinateBrwFilterCriteria filter, final Panel panelToRefresh, final Label pageTitleSmall) {
		super(id);
		init(filter, panelToRefresh, pageTitleSmall);
	}

	private void init(final ISubordinateBrwFilterCriteria filter, final Panel panelToRefresh, final Label pageTitleSmall) {
		setOutputMarkupId(true);

		this.add(new AttributeAppender("title", getString("common.button.chooseEmployees")));

		final Set<Long> emplyees = new HashSet<>();
		final Set<Long> emplyeesAlsoInvalid = new HashSet<>();

		boolean withSub = CSedSession.get().getUser().containsRole(IUserTypeCode.ID_EMPLOYEE_WITH_SUB);
		if (withSub || CSedSession.get().getUser().containsRole(IUserTypeCode.ID_ORG_ADMIN)) {
			try {
				for (CCodeListRecord user : userService.listSubordinateUsers(Boolean.TRUE, Boolean.TRUE)) {
					emplyees.add(user.getId());
				}

				if (!onlyValid) {
					for (CCodeListRecord user : userService.listSubordinateUsers(Boolean.FALSE, Boolean.TRUE)) {
						emplyeesAlsoInvalid.add(user.getId());
					}
				}
			} catch (CBussinessDataException e) {
				CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
			}
		}

		setOnlyMeToFiler(filter, pageTitleSmall);

		employeesModal = new CModalBorder("employeesModalWindow");
		employeesModal.setOutputMarkupId(true);
		employeesModal.getTitleModel().setObject(CStringResourceReader.read("chooseEmployees.chooseEmployees"));
		employeesModalPanel = new CEmployeesTreeModalPanel("employeesContent", employeesModal, filter, panelToRefresh, pageTitleSmall, getPageTitleSmallString(SelectOptions.CHOOSE_EMPLOYEES));
		employeesModalPanel.setOutputMarkupId(true);
		employeesModal.add(employeesModalPanel);
		add(employeesModal);

		final WebMarkupContainer manageEmployees = new WebMarkupContainer("manageEmployees");
		manageEmployees.setOutputMarkupId(true);
		add(manageEmployees);

		AjaxFallbackLink<Object> chooseEmployees = new AjaxFallbackLink<Object>("chooseEmployees") {
			/** serial uid */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				boolean admin = CSedSession.get().getUser().containsRole(IUserTypeCode.ID_ORG_ADMIN);

				((CEmployeesTreeModalPanel) employeesModalPanel).initProvider(onlyValid, admin ? true : false);
				((CEmployeesTreeModalPanel) employeesModalPanel).updateProviderSubset();
				target.add(employeesModalPanel);
				employeesModal.show(target);
				target.add(manageEmployees);
				if (feedbackPanel != null) {
					target.add(feedbackPanel);
				}
			}

			@Override
			public boolean isVisible() {
				// Ak employee nema podriadenych toto tlacidlo mu vobec nezobrazime
				return CSedSession.get().getUser().containsRole(IUserTypeCode.ID_EMPLOYEE_WITH_SUB) || CSedSession.get().getUser().containsRole(IUserTypeCode.ID_ORG_ADMIN);
			}
		};
		manageEmployees.add(chooseEmployees);

		AjaxFallbackLink<Object> chooseSubordinateEmployees = new AjaxFallbackLink<Object>("chooseSubordinateEmployees") {
			/** serial uid */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				filter.setEmplyees(new HashSet<Long>(emplyees));
				filter.getEmplyees().remove(CSedSession.get().getUser().getUserId());

				pageTitleSmall.setDefaultModelObject(getPageTitleSmallString(SelectOptions.ALL_SUBORDINATE));

				target.add(pageTitleSmall);

				refresh(panelToRefresh, target);

				target.add(manageEmployees);
				if (feedbackPanel != null) {
					target.add(feedbackPanel);
				}
			}

			@Override
			public boolean isVisible() {
				// Ak employee nema podriadenych toto tlacidlo mu vobec nezobrazime
				return CSedSession.get().getUser().containsRole(IUserTypeCode.ID_EMPLOYEE_WITH_SUB);
			}
		};
		manageEmployees.add(chooseSubordinateEmployees);

		AjaxFallbackLink<Object> showEmployees = new AjaxFallbackLink<Object>("showEmployees") {
			/** serial uid */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				((CEmployeesTreeModalPanel) employeesModalPanel).initProvider(onlyValid, true);
				((CEmployeesTreeModalPanel) employeesModalPanel).updateProviderSubset();
				target.add(employeesModalPanel);
				employeesModal.show(target);
				target.add(manageEmployees);
				if (feedbackPanel != null) {
					target.add(feedbackPanel);
				}
			}

			@Override
			public boolean isVisible() {
				// nezobrazovať pre administrátora
				return !CSedSession.get().getUser().containsRole(IUserTypeCode.ID_ORG_ADMIN);
			}
		};
		manageEmployees.add(showEmployees);

		AjaxFallbackLink<Object> showTeam = new AjaxFallbackLink<Object>("showTeam") {
			/** serial uid */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				filter.setEmplyees(getTeam());
				pageTitleSmall.setDefaultModelObject(getPageTitleSmallString(SelectOptions.SHOW_TEAM));

				target.add(pageTitleSmall);

				refresh(panelToRefresh, target);

				target.add(manageEmployees);
				if (feedbackPanel != null) {
					target.add(feedbackPanel);
				}
			}

			@Override
			public boolean isVisible() {
				// nezobrazovať pre administrátora a nezobrazovať pre nadriadeného
				boolean withoutSub = CSedSession.get().getUser().containsRole(IUserTypeCode.ID_EMPLOYEE_WITHOUT_SUB);
				return !CSedSession.get().getUser().containsRole(IUserTypeCode.ID_ORG_ADMIN) && withoutSub;
			}
		};
		manageEmployees.add(showTeam);

		AjaxFallbackLink<Object> chooseAllEmployeesInvalid = new AjaxFallbackLink<Object>("chooseAllEmployeesInvalid") {
			/** serial uid */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				filter.setEmplyees(new HashSet<Long>(emplyeesAlsoInvalid));
				pageTitleSmall.setDefaultModelObject(getPageTitleSmallString(SelectOptions.ALL_EMPLOYEES));

				target.add(pageTitleSmall);

				refresh(panelToRefresh, target);

				target.add(manageEmployees);
				if (feedbackPanel != null) {
					target.add(feedbackPanel);
				}
			}

			@Override
			public boolean isVisible() {
				// toto chceme zobrazit len ak mam pod sebou nejakeho neplatneho zamestnanca
				return !onlyValid && emplyees.size() != emplyeesAlsoInvalid.size();
			}
		};
		manageEmployees.add(chooseAllEmployeesInvalid);

		AjaxFallbackLink<Object> chooseOnlyMe = new AjaxFallbackLink<Object>("chooseOnlyMe") {
			/** serial uid */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				setOnlyMeToFiler(filter, pageTitleSmall);

				target.add(pageTitleSmall);

				refresh(panelToRefresh, target);

				target.add(manageEmployees);
				if (feedbackPanel != null) {
					target.add(feedbackPanel);
				}
			}

			@Override
			public boolean isVisible() {
				// u admina toto tlacidlo nema vyznam
				return !CSedSession.get().getUser().containsRole(IUserTypeCode.ID_ORG_ADMIN);
			}
		};
		manageEmployees.add(chooseOnlyMe);

		AjaxFallbackLink<Object> showAllEmployees = new AjaxFallbackLink<Object>("showAllEmployees") {
			/** serial uid */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				final Set<Long> allEmplyees = new HashSet<>();

				try {
					for (CCodeListRecord user : userService.getAllValidEmployees()) {
						allEmplyees.add(user.getId());
					}
				} catch (CBussinessDataException e) {
					CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
				}

				filter.setEmplyees(allEmplyees);
				pageTitleSmall.setDefaultModelObject(getPageTitleSmallString(SelectOptions.ALL_EMPLOYEES));

				target.add(pageTitleSmall);

				refresh(panelToRefresh, target);

				target.add(manageEmployees);
				if (feedbackPanel != null) {
					target.add(feedbackPanel);
				}
			}
		};
		manageEmployees.add(showAllEmployees);
	}

	private void setOnlyMeToFiler(final ISubordinateBrwFilterCriteria filter, Label pageTitleSmall) {
		Set<Long> emplyees = new HashSet<>();
		filter.setEmplyees(emplyees);
		if (IUserTypeCode.EMPLOYEE.equals(CSedSession.get().getUser().getRoleCode())) {
			emplyees.add(CSedSession.get().getUser().getUserId());
			pageTitleSmall.setDefaultModelObject(getPageTitleSmallString(SelectOptions.ME));
		}
	}

	private String getPageTitleSmallString(SelectOptions selected) {

		String pageTitleSmallString;

		pageTitleSmallString = getString("requests.label.title.small");

		switch (selected) {
		case CHOOSE_EMPLOYEES:
			break;

		case ME:
			pageTitleSmallString += " " + CSedSession.get().getLoggedUserName();
			break;

		case ALL_SUBORDINATE:
			pageTitleSmallString += " " + getString("chooseEmployees.allSubordinateEmployees");
			break;

		case SHOW_TEAM:
			pageTitleSmallString += " " + getString("chooseEmployees.team");
			break;

		case ALL_EMPLOYEES:
			pageTitleSmallString += " " + getString("chooseEmployees.allEmployees");
			break;
		}

		return pageTitleSmallString;
	}

	private void refresh(final Panel panelToRefresh, AjaxRequestTarget target) {
		if (panelToRefresh instanceof CMultiBarChart) {
			((CMultiBarChart) panelToRefresh).refresh(target, true);
		} else if (panelToRefresh instanceof CRequestTablePanel) {
			if (((CRequestTablePanel) panelToRefresh).isGraphVisible()) {
				((CRequestTablePanel) panelToRefresh).updateGraph(target, true);
			} else {
				target.add(((CRequestTablePanel) panelToRefresh).getTable());
			}
		} else {
			target.add(panelToRefresh);
		}
	}

	/**
	 * vrati clenov timu
	 * 
	 * @return
	 */
	private Set<Long> getTeam() {
		List<CViewOrganizationTreeNodeRecord> organizationTree = null;

		try {
			organizationTree = organizationTreeService.loadTreeByClient(CSedSession.get().getUser().getClientInfo().getClientId(), Boolean.TRUE);
		} catch (CBussinessDataException e) {
			CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
		}

		Long userId = null;
		Long parentId = null;

		// najdem si parenta
		for (CViewOrganizationTreeNodeRecord treeUser : organizationTree) {
			if (CSedSession.get().getUser().getUserId().equals(treeUser.getUserId())) {
				userId = treeUser.getId();
				parentId = treeUser.getParentId();
				break;
			}
		}

		boolean searchSupervisor = parentId != null;
		Set<Long> team = new HashSet<>();
		boolean withSub = CSedSession.get().getUser().containsRole(IUserTypeCode.ID_EMPLOYEE_WITH_SUB);

		for (CViewOrganizationTreeNodeRecord user : organizationTree) {
			for (CViewOrganizationTreeNodeRecord treeUser : organizationTree) {
				if (user.getUserId().equals(treeUser.getUserId())) {

					if (withSub) {
						if (isUnder(userId, treeUser.getParentId(), organizationTree) || CSedSession.get().getUser().getUserId().equals(user.getId())) {
							team.add(user.getUserId());
							break;
						}
					} else {
						// nema podriadenych
						if (searchSupervisor && parentId.equals(treeUser.getId())) {
							team.add(user.getUserId()); // pridaj nadriadeneho
							searchSupervisor = false;
							break;
						} else if (parentId != null && parentId.equals(treeUser.getParentId())) {
							team.add(user.getUserId()); // pridaj podriadenych nadriadeneho
							break;
						}
					}
				}
			}
		}
		return team;
	}

	/**
	 * urcuje ci v oranizacnej strukture je userId pod supervisorId
	 * 
	 * @param supervisorId
	 * @param userId
	 * @param organizationTree
	 * @return
	 */
	private boolean isUnder(Long supervisorId, Long userId, List<CViewOrganizationTreeNodeRecord> organizationTree) {

		if (userId == null) {
			return false;
		}

		if (supervisorId.equals(userId)) {
			return true;
		}

		for (CViewOrganizationTreeNodeRecord treeUser : organizationTree) {

			if (userId.equals(treeUser.getId())) {
				return isUnder(supervisorId, treeUser.getParentId(), organizationTree);
			}
		}
		return false;
	}
}
