package sk.qbsw.sed.api.security;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * wrapper for api requests
 *
 * @author Podmajersky Lukas
 * @since 2.2.0
 * @version 2.2.0
 */
public class CSedRequestWrapper extends HttpServletRequestWrapper {
	/** body of request */
	private final String body;

	/**
	 * create wrapper for request
	 *
	 * @param request actual request
	 * @throws IOException
	 */
	public CSedRequestWrapper(HttpServletRequest request) throws IOException {
		super(request);
		StringBuilder stringBuilder = new StringBuilder();

		InputStream inputStream = request.getInputStream();
		if (inputStream != null) {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

			char[] charBuffer = new char[128];
			int bytesRead = -1;
			while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
				stringBuilder.append(charBuffer, 0, bytesRead);
			}

			bufferedReader.close();
		} else {
			stringBuilder.append("");
		}

		body = stringBuilder.toString();
	}

	/**
	 * @return request body
	 */
	public String getBody() {
		return this.body;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequestWrapper#getInputStream()
	 */
	@Override
	public ServletInputStream getInputStream() throws IOException {
		final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes());
		return new ServletInputStream() {
			@Override
			public int read() throws IOException {
				return byteArrayInputStream.read();
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequestWrapper#getReader()
	 */
	@Override
	public BufferedReader getReader() throws IOException {
		return new BufferedReader(new InputStreamReader(this.getInputStream()));
	}
}
