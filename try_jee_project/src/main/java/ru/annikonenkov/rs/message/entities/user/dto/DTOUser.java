package ru.annikonenkov.rs.message.entities.user.dto;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import ru.annikonenkov.rs.message.entities.group.dto.DTOGroup;

public class DTOUser {
	private int id;
	private String name;
	private String fullName;
	private String email;
	private boolean isDeleted;
	private boolean isBanned;
	private Date regDate;
	private Date birthDay;
	private List<DTOUser> friends;
	private List<DTOGroup> groups;

	public DTOUser(int id, String name, String fullName, String email, boolean isDeleted, boolean isBanned,	Date regDate, Date birthDay) {

		this.id = id;
		this.name = name;
		this.fullName = fullName;
		this.email = email;
		this.isDeleted = isDeleted;
		this.isBanned = isBanned;
		this.regDate = regDate;
		this.birthDay = birthDay;
	}
	
	public DTOUser() {
		this.id = 0;
		this.name = null;
		this.fullName = null;
		this.email = null;
		this.isDeleted = true;
		this.isBanned = true;
		this.regDate = null;
		this.birthDay = null;
		
	}

	public int getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getFullName() {
		return this.fullName;
	}

	public String getEmail() {
		return this.email;
	}

	public boolean getIsDeleted() {
		return this.isDeleted;
	}

	public boolean getIsBanned() {
		return this.isBanned;
	}

	public Date getRegDate() {
		return this.regDate;
	}

	public Date getBirthDay() {
		return this.birthDay;
	}

	public List<DTOUser> getFriends() {
		return this.friends;
	}

	public List<DTOGroup> getGroups() {
		return this.groups;
	}

	public void setGroups(List<DTOGroup> groups) {
		this.groups = groups;
	}

	public void setFriends(List<DTOUser> friends) {
		this.friends = friends;
	}

}
