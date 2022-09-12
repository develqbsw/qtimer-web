package sk.qbsw.sed.panel.home;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.form.select.Select;
import org.apache.wicket.extensions.markup.html.form.select.SelectOptions;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import sk.qbsw.sed.client.model.ISearchConstants;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.component.renderer.CSelectRecordRenderer;

/**
 * 
 * @author lobb
 *
 */
public class CActivitySelect extends Select<CCodeListRecord> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String LABEL = "label";
	
	/**
	 * constructor
	 */
	public CActivitySelect(String id, List<CCodeListRecord> activityList) {
		super(id);
		initActivitySelect(activityList);
	}

	/**
	 * constructor
	 */
	public CActivitySelect(String id, final IModel<CCodeListRecord> model, List<CCodeListRecord> activityList) {
		super(id, model);
		initActivitySelect(activityList);
	}

	private void initActivitySelect(List<CCodeListRecord> activityList) {
		List<CCodeListRecord> myActivitiesList = new ArrayList<>();
		List<CCodeListRecord> lastUsedActivitiesList = new ArrayList<>();
		List<CCodeListRecord> allActivitiesList = new ArrayList<>();

		setActivitiesList(activityList, myActivitiesList, allActivitiesList, lastUsedActivitiesList);

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
		addOrReplace(myActivitiesLabel);
		addOrReplace(lastUsedActivitiesLabel);
		addOrReplace(allActivitiesLabel);

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
}
