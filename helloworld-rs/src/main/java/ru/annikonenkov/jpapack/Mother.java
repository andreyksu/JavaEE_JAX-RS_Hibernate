package ru.annikonenkov.jpapack;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "mother")
public class Mother {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "mother_name")
    private String motherName;

    @Column(name = "mother_old")
    private int motherOld;

    // This is called the non-owning side of the relationship т.к.(mappedBy)
    @OneToMany(mappedBy = "mother", fetch = FetchType.LAZY)
    //@Basic(fetch = FetchType.LAZY) - еще есть такая аннотация.
    //Возникает ошибка если не поставить EAGER. Есть предположине, что причина в использовании этой переменной после окончания транзацкции/сессии.
    //Согласно стратегии FetchType.LAZY связанные объекты загружаются только по мере необходимости, т.е. при обращении. Но при этом требуется, чтобы соединение с базой (или транзакция) сохранялись. Если быть точно, то требуется, чтобы объект был attached. Поэтому для работы с lazy объектами тратится больше ресурсов на поддержку соединений.
    //FetchType.EAGER — загружать коллекцию дочерних объектов вместе с загрузкой родительских объектов;
    //FetchType.LAZY — загружать коллекцию дочерних объектов при первом обращении к ней (вызове метода get) — это так называемая отложенная загрузка.

    //Действительно так и было, как только обращение к методу вынес из JAX-RS области, и пернес в методы EJB - так ошибка сразу ушла с LAZY. Так как при работе в методе EJB транзацкция JTA еще на закрыта - и вероятно и сессия к БД не знакрыта.
    //Оставил вариант с работой fetch = FetchType.LAZY

    private Set<Person> persons = new HashSet<>();

    //Вообще важно, что class Mother не обязательно должно содержать все поля таблицы соответствующей. В таблице может быть бьльше полей, чем объявленно в классе.

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setName(String name) {
        this.motherName = name;
    }

    public String getName() {
        return this.motherName;
    }

    public void setOld(int old) {
        this.motherOld = old;
    }

    public int getOld() {
        return this.motherOld;
    }

    public void addPerson(Person person) {
        this.persons.add(person);
    }

    public void setPersons(Set<Person> personSet) {
        this.persons = personSet;
    }

    public Set<Person> getPersons() {
        return this.persons;
    }

    @Override
    public String toString() {
        return String.format("{\"id\" : \"%d\", \"motherName\" : \"%s\", \"motherOld\" : \"%d\"}", id, motherName, motherOld);
    }

    public String toStringWithPerson() {
        String result = "[";
        int count = 0;
        for (Person i : this.persons) {
            if (count > 0) {
                result += ",";
            }
            result += i.toString();
            count++;
        }
        result += "]";
        return String.format("{\"id\" : \"%d\", \"motherName\" : \"%s\", \"motherOld\" : \"%d\", \"persons\" : %s}", id, motherName, motherOld, result);
    }

}
