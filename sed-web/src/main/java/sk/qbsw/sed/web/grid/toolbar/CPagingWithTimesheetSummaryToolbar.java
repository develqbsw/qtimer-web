package sk.qbsw.sed.web.grid.toolbar;

import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.inmethod.grid.IDataSource;
import com.inmethod.grid.datagrid.DataGrid;

import sk.qbsw.sed.client.model.timestamp.CGetSumAndAverageTimeForUser;
import sk.qbsw.sed.client.model.timestamp.CSubrodinateTimeStampBrwFilterCriteria;
import sk.qbsw.sed.client.response.CGetSumAndAverageTimeForUsersResponseContent;
import sk.qbsw.sed.common.utils.CDateUtils;
import sk.qbsw.sed.communication.service.ITimesheetClientService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;

public class CPagingWithTimesheetSummaryToolbar<D extends IDataSource<T>, T, S> extends CPagingToolbar<D, T, S> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SpringBean
	private ITimesheetClientService timesheetService;

	private String sumString = "";
	private String avgString = "";
	private String names = "";

	private String totalAvg = "";

	/**
	 * Constructor
	 * 
	 * @param grid                     data grid
	 * @param viewTimeStampFilterModel
	 * @param errorPanel
	 */
	public CPagingWithTimesheetSummaryToolbar(final DataGrid<D, T, S> grid, final CompoundPropertyModel<CSubrodinateTimeStampBrwFilterCriteria> viewTimeStampFilterModel) {
		super(grid);

		IModel<String> sumModel = new Model<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getObject() {
				try {
					return sumAsString(viewTimeStampFilterModel);
				} catch (CBussinessDataException e) {
					Logger.getLogger(CPagingWithTimesheetSummaryToolbar.class).error(e);
					return "";
				}
			}
		};

		Label summaryLabel = new Label("summaryLabel", sumModel);
		summaryLabel.setEscapeModelStrings(false);
		summaryLabel.setOutputMarkupId(true);
		add(summaryLabel);

		IModel<String> avgModel = new Model<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getObject() {
				return totalAvg;
			}
		};

		Label averageLabel = new Label("averageLabel", avgModel);
		averageLabel.setEscapeModelStrings(false);
		averageLabel.setOutputMarkupId(true);
		add(averageLabel);

		IModel<String> userNamesModel = new Model<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getObject() {
				return names;
			}
		};

		Label userNameLabel = new Label("userNameLabel", userNamesModel);
		userNameLabel.setEscapeModelStrings(false);
		userNameLabel.setOutputMarkupId(true);
		add(userNameLabel);

		IModel<String> userSumsModel = new Model<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getObject() {
				return sumString;
			}
		};

		Label userSumLabel = new Label("userSumLabel", userSumsModel);
		userSumLabel.setEscapeModelStrings(false);
		userSumLabel.setOutputMarkupId(true);
		add(userSumLabel);

		IModel<String> userAvgsModel = new Model<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getObject() {
				return avgString;
			}
		};

		Label userAvgLabel = new Label("userAvgLabel", userAvgsModel);
		userAvgLabel.setEscapeModelStrings(false);
		userAvgLabel.setOutputMarkupId(true);
		add(userAvgLabel);
	}

	private String sumAsString(CompoundPropertyModel<CSubrodinateTimeStampBrwFilterCriteria> viewTimeStampFilterModel) throws CBussinessDataException {

		String retVal = "";
		totalAvg = "";

		CGetSumAndAverageTimeForUsersResponseContent sumAndAverageResponse = timesheetService.getSumAndAverageTimeForUsers(viewTimeStampFilterModel.getObject());

		if (sumAndAverageResponse.getList().isEmpty()) {
			retVal = "";
		} else {

			retVal = "&Sigma;=" + CDateUtils.convertToHourMinuteString(BigDecimal.valueOf(sumAndAverageResponse.getTotalSums()));
			totalAvg = "&empty;=" + CDateUtils.convertToHourMinuteString(BigDecimal.valueOf(sumAndAverageResponse.getTotalAvg()));

			loadAllSelectedUsers(viewTimeStampFilterModel, sumAndAverageResponse.getList());
		}
		return retVal;
	}

	private void loadAllSelectedUsers(CompoundPropertyModel<CSubrodinateTimeStampBrwFilterCriteria> viewTimeStampFilterModel, List<CGetSumAndAverageTimeForUser> sumAndAverageList)
			throws CBussinessDataException {
		sumString = "";
		avgString = "";
		names = "";

		if (viewTimeStampFilterModel.getObject().getEmplyees().size() > 1) {

			for (CGetSumAndAverageTimeForUser employee : sumAndAverageList) {
				names += employee.getSurname() + " " + employee.getName() + "</br>";
				sumString += "&Sigma;=" + CDateUtils.convertToHourMinuteString(BigDecimal.valueOf(employee.getSumOfWorkHours())) + "</br>";
				avgString += "&empty;=" + CDateUtils.convertToHourMinuteString(BigDecimal.valueOf(employee.getSumOfWorkHours() / employee.getCountOfDays())) + "</br>";
			}
		}
	}
}
