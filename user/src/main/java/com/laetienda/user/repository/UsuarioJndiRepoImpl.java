package com.laetienda.user.repository;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.laetienda.lib.form.HtmlForm;
import com.laetienda.lib.mistake.Mistake;
import com.laetienda.lib.usuario.LdapAttribute;
import com.laetienda.lib.usuario.Status;
import com.laetienda.lib.utilities.Aes;
import com.laetienda.lib.utilities.AesFirstRepoImpl;
import com.laetienda.model.lib.Validate;
import com.laetienda.model.webdb.Usuario;
import com.laetienda.user.lib.Settings;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class UsuarioJndiRepoImpl implements UsuarioRepository {
	final private static Logger log = LogManager.getLogger(UsuarioJndiRepoImpl.class);
	
	private EntityManagerFactory emf;
	private Settings settings;
	private String username;
	private DirContext ctx;
	
	public UsuarioJndiRepoImpl() {
		
	}
	
	public UsuarioJndiRepoImpl(EntityManagerFactory emf, Settings settings, String username) throws GeneralSecurityException, NamingException {
		setEntityManagerFactory(emf);
		setSettings(settings);
		setUsername(username);
	}
	
	private void setUsername(String username2) {
		this.username = username2;
	}

	public void setEntityManagerFactory(EntityManagerFactory emf) {
		this.emf = emf;
	}
	
	public void setSettings(Settings settings) throws GeneralSecurityException, NamingException {
		this.settings = settings;
		ctx = createDirContext();
	}
	
	public void close() {
		closeDirContext(ctx);
	}

	@Override
	public List<Usuario> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Usuario findByUsername(String username) {
		EntityManager em = emf.createEntityManager();
		Usuario result = null;
		try {
			Usuario dbuser = em.createNamedQuery("Usuario.findByUsername", Usuario.class).setParameter("username", username).getSingleResult();
			Usuario ldapuser = findLdapUserByUsername(username);
			result = joinUsers(dbuser, ldapuser);
			
		}catch(PersistenceException e) {
			result = null;
			log.info("User not found. $username: {}, $exeption: {} -> $message: {}", username, e.getClass().getCanonicalName(), e.getMessage());
			log.debug("User not found. $username: {}",username, e);
		}finally {
			em.close();
		}
		return result;
	}
	
	@Override
	public Usuario findByEmail(String email) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Mistake> insert(Usuario user) {
		List<Mistake> result = new ArrayList<Mistake>();
		Validate validate = new Validate();
		String message;
		result = validate.isValid(user);
		Usuario userExists = findByUsername(user.getUsername());
		Usuario emailExists = findByEmail(user.getEmail());
		
		if(username != null) {
			message = String.format("User, \"%s\", can't create new users", username);
			log.debug(message);
			result.add(new Mistake(401, "Unauthorized user", message, user.getClass().getDeclaredAnnotation(HtmlForm.class).name(), username));
		
		}else if (result.size() > 0) {
			log.debug("User has errors and can't be persisted. $NoOfErrors: {}", result.size());
		
		}else if(userExists != null) {
			message = String.format("Username, \"%s\", already exists.", user.getUsername());
			log.debug("User can't be persisted, {}", message);
			result.add(new Mistake(400, "User exists", message, "username", user.getUsername()));
			
		}else if(emailExists != null) {
			message = String.format("Email, \"%s\", already exists.", user.getEmail());
			log.debug("User can't be persisted, {}", message);
			result.add(new Mistake(400, "eMail exists", message, "email", user.getEmail()));
			
		}else {
			user.setStatus(Status.EMAIL_PENDING_CONFIRMATION);
			EntityManager em = emf.createEntityManager();
			try {
				em.getTransaction().begin();
				em.persist(user);
				em.getTransaction().commit();
				log.debug("User, {}, has inserted to the database. $uid: {}", user.getUsername(), user.getUid());
				insertToLdap(user);
				log.debug("User, {}, has been inserted succesfully to db and ldap", user.getUsername());
			}catch(Exception e) {
				//TODO remove user from ldap to make sure database and ldap are synchronyzed
				//TODO remove user from database, first check if user exists.
				message = String.format("Failed to insert new user. $user: {}", user.getUsername());
				result.add(new Mistake(500, "Interal error", message, user.getClass().getDeclaredAnnotation(HtmlForm.class).name(), message));
				log.error("{}. $exception: {} -> $message: {}", message, e.getClass().getCanonicalName(), e.getMessage());
				log.debug("{}.", message, e);
			}finally {
				em.close();
			}
		}
		
		return result;
	}

	@Override
	public List<Mistake> update(Usuario user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Mistake> delete(Usuario user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Mistake> disable(Usuario user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Mistake> enable(Usuario user) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private Usuario joinUsers(Usuario dbuser, Usuario ldapuser) {
		
		if(dbuser == null) {
			log.debug("dbuser is null");
		} else if(ldapuser == null) {
			log.debug("ldapuser is null");
			dbuser = null;
		} else if(dbuser.getUid() != ldapuser.getUid()) {
			log.error("dbuserid is different from ldapuserid. $dbuserid: {}, $ldapuserid: {}", dbuser.getUid(), ldapuser.getUid());
			dbuser = null;
		}else if(!dbuser.getUsername().equals(ldapuser.getUsername())){
			log.error("dbusername is different from ldapusername. $dbusername: {}, $ldapusername: {}", dbuser.getUsername(), ldapuser.getUsername());
			dbuser = null;
		}else {
			dbuser.setEmail(ldapuser.getEmail());
			dbuser.setFirstName(ldapuser.getFirstName());
			dbuser.setLastName(ldapuser.getLastName());
		}
		
		return dbuser;
	}

	
	private DirContext createDirContext() throws GeneralSecurityException, NamingException {
		DirContext result = null;
		
		String adminDn = settings.get("ldap.admin.dn");
		log.debug("$adminDn: {}", adminDn);
		
		Hashtable<String, Object> env = new Hashtable<String, Object>(11);
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, settings.get("ldap.url"));
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, adminDn);
		
		Aes aes = new AesFirstRepoImpl();
		String adminPassword;
		
		try {
			adminPassword = aes.decrypt(settings.get("ldap.admin.password"), adminDn);			
			env.put(Context.SECURITY_CREDENTIALS, adminPassword);		
			result = new InitialDirContext(env);
			log.debug("Connected to ldap. {}", result.getAttributes("").toString());
		}catch (GeneralSecurityException | NamingException e) {
			log.error("Failed to insert user in LDAP Directory. $exception: {} -> $message: {}", e.getClass().getCanonicalName(), e.getMessage());
			throw e;
		}
		
		return result;
	}
	
	private void closeDirContext(DirContext ctx) {
		try {
			ctx.close();
		} catch (NullPointerException | NamingException e) {
			log.error("Failed to close LDAP Directory context. $exception: {} -> $message{}", e.getClass().getCanonicalName(), e.getMessage());
			log.debug("Failed to close LDAP Directory context.", e);
		}
	}
	
	private Context insertToLdap(Usuario user) throws IllegalArgumentException, IllegalAccessException, NamingException, IOException {
				
		//cn=username,ou=people,ou=disabled,dc=example,dc=com
		String userDn = String.format("cn=%s,ou=people,ou=disabled,%s", user.getUsername(), settings.get("ldap.domain"));
		log.debug("$userDn: {}", userDn);
		Context result = null;
		
		try {
			result = ctx.createSubcontext(userDn, getUserAttributes(user));
		} catch (IllegalArgumentException | IllegalAccessException | NamingException | IOException e) {
			log.error("Failed to inser user in LDAP Directory. $exception: {} -> $message: {}", e.getClass().getCanonicalName(), e.getMessage());
			throw e;
		} 
		
		return result;
	}
	
	private Attributes getUserAttributes(Usuario user) throws IllegalArgumentException , IllegalAccessException, IOException {
		Attributes result = new BasicAttributes();
		
		Attribute objclass = new BasicAttribute("objectclass");
		objclass.add("inetOrgPerson");
		result.put(objclass);
		Field[] fields = user.getClass().getDeclaredFields();
		
		try {
			for(Field field : fields) {
				Annotation annotation = field.getAnnotation(LdapAttribute.class);
				
				if(annotation instanceof LdapAttribute) {
					field.setAccessible(true);
					LdapAttribute ldapAttr = (LdapAttribute)annotation;
					Attribute attr = new BasicAttribute(ldapAttr.attribute());
					
					String value = new String();
					
					if(field.get(user) instanceof String) {
						value = (String)field.get(user);
					
					} else if(field.get(user) instanceof Integer) {
						Integer i = (Integer)field.get(user);
						value = Integer.toString(i);
						
					} else {
						log.error("Ivalid ldap attribute. $field: {}", field.getName());
						throw new IOException("Invalid Attribute");
					}
					
					log.debug("Attribute found. $attribute: {} -> $value: {}", ldapAttr.attribute(), value);
					attr.add(value);
					result.put(attr);
				}
			}
		}catch(IllegalArgumentException | IllegalAccessException e) {
			log.error("Failed to build user LDAP attributes. $exception: {} -> $message: {}", e.getClass().getCanonicalName(), e.getMessage());
			throw e;
		}
		
		return result;
	}
	

	private Usuario findLdapUserByUsername(String username2) {
		Usuario result = null;
		String disabledUserDn = String.format("cn=%s,ou=people,ou=disabled,%s", username2, settings.get("ldap.domain"));
		log.debug("$disabledUserDn: {}", disabledUserDn);
		String enabledUserDn = String.format("cn=%s,ou=people,%s", username2, settings.get("ldap.domain"));
		log.debug("$enabledUserDn: {}", enabledUserDn);
		
		Attributes attributes1 = getAttributesFromDn(enabledUserDn);
		Attributes attributes2 = getAttributesFromDn(disabledUserDn);
					
		if(attributes1 == null && attributes2 == null) {
			log.debug("User, {}, does not exist in ldap directory", username);
		}else if(attributes1 != null && attributes2 != null) {
			log.fatal("User, {}, exists in enabled group and disabled group");
		
		}else if(attributes1 != null && attributes2 == null) {
			log.debug("User, {}, exists and is in enabled root directory");
			result = setUserFieldsFromLdapAttributes(attributes1);

		}else if(attributes1 == null && attributes2 != null) {
			log.debug("User, {}, exists but is in disabled root directory");
			result = setUserFieldsFromLdapAttributes(attributes2);
		
		}else {
			log.fatal("Does not make sense to not fall in any of options before. $username: {}", username);
		}			
		
		return result;
	}
	
	private Attributes getAttributesFromDn(String dn) {
		Attributes result = null;
		
		try {
			result = ctx.getAttributes(dn, new String[] {"*", "+"});
		}catch(NamingException e) {
			//TODO
			log.debug(e);		
		}

		return result;
	}

	private Usuario setUserFieldsFromLdapAttributes(Attributes attributes) {
		Usuario result = new Usuario();
		Field[] fields = result.getClass().getDeclaredFields();
		
		try {
			for(Field field : fields) {
				Annotation anotation = field.getAnnotation(LdapAttribute.class);
				
				if(anotation instanceof LdapAttribute) {
					field.setAccessible(true);
					String name = field.getAnnotation(LdapAttribute.class).attribute();
					log.debug("$type: {}", field.getType().getCanonicalName());

					if(name.toLowerCase().equals("userpassword")) {
						log.debug("Password is not recovered");
						log.debug("$passwordType: {}", attributes.get(name).get(0).getClass().getCanonicalName());
						
					}else if(field.getType().getCanonicalName().equals("java.lang.String")) {
						String value = (String)attributes.get(name).get(0);
						log.debug("Setting user field. $field: {}, -> $value: {}", field.getName(), value);
						field.set(result, value);
						
					}else if(field.getType().getCanonicalName().equals("java.lang.Integer")) {
						String s = (String)attributes.get(name).get(0);
						Integer value = Integer.parseInt(s);
						log.debug("Setting user field. $field: {}, -> $value: {}", field.getName(), value);
						field.set(result, value);
					}else {
						log.error("Field is not string or integer. $type: {}", field.getType().getCanonicalName());
						
					}
				}
			}
		}catch(NamingException e) {
			//TODO 
			log.debug(e);
			result = null;
		}catch(IllegalArgumentException | IllegalAccessException | ClassCastException e) {
			//TODO
			log.debug(e);
			result = null;
		}
  		
		return result;
	}

	public static void main(String[] args) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.laetienda.user");
		Settings settings = new Settings();
		
		Usuario user = new Usuario();
		user.setUsername("username");
		user.setEmail("email@address.com");
		user.setFirstName("fname");
		user.setLastName("lname");
		user.setPassword("www.myself.com");
		
		UsuarioJndiRepoImpl urepo = null; 
		
		try {
			urepo = new UsuarioJndiRepoImpl(emf, settings, null);
			urepo.insert(user);
			Usuario find = urepo.findByUsername("username");
			log.debug("$email: {}", find.getEmail());
			log.debug("$status: {}", find.getStatus().toString());
			
		}catch (GeneralSecurityException | NamingException e) {
			log.error("Failed to insert user in LDAP Directory. $exception: {} -> $message: {}", e.getClass().getCanonicalName(), e.getMessage());
			log.debug("Failed to insert user in LDAP Directory." ,e);
		}finally {
			urepo.close();
		}
	}
}
