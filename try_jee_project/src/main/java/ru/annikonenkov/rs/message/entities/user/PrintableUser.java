package ru.annikonenkov.rs.message.entities.user;

import java.util.List;

public class PrintableUser {

	public static String doPrintableUser(User user) {
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append("User ID = ");
		sb.append(user.getId());
		sb.append(" ---------> ");
		sb.append("Name = ");
		sb.append(user.getName());
		sb.append(" ---------> ");
		sb.append("Email = ");
		sb.append(user.getEmail());
		return sb.toString();
	}

	public static String doPrintableUsers(List<User> users) {
		StringBuilder sb = new StringBuilder();
		users.forEach(userItem -> sb.append(doPrintableUser(userItem)));
		return sb.toString();
	}

}
