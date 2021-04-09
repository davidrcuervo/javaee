package com.laetienda.model.webdb;

import java.io.Serializable;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.laetienda.lib.usuario.Ldap;
import com.laetienda.lib.usuario.LdapAttribute;
import com.laetienda.lib.usuario.Status;
import com.laetienda.model.lib.ValidateParameters;

/**
 * Entity implementation class for Entity: Usuario
 *
 */
@Ldap
@Entity
@Table(name="usuario")
@NamedQueries({
	@NamedQuery(name="Usuario.findall", query="SELECT u FROM Usuario u"),
	@NamedQuery(name="Usuario.findByUid", query="SELECT u FROM Usuario u WHERE u.uid = :uid"),
	@NamedQuery(name="Usuario.findByUsername", query="SELECT u FROM Usuario u WHERE u.username = :username"),
	@NamedQuery(name="Usuario.findAllInReaders", query="SELECT u FROM Usuario u JOIN u.friends r WHERE r = :uid")
	
})
public class Usuario implements Serializable {
	private static final long serialVersionUID = 1L;
	final private static Logger log = LogManager.getLogger(Usuario.class);

	@Id
	@SequenceGenerator(name = "usuario_id_seq", sequenceName = "usuario_id_seq", allocationSize=1)
	@GeneratedValue(generator = "usuario_id_seq", strategy = GenerationType.SEQUENCE)
	@Column(name="\"uid\"")
	@LdapAttribute(attribute="uid")
	private Integer uid;
	
	@Column(name="\"status\"")
	private Status status;
	
	@Column(name="\"username\"", nullable=false, unique=true)
	@LdapAttribute(attribute="cn")
	@ValidateParameters(name="username", nullable=false, minlenght=5, maxlenght=100, regex = "[a-zA-Z0-9]+")
	private String username;

	@Transient
	@LdapAttribute(attribute="givenname")
	@ValidateParameters(name="First_Name", nullable=false, regex = "[a-zA-Z\\s]+")
	private String givenname;
	
	@Transient
	@LdapAttribute(attribute="sn")
	@ValidateParameters(name="Last_Name", nullable=false, regex = "[a-zA-Z\\s]+")
	private String surname;
	
	@Transient
	@LdapAttribute(attribute="mail")
	@ValidateParameters(name="email", nullable=false, regex = "^[A-Za-z0-9+_.-]+@(.+)$")
	private String mail;
	
	@Transient
	@LdapAttribute(attribute="userPassword")
	@ValidateParameters(name="password", nullable=false, minlenght=6, maxlenght=100)
	private String password;
	
	@ElementCollection
	@CollectionTable(name="usuario_friends")
	@ValidateParameters(name="friends", nullable=true)
	private List<Integer> friends = new ArrayList<Integer>();
	
	public Usuario() {
		super();
	}
	
	

	public Usuario(String username, String givenname, String surname, String mail, String password) {
		super();
		setUsername(username);
		setFirstName(givenname);
		setLastName(surname);
		setEmail(mail);
		setPassword(password);
	}



	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirstName() {
		return givenname;
	}

	public void setFirstName(String firstName) {
		this.givenname = firstName;
	}

	public String getLastName() {
		return surname;
	}

	public void setLastName(String lastName) {
		this.surname = lastName;
	}

	public String getEmail() {
		return mail;
	}

	public void setEmail(String email) {
		this.mail = email;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getUid() {
		return uid;
	}   
	
	public void addFriend(Usuario user) {
		if(user == null || friends.contains(user.getUid())) {
			log.debug("User, \"{}\", is already friend of {}", user.getUsername(), this.getUsername());
		}else {
			friends.add(user.getUid());
		}
	}
	
	public List<Integer> getFriendIds(){
		return this.friends;
	}
}
