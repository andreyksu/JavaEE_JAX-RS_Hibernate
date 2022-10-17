package ru.annikonenkov.rs.message.entities.user;

import java.util.ArrayList;
import java.util.List;

public class TransferToDTOUser {

	public List<DTOUser> transferListUserToListDTOUser(List<User> listOfUsers) {
		List<DTOUser> listOfDTOUsers = new ArrayList<>();
		for (User user : listOfUsers) {
			listOfDTOUsers.add(transferUserToDTOUser(user));
		}
		return listOfDTOUsers;
	}

	public DTOUser transferUserToDTOUser(User user) {
		DTOUser dtoUser = new DTOUser(user.getId(), user.getName(), user.getFullName(), user.getEmail(), user.getIsDeleted(), user.getBanned(), user.getRegDate(), user.getBirthDay());
		return dtoUser;
	}

}
