package com.kurento.kmf.jsonrpcconnector.client;

import java.io.Closeable;
import java.io.IOException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kurento.kmf.jsonrpcconnector.JsonRpcHandler;
import com.kurento.kmf.jsonrpcconnector.Session;
import com.kurento.kmf.jsonrpcconnector.internal.JsonRpcHandlerManager;
import com.kurento.kmf.jsonrpcconnector.internal.JsonRpcRequestSender;
import com.kurento.kmf.jsonrpcconnector.internal.JsonRpcRequestSenderHelper;
import com.kurento.kmf.jsonrpcconnector.internal.client.ClientSession;

/**
 * This class is used to make request to a server using the JSON-RPC protocol
 * with server events. This protocol can be implemented with two transport
 * types: Websockets or http (for request-response) and long-pooling for server
 * events.
 * 
 * Request: The request is a JSON with the following fields:
 * <ul>
 * <li>method: Name of the operation to be executed in the server</li>
 * <li>params: Parameters of the operation</li>
 * <li>id: Used if the operation must return a response. This id is used to
 * identify the response if it cannot be identified by means of underlying
 * transport.</li>
 * </ul>
 * 
 * Response: The response is a JSON with the following fields:
 * <ul>
 * <li>result: Result of the operation.</li>
 * <li>error: This field is used if operation generates an error.</li>
 * <li>id: request id</li>
 * </ul>
 * 
 * @author Micael Gallego (micael.gallego@gmail.com)
 */
public abstract class JsonRpcClient implements JsonRpcRequestSender, Closeable {

	protected JsonRpcHandlerManager handlerManager = new JsonRpcHandlerManager();
	protected JsonRpcRequestSenderHelper rsHelper;
	protected Object registerInfo;
	protected ClientSession session;

	public void setServerRequestHandler(JsonRpcHandler<?> handler) {
		this.handlerManager.setJsonRpcHandler(handler);
	}

	@Override
	public abstract void close() throws IOException;

	@Override
	public <R> R sendRequest(String method, Class<R> resultClass)
			throws IOException {
		return rsHelper.sendRequest(method, resultClass);
	}

	@Override
	public <R> R sendRequest(String method, Object params, Class<R> resultClass)
			throws IOException {
		return rsHelper.sendRequest(method, params, resultClass);
	}

	public void sendRequest(String method, JsonObject params,
			Continuation<JsonElement> continuation) {

		rsHelper.sendRequest(method, params, continuation);
	}

	@Override
	public JsonElement sendRequest(String method) throws IOException {
		return rsHelper.sendRequest(method);
	}

	@Override
	public JsonElement sendRequest(String method, Object params)
			throws IOException {
		return rsHelper.sendRequest(method, params);
	}

	@Override
	public void sendNotification(String method) throws IOException {
		rsHelper.sendNotification(method);
	}

	@Override
	public void sendNotification(String method, Object params)
			throws IOException {
		rsHelper.sendNotification(method, params);
	}

	public Session getSession() {
		return session;
	}

	public void setSessionId(String sessionId) {
		this.rsHelper.setSessionId(sessionId);
		this.session.setSessionId(sessionId);
	}

}