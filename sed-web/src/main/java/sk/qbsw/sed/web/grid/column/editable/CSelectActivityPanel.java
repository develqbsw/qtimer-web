package sk.qbsw.sed.web.grid.column.editable;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.form.select.Select;
import org.apache.wicket.extensions.markup.html.form.select.SelectOptions;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.inmethod.grid.column.AbstractColumn;
import com.inmethod.grid.column.editable.EditableCellPanel;

import sk.qbsw.sed.client.model.ISearchConstants;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.component.renderer.CSelectRecordRenderer;

public class CSelectActivityPanel<M, I, P, S> extends EditableCellPanel<M, I, P, S> {
	
	private static final long serialVersionUID = 1L;
	
	private static final String ACTIVITY_ID = "activity";
	
	private static final String LABEL = "label";

	protected class ActivitySelect<P> extends Select<P> {

		private static final long serialVersionUID = 1L;

		protected ActivitySelect(String id, IModel<P> model) {
			super(id, model);
		}

		@Override
		protected void onComponentTag(ComponentTag tag) {
			super.onComponentTag(tag);

			if (!isValid()) {
				tag.put("class", "imxt-invalid");
				FeedbackMessage message = getFeedbackMessages().first();
				if (message != null) {
					tag.put("title", message.getMessage().toString());
				}
			}
		}
	}

	public CSelectActivityPanel(String id, IModel<P> model, final IModel<I> rowModel, AbstractColumn<M, I, S> column, List<CCodeListRecord> activityList) {
		super(id, column, rowModel);

		Select<P> activity = new ActivitySelect<>(ACTIVITY_ID, model);

		initActivitySelect(activity, activityList);

		activity.setOutputMarkupId(true);
		activity.setOutputMarkupPlaceholderTag(true);
		activity.setLabel(column.getHeaderModel());
		add(new Component[] { activity });
	}

	@Override
	public FormComponent<P> getEditComponent() {
		return (FormComponent) get(ACTIVITY_ID);
	}

	private void initActivitySelect(Select<P> project, List<CCodeListRecord> projectList) {

		final List<CCodeListRecord> myActivitiesList = new ArrayList<>();
		final List<CCodeListRecord> lastUsedActivitiesList = new ArrayList<>();
		final List<CCodeListRecord> allActivitiesList = new ArrayList<>();

		setActivitiesLists(projectList, myActivitiesList, allActivitiesList, lastUsedActivitiesList);

		SelectOptions<CCodeListRecord> myActivities = new SelectOptions<>("myActivity", myActivitiesList, new CSelectRecordRenderer());
		SelectOptions<CCodeListRecord> lastUsedActivities = new SelectOptions<>("lastUsedActivity", lastUsedActivitiesList, new CSelectRecordRenderer());
		SelectOptions<CCodeListRecord> allActivities = new SelectOptions<>("allActivity", allActivitiesList, new CSelectRecordRenderer());

		WebMarkupContainer myActivitiesLabel = new WebMarkupContainer("myActivitiesLabel") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onBeforeRender() {
				super.onBeforeRender();
				if (myActivitiesList.isEmpty()) {
					this.setVisible(false);
				}
			}
		};
		myActivitiesLabel.add(new AttributeAppender(LABEL, new Model<String>(getString("label.group.activity.all_my_activities"))));

		WebMarkupContainer lastUsedActivitiesLabel = new WebMarkupContainer("lastUsedActivitiesLabel") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onBeforeRender() {
				super.onBeforeRender();
				if (lastUsedActivitiesList.isEmpty()) {
					this.setVisible(false);
				}
			}
		};
		lastUsedActivitiesLabel.add(new AttributeAppender(LABEL, new Model<String>(getString("label.group.activity.last_used"))));

		WebMarkupContainer allActivitiesLabel = new WebMarkupContainer("allActivitiesLabel") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onBeforeRender() {
				super.onBeforeRender();
				if (allActivitiesList.isEmpty()) {
					this.setVisible(false);
				}
			}
		};
		allActivitiesLabel.add(new AttributeAppender(LABEL, new Model<String>(getString("label.group.activity.all_activities"))));

		myActivitiesLabel.add(myActivities);
		lastUsedActivitiesLabel.add(lastUsedActivities);
		allActivitiesLabel.add(allActivities);
		project.add(myActivitiesLabel);
		project.add(lastUsedActivitiesLabel);
		project.add(allActivitiesLabel);

	}

	private void setActivitiesLists(List<CCodeListRecord> activityList, List<CCodeListRecord> myActivitiesList, List<CCodeListRecord> allActivitiesList, List<CCodeListRecord> lastUsedActivitiesList) {

		boolean myActivitiesSwitch = false;
		boolean lastUsedActivitiesSwitch = false;

		for (CCodeListRecord activity : activityList) {

			if (activity.getId() != null && activity.getId().equals(ISearchConstants.ACTIVITY_GROUP_MY)) {
				myActivitiesSwitch = true;
				lastUsedActivitiesSwitch = false;
				continue;
			} else if (activity.getId() != null && activity.getId().equals(ISearchConstants.ACTIVITY_GROUP_LAST_USED)) {
				myActivitiesSwitch = false;
				lastUsedActivitiesSwitch = true;
				continue;
			} else if (activity.getId() != null && activity.getId().equals(ISearchConstants.ACTIVITY_GROUP_ALL_OTHER)) {
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
}
