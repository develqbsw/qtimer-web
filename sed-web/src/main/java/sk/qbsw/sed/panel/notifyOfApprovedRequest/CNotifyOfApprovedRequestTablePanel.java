package sk.qbsw.sed.panel.notifyOfApprovedRequest;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.inmethod.grid.IDataSource;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.column.PropertyColumn;

import sk.qbsw.sed.client.model.codelist.CNotifyOfApprovedRequestFilterCriteria;
import sk.qbsw.sed.client.ui.screen.restriction.users.CEmployeeRecord;
import sk.qbsw.sed.communication.service.IUserClientService;
import sk.qbsw.sed.component.behaviour.CPlaceholderBehaviour;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CStringResourceReader;
import sk.qbsw.sed.web.grid.CSedDataGrid;
import sk.qbsw.sed.web.grid.datasource.CEmployeeDataSource;
import sk.qbsw.sed.web.grid.toolbar.CPagingToolbar;

public class CNotifyOfApprovedRequestTablePanel extends CPanel {
	private CSedDataGrid<IDataSource<CEmployeeRecord>, CEmployeeRecord, String> table;

	private final CNotifyOfApprovedRequestFilterCriteria nameFilter = new CNotifyOfApprovedRequestFilterCriteria();

	private CompoundPropertyModel<String> nameFilterModel;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private CNotifyOfApprovedRequestPanel tabPanel;

	private Label tabTitle;

	private CEmployeeDataSource dataSource;

	@SpringBean
	private IUserClientService userService;

	private Long entityID;

	public CNotifyOfApprovedRequestTablePanel(String id, CNotifyOfApprovedRequestPanel tabPanelParam, Label tabTitleParam) {
		super(id);
		this.tabPanel = tabPanelParam;
		this.tabTitle = tabTitleParam;

		this.nameFilterModel = new CompoundPropertyModel<>(new String());

		final List<IGridColumn<IDataSource<CEmployeeRecord>, CEmployeeRecord, String>> columns = new ArrayList<>();
		columns.add(new PropertyColumn<IDataSource<CEmployeeRecord>, CEmployeeRecord, String, String>(new StringResourceModel("employee.name", this, null), "name", "name").setInitialSize(250));

		dataSource = new CEmployeeDataSource(nameFilterModel);

		table = new CSedDataGrid<IDataSource<CEmployeeRecord>, CEmployeeRecord, String>("grid", dataSource, columns) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onRowClicked(AjaxRequestTarget target, IModel<CEmployeeRecord> rowModel) {
				showDetail(target, rowModel.getObject().getUserId());

			}
		};
		table.addBottomToolbar(new CPagingToolbar<IDataSource<CEmployeeRecord>, CEmployeeRecord, String>(table));
		add(table);

		CFeedbackPanel errorPanel = new CFeedbackPanel("errorPanel");
		registerFeedbackPanel(errorPanel);
		add(errorPanel);

		// text search filtration
		final TextField<String> search = new TextField<>("search", new Model<String>());
		search.add(new CPlaceholderBehaviour(getString("searchbar.placeholder")));
		search.add(new AjaxFormComponentUpdatingBehavior("onkeyup") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget target) {
				nameFilterModel.setObject(getComponent().getDefaultModelObjectAsString());
				table.setFirstPage();
				target.add(table);
			}
		});
		add(search);
		search.add(new CPlaceholderBehaviour(getString("searchbar.placeholder")));

	}

	private void showDetail(AjaxRequestTarget target, Long id) {
		// update nadpisu
		tabTitle.setDefaultModelObject(CStringResourceReader.read("tabTitle.detail"));
		target.add(tabTitle);

		// pri zobrazeni detailu konkretneho zaznamu, vzdy premazem searach field
		tabPanel.clearSearch();

		// update formulara
		tabPanel.setEntityID(id);
		target.add(tabPanel);
		target.appendJavaScript("Main.runQTimerCheck();");

		// prepnutie tabu
		target.appendJavaScript("$('#tab-2').click();$('#tab-2').css('display','block');");

		// nastavim tabulke ktora sa stara o zobrazenie zamestnancov na detail tabe, aby
		// sa setla prva strana
		tabPanel.getTable().setFirstPage();
		// ajax ju refereshne
		target.add(tabPanel.getTable());
	}
}
