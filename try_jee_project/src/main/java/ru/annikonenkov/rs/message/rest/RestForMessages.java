package ru.annikonenkov.rs.message.rest;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.jboss.logging.Logger;
import ru.annikonenkov.rs.message.entities.group.Group;
import ru.annikonenkov.rs.message.entities.group.GroupDAO;
import ru.annikonenkov.rs.message.entities.message.Message;
import ru.annikonenkov.rs.message.entities.message.MessageDAO;
import ru.annikonenkov.rs.message.entities.message.PrintableMessage;
import ru.annikonenkov.rs.message.entities.user.PrintableUser;
import ru.annikonenkov.rs.message.entities.user.User;
import ru.annikonenkov.rs.message.entities.user.UserDAO;

@Path("/chat/messages")
@RequestScoped
//@Transactional
//@Stateless
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
	public Response getMessagesByAuthorIdAndReceiverId(@PathParam("messageId") Integer messageId) {
		Message message = messageDAO.getMessageById(messageId);
		return Response.ok(PrintableMessage.doPrintableMessage(message)).build();
	}

	// +++++
	@GET
	@Path("/getMessages/authorId---{authorId}/receiverId---{receiverId}")
	@Produces({ "application/json" })
	public Response getMessagesByAuthorIdAndReceiverId(@PathParam("authorId") Integer authorId,
			@PathParam("receiverId") Integer receiverId) {
		List<Message> messages = messageDAO.getMessageByAuthorIdAndReceiverId(authorId, receiverId, false);
		return Response.ok(PrintableMessage.doPrintableMessages(messages)).build();
	}

	// +++++
	@GET
	@Path("/getMessages/groupId---{groupId}")
	@Produces({ "application/json" })
	public Response getMessagesByAuthorIdAndGroupId(@PathParam("groupId") Integer groupId) {
		List<Message> messages = messageDAO.getMessageByReceiverGroupId(groupId, false);
		return Response.ok(PrintableMessage.doPrintableMessagesGroup(messages)).build();
	}

	// +++++
	@GET
	@Path("/addNewMessage/authorId---{authorId}/receiverId---{receiverId}/text---{text}")
	@Produces({ "application/json" })
	public Response newMessageForAuthorIdAndReceiverId(@PathParam("authorId") Integer authorId, @PathParam("receiverId") Integer receiverId, @PathParam("text") String text) {
		boolean result = messageDAO.addNewMessage(text, authorId, receiverId);
		if (result) {
			List<Message> messageFinded = messageDAO.getMessageByAuthorIdAndReceiverId(authorId, receiverId, false);
			return Response.ok(PrintableMessage.doPrintableMessages(messageFinded)).build();
		}		
		return Response.status(400, "Не найден целевой пользователь или целевой получатель!").build();
	}

	// +++++
	// @PUT
	@GET
	@Path("/addNewMessage/authorId---{authorId}/groupId---{groupId}/text---{text}")
	@Produces({ "application/json" })
	public Response newMessageForAuthorIdAndGroupId(@PathParam("authorId") Integer authorId, @PathParam("groupId") Integer groupId, @PathParam("text") String text) {
		boolean result = messageDAO.addNewMessageToGroup(text, authorId, groupId);
		if (result) {
			List<Message> messageFinded = messageDAO.getMessageByReceiverGroupId(groupId, false);
			return Response.ok(PrintableMessage.doPrintableMessagesGroup(messageFinded)).build();
		}
		return Response.status(400, "Не найден целевой пользователь или целевая группа!").build();
	}

	// +++++
	// @DELETE
	@GET
	@Path("/deleteMessage/messageId---{messageId}")
	@Produces({ "application/json" })
	public Response newMessageForAuthorIdAndGroupId(@PathParam("messageId") Integer messageId) {
		Message message = messageDAO.getMessageById(messageId);
		messageDAO.deleteMessage(message);
		return Response.ok().build();
	}
}
