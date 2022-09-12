package sk.qbsw.sed.web.grid.datasource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.inmethod.grid.IDataSource;

import sk.qbsw.sed.client.exception.CSystemFailureException;
import sk.qbsw.sed.client.model.restriction.CEmployeeActivityLimitsData;
import sk.qbsw.sed.client.model.restriction.CXUserActivityRestrictionData;
import sk.qbsw.sed.communication.service.IActivityRestrictionClientService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.web.ui.CSedSession;

public class CEmployeeRestrictionDataSource implements IDataSource<CXUserActivityRestrictionData> {

	private static final long serialVersionUID = 1L;

	@SpringBean
	private IActivityRestrictionClientService service;

	private final int itemsPerPage;

	private final IModel<Long> filterModel;

	/**
	 * Constructor.
	 */
	public CEmployeeRestrictionDataSource(IModel<Long> filterModel) {
		Injector.get().inject(this);
		this.itemsPerPage = CSedSession.get().getUser().getTableRows();
		this.filterModel = filterModel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IModel<CXUserActivityRestrictionData> model(CXUserActivityRestrictionData object) {
		return new Model<>(object);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void query(IQuery query, IQueryResult<CXUserActivityRestrictionData> result) {
		// get the actual items
		List<CXUserActivityRestrictionData> resultList = new ArrayList<>();
		Integer count = 0;
		try {
			// if employee not chosen - empty list
			if (filterModel.getObject() != 0l) {
				// data from server
				CEmployeeActivityLimitsData apiRet = service.getEmployeeLimitsDetail(filterModel.getObject());
				// resultlist combine two lists from data, to one list with boolean flag
				for (int i = 0; i < apiRet.getEmployeeAssignedLimits().size(); i++) {
					resultList.add(new CXUserActivityRestrictionData(apiRet.getEmployeeAssignedLimits().get(i), true));
				}
				for (int i = 0; i < apiRet.getEmployeeNotAssignedLimits().size(); i++) {
					resultList.add(new CXUserActivityRestrictionData(apiRet.getEmployeeNotAssignedLimits().get(i), false));
				}
				Collections.sort(resultList, new CustomComparator());
				count = resultList.size();
				// paging
				if (resultList.size() > itemsPerPage) {
					List<CXUserActivityRestrictionData> list = new ArrayList<>();
					long indexTo = query.getFrom() + (query.getFrom() == 0 ? itemsPerPage : query.getCount());
					for (long i = query.getFrom(); i < indexTo; i++) {
						list.add(resultList.get((int) i));
					}
					resultList = list;
				}
			}
		} catch (CBussinessDataException e) {
			Logger.getLogger(CEmployeeRestrictionDataSource.class).error(e);
			throw new CSystemFailureException(e);
		}

		// determine the total count
		result.setTotalCount(count);

		result.setItems(resultList.iterator());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void detach() {
		// do nothing
	}

	private class CustomComparator implements Comparator<CXUserActivityRestrictionData> {

		@Override
		public int compare(CXUserActivityRestrictionData o1, CXUserActivityRestrictionData o2) {
			return o1.getName().compareToIgnoreCase(o2.getName());
		}
	}
}
