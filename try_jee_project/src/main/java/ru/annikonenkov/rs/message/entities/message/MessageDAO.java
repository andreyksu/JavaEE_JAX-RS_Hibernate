package ru.annikonenkov.rs.message.entities.message;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import ru.annikonenkov.rs.message.entities.group.Group;
import ru.annikonenkov.rs.message.entities.group.GroupDAO;
import ru.annikonenkov.rs.message.entities.user.User;
import ru.annikonenkov.rs.message.entities.user.UserDAO;
import ru.annikonenkov.rs.message.exception.ExceptionForAddMessage;

@Stateless
public class MessageDAO {

	@EJB
	private GroupDAO groupDAO;

	@EJB
	private UserDAO userDAO;

	@PersistenceContext(unitName = "DataSourceEx")
	private EntityManager em;

	public Message getMessageById(int id) {
		Message message = em.find(Message.class, id);
		return message;
	}

	public List<Message> getAllMessages(boolean isDeleted) {
		Query query = em.createQuery("SELECT m FROM Message m WHERE m.isDeleted = :isDeleted");
		query.setParameter("isDeleted", isDeleted);
		@SuppressWarnings("unchecked")
		List<Message> messageList = query.getResultList();
		return messageList;
	}

	public List<Message> getMessageByReceiverGroupId(Integer gReceiverId, boolean isDeleted) {
		Query query = em.createQuery(
				"SELECT m FROM Message m JOIN FETCH m.groupReceiver g JOIN FETCH m.author a WHERE g.id = :gReceiverId AND m.isDeleted = :isDeleted");
		query.setParameter("gReceiverId", gReceiverId);
		query.setParameter("isDeleted", isDeleted);
		@SuppressWarnings("unchecked")
		List<Message> messageList = query.getResultList();
		return messageList;
	}

	public List<Message> getMessageByAuthorIdAndReceiverId(Integer authorId, Integer rReceiverId, boolean isDeleted) {
		Query query = em.createQuery(
				"SELECT m FROM Message m JOIN FETCH m.author a JOIN FETCH m.receiver r WHERE a.id = :authorId AND r.id = :rReceivedId AND m.isDeleted = :isDeleted");
		query.setParameter("isDeleted", isDeleted);
		query.setParameter("authorId", authorId);
		query.setParameter("rReceivedId", rReceiverId);
		@SuppressWarnings("unchecked")
		List<Message> messageList = query.getResultList();
		return messageList;
	}

	/*
	 * Вот что сказано:
	 * 
	 * If you decide to use a bidirectional mapping, you always need to update both
	 * ends of your association. Otherwise, Hibernate might not persist your change
	 * in the database, and the entities in your current persistence context become
	 * outdated.
	 * 
	 * У меня здесь пропущено обновление author и group. Но все это работает так как именно в message хранится foreignKey и запись для связи в БД осуществляется через поле в message.
	 * Вопрос: где и как и когда они (author и group) должны сохраняться? Не в этом же DAO ведь? Тогда кто об этом должен заботиться? Не endPoind - же? А может и он? Нужен какой-то контроллер?
	 */
	public int addNewMessageToGroup(String textOfMessage, int authorId, int groupReceiverId) throws ExceptionForAddMessage {
		User author = userDAO.getUserById(authorId);
		Group groupReceiver = groupDAO.getGroupById(groupReceiverId);

		if (author == null || groupReceiver == null) {
			String message = String.format("Целевая группа или пользователь не найден isAuthorNull = %b isReceiverNull = %b", author == null, groupReceiver == null);	
			throw new ExceptionForAddMessage(message);
		}
		
		Date dateOfMessage = new Date();
		Message message = new Message();
		message.setDateOfMessage(dateOfMessage);
		message.setTextOfMessage(textOfMessage);
		message.setAuthor(author);
		message.setGroupReceiver(groupReceiver);
		em.persist(message);
		
		int idOfMessage = message.getId();
		return idOfMessage;
	}
	
	public int addNewMessageToGroupWithFile(String textOfMessage, int authorId, int groupReceiverId, byte[] file,
			String mediaType) throws ExceptionForAddMessage {
		User author = userDAO.getUserById(authorId);
		Group groupReceiver = groupDAO.getGroupById(groupReceiverId);

		if (author == null || groupReceiver == null) {
			String message = String.format("Целевая группа или пользователь не найден isAuthorNull = %b isReceiverNull = %b", author == null, groupReceiver == null);	
			throw new ExceptionForAddMessage(message);
		}

		Date dateOfMessage = new Date();
		Message message = new Message();
		message.setDateOfMessage(dateOfMessage);
		message.setTextOfMessage(textOfMessage);
		message.setAuthor(author);
		message.setGroupReceiver(groupReceiver);
		message.setFile(file);
		message.setMimeType(mediaType);
		em.persist(message);
		
		int idOfMessage = message.getId();
		return idOfMessage;
	}

	public int addNewMessage(String textOfMessage, int authorId, int userReceiverId) throws ExceptionForAddMessage {
		User author = userDAO.getUserById(authorId);
		User receiver = userDAO.getUserById(userReceiverId);

		if (author == null || receiver == null) {
			String message = String.format(
					"Целевая группа или пользователь не найден isAuthorNull = %b isReceiverNull = %b", author == null,
					receiver == null);
			throw new ExceptionForAddMessage(message);
		}

		Date dateOfMessage = new Date();
		Message message = new Message();
		message.setDateOfMessage(dateOfMessage);
		message.setTextOfMessage(textOfMessage);
		message.setAuthor(author);
		message.setReceiver(receiver);
		em.persist(message);
		int idOfMessage = message.getId();
		return idOfMessage;
	}

	public int addNewMessageToUserWithFile(String textOfMessage, int authorId, int userReceiverId, byte[] file,
			String mediaType) throws ExceptionForAddMessage {
		User author = userDAO.getUserById(authorId);
		User receiver = userDAO.getUserById(userReceiverId);

		if (author == null || receiver == null) {
			String message = String.format(
					"Целевая группа или пользователь не найден isAuthorNull = %b isReceiverNull = %b", author == null,
					receiver == null);
			throw new ExceptionForAddMessage(message);
		}

		Date dateOfMessage = new Date();
		Message message = new Message();
		message.setDateOfMessage(dateOfMessage);
		message.setTextOfMessage(textOfMessage);
		message.setAuthor(author);
		message.setReceiver(receiver);
		message.setFile(file);
		message.setMimeType(mediaType);
		em.persist(message);
		int idOfMessage = message.getId();
		return idOfMessage;
	}

	/**
	 * Сохраняет новое сообщение в БД.
	 * 
	 * @param message
	 */
	public void saveNewMessage(Message message) {
		em.persist(message);
	}

	/**
	 * Обновляет состояние целевого осообщения.
	 * 
	 * @param message - целевое сообщение, что подлежит обновлению.
	 */
	public void saveExistMessage(Message message) {
		em.merge(message);
	}

	public void saveExistMessages(Set<Message> messages) {
		messages.forEach(messageItem -> em.merge(messageItem));
	}

	/**
	 * Удаление сообщения. <br>
	 * Физического удаления сообщения не будет. Происходит лишь пометка на
	 * удаление.<br>
	 * <br>
	 * 
	 * @param message - целевое сообщение, что подлежит удалению.
	 */
	public void deleteMessage(Message message) {
		message.setIsDeleted(true);
		em.merge(message);
	}

	public void deleteMessage(Set<Message> messages) {
		messages.forEach(messageItem -> {
			messageItem.setIsDeleted(true);
			em.merge(messageItem);
		});
	}

}
