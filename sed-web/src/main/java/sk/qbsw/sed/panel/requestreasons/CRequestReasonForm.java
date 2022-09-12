package sk.qbsw.sed.panel.requestreasons;

import java.util.List;

import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;

import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.restriction.CRequestReasonData;
import sk.qbsw.sed.communication.service.IRequestTypeClientService;
import sk.qbsw.sed.fw.component.EDataType;
import sk.qbsw.sed.fw.component.form.CStatelessForm;
import sk.qbsw.sed.fw.component.form.input.CDropDownChoice;
import sk.qbsw.sed.fw.component.form.input.CTextField;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.web.ui.components.CLengthTooltipAppender;

public class CRequestReasonForm<T extends CRequestReasonData> extends CStatelessForm<CRequestReasonData> {

	/** serial uid */
	private static final long serialVersionUID = 1L;

	@SpringBean
	private IRequestTypeClientService requestTypeService;

	private boolean isModeDetail = false;

	/**
	 *
	 * @param id                 - wicket id
	 * @param requestReasonModel - model of form with CRequestReasonRecord
	 * @param actualPanel        - panel that renders after cancel button action
	 */
	public CRequestReasonForm(String id, final IModel<CRequestReasonData> requestReasonModel, Panel actualPanel, final boolean isEditMode) {
		super(id, requestReasonModel);
		setOutputMarkupId(true);

		List<CCodeListRecord> requestTypeList = null;

		try {
			requestTypeList = requestTypeService.getValidRecordsForRequestReason();
		} catch (CBussinessDataException e) {
			CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
			error(getString(e.getModel().getServerCode()));
		}

		CDropDownChoice<CCodeListRecord> typeField = new CDropDownChoice<CCodeListRecord>("requestType", requestTypeList) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		typeField.setNullValid(true);
		typeField.setRequired(true);
		add(typeField);

		CTextField<String> codeField = new CTextField<String>("code", EDataType.TEXT) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		codeField.add(new CLengthTooltipAppender(20));
		codeField.add(StringValidator.maximumLength(20));
		codeField.setRequired(true);
		add(codeField);

		CTextField<String> descriptionField = new CTextField<String>("name", EDataType.TEXT) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		descriptionField.add(new CLengthTooltipAppender(200));
		descriptionField.add(StringValidator.maximumLength(200));
		descriptionField.setRequired(true);
		add(descriptionField);

		CheckBox validBox = new CheckBox("valid", new PropertyModel<Boolean>(getModel(), "valid")) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}

			@Override
			protected void onBeforeRender() {
				super.onBeforeRender();

				if (CRequestReasonForm.this.getModelObject().getId() == null) {
					this.setDefaultModelObject(true);
				}
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
}
