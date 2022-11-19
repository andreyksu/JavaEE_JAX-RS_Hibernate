package ru.annikonenkov.rs.message.rest;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import javax.inject.Inject;

import javax.ws.rs.GET;

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
import ru.annikonenkov.rs.message.entities.group.dto.DTOGroup;
import ru.annikonenkov.rs.message.entities.group.dto.TransferToDTOGroup;
import ru.annikonenkov.rs.message.entities.message.MessageDAO;
import ru.annikonenkov.rs.message.entities.user.User;
import ru.annikonenkov.rs.message.entities.user.UserDAO;
import ru.annikonenkov.rs.message.entities.user.dto.DTOUser;
import ru.annikonenkov.rs.message.entities.user.dto.TransferToDTOUser;

@Stateless
@Path("/chat/users")
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
		// return Response.ok(targetUser).build();
		TransferToDTOUser transfer = new TransferToDTOUser();
		return Response.ok(transfer.transferUserToDTOUser(targetUser)).build();

	}

	@GET
	@Path("/getUserByIdForStart/userId==={userId}")
	@Produces({ "application/json" })
	public Response getUserByIdForStart(@PathParam("userId") Integer userId) {
		User targetUser = userDAO.getUserById(userId);
		List<User> friends = userDAO.getFriendsOfUserWithUserId(userId, false);
		List<Group> groups = groupDAO.getAllGroupsForUserId(userId, false);
		TransferToDTOUser transferUser = new TransferToDTOUser();
		TransferToDTOGroup transgerGroup = new TransferToDTOGroup();

		DTOUser dtoUser = transferUser.transferUserToDTOUser(targetUser);
		List<DTOUser> dtoFriends = transferUser.transferListUserToListDTOUser(friends);
		List<DTOGroup> dtoGroups = transgerGroup.transferGroupListToDTOGroupList(groups);

		dtoUser.setFriends(dtoFriends);
		dtoUser.setGroups(dtoGroups);

		return Response.ok(dtoUser).build();
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
	// @DELETE
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
