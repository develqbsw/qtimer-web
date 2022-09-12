
package sk.qbsw.sed.panel.activitylimits;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;

import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.restriction.CActivityIntervalData;
import sk.qbsw.sed.communication.service.IActivityClientService;
import sk.qbsw.sed.communication.service.IActivityRestrictionClientService;
import sk.qbsw.sed.fw.component.EDataType;
import sk.qbsw.sed.fw.component.form.CStatelessForm;
import sk.qbsw.sed.fw.component.form.input.CDropDownChoice;
import sk.qbsw.sed.fw.component.form.input.CTextField;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.fw.utils.CStringResourceReader;
import sk.qbsw.sed.web.ui.CSedSession;
import sk.qbsw.sed.web.ui.components.CLengthTooltipAppender;

public class CActivityRIntervalForm<T extends CActivityIntervalData> extends CStatelessForm<CActivityIntervalData> {

	/** serial uid */
	private static final long serialVersionUID = 1L;

	private CDropDownChoice<CCodeListRecord> activityGroup;

	@SpringBean
	private IActivityClientService activityService;

	@SpringBean
	private IActivityRestrictionClientService activityIntervalService;

	private boolean isModeDetail = false;

	private Long defaultGroupId;

	/**
	 *
	 * @param id            - wicket id
	 * @param activityModel - model of form with CActivityRecord
	 * @param actualPanel   - panel that renders after cancel button action
	 */
	public CActivityRIntervalForm(String id, final IModel<CActivityIntervalData> activityModel, Panel actualPanel, final boolean isEditMode) {
		super(id, activityModel);
		setOutputMarkupId(true);

		CTextField<String> name = new CTextField<String>("name", EDataType.TEXT) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		name.add(new CLengthTooltipAppender(200));
		name.add(StringValidator.maximumLength(200));
		name.setRequired(true);
		add(name);

		List<CCodeListRecord> activityTypeList = null;
		List<CCodeListRecord> activityGroupList = new ArrayList<>();

		try {
			activityTypeList = activityService.getValidRecordsForLimits();
		} catch (CBussinessDataException e) {
			CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
			error(getString(e.getModel().getServerCode()));
		}

		final CDropDownChoice<CCodeListRecord> activityType = new CDropDownChoice<CCodeListRecord>("activityType", activityTypeList) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};

		activityType.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				setActivityGroupList(activityType.getModelObject().getId(), target);
			}
		});
		activityType.setNullValid(true);
		activityType.setRequired(true);
		add(activityType);

		if (activityType.getModelObject() != null) {
			try {
				activityGroupList = activityIntervalService.getValidActivityGroups(CSedSession.get().getUser().getClientInfo().getClientId(), activityType.getModelObject().getId(), true);
			} catch (CBussinessDataException e) {
				CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
				error(getString(e.getModel().getServerCode()));
			}
		}

		activityGroup = new CDropDownChoice<CCodeListRecord>("group", activityGroupList) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		activityGroup.setNullValid(true);
		activityGroup.setRequired(true);
		activityGroup.setOutputMarkupId(true);
		add(activityGroup);

		List<CCodeListRecord> dayTypeList = new ArrayList<>();
		dayTypeList.add(new CCodeListRecord(0l, CStringResourceReader.read("daytype.value.workday")));
		dayTypeList.add(new CCodeListRecord(1l, CStringResourceReader.read("daytype.value.dayoff")));

		CDropDownChoice<CCodeListRecord> dayType = new CDropDownChoice<CCodeListRecord>("dayType", dayTypeList) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		dayType.setNullValid(true);
		dayType.setRequired(true);
		add(dayType);

		CTextField<Date> timeFrom = new CTextField<Date>("timeFrom", EDataType.TIME) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		timeFrom.setRequired(true);
		add(timeFrom);

		CTextField<Date> timeTo = new CTextField<Date>("timeTo", EDataType.TIME) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		timeTo.setRequired(true);
		add(timeTo);

		CTextField<Date> dateFrom = new CTextField<Date>("dateValidFrom", EDataType.DATE) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		add(dateFrom);

		CTextField<Date> dateTo = new CTextField<Date>("dateValidTo", EDataType.DATE) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		add(dateTo);

		CheckBox validBox = new CheckBox("valid", new PropertyModel<Boolean>(getModel(), "valid")) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		add(validBox);

		CTextField<String> idField = new CTextField<>("id", EDataType.NUMBER);
		idField.setEnabled(false);
		add(idField);
	}

	public void setModeDetail(boolean isModeDetail) {
		this.isModeDetail = isModeDetail;
	}

	public Long getDefaultGroupId() {
		return defaultGroupId;
	}

	public void setDefaultGroupId(Long defaultGroupId) {
		this.defaultGroupId = defaultGroupId;
	}

	public void setActivityGroupList(Long activityId, AjaxRequestTarget target) {
		try {
			activityGroup.setChoices(activityIntervalService.getValidActivityGroups(CSedSession.get().getUser().getClientInfo().getClientId(), activityId, true));
			if (target != null)
				target.add(activityGroup);
			else
				this.add(activityGroup);
		} catch (CBussinessDataException e) {
			CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
			error(getString(e.getModel().getServerCode()));
		}
	}
}
