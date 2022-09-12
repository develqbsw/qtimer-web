package sk.qbsw.sed.panel.activities;

import java.util.Date;

import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.RangeValidator;
import org.apache.wicket.validation.validator.StringValidator;

import sk.qbsw.sed.client.model.codelist.CActivityRecord;
import sk.qbsw.sed.fw.component.EDataType;
import sk.qbsw.sed.fw.component.form.CStatelessForm;
import sk.qbsw.sed.fw.component.form.input.CTextField;
import sk.qbsw.sed.web.ui.components.CLengthTooltipAppender;

public class CActivityForm<T extends CActivityRecord> extends CStatelessForm<CActivityRecord> {

	/** serial uid */
	private static final long serialVersionUID = 1L;

	private boolean isModeDetail = false;

	/**
	 *
	 * @param id            - wicket id
	 * @param activityModel - model of form with CActivityRecord
	 * @param actualPanel   - panel that renders after cancel button action
	 */
	public CActivityForm(String id, final IModel<CActivityRecord> activityModel, Panel actualPanel, final boolean isEditMode) {
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
		name.add(new CLengthTooltipAppender(100));
		name.add(StringValidator.maximumLength(100));
		name.setRequired(true);
		add(name);

		CheckBox active = new CheckBox("active", new PropertyModel<Boolean>(getModel(), "active")) {

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

				if (CActivityForm.this.getModelObject().getId() == null) {
					this.setDefaultModelObject(true);
				}
			}
		};
		add(active);

		CheckBox flagDefault = new CheckBox("flagDefault", new PropertyModel<Boolean>(getModel(), "flagDefault")) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		add(flagDefault);

		CTextField<Integer> order = new CTextField<Integer>("order", EDataType.NUMBER) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		order.add(RangeValidator.range(0, 1000));
		add(order);

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
		add(note);

		CTextField<String> changedBy = new CTextField<>("changedBy", EDataType.TEXT);
		changedBy.setEnabled(false);
		add(changedBy);

		CTextField<Date> changeTime = new CTextField<>("changeTime", EDataType.DATE_TIME);
		changeTime.setEnabled(false);
		add(changeTime);

		CTextField<String> timeMin = new CTextField<String>("timeMin", EDataType.TIME) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		add(timeMin);

		CTextField<String> timeMax = new CTextField<String>("timeMax", EDataType.TIME) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		add(timeMax);

		CTextField<String> hoursMax = new CTextField<String>("hoursMax", EDataType.NUMBER) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		hoursMax.add(new RangeValidator<Integer>(1, Integer.MAX_VALUE));
		add(hoursMax);

		CheckBox flagExport = new CheckBox("flagExport", new PropertyModel<Boolean>(getModel(), "flagExport")) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		add(flagExport);

		CheckBox flagSum = new CheckBox("flagSum", new PropertyModel<Boolean>(getModel(), "flagSum")) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		add(flagSum);
	}

	public void setModeDetail(boolean isModeDetail) {
		this.isModeDetail = isModeDetail;
	}
}
