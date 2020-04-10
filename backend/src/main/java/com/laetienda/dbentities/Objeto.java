package com.laetienda.dbentities;

import java.io.Serializable;
//import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.persistence.*;

import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.laetienda.myapptools.MyAppTools;
import com.laetienda.myldap.Group;
import com.laetienda.myldap.User;

@Entity
@Table(name="objetos")
@NamedQueries({
	@NamedQuery(name="Objeto.findall", query="SELECT o FROM Objeto o")
})
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Objeto implements Serializable, DatabaseEntity{	
	private static final long serialVersionUID = 1L;
	private static final Logger log = LogManager.getLogger(Objeto.class);
//	private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Id
	@SequenceGenerator(name = "objeto_id_seq", sequenceName = "objeto_id_seq", allocationSize=1)
	@GeneratedValue(generator = "objeto_id_seq", strategy = GenerationType.SEQUENCE)
	@Column(name="\"id\"")
	private Integer id;
	
	@Column(name="\"created\"", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar created;
	
	@Column(name="\"modified\"", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar modified;

	@Column(name="\"user\"", nullable=true, unique=false)
	private String owner;
	
	@Column(name="\"group\"", nullable=true, unique=false)
	private String group;
	
	@OneToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="\"read_acl_id\"", nullable=true, unique=false)
	private AccessList write;
	
	@OneToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="\"write_acl_id\"", nullable=true, unique=false)
	private AccessList read;
	
	@OneToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="\"delete_acl_id\"", nullable=true, unique=false)
	private AccessList delete;
	
	@Transient
	private HashMap<String, List<String>> errors = new HashMap<String, List<String>>();
	
	@Transient
	private MyAppTools tools;
	
	public Objeto() {
		tools = new MyAppTools();
	}
	
	/**
	 * 
	 * @param owner
	 * @param Group
	 * @param delete
	 * @param write
	 * @param read
	 * @param conn
	 * @throws Exception
	 */
	public Objeto(User owner, Group Group, AccessList delete, AccessList write, AccessList read, LdapConnection conn) throws Exception {
		tools = new MyAppTools();
		setOwner(owner, conn);
		setGroup(Group, conn);
		setDelete(delete);
		setWrite(write);
		setRead(read);
	}
	
	@PrePersist
	public void onPrePersist() {
		setCreated(Calendar.getInstance()); 
	}
	
	@PreUpdate
	public void noPreUpdate() {
		setModified(Calendar.getInstance());
	}
	
	public Calendar getModified() {
		return modified;
	}
	
	public void setModified(Calendar modified) {
		this.modified = modified;
	}
	
	public Calendar getCreated() {
		return created;
	}

	public String getOwner() {
		return owner;
	}
	
	public void setCreated(Calendar created) {
		this.created = created;
	}

	public Objeto setOwner(User owner, Group group, LdapConnection conn) {
		setOwner(owner, conn);
		setGroup(group, conn);
		return this;
	}
	
	public void setOwner(String owner, LdapConnection conn) throws Exception {
		User user = new User(owner, conn);
		setOwner(user, conn);
	}

	public void setOwner(User owner, LdapConnection conn) {
		this.owner = owner.getUid();
		
		try {
			if(conn.exists(owner.getLdapEntry().getDn())) {
				log.info("User, " + owner.getUid() + ", is correct to be added as owner");
			}else {
				addError("owner", "User to own the object does not exist");
			}
		} catch (LdapException e) {
			addError("owner", "Failed to set owner.");
			log.debug("Failed to set owner.", e);
		}
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String strGroup, LdapConnection conn) throws Exception {
		Group group = new Group(strGroup, conn);
		setGroup(group, conn);
	}
	
	public void setGroup(Group group, LdapConnection conn) {
		this.group = group.getGroupName();
		
		try {
			if(conn.exists(group.getLdapEntry().getDn())) {
				log.info("Group, " + group.getGroupName() + ", is correct to be added as group");
			}else {
				addError("group", "Disered group to own the object does not exist");
			}
		} catch (LdapException e) {
			addError("group", "Failed to set group.");
			log.debug("Failed to set group.", e);
		}
	}

	public AccessList getWrite() {
		return write;
	}

	public void setWrite(AccessList write) {
		this.write = write;
	}

	public AccessList getRead() {
		return read;
	}

	public void setRead(AccessList read) {
		this.read = read;
	}

	public AccessList getDelete() {
		return delete;
	}

	public void setDelete(AccessList delete) {
		this.delete = delete;
	}

	public Integer getId() {
		return id;
	}

	public Objeto setPermisions(AccessList delete, AccessList write, AccessList read){
		this.setDelete(delete);
		this.setWrite(write);
		this.setRead(read);
		return this;
	}
	
	@Override
	public HashMap<String, List<String>> getErrors(){
		return errors;
	}
	
	@Override
	public void addError(String list, String error) {
		tools.addError(list, error, errors);
	}
}
