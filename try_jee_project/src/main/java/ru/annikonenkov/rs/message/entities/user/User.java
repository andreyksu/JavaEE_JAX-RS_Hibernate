package ru.annikonenkov.rs.message.entities.user;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import ru.annikonenkov.rs.message.entities.group.Group;
import ru.annikonenkov.rs.message.entities.message.Message;

@Entity
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(name = "uniqEmailColumn", columnNames = { "email" }) })
@NamedQuery(name = "findAllUsersWithMask", query = "SELECT u FROM User u WHERE u.name LIKE :mask AND u.isDeleted = :isDeleted")
public class User {
	@Id
	@SequenceGenerator(name = "user_geneartor", sequenceName = "suid_grenerator", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_geneartor")
	@Column(name = "suid")
	private int id;

	@Column(name = "name", nullable = false, length = 500)
	private String name;

	@Column(name = "full_name")
	private String fullName;

	/*
	 * @Column(name = "email", nullable = false, length = 500, unique = true)
	 * 
	 * uniqueConstraints ={@UniqueConstraint(name = "uniqEmailColumn", columnNames =
	 * {"email"})} И unique = true полностью идентичны в рамках того, что будет
	 * сгенерированнов в БД.
	 * 
	 * Но в первом случае можно задать наименование.
	 */

	@Column(name = "email", nullable = false, length = 500)
	private String email;

	@Column(name = "is_deleted", nullable = false, columnDefinition = "boolean default false")
	private boolean isDeleted = false;

	@Column(name = "is_banned", nullable = false, columnDefinition = "boolean default false")
	private boolean isBanned = false;

	@Column(name = "reg_date", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date regDate;

	@Column(name = "birth_day", nullable = false)
	@Temporal(TemporalType.DATE)
	private Date birthDay;

	@Column(name = "profile_pict")
	@Lob
	private byte[] profilePict;

	/*
	 * Эта сущность является ведомой т.к. обладает mappedBy. Где значение usersSet -
	 * это наименование поля в сущности Group - т.е. mappedBy является указателем,
	 * каким полем мы связываемся. Владельцем является тот, кто хранит FK т.е. в
	 * нашем случае владелец связи Group.
	 * 
	 * Но не очень понятен принцип владельца в контексте ManyToMany - так как это
	 * поле уже не содержит FK, как например с аннотацией @JoinColumn - а содержится
	 * связь в третей связывающей таблице (и FK содержит именно эта треться
	 * стаблица)
	 * 
	 * И здесь важно, при удалении сущности "User" - в связывающей таблице не будет
	 * удалена строка а из за FK это приведет к ошибке удаления. Для избажения этого
	 * нужно сделать User владельцем связи - тогда при удалении User будет удалена
	 * строка в связывающей таблице но при этом не будет такой возмжности для Group.
	 * Т.е удаление в связывающей таблице происхоидт только при удалении владеющей
	 * сущености.
	 * 
	 * Для ManyToMany - нужно использовать только Set При испольовании List - работа
	 * неэффективна, при удалении, предварительно из кросс таблицы удалится все, а
	 * потом вставятся остатки.
	 * 
	 * Для OneToMany - еще позволительно использовать List. А вот для ManyToMany -
	 * не позволительно и там нужно использовать Set - объясняется
	 * производительностью.
	 */
	@ManyToMany(mappedBy = "usersSet", fetch = FetchType.LAZY) // владеемая сторона
	private Set<Group> groupsSetWhereUserIsMember = new HashSet<>();

	/*
	 * Правильный подход - когда все свзяи являются LAZY - и когда необходимо уже
	 * непосредственным запросом извлекать данные.
	 */
	@OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
	private Set<Message> messagesWhereUserIsAuthor = new HashSet<>();

	@OneToMany(mappedBy = "receiver", fetch = FetchType.LAZY)
	private Set<Message> messagesWhereUserIsReceiver = new HashSet<>();

	@ManyToMany(fetch = FetchType.LAZY) // владеющая сторона.
	@JoinTable(name = "user_and_friend", joinColumns = @JoinColumn(name = "user_suid", referencedColumnName = "suid", foreignKey = @ForeignKey(name = "fk_cross_table_user_on_friend")), inverseJoinColumns = @JoinColumn(name = "friend_suid", referencedColumnName = "suid", foreignKey = @ForeignKey(name = "fk_cross_table_firend_on_user")))
	private Set<User> friends = new HashSet<>();

	@ManyToMany(mappedBy = "friends", fetch = FetchType.LAZY) // владеемая сторона.
	private Set<User> partners = new HashSet<User>();

	public User() {

	}

	/*
	 * Видимо нужно будет удалить эти конструкторы.
	 * 
	 * В методах set устанавливается 2х стороннаяя связь, а в конструкторе
	 * устанавливается односторонняя связь.
	 * 
	 * Там где приводится примеры кода, там везде создается через пустой
	 * конструктора, а потом через set(...) происходит вставка необходимых данных.
	 * 
	 * Вот что сказано:
	 * 
	 * If you decide to use a bidirectional mapping, you always need to update both
	 * ends of your association. Otherwise, Hibernate might not persist your change
	 * in the database, and the entities in your current persistence context become
	 * outdated.
	 */
	public User(String name, String fullName, String email, Date regDate, Date birthDay) {
		this.name = name;
		this.fullName = fullName;
		this.email = email;
		this.regDate = regDate;
		this.birthDay = birthDay;
	}

	public User(String name, String fullName, String email, Date regDate, Date birthDay, byte[] profilePict) {
		this.name = name;
		this.fullName = fullName;
		this.email = email;
		this.regDate = regDate;
		this.birthDay = birthDay;
		this.profilePict = profilePict;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getFullName() {
		return this.fullName;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return this.email;
	}

	public void setIsDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public boolean getIsDeleted() {
		return this.isDeleted;
	}

	public void setBanned(boolean isBanned) {
		this.isBanned = isBanned;
	}

	public boolean getBanned() {
		return this.isBanned;
	}

	public void setRgeDate(Date regDate) {
		this.regDate = regDate;
	}

	public Date getRegDate() {
		return this.regDate;
	}

	public void setBirthDay(Date birthDay) {
		this.birthDay = birthDay;
	}

	public Date getBirthDay() {
		return this.birthDay;
	}

	public void setProfilePict(byte[] profilePict) {
		this.profilePict = profilePict;
	}

	public byte[] getProfilePict() {
		return this.profilePict;
	}

	public void addGroup(Group group) {
		group.getUsers().add(this);
		this.groupsSetWhereUserIsMember.add(group);
	}

	public void addGroups(Set<Group> groups) {
		groups.forEach(grItem -> grItem.getUsers().add(this));
		this.groupsSetWhereUserIsMember.addAll(groups);
	}

	public Set<Group> getGroupsSetWhereUserIsMember() {
		return this.groupsSetWhereUserIsMember;
	}

	public void addMessageWhereUserIsAuthor(Message message) {
		message.setAuthor(this);
		this.messagesWhereUserIsAuthor.add(message);
	}

	public void addMessagesWhereUserIsAuthor(Set<Message> messages) {
		messages.stream().forEach(currentItem -> currentItem.setAuthor(this));
		this.messagesWhereUserIsAuthor.addAll(messages);
	}

	public Set<Message> getMessagesWhereUserIsAuthor() {
		return this.messagesWhereUserIsAuthor;
	}

	//TODO: Такое допустимо, только из за Set - а так был бы дубль. По сути здесь добавление в коллекцию нужно убрать. Т.к. делается это в setReceiver(...).
	public void addMessageToReceiver(Message message) {
		message.setReceiver(this);
		this.messagesWhereUserIsReceiver.add(message);
	}

	public void addMessagesToReceiver(Set<Message> messages) {
		messages.stream().forEach(currentItem -> currentItem.setReceiver(this));
		this.messagesWhereUserIsReceiver.addAll(messages);
	}

	public Set<Message> getMessagesWhereUserIsReceiver() {
		return this.messagesWhereUserIsReceiver;
	}

	// Здесь выступаем в качестве partner
	public void addFriend(User friend) {
		friend.getPartners().add(this);// берем полученного friend и в его prtners добавляем себя в качестве partner.
		this.friends.add(friend);// добавляем к partner его нового freind.
	}

	public void addFriends(Set<User> friends) {
		friends.forEach(fItem -> fItem.getPartners().add(this));
		this.friends.addAll(friends);
	}

	public Set<User> getFriends() {
		return this.friends;
	}

	public void deleteFriend(User friend) {
		friend.getPartners().remove(this);
		this.friends.remove(friend);
	}

	// Здесь выступаем в качестве friend
	public void addPartner(User partner) {
		partner.getFriends().add(this);// берем полученного partner и в его friends добавляем себя в качестве friend.
		this.partners.add(partner);// добавляем к freind его нового parnter.
	}

	public void addPartners(Set<User> partners) {
		partners.forEach(pItem -> pItem.getFriends().add(this));
		this.partners.addAll(partners);
	}

	public Set<User> getPartners() {
		return this.partners;
	}

	public void deletePartner(User partner) {
		partner.getFriends().remove(this);
		this.partners.remove(partner);
	}

}
