package sk.qbsw.sed.panel.clientdetail;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;

import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.detail.CClientDetailRecord;
import sk.qbsw.sed.communication.service.ILegalFormClientService;
import sk.qbsw.sed.fw.component.EDataType;
import sk.qbsw.sed.fw.component.form.CStatelessForm;
import sk.qbsw.sed.fw.component.form.input.CDropDownChoice;
import sk.qbsw.sed.fw.component.form.input.CTextField;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.fw.utils.CStringResourceReader;
import sk.qbsw.sed.web.ui.components.CLengthTooltipAppender;

public class CClientDetailForm<T extends CClientDetailRecord> extends CStatelessForm<CClientDetailRecord> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean isModeDetail = false;

	@SpringBean
	private ILegalFormClientService legalFormService;

	public CClientDetailForm(String id, final IModel<CClientDetailRecord> requestReasonModel, Panel actualPanel, final boolean isEditMode) {
		super(id, requestReasonModel);
		setOutputMarkupId(true);

		CTextField<String> nameField = new CTextField<String>("name", EDataType.TEXT) {

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
		add(nameField);
		nameField.add(new CLengthTooltipAppender(100));
		nameField.add(StringValidator.maximumLength(100));

		CTextField<String> abbrevField = new CTextField<String>("nameShort", EDataType.TEXT) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		abbrevField.setRequired(true);
		add(abbrevField);
		abbrevField.add(new CLengthTooltipAppender(100));
		abbrevField.add(StringValidator.maximumLength(100));

		List<CCodeListRecord> legalFormList = new ArrayList<>();

		try {
			legalFormList = legalFormService.getValidRecords();
		} catch (CBussinessDataException e) {
			CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
			error(getString(e.getModel().getServerCode()));
		}

		CDropDownChoice<CCodeListRecord> legalFormField = new CDropDownChoice<CCodeListRecord>("legalForm", legalFormList) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		legalFormField.setNullValid(true);
		legalFormField.setRequired(true);
		add(legalFormField);

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
		streetField.setRequired(true);
		add(streetField);
		streetField.add(new CLengthTooltipAppender(100));
		streetField.add(StringValidator.maximumLength(100));

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
		add(streetNumField);
		streetNumField.add(new CLengthTooltipAppender(10));
		streetNumField.add(StringValidator.maximumLength(10));

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
		cityField.setRequired(true);
		add(cityField);
		cityField.add(new CLengthTooltipAppender(50));
		cityField.add(StringValidator.maximumLength(50));

		CTextField<String> zipField = new CTextField<String>("zipCode", EDataType.TEXT) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		zipField.setRequired(true);
		add(zipField);
		zipField.add(new CLengthTooltipAppender(10));
		zipField.add(StringValidator.maximumLength(10));

		CTextField<String> emailTimeField = new CTextField<String>("timeAutoEmail", EDataType.TIME) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		add(emailTimeField);

		List<CCodeListRecord> languageList = new ArrayList<>();
		languageList.add(new CCodeListRecord(0l, "sk"));
		languageList.add(new CCodeListRecord(1l, "en"));

		CDropDownChoice<CCodeListRecord> languageField = new CDropDownChoice<CCodeListRecord>("languageField", languageList) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		languageField.setNullValid(true);
		add(languageField);

		List<CCodeListRecord> shiftDayList = new ArrayList<>();
		shiftDayList.add(new CCodeListRecord(0l, CStringResourceReader.read("client.detail.form.shiftday.current")));
		shiftDayList.add(new CCodeListRecord(-1l, CStringResourceReader.read("client.detail.form.shiftday.before")));

		CDropDownChoice<CCodeListRecord> shiftDayField = new CDropDownChoice<CCodeListRecord>("shiftDayField", shiftDayList) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		shiftDayField.setNullValid(false);
		add(shiftDayField);

		CTextField<String> generateTimeField = new CTextField<String>("timeAutoGenerateTimestamps", EDataType.TIME) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		add(generateTimeField);

		CTextField<String> stopWorkField = new CTextField<String>("intervalStopWorkRec", EDataType.TIME) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		add(stopWorkField);

		CheckBox generateMessages = new CheckBox("generateMessages", new PropertyModel<Boolean>(getModel(), "generateMessages")) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		add(generateMessages);

		CTextField<String> bonusVacationField = new CTextField<String>("bonusVacation", EDataType.TEXT) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		add(bonusVacationField);
		bonusVacationField.add(new CLengthTooltipAppender(100));
		bonusVacationField.add(StringValidator.maximumLength(100));
	}

	public boolean isModeDetail() {
		return isModeDetail;
	}

	public void setModeDetail(boolean isModeDetail) {
		this.isModeDetail = isModeDetail;
	}
}
