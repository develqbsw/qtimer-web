package sk.qbsw.sed.web.grid.datasource;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.inmethod.grid.IDataSource;
import com.inmethod.grid.IGridSortState;
import com.inmethod.grid.IGridSortState.ISortStateColumn;

import sk.qbsw.sed.client.exception.CSystemFailureException;
import sk.qbsw.sed.client.model.brw.CTmpTimeSheet;
import sk.qbsw.sed.client.model.timestamp.CTimeStampGenerateFilterCriteria;
import sk.qbsw.sed.communication.service.IBrwTimeStampGenerateService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;

public class CGenerateTimestampDataSource implements IDataSource<CTmpTimeSheet> {

	private static final long serialVersionUID = 1L;

	@SpringBean
	private IBrwTimeStampGenerateService service;

	private final IModel<CTimeStampGenerateFilterCriteria> filterModel;

	private List<CTmpTimeSheet> resultList = null;

	private Date dateFrom;
	private Date dateTo;

	public CGenerateTimestampDataSource(IModel<CTimeStampGenerateFilterCriteria> filterModel) {
		Injector.get().inject(this);
		this.filterModel = filterModel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IModel<CTmpTimeSheet> model(CTmpTimeSheet object) {
		return new Model<>(object);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void query(IQuery query, IQueryResult<CTmpTimeSheet> result) {

		// default sortProperty
		String sortProperty = "id";
		boolean sortAsc = true;

		// is there any sorting
		if (query.getSortState().getColumns().size() > 0) {
			// get the most relevant column
			ISortStateColumn<Object> state = query.getSortState().getColumns().get(0);

			// get the column sort properties
			sortProperty = state.getPropertyName().toString();
			sortAsc = state.getDirection() == IGridSortState.Direction.ASC;
		}

		try {
			if (resultList == null || !filterModel.getObject().getDateFrom().equals(dateFrom) || !filterModel.getObject().getDateTo().equals(dateTo) || filterModel.getObject().getGenerateFromJira()) {
				resultList = service.fetch(0, Integer.MAX_VALUE, sortProperty, sortAsc, filterModel.getObject());

				dateFrom = filterModel.getObject().getDateFrom();
				dateTo = filterModel.getObject().getDateTo();
				filterModel.getObject().setGenerateFromJira(false);
			}

		} catch (CBussinessDataException e) {
			Logger.getLogger(CTimestampDataSource.class).error(e);
			throw new CSystemFailureException(e);
		}

		// determine the total count
		result.setTotalCount(resultList.size());

		result.setItems(resultList.iterator());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void detach() {
		// do nothing
	}

	public int deleteRow(CTmpTimeSheet item) {
		int index = resultList.indexOf(item);

		if (index == -1 && item.getId() != null) {
			for (CTmpTimeSheet tmp : resultList) {
				if (item.getId().equals(tmp.getId())) {
					item = tmp;
					break;
				}
			}
		}

		resultList.remove(item);
		return index;
	}

	public void insertRow(int index, CTmpTimeSheet item) {
		resultList.add(index, item);
	}

	public List<CTmpTimeSheet> getResultList() {
		return resultList;
	}

	public void clearResultList() {
		this.resultList = null;
	}
}
