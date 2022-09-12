package sk.qbsw.sed.panel.home;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import sk.qbsw.sed.client.model.request.CRequestRecord;
import sk.qbsw.sed.client.model.request.CSubordinateRequestsBrwFilterCriteria;
import sk.qbsw.sed.client.model.tree.org.CViewOrganizationTreeNodeRecord;
import sk.qbsw.sed.common.utils.CDateUtils;
import sk.qbsw.sed.communication.service.IBrwRequestClientService;
import sk.qbsw.sed.component.behaviour.CPlaceholderBehaviour;
import sk.qbsw.sed.framework.security.exception.CSecurityException;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.component.form.CDisableEnterKeyBehavior;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CStringResourceReader;
import sk.qbsw.sed.fw.utils.CTextUtils;
import sk.qbsw.sed.web.ui.CSedSession;

/**
 * SubPage: /home SubPage title: Dashboard
 * 
 * Panel RequestsPanel - Žiadosti
 */
public class CRequestsPanel extends CPanel {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	private static final String DATE_FROM_COL = "dateFrom";
	
	private static final String CLASS = "class";
	
	private static final String ACTIVE = "active";

	@SpringBean
	private IBrwRequestClientService requestsService;

	private List<CViewOrganizationTreeNodeRecord> organizationTree;

	/**
	 * create new requests panel
	 * 
	 * @throws CSecurityException
	 */
	public CRequestsPanel(String id, CFeedbackPanel errorPanel, List<CViewOrganizationTreeNodeRecord> organizationTree) {
		super(id);
		registerFeedbackPanel(errorPanel);
		this.organizationTree = organizationTree;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		load();
	}

	private void load() {
		WebMarkupContainer myTab = new WebMarkupContainer("myTab");
		myTab.setOutputMarkupId(true);
		add(myTab);

		WebMarkupContainer favoriteTab = new WebMarkupContainer("favoriteTab") {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return !getuser().getUserFavourites().isEmpty();
			}
		};
		favoriteTab.setOutputMarkupPlaceholderTag(true);
		add(favoriteTab);

		WebMarkupContainer myTabContent = new WebMarkupContainer("myTabContent");
		myTabContent.setOutputMarkupId(true);
		add(myTabContent);

		WebMarkupContainer favoriteTabContent = new WebMarkupContainer("favoriteTabContent");
		favoriteTabContent.setOutputMarkupId(true);
		add(favoriteTabContent);

		if (getuser().getUserFavourites().isEmpty()) {
			myTab.add(AttributeModifier.append(CLASS, ACTIVE));
			myTabContent.add(AttributeModifier.append(CLASS, ACTIVE));
		} else {
			favoriteTab.add(AttributeModifier.append(CLASS, ACTIVE));
			favoriteTabContent.add(AttributeModifier.append(CLASS, ACTIVE));
		}

		try {
			List<CRequestRecord> requests = null;
			List<CRequestRecord> teamRequests = null;
			List<CRequestRecord> currentUserRequests = null;
			List<CRequestRecord> favoriteRequests = null;
			final ListView<CRequestRecord> requestsListAll;
			final ListView<CRequestRecord> requestsListTeam;
			final ListView<CRequestRecord> requestsListCurrentUser;
			final ListView<CRequestRecord> requestsListFavorite;

			CSubordinateRequestsBrwFilterCriteria criteria = new CSubordinateRequestsBrwFilterCriteria();
			criteria.setDateFrom(new Date());
			requests = requestsService.loadData(0L, new Long(Integer.MAX_VALUE), DATE_FROM_COL, true, criteria);
			teamRequests = getTeamRequests(requests);
			currentUserRequests = getCurrentUserRequests(teamRequests);
			favoriteRequests = getFavorite(requests);

			requestsListAll = new ListViewOfReqests("requestsListAll", requests, "ALL");
			requestsListTeam = new ListViewOfReqests("requestsListTeam", teamRequests, "TEAM");
			requestsListCurrentUser = new ListViewOfReqests("requestsListCurrentUser", currentUserRequests, "CURRENT_USER");
			requestsListFavorite = new ListViewOfReqests("requestsListFavorite", favoriteRequests, "FAVORITE");

			final WebMarkupContainer listAll = new WebMarkupContainer("listAll");
			final WebMarkupContainer listTeam = new WebMarkupContainer("listTeam");
			final WebMarkupContainer listCurrentUser = new WebMarkupContainer("listCurrentUser");
			final WebMarkupContainer favoriteContainer = new WebMarkupContainer("listFavorite");

			listAll.add(requestsListAll);
			listAll.setOutputMarkupId(true);

			listTeam.add(requestsListTeam);
			listTeam.setOutputMarkupId(true);

			listCurrentUser.add(requestsListCurrentUser);
			listCurrentUser.setOutputMarkupId(true);

			favoriteContainer.add(requestsListFavorite);
			favoriteContainer.setOutputMarkupId(true);

			add(listAll);
			add(listTeam);
			myTabContent.add(listCurrentUser);
			favoriteTabContent.add(favoriteContainer);

			final TextField<String> search = new TextField<>("search", new Model<String>());
			search.add(new AjaxFormComponentUpdatingBehavior("onkeyup") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(final AjaxRequestTarget target) {
					try {
						CSubordinateRequestsBrwFilterCriteria criteria = new CSubordinateRequestsBrwFilterCriteria();
						criteria.setDateFrom(new Date());
						List<CRequestRecord> requests = (requestsService.loadData(0L, new Long(Integer.MAX_VALUE), DATE_FROM_COL, true, criteria));
						List<CRequestRecord> teamRequests = getTeamRequests(requests);
						List<CRequestRecord> currentUserRequests = getCurrentUserRequests(teamRequests);
						List<CRequestRecord> favoriteRequests = getFavorite(requests);

						requestsListAll.setModelObject(searchList(requests, search.getInput()));
						requestsListTeam.setModelObject(searchList(teamRequests, search.getInput()));
						requestsListCurrentUser.setModelObject(searchList(currentUserRequests, search.getInput()));
						requestsListFavorite.setModelObject(searchList(favoriteRequests, search.getInput()));

						target.add(listAll);
						target.add(listTeam);
						target.add(listCurrentUser);
						target.add(favoriteContainer);
						target.appendJavaScript("Main.runPanelScroll();");

					} catch (CBussinessDataException e) {
						CBussinessDataExceptionProcessor.process(e, getSession(), target, getPage());
					}
				}
			});

			search.add(new CDisableEnterKeyBehavior());
			add(search);
			search.add(new CPlaceholderBehaviour(getString("searchbar.placeholder")));

		} catch (CBussinessDataException e) {
			CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
		}
	}

	private List<CRequestRecord> getCurrentUserRequests(List<CRequestRecord> usersRequests) throws CBussinessDataException {

		List<CRequestRecord> currentUser = new ArrayList<>();

		for (CRequestRecord userRequest : usersRequests) {
			if (CSedSession.get().getUser().getUserId().equals(userRequest.getOwnerId())) {
				currentUser.add(userRequest);
			}
		}

		return currentUser;
	}

	private List<CRequestRecord> getFavorite(List<CRequestRecord> allRequests) throws CBussinessDataException {
		List<CRequestRecord> favorite = new ArrayList<>();

		for (CRequestRecord request : allRequests) {

			if (getuser().getUserFavourites().contains(request.getOwnerId())) {
				favorite.add(request);
			}
		}

		return favorite;
	}

	private List<CRequestRecord> getTeamRequests(List<CRequestRecord> requests) throws CBussinessDataException {

		List<CRequestRecord> teamRequests = new ArrayList<>();

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
		Long supervisor = null;
		List<Long> teamUsers = new ArrayList<>();
		List<Long> teamTemp = new ArrayList<>();

		for (CViewOrganizationTreeNodeRecord user : organizationTree) {
			if (isUnder(userId, user.getParentId()) || CSedSession.get().getUser().getUserId().equals(user.getUserId())) {
				teamUsers.add(user.getUserId());
			} else if (searchSupervisor && parentId.equals(user.getId())) {
				supervisor = user.getUserId();
				searchSupervisor = false;
			} else if (parentId != null && parentId.equals(user.getParentId())) {
				teamTemp.add(user.getUserId());
			}
		}

		if (teamUsers.size() == 1) { // nema podriadenych
			if (supervisor != null) { // pridaj nadriadeneho
				teamUsers.add(supervisor);
			}
			if (!teamTemp.isEmpty()) { // pridaj podriadenych nadriadeneho
				teamUsers.addAll(teamTemp);
			}
		}

		for (CRequestRecord request : requests) {
			for (Long user_id : teamUsers) {
				if (user_id.equals(request.getOwnerId())) {
					teamRequests.add(request);
					break;
				}
			}
		}

		return teamRequests;
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

	private class ListViewOfReqests extends ListView<CRequestRecord> {

		private static final long serialVersionUID = 1L;
		private String tabId;

		private ListViewOfReqests(String id, List<CRequestRecord> requests, String tabId) {
			super(id, requests);
			this.tabId = tabId;
		}

		@Override
		protected void populateItem(ListItem<CRequestRecord> item) {

			final CRequestRecord request = (CRequestRecord) item.getModelObject();
			item.add(AttributeModifier.append("id", tabId + "_" + request.getId()));

			item.add(new Label("name", request.getOwnerSurname() + " " + request.getOwnerName()));

			Label type = new Label("type", request.getTypeDescription());
			Long requestTypeId = request.getRequestType().getId();

			// Pracovné činnosti: 4 = "Pracovná cesta", 6 = "Školenie", 7 = "Práca z domu",
			// 8 = "Práca mimo pracoviska"
			if (Long.valueOf(4).equals(requestTypeId) || Long.valueOf(6).equals(requestTypeId) || Long.valueOf(7).equals(requestTypeId) || Long.valueOf(8).equals(requestTypeId)) {
				// pracovné činnosti azúrovo modrou farbou
				type.add(new AttributeModifier(CLASS, "text-azure"));
			} else {
				// nepracovné činnosti červenou farbou
				type.add(new AttributeModifier(CLASS, "text-red"));
			}

			item.add(type);

			if (request.getWorkDays() >= 1) {
				item.add(new Label("timeInfo", CDateUtils.formatDate(request.getDateFrom()) + " - " + CDateUtils.formatDate(request.getDateTo())));
			} else {
				item.add(new Label("timeInfo", CDateUtils.formatDate(request.getDateFrom()) + " - " + CStringResourceReader.read("requests.panel.halfday")));
			}

			Label status = new Label("status", request.getStatusDescription());
			String statusCssClass = "label ";
			switch (request.getStatusId().intValue()) {
			case 1:
				statusCssClass += "label-warning";
				break;
			case 3:
				statusCssClass += "label-success";
				break;
			case 2:
			case 4:
				statusCssClass += "label-danger";
				break;
			default:
				statusCssClass += "label-warning";
				break;
			}

			status.add(new AttributeModifier(CLASS, statusCssClass));
			item.add(status);

			add(item);
		}
	}

	private List<CRequestRecord> searchList(final List<CRequestRecord> list, String searchedText) {
		List<CRequestRecord> newList = new ArrayList<>();

		for (CRequestRecord e : list) {
			if (CTextUtils.contains(e.getOwnerName(), searchedText) || CTextUtils.contains(e.getOwnerSurname(), searchedText)) {
				newList.add(e);
			}
		}
		return newList;
	}
}
