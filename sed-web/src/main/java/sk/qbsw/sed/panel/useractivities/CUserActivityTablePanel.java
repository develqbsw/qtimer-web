package sk.qbsw.sed.panel.useractivities;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
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

import sk.qbsw.sed.client.model.ISearchConstants;
import sk.qbsw.sed.client.model.brw.CUserActivityRecord;
import sk.qbsw.sed.client.model.codelist.CActivityBrwFilterCriteria;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.communication.service.IActivityClientService;
import sk.qbsw.sed.communication.service.IUserClientService;
import sk.qbsw.sed.component.behaviour.CPlaceholderBehaviour;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.component.form.input.CDropDownChoice;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CStringResourceReader;
import sk.qbsw.sed.web.grid.CSedDataGrid;
import sk.qbsw.sed.web.grid.column.editable.CSelectFavoriteColumn;
import sk.qbsw.sed.web.grid.datasource.CUserActivityDataSource;
import sk.qbsw.sed.web.ui.CSedSession;

/**
 * SubPage: /useractivities SubPage title: Moje aktivity
 * 
 * Panel UserActivityTableContentPanel (tabuľka aktivít)
 */
public class CUserActivityTablePanel extends CPanel {

	private static final long serialVersionUID = 1L;

	// zdroj dát pre tabuľku: public.t_ct_activity
	private CSedDataGrid<IDataSource<CUserActivityRecord>, CUserActivityRecord, String> table;

	private CFeedbackPanel errorPanel;

	private final CActivityBrwFilterCriteria activityFilter = new CActivityBrwFilterCriteria();

	private CompoundPropertyModel<CActivityBrwFilterCriteria> activityFilterModel;

	@SpringBean
	private IActivityClientService activityService;

	@SpringBean
	private IUserClientService userService;

	public CUserActivityTablePanel(String id) {
		super(id);
		setOutputMarkupId(true);

		this.activityFilter.setActivityId(ISearchConstants.ALL_ACTIVE);
		this.activityFilterModel = new CompoundPropertyModel<>(activityFilter);

		final List<IGridColumn<IDataSource<CUserActivityRecord>, CUserActivityRecord, String>> columns = new ArrayList<>();

		CSelectFavoriteColumn<IDataSource<CUserActivityRecord>, CUserActivityRecord, String> col = 
				new CSelectFavoriteColumn<IDataSource<CUserActivityRecord>, CUserActivityRecord, String>("esd",	Model.of(getString("table.column.useractivity.mine"))) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSelect(AjaxRequestTarget target, IModel<CUserActivityRecord> rowModel, WebMarkupContainer rowComponent) {
				try {
					userService.modifyMyActivities(rowModel.getObject().getActivityId(), true, CSedSession.get().getUser().getUserId());
					super.onSelect(target, rowModel, rowComponent);
					target.add(table);

				} catch (CBussinessDataException e) {
					CBussinessDataExceptionProcessor.process(e, target, CUserActivityTablePanel.this);
				}
				if (getFeedbackPanel() != null) {
					target.add(getFeedbackPanel());
				}
			}

			@Override
			protected void onDeSelect(AjaxRequestTarget target, IModel<CUserActivityRecord> rowModel, WebMarkupContainer rowComponent) {
				try {
					userService.modifyMyActivities(rowModel.getObject().getActivityId(), false, CSedSession.get().getUser().getUserId());
					super.onSelect(target, rowModel, rowComponent);
					target.add(table);

				} catch (CBussinessDataException e) {
					CBussinessDataExceptionProcessor.process(e, target, CUserActivityTablePanel.this);
				}
				if (getFeedbackPanel() != null) {
					target.add(getFeedbackPanel());
				}
			}

		};
		col.setInitialSize(100);
		columns.add(col);

		PropertyColumn<IDataSource<CUserActivityRecord>, CUserActivityRecord, String, String> columnName = new PropertyColumn<>(new StringResourceModel("table.column.activityName", this, null), "activityName", "name");
		columnName.setInitialSize(400);
		columns.add(columnName);

		table = new CSedDataGrid<>("grid", new CUserActivityDataSource(activityFilterModel), columns);
		table.addBottomToolbar(new PagingToolbar<IDataSource<CUserActivityRecord>, CUserActivityRecord, String>(table));
		add(table);

		errorPanel = new CFeedbackPanel("errorPanel");
		registerFeedbackPanel(errorPanel);
		add(errorPanel);

		final Form<CActivityBrwFilterCriteria> activityFilterForm = new Form<>("filter", activityFilterModel);

		// validity filtration
		CCodeListRecord defaultOptionAllActivities = new CCodeListRecord(ISearchConstants.ALL_ACTIVE, CStringResourceReader.read("userActivityFilter.activities.all"));

		List<CCodeListRecord> flagMyOptions = new ArrayList<>();
		flagMyOptions.add(defaultOptionAllActivities);
		flagMyOptions.add(new CCodeListRecord(ISearchConstants.ALL_MY_ACTIVITIES, CStringResourceReader.read("userActivityFilter.activities.my")));

		final Model<CCodeListRecord> model = new Model<>();
		model.setObject(defaultOptionAllActivities);

		final CDropDownChoice<CCodeListRecord> flagMyChoice = new CDropDownChoice<>("activityId", model, flagMyOptions);
		flagMyChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				activityFilterModel.getObject().setActivityId(model.getObject().getId());
				target.add(table);
			}
		});
		activityFilterForm.add(flagMyChoice);

		// text search filtration
		final TextField<String> search = new TextField<>("search", new Model<String>());
		search.add(new CPlaceholderBehaviour(getString("searchbar.placeholder")));
		search.add(new AjaxFormComponentUpdatingBehavior("onkeyup") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget target) {
				activityFilterModel.getObject().setActivityName(search.getInput());
				target.add(table);
			}
		});
		activityFilterForm.add(search);

		add(activityFilterForm);
	}
}