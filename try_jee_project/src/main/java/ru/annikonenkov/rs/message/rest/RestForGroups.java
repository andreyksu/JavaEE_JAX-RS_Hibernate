package ru.annikonenkov.rs.message.rest;

import java.util.ArrayList;
import java.util.List;

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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.jboss.logging.Logger;

import ru.annikonenkov.rs.message.entities.group.Group;
import ru.annikonenkov.rs.message.entities.group.GroupDAO;
import ru.annikonenkov.rs.message.entities.group.dto.TransferToDTOGroup;
import ru.annikonenkov.rs.message.entities.group.printable.PrintableGroup;
import ru.annikonenkov.rs.message.entities.message.MessageDAO;
import ru.annikonenkov.rs.message.entities.user.User;
import ru.annikonenkov.rs.message.entities.user.UserDAO;
import ru.annikonenkov.rs.message.entities.user.printable.PrintableUser;

//Со слов Э.Гонсалвес - входной точкой должен быть EJB.
//@RequestScoped
//@Transactional Работает нормально. К ошибке не приводит, как если установить у метода.
@Stateless
@Path("/chat/groups")
public class RestForGroups {

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
	@Path("/getGroupById/groupId---{groupId}")
	@Produces({ "application/json" })
	public Response getGroupById(@PathParam("groupId") Integer groupId) {
		Group group = groupDAO.getGroupById(groupId);
		TransferToDTOGroup transfer = new TransferToDTOGroup();
		// return Response.ok(PrintableGroup.doPrintableGriup(group)).build();
		return Response.ok(transfer.transferGroupToDTOGroup(group)).build();
	}

	// +++++
	@GET
	@Path("/getGroupsByUserId")
	@Produces({ "application/json" })
	public Response getGroupsByUserId(@QueryParam("uId") Integer userId) {
		StringBuilder sbForGroup = new StringBuilder();
		List<Group> groupList = groupDAO.getAllGroupsForUserId(userId, false);
		groupList.forEach(groupItem -> {
			sbForGroup.append(PrintableGroup.doPrintableGriup(groupItem));
			List<User> listOfUsers = new ArrayList<>(groupItem.getUsers());
			sbForGroup.append(PrintableUser.doPrintableUsers(listOfUsers));
			sbForGroup.append("\n");
		});
		return Response.ok(sbForGroup.toString()).build();
	}

	// +++++
	// @PUT
	@GET
	@Path("/addNewGroup/groupName---{groupName}")
	@Produces({ "application/json" })
	public Response addNewGroup(@PathParam("groupName") String groupName) {
		Group group = groupDAO.addNewGroup(groupName);
		Group grFromDB = groupDAO.getGroupById(group.getId());
		return Response.ok(PrintableGroup.doPrintableGriup(grFromDB)).build();
	}

	// +++++
	// @DELETE
	@GET
	@Path("/deleteGroupById/groupId---{groupId}")
	@Produces({ "application/json" })
	public Response deleteGroupById(@PathParam("groupId") Integer groupId) {
		Group gr = groupDAO.getGroupById(groupId);
		groupDAO.deleteGroup(gr);
		List<Group> resultList = groupDAO.getAllGroups(false);
		return Response.ok(PrintableGroup.doPrintableGriups(resultList)).build();
	}

	// +++++
	// @PUT
	@GET
	@Path("/addUserIdToGroupsId/userId---{userId}/grId---{grId}")
	@Produces({ "application/json" })
	public Response addUserIdToGroupsId(@PathParam("userId") Integer userId, @PathParam("grId") Integer grId) {
		boolean result = groupDAO.addUserToGroup(userId, grId);
		if (result)
			return Response.ok("Успешно добавлен пользователь в группу").build();
		return Response.status(400, "Не найден целевой пользователь!").build();
	}

	// +++++
	// @DELETE
	@GET
	@Path("/deleteUserIdFromGroupsId/userId---{userId}/grId---{grId}")
	@Produces({ "application/json" })
	public Response deleteUserIdFromGroupsId(@PathParam("userId") Integer userId, @PathParam("grId") Integer grId) {
		boolean result = groupDAO.deleteUserFromGroup(userId, grId);
		if (result)
			return Response.ok("Успешно удален пользователь из группы").build();
		return Response.status(400, "Не найден целевой пользователь!").build();
	}

	// --------------------------------------Поиск--------------------------
	@GET
	@Path("/getGroupsByName/groupName---{groupName}")
	@Produces({ "application/json" })
	public Response getGroupsByName(@PathParam("groupName") String groupName) {
		List<Group> grs = groupDAO.getTargetGroupsByName(groupName, false);
		return Response.ok(PrintableGroup.doPrintableGriups(grs)).build();
	}
	// --------------------------------------Поиск--------------------------
}
