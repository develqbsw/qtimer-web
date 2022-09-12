package sk.qbsw.sed.web.ui.components.panel;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.form.select.Select;
import org.apache.wicket.extensions.markup.html.form.select.SelectOptions;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.StringValidator;

import sk.qbsw.sed.client.model.ISearchConstants;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.component.renderer.CSelectRecordRenderer;
import sk.qbsw.sed.fw.component.EDataType;
import sk.qbsw.sed.fw.component.form.input.CTextField;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.web.ui.components.IAjaxCommand;

/**
 *
 */
public class CMassEditDialogPanel extends CPanel {
	private static final long serialVersionUID = 1L;

	private static final String LABEL = "label";
	
	private IAjaxCommand action;

	private Select<CCodeListRecord> activity;
	private Select<CCodeListRecord> project;
	private CTextField<String> phase;
	private TextArea<String> note;

	private AjaxCheckBox activityCheckBox;
	private AjaxCheckBox projectCheckBox;
	private AjaxCheckBox phaseCheckBox;
	private AjaxCheckBox noteCheckBox;

	private Form<ChooseDefaultTimestampFormModel> form;
	
	private WebMarkupContainer activityContainer;
	private WebMarkupContainer projectContainer;
	
	/**
	 * 
	 */
	public CMassEditDialogPanel(String id, final CModalBorder parentWindow, List<CCodeListRecord> activityList, List<CCodeListRecord> projectList) {
		super(id);

		form = new Form<>("form", new Model<ChooseDefaultTimestampFormModel>(new ChooseDefaultTimestampFormModel()));
		add(form);

		activityContainer = new WebMarkupContainer("activityContainer1");
		activityContainer.setOutputMarkupPlaceholderTag(true);
		add(activityContainer);

		activity = new Select<CCodeListRecord>("activity1", new PropertyModel<CCodeListRecord>(form.getModel(), "activity")) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return activityCheckBox.getModelObject().booleanValue();
			}
		};

		initActivitySelect(activityList);

		activity.setOutputMarkupId(true);
		
		activityContainer.add(activity);
		form.add(activityContainer);
		
		activityCheckBox = new AjaxCheckBox("activityCheckBox", new PropertyModel<Boolean>(form.getModel(), "activityChecked")) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				if (activityCheckBox.getModelObject().booleanValue()) {
					activityContainer.setEnabled(true);
					target.add(activityContainer);
					target.appendJavaScript("$('.search-select-activities2').select2({placeholder: '',allowClear: false});");
				} else {
					activityContainer.setEnabled(false);
				}
				target.add(activityContainer);
			}

			@Override
			public boolean isEnabled() {
				return true;
			}
		};

		activityCheckBox.setModelObject(Boolean.FALSE);
		activityCheckBox.setOutputMarkupId(true);
		form.add(activityCheckBox);

		projectContainer = new WebMarkupContainer("projectContainer1");
		projectContainer.setOutputMarkupPlaceholderTag(true);
		add(projectContainer);
		
		project = new Select<CCodeListRecord>("project1", new PropertyModel<CCodeListRecord>(form.getModel(), "project")) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return projectCheckBox.getModelObject().booleanValue();
			}
		};
		
		initProjectSelect(projectList);

		project.setOutputMarkupId(true);

		projectContainer.add(project);
		form.add(projectContainer);

		projectCheckBox = new AjaxCheckBox("projectCheckBox", new PropertyModel<Boolean>(form.getModel(), "projectChecked")) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				if (projectCheckBox.getModelObject().booleanValue()) {
					projectContainer.setEnabled(true);
					target.add(projectContainer);
					target.appendJavaScript("$('.search-select-projects2').select2({placeholder: '',allowClear: false});");
				} else {
					projectContainer.setEnabled(false);
				}
				target.add(projectContainer);
			}

			@Override
			public boolean isEnabled() {
				return true;
			}
		};

		projectCheckBox.setModelObject(Boolean.FALSE);
		projectCheckBox.setOutputMarkupId(true);
		form.add(projectCheckBox);

		phase = new CTextField<>("phase", new PropertyModel<String>(form.getModel(), "phase"), EDataType.TEXT);
		phase.add(StringValidator.maximumLength(30));
		phase.setOutputMarkupId(true);
		phase.setEnabled(false);
		form.add(phase);

		phaseCheckBox = new AjaxCheckBox("phaseCheckBox", new PropertyModel<Boolean>(form.getModel(), "phaseChecked")) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				if (phaseCheckBox.getModelObject().booleanValue()) {
					phase.setEnabled(true);
				} else {
					phase.setEnabled(false);
				}
				target.add(phase);
			}

			@Override
			public boolean isEnabled() {
				return true;
			}
		};

		phaseCheckBox.setModelObject(Boolean.FALSE);
		phaseCheckBox.setOutputMarkupId(true);
		form.add(phaseCheckBox);

		note = new TextArea<>("note", new PropertyModel<String>(form.getModel(), "note"));
		note.setOutputMarkupId(true);
		note.setEnabled(false);
		form.add(note);

		noteCheckBox = new AjaxCheckBox("noteCheckBox", new PropertyModel<Boolean>(form.getModel(), "noteChecked")) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				if (noteCheckBox.getModelObject().booleanValue()) {
					note.setEnabled(true);
				} else {
					note.setEnabled(false);
				}
				target.add(note);
			}

			@Override
			public boolean isEnabled() {
				return true;
			}
		};

		noteCheckBox.setModelObject(Boolean.FALSE);
		noteCheckBox.setOutputMarkupId(true);
		form.add(noteCheckBox);

		form.add(new AjaxFallbackButton("btnOk", form) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form1) {
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
	 * 
	 */
	public void setAction(IAjaxCommand action) {
		this.action = action;
	}

	public Form<ChooseDefaultTimestampFormModel> getForm() {
		return form;
	}

	public void setForm(Form<ChooseDefaultTimestampFormModel> form) {
		this.form = form;
	}
	
	private void initActivitySelect(List<CCodeListRecord> activityList) {
		List<CCodeListRecord> myActivitiesList = new ArrayList<>();
		List<CCodeListRecord> lastUsedActivitiesList = new ArrayList<>();
		List<CCodeListRecord> allActivitiesList = new ArrayList<>();

		setActivitiesList(activityList, myActivitiesList, allActivitiesList, lastUsedActivitiesList);

		SelectOptions<CCodeListRecord> myActivities = new SelectOptions<>("myActivity", myActivitiesList, new CSelectRecordRenderer());
		SelectOptions<CCodeListRecord> lastUsedActivities = new SelectOptions<>("lastUsedActivity", lastUsedActivitiesList, new CSelectRecordRenderer());
		SelectOptions<CCodeListRecord> allActivities = new SelectOptions<>("allActivity", allActivitiesList, new CSelectRecordRenderer());

		WebMarkupContainer myActivitiesLabel = new WebMarkupContainer("myActivitiesLabel");
		myActivitiesLabel.add(new AttributeAppender(LABEL, new Model<String>(getString("label.group.activity.all_my_activities"))));

		WebMarkupContainer lastUsedActivitiesLabel = new WebMarkupContainer("lastUsedActivitiesLabel");
		lastUsedActivitiesLabel.add(new AttributeAppender(LABEL, new Model<String>(getString("label.group.activity.last_used"))));

		WebMarkupContainer allActivitiesLabel = new WebMarkupContainer("allActivitiesLabel");
		allActivitiesLabel.add(new AttributeAppender(LABEL, new Model<String>(getString("label.group.activity.all_activities"))));

		myActivitiesLabel.add(myActivities);
		lastUsedActivitiesLabel.add(lastUsedActivities);
		allActivitiesLabel.add(allActivities);
		activity.addOrReplace(myActivitiesLabel);
		activity.addOrReplace(lastUsedActivitiesLabel);
		activity.addOrReplace(allActivitiesLabel);
	}
	
	private void setActivitiesList(List<CCodeListRecord> activityList, List<CCodeListRecord> myActivitiesList, List<CCodeListRecord> allActivitiesList, List<CCodeListRecord> lastUsedActivitiesList) {

		boolean myActivitiesSwitch = false;
		boolean lastUsedActivitiesSwitch = false;

		for (CCodeListRecord activity : activityList) {

			if ((activity.getId() != null && activity.getId().equals(ISearchConstants.ACTIVITY_GROUP_MY))) {
				myActivitiesSwitch = true;
				lastUsedActivitiesSwitch = false;
				continue;
			} else if ((activity.getId() != null && activity.getId().equals(ISearchConstants.ACTIVITY_GROUP_LAST_USED))) {
				myActivitiesSwitch = false;
				lastUsedActivitiesSwitch = true;
				continue;
			} else if ((activity.getId() != null && activity.getId().equals(ISearchConstants.ACTIVITY_GROUP_ALL_OTHER))) {
				myActivitiesSwitch = false;
				lastUsedActivitiesSwitch = false;
				continue;
			}

			if (myActivitiesSwitch) {
				myActivitiesList.add(activity);
			} else if (lastUsedActivitiesSwitch) {
				lastUsedActivitiesList.add(activity);
			} else {
				allActivitiesList.add(activity);
			}
		}
	}
	
	private void initProjectSelect(List<CCodeListRecord> projectList) {

		List<CCodeListRecord> myProjectsList = new ArrayList<>();
		List<CCodeListRecord> lastUsedProjectsList = new ArrayList<>();
		List<CCodeListRecord> allProjectsList = new ArrayList<>();

		setProjectsLists(projectList, myProjectsList, allProjectsList, lastUsedProjectsList);

		SelectOptions<CCodeListRecord> myProjects = new SelectOptions<>("myProject", myProjectsList, new CSelectRecordRenderer());
		SelectOptions<CCodeListRecord> lastUsedProjects = new SelectOptions<>("lastUsedProject", lastUsedProjectsList, new CSelectRecordRenderer());
		SelectOptions<CCodeListRecord> allProjects = new SelectOptions<>("allProject", allProjectsList, new CSelectRecordRenderer());

		WebMarkupContainer myProjectsLabel = new WebMarkupContainer("myProjectsLabel");
		myProjectsLabel.add(new AttributeAppender(LABEL, new Model<String>(getString("label.group.project.all_my_projects"))));

		WebMarkupContainer lastUsedProjectsLabel = new WebMarkupContainer("lastUsedProjectsLabel");
		lastUsedProjectsLabel.add(new AttributeAppender(LABEL, new Model<String>(getString("label.group.project.last_used"))));

		WebMarkupContainer allProjectsLabel = new WebMarkupContainer("allProjectsLabel");
		allProjectsLabel.add(new AttributeAppender(LABEL, new Model<String>(getString("label.group.project.all_projects"))));

		myProjectsLabel.add(myProjects);
		lastUsedProjectsLabel.add(lastUsedProjects);
		allProjectsLabel.add(allProjects);
		project.add(myProjectsLabel);
		project.add(lastUsedProjectsLabel);
		project.add(allProjectsLabel);
	}
	
	private void setProjectsLists(List<CCodeListRecord> projectList, List<CCodeListRecord> myProjectsList, List<CCodeListRecord> allProjectsList, List<CCodeListRecord> lastUsedProjectsList) {

		boolean myProjectsSwitch = false;
		boolean lastUsedProjectsSwitch = false;

		for (CCodeListRecord project : projectList) {

			if ((project.getId() != null && project.getId().equals(ISearchConstants.PROJECT_GROUP_MY))) {
				myProjectsSwitch = true;
				lastUsedProjectsSwitch = false;
				continue;
			} else if ((project.getId() != null && project.getId().equals(ISearchConstants.PROJECT_GROUP_LAST_USED))) {
				myProjectsSwitch = false;
				lastUsedProjectsSwitch = true;
				continue;
			} else if ((project.getId() != null && project.getId().equals(ISearchConstants.PROJECT_GROUP_ALL_OTHER))) {
				myProjectsSwitch = false;
				lastUsedProjectsSwitch = false;
				continue;
			}

			if (myProjectsSwitch) {
				myProjectsList.add(project);
			} else if (lastUsedProjectsSwitch) {
				lastUsedProjectsList.add(project);
			} else {
				allProjectsList.add(project);
			}
		}
	}

	public WebMarkupContainer getActivityContainer() {
		return activityContainer;
	}

	public WebMarkupContainer getProjectContainer() {
		return projectContainer;
	}

	public AjaxCheckBox getActivityCheckBox() {
		return activityCheckBox;
	}

	public AjaxCheckBox getProjectCheckBox() {
		return projectCheckBox;
	}
	
}
