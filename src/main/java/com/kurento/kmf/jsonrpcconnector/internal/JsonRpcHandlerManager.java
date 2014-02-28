package com.kurento.kmf.jsonrpcconnector.internal;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.kurento.kmf.jsonrpcconnector.DefaultJsonRpcHandler;
import com.kurento.kmf.jsonrpcconnector.JsonRpcHandler;
import com.kurento.kmf.jsonrpcconnector.Session;
import com.kurento.kmf.jsonrpcconnector.internal.message.MessageUtils;
import com.kurento.kmf.jsonrpcconnector.internal.message.Request;
import com.kurento.kmf.jsonrpcconnector.internal.message.Response;
import com.kurento.kmf.jsonrpcconnector.internal.message.ResponseError;
import com.kurento.kmf.jsonrpcconnector.internal.server.PerSessionJsonRpcHandler;
import com.kurento.kmf.jsonrpcconnector.internal.server.TransactionImpl;
import com.kurento.kmf.jsonrpcconnector.internal.server.TransactionImpl.ResponseSender;

public class JsonRpcHandlerManager {

	private static final Logger log = LoggerFactory
			.getLogger(JsonRpcHandlerManager.class);

	private JsonRpcHandler<?> handler;

	public JsonRpcHandlerManager(JsonRpcHandler<?> handler) {
		this.handler = handler;
	}

	public JsonRpcHandlerManager() {
	}

	public void setJsonRpcHandler(JsonRpcHandler<?> handler) {
		this.handler = handler;
	}

	public void afterConnectionClosed(Session session, String reason) {
		if (handler != null) {
			try {
				handler.afterConnectionClosed(session, reason);
			} catch (Exception e) {
				try {
					handler.handleUncaughtException(session, e);
				} catch (Exception e2) {
					log.error(
							"Exception while executing handleUncaughtException",
							e2);
				}
			}
		}
	}

	public void afterConnectionEstablished(Session session) {

		try {
			if (handler != null) {
				handler.afterConnectionEstablished(session);
			}
		} catch (Exception e) {
			try {
				handler.handleUncaughtException(session, e);
			} catch (Exception e2) {
				log.error("Exception while executing handleUncaughtException",
						e2);
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void handleRequest(Session session, Request<JsonElement> request,
			ResponseSender rs) throws IOException {

		try {

			Class<?> paramsType = getParamsType(handler);
			Request<?> nonGenRequest;
			try {

				nonGenRequest = MessageUtils
						.convertRequest(request, paramsType);

			} catch (ClassCastException e) {

				String message = "The handler "
						+ handler.getClass()
						+ " is trying to process the request. But request params '"
						+ request.getParams() + "' cannot be converted to "
						+ paramsType.getCanonicalName()
						+ ". The type to convert params is specified in the"
						+ " handler as the supertype generic parameter";

				// TODO Maybe use the pattern handleUncaughtException
				log.error(message);

				if (request.getId() != null) {
					rs.sendResponse(new Response<Object>(null,
							new ResponseError(0, message, null)));
				}
				return;
			}

			JsonRpcHandler nonGenHandler = handler;

			TransactionImpl tx = new TransactionImpl(session, request, rs);
			nonGenHandler.handleRequest(tx, nonGenRequest);

			if (!tx.isAsync() && request.getId() != null) {

				boolean notResponded = tx.setRespondedIfNot();

				if (notResponded) {
					// Empty response
					rs.sendResponse(new Response<Object>(request.getId(), null));
				}
			}

		} catch (Exception e) {

			// TODO Maybe use the pattern handleUncaughtException
			log.error("Exception while processing request", e);

			ResponseError error = ResponseError.newFromException(e);
			rs.sendResponse(new Response<Object>(request.getId(), error));
		}
	}

	@SuppressWarnings("rawtypes")
	public static Class<?> getParamsType(JsonRpcHandler<?> handler) {

		if (handler instanceof PerSessionJsonRpcHandler) {
			return getParamsType(((PerSessionJsonRpcHandler) handler)
					.getHandlerType());
		} else {
			return getParamsType(handler.getClass());
		}
	}

	// TODO Improve this way to obtain the generic parameters in class
	// hierarchies
	private static Class<?> getParamsType(Class<?> handlerClass) {

		Type[] genericInterfaces = handlerClass.getGenericInterfaces();

		for (Type type : genericInterfaces) {

			if (type instanceof ParameterizedType) {
				ParameterizedType parameterized = (ParameterizedType) type;

				if (parameterized.getRawType() == JsonRpcHandler.class) {
					return (Class<?>) parameterized.getActualTypeArguments()[0];
				}
			}
		}

		Type genericSuperclass = handlerClass.getGenericSuperclass();
		if (genericSuperclass != null) {

			if (genericSuperclass instanceof Class) {
				return getParamsType((Class<?>) genericSuperclass);
			} else {

				ParameterizedType paramClass = (ParameterizedType) genericSuperclass;

				if (paramClass.getRawType() == DefaultJsonRpcHandler.class) {
					return (Class<?>) paramClass.getActualTypeArguments()[0];
				}

				return getParamsType((Class<?>) paramClass.getRawType());
			}
		}

		throw new RuntimeException(
				"Unable to obtain the type paramter of JsonRpcHandler");
	}
}