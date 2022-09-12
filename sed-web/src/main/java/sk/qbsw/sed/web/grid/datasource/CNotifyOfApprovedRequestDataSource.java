package sk.qbsw.sed.web.grid.datasource;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import sk.qbsw.sed.client.model.codelist.CUserSystemEmailFilterCriteria;
import sk.qbsw.sed.client.model.codelist.CUserSystemEmailRecord;
import sk.qbsw.sed.communication.service.IUserClientService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.web.ui.CSedSession;

public class CNotifyOfApprovedRequestDataSource implements IDataSource<CUserSystemEmailRecord> {
	private static final long serialVersionUID = 1L;

	private final int itemsPerPage;

	private List<CUserSystemEmailRecord> dataList;

	private List<CUserSystemEmailRecord> selectedList;

	private final IModel<CUserSystemEmailFilterCriteria> filterModel;

	@SpringBean
	private IUserClientService userService;

	private Long userRequestID;

	public CNotifyOfApprovedRequestDataSource(CompoundPropertyModel<CUserSystemEmailFilterCriteria> userFilterModel, Long entityID) {
		Injector.get().inject(this);
		this.itemsPerPage = CSedSession.get().getUser().getTableRows();
		this.filterModel = userFilterModel;
		this.userRequestID = entityID;
		this.loadData();

	}

	@Override
	public void query(IQuery query, IQueryResult<CUserSystemEmailRecord> result) {
		this.loadData();
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

		// filtering
		List<CUserSystemEmailRecord> filteredList = new ArrayList<>();
		if (filterModel.getObject().getName() == null && filterModel.getObject().getSelected() == false) {
			filteredList = dataList;
		} else if (filterModel.getObject().getName() != null && filterModel.getObject().getSelected() == true) {
			for (int i = 0; i < dataList.size(); i++) {
				if (dataList.get(i).getSelected().equals(filterModel.getObject().getSelected())
						&& ignoreAccent(dataList.get(i).getName()).toLowerCase().contains(ignoreAccent(filterModel.getObject().getName().toLowerCase()))) {
					filteredList.add(dataList.get(i));
				}
			}
		} else if (filterModel.getObject().getName() != null && filterModel.getObject().getSelected() == false) {
			for (int i = 0; i < dataList.size(); i++) {
				if (ignoreAccent(dataList.get(i).getName()).toLowerCase().contains(ignoreAccent(filterModel.getObject().getName().toLowerCase()))) {
					filteredList.add(dataList.get(i));
				}
			}
		} else if (filterModel.getObject().getName() == null && filterModel.getObject().getSelected() == true) {
			for (int i = 0; i < dataList.size(); i++) {
				if (dataList.get(i).getSelected().equals(filterModel.getObject().getSelected())) {
					filteredList.add(dataList.get(i));
				}
			}
		}

		// sorting
		if (sortProperty.equals("name")) {
			Collections.sort(filteredList, new NameComparator(sortAsc));
		} else if (sortProperty.equals("selected")) {
			Collections.sort(filteredList, new SelectedComparator(sortAsc));
		}

		// paging
		List<CUserSystemEmailRecord> list = new ArrayList<>();
		if (filteredList.size() > itemsPerPage) {
			long indexTo = query.getFrom() + (query.getFrom() == 0 ? itemsPerPage : query.getCount());

			// SED-780 - IndexOutOfBoundsException ak bol index > size
			if (indexTo > filteredList.size()) {
				indexTo = filteredList.size();
			}

			for (long i = query.getFrom(); i < indexTo; i++) {
				list.add(filteredList.get((int) i));
			}
		} else {
			list = filteredList;
		}

		result.setTotalCount(filteredList.size());

		result.setItems(list.iterator());
	}

	public void loadData() {

		try {
			dataList = userService.getAccounts4Notification(this.userRequestID);
		} catch (CBussinessDataException e) {
			Logger.getLogger(CPresenceEmailDataSource.class).error(e);
			throw new CSystemFailureException(e);
		}
		
		selectedList = new ArrayList<>();
		for (int i = 0; i < dataList.size(); i++) {
			if (dataList.get(i).getSelected()) {
				selectedList.add(dataList.get(i));
			}
		}
	}

	@Override
	public void detach() {
		// do nothing
	}

	private class NameComparator implements Comparator<CUserSystemEmailRecord> {
		private final boolean sortAsc;

		public NameComparator(boolean sortAsc) {
			this.sortAsc = sortAsc;
		}

		@Override
		public int compare(CUserSystemEmailRecord o1, CUserSystemEmailRecord o2) {
			int retval = o1.getName().compareToIgnoreCase(o2.getName());
			if (sortAsc == false)
				retval *= -1;
			return retval;
		}

	}

	private class SelectedComparator implements Comparator<CUserSystemEmailRecord> {
		private final boolean sortAsc;

		public SelectedComparator(boolean sortAsc) {
			this.sortAsc = sortAsc;
		}

		@Override
		public int compare(CUserSystemEmailRecord o1, CUserSystemEmailRecord o2) {
			int retval = o1.getSelected().compareTo(o2.getSelected());
			if (sortAsc == true)
				retval *= -1;
			return retval;
		}

	}

	private String ignoreAccent(String string) {
		return Normalizer.normalize(string, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	}

	@Override
	public IModel<CUserSystemEmailRecord> model(CUserSystemEmailRecord object) {
		return new Model<>(object);
	}

	public List<CUserSystemEmailRecord> getDataList() {
		return selectedList;
	}

	public void setDataList(List<CUserSystemEmailRecord> dataList) {
		this.selectedList = dataList;
	}

	public List<CUserSystemEmailRecord> getSelectedList() {
		return selectedList;
	}

	public void setSelectedList(List<CUserSystemEmailRecord> selectedList) {
		this.selectedList = selectedList;
	}

	public void setUserRequestId(Long entityID) {
		this.userRequestID = entityID;
	}

	public void setEntityId(Long entityID) {
		this.userRequestID = entityID;
	}
}
