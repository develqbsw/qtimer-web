package sk.qbsw.sed.server.dao;

import java.util.List;

import sk.qbsw.core.dao.IDao;
import sk.qbsw.sed.server.model.domain.CClient;

/**
 * Dao for client
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 */
public interface IClientDao extends IDao<CClient> {
	
	/**
	 * Finds client by VAT (unique)
	 * 
	 * @param vat
	 * @return
	 */
	public CClient findByVatNo(String vat);

	/**
	 * Finds client by TAX (unique)
	 * 
	 * @param vat
	 * @return
	 */
	public CClient findByTaxNo(String tax);

	/**
	 * Finds client by IDNo (unique)
	 * 
	 * @param vat
	 * @return
	 */
	public CClient findByIdNo(String id);

	public CClient findClientById(Long id);

	/**
	 * Returns all application clients
	 * 
	 * @return
	 */
	public List<CClient> getApplicationClients();
}
