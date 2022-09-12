package sk.qbsw.sed.server.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import sk.qbsw.sed.client.ui.localization.CClientExceptionsMessages;
import sk.qbsw.sed.framework.security.exception.CSecurityException;

/**
 * 
 * @author rosenberg
 * 
 */
public class CEncryptUtils {

	private static final String UTF_8 = "UTF-8";
	
	private static Base64 base64 = new Base64();

	private CEncryptUtils() {

	}

	public static String getHash(String inputString, String salt) throws CSecurityException {
		String retVal = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");

			digest.reset();
			byte[] bSalt = salt.getBytes(UTF_8);
			digest.update(bSalt);
			String hash = encode(digest.digest(inputString.getBytes(UTF_8)));
			retVal = hash;
		} catch (NoSuchAlgorithmException e1) {
			Logger.getLogger(CEncryptUtils.class).info(e1);
			throw new CSecurityException(CClientExceptionsMessages.ENCRYPT_ERROR);
		} catch (UnsupportedEncodingException e2) {
			Logger.getLogger(CEncryptUtils.class).info(e2);
			throw new CSecurityException(CClientExceptionsMessages.ENCRYPT_ERROR);
		}
		return retVal;
	}

	public static String getHash(String inputString) throws CSecurityException {
		String retVal = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");

			digest.reset();

			String hash = encode(digest.digest(inputString.getBytes(UTF_8)));
			retVal = hash;
		} catch (NoSuchAlgorithmException e1) {
			Logger.getLogger(CEncryptUtils.class).info(e1);
			throw new CSecurityException(CClientExceptionsMessages.ENCRYPT_ERROR);
		} catch (UnsupportedEncodingException e2) {
			Logger.getLogger(CEncryptUtils.class).info(e2);
			throw new CSecurityException(CClientExceptionsMessages.ENCRYPT_ERROR);
		}
		return retVal;
	}

	/**
	 * From a base 64 representation, returns the corresponding byte[]
	 * 
	 * @param data String The base64 representation
	 * @return byte[] corresponding byte[] of input string
	 * @throws IOException in error case
	 */
	public static byte[] decode(String data) throws IOException {
		return base64.decode(data.getBytes(UTF_8));
	}

	/**
	 * From a byte[] returns a base 64 representation string
	 * 
	 * @param data input byte[]
	 * @return String
	 * @throws UnsupportedEncodingException in error case
	 */
	public static String encode(byte[] data) throws UnsupportedEncodingException {
		return new String(base64.encode(data), UTF_8);
	}

	public static void main(String[] args) {

		// treba importnut certifikat riso mi poslal link ze su tu z:\Install\Certificates_Startcom\

		// keytool -importcert -file ca.cer -keystore "c:\Program Files\Java\jre7\lib\security\cacerts" -alias 
		// StartCom_CA -trustcacerts keytool -importcert -file sub.class2.server.ca.cer -keystore 
		// "c:\Program Files\Java\jre7\lib\security\cacerts" -alias StartCom_CA_2 -trustcacerts

		// proxy len pre localhost
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("192.168.121.31", 3128));

		String username = "";
		String password = "";

		String generate_URL = "https://jira.qbsw.sk/rest/api/2/issue/ISSP-10612?fields=summary";
		String inputLine;
		try {
			URL data = new URL(generate_URL);

			HttpURLConnection con = (HttpURLConnection) data.openConnection(proxy);

			String userpass = username + ":" + password;
			String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());

			con.setRequestProperty("Authorization", basicAuth);

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

			String s = "";

			while ((inputLine = in.readLine()) != null) {
				s = inputLine;
			}

			System.out.println(s.substring(s.indexOf("summary") + 10, s.length() - 3));

			in.close();

			con.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
