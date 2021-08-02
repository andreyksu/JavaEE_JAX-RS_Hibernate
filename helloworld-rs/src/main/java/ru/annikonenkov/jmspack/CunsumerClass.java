package ru.annikonenkov.jmspack;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;

@MessageDriven(name = "Receiver", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:/jms/queue/ForTest"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")})

public class CunsumerClass implements MessageListener {

    private Logger LOG = Logger.getLogger(CunsumerClass.class.getName());

    @Override
    public void onMessage(Message rcvMessage) {
        try {
            String recivedMessage = rcvMessage.getBody(String.class);
            LOG.info(recivedMessage);
        } catch (JMSException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

}
