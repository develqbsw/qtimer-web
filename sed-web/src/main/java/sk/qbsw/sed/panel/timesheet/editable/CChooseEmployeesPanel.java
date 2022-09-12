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
import sk.qbsw.sed.client.model.timestamp.CSubrodinateTimeStampBrwFilterCriteria;
import sk.qbsw.sed.client.model.tree.org.CViewOrganizationTreeNodeRecord;
import sk.qbsw.sed.client.ui.component.panel.menu.IUserTypeCode;
import sk.qbsw.sed.communication.service.IUserClientService;
import sk.qbsw.sed.component.stats.CMultiBarChart;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CStringResourceReader;
import sk.qbsw.sed.panel.requests.CRequestTablePanel;
import sk.qbsw.sed.web.grid.CSedDataGrid;
import sk.qbsw.sed.web.ui.CSedSession;
import sk.qbsw.sed.web.ui.components.panel.CEmployeesTreeModalPanel;
import sk.qbsw.sed.web.ui.components.panel.CModalBorder;

public class CChooseEmployeesPanel extends CPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String EMPLOYEE = "employee";

	private List<CViewOrganizationTreeNodeRecord> listNotifiedEmployees;

	private List<CViewOrganizationTreeNodeRecord> listNotifiedEmployeesWithoutSubordinates;

	private boolean hasNotified;

	private boolean hasSubordiantes;

	private CModalBorder employeesModal;

	private WebMarkupContainer employeesModalPanel;

	private CTimesheetEditableTablePanel cTimesheetEditableTablePanel;

	@SpringBean
	private IUserClientService userService;

	private CFeedbackPanel feedbackPanel = null;

	public enum SupportedPageProperties {
		VYKAZ_PRACE, ZIADOSTI, STATS
	}

	private enum SelectOptions {
		CHOOSE_EMPLOYEES, ALL_SUBORDINATE, ALL_EMPLOYEES, ME
	}

	private SupportedPageProperties spp;

	private Boolean onlyValid = Boolean.TRUE;

	public CChooseEmployeesPanel(String id, final ISubordinateBrwFilterCriteria filter, final Panel panelToRefresh, final Label pageTitleSmall, SupportedPageProperties spp) {
		super(id);
		init(filter, panelToRefresh, pageTitleSmall, spp);
	}

	public CChooseEmployeesPanel(String id, final ISubordinateBrwFilterCriteria filter, final Panel panelToRefresh, final Label pageTitleSmall, SupportedPageProperties spp, CFeedbackPanel feedback,
			CTimesheetEditableTablePanel cTimesheetEditableTablePanel) {
		super(id);
		this.feedbackPanel = feedback;
		this.cTimesheetEditableTablePanel = cTimesheetEditableTablePanel;
		init(filter, panelToRefresh, pageTitleSmall, spp);
	}

	private void init(final ISubordinateBrwFilterCriteria filter, final Panel panelToRefresh, final Label pageTitleSmall, SupportedPageProperties spp) {
		setOutputMarkupId(true);

		this.add(new AttributeAppender("title", getString("common.button.chooseEmployees")));

		this.spp = spp;
		if (SupportedPageProperties.STATS.equals(spp)) {
			onlyValid = Boolean.FALSE;
		}

		final Set<Long> emplyees = new HashSet<>();
		final Set<Long> emplyeesAlsoInvalid = new HashSet<>();
		final Set<Long> notifiedEmployees = new HashSet<>();

		try {
			this.listNotifiedEmployees = userService.listNotifiedUsers(Boolean.TRUE);
			for (CViewOrganizationTreeNodeRecord user : listNotifiedEmployees) {
				notifiedEmployees.add(user.getId());
			}
		} catch (CBussinessDataException e) {
			CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
		}

		final Set<Long> notifiedEmployeesWithoutSubordiantes = new HashSet<>();
		try {
			this.listNotifiedEmployeesWithoutSubordinates = userService.listNotifiedUsers(Boolean.FALSE);
			for (CViewOrganizationTreeNodeRecord user : listNotifiedEmployeesWithoutSubordinates) {
				notifiedEmployeesWithoutSubordiantes.add(user.getUserId());
			}

			this.hasNotified = notifiedEmployeesWithoutSubordiantes.size() > 0;

			List<CCodeListRecord> subordinates = userService.listSubordinateUsers(Boolean.TRUE, Boolean.FALSE);
			this.hasSubordiantes = subordinates.size() > 0;

			// odstránim si zo zoznamu notifikovaných seba a svojich podriadených
			if (!notifiedEmployeesWithoutSubordiantes.isEmpty()) {

				List<CCodeListRecord> subordinatesAndMe = userService.listSubordinateUsers(Boolean.TRUE, Boolean.TRUE);

				// odstránim podriadených aj seba
				for (CCodeListRecord user : subordinatesAndMe) {
					notifiedEmployeesWithoutSubordiantes.remove(user.getId());
				}
			}

		} catch (CBussinessDataException e) {
			CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
		}

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

		if (SupportedPageProperties.STATS.equals(spp)) {
			if (withSub) {
				filter.setEmplyees(new HashSet<Long>(emplyeesAlsoInvalid));
				pageTitleSmall.setDefaultModelObject(getPageTitleSmallString(SelectOptions.ALL_SUBORDINATE));
			} else {
				setOnlyMeToFiler(filter, pageTitleSmall);
			}
		} else {
			setOnlyMeToFiler(filter, pageTitleSmall);
		}

		employeesModal = new CModalBorder("employeesModalWindow");
		employeesModal.setOutputMarkupId(true);
		employeesModal.getTitleModel().setObject(CStringResourceReader.read("timesheet.chooseEmployees"));
		if (CSedSession.get().getUser().containsRole(IUserTypeCode.ID_EMPLOYEE_WITH_SUB) || CSedSession.get().getUser().containsRole(IUserTypeCode.ID_ORG_ADMIN)
				|| !this.listNotifiedEmployees.isEmpty()) {
			employeesModalPanel = new CEmployeesTreeModalPanel("employeesContent", employeesModal, filter, panelToRefresh, pageTitleSmall, getPageTitleSmallString(SelectOptions.CHOOSE_EMPLOYEES));
		} else {
			// Ak employee nema podriadenych tento panel nepotrebuje
			employeesModalPanel = new WebMarkupContainer("employeesContent");
		}
		employeesModalPanel.setOutputMarkupId(true);
		employeesModal.add(employeesModalPanel);
		add(employeesModal);

		final WebMarkupContainer manageEmployees = new WebMarkupContainer("manageEmployees") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				// Ak employee nema podriadenych toto tlacidlo mu vobec nezobrazime
				return CSedSession.get().getUser().containsRole(IUserTypeCode.ID_EMPLOYEE_WITH_SUB) || CSedSession.get().getUser().containsRole(IUserTypeCode.ID_ORG_ADMIN) || hasNotified;
			}
		};
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
				if (cTimesheetEditableTablePanel != null) {
					cTimesheetEditableTablePanel.setEditingActions(false);
					cTimesheetEditableTablePanel.setFromOtherEmployeess(false);
					cTimesheetEditableTablePanel.getTable().setFirstPage();
					cTimesheetEditableTablePanel.setShowMassEditButton(false);
					target.add(cTimesheetEditableTablePanel);
					target.add(cTimesheetEditableTablePanel.getcTimesheetConfirmButtonPanel());
				}
			}

			@Override
			public boolean isVisible() {
				return hasSubordiantes;
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

				if (filter instanceof CSubrodinateTimeStampBrwFilterCriteria) {
					// pre vykaz prace chcem dynamicky zobrazovat/skryvat stlpec zamestnanec
					if (filter.getEmplyees().size() <= 1) {
						// skryjem stlpec zamestnanec
						((CSedDataGrid) panelToRefresh).showColumn(EMPLOYEE, false, target);
					} else {
						// zobrazim stlpec zamestnanec
						((CSedDataGrid) panelToRefresh).showColumn(EMPLOYEE, true, target);
					}
				}

				target.add(pageTitleSmall);

				refresh(panelToRefresh, target);

				target.add(manageEmployees);
				if (feedbackPanel != null) {
					target.add(feedbackPanel);
				}

				if (cTimesheetEditableTablePanel != null) {
					cTimesheetEditableTablePanel.setEditingActions(false);
					cTimesheetEditableTablePanel.setFromOtherEmployeess(false);
					cTimesheetEditableTablePanel.getTable().setFirstPage();
					cTimesheetEditableTablePanel.setShowMassEditButton(false);
					target.add(cTimesheetEditableTablePanel);
					target.add(cTimesheetEditableTablePanel.getcTimesheetConfirmButtonPanel());
				}
			}

			@Override
			public boolean isVisible() {
				// u admina toto tlacidlo nema vyznam alebo ak nema ziadnych podriadenych
				return !CSedSession.get().getUser().containsRole(IUserTypeCode.ID_ORG_ADMIN) && !emplyees.isEmpty();
			}
		};
		manageEmployees.add(chooseSubordinateEmployees);

		AjaxFallbackLink<Object> chooseAllEmployees = new AjaxFallbackLink<Object>("chooseAllEmployees") {
			/** serial uid */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				filter.setEmplyees(new HashSet<Long>(emplyees));
				pageTitleSmall.setDefaultModelObject(getPageTitleSmallString(SelectOptions.ALL_EMPLOYEES));

				if (filter instanceof CSubrodinateTimeStampBrwFilterCriteria) {
					// pre vykaz prace chcem dynamicky zobrazovat/skryvat stlpec zamestnanec

					if (filter.getEmplyees().size() <= 1) {
						// skryjem stlpec zamestnanec
						((CSedDataGrid) panelToRefresh).showColumn(EMPLOYEE, false, target);
					} else {
						// zobrazim stlpec zamestnanec
						((CSedDataGrid) panelToRefresh).showColumn(EMPLOYEE, true, target);
					}
				}

				target.add(pageTitleSmall);

				refresh(panelToRefresh, target);

				target.add(manageEmployees);
				if (feedbackPanel != null) {
					target.add(feedbackPanel);
				}

				if (cTimesheetEditableTablePanel != null) {
					cTimesheetEditableTablePanel.setEditingActions(false);
					cTimesheetEditableTablePanel.setFromOtherEmployeess(false);
					cTimesheetEditableTablePanel.getTable().setFirstPage();
					cTimesheetEditableTablePanel.setShowMassEditButton(false);
					target.add(cTimesheetEditableTablePanel);
					target.add(cTimesheetEditableTablePanel.getcTimesheetConfirmButtonPanel());
				}
			}

			@Override
			public boolean isVisible() {
				// iba pre admina
				return CSedSession.get().getUser().containsRole(IUserTypeCode.ID_ORG_ADMIN);
			}
		};
		manageEmployees.add(chooseAllEmployees);

		AjaxFallbackLink<Object> chooseAllEmployeesInvalid = new AjaxFallbackLink<Object>("chooseAllEmployeesInvalid") {
			/** serial uid */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				filter.setEmplyees(new HashSet<Long>(emplyeesAlsoInvalid));
				pageTitleSmall.setDefaultModelObject(getPageTitleSmallString(SelectOptions.ALL_SUBORDINATE));

				target.add(pageTitleSmall);

				refresh(panelToRefresh, target);

				target.add(manageEmployees);
				if (feedbackPanel != null) {
					target.add(feedbackPanel);
				}

				if (cTimesheetEditableTablePanel != null) {
					cTimesheetEditableTablePanel.setEditingActions(false);
					cTimesheetEditableTablePanel.setFromOtherEmployeess(false);
					cTimesheetEditableTablePanel.getTable().setFirstPage();
					cTimesheetEditableTablePanel.setShowMassEditButton(false);
					target.add(cTimesheetEditableTablePanel);
					target.add(cTimesheetEditableTablePanel.getcTimesheetConfirmButtonPanel());
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

				if (filter instanceof CSubrodinateTimeStampBrwFilterCriteria) {
					// pre vykaz prace chcem dynamicky zobrazovat/skryvat stlpec zamestnanec

					if (filter.getEmplyees().size() <= 1) {
						// skryjem stlpec zamestnanec
						((CSedDataGrid) panelToRefresh).showColumn(EMPLOYEE, false, target);
					} else {
						// zobrazim stlpec zamestnanec
						((CSedDataGrid) panelToRefresh).showColumn(EMPLOYEE, true, target);
					}
				}

				target.add(pageTitleSmall);

				refresh(panelToRefresh, target);

				target.add(manageEmployees);
				if (feedbackPanel != null) {
					target.add(feedbackPanel);
				}

				if (cTimesheetEditableTablePanel != null) {
					cTimesheetEditableTablePanel.setEditingActions(false);
					cTimesheetEditableTablePanel.setFromOtherEmployeess(false);
					cTimesheetEditableTablePanel.getTable().setFirstPage();
					cTimesheetEditableTablePanel.setShowMassEditButton(true);
					target.add(cTimesheetEditableTablePanel);
					target.add(cTimesheetEditableTablePanel.getcTimesheetConfirmButtonPanel());
				}
			}

			@Override
			public boolean isVisible() {
				// u admina toto tlacidlo nema vyznam
				return !CSedSession.get().getUser().containsRole(IUserTypeCode.ID_ORG_ADMIN);
			}
		};
		manageEmployees.add(chooseOnlyMe);

		AjaxFallbackLink<Object> chooseNotified = new AjaxFallbackLink<Object>("chooseNotified") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				((CEmployeesTreeModalPanel) employeesModalPanel).getNotifiedUsers(listNotifiedEmployees);
				((CEmployeesTreeModalPanel) employeesModalPanel).updateProviderSubset();
				target.add(employeesModalPanel);
				employeesModal.show(target);
				target.add(manageEmployees);
				if (feedbackPanel != null) {
					target.add(feedbackPanel);
				}
				if (cTimesheetEditableTablePanel != null) {
					/*
					 * klikol som na Vybrať ďalších zamestnancov, tak nastavím booleany, ktorými
					 * zakážem editovanie časových značiek
					 */
					cTimesheetEditableTablePanel.setEditingActions(true);
					cTimesheetEditableTablePanel.setFromOtherEmployeess(true);
					cTimesheetEditableTablePanel.getTable().setFirstPage();
					cTimesheetEditableTablePanel.setShowMassEditButton(false); // skryjem tlačidlo na hromadnú editáciu ČZ
					target.add(cTimesheetEditableTablePanel);
					target.add(cTimesheetEditableTablePanel.getcTimesheetConfirmButtonPanel());
				}
			}

			@Override
			public boolean isVisible() {
				// tlačidlo "Zobraziť prístupných zamestnancov" zobrazujem podľa notifikovaných ale na obrazovke zobrazujem aj seba a podriadených
				return notifiedEmployeesWithoutSubordiantes.size() != 0;
			}
		};
		manageEmployees.add(chooseNotified);

		AjaxFallbackLink<Object> chooseAllNotified = new AjaxFallbackLink<Object>("chooseAllNotified") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				Set<Long> allEmplyees = new HashSet<>();

				try {
					for (CViewOrganizationTreeNodeRecord cViewOrganizationTreeNodeRecord : userService.listNotifiedUsers(Boolean.TRUE)) {
						allEmplyees.add(cViewOrganizationTreeNodeRecord.getUserId());
					}
				} catch (CBussinessDataException e) {
					CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
				}

				filter.setEmplyees(allEmplyees);

				if (filter instanceof CSubrodinateTimeStampBrwFilterCriteria) {
					// pre vykaz prace chcem dynamicky zobrazovat/skryvat stlpec
					// zamestnanec

					if (filter.getEmplyees().size() <= 1) {
						// skryjem stlpec zamestnanec
						((CSedDataGrid) panelToRefresh).showColumn(EMPLOYEE, false, target);
					} else {
						// zobrazim stlpec zamestnanec
						((CSedDataGrid) panelToRefresh).showColumn(EMPLOYEE, true, target);
					}
				}

				pageTitleSmall.setDefaultModelObject(getPageTitleSmallString(SelectOptions.ALL_EMPLOYEES));

				target.add(pageTitleSmall);
				target.add(manageEmployees);

				refresh(panelToRefresh, target);

				if (cTimesheetEditableTablePanel != null) {
					/*
					 * klikol som na Vybrať ďalších zamestnancov, tak nastavím booleany, ktorými
					 * zakážem editovanie časových značiek
					 */
					cTimesheetEditableTablePanel.setEditingActions(true);
					cTimesheetEditableTablePanel.setFromOtherEmployeess(true);
					cTimesheetEditableTablePanel.getTable().setFirstPage();
					cTimesheetEditableTablePanel.setShowMassEditButton(false); // skryjem tlačidlo na hromadnú editáciu ČZ
					target.add(cTimesheetEditableTablePanel);
					target.add(cTimesheetEditableTablePanel.getcTimesheetConfirmButtonPanel());
				}
			}

			@Override
			public boolean isVisible() {
				// tlačidlo Zobraziť všetkých prístupných zamestnancov" zobrazujem podľa notifikovaných ale na obrazovke zobrazujem aj seba a podriadených
				return notifiedEmployeesWithoutSubordiantes.size() != 0;
			}

		};
		manageEmployees.add(chooseAllNotified);
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

		String pageTitleSmallString = "";

		if (spp == null) {
			return pageTitleSmallString;
		}

		switch (spp) {
		case VYKAZ_PRACE:
			pageTitleSmallString = getString("page.timesheet.label.title.small");
			break;

		case ZIADOSTI:
			pageTitleSmallString = getString("requests.label.title.small");
			break;

		case STATS:
			pageTitleSmallString = getString("multiBarChart.label.title.small");
			break;
		}

		switch (selected) {
		case CHOOSE_EMPLOYEES:
			break;

		case ME:
			pageTitleSmallString += " " + CSedSession.get().getLoggedUserName();
			break;

		case ALL_SUBORDINATE:
			pageTitleSmallString += " " + getString("chooseEmployees.allSubordinateEmployees");
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
}
