package sk.qbsw.sed.web.ui.tree;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.wicket.MetaDataKey;
import org.apache.wicket.Session;

import sk.qbsw.sed.client.model.tree.org.CViewOrganizationTreeNodeRecord;

public class CEmployeeTreeExpansion implements Set<CViewOrganizationTreeNodeRecord>, Serializable {
	private static final long serialVersionUID = 1L;

	private static MetaDataKey<CEmployeeTreeExpansion> key = new MetaDataKey<CEmployeeTreeExpansion>() {
		private static final long serialVersionUID = 1L;
	};

	private Set<Long> ids = new HashSet<>();

	private boolean inverse;

	public void expandAll() {
		ids.clear();

		inverse = true;
	}

	public void collapseAll() {
		ids.clear();

		inverse = false;
	}

	@Override
	public boolean add(CViewOrganizationTreeNodeRecord CViewOrganizationTreeNodeRecord) {
		if (inverse) {
			return ids.remove(CViewOrganizationTreeNodeRecord.getId());
		} else {
			return ids.add(CViewOrganizationTreeNodeRecord.getId());
		}
	}

	@Override
	public boolean remove(Object o) {
		CViewOrganizationTreeNodeRecord CViewOrganizationTreeNodeRecord = (CViewOrganizationTreeNodeRecord) o;

		if (inverse) {
			return ids.add(CViewOrganizationTreeNodeRecord.getId());
		} else {
			return ids.remove(CViewOrganizationTreeNodeRecord.getId());
		}
	}

	@Override
	public boolean contains(Object o) {
		CViewOrganizationTreeNodeRecord CViewOrganizationTreeNodeRecord = (CViewOrganizationTreeNodeRecord) o;

		if (inverse) {
			return !ids.contains(CViewOrganizationTreeNodeRecord.getId());
		} else {
			return ids.contains(CViewOrganizationTreeNodeRecord.getId());
		}
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isEmpty() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <A> A[] toArray(A[] a) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<CViewOrganizationTreeNodeRecord> iterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends CViewOrganizationTreeNodeRecord> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Get the expansion for the session.
	 * 
	 * @return expansion
	 */
	public static CEmployeeTreeExpansion get() {
		CEmployeeTreeExpansion expansion = Session.get().getMetaData(key);
		if (expansion == null) {
			expansion = new CEmployeeTreeExpansion();

			Session.get().setMetaData(key, expansion);
		}
		return expansion;
	}
}
