package com.laetienda.model.webdb;

import java.io.Serializable;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.laetienda.lib.form.Form;
import com.laetienda.lib.form.HtmlForm;
import com.laetienda.lib.form.InputForm;
import com.laetienda.lib.form.InputType;
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
@HtmlForm(name = "Usuario")
public class Usuario implements Serializable, Form {
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
	@InputForm(id = "usernameInput", label = "Username", name = "username", placeholder = "Please insert your username")
	private String username;

	@Transient
	@LdapAttribute(attribute="givenname")
	@ValidateParameters(name="name", nullable=false, regex = "[a-zA-Z\\s]+")
	@InputForm(id = "fNameInput", label = "First Name", name = "name", placeholder = "Please insert your First Name")
	private String givenname;
	
	@Transient
	@LdapAttribute(attribute="sn")
	@ValidateParameters(name="surname", nullable=false, regex = "[a-zA-Z\\s]+")
	@InputForm(id = "lNameInput", label = "Last Name", name = "surname", placeholder = "Please insert your Last Name")
	private String surname;
	
	@Transient
	@LdapAttribute(attribute="mail")
	@ValidateParameters(name="email", nullable=false, regex = "^[A-Za-z0-9+_.-]+@(.+)$")
	@InputForm(id = "mailInput", label = "eMail address", name = "email", placeholder = "Please insert your email address")
	private String email;
	
	@Transient
	@LdapAttribute(attribute="userPassword")
	@ValidateParameters(name="password", nullable=false, minlenght=6, maxlenght=100)
	@InputForm(id = "passwordInput", type=InputType.PASSWORD, label = "Password", name = "password", placeholder = "Please insert your password")
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
		setGivenname(givenname);
		setSurname(surname);
		setMail(mail);
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

	public String getGivenname() {
		return givenname;
	}

	public void setGivenname(String firstName) {
		this.givenname = firstName;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String lastName) {
		this.surname = lastName;
	}

	public String getMail() {
		return email;
	}

	public void setMail(String email) {
		this.email = email;
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
