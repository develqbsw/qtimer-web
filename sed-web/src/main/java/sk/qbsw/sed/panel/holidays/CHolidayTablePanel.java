package sk.qbsw.sed.panel.holidays;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.inmethod.grid.IDataSource;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.column.PropertyColumn;
import com.inmethod.grid.toolbar.paging.PagingToolbar;

import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.codelist.CHolidayBrwFilterCriteria;
import sk.qbsw.sed.client.model.codelist.CHolidayRecord;
import sk.qbsw.sed.client.ui.localization.CClientExceptionsMessages;
import sk.qbsw.sed.communication.service.IHolidayClientService;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.component.form.input.CDropDownChoice;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CStringResourceReader;
import sk.qbsw.sed.fw.utils.CWicketUtils;
import sk.qbsw.sed.web.grid.CSedDataGrid;
import sk.qbsw.sed.web.grid.column.CCheckBoxColumn;
import sk.qbsw.sed.web.grid.datasource.CHolidayDataSource;
import sk.qbsw.sed.web.ui.CSedSession;

/**
 * SubPage: /holidays SubPage title: Sviatky
 * 
 * HolidayTablePanel - Prehľady
 */
public class CHolidayTablePanel extends CPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String CLASS = "class";
	
	// zdroj dát pre tabuľku: public.t_ct_holiday
	private CSedDataGrid<IDataSource<CHolidayRecord>, CHolidayRecord, String> table;

	private final Label tabTitle;

	private CFeedbackPanel errorPanel;

	private final CHolidayBrwFilterCriteria holidayFilter = new CHolidayBrwFilterCriteria();

	private CompoundPropertyModel<CHolidayBrwFilterCriteria> holidayFilterModel;

	@SpringBean
	private IHolidayClientService holidayService;

	private AjaxFallbackLink<Object> generateBtn;

	public CHolidayTablePanel(String id, final CHolidayContentPanel tabPanel, Label tabTitleParam) {
		super(id);

		this.tabTitle = tabTitleParam;

		Long thisYear = (long) (new GregorianCalendar().get(Calendar.YEAR));
		this.holidayFilter.setYear(thisYear);
		this.holidayFilterModel = new CompoundPropertyModel<>(holidayFilter);

		final List<IGridColumn<IDataSource<CHolidayRecord>, CHolidayRecord, String>> columns = new ArrayList<>();

		columns.add(new PropertyColumn<IDataSource<CHolidayRecord>, CHolidayRecord, String, String>(
				new StringResourceModel("table.column.holidayId", this, null), "id", "id").setInitialSize(100));

		columns.add(new PropertyColumn<IDataSource<CHolidayRecord>, CHolidayRecord, String, String>(
				new StringResourceModel("table.column.holidayName", this, null), "description", "description").setInitialSize(300));

		columns.add(new PropertyColumn<IDataSource<CHolidayRecord>, CHolidayRecord, String, String>(
				new StringResourceModel("table.column.holidayDate", this, null), "day", "day").setInitialSize(100));

		columns.add(new CCheckBoxColumn<IDataSource<CHolidayRecord>, CHolidayRecord, String, String>(
				new StringResourceModel("table.column.holidayValid", this, null), "active", "valid").setInitialSize(90));

		table = new CSedDataGrid<IDataSource<CHolidayRecord>, CHolidayRecord, String>("grid", new CHolidayDataSource(holidayFilterModel), columns) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onDoubleRowClicked(AjaxRequestTarget target, IModel<CHolidayRecord> rowModel) {
				// update nadpisu
				tabTitle.setDefaultModelObject(CStringResourceReader.read("tabTitle.detail"));
				target.add(tabTitle);

				// update formulara
				tabPanel.setModeDetail(true);
				tabPanel.setEntityID(rowModel.getObject().getId());
				tabPanel.clearFeedbackMessages();
				target.add(tabPanel);

				// prepnutie tabu
				target.appendJavaScript("$('#tab-2').click()");
			}
		};
		table.addBottomToolbar(new PagingToolbar<IDataSource<CHolidayRecord>, CHolidayRecord, String>(table));
		add(table);

		errorPanel = new CFeedbackPanel("errorPanel");
		registerFeedbackPanel(errorPanel);
		add(errorPanel);

		final Form<CHolidayBrwFilterCriteria> holidayFilterForm = new Form<>("filter", holidayFilterModel);

		List<CCodeListRecord> yearOptions = new ArrayList<>();
		for (Long i = thisYear - 1; i < thisYear + 2; i++) {
			CCodeListRecord local = new CCodeListRecord(i, Long.toString(i));
			yearOptions.add(local);
		}

		final Model<CCodeListRecord> yearModel = new Model<>();
		yearModel.setObject(yearOptions.get(1));

		final CDropDownChoice<CCodeListRecord> yearChoice = new CDropDownChoice<>("yearBox", yearModel, yearOptions);
		yearChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				holidayFilterModel.getObject().setYear(yearModel.getObject().getId());
				target.add(table);
				toggleGenerateBtnEnabled();
				target.add(generateBtn);
			}
		});

		holidayFilterForm.add(yearChoice);

		add(holidayFilterForm);

		generateBtn = new AjaxFallbackLink<Object>("generateBtn") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				try {
					holidayService.cloneRecordsForNextYear(CSedSession.get().getUser().getClientInfo().getClientId());
					target.add(table);
					toggleGenerateBtnEnabled();
					target.add(generateBtn);
				} catch (CBussinessDataException e) {
					if (e.getModel().getServerCode().equals(CClientExceptionsMessages.HOLIDAYS_CANT_BE_GENERATED)) {
						CHolidayTablePanel.this.error(CStringResourceReader.read("holiday.cantGenerate"));
						CWicketUtils.refreshFeedback(target, CHolidayTablePanel.this);
					} else {
						CBussinessDataExceptionProcessor.process(e, target, CHolidayTablePanel.this);
					}
				}

			}

		};
		
		generateBtn.add(new AttributeAppender("title", getString("holiday.generateForNextYear")));
		generateBtn.setEnabled(false);
		generateBtn.add(AttributeModifier.append(CLASS, "disabled"));
		generateBtn.setOutputMarkupId(true);
		add(generateBtn);

	}

	private void toggleGenerateBtnEnabled() {
		Calendar today = Calendar.getInstance();
		Long thisYear = new Long(today.get(Calendar.YEAR));

		if (holidayFilterModel.getObject().getYear().equals(thisYear + 1) && table.getTotalRowCount() == 0) {
			generateBtn.setEnabled(true);
			generateBtn.add(AttributeModifier.replace(CLASS, "btn btn-green enabled"));
		} else {
			generateBtn.setEnabled(false);
			generateBtn.add(AttributeModifier.replace(CLASS, "btn btn-green disabled"));
		}
	}
}
