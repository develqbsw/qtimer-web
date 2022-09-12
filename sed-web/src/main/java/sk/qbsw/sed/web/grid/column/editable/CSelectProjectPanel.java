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
import sk.qbsw.sed.client.model.timestamp.CTimeStampRecord;
import sk.qbsw.sed.component.renderer.CSelectRecordRenderer;

public class CSelectProjectPanel<M, I, P, S> extends EditableCellPanel<M, I, P, S> {
	private static final long serialVersionUID = 1L;
	private static final String Select_ID = "project";
	private WebMarkupContainer projectContainer;
	
	protected class ProjectSelect<P> extends Select<P> {

		private static final long serialVersionUID = 1L;

		protected ProjectSelect(String id, IModel<P> model) {
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

	public CSelectProjectPanel(String id, IModel<P> model, final IModel<I> rowModel, AbstractColumn<M, I, S> column, List<CCodeListRecord> projectList) {
		super(id, column, rowModel);

		Select<P> project = new ProjectSelect<P>(Select_ID, model) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				if (rowModel.getObject() instanceof CTimeStampRecord)
					return ((CTimeStampRecord) rowModel.getObject()).getShowProject();
				else
					return true;
			}
		};

		List<CCodeListRecord> projectListNew = new ArrayList<>();
		projectListNew.addAll(projectList);

		if (rowModel.getObject() instanceof CTimeStampRecord) {
			CCodeListRecord projectRecord = new CCodeListRecord(((CTimeStampRecord) rowModel.getObject()).getProjectId(), null);

			if (!projectList.contains(projectRecord)) {
				// ak je projekt neplatny tak v zozname nebude, pridame prazdny zaznam
				projectListNew.add(1, new CCodeListRecord());
			}
		}

		initProjectSelect(project, projectListNew);

		project.setOutputMarkupId(true);
		project.setOutputMarkupPlaceholderTag(true);
		project.setLabel(column.getHeaderModel());

		projectContainer = new WebMarkupContainer("projectContainer") {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				if (rowModel.getObject() instanceof CTimeStampRecord) {
					return ((CTimeStampRecord) rowModel.getObject()).getShowProject();
				} else {
					return true;
				}
			}
		};
		projectContainer.setOutputMarkupId(true);
		projectContainer.setOutputMarkupPlaceholderTag(true);

		/*
		 * aby bolo možné skryť pole "projekt" na prehľade, v prípade, že vyberiem 
		 * nepracovnú aktivitu, tak ho musím vložiť do "projectContainer", ktorý 
		 * budem refreshovať pomocou target.add(projectComponent.getParent())
		 * vyhodnotenie zobrazenia/skrytia sa nachádza v metóde isVisible() (projectContainer)
		 */
		projectContainer.add(new Component[] { project });
		add(projectContainer);
	}

	@Override
	public FormComponent<P> getEditComponent() {
		return (FormComponent) projectContainer.get(Select_ID);
	}

	private void initProjectSelect(Select<P> project, List<CCodeListRecord> projectList) {

		final List<CCodeListRecord> myProjectsList = new ArrayList<>();
		final List<CCodeListRecord> lastUsedProjectsList = new ArrayList<>();
		final List<CCodeListRecord> allProjectsList = new ArrayList<>();

		setProjectsLists(projectList, myProjectsList, allProjectsList, lastUsedProjectsList);

		SelectOptions<CCodeListRecord> myProjects = new SelectOptions<>("myProject", myProjectsList, new CSelectRecordRenderer());
		SelectOptions<CCodeListRecord> lastUsedProjects = new SelectOptions<>("lastUsedProject", lastUsedProjectsList, new CSelectRecordRenderer());
		SelectOptions<CCodeListRecord> allProjects = new SelectOptions<>("allProject", allProjectsList, new CSelectRecordRenderer());

		WebMarkupContainer myProjectsLabel = new WebMarkupContainer("myProjectsLabel") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onBeforeRender() {
				super.onBeforeRender();
				if (myProjectsList.isEmpty()) {
					this.setVisible(false);
				}
			}
		};
		myProjectsLabel.add(new AttributeAppender("label", new Model<String>(getString("label.group.project.all_my_projects"))));

		WebMarkupContainer lastUsedProjectsLabel = new WebMarkupContainer("lastUsedProjectsLabel") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onBeforeRender() {
				super.onBeforeRender();
				if (lastUsedProjectsList.isEmpty()) {
					this.setVisible(false);
				}
			}
		};
		lastUsedProjectsLabel.add(new AttributeAppender("label", new Model<String>(getString("label.group.project.last_used"))));

		WebMarkupContainer allProjectsLabel = new WebMarkupContainer("allProjectsLabel") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onBeforeRender() {
				super.onBeforeRender();
				if (allProjectsList.isEmpty()) {
					this.setVisible(false);
				}
			}
		};
		allProjectsLabel.add(new AttributeAppender("label", new Model<String>(getString("label.group.project.all_projects"))));

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

			if (project.getId() != null && project.getId().equals(ISearchConstants.PROJECT_GROUP_MY)) {
				myProjectsSwitch = true;
				lastUsedProjectsSwitch = false;
				continue;
			} else if (project.getId() != null && project.getId().equals(ISearchConstants.PROJECT_GROUP_LAST_USED)) {
				myProjectsSwitch = false;
				lastUsedProjectsSwitch = true;
				continue;
			} else if (project.getId() != null && project.getId().equals(ISearchConstants.PROJECT_GROUP_ALL_OTHER)) {
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
}
