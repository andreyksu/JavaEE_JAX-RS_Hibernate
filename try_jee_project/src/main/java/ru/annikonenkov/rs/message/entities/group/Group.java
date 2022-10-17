package ru.annikonenkov.rs.message.entities.group;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import ru.annikonenkov.rs.message.entities.message.Message;
import ru.annikonenkov.rs.message.entities.user.User;

@Entity
@Table(name = "groups")
public class Group {

	/*
	 * @GeneratedValue(strategy = GenerationType.IDENTITY)
	 * 
	 * Если выбранно "GenerationType.IDENTITY" и генерация схемы производится через
	 * Hibernate - то для каждой таблицы создается своя последовательность.
	 * 
	 * Можно удалить эти индивидуальные последовательности и создать одну
	 * последовательность и в каждой таблице задать для поля Id эту новую
	 * последовательность в качестве значения по умолчанию.
	 * 
	 * Вообще не самая плохая идея остановиться на общей последовательности и
	 * GenerationType.IDENTITY
	 * -----------------------------------------------------------------------------
	 * "strategy = GenerationType.SEQUENCE"
	 * 
	 * allocationSize - значение по умолчанию равно 50. Что это значит? Это значит,
	 * что hibernate резервирует 50 записей и без запроса к базе просто прибавляет
	 * +1 при каждой вставке (без обращения к базе). Дошли до 50 запросили еще раз
	 * итд.
	 * 
	 * Более того, что самое неожиданное, даже если хибернейт делает запрос select
	 * nextval ('suid_grenerator') и ему возвращается 100(пусть мы указали
	 * последовательности считать со 100), НО он начнет считать от 50 - и ему все
	 * равно, что вернула последовательность.
	 * -----------------------------------------------------------------------------
	 * Не помогло, при включение опрций в persistance.xml, он начинает считать со
	 * 101. Нужно изучать отдельно. Пока верунул allocationSize=1
	 * 
	 * properties.put("hibernate.id.new_generator_mappings", "true");
	 * 
	 * <persistence-unit name="testPU"> <properties> <property
	 * name="hibernate.id.new_generator_mappings" value="true" /> </properties>
	 * </persistence-unit>
	 *
	 * -----------------------------------------------------------------------------
	 * allocationSize = 1 - запрос на получение значения последовательности будет
	 * выполняться каждый раз, при insert и тут проблем нет, кроме запроса при
	 * каждом insert (т.е. условная производительность падает).
	 */
	@Id
	@SequenceGenerator(name = "group_geneartor", sequenceName = "suid_grenerator", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "group_geneartor")
	@Column(name = "suid")
	private int id;

	@Column(name = "name", nullable = false, length = 500)
	private String name;

	//Не помню зачем добавлял это поле.
	@Column(name = "is_active", nullable = false, columnDefinition = "boolean default true")
	private boolean isActive = true; // Не уверен, что так делают, может нужно в конструкторе

	@Column(name = "is_deleted", nullable = false, columnDefinition = "boolean default false")
	private boolean isDeleted = false; // Не уверен, что так делают, может нужно в конструкторе

	@Column(name = "create_date", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;

	/*
	 * Эта сущность является владеющий стороной. Вообще, советуют делать не
	 * ManyToMany - а через третью Сущность, которая будет связывать две сущности и
	 * в ней будет связь через OneToMany на каждую их этих 2х сущностей.
	 * 
	 * При желаниии такой связывающей сущности можно добавить дополнительные поля. В
	 * данном случае не удалять связь а ставить ей маркер, удалена. Можно еще и дату
	 * добавить (добавления связи и удаления вязи итд)
	 */
	@ManyToMany(fetch = FetchType.LAZY)
	// @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE,
	// CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinTable(name = "user_and_group", joinColumns = @JoinColumn(name = "group_suid", referencedColumnName = "suid", foreignKey = @ForeignKey(name = "fk_cross_table_on_group")), inverseJoinColumns = @JoinColumn(name = "user_suid", referencedColumnName = "suid", foreignKey = @ForeignKey(name = "fk_cross_table_on_user")))
	//@OrderBy("name ASC")
	@Fetch(FetchMode.SUBSELECT)
	private Set<User> usersSet = new HashSet<>();

	/*
	 * In a One-to-Many/Many-to-One relationship, the owning side is usually defined
	 * on the many side of the relationship. По этой причине эту сторону помечаем
	 * как сторону которой владеют. И у Гансалвеса написано что mappedBy нельзя
	 * использовать в ManyToOne, что увязывается с вышеприведенной рекамендацией.
	 * 
	 * ------------------------------------------------------------------------
	 * 
	 * @Fetch(FetchMode.SUBSELECT) Так достает одним запросом. Сам запрос похож, но
	 * в конце идет where group_suid in (select suid from groups)
	 * 
	 * @Fetch(FetchMode.JOIN) С этой аннотацией, для каждой группы делает ОТДЕЛЬНЫЙ
	 * запрос для поиска сообщений, где у сообщения group_suid равен текущей группе.
	 * Т.е. если 10.000 то 10.000 отдельных запросов.
	 * 
	 * @Fetch(FetchMode.SELECT) //По запросам идентично FetchMode.JOIN.
	 */
	@OneToMany(mappedBy = "groupReceiver", fetch = FetchType.LAZY)
	// @OneToMany(mappedBy = "groupReceiver", fetch = FetchType.LAZY, cascade = {
	// CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
	@OrderBy("dataOfMessage ASC")
	@Fetch(FetchMode.SUBSELECT)
	private Set<Message> messagesSet = new HashSet<>();

	public Group() {

	}

/*
	public Group(String name, Date createDate) {
		this.name = name;
		this.createDate = createDate;
	}

	public Group(String name, boolean isActive, boolean isDeleted, Date createDate) {
		this.name = name;
		this.isActive = isActive;
		this.isDeleted = isDeleted;
		this.createDate = createDate;
	}
*/

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

	public void setActivity(boolean active) {
		this.isActive = active;
	}

	public boolean getActivity() {
		return this.isActive;
	}

	public void setDeleted(boolean deleted) {
		this.isDeleted = deleted;
	}

	public boolean getDeleted() {
		return this.isDeleted;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getCreateDate() {
		return this.createDate;
	}

	public void addUser(User user) {
		user.getGroupsSetWhereUserIsMember().add(this);
		this.usersSet.add(user);
	}

	public void addUsers(Set<User> users) {
		users.stream().forEach(currentItem -> currentItem.getGroupsSetWhereUserIsMember().add(this));
		this.usersSet.addAll(users);
	}

	public boolean removeUser(User user) {
		boolean firstResult = user.getGroupsSetWhereUserIsMember().remove(this);
		boolean secondResult = this.usersSet.remove(user);
		return firstResult && secondResult;
	}

	public boolean removeUsers(Set<User> users) {
		users.stream().forEach(currentItem -> currentItem.getGroupsSetWhereUserIsMember().remove(this));
		return this.usersSet.removeAll(users);
	}

	public Set<User> getUsers() {
		return this.usersSet;
	}

	public void addMessage(Message message) {
		message.setGroupReceiver(this);
		this.messagesSet.add(message);
	}

	public void addMessages(Set<Message> messages) {
		messages.stream().forEach(currentItem -> currentItem.setGroupReceiver(this));
		this.messagesSet.addAll(messages);
	}

	public Set<Message> getMessages() {
		return this.messagesSet;
	}
}
