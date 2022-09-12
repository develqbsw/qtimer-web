package sk.qbsw.sed.web.grid.datasource;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.inmethod.grid.IDataSource;
import com.inmethod.grid.IGridSortState;
import com.inmethod.grid.IGridSortState.ISortStateColumn;

import sk.qbsw.sed.client.exception.CSystemFailureException;
import sk.qbsw.sed.client.model.request.CRequestRecord;
import sk.qbsw.sed.client.model.request.CSubordinateRequestsBrwFilterCriteria;
import sk.qbsw.sed.communication.service.IBrwRequestClientService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.web.ui.CSedSession;

public class CRequestDataSource implements IDataSource<CRequestRecord> {

	private static final long serialVersionUID = 1L;

	@SpringBean
	private IBrwRequestClientService service;

	private final int itemsPerPage;

	private final IModel<CSubordinateRequestsBrwFilterCriteria> filterModel;

	public CRequestDataSource(CompoundPropertyModel<CSubordinateRequestsBrwFilterCriteria> requestFilterModel) {
		Injector.get().inject(this);
		this.itemsPerPage = CSedSession.get().getUser().getTableRows();
		this.filterModel = requestFilterModel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IModel<CRequestRecord> model(CRequestRecord object) {
		return new Model<>(object);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void query(IQuery query, IQueryResult<CRequestRecord> result) {
		// default sortProperty
		String sortProperty = "dateFrom";
		boolean sortAsc = true;

		// is there any sorting
		if (query.getSortState().getColumns().size() > 0) {
			// get the most relevant column
			ISortStateColumn<Object> state = query.getSortState().getColumns().get(0);

			// get the column sort properties
			sortProperty = state.getPropertyName().toString();
			sortAsc = state.getDirection() == IGridSortState.Direction.ASC;
		}

		// get the actual items
		List<CRequestRecord> resultList = null;
		Long count = null;

		try {
			resultList = service.loadData(query.getFrom(), query.getFrom() == 0 ? itemsPerPage : query.getCount(), sortProperty, sortAsc, filterModel.getObject());
			count = service.count(filterModel.getObject());
		} catch (CBussinessDataException e) {
			Logger.getLogger(CRequestDataSource.class).error(e);
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
}
