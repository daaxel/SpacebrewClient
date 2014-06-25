/*******************************************************************************
 * Copyright (c) 2014 Axel Baumgartner.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Axel Baumgartner - initial API and implementation
 ******************************************************************************/
package at.ac.sbg.icts.spacebrew.client;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

/**
 * Implements <code>WebSocketClient</code> and uses a callback interface to pass
 * through method calls.
 * 
 * @author Axel Baumgartner
 */
public class WebSocketClientImpl extends WebSocketClient
{
	/**
	 * The object that implements the callback methods.
	 */
	private final WebSocketClientImplCallback	callback;

	/**
	 * @param callback The object that implements the callback methods
	 * @param serverURI The URI of the server to connect to
	 */
	public WebSocketClientImpl(WebSocketClientImplCallback callback, String serverURI) throws URISyntaxException
	{
		super(new URI(serverURI));
		this.callback = callback;
	}

	/**
	 * Called by <code>WebSocketClient</code> and passes the call through to the
	 * callback object.
	 */
	@Override
	public void onOpen(ServerHandshake handshakedata)
	{
		callback.onOpen();
	}

	/**
	 * Called by <code>WebSocketClient</code> and passes the call through to the
	 * callback object.
	 */
	@Override
	public void onMessage(String message)
	{
		callback.onMessage(message);
	}

	/**
	 * Called by <code>WebSocketClient</code> and passes the call through to the
	 * callback object.
	 * 
	 * @param code The status code of the WebSocket close control frame as
	 *            defined in the <a
	 *            href="http://tools.ietf.org/html/rfc6455#section-7.4.1"
	 *            >WebSocket protocol (RFC6455, Section 7.4.1.)</a>
	 * @param reason A short explanation of the status code
	 * @param remote Whether the connection was closed by the remote entity
	 */
	@Override
	public void onClose(int code, String reason, boolean remote)
	{
		callback.onClose();
	}

	/**
	 * Called by <code>WebSocketClient</code> and passes the call through to the
	 * callback object.
	 * 
	 * @param ex The <code>Exception</code> that caused the error
	 */
	@Override
	public void onError(Exception ex)
	{
		callback.onError(ex);
	}
}
