package sk.qbsw.sed.api.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.log4j.Logger;

@ServerEndpoint("/usersPanelWebSocket")
public class CUsersPanelWebSocket {

	private static final long DEFAULT_TIMEOUT = 3600000;

	private static Set<Session> sessions = Collections.synchronizedSet(new HashSet<Session>());

	private static final Logger logger = Logger.getLogger(CUsersPanelWebSocket.class.getName());

	public static void sendAll(String message) {
		synchronized (sessions) {
			for (Session s : sessions) {
				try {
					if (s.isOpen()) {
						s.getBasicRemote().sendText(message);
					}
				} catch (IOException e) {
					logger.info(e);
				}
			}
		}
	}

	/**
	 * The user closes the connection.
	 * 
	 * Note: you can't send messages to the client from this method
	 */
	@OnClose
	public void onClose(Session session) {
		logger.info("Session " + session.getId() + " has ended");
		sessions.remove(session);
	}

	/**
	 * When a user sends a message to the server, this method will intercept the
	 * message and allow us to react to it.
	 */
	@OnMessage
	public void onMessage(String message, Session session) {
		// do nothing
	}

	/**
	 * @OnOpen allows us to intercept the creation of a new session. The session
	 *         class allows us to send data to the user. In the method onOpen, we'll
	 *         let the user know that the handshake was successful.
	 */
	@OnOpen
	public void onOpen(Session session) {
		session.setMaxIdleTimeout(DEFAULT_TIMEOUT);
		sessions.add(session);
		logger.info("Number of clients:[" + sessions.size() + "]");
		logger.info(session.getId() + " has opened a connection");
	}
}
