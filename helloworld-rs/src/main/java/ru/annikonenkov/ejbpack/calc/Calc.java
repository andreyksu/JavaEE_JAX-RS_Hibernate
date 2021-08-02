package ru.annikonenkov.ejbpack.calc;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.AfterBegin;
import javax.ejb.AfterCompletion;
import javax.ejb.BeforeCompletion;
import javax.ejb.EJB;
import javax.ejb.Remove;
import javax.ejb.Stateless;

import ru.annikonenkov.ejbpack.info.IInfo;

@Stateless
public class Calc implements ICalck {

    /**
     * Вот что пишует в инетах.<br>
     * Note the @Stateless annotation on the class declaration. It denotes that this
     * bean is a stateless session bean. This kind of bean does not have any
     * associated client state, but it may preserve its instance state and is
     * normally used to do independent operations.
     */

    /**
     * Нужно обратить внимание, что переменные инстенса бина, сохраняются между
     * вызовами.<br>
     * <br>
     * Вообще, со слов опытных разработчиков, полей класса не должно быть
     * для @Stateless бинов. Не важно public или private поля, их не должно
     * быть.<br>
     * <br>
     * Исключением может быть то, что инжектится/поставляется контейнером.
     */
    private List<String> postConstruct;// Инициализируется в методе с @PostConstruct
    private List<String> constructor;// Инициализируется в конструкторе.

    public Calc() {
        constructor = new ArrayList<String>();
    }

    @EJB
    private IInfo info;

    @Override
    public int summ(int i, int j) {
        return i + j;
    }

    @Override
    public int difference(int i, int j) {
        int result = i > j ? i - j : j - i;
        return result;
    }

    @Override
    public String getFullInfo() {
        constructor.add("constructor_Calc");
        postConstruct.add("postConstruct_Calc");
        return String.format(
                "info.getInfoSecurityContext() = %s\n\n\n info.getInfoSessionContext() = %s\n postConstruct = %s\n constructor = %s\n",
                info.getInfoSecurityContext(), info.getInfoSessionContext(), postConstruct.toString(),
                constructor.toString());
    }

    @PostConstruct
    public void init() {
        postConstruct = new ArrayList<String>();
        System.out.println("Calc: @PostConstruct : init");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("Calc: @PreDestroy : destroy");
    }

    /**
     * Аннотация используется только для stateful
     */
    @Remove
    public void checkOut() {
        System.out.println("Calc: @Remove : checkOut");
    }

    /**
     * Аннотация используется только для stateful
     */
    @AfterBegin
    private void afterBegin() {
        System.out.println("Calc: @AfterBegin : afterBegin");
    }

    /**
     * Аннотация используется только для stateful
     */
    @BeforeCompletion
    private void beforeCompletion() {
        System.out.println("Calc: @BeforeCompletion : beforeCompletion");
    }

    /**
     * Аннотация используется только для stateful
     */
    @AfterCompletion
    private void afterCompletion(boolean committed) {
        System.out.println("Calc: @AfterCompletion : afterCompletion");
    }

}
