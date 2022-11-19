package ru.annikonenkov.rs.message.entities.message.dto;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import ru.annikonenkov.rs.message.entities.group.dto.DTOGroup;
import ru.annikonenkov.rs.message.entities.user.dto.DTOUser;

public class DTOMessage {
//TODO: Добавить наличие/отсутствие файла - в БД есть поле.
	private int id;
	private Date dateOfMessage;
	private String textOfMessage;
	private boolean isPresentFile;
	private String mimeType;
	private DTOUser author;
	private DTOUser receiver;
	private DTOGroup groupReceiver;

	public DTOMessage(int id, Date dateOfMessage, String textOfMessage, boolean isPresentFile, String mimeType, DTOUser author, DTOUser receiver, DTOGroup groupReceiver) {
		this.id = id;
		this.dateOfMessage = dateOfMessage;
		this.textOfMessage = textOfMessage;
		this.isPresentFile = isPresentFile;
		this.mimeType = mimeType;
		this.author = author;
		this.receiver = receiver;
		this.groupReceiver = groupReceiver;
	}

	public DTOMessage(int id, Date dateOfMessage, String textOfMessage, boolean isPresentFile, String mimeType) {
		this.id = id;
		this.dateOfMessage = dateOfMessage;
		this.textOfMessage = textOfMessage;
		this.isPresentFile = isPresentFile;
		this.mimeType = mimeType;
	}
	
	public DTOMessage() {
		this.id = 0;
		this.dateOfMessage = Date.from(LocalDate.ofEpochDay(0).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
		this.textOfMessage = "";
		this.isPresentFile = false;
		this.mimeType = "";
		this.author = new DTOUser();
		this.receiver = new DTOUser();
		this.groupReceiver = new DTOGroup();		
	}

	public int getId() {
		return this.id;
	}

	public Date getDateOfMessage() {
		return this.dateOfMessage;
	}

	public String getTextOfMessage() {
		return this.textOfMessage;
	}

	public boolean getIsPresentFile() {
		return this.isPresentFile;
	}

	public String getMimeType() {
		return this.mimeType;
	}

	public DTOUser getAuthor() {
		return this.author;
	}

	public void setAuthor(DTOUser author) {
		this.author = author;
	}

	public DTOUser getReceiver() {
		return this.receiver;
	}

	public void setReceiver(DTOUser receiver) {
		this.receiver = receiver;
	}

	public DTOGroup getGroupReceiver() {
		return this.groupReceiver;
	}

	public void setGroupReceiver(DTOGroup groupReceiver) {
		this.groupReceiver = groupReceiver;
	}
}
