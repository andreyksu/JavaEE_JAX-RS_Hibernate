package ru.annikonenkov.rs.message.entities.message.handler;

import java.io.InputStream;

import javax.ws.rs.core.GenericType;

import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import ru.annikonenkov.rs.message.exception.ExceptionParseRequest;

public class HandlerPostRquestForNewMessageWithFileSecond implements iHandlerPostRequestForNewMessageWithFile {

	private final Logger log = Logger.getLogger(HandlerPostRquestForNewMessageWithFileSecond.class);

	private final MultipartFormDataInput _mapFormData;

	//private String _widdenMediaTypeOfFile = null;

	public HandlerPostRquestForNewMessageWithFileSecond(MultipartFormDataInput mapFormData) {
		_mapFormData = mapFormData;
	}

	@Override
	public boolean checkIsPresentAllRequiredParameters() {
		// TODO: Подумать что здесь делать.
		return true;
	}

	private <T> T parseTargetParameter(String targetParameter, Class<T> clazz) throws ExceptionParseRequest {
		T targetValue = null;
		try {
			targetValue = _mapFormData.getFormDataPart(targetParameter, new GenericType<T>(clazz));
		} catch (Exception e) {
			String message = String.format("При извелчении targetParameter = %s из запроса возникла ошибка: %s", targetParameter, e.getMessage());
			log.error(message);
			throw new ExceptionParseRequest(message, e);
		}
		return targetValue;
	}

	@Override
	public Integer getAuthorId() throws ExceptionParseRequest {
		return parseTargetParameter(MessageFileParameters.AuthorID.getParameter(), Integer.class);
	}

	@Override
	public Integer getReceiverId() throws ExceptionParseRequest {
		return parseTargetParameter(MessageFileParameters.ReceiverID.getParameter(), Integer.class);
	}

	@Override
	public String getMessage() throws ExceptionParseRequest {
		return parseTargetParameter(MessageFileParameters.TextMessage.getParameter(), String.class);
	}

	@Override
	public byte[] getByteArrayOfFile() throws ExceptionParseRequest {
		InputStream is = parseTargetParameter(MessageFileParameters.File.getParameter(), InputStream.class);
		byte[] bytes = null;
		try {
			bytes = is.readAllBytes();
		} catch (Exception e) {
			String message = String.format("При чтении byte[] из InputStream озникал ошибка: %s", e.getMessage());
			log.error(message);
			throw new ExceptionParseRequest(message, e);
		}
		return bytes;
	}

	@Override
	public String getMediaTypeOfFile() {
		// TODO: Дописать обработку исключений. Да и вообще переделать нужно, как-то не очень.
		return _mapFormData.getFormDataMap().get("file").get(0).getMediaType().toString();
	}

	@Override
	public void printCommonInfo() {
		// TODO: Подумать что здесь делать.
	}

}
