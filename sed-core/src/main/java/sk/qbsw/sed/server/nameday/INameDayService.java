package sk.qbsw.sed.server.nameday;

import org.springframework.cache.annotation.Cacheable;

import sk.qbsw.sed.framework.security.exception.CBusinessException;

public interface INameDayService {

	@Cacheable(value = "namesdayCache", key = "#language")
	public String getNamesday(String language) throws CBusinessException;
}
