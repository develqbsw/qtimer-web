package sk.qbsw.sed.server.dao.hibernate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import sk.qbsw.core.dao.hibernate.AHibernateDao;
import sk.qbsw.sed.client.model.IUserTypes;
import sk.qbsw.sed.framework.report.model.CReportModel;
import sk.qbsw.sed.server.dao.IUserDao;
import sk.qbsw.sed.server.model.domain.CUser;
import sk.qbsw.sed.server.model.domain.CZone;
import sk.qbsw.sed.server.model.report.CWorkplaceDataModel;

/**
 * DAO to object CUser working with table t_user
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 */
@Repository
public class CUserDao extends AHibernateDao<CUser> implements IUserDao {

	private static final String VALID = "valid";
	private static final String CLIENT_ID = "client.id";
	private static final String PIN_CODE = "pinCode";
	private static final String TYPE_ID = "type.id";
	
	private static final String SELECT_FROM_T_USER_WHERE_FK_CLIENT = "select * from t_user u where u.fk_client = ";
	
	private static final String AND_FLAG_VALID_TRUE = " and u.c_flag_valid = true ";
	
	/**
	 * @see IUserDao#findByLogin(String)
	 */
	@Override
	public CUser findByLogin(final String login) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CUser.class);
		criteria.add(Property.forName("loginLong").eq(login));

		return (CUser) criteria.uniqueResult();
	}

	@Override
	public CUser findByAutoLoginToken(final String token) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CUser.class);
		criteria.add(Property.forName("autoLoginToken").eq(token.trim()));

		return (CUser) criteria.uniqueResult();
	}

	/**
	 * @see CUserDao#findOtherMain(CUser)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CUser> findOtherMain(final CUser user) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CUser.class);
		criteria.add(Property.forName("id").ne(user.getId()));
		criteria.add(Property.forName("client").eq(user.getClient()));
		criteria.add(Property.forName("type").eq(user.getType()));
		criteria.add(Property.forName(VALID).eq(Boolean.TRUE));
		criteria.add(Property.forName("main").eq(Boolean.TRUE));

		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CUser> findSubordinate(final CUser user, final boolean includeMe, final boolean showOnlyValid, final Long userTypeId) {
		String userTypeCond = "";

		if (userTypeId != null) {
			userTypeCond = " and u.fk_user_type = " + userTypeId + " ";
		}

		String validCondition = "";
		if (showOnlyValid) {
			validCondition = AND_FLAG_VALID_TRUE;
		}

		SQLQuery query;
		if (includeMe) {
			query = sessionFactory.getCurrentSession().createSQLQuery(SELECT_FROM_T_USER_WHERE_FK_CLIENT + user.getClient().getId() + userTypeCond + validCondition
					+ " and (f_sed__get_is_user_under(u.pk_id, " + user.getId() + " ," + includeMe + ")=true or (u.pk_id = " + user.getId() + ") ) order by u.c_surname");
		} else {
			query = sessionFactory.getCurrentSession().createSQLQuery(SELECT_FROM_T_USER_WHERE_FK_CLIENT + user.getClient().getId() + userTypeCond + validCondition
					+ " and f_sed__get_is_user_under(u.pk_id, " + user.getId() + " , false)=true order by u.c_surname");
		}
		query.addEntity(CUser.class);

		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CUser> findSimilarToLogin(final String login) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CUser.class);
		criteria.add(Property.forName("loginLong").like(login, MatchMode.START));

		return criteria.list();
	}

	/**
	 * @see IUserDao#findAllEmployees(CUser, Long)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CUser> findAllEmployees(CUser user, Long userTypeId, Boolean showOnlyValid) {

		String userTypeCond = "";

		if (userTypeId != null) {
			userTypeCond = " and u.fk_user_type = " + userTypeId + " ";
		}

		String validCondition = "";
		if (showOnlyValid) {
			validCondition = AND_FLAG_VALID_TRUE;
		}

		final SQLQuery query = sessionFactory.getCurrentSession()
				.createSQLQuery(SELECT_FROM_T_USER_WHERE_FK_CLIENT + user.getClient().getId() + userTypeCond + validCondition + " order by u.c_surname, u.c_name");
		query.addEntity(CUser.class);

		return query.list();
	}

	/**
	 * @see IUserDao#findClientUsersWithoutLockInfoRecords(Long, Long, Boolean)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CUser> findClientUsersWithoutLockInfoRecords(Long clientId, Long userTypeId, Boolean valid) {
		String userTypeCond = "";

		if (userTypeId != null) {
			userTypeCond = " and u0.fk_user_type = " + userTypeId + " ";
		}

		String validCond = "";
		if (valid != null) {
			validCond = " and u0.c_flag_valid = " + valid.toString() + " ";
		}
		StringBuilder sbQuery = new StringBuilder();
		sbQuery.append("select * from t_user u0 where u0.fk_client = " + clientId + userTypeCond + validCond + " and (u0.pk_id) not in ");
		sbQuery.append("(");
		sbQuery.append(" select u.pk_id from t_user u, t_lock_date ld where u.fk_client = " + clientId + " and u.pk_id = ld.fk_user_owner ");
		sbQuery.append(")");
		final SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sbQuery.toString());
		query.addEntity(CUser.class);

		return query.list();
	}

	/**
	 * @see IUserDao#findAllEmployeesByValidFlag(Long, boolean, Long)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CUser> findAllEmployeesByValidFlag(Long clientId, boolean showOnlyValid, Long userTypeId) {

		String userTypeCond = "";
		if (userTypeId != null) {
			userTypeCond = " and u.fk_user_type = " + userTypeId + " ";
		}
		String validCondition = "";
		if (showOnlyValid) {
			validCondition = AND_FLAG_VALID_TRUE;
		}

		final SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(SELECT_FROM_T_USER_WHERE_FK_CLIENT + clientId + userTypeCond + validCondition + " order by u.c_surname");
		query.addEntity(CUser.class);

		return query.list();
	}

	/**
	 * @see IUserDao#getPinCodes(Long)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getCardCodes(Long clientId) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CUser.class);
		criteria.add(Property.forName(CLIENT_ID).eq(clientId));
		criteria.add(Property.forName("cardCode").isNotNull());
		List<CUser> users = criteria.list();

		List<String> cardCodes = new ArrayList<>();
		for (CUser user : users) {
			cardCodes.add(user.getCardCode());
		}

		return cardCodes;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getPinCodes(Long clientId) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CUser.class);
		criteria.add(Property.forName(CLIENT_ID).eq(clientId));
		criteria.add(Property.forName(PIN_CODE).isNotNull());
		List<CUser> users = criteria.list();

		List<String> pins = new ArrayList<>();
		for (CUser user : users) {
			pins.add(user.getPinCode());
		}

		return pins;
	}

	/**
	 * @see IUserDao#getPinCodeSalts(Long)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getPinCodeSalts(Long clientId) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CUser.class);
		criteria.add(Property.forName(CLIENT_ID).eq(clientId));
		criteria.add(Property.forName(PIN_CODE).isNotNull());
		List<CUser> users = criteria.list();

		List<String> salts = new ArrayList<>();
		for (CUser user : users) {
			salts.add(user.getPinCodeSalt());
		}

		return salts;
	}

	/**
	 * @see IUserDao#getPinCodeSalts(Long)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getCardCodeSalts(Long clientId) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CUser.class);
		criteria.add(Property.forName(CLIENT_ID).eq(clientId));
		criteria.add(Property.forName("cardCode").isNotNull());
		List<CUser> users = criteria.list();

		List<String> salts = new ArrayList<>();
		for (CUser user : users) {
			salts.add(user.getCardCodeSalt());
		}

		return salts;
	}

	/**
	 * @see IUserDao#getApplicationUsers
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CUser> getClientUsers(Long clientId) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CUser.class);
		criteria.add(Property.forName(CLIENT_ID).eq(clientId));
		criteria.add(Property.forName(VALID).eq(true));
		criteria.addOrder(Order.asc("id"));
		return criteria.list();
	}

	/**
	 * @see IUserDao#findClientAdministratorAccount(Long)
	 */
	@Override
	public CUser findClientAdministratorAccount(Long clientId) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CUser.class);
		criteria.add(Property.forName(CLIENT_ID).eq(clientId));
		criteria.add(Property.forName(TYPE_ID).eq(IUserTypes.ORG_MAN));
		return (CUser) criteria.uniqueResult();
	}

	/**
	 * @see IUserDao#findClientReceptionAccount(Long)
	 */
	@Override
	public CUser findClientReceptionAccount(Long clientId) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CUser.class);
		criteria.add(Property.forName(CLIENT_ID).eq(clientId));
		criteria.add(Property.forName(TYPE_ID).eq(IUserTypes.RECEPTION));
		return (CUser) criteria.uniqueResult();
	}

	/**
	 * @see IUserDao#findBySystemEmailFlag(Long)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CUser> findBySystemEmailFlag(Long clientId) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CUser.class);
		criteria.add(Property.forName(CLIENT_ID).eq(clientId));
		criteria.add(Property.forName("receiveSystemEmail").eq(Boolean.TRUE));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CUser> findAllEmployeesForRestrictions(Long clientId, boolean valid, Long type, Integer startRow, Integer endRow, String sortProperty, boolean sortAsc, String name) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CUser.class);
		this.setCriteriaForEmployeesFR(criteria, name, clientId, valid, type);

		if (sortAsc) {
			criteria.addOrder(Order.asc(sortProperty));
		} else {
			criteria.addOrder(Order.desc(sortProperty));
		}

		criteria.setFirstResult(startRow);
		criteria.setFetchSize(endRow - startRow);

		return criteria.list();
	}

	@Override
	public Long count(Long clientId, boolean valid, Long type, String name) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CUser.class);
		this.setCriteriaForEmployeesFR(criteria, name, clientId, valid, type);
		criteria.setProjection(Projections.count("id"));
		return (Long) criteria.uniqueResult();
	}

	private void setCriteriaForEmployeesFR(Criteria criteria, String name, Long clientId, boolean valid, Long type) {
		criteria.add(Restrictions.eq(CLIENT_ID, clientId));
		criteria.add(Restrictions.eq(VALID, valid));
		criteria.add(Restrictions.eq(TYPE_ID, type));

		if (name != null && !"".equals(name)) {
			String[] inputs = name.split(" ");
			Criterion rest;
			if (inputs.length == 1) {
				rest = Restrictions.or(Restrictions.sqlRestriction("lower(unaccent(this_.c_name)) like lower(unaccent('%" + inputs[0] + "%')) "),
						Restrictions.sqlRestriction("lower(unaccent(this_.c_surname)) like lower(unaccent('%" + inputs[0] + "%')) "));
			} else if (inputs.length == 2) {
				rest = Restrictions.or(
						Restrictions.and(Restrictions.sqlRestriction("lower(unaccent(this_.c_name)) like lower(unaccent('%" + inputs[0] + "%')) "),
								Restrictions.sqlRestriction("lower(unaccent(this_.c_surname)) like lower(unaccent('%" + inputs[1] + "%')) ")),
						Restrictions.and(Restrictions.sqlRestriction("lower(unaccent(this_.c_name)) like lower(unaccent('%" + inputs[1] + "%')) "),
								Restrictions.sqlRestriction("lower(unaccent(this_.c_surname)) like lower(unaccent('%" + inputs[0] + "%')) ")));
			} else {
				rest = Restrictions.ne(VALID, valid);
			}
			criteria.add(rest);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CUser> findEmloyeesToNotified(CUser owner) {
		
		// SED-849 - vyberať len platných používateľov
		final SQLQuery query = sessionFactory.getCurrentSession()
				.createSQLQuery("select u.* from t_request_notification n JOIN t_user u ON u.pk_id = n.fk_user_notify where n.fk_user_request = " + owner.getId() + " and u.c_flag_valid = true");
		query.addEntity(CUser.class);

		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CReportModel> findUsersRecordsForWorkplaceReport(Long clientId, Boolean onlyValid) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CUser.class);

		criteria.add(Property.forName(VALID).eq(Boolean.TRUE)); // c_flag_valid = true
		criteria.add(Restrictions.eq(CLIENT_ID, clientId)); // fk_client
		criteria.add(Restrictions.eq(TYPE_ID, 3L)); // fk_user_type = 3
		criteria.addOrder(Order.asc("surname"));
		criteria.addOrder(Order.asc("name"));

		return this.convertModel(this.groupModel(criteria.list()));
	}

	private Map<Long, LinkedList<CWorkplaceDataModel>> groupModel(final List<CUser> data) {
		final Map<Long, LinkedList<CWorkplaceDataModel>> group = new LinkedHashMap<>();

		for (final CUser record : data) {
			LinkedList<CWorkplaceDataModel> model = new LinkedList<>();

			model = new LinkedList<>();

			CWorkplaceDataModel singleModel = new CWorkplaceDataModel();

			singleModel.setName(record.getSurname() + " " + record.getName());
			singleModel.setContactPhone(record.getPhone());
			singleModel.setOfficeNumber(record.getOfficeNumber());

			CZone zone = record.getZone();
			String zona = null;

			if (zone != null) {
				if (zone.getId().intValue() == 1) {
					zona = "3";
				} else if (zone.getId().intValue() == 2) {
					zona = "2";
				} else if (zone.getId().intValue() == 3) {
					zona = "4";
				} else {
					zona = null;
				}
			}

			singleModel.setZone(zona);

			model.add(singleModel);
			group.put(record.getId(), model);
		}

		return group;
	}

	private List<CReportModel> convertModel(final Map<Long, LinkedList<CWorkplaceDataModel>> group) {
		final List<CReportModel> reportModel = new ArrayList<>(group.size());
		for (final Long id : group.keySet()) {
			for (final CWorkplaceDataModel model : group.get(id)) {
				final CReportModel rowModel = new CReportModel();

				// 0 = name surname (meno a priezvisko)
				rowModel.setValue(0, model.getName());
				// 1 = office number (kancelária)
				rowModel.setValue(1, model.getOfficeNumber());
				// 2 = phone number (klapka)
				rowModel.setValue(2, model.getContactPhone());
				// 3 = zone (zóna)
				rowModel.setValue(3, model.getZone());

				reportModel.add(rowModel);
			}
		}
		return reportModel;
	}
}