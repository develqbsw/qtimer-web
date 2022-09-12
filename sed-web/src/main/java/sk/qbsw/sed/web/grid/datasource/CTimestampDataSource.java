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
import sk.qbsw.sed.client.model.timestamp.CSubrodinateTimeStampBrwFilterCriteria;
import sk.qbsw.sed.client.model.timestamp.CTimeStampRecord;
import sk.qbsw.sed.communication.service.IBrwTimeStampService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.panel.timesheet.editable.CTimesheetEditableTablePanel;
import sk.qbsw.sed.web.ui.CSedSession;

/**
 * Simple DataSource that load timestamps.
 * 
 * @author Pavol Lobb
 */
public class CTimestampDataSource implements IDataSource<CTimeStampRecord> {

	private static final long serialVersionUID = 1L;

	@SpringBean
	private IBrwTimeStampService service;

	private final IModel<CSubrodinateTimeStampBrwFilterCriteria> filterModel;

	private final int itemsPerPage;

	private final CTimesheetEditableTablePanel cTimesheetEditableTablePanel;

	/**
	 * Constructor.
	 */
	public CTimestampDataSource(IModel<CSubrodinateTimeStampBrwFilterCriteria> filterModel, CTimesheetEditableTablePanel cTimesheetEditableTablePanel) {
		Injector.get().inject(this);
		this.filterModel = filterModel;
		this.itemsPerPage = CSedSession.get().getUser().getTableRows();
		this.cTimesheetEditableTablePanel = cTimesheetEditableTablePanel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IModel<CTimeStampRecord> model(CTimeStampRecord object) {
		return new Model<>(object);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void query(IQuery query, IQueryResult<CTimeStampRecord> result) {
		// default sortProperty
		String sortProperty = "timeFrom";
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
		List<CTimeStampRecord> resultList = null;
		Long count = null;

		try {
			resultList = service.loadData(query.getFrom(), query.getFrom() == 0 ? itemsPerPage : query.getCount(), sortProperty, sortAsc, filterModel.getObject());
			count = service.count(filterModel.getObject());
		} catch (CBussinessDataException e) {
			Logger.getLogger(CTimestampDataSource.class).error(e);
			throw new CSystemFailureException(e);
		}

		// determine the total count
		result.setTotalCount(count);

		result.setItems(resultList.iterator());

		/*
		 * ak idem z menu Vybrať ďalších zamestnancov, tak zakážem editáciu časových značiek, v datasource je to kvôli pagingu 
		 * - pri prepnutí čísla stránky sa volá táto query() metóda, tak tu musím rozhodnúť, či povolím alebo zakážem editáciu
		 */
		if (this.cTimesheetEditableTablePanel.getFromOtherEmployees()) {
			this.cTimesheetEditableTablePanel.setEditingActions(true);
		} else {
			this.cTimesheetEditableTablePanel.setEditingActions(false);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void detach() {
		// do nothing
	}
}
