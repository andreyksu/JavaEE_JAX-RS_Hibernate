package ru.annikonenkov.rs.message.entities.message.printable;

import java.util.List;

import ru.annikonenkov.rs.message.entities.message.Message;

public class PrintableMessage {

	public static String doPrintableMessage(Message message) {
		StringBuilder messageSB = new StringBuilder();
		messageSB.append("Message Id = ");
		messageSB.append(message.getId());
		messageSB.append(" -----> ");
		messageSB.append(message.getTextOfMessage());
		messageSB.append(" -----> ");
		messageSB.append(" Author = ");
		messageSB.append(message.getAuthor().getName());
		messageSB.append(" -----> ");
		messageSB.append(" Receiver = ");
		messageSB.append(message.getReceiver().getName());
		messageSB.append("\n");
		return messageSB.toString();
	}

	public static String doPrintableMessages(List<Message> messages) {
		StringBuilder messageSB = new StringBuilder();
		messages.forEach(messageItem -> messageSB.append(doPrintableMessage(messageItem)));
		return messageSB.toString();
	}

	public static String doPrintableMessageGroup(Message message) {
		StringBuilder messageSB = new StringBuilder();
		messageSB.append("Message Id = ");
		messageSB.append(message.getId());
		messageSB.append(" -----> ");
		messageSB.append(message.getTextOfMessage());
		messageSB.append(" -----> ");
		messageSB.append(" Author = ");
		messageSB.append(message.getAuthor().getName());
		messageSB.append(" -----> ");
		messageSB.append(" ReceiverGR = ");
		messageSB.append(message.getGroupReceiver().getName());
		messageSB.append("\n");
		return messageSB.toString();
	}

	public static String doPrintableMessagesGroup(List<Message> messages) {
		StringBuilder messageSB = new StringBuilder();
		messages.forEach(messageItem -> messageSB.append(doPrintableMessageGroup(messageItem)));
		return messageSB.toString();
	}

}
