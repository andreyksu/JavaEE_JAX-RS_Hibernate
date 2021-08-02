package ru.annikonenkov.ejbpack.info;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.AfterBegin;
import javax.ejb.AfterCompletion;
import javax.ejb.BeforeCompletion;
import javax.ejb.EJBContext;
import javax.ejb.Remove;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

@Stateless
public class Info implements IInfo {

    /**
     * Аналогично классу Calc.<br>
     *
     * @Stateless - бины не должны иметь полей класса/экземпляра.
     */
    private List<String> postConstruct;
    private List<String> constructor;

    public Info() {
        constructor = new ArrayList<String>();
    }

    @Resource
    EJBContext securityContext;

    @Resource
    SessionContext sessionContext;

    @Resource
    EJBContext ejbContext;

    @Override
    public String getInfoEJBContext() {
        String callerPrincipal = ejbContext.getCallerPrincipal().getName();
        String mapOfContextData = ejbContext.getContextData().toString();
        String resulStr = String.format("\n-----\n getCallerPrincipal() = %s\n getContextData() = %s\n",
                callerPrincipal, mapOfContextData);
        return resulStr;
    }

    @Override
    public String getInfoSecurityContext() {
        // String userTransaction = securityContext.getUserTransaction().toString();
        String mapOfContextData = securityContext.getContextData().toString();
        String resulStr = String.format("\n-----\n getContextData() = %s\n", mapOfContextData);
        return resulStr;
    }

    @Override
    public String getInfoSessionContext() {
        String callerPrincipal = sessionContext.getCallerPrincipal().getName();
        String mapOfContextData = sessionContext.getContextData().toString();
        String invokedBusinessInterface = sessionContext.getInvokedBusinessInterface().toString();
        // String messageContext = sessionContext.getMessageContext().toString();
        // String userTransaction = sessionContext.getUserTransaction().toString();

        constructor.add("constructor_Info");
        postConstruct.add("postConstruct_Info");

        String resulStr = String.format(
                "\n-----\n getCallerPrincipal() = %s\n getContextData() = %s\n getInvokedBusinessInterface() = %s\n  postConstruct = %s\n constructor = %s\n",
                callerPrincipal, mapOfContextData, invokedBusinessInterface, postConstruct.toString(),
                constructor.toString());
        return resulStr;
    }

    @PostConstruct
    public void init() {
        postConstruct = new ArrayList<String>();
        System.out.println("Info: @PostConstruct : init");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("Info: @PreDestroy : destroy");
    }

    /**
     * Аннотация используется только для stateful
     */
    @Remove
    public void checkOut() {
        System.out.println("Info: @Remove : checkOut");
    }

    /**
     * Аннотация используется только для stateful
     */
    @AfterBegin
    private void afterBegin() {
        System.out.println("Info: @AfterBegin : afterBegin");
    }

    /**
     * Аннотация используется только для stateful
     */
    @BeforeCompletion
    private void beforeCompletion() {
        System.out.println("Info: @BeforeCompletion : beforeCompletion");
    }

    /**
     * Аннотация используется только для stateful
     */
    @AfterCompletion
    private void afterCompletion(boolean committed) {
        System.out.println("Info: @AfterCompletion : afterCompletion");
    }

}
