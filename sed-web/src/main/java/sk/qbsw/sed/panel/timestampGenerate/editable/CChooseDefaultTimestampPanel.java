package sk.qbsw.sed.panel.timestampGenerate.editable;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.extensions.markup.html.form.select.Select;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import sk.qbsw.sed.client.model.IHomeOfficePermissionConstants;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.model.timestamp.CTimeStampGenerateFilterCriteria;
import sk.qbsw.sed.communication.service.IRequestClientService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.panel.home.CActivitySelect;
import sk.qbsw.sed.web.ui.CSedSession;
import sk.qbsw.sed.web.ui.components.IAjaxCommand;
import sk.qbsw.sed.web.ui.components.panel.CModalBorder;

/**
 * Confirm panel for modal dialog
 * 
 * @author farkas.roman
 * @since 2.3.0
 * @version 2.3.0
 */
public class CChooseDefaultTimestampPanel extends CPanel {
	@SpringBean
	private IRequestClientService requestService;

	private static final long serialVersionUID = 1L;

	/** action command to execute external code on OK button click */
	private IAjaxCommand action;

	private CheckBox homeOffice;

	/**
	 * 
	 * @param id
	 * @param parentWindow paren CModalBorder to hide on CANCEL button action
	 */
	public CChooseDefaultTimestampPanel(String id, final CModalBorder parentWindow, List<CCodeListRecord> activityList, CompoundPropertyModel<CTimeStampGenerateFilterCriteria> filterModel) {
		super(id);

		Form<ChooseDefaultTimestampModel> form = new Form<>("form", new Model<ChooseDefaultTimestampModel>(new ChooseDefaultTimestampModel()));
		add(form);

		CheckBox jiraKeyToPhase = new CheckBox("jiraKeyToPhase", new PropertyModel<Boolean>(form.getModel(), "jiraKeyToPhase"));
		form.add(jiraKeyToPhase);

		IModel<CCodeListRecord> activityModel = new Model<>();
		Select<CCodeListRecord> activityField = new CActivitySelect("activity", activityModel, activityList);
		form.add(activityField);

		CheckBox outside = new CheckBox("outsideWorkplace", new PropertyModel<Boolean>(form.getModel(), "outsideWorkplace"));
		form.add(outside);

		homeOffice = new CheckBox("homeOffice", new PropertyModel<Boolean>(form.getModel(), "homeOffice")) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				Date filterDateFrom = filterModel.getObject().getDateFrom();
				Date filterDateTo = filterModel.getObject().getDateTo();
				CLoggedUserRecord userDetailRecord = CSedSession.get().getUser();
				return enableHomeOffice(userDetailRecord, filterDateFrom, filterDateTo);
			}
		};
		form.add(homeOffice);

		form.add(new AjaxFallbackButton("btnOk", form) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form1) {
				filterModel.getObject().setDefaultActivity(activityModel.getObject());
				filterModel.getObject().setDefaultOutside(outside.getModelObject());
				filterModel.getObject().setDefaultHomeOffice(homeOffice.getModelObject());
				filterModel.getObject().setJiraKeyToPhase(jiraKeyToPhase.getModelObject());

				parentWindow.hide(target);
				action.execute(target);
			}
		});

		form.add(new AjaxFallbackButton("btnCancel", form) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form1) {
				parentWindow.hide(target);
			}
		});
	}

	/**
	 * sets action which is executed on OK button action
	 * 
	 * @param action
	 */
	public void setAction(IAjaxCommand action) {
		this.action = action;
	}

	public void uncheckHomeOffice() {
		this.homeOffice.setModelObject(Boolean.FALSE);
	}

	public boolean enableHomeOffice(CLoggedUserRecord loggedUser, Date dateFrom, Date dateTo) {

		if ((loggedUser.getHomeOfficePermission()).equals(IHomeOfficePermissionConstants.HO_PERMISSION_DISALLOWED)) {
			return false;
		} else if ((loggedUser.getHomeOfficePermission()).equals(IHomeOfficePermissionConstants.HO_PERMISSION_REQUEST)) {
			try {
				return requestService.isAllowedHomeOfficeInInterval(loggedUser.getUserId(), loggedUser.getClientInfo().getClientId(), dateFrom, dateTo);
			} catch (CBussinessDataException e) {
				CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
				error(getString(e.getModel().getServerCode()));
			}
		} else if ((loggedUser.getHomeOfficePermission()).equals(IHomeOfficePermissionConstants.HO_PERMISSION_ALLOWED)) {
			return true;
		}
		return false;
	}

	class ChooseDefaultTimestampModel implements Serializable {

		private static final long serialVersionUID = 1L;

		private boolean outsideWorkplace;
		private boolean homeOffice;
		private CCodeListRecord activity;
		private boolean jiraKeyToPhase;

		public boolean isOutsideWorkplace() {
			return outsideWorkplace;
		}

		public void setOutsideWorkplace(boolean outsideWorkplace) {
			this.outsideWorkplace = outsideWorkplace;
		}

		public CCodeListRecord getActivity() {
			return activity;
		}

		public void setActivity(CCodeListRecord activity) {
			this.activity = activity;
		}

		public boolean isJiraKeyToPhase() {
			return jiraKeyToPhase;
		}

		public void setJiraKeyToPhase(boolean jiraKeyToPhase) {
			this.jiraKeyToPhase = jiraKeyToPhase;
		}

		public boolean isHomeOffice() {
			return homeOffice;
		}

		public void setHomeOffice(boolean homeOffice) {
			this.homeOffice = homeOffice;
		}
	}
}
