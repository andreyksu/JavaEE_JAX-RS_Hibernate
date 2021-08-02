package ru.annikonenkov.jpapack;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class PersonDAO {
    @PersistenceContext(unitName = "DataSourceEx")
    private EntityManager em;

    public void put(String name, int old) {

        Mother mother = new Mother();
        mother.setName("Mother_M_1");
        mother.setOld(77);
        em.persist(mother);

        Wife wife = new Wife();
        wife.setName("Olga " + name);
        wife.setOld(old - 3);
        em.persist(wife);

        Person pers = new Person();
        pers.setName(name);
        pers.setOld(old);
        pers.setWife(wife);
        pers.setMother(mother);
        em.persist(pers);
    }

    public List<Person> findAny() {
        Query query = em.createQuery("SELECT p FROM Person p");
        List<Person> list = query.getResultList();
        return list;
    }

    public List<Person> findByName(String name) {
        Query query = em.createQuery("SELECT p FROM Person p where p.personName = :personeName");
        query.setParameter("personeName", name);
        List<Person> list = query.getResultList();
        return list;
    }

    public Person findById(int id) {
        Person p = em.find(Person.class, id);
        return p;
    }

}
