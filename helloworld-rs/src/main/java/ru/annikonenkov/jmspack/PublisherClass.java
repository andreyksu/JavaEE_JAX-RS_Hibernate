package ru.annikonenkov.jmspack;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;

import javax.inject.Inject;

import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.MessageProducer;

@ApplicationScoped
public class PublisherClass {
    @Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(mappedName = "java:/jms/queue/ForTest")
    private Queue queue;

    @Inject
    private JMSContext context;

    public void sendMessage(String txt) {
        context.createProducer().send(queue, txt);

		/*
		try {
			Connection connection = connectionFactory.createConnection();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer messageProducer = session.createProducer(queue);
			connection.start();
			TextMessage textMessage = session.createTextMessage();
			textMessage.setText(txt);
			messageProducer.send(textMessage);
			connection.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
		*/
    }

}
