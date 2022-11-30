package ru.annikonenkov.rs.message.entities.message.dto;

import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;

import ru.annikonenkov.rs.message.entities.group.Group;
import ru.annikonenkov.rs.message.entities.group.dto.DTOGroup;
import ru.annikonenkov.rs.message.entities.group.dto.TransferToDTOGroup;
import ru.annikonenkov.rs.message.entities.message.Message;
import ru.annikonenkov.rs.message.entities.user.User;
import ru.annikonenkov.rs.message.entities.user.dto.DTOUser;
import ru.annikonenkov.rs.message.entities.user.dto.TransferToDTOUser;

public class TransferToDTOMessage {
	
	Logger log = Logger.getLogger(TransferToDTOMessage.class);

	TransferToDTOUser transferToDTOUser = new TransferToDTOUser();
	TransferToDTOGroup transferToDTOGroup = new TransferToDTOGroup();

	public List<DTOMessage> transferListMessageToListDTOMessage(List<Message> messagesList) {
		List<DTOMessage> listOfDTOMessages = new ArrayList<>();
		for (Message message : messagesList) {
			listOfDTOMessages.add(transeferMessageToDTOMessage(message));
		}
		return listOfDTOMessages;
	}

	public DTOMessage transeferMessageToDTOMessage(Message message) {
		DTOMessage dtoMessage = new DTOMessage(message.getId(), message.getDateOfMessage(), message.getTextOfMessage(),
				message.getIsPresentFile(), message.getMimeType());

		User author = message.getAuthor();
		if (author != null) {
			//TODO: Нужно генерировать исключение.
			//log.info(String.format("AuthorID = %d", author.getId()));
			dtoMessage.setAuthor(transferToDTOUser.transferUserToDTOUser(author));
		} else {
			dtoMessage.setAuthor(null);
		}

		User receiver = message.getReceiver();
		if (receiver != null) {
			//log.info(String.format("ReceiverID = %d", receiver.getId()));
			dtoMessage.setReceiver(transferToDTOUser.transferUserToDTOUser(receiver));
		} else {
			//dtoMessage.setReceiver(new DTOUser(0, null, null, null, false, false, null, null));
			dtoMessage.setReceiver(new DTOUser());
		}

		Group groupReceiver = message.getGroupReceiver();
		if (groupReceiver != null) {
			//log.info(String.format("GroupID = %d", groupReceiver.getId()));
			dtoMessage.setGroupReceiver(transferToDTOGroup.transferGroupToDTOGroup(groupReceiver));
		} else {
			//dtoMessage.setGroupReceiver(new DTOGroup(0, null, false, false, null));
			dtoMessage.setGroupReceiver(new DTOGroup());
		}
		return dtoMessage;
	}
}
