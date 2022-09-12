package sk.qbsw.sed.server.ldap;

import java.util.Hashtable;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.SizeLimitExceededException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.log4j.Logger;

import sk.qbsw.sed.framework.security.exception.CBusinessException;

public class CLdapAuthentication {

	private static final String LDAPS_PREFIX = "ldaps://";

	private CLdapAuthentication() {
	}

	public static Boolean authenticate(String userName, String userPassword, ILdapAuthenticationConfigurator ldapAuthenticationConfigurator) throws CBusinessException {
		Hashtable<String, String> env = new Hashtable<>();

		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL,
				LDAPS_PREFIX + ldapAuthenticationConfigurator.getServerName() + ":" + ldapAuthenticationConfigurator.getServerPort() + "/" + ldapAuthenticationConfigurator.getUserSearchBaseDn());

		// To get rid of the PartialResultException when using Active Directory
		env.put(Context.REFERRAL, "follow");

		// Needed for the Bind (User Authorized to Query the LDAP server)
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, ldapAuthenticationConfigurator.getUserDn());
		env.put(Context.SECURITY_CREDENTIALS, ldapAuthenticationConfigurator.getUserPassword());

		DirContext ctx;
		try {
			ctx = new InitialDirContext(env);
		} catch (NamingException e) {
			Logger.getLogger(CLdapAuthentication.class).info("Nepodarilo sa pripojit k servru: " + ldapAuthenticationConfigurator.getServerName(), e);
			// skusim sa prihlasit na druhy ldap server
			env.put(Context.PROVIDER_URL, LDAPS_PREFIX + ldapAuthenticationConfigurator.getServerName2() + ":" + ldapAuthenticationConfigurator.getServerPort2() + "/"
					+ ldapAuthenticationConfigurator.getUserSearchBaseDn());
			Logger.getLogger(CLdapAuthentication.class).info("Pokus o pripojenie k servru: " + ldapAuthenticationConfigurator.getServerName2());
			try {
				ctx = new InitialDirContext(env);
			} catch (NamingException e1) {
				Logger.getLogger(CLdapAuthentication.class).info("Nepodarilo sa pripojit ani k servru: " + ldapAuthenticationConfigurator.getServerName2(), e1);
				throw new CBusinessException(e1.getMessage());
			}
		}

		NamingEnumeration<SearchResult> results = null;

		try {
			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE); // Search Entire Subtree
			controls.setCountLimit(1); // Sets the maximum number of entries to be returned as a result of the search
			controls.setTimeLimit(5000); // Sets the time limit of these SearchControls in milliseconds

			String searchString = "(&(objectCategory=user)(sAMAccountName=" + userName + "))";

			results = ctx.search("", searchString, controls);

			if (results.hasMore()) {

				SearchResult result = results.next();
				Attributes attrs = result.getAttributes();
				Attribute dnAttr = attrs.get("distinguishedName");
				String dn = (String) dnAttr.get();

				// User Exists, Validate the Password

				env.put(Context.SECURITY_PRINCIPAL, dn);
				env.put(Context.SECURITY_CREDENTIALS, userPassword);

				ctx = new InitialDirContext(env); // Exception will be thrown on Invalid case
				return true;
			} else {
				Logger.getLogger(CLdapAuthentication.class).info("V LDAPe sa nenasiel user: " + userName);
				return false;
			}

		} catch (AuthenticationException e) { // Invalid Login
			Logger.getLogger(CLdapAuthentication.class).info("Zadane zle heslo. user: " + userName, e);
			return false;
		} catch (NameNotFoundException e) { // The base context was not found.
			Logger.getLogger(CLdapAuthentication.class).info(e);
			return false;
		} catch (SizeLimitExceededException e) {
			Logger.getLogger(CLdapAuthentication.class).info(e);
			throw new CBusinessException("LDAP Query Limit Exceeded, adjust the query to bring back less records");
		} catch (NamingException e) {
			Logger.getLogger(CLdapAuthentication.class).info(e);
			throw new CBusinessException(e.getMessage());
		} finally {

			if (results != null) {
				try {
					results.close();
				} catch (NamingException e) {
					Logger.getLogger(CLdapAuthentication.class).info(e);
				}
			}

			try {
				ctx.close();
			} catch (NamingException e) {
				Logger.getLogger(CLdapAuthentication.class).info(e);
			}
		}
	}

	public static Boolean existsInDomain(String userName, ILdapAuthenticationConfigurator ldapAuthenticationConfigurator) throws CBusinessException {
		Hashtable<String, String> env = new Hashtable<>();

		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL,
				LDAPS_PREFIX + ldapAuthenticationConfigurator.getServerName() + ":" + ldapAuthenticationConfigurator.getServerPort() + "/" + ldapAuthenticationConfigurator.getUserSearchBaseDn());

		// To get rid of the PartialResultException when using Active Directory
		env.put(Context.REFERRAL, "follow");

		// Needed for the Bind (User Authorized to Query the LDAP server)
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, ldapAuthenticationConfigurator.getUserDn());
		env.put(Context.SECURITY_CREDENTIALS, ldapAuthenticationConfigurator.getUserPassword());

		DirContext ctx;
		try {
			ctx = new InitialDirContext(env);
		} catch (NamingException e) {
			Logger.getLogger(CLdapAuthentication.class).info(e);
			throw new CBusinessException(e.getMessage());
		}

		NamingEnumeration<SearchResult> results = null;

		try {
			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE); // Search Entire Subtree
			controls.setCountLimit(1); // Sets the maximum number of entries to be returned as a result of the search
			controls.setTimeLimit(5000); // Sets the time limit of these SearchControls in milliseconds

			String searchString = "(&(objectCategory=user)(sAMAccountName=" + userName + "))";

			results = ctx.search("", searchString, controls);

			return results.hasMore();
		} catch (NamingException e) {
			Logger.getLogger(CLdapAuthentication.class).info(e);
			throw new CBusinessException(e.getMessage());
		} finally {

			if (results != null) {
				try {
					results.close();
				} catch (NamingException e) {
					Logger.getLogger(CLdapAuthentication.class).info(e);
				}
			}

			try {
				ctx.close();
			} catch (NamingException e) {
				Logger.getLogger(CLdapAuthentication.class).info(e);
			}
		}
	}
}
