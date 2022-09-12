package sk.qbsw.core.dao;

/**
 * Base DAO interface. Should be extended by specific DAOs
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 * 
 * @param <T> object which will be covered by DAO interface
 */
public interface IDao<T> {

	/**
	 * Finds object according ID
	 * 
	 * @param id id of object to search
	 * @return searched object or null
	 */
	public T findById(Long id);

	/**
	 * saves or updates object
	 * 
	 * @param object object to update or save
	 */
	public void saveOrUpdate(T object);
}
