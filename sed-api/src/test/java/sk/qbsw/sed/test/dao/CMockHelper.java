package sk.qbsw.sed.test.dao;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.stereotype.Component;

/**
 * The helper for mocking.
 *
 * @author Tomas Lauro
 * 
 * @version 1.9.0
 * @since 1.9.0
 */
@Component ("mockHelper")
public class CMockHelper 
{
	/* (non-Javadoc)
	 * @see sk.qbsw.core.testing.mock.IMockHelper#unwrapSpringProxyObject(java.lang.Object)
	 */
	@SuppressWarnings ("unchecked")
	public <T>T unwrapSpringProxyObject (T object) throws Exception
	{
		if (AopUtils.isAopProxy(object) && object instanceof Advised)
		{
			return (T) ((Advised) object).getTargetSource().getTarget();
		}
		else
		{
			throw new Exception("The object is not a aop advise proxy object");
		}
	}
}
