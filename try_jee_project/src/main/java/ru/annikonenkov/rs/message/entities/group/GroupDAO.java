package ru.annikonenkov.rs.message.entities.group;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.logging.Logger;

import ru.annikonenkov.rs.message.entities.user.User;
import ru.annikonenkov.rs.message.entities.user.UserDAO;

@Stateless
public class GroupDAO {

	@EJB
	private UserDAO userDAO;

	@Inject
	Logger logger;

	@PersistenceContext(unitName = "DataSourceEx")
	private EntityManager em;

	public Group getGroupById(int id) {
		Group gr = em.find(Group.class, id);
		return gr;
	}

	//Самое интересное, что будет извлечен только один пользователь, с указанным id - это будет видно при обходе по групп. 
	public List<Group> getAllGroupsForUserId(Integer idOfUser, boolean isDeleted) {
		Query query = em.createQuery(
				"SELECT g FROM Group g JOIN FETCH g.usersSet u where u.id = :id AND g.isDeleted = :isDeleted");
		query.setParameter("id", idOfUser);
		query.setParameter("isDeleted", isDeleted);
		@SuppressWarnings("unchecked")
		List<Group> groupList = query.getResultList();
		return groupList;
	}

	public Group addNewGroup(String nameOfGroup) {
		Date dateOfCreateGroup = new Date();
		Group group = new Group();
		group.setName(nameOfGroup);
		group.setCreateDate(dateOfCreateGroup);
		em.persist(group);
		return group;
	}

	public void saveNewGroup(Group group) {
		em.persist(group);
	}

//Как себя поведет, если мы сделаем merge в этой же транзакции?
//Т.е. когда запрашивающий мето тоже транзакционный. 
	public void saveExistGroup(Group group) {
		em.merge(group);
	}

	/**
	 * Обновление в базе измененных групп.
	 * 
	 * @param groups
	 */
	public void saveExistsGroups(Set<Group> groups) {
		groups.forEach(groupItem -> em.merge(groupItem));
	}

	/**
	 * Удаление группы. <br>
	 * Физического удаления группы не происходит. Просходит пометка, что группа
	 * удалена.
	 * 
	 * @param group
	 */
	public void deleteGroup(Group group) {
		group.setDeleted(true);
		em.merge(group);
	}

	public void deleteGroup(Set<Group> groups) {
		groups.forEach(groupItem -> {
			groupItem.setDeleted(true);
			em.merge(groupItem);
		});
	}

	public boolean deleteUserFromGroup(int userId, int groupId) {
		Group grFromDB = getGroupById(groupId);
		User userFromBD = userDAO.getUserById(userId);
		if (grFromDB == null || userFromBD == null) {
			String forLogging = String.format(
					"Не будет удален пользователь из группы, одна из сущностей не найдена grFromDB = %s ---> userFromBD = %s",
					grFromDB, userFromBD);
			logger.warn(forLogging);
			return false;
		}
		grFromDB.removeUser(userFromBD);
		return true;
	}

	public boolean addUserToGroup(int userId, int groupId) {
		Group grFromDB = getGroupById(groupId);
		User userFromBD = userDAO.getUserById(userId);
		if (grFromDB == null || userFromBD == null) {
			String forLogging = String.format(
					"Не будет добавлен пользователь в группу, одна из сущностей не найдена grFromDB = %s ---> userFromBD = %s",
					grFromDB, userFromBD);
			logger.warn(forLogging);
			return false;
		}
		grFromDB.addUser(userFromBD);
		return true;
	}

//--------------------------------------Поиск--------------------------	
	public List<Group> getAllGroups(boolean isDeleted) {
		Query query = em.createQuery("SELECT g FROM Group g  WHERE g.isDeleted = :isDeleted");
		query.setParameter("isDeleted", isDeleted);
		@SuppressWarnings("unchecked")
		List<Group> groupList = query.getResultList();
		return groupList;
	}

	public List<Group> getTargetGroupsByName(String nameOfGroup, boolean isDeleted) {
		Query query = em.createQuery("SELECT g FROM Group g WHERE g.name LIKE :name AND g.isDeleted = :isDeleted");
		query.setParameter("name", '%' + nameOfGroup + '%');
		query.setParameter("isDeleted", isDeleted);
		@SuppressWarnings("unchecked")
		List<Group> groupList = query.getResultList();
		return groupList;
	}

	/*
	 * groupList.stream().forEach(item -> item.getUsers());
	 * 
	 * Такое простое получение списка пользователей не подходит (т.к. падает ошибка
	 * "failed to lazily initialize") - если попытаться обраться к пользователям
	 * группы, в методах, что находятся вне транзакции. Это и понятно, мы лишь
	 * получаем ссылку на Set
	 * 
	 * 1. Видимо, у элементов этого списка, нужно вызвать методы получения данных,
	 * дабы реально был запрос к данным и хибернэйт обратился к БД.
	 * groupList.stream().forEach(item -> item.getUsers().stream().forEach(itemU ->
	 * itemU.getFullName()));
	 * 
	 * 2. Также JOIN FETCH - позволяет решить эту проблему, при выборке групп, он
	 * выбирает и пользователей, а дальше еще можно указать и для friends и те тоже
	 * будут выбраны.
	 * 
	 * Итого, когда у нас LAZY и мы получаем целевую Entity - нам либо нужно
	 * обратиться к полям этой сущности, для извлечения зависимой сущности, либо
	 * выгребать отдельным запросом и как-то вязать их, либо при запросе в JOIN
	 * добавить FETCH того. что мы хотели бы извлечь.
	 * 
	 * При этом что п.1 что п.2 генерируют одинаковый запрос к БД.
	 * 
	 * В интернете рекамендуют, либо работать в рамка одной транзакции, либо
	 * обратиться к полям дабы вытащить данные.
	 */
	// Нужен для поиска целевой группы, где присутствует пользователь.
	public List<Group> getGroupsByGroupsNameAndUsersId(String nameOfGroup, Integer userId, boolean isDeleted) {
		Query query = em.createQuery(
				"SELECT g FROM Group g JOIN g.usersSet u LEFT JOIN u.friends f WHERE g.name LIKE :gname AND u.id = :id  AND g.isDeleted = :isDeleted");
		query.setParameter("gname", '%' + nameOfGroup + '%');
		query.setParameter("id", userId);
		query.setParameter("isDeleted", isDeleted);
		@SuppressWarnings("unchecked")
		List<Group> groupList = query.getResultList();
		// groupList.stream().forEach(item -> item.getUsers().stream().forEach(itemU ->
		// itemU.getFullName()));
		return groupList;
	}

	/*
	 * Это запрос когда нужно выдернуть с LAZY полями необходимые
	 * сущности/поля(услованая замена EARGER) Query query = em.
	 * createQuery("SELECT g FROM Group g LEFT JOIN FETCH g.usersSet u LEFT JOIN FETCH u.friends f where g.name = :gname and u.id = :id"
	 * ); -----------------------------------------------------------------------
	 * 
	 * Внимание: такая запись возвращает несколько одинаковых групп. Видимо т.к. в
	 * Join будет несколько записей для одной группы И соответствовать количеству
	 * пользователей в группе. Т.е. все как для обычного Join.
	 * 
	 * Query query = em.
	 * createQuery("SELECT g FROM Group g LEFT JOIN g.usersSet u where g.name = :gname"
	 * );
	 */
	public List<Group> getGroupsByGroupsNameAndUsersIdWithRelates(String nameOfGroup, Integer userId,
			boolean isDeleted) {
		Query query = em.createQuery("SELECT g FROM Group g WHERE g.name = :gname  AND g.isDeleted = :isDeleted");
		query.setParameter("gname", nameOfGroup);
		// query.setParameter("id", userId);
		query.setParameter("isDeleted", isDeleted);
		@SuppressWarnings("unchecked")
		List<Group> groupList = query.getResultList();
		return groupList;
	}
//--------------------------------------Поиск--------------------------
}
