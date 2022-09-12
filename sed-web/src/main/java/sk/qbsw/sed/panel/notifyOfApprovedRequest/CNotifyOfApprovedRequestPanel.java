package sk.qbsw.sed.panel.notifyOfApprovedRequest;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.inmethod.grid.IDataSource;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.column.PropertyColumn;

import sk.qbsw.sed.client.exception.CSystemFailureException;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.codelist.CNotifyOfApprovedRequestContainer;
import sk.qbsw.sed.client.model.codelist.CUserSystemEmailFilterCriteria;
import sk.qbsw.sed.client.model.codelist.CUserSystemEmailRecord;
import sk.qbsw.sed.communication.service.IUserClientService;
import sk.qbsw.sed.component.behaviour.CPlaceholderBehaviour;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.component.form.input.CDropDownChoice;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CStringResourceReader;
import sk.qbsw.sed.panel.presenceemail.CPresenceEmailTablePanel;
import sk.qbsw.sed.web.grid.CSedDataGrid;
import sk.qbsw.sed.web.grid.column.editable.CEditSubmitCancelColumn;
import sk.qbsw.sed.web.grid.column.editable.CEditableCheckBoxColumn;
import sk.qbsw.sed.web.grid.datasource.CNotifyOfApprovedRequestDataSource;
import sk.qbsw.sed.web.grid.toolbar.CPagingToolbar;
import sk.qbsw.sed.web.ui.components.panel.CConfirmDialogPanel;
import sk.qbsw.sed.web.ui.components.panel.CModalBorder;

public class CNotifyOfApprovedRequestPanel extends CPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String SELECTED = "selected";
	
	private final Label tabTitle;

	private Long entityID;

	private CModalBorder modal;

	private CConfirmDialogPanel modalPanel;

	private CNotifyOfApprovedRequestDataSource dataSource;

	private final CUserSystemEmailFilterCriteria userFilter = new CUserSystemEmailFilterCriteria();

	private CompoundPropertyModel<CUserSystemEmailFilterCriteria> nameFilterModel;

	private CSedDataGrid<IDataSource<CUserSystemEmailRecord>, CUserSystemEmailRecord, String> table;

	@SpringBean
	private IUserClientService userService;

	private TextField<String> search;

	private CDropDownChoice<CCodeListRecord> selectedChoice;

	public CNotifyOfApprovedRequestPanel(String id, Label tabTitle) {
		super(id);
		this.tabTitle = tabTitle;
	}

	public void setEntityID(Long entityID) {
		this.entityID = entityID;
		this.dataSource.setEntityId(entityID);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		userFilter.setSelected(false);

		this.nameFilterModel = new CompoundPropertyModel<>(userFilter);

		final List<IGridColumn<IDataSource<CUserSystemEmailRecord>, CUserSystemEmailRecord, String>> columns = new ArrayList<>();

		CEditSubmitCancelColumn<IDataSource<CUserSystemEmailRecord>, CUserSystemEmailRecord, String> editColumn = new CEditSubmitCancelColumn<IDataSource<CUserSystemEmailRecord>, CUserSystemEmailRecord, String>(
				"esd", Model.of(getString("table.column.actions"))) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmitted(AjaxRequestTarget target, IModel<CUserSystemEmailRecord> rowModel, WebMarkupContainer rowComponent) {
				boolean toSave = false; // needed to save or not
				// if selected for a receiver and list of selected does not contain such user
				if (rowModel.getObject().getSelected() == true) {
					if (!dataSource.getSelectedList().contains(rowModel.getObject())) {
						dataSource.getSelectedList().add(rowModel.getObject());
						toSave = true;
					}
					// if not selected for a receiver and list of selected contains such user
				} else if (rowModel.getObject().getSelected() == false) {
					if (dataSource.getSelectedList().contains(rowModel.getObject())) {
						dataSource.getSelectedList().remove(rowModel.getObject());
						toSave = true;
					}
				}
				if (toSave == true) {
					CNotifyOfApprovedRequestContainer selected = new CNotifyOfApprovedRequestContainer();
					selected.setSelectedUsers(dataSource.getSelectedList());
					selected.setUserRequestId(entityID);
					try {

						userService.saveNotifyOfApprovedRequest(selected);
					} catch (CBussinessDataException e) {
						Logger.getLogger(CPresenceEmailTablePanel.class).error(e);
						throw new CSystemFailureException(e);
					}
				}
				super.onSubmitted(target, rowModel, rowComponent);
			}

		};
		editColumn.setInitialSize(65);
		columns.add(editColumn);

		columns.add(new PropertyColumn<IDataSource<CUserSystemEmailRecord>, CUserSystemEmailRecord, String, String>(
				new StringResourceModel("employee.notify", this, null), "name", "name").setInitialSize(250));

		columns.add(new CEditableCheckBoxColumn<IDataSource<CUserSystemEmailRecord>, CUserSystemEmailRecord, String>(
				new StringResourceModel("notify.selected", this, null), SELECTED, SELECTED).setInitialSize(200));

		dataSource = new CNotifyOfApprovedRequestDataSource(nameFilterModel, this.entityID);

		table = new CSedDataGrid<>("grid", dataSource, columns);
		table.addBottomToolbar(new CPagingToolbar<IDataSource<CUserSystemEmailRecord>, CUserSystemEmailRecord, String>(table));
		add(table);

		CFeedbackPanel errorPanel = new CFeedbackPanel("errorPanel");
		registerFeedbackPanel(errorPanel);
		add(errorPanel);

		final Form<CUserSystemEmailFilterCriteria> userFilterForm = new Form<>("filter", nameFilterModel);

		// selected filtration
		CCodeListRecord defaultOptionAllEmps = new CCodeListRecord(0l, CStringResourceReader.read("presenceEmail.filter.all"));

		List<CCodeListRecord> selectedOptions = new ArrayList<>();
		selectedOptions.add(defaultOptionAllEmps);
		selectedOptions.add(new CCodeListRecord(1l, CStringResourceReader.read("notify.filter.receivers")));

		final Model<CCodeListRecord> model = new Model<>();
		model.setObject(defaultOptionAllEmps);

		selectedChoice = new CDropDownChoice<>(SELECTED, model, selectedOptions);
		selectedChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				nameFilterModel.getObject().setSelected(model.getObject().getId() == 0l ? false : true);
				table.setFirstPage();
				target.add(table);
			}
		});
		userFilterForm.add(selectedChoice);

		// text search filtration
		search = new TextField<>("search", new Model<String>());
		search.add(new CPlaceholderBehaviour(getString("searchbar.placeholder")));
		search.add(new PreventSubmitOnEnterBehavior()); // aby sa po stlačení ENTER neprepol tab z detailu na prehľady
		search.add(new AjaxFormComponentUpdatingBehavior("onkeyup") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget target) {
				nameFilterModel.getObject().setName(search.getInput());
				table.setFirstPage();
				target.add(table);
			}
		});
		userFilterForm.add(search);

		add(userFilterForm);
	}

	public void clearSearch() {
		search.setModelObject("");
		nameFilterModel.getObject().setName(search.getInput());
		selectedChoice.setDefaultModelObject("");
		nameFilterModel.getObject().setSelected(false);
	}

	// vnorená trieda, ktorá zabráni odoslaniu po stlačení enteru
	class PreventSubmitOnEnterBehavior extends Behavior {
		private static final long serialVersionUID = 1L;

		public PreventSubmitOnEnterBehavior() {
			// do nothing
		}

		@Override
		public void bind(Component component) {
			super.bind(component);
			component.add(AttributeModifier.replace("onkeydown", Model.of("if(event.keyCode == 13) {event.preventDefault();}")));
		}
	}

	public CSedDataGrid<IDataSource<CUserSystemEmailRecord>, CUserSystemEmailRecord, String> getTable() {
		return table;
	}

	public void setTable(CSedDataGrid<IDataSource<CUserSystemEmailRecord>, CUserSystemEmailRecord, String> table) {
		this.table = table;
	}
}
