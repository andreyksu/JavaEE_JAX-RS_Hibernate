package ru.annikonenkov.jpapack;


import javax.persistence.*;

@Entity
@Table(name = "wife")
public class Wife {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "wife_name")
    private String wifeName;

    @Column(name = "wife_old")
    private int wifeOld;

    // This is called the non-owning side of the relationship т.к.(mappedBy)
    @OneToOne(mappedBy = "wife", optional = false)
    private Person person;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setName(String name) {
        this.wifeName = name;
    }

    public String getName() {
        return this.wifeName;
    }

    public void setOld(int old) {
        this.wifeOld = old;
    }

    public int getOld() {
        return this.wifeOld;
    }

    public void setPerson(Person person){
        this.person = person;
    }

    public Person getPerson(){
        return this.person;
    }

    @Override
    public String toString() {
        return String.format("{\"id\" : \"%d\", \"wifeName\" : \"%s\", \"wifeOld\" : \"%d\"}", id, wifeName, wifeOld);
    }
}
