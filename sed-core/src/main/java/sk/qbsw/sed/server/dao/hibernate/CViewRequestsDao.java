package sk.qbsw.sed.server.dao.hibernate;

import org.springframework.stereotype.Repository;

import sk.qbsw.core.dao.hibernate.AHibernateDao;
import sk.qbsw.sed.server.dao.IViewRequestsDao;
import sk.qbsw.sed.server.model.brw.CViewRequest;

/**
 * DAO accessing DB for browser data
 * 
 * @author Dalibor Rak
 * 
 * @version 0.1
 * @since 0.1
 */
@Repository
public class CViewRequestsDao extends AHibernateDao<CViewRequest> implements IViewRequestsDao {

}
