package sk.qbsw.sed.panel.timesheet;

import java.io.Serializable;
import java.util.Date;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import sk.qbsw.sed.communication.service.ITimesheetClientService;
import sk.qbsw.sed.fw.component.EDataType;
import sk.qbsw.sed.fw.component.form.input.CTextField;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CStringResourceReader;
import sk.qbsw.sed.web.ui.components.panel.CModalBorder;

public class CModalWindowTimesheetContentPanel extends CPanel {

	@SpringBean
	private ITimesheetClientService timesheetService;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// z tohoto sa bude skladat cas na rozdelenie casovej znacky
	private Date dateFrom;

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public CModalWindowTimesheetContentPanel(String id, final CModalBorder parentWindow, CTimesheetContentPanel panel) {
		super(id);

		Form<CModalWindowTimesheetContentPanelModel> form = new Form<>("form", new Model<CModalWindowTimesheetContentPanelModel>(new CModalWindowTimesheetContentPanelModel()));

		CTextField<Date> timeSplit = new CTextField<>("timeSplit", new PropertyModel<Date>(form.getModel(), "timeSplit"), EDataType.TIME);
		form.add(timeSplit);

		form.add(new AjaxFallbackButton("btnOk", form) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form1) {

				System.out.println("SUBMIT");
				Date splitTime = form.getModel().getObject().getTimeSplit();

				if (splitTime != null) {
					try {

						// splitTime sa vytvori tak ze tam je rok 1970 a pri porovnavani to nekorektne vyhodnuti podmienky
						// preto si nasetujem z casovej znacky jej datum a vlozim do splitTime a tak poslem dalej
						splitTime.setDate(dateFrom.getDate());
						splitTime.setMonth(dateFrom.getMonth());
						splitTime.setYear(dateFrom.getYear());
						timesheetService.split(panel.getForm().getModel().getObject(), splitTime);
						parentWindow.hide(target);

						panel.getFeedbackPanel().success(CStringResourceReader.read("TIMESTAMPSPLIT.ACTION.SUCCESS"));
						panel.refreshTablePanel(target);

						target.add(panel.getPanelToRefreshAfterSumit());
						target.add(panel.getFeedbackToRefreshAfterSumit());
						target.add(panel.getFeedbackPanel());

					} catch (CBussinessDataException e) {
						CBussinessDataExceptionProcessor.process(e, target, panel);
						panel.refreshTablePanelWithoutTabClick(target);
						parentWindow.hide(target);
					}
				}
			}
		});

		form.add(new AjaxFallbackButton("btnCancel", form) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form1) {
				parentWindow.hide(target);
			}
		});

		add(form);
	}

	class CModalWindowTimesheetContentPanelModel implements Serializable {

		private static final long serialVersionUID = 1L;

		private Date timeSplit;

		public Date getTimeSplit() {
			return timeSplit;
		}

		public void setTimeSplit(Date timeSplit) {
			this.timeSplit = timeSplit;
		}
	}
}
