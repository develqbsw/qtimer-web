package sk.qbsw.sed.panel.projects;

import java.util.Date;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.RangeValidator;
import org.apache.wicket.validation.validator.StringValidator;

import sk.qbsw.sed.client.model.codelist.CProjectRecord;
import sk.qbsw.sed.fw.component.EDataType;
import sk.qbsw.sed.fw.component.form.CStatelessForm;
import sk.qbsw.sed.fw.component.form.input.CTextField;
import sk.qbsw.sed.web.ui.components.CLengthTooltipAppender;

public class CProjectForm<T extends CProjectRecord> extends CStatelessForm<CProjectRecord> {

	/** serial uid */
	private static final long serialVersionUID = 1L;

	private boolean isModeDetail = false;

	/**
	 *
	 * @param id           - wicket id
	 * @param projectModel - model of form with CActivityRecord
	 * @param actualPanel  - panel that renders after cancel button action
	 */
	public CProjectForm(String id, final IModel<CProjectRecord> projectModel, Panel actualPanel, final boolean isEditMode) {
		super(id, projectModel);
		setOutputMarkupId(true);

		final CTextField<String> group = new CTextField<String>("group", EDataType.TEXT) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		group.add(new CLengthTooltipAppender(50));
		group.add(StringValidator.maximumLength(50));
		group.setRequired(true);
		group.setOutputMarkupId(true);
		add(group);

		final CTextField<String> code = new CTextField<String>("code", EDataType.TEXT) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		code.add(new CLengthTooltipAppender(50));
		code.add(StringValidator.maximumLength(50));
		code.setRequired(true);
		code.setOutputMarkupId(true);
		add(code);

		final CTextField<String> name = new CTextField<String>("name", EDataType.TEXT) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		name.add(new AjaxFormComponentUpdatingBehavior("onchange") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {

				String nameInput = name.getDefaultModelObjectAsString();

				if (nameInput != null) {
					int separatorIndex = nameInput.indexOf(".");
					if (separatorIndex != -1) {
						group.setDefaultModelObject(nameInput.substring(0, separatorIndex));
						nameInput = nameInput.length() - 1 > separatorIndex ? nameInput.substring(separatorIndex + 1) : nameInput.substring(separatorIndex);
						target.add(group);
					}
					separatorIndex = nameInput.indexOf(" - ");
					if (separatorIndex != -1) {
						code.setDefaultModelObject(nameInput.substring(0, separatorIndex));
						target.add(code);
					}
				}
			}
		});
		name.add(new CLengthTooltipAppender(250));
		name.add(StringValidator.maximumLength(250));
		name.setRequired(true);
		add(name);

		CheckBox valid = new CheckBox("valid", new PropertyModel<Boolean>(getModel(), "active")) {

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

				if (CProjectForm.this.getModelObject().getId() == null) {
					this.setDefaultModelObject(true);
				}
			}
		};
		add(valid);

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
	}

	public void setModeDetail(boolean isModeDetail) {
		this.isModeDetail = isModeDetail;
	}
}
