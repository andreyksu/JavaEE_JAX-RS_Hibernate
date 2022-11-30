package ru.annikonenkov.rs.message.entities.message.handler;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;

import ru.annikonenkov.rs.message.exception.ExceptionParseRequest;

public class HandlerPostRquestForNewMessageWithFileFirst implements iHandlerPostRequestForNewMessageWithFile {

	private final Logger log = Logger.getLogger(HandlerPostRquestForNewMessageWithFileFirst.class);

	private final Map<String, List<InputPart>> _mapFormData;

	private String _widdenMediaTypeOfFile = null;

	public HandlerPostRquestForNewMessageWithFileFirst(Map<String, List<InputPart>> mapFormData) {
		_mapFormData = mapFormData;
	}

	@Override
	public boolean checkIsPresentAllRequiredParameters() {
		for (MessageFileParameters key : MessageFileParameters.values()) {
			if (!_mapFormData.containsKey(key.getParameter())) {
				log.warn(String.format("The parameter = %s is missed or has incorrect name. The message will not be created.", key.getParameter()));
				return false;
			}
		}
		return true;
	}

	private String getBodyAsStringForTargetParameter(String targetParameter) throws ExceptionParseRequest {
		String bodyAsString = null;
		String shortMimeType = null;
		String widdenMimeType = null;

		List<InputPart> inputParts = _mapFormData.get(targetParameter);
		if (inputParts == null) {
			String message = String.format("В запросе не найден параметр targetParameter = %s", targetParameter);
			log.error(message);
			throw new ExceptionParseRequest(message);
		}

		int sizeOfListInputPart = inputParts.size();
		// TODO: Возможна потенциальная проблема если будет больше 1го элемента - просто будем перетерать. Нужно отдельно разобраться, возмжно ли больше чем 1элемент.
		if (sizeOfListInputPart > 1) {
			String message = String.format("Для параметра targetParameter = '%s' у полученного IntupPart размер больше 1 и равен '%d'.", targetParameter, sizeOfListInputPart);
			log.error(message);
			throw new ExceptionParseRequest(message);
		}
		// TODO: С учетом условия выше, этот перебор уже не нужен. Можно получать первый элемент и все.
		for (InputPart ip : inputParts) {
			shortMimeType = ip.getMediaType().getType();
			widdenMimeType = ip.getMediaType().toString();
			log.info(String.format("Для параметра targetParameter = '%s' полученный IntupPart имеет shortMimeType  = '%s' и widdenMimeType = '%s'", targetParameter, shortMimeType, widdenMimeType));
			if (!(widdenMimeType.contains("text/plain"))) {
				String message = String.format("Для параметра targetParameter = '%s', widdenMediaType = '%s' а должен быть 'text/plain'.", targetParameter, widdenMimeType);
				log.error(message);
				throw new ExceptionParseRequest(message);
			}

			try {
				bodyAsString = ip.getBody(String.class, String.class);
				log.info(String.format("Для targetParameter = '%s' полученное body of part = '%s'.", bodyAsString, targetParameter));
			} catch (IOException e) {
				String message = String.format("Для параметра targetParameter = '%s' не смогли извлечь из POST-запроса соответствующее body of part.",targetParameter);
				log.error(message);
				throw new ExceptionParseRequest(message, e);
			}
		}
		return bodyAsString;
	}

	private Integer parseIntegerFormStringBody(String bodyAsString) throws ExceptionParseRequest {
		Integer bodyAsInt = null;
		try {
			bodyAsInt = Integer.valueOf(bodyAsString);
		} catch (NumberFormatException e) {
			String message = String.format("Не смогли извлеченное body of part распарсить как Int '%s'.", bodyAsString);
			log.error(message);
			throw new ExceptionParseRequest(message, e);
		}
		return bodyAsInt;

	}

	@Override
	public Integer getAuthorId() throws ExceptionParseRequest {
		Integer bodyAsInt = null;
		String parsedBodyAsString = getBodyAsStringForTargetParameter(MessageFileParameters.AuthorID.getParameter());
		bodyAsInt = parseIntegerFormStringBody(parsedBodyAsString);
		return bodyAsInt;
	}

	@Override
	public Integer getReceiverId() throws ExceptionParseRequest {
		Integer bodyAsInt = null;
		String parsedBodyAsString = getBodyAsStringForTargetParameter(MessageFileParameters.ReceiverID.getParameter());
		bodyAsInt = parseIntegerFormStringBody(parsedBodyAsString);
		return bodyAsInt;
	}

	@Override
	public String getMessage() throws ExceptionParseRequest {
		String parsedBodyAsString = getBodyAsStringForTargetParameter(MessageFileParameters.TextMessage.getParameter());
		return parsedBodyAsString;
	}

	@Override
	public byte[] getByteArrayOfFile() throws ExceptionParseRequest {
		byte[] allBytes = null;
		String shortMediaTypeOfFile = null;
		List<InputPart> inputParts = _mapFormData.get(MessageFileParameters.File.getParameter());
		
		if (inputParts == null) {
			String message = String.format("В запросе не найден параметр targetParameter = %s", MessageFileParameters.File.getParameter());
			log.error(message);
			throw new ExceptionParseRequest(message);
		}

		int sizeOfListInputPart = inputParts.size();
		// TODO: Возможна потенциальная проблема если будет больше 1го элемента - просто будем перетерать предыдущий файл.
		// TODO: Такое возможно, когда две части multipart/form-data имеют одинаковое ИМЯ в терминологии curl --form 'file=... и --form 'file=...
		// TODO: Возможно стоит переделать и возвращать List<byte []> - это для случая, когда в одном сообщении будет приходить несколько файлов с одинаковым наименованием part. Но тогда нужно думать как это сохранять.
		if (sizeOfListInputPart > 1) {
			String message = String.format("У полученного multipart/form-data количество part с именем = %s больше 1 и равно = %d. На тек. момент предполагается одно сообщение, один файл. Сообщение создано не будет.", MessageFileParameters.File.getParameter(), sizeOfListInputPart);
			log.warn(message);
			throw new ExceptionParseRequest(message);
		}
		for (InputPart ip : inputParts) {
			shortMediaTypeOfFile = ip.getMediaType().getType();
			_widdenMediaTypeOfFile = ip.getMediaType().toString();
			log.info(String.format(
					"Для targetParameter = %s полученный IntupPart имеет widdenMediaType  = %s и shortMediaType = %s",
					MessageFileParameters.File.getParameter(), _widdenMediaTypeOfFile, shortMediaTypeOfFile));
			try {
				InputStream inputStream = ip.getBody(InputStream.class, InputStream.class);
				allBytes = inputStream.readAllBytes();
			} catch (IOException e) {
				String message = String.format(
						"Для targetParameter = %s c mediaType = %s не смогли извлеченное body распарсить как InputStream. Сообщение создано не будет.",
						MessageFileParameters.File.getParameter(), _widdenMediaTypeOfFile);
				log.warn(message);
				throw new ExceptionParseRequest(message, e);
			}
		}
		return allBytes;
	}

	public String getMediaTypeOfFile() {
		return _widdenMediaTypeOfFile;
	}

	@Override
	public void printCommonInfo() {
		_mapFormData.forEach((key, listInputPart) -> {
			log.info("------------------------------------------");
			log.info(String.format("keyFomDataMap = %s", key));
			listInputPart.forEach(inputPart -> {
				log.info("-------");
				MultivaluedMap<String, String> headers = inputPart.getHeaders();
				headers.forEach((k, v) -> {
					log.info(String.format("keyHeaderFromPart = %s", k));
					log.info(String.format("valueHeaderFromPart = %s", v));
				});
				log.info("-------");
				MediaType mediaType = inputPart.getMediaType();
				log.info(String.format("mediaTypeFromInputPart = %s", mediaType.toString()));
				log.info(String.format("Is the mediaType got From meddage = %s", inputPart.isContentTypeFromMessage()));
				try {
					InputStream inputStream = inputPart.getBody(InputStream.class, InputStream.class);
					byte[] allBytes = inputStream.readAllBytes();
					log.info(String.format("Размер полученного файла равен %d byte", allBytes.length));
				} catch (IOException e) {
					log.error(String.format("Ошибка при прочтении byteArray из InputStream: %s", e.getMessage()));
				}
			});
		});
	}
}
