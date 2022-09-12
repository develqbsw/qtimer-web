package sk.qbsw.sed.web.ui.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import sk.qbsw.sed.client.model.tree.org.CViewOrganizationTreeNodeRecord;
import sk.qbsw.sed.communication.service.IOrganizationTreeClientService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.web.ui.CSedSession;

public class CEmployeeTreeProvider implements ITreeProvider<CViewOrganizationTreeNodeRecord> {

	private static final long serialVersionUID = 1L;

	@SpringBean
	private IOrganizationTreeClientService organizationTreeService;

	private List<CViewOrganizationTreeNodeRecord> organizationTree = new ArrayList<>();

	private Map<Long, CViewOrganizationTreeNodeRecord> recordMap;

	private Boolean all;

	private Boolean notified;

	/**
	 * Construct.
	 */
	public CEmployeeTreeProvider() {
		Injector.get().inject(this);
	}

	public void init(List<CViewOrganizationTreeNodeRecord> organizationTree) {

		recordMap = new HashMap<>();
		for (CViewOrganizationTreeNodeRecord record : organizationTree) {
			recordMap.put(record.getUserId(), record);
		}
	}

	/**
	 * Nothing to do.
	 */
	@Override
	public void detach() {
		// do nothing
	}

	@Override
	public Iterator<CViewOrganizationTreeNodeRecord> getRoots() {
		List<CViewOrganizationTreeNodeRecord> roots = new ArrayList<>();

		for (CViewOrganizationTreeNodeRecord child : organizationTree) {
			if (all != null && all) {
				if (child.getParentId() == null || this.notified) {
					roots.add(child);
				}
			} else {
				if (child.getUserId().equals(CSedSession.get().getUser().getUserId())) {
					roots.add(child);
				}
			}
		}

		return roots.iterator();
	}

	@Override
	public boolean hasChildren(CViewOrganizationTreeNodeRecord parent) {
		for (CViewOrganizationTreeNodeRecord child : organizationTree) {
			if (child.getParentId() != null && child.getParentId().equals(parent.getId())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Iterator<CViewOrganizationTreeNodeRecord> getChildren(final CViewOrganizationTreeNodeRecord parent) {
		List<CViewOrganizationTreeNodeRecord> children = new ArrayList<>();

		for (CViewOrganizationTreeNodeRecord child : organizationTree) {
			if (child.getParentId() != null && child.getParentId().equals(parent.getId())) {
				children.add(child);
			}
		}
		return children.iterator();
	}

	/**
	 * Creates a {@link CViewOrganizationTreeNodeRecordModel}.
	 */
	@Override
	public IModel<CViewOrganizationTreeNodeRecord> model(CViewOrganizationTreeNodeRecord viewOrganizationTreeNodeRecord) {
		return new CViewOrganizationTreeNodeRecordModel(viewOrganizationTreeNodeRecord);
	}

	private class CViewOrganizationTreeNodeRecordModel extends LoadableDetachableModel<CViewOrganizationTreeNodeRecord> {
		private static final long serialVersionUID = 1L;

		private final Long id;

		private CViewOrganizationTreeNodeRecord treeNodeRecord;

		public CViewOrganizationTreeNodeRecordModel(CViewOrganizationTreeNodeRecord treeNodeRecord) {
			super(treeNodeRecord);

			id = treeNodeRecord.getId();
			this.treeNodeRecord = treeNodeRecord;
		}

		@Override
		protected CViewOrganizationTreeNodeRecord load() {
			return treeNodeRecord;
		}

		/**
		 * Important! Models must be identifyable by their contained object.
		 */
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof CViewOrganizationTreeNodeRecordModel) {
				return ((CViewOrganizationTreeNodeRecordModel) obj).getObject().getId().equals(id);
			}
			return false;
		}

		/**
		 * Important! Models must be identifyable by their contained object.
		 */
		@Override
		public int hashCode() {
			return id.hashCode();
		}
	}

	public CViewOrganizationTreeNodeRecord getById(Long id) {
		return recordMap.get(id);
	}

	public void getNotifiedUsers(List<CViewOrganizationTreeNodeRecord> listNotifiedEmployees) {
		this.all = true;
		this.notified = true;
		organizationTree = listNotifiedEmployees;
		this.init(listNotifiedEmployees);

	}

	public void getAllEmployees(Boolean onlyValid, Boolean all) {
		this.all = all;
		this.notified = false;

		try {
			if (all) {
				organizationTree = organizationTreeService.loadTreeByClient(CSedSession.get().getUser().getClientInfo().getClientId(), onlyValid);
			} else {
				organizationTree = organizationTreeService.loadTreeByClientUser(
						CSedSession.get().getUser().getClientInfo().getClientId(), CSedSession.get().getUser().getUserId(), onlyValid, Boolean.FALSE);
			}
		} catch (CBussinessDataException e) {
			Logger.getLogger(CEmployeeTreeProvider.class).error(e);
		}

		this.init(organizationTree);
	}
}
