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
import sk.qbsw.sed.client.ui.screen.restriction.users.CEmployeeRecord;
import sk.qbsw.sed.communication.service.IBrwEmployeeClientService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.web.ui.CSedSession;

public class CEmployeeDataSource implements IDataSource<CEmployeeRecord> {

	private static final long serialVersionUID = 1L;

	@SpringBean
	private IBrwEmployeeClientService service;

	private final int itemsPerPage;

	private final IModel<String> filterModel;

	/**
	 * Constructor.
	 * 
	 * @param projectFilterModel
	 */
	public CEmployeeDataSource(CompoundPropertyModel<String> projectFilterModel) {
		Injector.get().inject(this);
		this.itemsPerPage = CSedSession.get().getUser().getTableRows();
		this.filterModel = projectFilterModel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IModel<CEmployeeRecord> model(CEmployeeRecord object) {
		return new Model<>(object);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void query(IQuery query, IQueryResult<CEmployeeRecord> result) {
		// default sortProperty
		String sortProperty = "surname";
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
		List<CEmployeeRecord> resultList = null;
		Long count = null;

		try {
			resultList = service.loadData(query.getFrom(), query.getFrom() == 0 ? itemsPerPage : query.getCount(), sortProperty, sortAsc, filterModel.getObject());
			count = service.count(filterModel.getObject());
		} catch (CBussinessDataException e) {
			Logger.getLogger(CEmployeeDataSource.class).error(e);
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
