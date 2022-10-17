package ru.annikonenkov.rs.message.entities.user;

import java.util.Date;

public class DTOUser {
	private int id;
	private String name;
	private String fullName;
	private String email;
	private boolean isDeleted;
	private boolean isBanned;
	private Date regDate;
	private Date birthDay;

	public DTOUser(int id, String name, String fullName, String email, boolean isDeleted, boolean isBanned,
			Date regDate, Date birthDay) {

		this.id = id;
		this.name = name;
		this.fullName = fullName;
		this.email = email;
		this.isDeleted = isDeleted;
		this.isBanned = isBanned;
		this.regDate = regDate;
		this.birthDay = birthDay;
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

}
