package sk.qbsw.sed.panel.userdetail;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.upload.FileUploadBase.SizeLimitExceededException;
import org.apache.wicket.util.upload.FileUploadException;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.RangeValidator;
import org.apache.wicket.validation.validator.StringValidator;

import sk.qbsw.sed.client.model.ILanguageConstant;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.detail.CUserDetailRecord;
import sk.qbsw.sed.client.model.security.CZoneRecord;
import sk.qbsw.sed.communication.service.IUserClientService;
import sk.qbsw.sed.fw.component.CImage;
import sk.qbsw.sed.fw.component.EDataType;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.component.form.CStatelessForm;
import sk.qbsw.sed.fw.component.form.file.CFileUploadField;
import sk.qbsw.sed.fw.component.form.input.CDropDownChoice;
import sk.qbsw.sed.fw.component.form.input.CTextField;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.model.CSystemSettings;
import sk.qbsw.sed.web.ui.CSedSession;
import sk.qbsw.sed.web.ui.components.CLengthTooltipAppender;

public class CUserDetailForm<T extends CUserDetailRecord> extends CStatelessForm<CUserDetailRecord> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String ONBLUR = "onblur";
	
	private boolean isModeDetail = false;

	private boolean isModeUserEdit = false;

	private boolean isSelectBoxSupervisor = false;

	private CImage image;

	private Boolean fileTooBig = false;

	@SpringBean
	private CSystemSettings settings;

	@SpringBean
	private IUserClientService userService;

	private CTextField<String> emailField;
	private CTextField<String> loginField;

	private CTextField<String> nameField;
	private boolean showVacation = false;

	public CUserDetailForm(String id, final IModel<CUserDetailRecord> userDetailModel, final boolean isEditmode, boolean isSelectBoxSupervisor) {
		super(id, userDetailModel);
		setOutputMarkupId(true);
		this.isSelectBoxSupervisor = isSelectBoxSupervisor;
		this.showVacation = true;
		createForm();
	}

	public CUserDetailForm(String id, final IModel<CUserDetailRecord> userDetailModel, final boolean isEditmode) {
		super(id, userDetailModel);
		setOutputMarkupId(true);
		createForm();
	}

	private void createForm() {

		CFeedbackPanel feedback = new CFeedbackPanel("feedbackForForm");
		setFeedbackPanel(feedback);
		add(feedback);

		nameField = new CTextField<String>("name", EDataType.TEXT) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		nameField.setRequired(true);
		nameField.add(new CLengthTooltipAppender(50));
		nameField.add(StringValidator.maximumLength(50));
		nameField.add(new AjaxFormComponentUpdatingBehavior(ONBLUR) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget target) {

				target.add(nameField);
				customTrimField(nameField);
			}

			@Override
			protected void onError(final AjaxRequestTarget target, RuntimeException e) {

				target.add(nameField);
				customTrimField(nameField);
			}
		});
		add(nameField);

		CTextField<String> surnameField = new CTextField<String>("surname", EDataType.TEXT) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		surnameField.setRequired(true);
		surnameField.add(new CLengthTooltipAppender(50));
		surnameField.add(StringValidator.maximumLength(50));
		surnameField.add(new AjaxFormComponentUpdatingBehavior(ONBLUR) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget target) {

				target.add(surnameField);
				customTrimField(surnameField);
			}

			@Override
			protected void onError(final AjaxRequestTarget target, RuntimeException e) {

				target.add(surnameField);
				customTrimField(surnameField);
			}
		});
		add(surnameField);

		loginField = new CTextField<String>("login", EDataType.TEXT) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail && CSedSession.get().getUser().getClientInfo().isQbsw();
			}
		};
		loginField.add(new CLengthTooltipAppender(20));
		loginField.add(StringValidator.maximumLength(20));
		loginField.setRequired(true);
		loginField.setOutputMarkupId(true);
		loginField.add(new AjaxFormComponentUpdatingBehavior(ONBLUR) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget target) {

				target.add(loginField);
				customTrimField(loginField);
			}

			@Override
			protected void onError(final AjaxRequestTarget target, RuntimeException e) {

				target.add(loginField);
				customTrimField(loginField);
			}
		});
		add(loginField);

		CTextField<String> phoneField = new CTextField<String>("phoneFix", EDataType.TEXT) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		phoneField.add(new CLengthTooltipAppender(50));
		phoneField.add(StringValidator.maximumLength(50));
		phoneField.add(new AjaxFormComponentUpdatingBehavior(ONBLUR) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget target) {

				target.add(phoneField);
				customTrimField(phoneField);
			}

			@Override
			protected void onError(final AjaxRequestTarget target, RuntimeException e) {

				target.add(phoneField);
				customTrimField(phoneField);
			}
		});
		add(phoneField);

		emailField = new CTextField<String>("email", EDataType.TEXT) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		emailField.add(new CLengthTooltipAppender(50));
		emailField.add(StringValidator.maximumLength(50));
		emailField.setRequired(true);
		emailField.add(EmailAddressValidator.getInstance());
		emailField.add(new AjaxFormComponentUpdatingBehavior("onkeyup") { // ked user pise email, tak sa automaticky doplni login

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget target) {

				target.add(loginField);
				setLoginField();
			}

			@Override
			protected void onError(final AjaxRequestTarget target, RuntimeException e) { // ked neprejde validacia stringu tak sa vola onError a nie onUpdate

				target.add(loginField);
				setLoginField();
			}
		});

		emailField.add(new AjaxFormComponentUpdatingBehavior(ONBLUR) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget target) {

				target.add(emailField);
				customTrimField(emailField);
			}

			@Override
			protected void onError(final AjaxRequestTarget target, RuntimeException e) {

				target.add(emailField);
				customTrimField(emailField);
			}
		});
		add(emailField);

		CTextField<String> userTypeField = new CTextField<>("userTypeString", EDataType.TEXT);
		userTypeField.setEnabled(false);
		userTypeField.add(new AjaxFormComponentUpdatingBehavior(ONBLUR) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget target) {

				target.add(userTypeField);
				customTrimField(userTypeField);
			}

			@Override
			protected void onError(final AjaxRequestTarget target, RuntimeException e) {

				target.add(userTypeField);
				customTrimField(userTypeField);
			}
		});
		add(userTypeField);

		CDropDownChoice<CZoneRecord> zoneField = new CDropDownChoice<CZoneRecord>("zoneField", CSedSession.get().getUser().getClientInfo().getZones()) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		zoneField.setNullValid(true);
		add(zoneField);

		CTextField<String> officeNumberField = new CTextField<String>("officeNumber", EDataType.TEXT) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		officeNumberField.add(new CLengthTooltipAppender(10));
		officeNumberField.add(StringValidator.maximumLength(10));
		officeNumberField.add(new AjaxFormComponentUpdatingBehavior(ONBLUR) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget target) {

				target.add(officeNumberField);
				customTrimField(officeNumberField);
			}

			@Override
			protected void onError(final AjaxRequestTarget target, RuntimeException e) {

				target.add(officeNumberField);
				customTrimField(officeNumberField);
			}
		});
		add(officeNumberField);

		CTextField<String> employeeCodeField = new CTextField<String>("employeeCode", EDataType.TEXT) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		employeeCodeField.add(new CLengthTooltipAppender(10));
		employeeCodeField.add(StringValidator.maximumLength(10));
		employeeCodeField.setRequired(true);
		employeeCodeField.add(new AjaxFormComponentUpdatingBehavior(ONBLUR) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget target) {

				target.add(employeeCodeField);
				customTrimField(employeeCodeField);
			}

			@Override
			protected void onError(final AjaxRequestTarget target, RuntimeException e) {

				target.add(employeeCodeField);
				customTrimField(employeeCodeField);
			}
		});
		add(employeeCodeField);

		CheckBox validBox = new CheckBox("isValid", new PropertyModel<Boolean>(getModel(), "isValid")) {

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

		CheckBox editTimeBox = new CheckBox("editTime", new PropertyModel<Boolean>(getModel(), "editTime")) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		add(editTimeBox);

		CheckBox alertnessWorkBox = new CheckBox("allowedAlertnessWork", new PropertyModel<Boolean>(getModel(), "allowedAlertnessWork")) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		add(alertnessWorkBox);

		List<CCodeListRecord> languageList = new ArrayList<>();
		languageList.add(getLanguageRecord(ILanguageConstant.ID_SK, ILanguageConstant.SK));
		languageList.add(getLanguageRecord(ILanguageConstant.ID_EN, ILanguageConstant.EN));

		CDropDownChoice<CCodeListRecord> languageField = new CDropDownChoice<CCodeListRecord>("languageField", languageList) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail || isModeUserEdit;
			}
		};
		languageField.setNullValid(false);
		add(languageField);

		CTextField<String> superiorNameField = new CTextField<>("superior", EDataType.TEXT);
		superiorNameField.setEnabled(false);
		if (!isSelectBoxSupervisor) {
			add(superiorNameField);
		}

		CTextField<String> superiorPositionField = new CTextField<>("superiorPosition", EDataType.TEXT);
		superiorPositionField.setEnabled(false);
		add(superiorPositionField);

		CTextField<String> noteField = new CTextField<String>("note", EDataType.TEXT) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		noteField.add(new CLengthTooltipAppender(1000));
		noteField.add(StringValidator.maximumLength(1000));
		noteField.add(new AjaxFormComponentUpdatingBehavior(ONBLUR) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget target) {

				target.add(noteField);
				customTrimField(noteField);
			}

			@Override
			protected void onError(final AjaxRequestTarget target, RuntimeException e) {

				target.add(noteField);
				customTrimField(noteField);
			}
		});
		add(noteField);

		image = new CImage("photo", CUserDetailForm.this.getModel().getObject().getPhotoId());
		image.setOutputMarkupId(true);
		add(image);

		final CFileUploadField userPhotoUpload = new CFileUploadField("photoUpload", new Model()) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}

			@Override
			public boolean isVisible() {
				return !isModeDetail;
			}
		};

		add(userPhotoUpload);
		// Enable multipart mode (need for uploads file)
		setMultiPart(true);
		// max upload size, 400k
		setMaxSize(Bytes.kilobytes(settings.getPhotoMaxSize()));

		ArrayList<CCodeListRecord> tableRowsList = new ArrayList<>();
		tableRowsList.add(new CCodeListRecord(10l, "10"));
		tableRowsList.add(new CCodeListRecord(15l, "15"));
		tableRowsList.add(new CCodeListRecord(20l, "20"));
		tableRowsList.add(new CCodeListRecord(50l, "50"));
		tableRowsList.add(new CCodeListRecord(100l, "100"));

		Model<CCodeListRecord> tableRowsModel = new Model<>();
		tableRowsModel.setObject(tableRowsList.get(0));

		CDropDownChoice<CCodeListRecord> tableRows = new CDropDownChoice<CCodeListRecord>("tableRowsField", tableRowsList) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail || isModeUserEdit;
			}
		};
		add(tableRows);

		// pole titul
		CTextField<String> degTitleField = new CTextField<String>("degTitle", EDataType.TEXT) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};

		degTitleField.add(new CLengthTooltipAppender(20));
		degTitleField.add(StringValidator.maximumLength(20));
		degTitleField.add(new AjaxFormComponentUpdatingBehavior(ONBLUR) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget target) {

				target.add(degTitleField);
				customTrimField(degTitleField);
			}

			@Override
			protected void onError(final AjaxRequestTarget target, RuntimeException e) {

				target.add(degTitleField);
				customTrimField(degTitleField);
			}
		});
		add(degTitleField);
		
		// Generovanie JIRA tokenu
		CheckBox jiraTokenGenerationCheckBox = new CheckBox("jiraTokenGeneration", new PropertyModel<>(getModel(), "jiraTokenGeneration")) {

			/**
			 * 
			 */
			private static final long serialVersionUID = -793430177505791053L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail || isModeUserEdit;
			}
			
		};
		add(jiraTokenGenerationCheckBox);

		if (showVacation) {
			CTextField<Date> birthDateField = new CTextField<Date>("birthDate", EDataType.DATE) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isEnabled() {
					return !isModeDetail;
				}
			};
			add(birthDateField);

			CTextField<Date> workStartDateField = new CTextField<Date>("workStartDate", EDataType.DATE) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isEnabled() {
					return !isModeDetail;
				}
			};
			add(workStartDateField);

			CTextField<Double> vacationField = new CTextField<Double>("vacation", EDataType.DOUBLE) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isEnabled() {
					return !isModeDetail;
				}
			};
			vacationField.add(RangeValidator.range(Double.valueOf(0), Double.valueOf(99)));
			add(vacationField);

			CTextField<Double> vacationNextYearField = new CTextField<Double>("vacationNextYear", EDataType.DOUBLE) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isEnabled() {
					return !isModeDetail;
				}
			};
			vacationNextYearField.add(RangeValidator.range(Double.valueOf(0), Double.valueOf(99)));
			add(vacationNextYearField);
		}

		// fieldy vytvoriť len pre administrátora
		if (isSelectBoxSupervisor) {

			// pole práca z domu
			CDropDownChoice<CCodeListRecord> homeOfficePermissionField = new CDropDownChoice<CCodeListRecord>("homeOfficePermissionField", getAllTypesOfHomeOfficePermission()) {
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isEnabled() {
					return !isModeDetail;
				}
			};
			add(homeOfficePermissionField);

			// pole pozícia
			CTextField<String> positionField = new CTextField<String>("position", EDataType.TEXT) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isEnabled() {
					return !isModeDetail;
				}
			};
			positionField.add(new CLengthTooltipAppender(100));
			positionField.add(StringValidator.maximumLength(100));
			positionField.add(new AjaxFormComponentUpdatingBehavior(ONBLUR) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(final AjaxRequestTarget target) {

					target.add(positionField);
					customTrimField(positionField);
				}

				@Override
				protected void onError(final AjaxRequestTarget target, RuntimeException e) {

					target.add(positionField);
					customTrimField(positionField);
				}
			});
			add(positionField);

			// pole telefón (mobil)
			CTextField<String> mobileField = new CTextField<String>("phoneMobile", EDataType.TEXT) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isEnabled() {
					return !isModeDetail;
				}
			};
			mobileField.add(new CLengthTooltipAppender(50));
			mobileField.add(StringValidator.maximumLength(50));
			mobileField.add(new AjaxFormComponentUpdatingBehavior(ONBLUR) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(final AjaxRequestTarget target) {

					target.add(mobileField);
					customTrimField(mobileField);
				}

				@Override
				protected void onError(final AjaxRequestTarget target, RuntimeException e) {

					target.add(mobileField);
					customTrimField(mobileField);
				}
			});
			add(mobileField);

			// pole ulica
			CTextField<String> streetField = new CTextField<String>("street", EDataType.TEXT) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isEnabled() {
					return !isModeDetail;
				}
			};
			streetField.add(new CLengthTooltipAppender(50));
			streetField.add(StringValidator.maximumLength(50));
			streetField.add(new AjaxFormComponentUpdatingBehavior(ONBLUR) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(final AjaxRequestTarget target) {

					target.add(streetField);
					customTrimField(streetField);
				}

				@Override
				protected void onError(final AjaxRequestTarget target, RuntimeException e) {

					target.add(streetField);
					customTrimField(streetField);
				}
			});
			add(streetField);

			// pole číslo domu
			CTextField<String> streetNumField = new CTextField<String>("streetNumber", EDataType.TEXT) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isEnabled() {
					return !isModeDetail;
				}
			};
			streetNumField.add(new CLengthTooltipAppender(10));
			streetNumField.add(StringValidator.maximumLength(10));
			streetNumField.add(new AjaxFormComponentUpdatingBehavior(ONBLUR) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(final AjaxRequestTarget target) {

					target.add(streetNumField);
					customTrimField(streetNumField);
				}

				@Override
				protected void onError(final AjaxRequestTarget target, RuntimeException e) {

					target.add(streetNumField);
					customTrimField(streetNumField);
				}
			});
			add(streetNumField);

			// pole mesto
			CTextField<String> cityField = new CTextField<String>("city", EDataType.TEXT) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isEnabled() {
					return !isModeDetail;
				}
			};
			cityField.add(new CLengthTooltipAppender(50));
			cityField.add(StringValidator.maximumLength(50));
			cityField.add(new AjaxFormComponentUpdatingBehavior(ONBLUR) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(final AjaxRequestTarget target) {

					target.add(cityField);
					customTrimField(cityField);
				}

				@Override
				protected void onError(final AjaxRequestTarget target, RuntimeException e) {

					target.add(cityField);
					customTrimField(cityField);
				}
			});
			add(cityField);

			// pole PSČ
			CTextField<String> zipField = new CTextField<String>("zip", EDataType.TEXT) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isEnabled() {
					return !isModeDetail;
				}
			};
			zipField.add(new CLengthTooltipAppender(10));
			zipField.add(StringValidator.maximumLength(10));
			zipField.add(new AjaxFormComponentUpdatingBehavior(ONBLUR) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(final AjaxRequestTarget target) {

					target.add(zipField);
					customTrimField(zipField);
				}

				@Override
				protected void onError(final AjaxRequestTarget target, RuntimeException e) {

					target.add(zipField);
					customTrimField(zipField);
				}
			});
			add(zipField);

			// pole rodné číslo
			CTextField<String> personalIdNumberField = new CTextField<String>("personalIdNumber", EDataType.TEXT) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isEnabled() {
					return !isModeDetail;
				}
			};
			personalIdNumberField.add(new CLengthTooltipAppender(20));
			personalIdNumberField.add(StringValidator.maximumLength(20));
			personalIdNumberField.add(new AjaxFormComponentUpdatingBehavior(ONBLUR) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(final AjaxRequestTarget target) {

					target.add(personalIdNumberField);
					customTrimField(personalIdNumberField);
				}

				@Override
				protected void onError(final AjaxRequestTarget target, RuntimeException e) {

					target.add(personalIdNumberField);
					customTrimField(personalIdNumberField);
				}
			});
			add(personalIdNumberField);

			// pole IČO
			CTextField<String> crnField = new CTextField<String>("crn", EDataType.TEXT) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isEnabled() {
					return !isModeDetail;
				}
			};
			crnField.add(new CLengthTooltipAppender(20));
			crnField.add(StringValidator.maximumLength(20));
			crnField.add(new AjaxFormComponentUpdatingBehavior(ONBLUR) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(final AjaxRequestTarget target) {

					target.add(crnField);
					customTrimField(crnField);
				}

				@Override
				protected void onError(final AjaxRequestTarget target, RuntimeException e) {

					target.add(crnField);
					customTrimField(crnField);
				}
			});
			add(crnField);

			// pole DIČ
			CTextField<String> vatinField = new CTextField<String>("vatin", EDataType.TEXT) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isEnabled() {
					return !isModeDetail;
				}
			};
			vatinField.add(new CLengthTooltipAppender(20));
			vatinField.add(StringValidator.maximumLength(20));
			vatinField.add(new AjaxFormComponentUpdatingBehavior(ONBLUR) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(final AjaxRequestTarget target) {

					target.add(vatinField);
					customTrimField(vatinField);
				}

				@Override
				protected void onError(final AjaxRequestTarget target, RuntimeException e) {

					target.add(vatinField);
					customTrimField(vatinField);
				}
			});
			add(vatinField);

			// pole typ pracovného pomeru
			CDropDownChoice<CCodeListRecord> typeOfEmploymentField = new CDropDownChoice<CCodeListRecord>("typeOfEmploymentField", getAllTypesOfEmployment()) {
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isEnabled() {
					return !isModeDetail;
				}
			};
			add(typeOfEmploymentField);

			// pole dátum konca pracovného pomeru
			CTextField<Date> workEndDateField = new CTextField<Date>("workEndDate", EDataType.DATE) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isEnabled() {
					return !isModeDetail;
				}
			};
			add(workEndDateField);

			// pole číslo OP
			CTextField<String> residentIdCardNumField = new CTextField<String>("residentIdCardNum", EDataType.TEXT) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isEnabled() {
					return !isModeDetail;
				}
			};
			residentIdCardNumField.add(new CLengthTooltipAppender(20));
			residentIdCardNumField.add(StringValidator.maximumLength(20));
			residentIdCardNumField.add(new AjaxFormComponentUpdatingBehavior(ONBLUR) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(final AjaxRequestTarget target) {

					target.add(residentIdCardNumField);
					customTrimField(residentIdCardNumField);
				}

				@Override
				protected void onError(final AjaxRequestTarget target, RuntimeException e) {

					target.add(residentIdCardNumField);
					customTrimField(residentIdCardNumField);
				}
			});
			add(residentIdCardNumField);

			// pole zdravotná poisťovňa
			CTextField<String> healthInsurCompField = new CTextField<String>("healthInsurComp", EDataType.TEXT) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isEnabled() {
					return !isModeDetail;
				}
			};
			healthInsurCompField.add(new CLengthTooltipAppender(50));
			healthInsurCompField.add(StringValidator.maximumLength(50));
			healthInsurCompField.add(new AjaxFormComponentUpdatingBehavior(ONBLUR) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(final AjaxRequestTarget target) {

					target.add(healthInsurCompField);
					customTrimField(healthInsurCompField);
				}

				@Override
				protected void onError(final AjaxRequestTarget target, RuntimeException e) {

					target.add(healthInsurCompField);
					customTrimField(healthInsurCompField);
				}
			});
			add(healthInsurCompField);

			// pole číslo účtu
			CTextField<String> bankAccountNumberField = new CTextField<String>("bankAccountNumber", EDataType.TEXT) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isEnabled() {
					return !isModeDetail;
				}
			};
			bankAccountNumberField.add(new CLengthTooltipAppender(34));
			bankAccountNumberField.add(StringValidator.maximumLength(34));
			bankAccountNumberField.add(new AjaxFormComponentUpdatingBehavior(ONBLUR) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(final AjaxRequestTarget target) {

					target.add(bankAccountNumberField);
					customTrimField(bankAccountNumberField);
				}

				@Override
				protected void onError(final AjaxRequestTarget target, RuntimeException e) {

					target.add(bankAccountNumberField);
					customTrimField(bankAccountNumberField);
				}
			});
			add(bankAccountNumberField);

			// pole banka
			CTextField<String> bankInstitutionField = new CTextField<String>("bankInstitution", EDataType.TEXT) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isEnabled() {
					return !isModeDetail;
				}
			};
			bankInstitutionField.add(new CLengthTooltipAppender(50));
			bankInstitutionField.add(StringValidator.maximumLength(50));
			bankInstitutionField.add(new AjaxFormComponentUpdatingBehavior(ONBLUR) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(final AjaxRequestTarget target) {

					target.add(bankInstitutionField);
					customTrimField(bankInstitutionField);
				}

				@Override
				protected void onError(final AjaxRequestTarget target, RuntimeException e) {

					target.add(bankInstitutionField);
					customTrimField(bankInstitutionField);
				}
			});
			add(bankInstitutionField);

			// pole štát
			CTextField<String> countryField = new CTextField<String>("country", EDataType.TEXT) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isEnabled() {
					return !isModeDetail;
				}
			};
			countryField.add(new CLengthTooltipAppender(50));
			countryField.add(StringValidator.maximumLength(50));
			countryField.add(new AjaxFormComponentUpdatingBehavior(ONBLUR) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(final AjaxRequestTarget target) {

					target.add(countryField);
					customTrimField(countryField);
				}

				@Override
				protected void onError(final AjaxRequestTarget target, RuntimeException e) {

					target.add(countryField);
					customTrimField(countryField);
				}
			});
			add(countryField);

			// pole miesto narodenia
			CTextField<String> birthPlaceField = new CTextField<String>("birthPlace", EDataType.TEXT) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isEnabled() {
					return !isModeDetail;
				}
			};
			birthPlaceField.add(new CLengthTooltipAppender(50));
			birthPlaceField.add(StringValidator.maximumLength(50));
			birthPlaceField.add(new AjaxFormComponentUpdatingBehavior(ONBLUR) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(final AjaxRequestTarget target) {

					target.add(birthPlaceField);
					customTrimField(birthPlaceField);
				}

				@Override
				protected void onError(final AjaxRequestTarget target, RuntimeException e) {

					target.add(birthPlaceField);
					customTrimField(birthPlaceField);
				}
			});
			add(birthPlaceField);

			CheckBox absentCheckBox = new CheckBox("absentCheck", new PropertyModel<Boolean>(getModel(), "absentCheck")) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isEnabled() {
					return !isModeDetail;
				}
			};
			add(absentCheckBox);

			CheckBox criminalRecordsCheckBox = new CheckBox("criminalRecords", new PropertyModel<Boolean>(getModel(), "criminalRecords")) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isEnabled() {
					return !isModeDetail;
				}
			};
			add(criminalRecordsCheckBox);

			CheckBox recMedicalCheckCheckBox = new CheckBox("recMedicalCheck", new PropertyModel<Boolean>(getModel(), "recMedicalCheck")) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isEnabled() {
					return !isModeDetail;
				}
			};
			add(recMedicalCheckCheckBox);

			CheckBox multisportCardCheckBox = new CheckBox("multisportCard", new PropertyModel<Boolean>(getModel(), "multisportCard")) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isEnabled() {
					return !isModeDetail;
				}
			};
			add(multisportCardCheckBox);
		}
	}

	@Override
	protected void onFileUploadException(FileUploadException e, Map<String, Object> arg1) {

		if (e instanceof SizeLimitExceededException) {
			fileTooBig = true;
			error(MessageFormat.format(getString("userForm.uploadTooLarge"), settings.getPhotoMaxSize()));
		}
	}

	public boolean isModeDetail() {
		return isModeDetail;
	}

	public void setModeDetail(boolean isModeDetail) {
		this.isModeDetail = isModeDetail;
	}

	public void setModeUserEdit(boolean isModeUserEdit) {
		this.isModeUserEdit = isModeUserEdit;
	}

	private CCodeListRecord getLanguageRecord(Long id, String name) {
		CCodeListRecord record = new CCodeListRecord();
		record.setId(id);
		record.setName(name);
		return record;
	}

	public CImage getImage() {
		return image;
	}

	private void setLoginField() {
		String email = emailField.getValue();
		loginField.getFeedbackMessages().clear();

		if (CSedSession.get().getUser().getClientInfo().isQbsw()) {
			if (email.contains("@")) {
				String emailSubstring = email.substring(0, email.indexOf('@'));
				loginField.setModelObject(emailSubstring);
				customTrimField(loginField);
			}
		} else {
			loginField.setModelObject(email);
			customTrimField(loginField);
		}
	}

	private void customTrimField(CTextField<String> textField) {
		String textFieldValue = textField.getValue();
		textField.getFeedbackMessages().clear();
		textField.setModelObject(textFieldValue.trim());
	}

	public Boolean isFileTooBig() {
		return fileTooBig;
	}

	public void setFileTooBig(Boolean fileTooBig) {
		this.fileTooBig = fileTooBig;
	}

	public List<CCodeListRecord> getAllTypesOfEmployment() {

		List<CCodeListRecord> allTypesOfEmployment = null;

		try {
			allTypesOfEmployment = userService.getAllTypesOfEmployment();
		} catch (CBussinessDataException e) {
			CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
		}

		return allTypesOfEmployment;
	}

	public List<CCodeListRecord> getAllTypesOfHomeOfficePermission() {

		List<CCodeListRecord> allTypesOfHomeOfficePermissions = null;

		try {
			allTypesOfHomeOfficePermissions = userService.getAllTypesOfHomeOfficePermission();
		} catch (CBussinessDataException e) {
			CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
		}

		return allTypesOfHomeOfficePermissions;
	}
}
