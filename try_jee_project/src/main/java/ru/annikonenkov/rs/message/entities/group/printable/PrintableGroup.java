package ru.annikonenkov.rs.message.entities.group.printable;

import java.util.List;

import ru.annikonenkov.rs.message.entities.group.Group;

public class PrintableGroup {

	public static String doPrintableGriup(Group group) {
		StringBuilder sbForGroup = new StringBuilder();
		sbForGroup.append("\n");
		sbForGroup.append("Group Id = ");		
		sbForGroup.append(group.getId());
		sbForGroup.append(" -----> ");
		sbForGroup.append("Group Name = ");
		sbForGroup.append(group.getName());
		return sbForGroup.toString();
	}

	public static String doPrintableGriups(List<Group> groups) {
		StringBuilder sbForGroup = new StringBuilder();
		groups.forEach(currentGr -> sbForGroup.append(doPrintableGriup(currentGr)));
		return sbForGroup.toString();
	}

}
