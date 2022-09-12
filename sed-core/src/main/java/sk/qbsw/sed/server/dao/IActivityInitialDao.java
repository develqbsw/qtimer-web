package sk.qbsw.sed.server.dao;

import java.util.List;

import sk.qbsw.core.dao.IDao;
import sk.qbsw.sed.server.model.codelist.CActivityInitial;

/**
 * Interface for accessing Activity object
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 */
public interface IActivityInitialDao extends IDao<CActivityInitial> {
	public List<CActivityInitial> findAll();
}
