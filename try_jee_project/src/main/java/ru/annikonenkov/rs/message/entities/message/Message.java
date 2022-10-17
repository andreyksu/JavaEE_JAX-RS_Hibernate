package ru.annikonenkov.rs.message.entities.message;

import java.util.Date;

import javax.persistence.*;

import org.hibernate.annotations.ColumnDefault;

import ru.annikonenkov.rs.message.entities.group.Group;
import ru.annikonenkov.rs.message.entities.user.User;

@Entity
@Table(name = "messages", uniqueConstraints = {@UniqueConstraint(columnNames = { "r_user_suid", "group_suid" }, name = "uk_user_and_friend") })
public class Message {
	@Id
	@SequenceGenerator(name = "message_geneartor", sequenceName = "suid_grenerator", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "message_geneartor")
	@Column(name = "suid")
	private int id;

	@ColumnDefault("now()")
	@Column(name = "date_of_message")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataOfMessage;

	@Column(name = "text_of_message")
	private String textOfMessage;

	@Lob
	@Column(name = "file_of_message")
	@Basic(fetch = FetchType.LAZY)
	private byte[] fileOfMessage;

	@Column(name = "is_deleted", nullable = false, columnDefinition = "boolean default false")
	private boolean isDeleted = false;

	/*
	 * Правильный подход - когда все свзяи являются LAZY - а когда необходимо уже
	 * непосредственным запросом запрашиваем данные. Здесь оставил EAGER - т.к. один
	 * пользователь у автор и один получатель. И далее у них все зависимости LAZY.
	 * 
	 * https://thorben-janssen.com/self-referencing-associations/
	 * 
	 * You should always use FetchType.LAZY for all of your associations. This is
	 * the default for all to-many associations, and you need to declare it all of
	 * your to-one associations. So, better double-check all your association
	 * mappings and make sure you’re not using eager fetching. So please, don’t use
	 * FetchType.EAGER. You should always prefer FetchType.LAZY.
	 * 
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "a_user_suid", referencedColumnName = "suid", foreignKey = @ForeignKey(name = "fk_message_on_a_user"), nullable = false)
	private User author;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "r_user_suid", referencedColumnName = "suid", foreignKey = @ForeignKey(name = "fk_message_on_user_r"))
	private User receiver;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "group_suid", referencedColumnName = "suid", foreignKey = @ForeignKey(name = "fk_message_on_group"))
	private Group groupReceiver;

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public void setDateOfMessage(Date dataOfMessage) {
		this.dataOfMessage = dataOfMessage;
	}

	public Date getDateOfMessage() {
		return this.dataOfMessage;
	}

	public void setTextOfMessage(String textOfMessage) {
		this.textOfMessage = textOfMessage;
	}

	public String getTextOfMessage() {
		return this.textOfMessage;
	}

	public void setFile(byte[] fileOfMessage) {
		this.fileOfMessage = fileOfMessage;
	}

	public byte[] getFile() {
		return this.fileOfMessage;
	}

	public Message() {
	}

	public void setAuthor(User author) {
		this.author = author;
		author.getMessagesWhereUserIsAuthor().add(this);
	}

	public User getAuthor() {
		return this.author;
	}

	public void setReceiver(User receiver) {
		receiver.getMessagesWhereUserIsReceiver().add(this);
		this.receiver = receiver;
	}

	public User getReceiver() {
		return this.receiver;
	}

	public void setGroupReceiver(Group groupReceiver) {
		groupReceiver.getMessages().add(this);
		this.groupReceiver = groupReceiver;
	}

	public Group getGroupReceiver() {
		return this.groupReceiver;
	}

	public void setIsDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	
	public boolean getIsDeleted() {
		return isDeleted;
	}

}
