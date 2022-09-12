package sk.qbsw.sed.server.dao;

import java.util.List;

import sk.qbsw.core.dao.IDao;
import sk.qbsw.sed.server.model.codelist.CLegalForm;

/**
 * Legal form dao.
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 * 
 */
public interface ILegalFormDao extends IDao<CLegalForm> {
	public List<CLegalForm> getAllValid();
}
