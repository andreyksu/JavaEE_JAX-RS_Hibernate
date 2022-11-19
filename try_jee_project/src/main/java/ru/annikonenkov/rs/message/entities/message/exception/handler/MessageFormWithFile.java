package ru.annikonenkov.rs.message.entities.message.exception.handler;

import javax.ws.rs.FormParam;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

public class MessageFormWithFile implements iHandlerPostRequestForNewMessageWithFile {
	private byte[] byteArrayOfFile;
	private String message;
	private int authorId;
	private int receiverId;

	public MessageFormWithFile() {
	}

	@Override
	public boolean checkIsPresentAllRequiredParameters() {
		return (!(authorId == 0 || receiverId == 0 || message == null || byteArrayOfFile == null));
	}

	@Override
	public Integer getAuthorId() {
		return authorId;
	}

	@Override
	public Integer getReceiverId() {
		return receiverId;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public byte[] getByteArrayOfFile() {
		return byteArrayOfFile;
	}

	@Override
	public String getMediaTypeOfFile() {
		return null;
	}

	@FormParam("file")
	@PartType("application/octet-stream")
	public void setFile(final byte[] file) {
		this.byteArrayOfFile = file;
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

	@Override
	public void printCommonInfo() {
		// TODO Auto-generated method stub
	}
}
