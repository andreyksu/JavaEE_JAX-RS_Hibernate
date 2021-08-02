package ru.annikonenkov.jpapack;

import javax.persistence.*;

@Entity
@Table(name = "person")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "person_name", nullable = true, insertable = true, updatable = true, length = 200)
    private String personName;

    @Column(name = "person_old", nullable = false, insertable = true, updatable = true, length = 200)
    private int personOld;

/*	@Column(name = "wife_id", unique = true)
	private int wifeId; //Работает без этого. Видимо ID берет из поля wife, когда сетим.
*/

    //This is called the owning side of the relationship
    //name = "wife_id" must be the foreign key on the "wife.id"
    @OneToOne(cascade = CascadeType.ALL, optional = true)
    //под вопросом нужно ли это здесь cascade = CascadeType.ALL - при удалении Person, Wife вероятно может остаться.
    @JoinColumn(name = "wife_id", referencedColumnName = "id") //Один из 3х видов связи OneToOne.
    private Wife wife;

    //This is called the owning side of the relationship
    @ManyToOne()
    @JoinColumn(name = "mother_id", nullable = false)
    private Mother mother;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setName(String name) {
        this.personName = name;
    }

    public String getName() {
        return this.personName;
    }

    public void setOld(int old) {
        this.personOld = old;
    }

    public int getOld() {
        return this.personOld;
    }


    public void setWife(Wife wife) {
        wife.setPerson(this);
        this.wife = wife;
    }

    public Wife getWife() {
        return this.wife;
    }

    public void setMother(Mother mother) {
        mother.addPerson(this);
        this.mother = mother;
    }

    public Mother getMother() {
        return this.mother;
    }

    @Override
    public String toString() {
        return String.format("{\"id\" : \"%d\", \"personName\" : \"%s\", \"personOld\" : \"%d\"}", id, personName, personOld);
    }

    public String toStringFullWithWife() {
        return String.format("{\"id\" : \"%d\", \"personName\" : \"%s\", \"personOld\" : \"%d\", \"wife\" : %s}", id, personName, personOld, wife.toString());
    }

    public String toStringFullWithMother() {
        return String.format("{\"id\" : \"%d\", \"personName\" : \"%s\", \"personOld\" : \"%d\", \"mother\" : %s}", id, personName, personOld, mother.toString());
    }

    public String toStringFullRelates() {
        return String.format("{\"id\" : \"%d\", \"personName\" : \"%s\", \"personOld\" : \"%d\", \"wife\" : %s, \"mother\" : %s}", id, personName, personOld, wife.toString(), mother.toString());
    }

}
