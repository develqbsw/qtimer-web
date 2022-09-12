package sk.qbsw.sed.server.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.springframework.stereotype.Repository;

import sk.qbsw.core.dao.hibernate.AHibernateDao;
import sk.qbsw.sed.server.dao.IViewOrganizationTreeDao;
import sk.qbsw.sed.server.model.domain.CUser;
import sk.qbsw.sed.server.model.tree.org.CViewOrganizationTreeNode;

/**
 * Dao for accessing view V_ORGANIZATION_TREE.
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 */
@Repository
public class CViewOrganizationTreeNodeDao extends AHibernateDao<CViewOrganizationTreeNode> implements IViewOrganizationTreeDao {

	@SuppressWarnings("unchecked")
	public List<CViewOrganizationTreeNode> findByClientValidity(final Long clientId, final Boolean onlyValid) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CViewOrganizationTreeNode.class);
		criteria.add(Property.forName("clientId").eq(clientId));

		if ((onlyValid != null) && onlyValid) {
			criteria.add(Property.forName("isValid").eq(Boolean.TRUE));
		}
		criteria.addOrder(Order.asc("surname"));
		criteria.addOrder(Order.asc("name"));
		criteria.addOrder(Order.asc("parentId"));

		return criteria.list();
	}

	public List<CViewOrganizationTreeNode> findByClientUserValidity(final Long clientId, final Long userId, final Boolean onlyValid, final Boolean withoutMe) {
		return findByClientUserValidity(clientId, userId, onlyValid, withoutMe, Boolean.FALSE);
	}

	@SuppressWarnings("unchecked")
	public List<CViewOrganizationTreeNode> findByClientUserValidity(final Long clientId, final Long userId, final Boolean onlyValid, final Boolean withoutMe, Boolean adminFlag) {
		String withoutMeCondition = "";
		String validity = "";
		if ((onlyValid != null) && onlyValid) {
			validity = " c_flag_valid = " + onlyValid + " and ";
		}
		if (withoutMe) {
			withoutMeCondition = " and user_pk_id != " + userId;
		}
		final SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery("select * from v_organization_tree t where " + validity + " t.fk_client = " + clientId
				+ " and f_sed__get_is_tree_under(t.pk_id," + userId + ",true)=true " + withoutMeCondition + " order by fk_possition_superior desc");
		query.addEntity(CViewOrganizationTreeNode.class);

		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CViewOrganizationTreeNode> findDirectSubordinates(final Long userId) {
		final SQLQuery query = sessionFactory.getCurrentSession()
				.createSQLQuery("select * from v_organization_tree t where fk_possition_superior = (select pk_id from v_organization_tree where user_pk_id = " + userId + ") and c_flag_valid = true");
		query.addEntity(CViewOrganizationTreeNode.class);

		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CViewOrganizationTreeNode> findNotified(List<CUser> newListNotified) {

		if (!newListNotified.isEmpty()) {
			List<Long> ids = new ArrayList<>();
			for (CUser u : newListNotified) {
				ids.add(u.getId());
			}

			final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CViewOrganizationTreeNode.class);
			criteria.add(Property.forName("userId").in(ids));

			criteria.addOrder(Order.asc("surname"));
			criteria.addOrder(Order.asc("name"));
			criteria.addOrder(Order.asc("parentId"));

			return criteria.list();
		}

		return new ArrayList<>();
	}
}
