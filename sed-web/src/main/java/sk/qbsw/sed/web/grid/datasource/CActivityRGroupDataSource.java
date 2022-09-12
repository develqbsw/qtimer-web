package sk.qbsw.sed.web.grid.datasource;

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
import sk.qbsw.sed.client.model.restriction.CGroupsAIData;
import sk.qbsw.sed.communication.service.IBrwActivityRGroupClientService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.web.ui.CSedSession;

public class CActivityRGroupDataSource implements IDataSource<CGroupsAIData> {

	private static final long serialVersionUID = 1L;

	@SpringBean
	private IBrwActivityRGroupClientService service;

	private final int itemsPerPage;

	/**
	 * Constructor.
	 */
	public CActivityRGroupDataSource() {
		Injector.get().inject(this);
		this.itemsPerPage = CSedSession.get().getUser().getTableRows();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IModel<CGroupsAIData> model(CGroupsAIData object) {
		return new Model<>(object);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void query(IQuery query, IQueryResult<CGroupsAIData> result) {
		// default sortProperty
		String sortProperty = "name";
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
		List<CGroupsAIData> resultList = null;
		Long count = null;

		try {
			resultList = service.loadData(query.getFrom(), query.getFrom() == 0 ? itemsPerPage : query.getCount(), sortProperty, sortAsc);
			count = service.count();
		} catch (CBussinessDataException e) {
			Logger.getLogger(CActivityRGroupDataSource.class).error(e);
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
