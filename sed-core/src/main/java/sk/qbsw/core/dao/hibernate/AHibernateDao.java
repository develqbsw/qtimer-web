package sk.qbsw.core.dao.hibernate;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.springframework.core.GenericTypeResolver;

/**
 * Defines basic methods for each DAO (findById and saveOrUpdate)
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 *
 * @param <T> object which will be managed by DAO
 */
public class AHibernateDao<T> {
	private Class<T> objectClass;

	@Resource
	protected SessionFactory sessionFactory;

	/**
	 * The only constructor
	 * 
	 * @param objectClass class of managed object
	 */
	@SuppressWarnings("unchecked")
	public AHibernateDao() {
		this.objectClass = (Class<T>) GenericTypeResolver.resolveTypeArgument(getClass(), AHibernateDao.class);
	}

	/**
	 * Finds object according entered Id
	 * 
	 * @param id id to search
	 * @return <T> as object
	 */
	@SuppressWarnings("unchecked")
	public T findById(Long id) {
		return (T) sessionFactory.getCurrentSession().get(objectClass, id);
	}

	/**
	 * Saves or updates object
	 * 
	 * @param object object to save or update
	 */
	public void saveOrUpdate(T object) {
		sessionFactory.getCurrentSession().saveOrUpdate(object);
	}

	/**
	 * 
	 * bodku pouzivame ak chceme zoradovat podla stlpca v join tabulke
	 * 
	 * @param criteria
	 * @param from
	 * @param count
	 * @param sortProperty
	 * @param sortAsc
	 */
	protected void prepareOrderAndCount(Criteria criteria, final int from, final int count, String sortProperty, boolean sortAsc) {

		// takto mozem retazit stringy aby som mohol orderovat podla viacerych
		// kriterii retazi sa string+string2
		if (sortProperty.contains("+")) {
			sortProperty = sortProperty.replaceAll("\\s+", "");
			String[] property2 = sortProperty.split("\\+");
			String[] property = null;
			// vytvorim si hashset aby som mal unikatne aliasy
			Set<String> setAliases = new HashSet<>();
			for (String s : property2) {
				// ak string obsahuje bodku, to znamena ze pristupujem cez danu
				// entitu k dalsej a na to musim vytvorit alias
				if (s.contains(".")) {
					property = s.split("\\.");
					setAliases.add(property[0]);
				}
				if (sortAsc) {
					criteria.addOrder(Order.asc(s));
				} else {
					criteria.addOrder(Order.desc(s));
				}

			}
			for (String s : setAliases) {
				criteria.createAlias(s, s);
			}
		} else {
			if (sortProperty.contains(".")) {
				String[] property = sortProperty.split("\\.");
				criteria.createAlias(property[0], property[0]);
			}

			if (sortAsc) {
				criteria.addOrder(Order.asc(sortProperty));
			} else {
				criteria.addOrder(Order.desc(sortProperty));
			}
		}

		criteria.setFirstResult(from);
		criteria.setFetchSize(count);
	}
}
