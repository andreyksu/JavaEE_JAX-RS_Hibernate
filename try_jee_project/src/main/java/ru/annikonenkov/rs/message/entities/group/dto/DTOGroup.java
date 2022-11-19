package ru.annikonenkov.rs.message.entities.group.dto;

import java.util.Date;

public class DTOGroup {

	private int id;
	private String name;
	private boolean isActive;
	private boolean isDeleted;
	private Date createDate;

	public DTOGroup(int id, String name, boolean isActive, boolean isDeleted, Date createDate) {
		this.id = id;
		this.name = name;
		this.isActive = isActive;
		this.isDeleted = isDeleted;
		this.createDate = createDate;
	}
	
	public DTOGroup() {
		this.id = 0;
		this.name = null;
		this.isActive = false;
		this.isDeleted = true;
		this.createDate = null;
		//this.createDate = Date.from(LocalDate.ofEpochDay(0).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public boolean getIsActive() {
		return isActive;
	}

	public boolean getIsDeleted() {
		return isDeleted;
	}

	public Date getCreateDate() {
		return createDate;
	}

}
