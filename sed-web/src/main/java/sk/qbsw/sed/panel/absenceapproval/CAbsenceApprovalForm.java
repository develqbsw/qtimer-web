package sk.qbsw.sed.panel.absenceapproval;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;

import sk.qbsw.sed.client.model.codelist.CGetListOfUsersWithCorruptedSummaryReport;
import sk.qbsw.sed.component.calendar.CDateRangePicker;
import sk.qbsw.sed.component.calendar.CDateRangePicker.SupportedDefaults;
import sk.qbsw.sed.fw.component.form.CStatelessForm;
import sk.qbsw.sed.fw.component.validator.CStringDateRangeValidator;

public class CAbsenceApprovalForm extends CStatelessForm<CGetListOfUsersWithCorruptedSummaryReport> {

	/** serial uid */
	private static final long serialVersionUID = 1L;

	public CAbsenceApprovalForm(String id, final IModel<CGetListOfUsersWithCorruptedSummaryReport> projectModel) {
		super(id, projectModel);
		setOutputMarkupId(true);

		final WebMarkupContainer date = new CDateRangePicker("date", SupportedDefaults.SPRACOVANIE_NEPRITOMNOSTI, true);
		final TextField<String> dateInput = new TextField<>("dateInput");
		dateInput.add(new CStringDateRangeValidator<>());
		dateInput.setRequired(true);
		date.add(dateInput);

		add(date);
	}
}
