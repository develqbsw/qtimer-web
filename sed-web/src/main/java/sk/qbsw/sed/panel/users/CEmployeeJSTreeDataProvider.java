package sk.qbsw.sed.panel.users;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import sk.qbsw.sed.client.model.ITreeActions;
import sk.qbsw.sed.client.model.tree.org.CViewOrganizationTreeNodeRecord;
import sk.qbsw.sed.communication.service.IOrganizationTreeClientService;
import sk.qbsw.sed.component.jstree.CJSTreeModel;
import sk.qbsw.sed.component.jstree.CJSTreeProvider;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.web.ui.CSedSession;

public class CEmployeeJSTreeDataProvider extends CJSTreeProvider<CViewOrganizationTreeNodeRecord> {

	private static final long serialVersionUID = 1L;

	@SpringBean
	private IOrganizationTreeClientService organizationTreeService;

	private List<CViewOrganizationTreeNodeRecord> organizationTree = null;

	private Map<Long, CViewOrganizationTreeNodeRecord> recordMap;
	private Map<Long, Long> parentMap;
	private Map<Long, Set<Long>> childrenMap;
	private Set<Long> rootSet;

	/**
	 * Last time of data being loaded
	 */
	private Date lastLoadTime;

	/**
	 * Construct.
	 * 
	 * @throws CBussinessDataException
	 */
	public CEmployeeJSTreeDataProvider() throws CBussinessDataException {
		Injector.get().inject(this);
		loadData(Boolean.TRUE);
	}

	@Override
	public void loadData(Boolean onlyValid) throws CBussinessDataException {
		organizationTree = organizationTreeService.loadTreeByClient(CSedSession.get().getUser().getClientInfo().getClientId(), onlyValid);
		init(organizationTree);
		lastLoadTime = new Date();
	}

	private void init(List<CViewOrganizationTreeNodeRecord> organizationTree) {
		recordMap = new HashMap<>();
		parentMap = new HashMap<>();
		childrenMap = new HashMap<>();
		rootSet = new LinkedHashSet<>();
		for (CViewOrganizationTreeNodeRecord record : organizationTree) {
			recordMap.put(record.getId(), record);
			parentMap.put(record.getId(), record.getParentId());
			if (record.getParentId() != null) {
				Set<Long> childrenSet = childrenMap.get(record.getParentId());
				if (childrenSet == null) {
					childrenSet = new LinkedHashSet<>();
					childrenMap.put(record.getParentId(), childrenSet);
				}
				childrenSet.add(record.getId());
			} else {
				rootSet.add(record.getId());
			}
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
		List<CViewOrganizationTreeNodeRecord> list = new ArrayList<>();
		for (Long id : rootSet) {
			list.add(recordMap.get(id));
		}
		return list.iterator();
	}

	@Override
	public boolean hasChildren(CViewOrganizationTreeNodeRecord parent) {
		Set<Long> childrenSet = childrenMap.get(parent.getId());
		if (childrenSet != null) {
			return childrenSet.size() > 0;
		}
		return false;
	}

	@Override
	public Iterator<CViewOrganizationTreeNodeRecord> getChildren(final CViewOrganizationTreeNodeRecord parent) {
		List<CViewOrganizationTreeNodeRecord> list = new ArrayList<>();
		Set<Long> childrenSet = childrenMap.get(parent.getId());
		if (childrenSet != null && childrenSet.size() > 0) {
			for (Long id : childrenSet) {
				list.add(recordMap.get(id));
			}
		}
		return list.iterator();
	}

	/**
	 * Creates a {@link CViewOrganizationTreeNodeRecordModel}.
	 */
	@Override
	public IModel<CViewOrganizationTreeNodeRecord> model(CViewOrganizationTreeNodeRecord CViewOrganizationTreeNodeRecord) {
		return null;
	}

	public void remove(CViewOrganizationTreeNodeRecord model) {
		// do nothing
	}

	public void add(CViewOrganizationTreeNodeRecord model) {
		// do nothing
	}

	public void addBefore(CViewOrganizationTreeNodeRecord model) {
		// do nothing
	}

	@Override
	public void moveNode(Long treeNodeFrom, Long treeNodeTo) throws CBussinessDataException {
		// SED-573 vykonávať vždy rovnakú akciu ako keď je aktuálne v rozbaľovacom zozname vybratá možnosť "Presunúť s podriadenými".
		organizationTreeService.move(treeNodeFrom, treeNodeTo, ITreeActions.MOVE_W_SUB, lastLoadTime);
	}

	@Override
	public CJSTreeModel transform(CViewOrganizationTreeNodeRecord object) {

		CJSTreeModel model = new CJSTreeModel();
		model.setId(object.getId());
		model.setText(object.toString());
		model.setValid(object.getIsValid());
		model.setLi_attr(model.getLi_attr());

		Iterator<CViewOrganizationTreeNodeRecord> iter = getChildren(object);
		List<CJSTreeModel> children = new ArrayList<>();
		
		while (iter.hasNext()) {
			children.add(transform(iter.next()));
		}
		
		model.setChildren(children);
		return model;
	}

	@Override
	public CViewOrganizationTreeNodeRecord getById(Long id) {
		return recordMap.get(id);
	}
}
