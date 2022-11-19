package ru.annikonenkov.rs.message.entities.group.dto;

import java.util.ArrayList;
import java.util.List;

import ru.annikonenkov.rs.message.entities.group.Group;

public class TransferToDTOGroup {

	public List<DTOGroup> transferGroupListToDTOGroupList(List<Group> groupList) {
		List<DTOGroup> dtoGroupList = new ArrayList<>();
		for (Group gr : groupList) {
			dtoGroupList.add(transferGroupToDTOGroup(gr));
		}
		return dtoGroupList;
	}

	public DTOGroup transferGroupToDTOGroup(Group group) {
		DTOGroup dtoGroup = new DTOGroup(group.getId(), group.getName(), group.getActivity(), group.getDeleted(), group.getCreateDate());
		return dtoGroup;
	}

}
