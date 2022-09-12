package sk.qbsw.sed.communication.http;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.TrustStrategy;

class CTrustManagerDecorator implements X509TrustManager {

	private final X509TrustManager trustManager;
	private final TrustStrategy trustStrategy;

	CTrustManagerDecorator(final X509TrustManager trustManager, final TrustStrategy trustStrategy) {
		super();
		this.trustManager = trustManager;
		this.trustStrategy = trustStrategy;
	}

	@Override
	public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
		this.trustManager.checkClientTrusted(chain, authType);
	}

	@Override
	public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
		if (!this.trustStrategy.isTrusted(chain, authType)) {
			this.trustManager.checkServerTrusted(chain, authType);
		}
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return this.trustManager.getAcceptedIssuers();
	}
}
