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
 * @author ludovit.kovac
 *
 */
public class CProjectSelect extends Select<CCodeListRecord> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String LABEL = "label";
	
	/**
	 * constructor
	 */
	public CProjectSelect(String id, final IModel<CCodeListRecord> model, List<CCodeListRecord> projectList) {
		super(id, model);
		initProjectSelect(projectList);
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

		addOrReplace(myProjectsLabel);
		addOrReplace(lastUsedProjectsLabel);
		addOrReplace(allProjectsLabel);
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
}
