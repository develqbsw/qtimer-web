package sk.qbsw.sed.server.dao.hibernate;

import org.springframework.stereotype.Repository;

import sk.qbsw.core.dao.hibernate.AHibernateDao;
import sk.qbsw.sed.server.dao.IUserTypeDao;
import sk.qbsw.sed.server.model.codelist.CUserType;

/**
 * DAO implementation using hibernate
 * 
 * @author Dalibor Rak
 * 
 */
@Repository
public class CUserTypeDao extends AHibernateDao<CUserType> implements IUserTypeDao {

}
