package ru.annikonenkov.rs.message.rest;

import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import ru.annikonenkov.rs.message.entities.group.GroupDAO;
import ru.annikonenkov.rs.message.entities.message.Message;
import ru.annikonenkov.rs.message.entities.message.MessageDAO;
import ru.annikonenkov.rs.message.entities.message.dto.TransferToDTOMessage;
import ru.annikonenkov.rs.message.entities.message.handler.HandlerPostRquestForNewMessageWithFileFirst;
import ru.annikonenkov.rs.message.entities.message.handler.HandlerPostRquestForNewMessageWithFileSecond;
import ru.annikonenkov.rs.message.entities.message.handler.MessageForm;
import ru.annikonenkov.rs.message.entities.message.handler.MessageFormWithFile;
import ru.annikonenkov.rs.message.entities.message.handler.iHandlerPostRequestForNewMessageWithFile;
import ru.annikonenkov.rs.message.entities.user.UserDAO;
import ru.annikonenkov.rs.message.exception.ExceptionForAddMessage;
import ru.annikonenkov.rs.message.exception.ExceptionParseRequest;

@Stateless
@Path("/chat/messages")
public class RestForMessages {

	@Inject
	Logger log;

	@Context
	private HttpHeaders httpHeaders;

	@Context
	Request request;

	@Context
	private UriInfo uriInfo;

	@EJB
	private GroupDAO groupDAO;

	@EJB
	private MessageDAO messageDAO;

	@EJB
	private UserDAO userDAO;

	// +++++
	@GET
	@Path("/getMessageById/messageId---{messageId}")
	@Produces({ "application/json" })
	@Consumes({ "application/json" })
	public Response getMessageById(@PathParam("messageId") Integer messageId) {
		Message message = messageDAO.getMessageById(messageId);
		TransferToDTOMessage transfer = new TransferToDTOMessage();
		// return Response.ok(PrintableMessage.doPrintableMessage(message)).build();
		return Response.ok(transfer.transeferMessageToDTOMessage(message)).build();
	}

	// +++++
	@GET
	@Path("/getMessages/authorId---{authorId}/receiverId---{receiverId}")
	@Produces({ "application/json" })
	@Consumes({ "application/json" })
	public Response getMessagesByAuthorIdAndReceiverId(@PathParam("authorId") Integer authorId,
			@PathParam("receiverId") Integer receiverId) {
		List<Message> messages = messageDAO.getMessageByAuthorIdAndReceiverId(authorId, receiverId, false);
		TransferToDTOMessage transfer = new TransferToDTOMessage();
		return Response.ok(transfer.transferListMessageToListDTOMessage(messages)).build();
	}

	// +++++
	@GET
	@Path("/getMessages/groupId---{groupId}")
	@Produces({ "application/json" })
	@Consumes({ "application/json" })
	public Response getMessagesByGroupId(@PathParam("groupId") Integer groupId) {
		List<Message> messages = messageDAO.getMessageByReceiverGroupId(groupId, false);
		TransferToDTOMessage transfer = new TransferToDTOMessage();
		return Response.ok(transfer.transferListMessageToListDTOMessage(messages)).build();
	}
	
	//TODO: Методы для группы и для пользователей (для Файлов и без Файлов) схожи между собой. Нужно вынести общие части в утилитарный класс, дабы убрать здесь ненужное дублирование.
	
	@POST
	@Path("/addNewMessageToGroup/")
	@Produces({ "application/json" })
	@Consumes("multipart/form-data")
	public Response addNewMessageToGroup(@MultipartForm MessageForm form) {
		String messageForLog = "";
		
		
		int authorID = form.getAuthorId();
		int receiverID = form.getReceiverId();
		String message = form.getMessage();
		boolean resultOfCheck = form.checkIsPresentAllRequiredParameters();
		
		int idOfNewMessage = 0;
		
		if (!resultOfCheck) {
			messageForLog = String.format("Сообщение создано не будет, один из параметров пуст или отсутствует: resultOfCheck = '%b' authorID = '%d' receiverID = '%d' message = '%s'", resultOfCheck, authorID, receiverID, message);
			log.error(messageForLog);
			return Response.status(400).entity(messageForLog).build();
		}
		
		messageForLog = String.format("resultOfCheck = '%b' authorID = '%d' receiverID = '%d' message = '%s'", resultOfCheck, authorID, receiverID, message);
		log.info(messageForLog);
		
		try {
			//TODO: Эта часть отличается.
			idOfNewMessage = messageDAO.addNewMessageToGroup(message, authorID, receiverID);
		} catch (ExceptionForAddMessage e) {
			messageForLog = String.format("Сообщение не было добавлено: %s", e.getMessage());
			log.error(messageForLog);
			return Response.status(400).entity(messageForLog).build();
		}
		String forResponse = String.format("{\"idOfNewMessage\" : %d}", idOfNewMessage);
		return Response.status(200).entity(forResponse).build();
	}
	
	@POST
	@Path("/addNewMessageWithFileToGroup/")
	@Produces({ "application/json" })
	@Consumes("multipart/form-data")
	public Response addNewMessageWithFileToGroup(MultipartFormDataInput input) {
		Map<String, List<InputPart>> mapFormData = input.getFormDataMap();
		iHandlerPostRequestForNewMessageWithFile hprfnm = new HandlerPostRquestForNewMessageWithFileFirst(mapFormData);
		String messageForLog;
		int idOfNewMessage;
		try {
			
			int authorID = hprfnm.getAuthorId();
			int receiverID = hprfnm.getReceiverId();
			String message = hprfnm.getMessage();
			byte[] byteArray = hprfnm.getByteArrayOfFile();
			String mediaType = hprfnm.getMediaTypeOfFile();
			
			boolean resultOfCheck = hprfnm.checkIsPresentAllRequiredParameters();
			
			if (!resultOfCheck) {
				messageForLog = String.format("Сообщение создано не будет, один из параметров пуст или отсутствует: resultOfCheck = '%b' authorID = '%d' receiverID = '%d' message = '%s', byteArrayIsNull = %b", resultOfCheck, authorID, receiverID, message, byteArray == null);
				log.error(messageForLog);
				return Response.status(400).entity(messageForLog).build();
			}
			
			messageForLog = String.format("resultOfCheck = '%b' authorID = '%d' receiverID = '%d' message = '%s', sizeOfFile = %d byte, mediaType = '%s'", resultOfCheck, authorID, receiverID, message, byteArray.length, mediaType);
			log.info(messageForLog);
			//TODO: Эта часть отличается.
			idOfNewMessage = messageDAO.addNewMessageToGroupWithFile(message, authorID, receiverID, byteArray, mediaType);
			
		} catch (ExceptionParseRequest e) {			
			messageForLog = String.format("Сообщение создано не будет: %s", e.getMessage());			
			log.error(messageForLog, e);
			return Response.status(400).entity(messageForLog).build();
		} catch (Exception e) {
			messageForLog = String.format("Сообщение создано не будет: %s", e.getMessage());			
			log.error(messageForLog, e);
			return Response.status(400).entity(messageForLog).build();
		}
		String forResponse = String.format("{\"idOfNewMessage\" : %d}", idOfNewMessage);
		return Response.status(200).entity(forResponse).build();
	}	
	
	@POST
	@Path("/addNewMessageToUser/")
	@Produces({ "application/json" })
	@Consumes("multipart/form-data")	
	public Response addNewMessageToUser(@MultipartForm MessageForm form) {
		String messageForLog = "";
		
		
		int authorID = form.getAuthorId();
		int receiverID = form.getReceiverId();
		String message = form.getMessage();
		boolean resultOfCheck = form.checkIsPresentAllRequiredParameters();
		
		int idOfNewMessage = 0;
		
		if (!resultOfCheck) {
			messageForLog = String.format("Сообщение создано не будет, один из параметров пуст или отсутствует: resultOfCheck = '%b' authorID = '%d' receiverID = '%d' message = '%s'", resultOfCheck, authorID, receiverID, message);
			log.error(messageForLog);
			return Response.status(400).entity(messageForLog).build();
		}
		
		messageForLog = String.format("resultOfCheck = '%b' authorID = '%d' receiverID = '%d' message = '%s'", resultOfCheck, authorID, receiverID, message);
		log.info(messageForLog);
		
		try {
			//TODO: Только эта часть отличается от аналогичного метода addNewMessageToGroup(...) - нужно вынести общую часть в отдельный утилитартный класс.
			idOfNewMessage = messageDAO.addNewMessage(message, authorID, receiverID);
		} catch (ExceptionForAddMessage e) {
			messageForLog = String.format("Сообщение не было добавлено: %s", e.getMessage());
			log.error(messageForLog);
			return Response.status(400).entity(messageForLog).build();
		}
		String forResponse = String.format("{\"idOfNewMessage\" : %d}", idOfNewMessage);
		return Response.status(200).entity(forResponse).build();
	}
	

	@POST
	@Path("/addNewMessageWithFileToUser/")
	@Produces({ "application/json" })
	@Consumes("multipart/form-data")
	public Response addNewMessageWithFileToUser(MultipartFormDataInput input) {
		Map<String, List<InputPart>> mapFormData = input.getFormDataMap();
		iHandlerPostRequestForNewMessageWithFile hprfnm = new HandlerPostRquestForNewMessageWithFileFirst(mapFormData);
		String messageForLog;
		int idOfNewMessage;
		try {
			
			int authorID = hprfnm.getAuthorId();
			int receiverID = hprfnm.getReceiverId();
			String message = hprfnm.getMessage();
			byte[] byteArray = hprfnm.getByteArrayOfFile();
			String mediaType = hprfnm.getMediaTypeOfFile();			
			boolean resultOfCheck = hprfnm.checkIsPresentAllRequiredParameters();
			
			if (!resultOfCheck) {
				messageForLog = String.format("Сообщение создано не будет, один из параметров пуст или отсутствует: resultOfCheck = '%b' authorID = '%d' receiverID = '%d' message = '%s', byteArrayIsNull = %b", resultOfCheck, authorID, receiverID, message, byteArray == null);
				log.error(messageForLog);
				return Response.status(400).entity(messageForLog).build();
			}
			
			messageForLog = String.format("resultOfCheck = '%b' authorID = '%d' receiverID = '%d' message = '%s', sizeOfFile = %d byte, mediaType = '%s'", resultOfCheck, authorID, receiverID, message, byteArray.length, mediaType);
			log.info(messageForLog);
			//TODO: Только эта часть отличается от аналогичного метода addNewMessageWithFileToGroup(...) - нужно вынести общую часть в отдельный утилитартный класс.
			idOfNewMessage = messageDAO.addNewMessageToUserWithFile(message, authorID, receiverID, byteArray, mediaType);
			
		} catch (ExceptionParseRequest e) {
			messageForLog = String.format("Сообщение создано не будет: %s", e.getMessage());
			log.error(messageForLog, e);
			return Response.status(400).entity(messageForLog).build();
		} catch (Exception e) {
			messageForLog = String.format("Сообщение создано не будет: %s", e.getMessage());
			log.error(messageForLog, e);
			return Response.status(400).entity(messageForLog).build();
		}
		String forResponse = String.format("{\"idOfNewMessage\" : %d}", idOfNewMessage);
		return Response.status(200).entity(forResponse).build();
	}
	

	// TODO: Доделать, чтоб по ID не извлекались сообщения-файлы что помечены как удаленные.
	@GET
	@Path("/getFileOfMessage/messageId---{messageId}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response downloadFile(@PathParam("messageId") Integer messageId) {
		//TODO: Добавить проверку, есть ли файл is_present_file - если нет, то возвращать 400
		Message message = messageDAO.getMessageById(messageId);
		byte[] byteArray = message.getFile();
	    return Response.ok(byteArray, MediaType.APPLICATION_OCTET_STREAM).header("Content-Disposition", "attachment; filename=\"FileFromMessage.jpg\"").build();
	}
	
	// +++++
	// TODO: Доделать, чтоб по ID не извлекались сообщения что помечены как удаленные.
	@DELETE
	@Path("/deleteMessage/messageId---{messageId}")
	@Produces({ "application/json" })
	public Response deleteMessage(@PathParam("messageId") Integer messageId) {
		Message message = messageDAO.getMessageById(messageId);
		messageDAO.deleteMessage(message);
		return Response.ok(200).entity(String.format("Сообщение c ID=%d удалено!", message.getId())).build();
	}

	//----------------------------------------------------------------------------------------------------------------------------------
	//Методы для аппробации - другого разбора сообщений Post
	//----------------------------------------------------------------------------------------------------------------------------------
	@POST
	@Path("/parsePostRequest_only_for_try_1/")
	@Produces({ "application/json" })
	@Consumes("multipart/form-data")
	public Response parsePostRequest_1(@MultipartForm MessageFormWithFile form) {
		
		// TODO: Необходимо добавить обработку исключений.		
		int authorID = form.getAuthorId();
		int receiverID = form.getReceiverId();
		String message = form.getMessage();
		byte[] byteArray = form.getByteArrayOfFile();
		String mediaType = form.getMediaTypeOfFile();
		boolean resultOfCheck = form.checkIsPresentAllRequiredParameters();
		
		if (!resultOfCheck) {
			String messageForLog = String.format("Сообщение создано не будет, один из параметров пуст или отсутствует: resultOfCheck = '%b' authorID = '%d' receiverID = '%d' message = '%s', byteArrayIsNull = %b", resultOfCheck, authorID, receiverID, message, byteArray == null);
			log.error(messageForLog);
			return Response.status(400).entity(messageForLog).build();
		}
		String messageForLog = String.format("resultOfCheck = '%b' authorID = '%d' receiverID = '%d' message = '%s', sizeOfFile = %d byte, mediaType = '%s'", resultOfCheck, authorID, receiverID, message, byteArray.length, mediaType);
		log.info(messageForLog);
		// messageDAO.addNewMessageWithFile(message, authorID, receiverID, byteArray, mediaType);
		return Response.status(200).entity(messageForLog).build();
	}

	@POST
	@Path("/parsePostRequestFor_only_for_try_2/")
	@Produces({ "application/json" })
	@Consumes("multipart/form-data")
	public Response parsePostRequest_2(MultipartFormDataInput input) {
		iHandlerPostRequestForNewMessageWithFile hprfnm = new HandlerPostRquestForNewMessageWithFileSecond(input);
		String messageForLog;
		try {
			
			int authorID = hprfnm.getAuthorId();
			int receiverID = hprfnm.getReceiverId();
			String message = hprfnm.getMessage();
			byte[] byteArray = hprfnm.getByteArrayOfFile();
			String mediaType = hprfnm.getMediaTypeOfFile();
			boolean resultOfCheck = hprfnm.checkIsPresentAllRequiredParameters();
			
			if (!resultOfCheck) {
				messageForLog = String.format("Сообщение создано не будет, один из параметров пуст или отсутствует: resultOfCheck = '%b' authorID = '%d' receiverID = '%d' message = '%s', byteArrayIsNull = %b", resultOfCheck, authorID, receiverID, message, byteArray == null);
				log.error(messageForLog);
				return Response.status(400).entity(messageForLog).build();
			}
			
			messageForLog = String.format("resultOfCheck = '%b' authorID = '%d' receiverID = '%d' message = '%s', sizeOfFile = %d byte, mediaType = '%s'", resultOfCheck, authorID, receiverID, message, byteArray.length, mediaType);
			log.info(messageForLog);
			// messageDAO.addNewMessageWithFile(message, authorID, receiverID, byteArray, mediaType);
			
		} catch (ExceptionParseRequest e) {
			messageForLog = String.format("Сообщение создано не будет: %s", e.getMessage());			
			log.error(messageForLog, e);
			return Response.status(400).entity(messageForLog).build();
		} catch (Exception e) {
			messageForLog = String.format("Сообщение создано не будет: %s", e.getMessage());			
			log.error(messageForLog, e);
			return Response.status(400).entity(messageForLog).build();			
		}
		return Response.status(200).entity(messageForLog).build();
	}
	//----------------------------------------------------------------------------------------------------------------------------------
	//Методы что были добавлены до обработки Post нужно удалить.
	//----------------------------------------------------------------------------------------------------------------------------------
	
	// +++++
	//TODO: Удалить, добавлял для первичной отладки добавления сообщения в БД.
	@GET
	@Path("/addNewMessage/authorId---{authorId}/receiverId---{receiverId}/text---{text}")
	@Produces({ "application/json" })
	@Consumes({ "application/json" })
	public Response newMessageForAuthorIdAndReceiverId(@PathParam("authorId") Integer authorId, @PathParam("receiverId") Integer receiverId, @PathParam("text") String text) {
		try {
			int idOfNewMessage = messageDAO.addNewMessage(text, authorId, receiverId);
			//List<Message> messages = messageDAO.getMessageByAuthorIdAndReceiverId(authorId, receiverId, false);
			//TransferToDTOMessage transfer = new TransferToDTOMessage();
			//return Response.ok(transfer.transferListMessageToListDTOMessage(messages)).build();
			String forResponse = String.format("{\"idOfNewMessage\" : '%d'}", idOfNewMessage);
			return Response.status(200).entity(forResponse).build();
		} catch (ExceptionForAddMessage e) {
			String message = String.format("Сообщение не было добавлено: %s", e.getMessage());
			return Response.status(400).entity(message).build();
		}
	}
	
	
	// +++++
	//TODO: Удалить, добавлял для первичной отладки добавления сообщения в БД.
	@GET
	@Path("/addNewMessage/authorId---{authorId}/groupId---{groupId}/text---{text}")
	@Produces({ "application/json" })
	@Consumes({ "application/json" })
	public Response newMessageForAuthorIdAndGroupId(@PathParam("authorId") Integer authorId, @PathParam("groupId") Integer groupId, @PathParam("text") String text) {
		try {
			int idOfNewMessage = messageDAO.addNewMessageToGroup(text, authorId, groupId);
			//List<Message> messages = messageDAO.getMessageByReceiverGroupId(groupId, false);
			//TransferToDTOMessage transfer = new TransferToDTOMessage();
			//return Response.ok(transfer.transferListMessageToListDTOMessage(messages)).build();
			String forResponse = String.format("{\"idOfNewMessage\" : '%d'}", idOfNewMessage);
			return Response.status(200).entity(forResponse).build();
		} catch (ExceptionForAddMessage e) {
			String messageForLog = String.format("Сообщение не было добавлено: %s", e.getMessage());
			return Response.status(400).entity(messageForLog).build();
		}	
	}
//----------------------------------------------------------------------------------------------------------------------------------

}
