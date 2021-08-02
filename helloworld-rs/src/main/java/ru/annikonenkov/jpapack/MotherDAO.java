package ru.annikonenkov.jpapack;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class MotherDAO {
    @PersistenceContext(unitName = "DataSourceEx")
    private EntityManager em;

    public Mother getById(int id) {
        Mother m = em.find(Mother.class, id);
        return m;
    }

    public List<Mother> findAny() {
        Query query = em.createQuery("SELECT m FROM Mother m");
        List<Mother> list = query.getResultList();
        return list;
    }

    public String findAnyAsStr() { //Если это использовать здесь, то с Lazy ошибок не возникает. Так как JTA транзацкция в этом методе еще не завершена. И можно обращаться к полям.
        List<Mother> listOfTohers = this.findAny();
        String result = "[";
        int count = 0;
        for (Mother i : listOfTohers) {
            if (count > 0) {
                result += ",";
            }
            result += i.toStringWithPerson();
            count++;
        }
        result += "]";
        return result;
    }
}
