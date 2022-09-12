package sk.qbsw.sed.panel.activitylimits;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;

import sk.qbsw.sed.client.exception.CSystemFailureException;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.restriction.CGroupsAIData;
import sk.qbsw.sed.communication.service.IActivityClientService;
import sk.qbsw.sed.communication.service.IProjectClientService;
import sk.qbsw.sed.fw.component.EDataType;
import sk.qbsw.sed.fw.component.form.CStatelessForm;
import sk.qbsw.sed.fw.component.form.input.CDropDownChoice;
import sk.qbsw.sed.fw.component.form.input.CTextField;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.web.ui.components.CLengthTooltipAppender;

public class CActivityRGroupForm<T extends CGroupsAIData> extends CStatelessForm<CGroupsAIData> {

	/** serial uid */
	private static final long serialVersionUID = 1L;

	@SpringBean
	private IActivityClientService activityeService;

	private boolean isModeDetail = false;

	@SpringBean
	private IProjectClientService projectService;

	/**
	 *
	 * @param id            - wicket id
	 * @param activityModel - model of form with CActivityRecord
	 * @param actualPanel   - panel that renders after cancel button action
	 */
	public CActivityRGroupForm(String id, final IModel<CGroupsAIData> activityModel, Panel actualPanel, final boolean isEditMode) {
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

		try {
			activityTypeList = activityeService.getValidRecordsForLimits();
		} catch (CBussinessDataException e) {
			CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
			error(getString(e.getModel().getServerCode()));
		}

		CDropDownChoice<CCodeListRecord> typeField = new CDropDownChoice<CCodeListRecord>("activityType", activityTypeList) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		typeField.setNullValid(true);
		typeField.setRequired(true);
		add(typeField);

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

		List<String> projectGroupsOptions = new ArrayList<>();
		try {
			for (CCodeListRecord code : projectService.getAllRecordsWithGroups().getProjectGroups()) {
				projectGroupsOptions.add(code.getName());
			}
		} catch (CBussinessDataException e) {
			Logger.getLogger(CActivityRGroupForm.class).error(e);
			throw new CSystemFailureException(e);
		}

		final CDropDownChoice<String> projectGroupsChoice = new CDropDownChoice<String>("projectGroup", projectGroupsOptions) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		projectGroupsChoice.setNullValid(true);
		add(projectGroupsChoice);
	}

	public void setModeDetail(boolean isModeDetail) {
		this.isModeDetail = isModeDetail;
	}
}
