package ru.annikonenkov.rs.message.entities.message.handler;

public enum MessageFileParameters {

	AuthorID("authorId"), ReceiverID("receiverId"), TextMessage("textMessage"), File("file");

	private String parameter;

	MessageFileParameters(String parameter) {
		this.parameter = parameter;
	}

	public String getParameter() {
		return this.parameter;
	}

	@Override
	public String toString() {
		return this.parameter;
	}

}
