package sk.qbsw.sed.panel.requests;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.StringValidator;

import sk.qbsw.sed.client.model.IRequestTypes;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.codelist.CHolidayRecord;
import sk.qbsw.sed.client.model.detail.CUserDetailRecord;
import sk.qbsw.sed.client.model.request.CRequestRecord;
import sk.qbsw.sed.client.ui.component.panel.menu.IUserTypeCode;
import sk.qbsw.sed.common.utils.CDateUtils;
import sk.qbsw.sed.communication.service.IHolidayClientService;
import sk.qbsw.sed.communication.service.IRequestReasonClientService;
import sk.qbsw.sed.communication.service.IRequestTypeClientService;
import sk.qbsw.sed.communication.service.IUserClientService;
import sk.qbsw.sed.fw.component.EDataType;
import sk.qbsw.sed.fw.component.form.CStatelessForm;
import sk.qbsw.sed.fw.component.form.input.CDropDownChoice;
import sk.qbsw.sed.fw.component.form.input.CTextField;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.web.ui.CSedSession;
import sk.qbsw.sed.web.ui.components.CLengthTooltipAppender;

public class CRequestForm<T extends CRequestRecord> extends CStatelessForm<CRequestRecord> {

	/** serial uid */
	private static final long serialVersionUID = 1L;

	private static final String CLASS = "class";
	
	private static final String ON_CHANGE = "onchange";
	
	private List<CCodeListRecord> sicknessReasonList;

	private List<CCodeListRecord> workbreakReasonList;

	private List<CCodeListRecord> homeofficeReasonList;

	@SpringBean
	private IUserClientService userService;

	@SpringBean
	private IRequestReasonClientService requestReasonService;

	@SpringBean
	private IRequestTypeClientService requestTypeService;

	@SpringBean
	private IHolidayClientService holidayClientService;

	private boolean isModeDetail = false;
	private boolean isModeAdd = true;
	private boolean isModeEdit = false;

	private AjaxCheckBox halfDay;
	private CTextField<String> dateField;
	private CTextField<String> workDays;
	private CTextField<String> remainingDaysField;
	private final WebMarkupContainer remainingDaysContainer;
	private boolean halfDayFlag = false;
	private Float oldWorkDays = Float.valueOf(0);

	public CRequestForm(String id, final IModel<CRequestRecord> projectModel) {
		super(id, projectModel);
		setOutputMarkupId(true);

		List<CCodeListRecord> employeeList = new ArrayList<>();
		sicknessReasonList = new ArrayList<>();
		workbreakReasonList = new ArrayList<>();
		homeofficeReasonList = new ArrayList<>();
		List<CCodeListRecord> typeOptions = new ArrayList<>();
		try {
			employeeList = userService.listSubordinateUsers(Boolean.TRUE, Boolean.TRUE);
			sicknessReasonList = requestReasonService.getReasonLists(CSedSession.get().getUser().getClientInfo().getClientId()).getSicknessReasonList();
			workbreakReasonList = requestReasonService.getReasonLists(CSedSession.get().getUser().getClientInfo().getClientId()).getWorkbreakReasonList();
			homeofficeReasonList = requestReasonService.getReasonLists(CSedSession.get().getUser().getClientInfo().getClientId()).getHomeofficeReasonList();
			typeOptions = requestTypeService.getValidRecords();
		} catch (CBussinessDataException e) {
			CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
			error(getString(e.getModel().getServerCode()));
		}

		final CTextField<String> superior = new CTextField<>("superior", EDataType.TEXT);
		superior.setOutputMarkupId(true);
		superior.setEnabled(false);
		add(superior);

		final CDropDownChoice<CCodeListRecord> employee = new CDropDownChoice<CCodeListRecord>("owner", employeeList) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				Boolean isBasicEmployee = CSedSession.get().getUser().containsRole(IUserTypeCode.ID_EMPLOYEE) || CSedSession.get().getUser().containsRole(IUserTypeCode.ID_EMPLOYEE_WITHOUT_SUB);
				return isModeAdd || (isModeEdit && !isBasicEmployee);
			}

			@Override
			public boolean isVisible() {
				return isModeAdd;
			}
		};
		employee.add(new AjaxFormComponentUpdatingBehavior(ON_CHANGE) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				try {
					superior.getModel().setObject(userService.getUserDetails(employee.getModelObject().getId()).getSuperior());
					target.add(superior);

					target.add(remainingDaysContainer);

					if (isRemainingDaysVisible()) {
						remainingDaysField.setDefaultModelObject(recalculateRemainingDays(getModelObject().getWorkDays()));
						target.add(remainingDaysField);
					}

				} catch (CBussinessDataException e) {
					CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
					error(getString(e.getModel().getServerCode()));
				}
			}
		});
		employee.setRequired(true);
		add(employee);

		try {
			superior.getModel().setObject(userService.getUserDetails(employee.getModelObject().getId()).getSuperior());
		} catch (CBussinessDataException e) {
			CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
			error(getString(e.getModel().getServerCode()));
		}

		CTextField<String> employeeField = new CTextField<String>("employeeField", EDataType.TEXT) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return !isModeAdd;
			}
		};
		employeeField.setEnabled(false);
		add(employeeField);

		// container, aby menil visibility aj pre label
		final WebMarkupContainer halfDayContainer = new WebMarkupContainer("halfDayContainer") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return getModelObject().getTypeId().equals(IRequestTypes.ID_H);
			}
		};
		halfDayContainer.setOutputMarkupId(true);
		halfDayContainer.setOutputMarkupPlaceholderTag(true);
		add(halfDayContainer);

		halfDay = new AjaxCheckBox("halfday", new PropertyModel<Boolean>(getModel(), "halfday")) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				halfDayFlag = !halfDayFlag;
				CRequestForm.this.getModelObject().setHalfday(halfDayFlag);

				if (dateField.hasErrorMessage()) {
					dateField.getFeedbackMessages().clear();
				}

				if (halfDayFlag) {
					dateField.add(AttributeModifier.replace(CLASS, "form-control date-picker"));
					target.appendJavaScript("$('.daterangepicker').hide();");

					CRequestForm.this.getModelObject().setDateTo(CRequestForm.this.getModelObject().getDateFrom());
				} else {
					dateField.add(AttributeModifier.replace(CLASS, "form-control date-range"));
					target.appendJavaScript("$('.datepicker').hide();");
				}

				target.add(dateField);
				target.appendJavaScript("FormElements.init();");

				try {
					double workingDays = recalculateWorkingDays();
					workDays.setDefaultModelObject(workingDays);
					target.add(workDays);

					if (isRemainingDaysVisible()) {
						remainingDaysField.setDefaultModelObject(recalculateRemainingDays(workingDays));
						target.add(remainingDaysField);
					}
				} catch (CBussinessDataException e) {
					CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
				}
			}

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		halfDayContainer.add(halfDay);

		dateField = new CTextField<String>("date", EDataType.TEXT) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}

			@Override
			public void validate() {
				super.validate();

				try {
					if (halfDayFlag) {
						CDateUtils.parseDate(dateField.getValue());
					} else {
						CDateUtils.parseRange(dateField.getValue());
					}
				} catch (ParseException e) {
					ValidationError error = new ValidationError();
					error.addKey("DateRangeValidator");
					dateField.error(error);
				}
			}
		};
		dateField.setRequired(true);
		add(dateField);

		dateField.add(new AjaxFormComponentUpdatingBehavior(ON_CHANGE) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				try {
					double workingDays = recalculateWorkingDays();
					workDays.setDefaultModelObject(workingDays);
					target.add(workDays);

					if (isRemainingDaysVisible()) {
						remainingDaysField.setDefaultModelObject(recalculateRemainingDays(workingDays));
						target.add(remainingDaysField);
					}
				} catch (CBussinessDataException e) {
					CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
				}
			}
		});

		workDays = new CTextField<String>("workDays", EDataType.TEXT) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return false;
			}
		};
		workDays.setOutputMarkupId(true);
		add(workDays);

		// container, aby menil visibility aj pre label
		remainingDaysContainer = new WebMarkupContainer("remainingDaysContainer") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return isRemainingDaysVisible() && !isModeDetail;
			}
		};
		remainingDaysContainer.setOutputMarkupPlaceholderTag(true);
		add(remainingDaysContainer);

		remainingDaysField = new CTextField<String>("remainingDays", EDataType.TEXT) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return false;
			}
		};
		remainingDaysField.setOutputMarkupPlaceholderTag(true);
		remainingDaysContainer.add(remainingDaysField);

		CTextField<String> place = new CTextField<String>("place", EDataType.TEXT) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		place.add(new CLengthTooltipAppender(100));
		place.add(StringValidator.maximumLength(100));
		add(place);

		// container, aby menil visibility aj pre label
		final WebMarkupContainer requestResponsalisContainer = new WebMarkupContainer("requestResponsalisContainer") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return getModelObject().getTypeId().equals(IRequestTypes.ID_H);
			}
		};
		requestResponsalisContainer.setOutputMarkupId(true);
		requestResponsalisContainer.setOutputMarkupPlaceholderTag(true);
		add(requestResponsalisContainer);

		CTextField<String> responsalis = new CTextField<String>("responsalisName", EDataType.TEXT) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		responsalis.add(new CLengthTooltipAppender(150));
		responsalis.add(StringValidator.maximumLength(150));
		requestResponsalisContainer.add(responsalis);

		// container, aby menil visibility aj pre label
		final WebMarkupContainer requestNoteContainer = new WebMarkupContainer("requestNoteContainer") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return getModelObject().getTypeId().equals(IRequestTypes.ID_WAH) || // práca z domu
						getModelObject().getTypeId().equals(IRequestTypes.ID_ST) || // školenie
						getModelObject().getTypeId().equals(IRequestTypes.ID_H);	// dovolenka
			}
		};
		requestNoteContainer.setOutputMarkupId(true);
		requestNoteContainer.setOutputMarkupPlaceholderTag(true);
		add(requestNoteContainer);

		CTextField<String> note = new CTextField<String>("note", EDataType.TEXT) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		note.add(new CLengthTooltipAppender(1000));
		note.add(StringValidator.maximumLength(1000));
		requestNoteContainer.add(note);

		CTextField<Date> createDate = new CTextField<>("createDate", EDataType.DATE);
		createDate.setEnabled(false);
		add(createDate);

		// container, aby menil visibility aj pre label
		final WebMarkupContainer requestReasonContainer = new WebMarkupContainer("requestReasonContainer") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return getModelObject().getTypeId().equals(IRequestTypes.ID_SD) || getModelObject().getTypeId().equals(IRequestTypes.ID_WB)
						|| getModelObject().getTypeId().equals(IRequestTypes.ID_WAH);
			}
		};
		requestReasonContainer.setOutputMarkupId(true);
		requestReasonContainer.setOutputMarkupPlaceholderTag(true);
		add(requestReasonContainer);

		final CDropDownChoice<CCodeListRecord> requestReason = new CDropDownChoice<CCodeListRecord>("requestReason", new ArrayList<CCodeListRecord>()) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}

			@Override
			public boolean isRequired() {
				return CRequestForm.this.getModelObject().getTypeId().equals(IRequestTypes.ID_SD) || CRequestForm.this.getModelObject().getTypeId().equals(IRequestTypes.ID_WB);
			}
		};
		requestReason.setNullValid(false);
		requestReasonContainer.add(requestReason);

		final CDropDownChoice<CCodeListRecord> requestType = new CDropDownChoice<CCodeListRecord>("requestType", typeOptions) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return isModeAdd;
			}
		};
		requestType.add(new AjaxFormComponentUpdatingBehavior(ON_CHANGE) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				setRequestReasonChoises();

				if (halfDayFlag) {
					setHalfDayFlag(false);
					CRequestForm.this.getModelObject().setHalfday(halfDayFlag);
					target.add(dateField);
					target.appendJavaScript("FormElements.init();");

					try {
						double workingDays = recalculateWorkingDays();
						workDays.setDefaultModelObject(workingDays);
						target.add(workDays);

						if (isRemainingDaysVisible()) {
							remainingDaysField.setDefaultModelObject(recalculateRemainingDays(workingDays));
							target.add(remainingDaysField);
						}
					} catch (CBussinessDataException e) {
						CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
					}
				}

				target.add(halfDayContainer);
				target.add(requestReasonContainer);
				target.add(requestNoteContainer);
				target.add(requestResponsalisContainer);

				target.add(remainingDaysContainer);
			}

			@Override
			protected void onComponentRendered() {
				super.onComponentRendered();

				setRequestReasonChoises();
			}

			private void setRequestReasonChoises() {
				getModelObject().setTypeId(requestType.getModel().getObject().getId());
				if (getModelObject().getTypeId().equals(IRequestTypes.ID_SD)) {
					requestReason.setChoices(sicknessReasonList);
				} else if (getModelObject().getTypeId().equals(IRequestTypes.ID_WB)) {
					requestReason.setChoices(workbreakReasonList);
				} else if (getModelObject().getTypeId().equals(IRequestTypes.ID_WAH)) {
					requestReason.setChoices(homeofficeReasonList);
				}
			}
		});
		add(requestType);

		// container, aby menil visibility aj pre label
		final WebMarkupContainer changeContainer = new WebMarkupContainer("changeContainer") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return !isModeAdd;
			}
		};
		changeContainer.setOutputMarkupId(true);
		changeContainer.setOutputMarkupPlaceholderTag(true);
		add(changeContainer);

		CTextField<Date> changeDateTime = new CTextField<>("changeDateTime", EDataType.DATE_TIME);
		changeDateTime.setEnabled(false);
		changeContainer.add(changeDateTime);

		CTextField<String> changedBy = new CTextField<>("changedBy", EDataType.TEXT);
		changedBy.setEnabled(false);
		changeContainer.add(changedBy);

		CTextField<String> stateField = new CTextField<>("statusDescription", EDataType.TEXT);
		stateField.setEnabled(false);
		changeContainer.add(stateField);
	}

	public void setModeDetail(boolean isModeDetail) {
		this.isModeDetail = isModeDetail;
	}

	public void setModeAdd(boolean isModeAdd) {
		this.isModeAdd = isModeAdd;
	}

	private double recalculateWorkingDays() throws CBussinessDataException {
		if (getModelObject().getDateFrom() == null || getModelObject().getDateTo() == null) {
			return 0;
		}

		List<CHolidayRecord> holidays = holidayClientService.getClientRecordsForTheYear(CSedSession.get().getUser().getClientInfo().getClientId(), null);

		if (halfDayFlag) {
			return 0.5 * CDateUtils.getWorkingDaysCheckHolidays(getModelObject().getDateFrom(), getModelObject().getDateTo(), holidays);
		} else {
			return 1.0 * CDateUtils.getWorkingDaysCheckHolidays(getModelObject().getDateFrom(), getModelObject().getDateTo(), holidays);
		}
	}

	/**
	 * pole "Zostávajúce dni dovolenky", ktoré bude obsahovať rozdiel medzi
	 * t_user.c_vacation pre daného používateľa a hodnotou v poli "Počet pracovných dní"
	 * 
	 * @param workingDays
	 * @return remaining days
	 * @throws CBussinessDataException
	 */
	private double recalculateRemainingDays(double workingDays) throws CBussinessDataException {
		CUserDetailRecord user = userService.getUserDetails(getModelObject().getOwnerId());
		Double vacation = user.getVacation();

		if (vacation != null) {
			return vacation - recalculateWorkingDaysForActualYear(workingDays) + oldWorkDays;
		}
		return 0;
	}

	public void setHalfDayFlag(boolean halfDayFlag) {
		this.halfDayFlag = halfDayFlag;

		if (halfDayFlag) {
			dateField.add(AttributeModifier.replace(CLASS, "form-control date-picker"));
		} else {
			dateField.add(AttributeModifier.replace(CLASS, "form-control date-range"));
		}
	}

	public CTextField<String> getDateField() {
		return dateField;
	}

	public boolean isModeEdit() {
		return isModeEdit;
	}

	public void setModeEdit(boolean isModeEdit) {
		this.isModeEdit = isModeEdit;
	}

	/**
	 * v prípade, že je zobrazená žiadosť s typom "Dovolenka", na obrazovke zobraziť
	 * pole "Zostávajúce dni dovolenky" ak to niekto ma prazdne (napr. je
	 * zivnostnik) tak tiez pole nezobrazujeme
	 *
	 * @return
	 * @throws CBussinessDataException
	 */
	private boolean isRemainingDaysVisible() {
		CUserDetailRecord user;
		try {
			user = userService.getUserDetails(getModelObject().getOwnerId());
			Double vacation = user.getVacation();
			return getModelObject().getTypeId().equals(IRequestTypes.ID_H) && vacation != null;
		} catch (CBussinessDataException e) {
			Logger.getLogger(CRequestForm.class).error(e);
		}
		return false;
	}

	public void setOldWorkDays(Float oldWorkDays) {
		this.oldWorkDays = (float) recalculateWorkingDaysForActualYear(oldWorkDays.doubleValue());
	}

	/**
	 * 
	 * @return
	 * @throws CBussinessDataException
	 */
	private double recalculateWorkingDaysForActualYear(double workingDays) {
		if (getModelObject().getDateFrom() == null || getModelObject().getDateTo() == null) {
			return 0;
		}

		Calendar calendarTo = Calendar.getInstance();
		calendarTo.setTime(getModelObject().getDateTo());

		int actualYear = Calendar.getInstance().get(Calendar.YEAR);

		if (calendarTo.get(Calendar.YEAR) > actualYear) {
			calendarTo.set(Calendar.YEAR, actualYear);
			calendarTo.set(Calendar.MONTH, 11);
			calendarTo.set(Calendar.DAY_OF_MONTH, 31);
		} else {
			return workingDays;
		}

		List<CHolidayRecord> holidays = null;
		try {
			holidays = holidayClientService.getClientRecordsForTheYear(CSedSession.get().getUser().getClientInfo().getClientId(), actualYear);
		} catch (CBussinessDataException e) {
			CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
		}

		if (halfDayFlag) {
			return 0.5 * CDateUtils.getWorkingDaysCheckHolidays(getModelObject().getDateFrom(), calendarTo.getTime(), holidays);
		} else {
			return 1.0 * CDateUtils.getWorkingDaysCheckHolidays(getModelObject().getDateFrom(), calendarTo.getTime(), holidays);
		}
	}
}
