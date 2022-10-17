package ru.annikonenkov.rs.message.rest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
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
import ru.annikonenkov.rs.message.entities.message.MessageDAO;
import ru.annikonenkov.rs.message.entities.user.PrintableUser;
import ru.annikonenkov.rs.message.entities.user.TransferToDTOUser;
import ru.annikonenkov.rs.message.entities.user.User;
import ru.annikonenkov.rs.message.entities.user.UserDAO;

@Path("/chat/users")
//@RequestScoped
@Stateless
public class RestForUsers {

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
	@Path("/getUserById/userId==={userId}")
	@Produces({ "application/json" })
	public Response getUserById(@PathParam("userId") Integer userId) {
		User targetUser = userDAO.getUserById(userId);
		TransferToDTOUser transfer = new TransferToDTOUser();
		return Response.ok(transfer.transferUserToDTOUser(targetUser)).build();
	}

	// +++++
	@GET
	@Path("/getUsersByGroupId/groupId==={groupId}")
	@Produces({ "application/json" })
	public Response getUsersByGroupId(@PathParam("groupId") Integer groupId) {
		List<User> linkedUsers = userDAO.getUsersByGroupID(groupId, false);
		TransferToDTOUser transfer = new TransferToDTOUser();
		return Response.ok(transfer.transferListUserToListDTOUser(linkedUsers)).build();
	}

	// +++++
	@GET
	@Path("/getFriendsOfUserId_TMP/userId==={userId}")
	@Produces({ "application/json" })
	public Response getFriedsOfUserId_TMP(@PathParam("userId") Integer userId) {
		User targetUser = userDAO.getUserById(userId);
		List<User> list = userDAO.getFriendsOfUserWithUserId(userId, false);
		StringBuilder sbForLogOut = new StringBuilder();
		sbForLogOut.append("The size of messages array = ");
		sbForLogOut.append(list.size());
		sbForLogOut.append("\n");
		sbForLogOut.append("Target:");
		sbForLogOut.append(PrintableUser.doPrintableUser(targetUser));
		sbForLogOut.append("\n");
		sbForLogOut.append("Friends:");
		sbForLogOut.append("\n");
		sbForLogOut.append(PrintableUser.doPrintableUsers(list));
		return Response.ok(sbForLogOut.toString()).build();
	}

	@GET
	@Path("/getFriendsOfUserId/userId==={userId}")
	@Produces({ "application/json" })
	public Response getFriedsOfUserId(@PathParam("userId") Integer userId) {
		User targetUser = userDAO.getUserById(userId);
		List<User> list = userDAO.getFriendsOfUserWithUserId(userId, false);
		TransferToDTOUser transfer = new TransferToDTOUser();
		return Response.ok(transfer.transferListUserToListDTOUser(list)).build();
	}

	// +++++
	// @PUT
	@GET
	@Path("/addNewUser/userName---{userName}/fullName---{fullName}/email---{email}")
	@Produces({ "application/json" })
	public Response addNewUser(@PathParam("userName") String userName, @PathParam("fullName") String fullName,
			@PathParam("email") String email) {
		User user = userDAO.addNewUser(userName, fullName, email);
		List<User> allUsers = userDAO.getAllUsers(false);
		TransferToDTOUser transfer = new TransferToDTOUser();
		return Response.ok(transfer.transferListUserToListDTOUser(allUsers)).build();
	}

	// +++++
	@GET
	@Path("/deleteUserById/userId---{userId}")
	@Produces({ "application/json" })
	public Response deleteUserById(@PathParam("userId") Integer userId) {
		boolean result = userDAO.deleteUser(userId);
		if (result) {
			List<User> allUsers = userDAO.getAllUsers(false);
			TransferToDTOUser transfer = new TransferToDTOUser();
			return Response.ok(transfer.transferListUserToListDTOUser(allUsers)).build();
		}
		return Response.status(400, "Не найден целевой пользователь!").build();
	}

	// +++++
	// @PUT
	@GET
	@Path("/addFriendToUser/userId---{userId}/friendId---{friendId}")
	@Produces({ "application/json" })
	public Response addFriendToUser(@PathParam("userId") Integer userId, @PathParam("friendId") Integer friendId) {
		boolean result = userDAO.addFriendToUser(userId, friendId);
		if (result)
			return Response.ok("{\"message\": \"Пользователю добавлен друг!\"}").build();
		return Response.status(400, "Не найдены целевые пользователи!").build();
	}

	// +++++
	// @DELETE
	@GET
	@Path("/deleteFriendFromUser/userId---{userId}/friendId---{friendId}")
	@Produces({ "application/json" })
	public Response deleteFriendFromUser(@PathParam("userId") Integer userId, @PathParam("friendId") Integer friendId) {
		boolean result = userDAO.deleteFriendToUser(userId, friendId);
		if (result)
			return Response.ok("{\"message\": \"Пользователь успешно удален из друзей!\"}").build();
		return Response.status(400, "Не найдены целевые пользователи").build();
	}

	// --------------------------------------Поиск--------------------------
	@GET
	@Path("/getUserByMask/userNameMask---{userNameMask}")
	@Produces({ "application/json" })
	public Response getUserByMask(@PathParam("userNameMask") String userNameMask) {
		List<User> userList = userDAO.getUsersByMaskOfNameViaNamedQuery(userNameMask, false);
		TransferToDTOUser transfer = new TransferToDTOUser();
		return Response.ok(transfer.transferListUserToListDTOUser(userList)).build();
	}
}
