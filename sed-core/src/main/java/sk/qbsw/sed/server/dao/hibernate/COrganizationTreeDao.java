package sk.qbsw.sed.server.dao.hibernate;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Property;
import org.springframework.stereotype.Repository;

import sk.qbsw.core.dao.hibernate.AHibernateDao;
import sk.qbsw.sed.client.exception.CSystemFailureException;
import sk.qbsw.sed.client.model.ITreeActions;
import sk.qbsw.sed.client.ui.localization.CClientExceptionsMessages;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.server.dao.IOrganizationTreeDao;
import sk.qbsw.sed.server.model.domain.COrganizationTree;

/**
 * DAO for accessing Organization Tree
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 * 
 */
@Repository
public class COrganizationTreeDao extends AHibernateDao<COrganizationTree> implements IOrganizationTreeDao {

	public void move(Long source, Long destination, String mode, Long userId) throws CBusinessException {

		int action = -1;

		if (ITreeActions.MOVE_SUB.equals(mode)) {
			action = 3;
		} else if (ITreeActions.MOVE_W_SUB.equals(mode)) {
			action = 1;
		} else if (ITreeActions.MOVE_WO_SUB.equals(mode)) {
			action = 2;
		} else if (ITreeActions.SWITCH.equals(mode)) {
			action = 4;
		}

		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery("select f_sed__move_tree(" + source + "," + destination + "," + action + "," + userId + ")");

		String retVal = query.list().get(0).toString();

		if ("0".equals(retVal)) {
			// do nothing
		} else if ("1".equals(retVal)) {
			throw new CBusinessException(CClientExceptionsMessages.TREE_INVALID_DESTINATION);
		} else if ("2".equals(retVal)) {
			throw new CBusinessException(CClientExceptionsMessages.TREE_CYCLE_SUB);
		} else if ("3".equals(retVal)) {
			throw new CBusinessException(CClientExceptionsMessages.TREE_CYCLE_SUPER);
		} else if ("4".equals(retVal)) {
			throw new CBusinessException(CClientExceptionsMessages.TREE_SWITCH_WITH_ROOT);
		} else {
			throw new CSystemFailureException("Unknown response from PLSQL", null);
		}
	}

	public COrganizationTree findParentTree(Long ownerId) throws CBusinessException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(COrganizationTree.class);
		criteria.add(Property.forName("owner.id").eq(ownerId));

		return (COrganizationTree) criteria.uniqueResult();
	}
}