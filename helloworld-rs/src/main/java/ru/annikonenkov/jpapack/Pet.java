package ru.annikonenkov.jpapack;

import javax.persistence.*;

@Entity
@Table(name = "pet")
public class Pet {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "pets_name")
	private String petsName;

	@Column(name = "pets_old")
	private int petsOld;

	@Column(name = "person_id")
	private int personId;

	/*@ManyToMany
	private Person person;*/

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public void setName(String name) {
		this.petsName = name;
	}

	public String getName() {
		return this.petsName;
	}

	public void setOld(int old) {
		this.petsOld = old;
	}

	public int getOld() {
		return this.petsOld;
	}

	public void setPerson(int personId) {
		this.personId = personId;
	}

	public int getPerson() {
		return this.personId;
	}

	@Override
	public String toString() {
		return String.format("Pet {id = %d,   petsName = %s,   pets_old = %d}", id, petsName, petsOld);
	}

}
