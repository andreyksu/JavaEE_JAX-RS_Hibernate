package ru.annikonenkov.rs.message.entities.message.exception.handler;

import javax.ws.rs.FormParam;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

public class MessageForm {

	private String message;
	private int authorId;
	private int receiverId;

	public MessageForm() {

	}
	
	public boolean checkIsPresentAllRequiredParameters() {
		return (!(authorId == 0 || receiverId == 0 || message == null));
	}

	public String getMessage() {
		return message;
	}

	public Integer getAuthorId() {
		return this.authorId;
	}

	public Integer getReceiverId() {
		return this.receiverId;
	}

	@FormParam("textMessage")
	@PartType("text/plain")
	public void setTextMessage(final String message) {
		this.message = message;
	}

	@FormParam("authorId")
	@PartType("text/plain")
	public void setAuthor(final int authorId) {
		this.authorId = authorId;
	}

	@FormParam("receiverId")
	@PartType("text/plain")
	public void setRaceiver(final int receiverId) {
		this.receiverId = receiverId;
	}

}
