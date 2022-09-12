package sk.qbsw.sed.panel.holidays;

import java.util.Date;

import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.StringValidator;

import sk.qbsw.sed.client.model.codelist.CHolidayRecord;
import sk.qbsw.sed.fw.component.EDataType;
import sk.qbsw.sed.fw.component.form.CStatelessForm;
import sk.qbsw.sed.fw.component.form.input.CTextField;
import sk.qbsw.sed.web.ui.components.CLengthTooltipAppender;

public class CHolidayForm<T extends CHolidayRecord> extends CStatelessForm<CHolidayRecord> {

	/** serial uid */
	private static final long serialVersionUID = 1L;

	private boolean isModeDetail = false;

	private CheckBox valid;

	public void setValid(Boolean value) {
		this.valid.setOutputMarkupId(value);
	}

	/**
	 *
	 * @param id           - wicket id
	 * @param projectModel - model of form with CActivityRecord
	 * @param actualPanel  - panel that renders after cancel button action
	 */
	public CHolidayForm(String id, final IModel<CHolidayRecord> projectModel, Panel actualPanel, final boolean isEditMode) {
		super(id, projectModel);
		setOutputMarkupId(true);

		CTextField<String> description = new CTextField<String>("description", EDataType.TEXT) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		description.add(new CLengthTooltipAppender(100));
		description.add(StringValidator.maximumLength(100));
		description.setRequired(true);
		add(description);

		CTextField<Date> date = new CTextField<Date>("day", EDataType.DATE) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		date.setRequired(true);
		add(date);

		valid = new CheckBox("valid", new PropertyModel<Boolean>(getModel(), "active")) {

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

				if (CHolidayForm.this.getModelObject().getId() == null) {
					this.setDefaultModelObject(true);
				}
			}
		};
		add(valid);

		CTextField<String> idField = new CTextField<>("id", EDataType.NUMBER);
		idField.setEnabled(false);
		add(idField);
	}

	public void setModeDetail(boolean isModeDetail) {
		this.isModeDetail = isModeDetail;
	}
}
