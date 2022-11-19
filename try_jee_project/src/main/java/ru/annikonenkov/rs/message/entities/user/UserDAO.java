package ru.annikonenkov.rs.message.entities.user;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class UserDAO {
	@PersistenceContext(unitName = "DataSourceEx")
	private EntityManager em;

	/**
	 * Получение пользователя по его ID.
	 */
	public User getUserById(int id) {
		User user = em.find(User.class, id);
		return user;
	}

	/**
	 * Получение списка пользователей по ID группы.
	 */
	public List<User> getUsersByGroupID(Integer groupId, boolean isDeleted) {
		Query query = em.createQuery(
				"SELECT u FROM User u JOIN u.groupsSetWhereUserIsMember g WHERE g.id = :id AND u.isDeleted = :isDeleted");
		query.setParameter("id", groupId);
		query.setParameter("isDeleted", isDeleted);
		@SuppressWarnings("unchecked")
		List<User> userList = query.getResultList();
		return userList;
	}

	/**
	 * Получение списка друзей(пользователей) по ID целевого пользователя.
	 * 
	 * @param userId
	 * @return
	 */
	public List<User> getFriendsOfUserWithUserId(Integer userId, boolean isDeleted) {
		Query query = em.createQuery("SELECT u FROM User u JOIN FETCH u.partners p WHERE p.id = :id AND u.isDeleted = :isDeleted");
		query.setParameter("id", userId);
		query.setParameter("isDeleted", isDeleted);
		@SuppressWarnings("unchecked")
		List<User> userList = query.getResultList();
		return userList;
	}

	/**
	 * Создание/добавление нового пользователя. <br>
	 * 
	 * @param nameOfUser
	 * @param fullName
	 * @param email
	 * @return
	 */
	public User addNewUser(String nameOfUser, String fullName, String email) {
		Date regDate = new Date();
		Date birthDate = new Date();
		User user = new User(nameOfUser, fullName, email, regDate, birthDate);
		em.persist(user);
		return user;
	}

	public void saveNewUser(User user) {// Здесь нужно подумать. Предварительно выбрать пользователей по Email вертунть
										// запрет или же обработать исключение при вставка. Т.к. email уникальное должно
										// быть.
		em.persist(user);
	}

	public void saveExistUser(User user) {
		em.merge(user);
	}
	
	public boolean addFriendToUser(int userId, int friendId) {
		User user = em.find(User.class, userId);
		User friend = em.find(User.class, friendId);		
		if (user == null || friend == null)
			return false;
		user.addFriend(friend);//Нужно обратить внимание, что удаляется с 2х сторон.
		friend.addFriend(user);
		return true;
	}

	public boolean deleteFriendToUser(int userId, int friendId) {
		User user = em.find(User.class, userId);
		User friend = em.find(User.class, friendId);
		if (user == null || friend == null)
			return false;
		user.deleteFriend(friend);//Нужно обратить внимание, что удаляется с 2х сторон.
		friend.deleteFriend(user);
		return true;
	}

	public boolean deleteUser(int userId) {
		User user = getUserById(userId);
		if (user == null)
			return false;
		user.setIsDeleted(true);
		em.merge(user);
		return true;
	}

	public void deleteUsers(Set<User> users) {
		users.forEach(currentUser -> {
			currentUser.setIsDeleted(true);
			em.merge(currentUser);
		});
	}

	// --------------------------------------Поиск--------------------------
	public List<User> getAllUsers(boolean isDeleted) {
		Query query = em.createQuery("SELECT u FROM User u WHERE u.isDeleted = :isDeleted");
		query.setParameter("isDeleted", isDeleted);
		@SuppressWarnings("unchecked")
		List<User> userList = query.getResultList();
		return userList;
	}

	/**
	 * Метод используется для поиска.
	 */
	public List<User> getUsersByMaskOfNameViaNamedQuery(String userName, boolean isDeleted) {
		Query query = em.createNamedQuery("findAllUsersWithMask");
		String str = '%' + userName + '%';
		query.setParameter("mask", str);
		query.setParameter("isDeleted", isDeleted);
		@SuppressWarnings("unchecked")
		List<User> userList = query.getResultList();
		return userList;
	}
}
