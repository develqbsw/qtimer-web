package sk.qbsw.sed.server.nameday;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import sk.qbsw.sed.client.model.ILanguageConstant;
import sk.qbsw.sed.framework.security.exception.CBusinessException;

@Service("nameDayService")
public class CNameDayService implements INameDayService {

	public String getNamesday(String language) throws CBusinessException {
		String generate_URL = "https://calendar.zoznam.sk/embed1/sk/sk/";

		if (ILanguageConstant.EN.equals(language)) {
			generate_URL = "https://calendar.zoznam.sk/embed1/en/en/";
		}

		String inputLine;
		try {
			URL data = new URL(generate_URL);

			// ak je proxy == null zavolám openconnection bez parametra proxy
			HttpURLConnection con = getProxy() == null ? (HttpURLConnection) data.openConnection() : (HttpURLConnection) data.openConnection(getProxy());
			con.setConnectTimeout(1000); // set timeout to 1 seconds
			
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "windows-1250"));
			String s = "";

			while ((inputLine = in.readLine()) != null) {
				s += inputLine;
			}

			s = s.replace("document.write('<div class=\"calwid_body\">');", "");
			s = s.replace("document.write('<span class=\"calwid_day\">", "");
			s = s.replace("</span>');document.write('", "");
			s = s.replace("');document.write('<a href=\"http://calendar.zoznam.sk/meno/\">", "");
			s = s.replace("<span class=\"calwid_name1\">", "");
			s = s.replace("</span></a>", "");
			s = s.replace("<span class=\"calwid_name2\">", "");
			s = s.replace("</span>.');document.write('</div>');", "");

			in.close();
			con.disconnect();

			return s;
		} catch (Exception e) {
			Logger.getLogger(CNameDayService.class).error(e);
			throw new CBusinessException("");
		}
	}

	private static Proxy getProxy() {

		Proxy proxy = null;

		String proxyHost = System.getProperties().getProperty("http.proxyHost");
		Integer proxyPort;

		try {
			proxyPort = Integer.parseInt(System.getProperties().getProperty("http.proxyPort"));
		} catch (Exception e) {
			proxyPort = null;
		}

		if (System.getProperties().getProperty("http.proxyHost") != null && System.getProperties().getProperty("http.proxyPort") != null) {
			proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
		} else {
			proxy = null;
		}

		return proxy;
	}
}
