package sk.qbsw.sed.panel.presenceemail;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.behavior.AttributeAppender;
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
import com.inmethod.grid.toolbar.paging.PagingToolbar;

import sk.qbsw.sed.client.exception.CSystemFailureException;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.codelist.CUserSystemEmailContainer;
import sk.qbsw.sed.client.model.codelist.CUserSystemEmailFilterCriteria;
import sk.qbsw.sed.client.model.codelist.CUserSystemEmailRecord;
import sk.qbsw.sed.communication.service.ISendEmailClientService;
import sk.qbsw.sed.communication.service.IUserClientService;
import sk.qbsw.sed.component.behaviour.CPlaceholderBehaviour;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.component.form.input.CDropDownChoice;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CStringResourceReader;
import sk.qbsw.sed.web.grid.CSedDataGrid;
import sk.qbsw.sed.web.grid.column.editable.CEditSubmitCancelColumn;
import sk.qbsw.sed.web.grid.column.editable.CEditableCheckBoxColumn;
import sk.qbsw.sed.web.grid.datasource.CPresenceEmailDataSource;
import sk.qbsw.sed.web.ui.components.IAjaxCommand;
import sk.qbsw.sed.web.ui.components.panel.CConfirmDialogPanel;
import sk.qbsw.sed.web.ui.components.panel.CModalBorder;

/**
 * SubPage: /presenceEmail SubPage title: Email o dochádzke
 * 
 * Panel PresenceEmailTablePanel
 */
public class CPresenceEmailTablePanel extends CPanel {

	private static final long serialVersionUID = 1L;

	private static final String SELECTED = "selected";
	
	// zdroj dát pre tabuľku: public.t_user
	private CSedDataGrid<IDataSource<CUserSystemEmailRecord>, CUserSystemEmailRecord, String> table;

	private CPresenceEmailDataSource dataSource;

	private final CUserSystemEmailFilterCriteria userFilter;

	private CompoundPropertyModel<CUserSystemEmailFilterCriteria> userFilterModel;

	private CModalBorder modal;

	private CConfirmDialogPanel modalPanel;

	@SpringBean
	private IUserClientService userService;

	@SpringBean
	private ISendEmailClientService emailService;

	public CPresenceEmailTablePanel(String id, Label pageTitleSmall) {
		super(id);
		setOutputMarkupId(true);
		pageTitleSmall.setDefaultModelObject(CStringResourceReader.read("presenceEmail.subtitle"));

		modal = new CModalBorder("modalWindow");
		modal.setOutputMarkupId(true);
		modalPanel = new CConfirmDialogPanel("content", modal);
		modal.add(modalPanel);
		add(modal);

		userFilter = new CUserSystemEmailFilterCriteria();
		userFilter.setSelected(false);

		this.userFilterModel = new CompoundPropertyModel<>(userFilter);

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
					CUserSystemEmailContainer selected = new CUserSystemEmailContainer();
					selected.setSelectedUsers(dataSource.getSelectedList());
					try {
						userService.saveSystemEmailAccounts(selected);
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
				new StringResourceModel("employee.name", this, null), "name", "name").setInitialSize(250));

		columns.add(new CEditableCheckBoxColumn<IDataSource<CUserSystemEmailRecord>, CUserSystemEmailRecord, String>(
				new StringResourceModel("employee.selected", this, null), SELECTED, SELECTED).setInitialSize(100));

		dataSource = new CPresenceEmailDataSource(userFilterModel);

		table = new CSedDataGrid<>("grid", dataSource, columns);
		table.addBottomToolbar(new PagingToolbar<IDataSource<CUserSystemEmailRecord>, CUserSystemEmailRecord, String>(table));
		add(table);

		CFeedbackPanel errorPanel = new CFeedbackPanel("errorPanel");
		registerFeedbackPanel(errorPanel);
		add(errorPanel);

		final Form<CUserSystemEmailFilterCriteria> userFilterForm = new Form<>("filter", userFilterModel);

		// selected filtration
		CCodeListRecord defaultOptionAllEmps = new CCodeListRecord(0l, CStringResourceReader.read("presenceEmail.filter.all"));

		List<CCodeListRecord> selectedOptions = new ArrayList<>();
		selectedOptions.add(defaultOptionAllEmps);
		selectedOptions.add(new CCodeListRecord(1l, CStringResourceReader.read("presenceEmail.filter.receivers")));

		final Model<CCodeListRecord> model = new Model<>();
		model.setObject(defaultOptionAllEmps);

		final CDropDownChoice<CCodeListRecord> selectedChoice = new CDropDownChoice<>(SELECTED, model, selectedOptions);
		selectedChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				userFilterModel.getObject().setSelected(model.getObject().getId() == 0l ? false : true);
				target.add(table);
			}
		});
		userFilterForm.add(selectedChoice);

		AjaxFallbackLink<Object> senEmailsButton = new AjaxFallbackLink<Object>("sendEmailsButton") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				CUserSystemEmailContainer employeesReceivingEmail = new CUserSystemEmailContainer();
				employeesReceivingEmail.setSelectedUsers(dataSource.getSelectedList());

				IAjaxCommand commandToExectute = getConfirmCommand(employeesReceivingEmail);
				showModal(target, commandToExectute, "sednEmail.dialog.confirm.title");
			}

		};
		senEmailsButton.add(new AttributeAppender("title", getString("button.sendPresenceEmails")));
		userFilterForm.add(senEmailsButton);

		// text search filtration
		final TextField<String> search = new TextField<>("search", new Model<String>());
		search.add(new CPlaceholderBehaviour(getString("searchbar.placeholder")));
		search.add(new AjaxFormComponentUpdatingBehavior("onkeyup") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget target) {
				userFilterModel.getObject().setName(search.getInput());
				target.add(table);
			}
		});
		userFilterForm.add(search);

		add(userFilterForm);
	}

	IAjaxCommand getConfirmCommand(final CUserSystemEmailContainer employeesReceivingEmail) {
		return new IAjaxCommand() {
			/** serial uid */
			private static final long serialVersionUID = 1L;

			@Override
			public void execute(AjaxRequestTarget target) {
				try {
					emailService.sendMissingEmployeesEmail(employeesReceivingEmail);
				} catch (CBussinessDataException e) {
					CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
					CPresenceEmailTablePanel.this.getFeedbackPanel().error(getString(e.getModel().getServerCode()));
				}
			}
		};

	}

	private void showModal(AjaxRequestTarget target, final IAjaxCommand commandToExecute, String titleCode) {

		modal.getTitleModel().setObject(CStringResourceReader.read(titleCode));
		modalPanel.setAction(commandToExecute);
		modal.show(target);
	}
}
