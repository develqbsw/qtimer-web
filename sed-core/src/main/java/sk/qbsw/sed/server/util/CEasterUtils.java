package sk.qbsw.sed.server.util;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Locale;

import org.jasypt.contrib.org.apache.commons.codec_1_3.binary.Base64;

/**
 * Utils for Christian feast days
 * 
 * @author rosenberg
 * 
 */
public class CEasterUtils {
	
	// R = rok, v ktorom chceme vypocitat datum Velkonocnej nedele
	// C = INT(R/100)
	// N = R-19*INT(R/19)
	// K = INT((C-17)/25)
	// I = C-INT(C/4)-INT((C-K)/3)+19*N+15
	// I = I-30*INT(I/30)
	// I = I-INT(I/28)*(1-INT(I/28)*INT(29/(I+1))*INT((21-N)/11))
	// J = R+INT(R/4)+I+2-C+INT(C/4)
	// J = J-7*INT(J/7)
	// L = I-J
	// M = 3+INT((L+40)/44) -->> M je mesiac, v ktorom je Velkonocna nedela
	// D = L+28-31*INT(M/4) -->> D je den Velkonocnej nedele v mesiaci
	// ak M = 3 -->> Velkonocna nedela je v marci
	// ak M = 4 -->> Velkonocna nedela je v aprili

	public static int[] getEasterSundayValues(Integer targetYear) {
		int[] retVal = new int[3];

		int r = targetYear.intValue();
		int c = r / 100;
		int n = r - 19 * (int) (r / 19);
		int k = (int) ((c - 17) / 25);
		int i = c - (int) (c / 4) - (int) ((c - k) / 3) + 19 * n + 15;
		i = i - 30 * (int) (i / 30);
		i = i - (int) (i / 28) * (1 - (int) (i / 28) * (int) (29 / (i + 1)) * (int) ((21 - n) / 11));
		int j = r + (int) (r / 4) + i + 2 - c + (int) (c / 4);
		j = j - 7 * (int) (j / 7);
		int l = i - j;
		int m = 3 + (int) ((l + 40) / 44);
		int d = l + 28 - 31 * (int) (m / 4);

		retVal[0] = r;
		retVal[1] = m - 1;
		retVal[2] = d;

		return retVal;

	}

	/**
	 * Easter Sunday date for selected year
	 * 
	 * @param inputYear input year
	 * @return calendar object
	 */
	public static Calendar getEasterSunday(Integer inputYear) {

		int[] values = getEasterSundayValues(inputYear);
		int year = values[0];
		int month = values[1];
		int day = values[2];

		Calendar retVal = getDateOnly();
		retVal.set(Calendar.YEAR, year);
		retVal.set(Calendar.MONTH, month);
		retVal.set(Calendar.DAY_OF_MONTH, day);

		return retVal;
	}

	/**
	 * Easter Friday date for selected year
	 * 
	 * @param inputYear input year
	 * @return calendar object
	 */
	public static Calendar getEasterFriday(Integer inputYear) {
		Calendar velkonocnaNedela = getEasterSunday(inputYear);
		Calendar velkyPiatok = (Calendar) velkonocnaNedela.clone();
		velkyPiatok.add(Calendar.DAY_OF_MONTH, -2);
		return velkyPiatok;
	}

	/**
	 * Easter Monday date for selected year
	 * 
	 * @param inputYear input year
	 * @return calendar object
	 */
	public static Calendar getEasterMonday(Integer inputYear) {
		Calendar velkonocnaNedela = getEasterSunday(inputYear);
		Calendar velkaNoc = (Calendar) velkonocnaNedela.clone();
		velkaNoc.add(Calendar.DAY_OF_MONTH, 1);
		return velkaNoc;
	}

	/**
	 * 
	 * @return
	 */
	private static Calendar getDateOnly() {
		Calendar retVal = Calendar.getInstance(new Locale("SK"));
		retVal.set(Calendar.HOUR_OF_DAY, 0);
		retVal.set(Calendar.MINUTE, 0);
		retVal.set(Calendar.SECOND, 0);
		retVal.set(Calendar.MILLISECOND, 0);
		return retVal;
	}

	/**
	 * Test case
	 * 
	 * @param args
	 * @throws UnsupportedEncodingException
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {
		String userpass = "password";
		String basicAuth = javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());

		System.out.println(basicAuth);

		System.out.println(new String(Base64.decodeBase64(basicAuth.getBytes()), "UTF-8"));
	}
}
