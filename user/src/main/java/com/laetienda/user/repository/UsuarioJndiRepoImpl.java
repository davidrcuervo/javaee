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
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
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
	private String visitor;
//	private Integer userId;
	private DirContext ctx;
	
	public UsuarioJndiRepoImpl() {
		
	}
	
	public UsuarioJndiRepoImpl(EntityManagerFactory emf, Settings settings, String username) throws GeneralSecurityException, NamingException {
		setEntityManagerFactory(emf);
		setSettings(settings);
		setUsername(username);
	}
	
	public void setEntityManagerFactory(EntityManagerFactory emf) {
		this.emf = emf;
	}
	
	public void setSettings(Settings settings) throws GeneralSecurityException, NamingException {
		this.settings = settings;
		ctx = createDirContext();
	}
	
	private void setUsername(String username2) {
		this.visitor = username2;
		
		EntityManager em = emf.createEntityManager();
		try {
			if(username2 == null) {
//				userId = null;
			}else {
				Usuario u = em.createNamedQuery("Usuario.findByUsername", Usuario.class).setParameter("username", this.visitor).getSingleResult();
//				this.userId = u.getUid();
			}
		}catch(PersistenceException e) {
			log.warn("Failed to find user in database. $username: {}. $Exception: {} -> $message: {}", username2, e.getClass().getCanonicalName(), e.getMessage());
			this.visitor = null;
//			this.userId = null;
		}finally {
			em.close();
		}
	}
	
	public void close() {
		closeDirContext(ctx);
	}
	
	@Override
	public boolean userExist(String username) {
		Usuario user = findByUsernameNoRestriction(username);
		boolean result = false;

		if(user == null) {
			result = false;
		}else {
			result = true;
		}
		
		return result;
	}
	
	@Override
	public Usuario findByUsername(String username) {
		Usuario result = findByUsernameNoRestriction(username);
		
		if(result != null && canRead(result)) {
			log.debug("User, {}, can be readen by {}", username, visitor);
		}else {
			result = null;
		}
		
		return result;
	}

	@Override
	public Usuario findByEmail(String email) {
		Usuario result = findByEmailNoRestriction(email);
		
		if(result != null && canRead(result)) {
			log.debug("User with email, {}, can be readen by {}.", email, visitor);
		}else {
			result = null;
		}
		
		return result;
	}

	@Override
	public List<Usuario> findAll() {
		return findUsers("findAll");
	}
	
	@Override
	public List<Usuario> findFriends() {
		return findUsers("findFriends");
	}
	
	private List<Usuario> findUsers(String token){
		List<Usuario> result = new ArrayList<Usuario>();
		EntityManager em = emf.createEntityManager();
		
		try {
			
			if(token.equals("findFriends")) {
				result = em.createNamedQuery("Usuario.findByUsername", Usuario.class).setParameter("visitor", this.visitor).getSingleResult().getFriends();
			}else if(token.equals("findAll")) {
				result = em.createNamedQuery("Usuario.findall", Usuario.class).getResultList();
			}
			
			for(Usuario u : result) {
				Usuario uldap = this.findLdapUserByUsername(u.getUsername());
				u.setGivenname(uldap.getGivenname());
				u.setMail(uldap.getMail());
				u.setSurname(uldap.getSurname());
			}
			
		}catch(NullPointerException | PersistenceException e) {
			log.debug(e.getMessage(), e);
			
		}finally {
			em.close();
		}
		
		return result;
	}


	private Usuario findByUsernameNoRestriction(String username) {
		EntityManager em = emf.createEntityManager();
		Usuario result = null;
		try {
			Usuario dbuser = em.createNamedQuery("Usuario.findByUsername", Usuario.class).setParameter("username", username).getSingleResult();
			Usuario ldapuser = findLdapUserByUsername(username);
			result = joinUsers(dbuser, ldapuser);
			
		}catch(PersistenceException e) {
			result = null;
			log.info("User not found. $username: {}, $exeption: {} -> $message: {}", username, e.getClass().getCanonicalName(), e.getMessage());
//			log.debug("User not found. $username: {}",username, e);
		}finally {
			em.close();
		}
		return result;
	}
	

	private Usuario findByEmailNoRestriction(String email) {
		
		Usuario result = null;
		Usuario ldapuser = null;
		Usuario dbuser = null;
		
		String filter = String.format("(mail=%s)", email);
		log.debug("$filter: {}", filter);
		
		String disabledPeopleDn = String.format("ou=people,ou=disabled,%s", settings.get("ldap.domain"));
		String enabledPeopleDn = String.format("ou=people,%s", settings.get("ldap.domain"));
		log.debug("$disabledPeopleDn: {}, $enabledPeopleDn: {}", disabledPeopleDn, enabledPeopleDn);
		
		SearchControls controls = new SearchControls();
		controls.setReturningAttributes(new String[] {"*", "+"});
		
		List<Usuario> disabledUsers = searchUserFromLdap(disabledPeopleDn, filter, controls);
		List<Usuario> enabledUsers = searchUserFromLdap(enabledPeopleDn, filter, controls);
			
		if(enabledUsers.size() > 1 || disabledUsers.size() > 1) {
			log.error("There is more than one user with same email address. $email: {}", email);
			
		}else if(enabledUsers.size() == 0 && disabledUsers.size() == 0) {
			log.debug("There is no user using that email. $email: {}", email); 	
			
		}else if(enabledUsers.size() > 1 && disabledUsers.size() > 1) {
			log.error("There is more than one user with same email address. $email: {}", email);
		}else if(enabledUsers.size() == 1 && disabledUsers.size() == 0) {
			ldapuser = enabledUsers.get(0);
			log.debug("There is one user with email. $username: {}, $mail: {}", ldapuser.getUid(), email);
			
		}else if(enabledUsers.size() == 0 && disabledUsers.size() == 1) {
			ldapuser = disabledUsers.get(0);
			log.debug("There is one user with email. $username: {}, $mail: {}", ldapuser.getUid(), email);
			
		}else {
			log.error("Definetely there is no user with that email, this option is not possible to be.");
		}
		
		EntityManager em = emf.createEntityManager();
		try {
			if(ldapuser != null) {
				dbuser = em.find(Usuario.class, ldapuser.getUid());
				result = joinUsers(dbuser, ldapuser);
			}
			
		}catch(PersistenceException e) {
			//TODO
			log.debug(e.getMessage(), e);
			result = null;
		}finally {
			em.close();
		}
		
		return result;
	}

	@Override
	public List<Mistake> insert(Usuario user) {
		List<Mistake> result = new ArrayList<Mistake>();
		Validate validate = new Validate();
		String message = String.format("Failed to insert new user. $user: {}", visitor);;
		String username = user.getUsername();
		result = validate.isValid(user);
		Usuario userExists = findByUsernameNoRestriction(user.getUsername());
		Usuario emailExists = findByEmailNoRestriction(user.getMail());
		
		if(!visitor.equals("tomcat")) {
			message = String.format("User, \"%s\", can't create new users", visitor);
			log.debug(message);
			result.add(new Mistake(401, "Unauthorized user", message, user.getClass().getName(), username));
		
		}else if (result.size() > 0) {
			log.debug("User has errors and can't be persisted. $NoOfErrors: {}", result.size());
		
		}else if(userExists != null) {
			message = String.format("Username, \"%s\", already exists.", user.getUsername());
			log.debug("User can't be persisted, {}", message);
			result.add(new Mistake(400, "User exists", message, "username", user.getUsername()));
			
		}else if(emailExists != null) {
			message = String.format("Email, \"%s\", already exists.", user.getMail());
			log.debug("User can't be persisted, {}", message);
			result.add(new Mistake(400, "eMail exists", message, "email", user.getMail()));
			
		}else {
			user.setStatus(Status.EMAIL_PENDING_CONFIRMATION);
			EntityManager em = emf.createEntityManager();
			try {
				em.getTransaction().begin();
				em.persist(user);
				log.debug("User, {}, has inserted to the database. $uid: {}", user.getUsername(), user.getUid());
				insertToLdap(user);
				em.getTransaction().commit();
				log.debug("User, {}, has been inserted succesfully to db and ldap", user.getUsername());
			}catch(PersistenceException e) {
				message = String.format("Fail to insert user. $username: %s, $exception: {}, $message: {}", username, e.getClass().getCanonicalName(), e.getMessage());
				result.add(new Mistake(500, "Interal error", message, user.getClass().getName(), username));
				log.error(message);
				log.debug("{}.", message, e);
			}catch(IllegalArgumentException | IllegalAccessException | NamingException | IOException e) {
				message = String.format("Fail to insert user. $username: %s, $exception: {}, $message: {}", username, e.getClass().getCanonicalName(), e.getMessage());
				result.add(new Mistake(500, "Interal error", message, user.getClass().getName(), username));
				log.error(message);
				log.debug("{}.", message, e);
				dbRollback(em);
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
	public List<Mistake> delete(String username) {
		
		List<Mistake> result = new ArrayList<Mistake>();
		
		Usuario user = findByUsernameNoRestriction(username);
		if(user == null) {
			String message = String.format("User, %s, does not exist or \"%s\" does not have privileges to read user", username, visitor);
			result.add(new Mistake(400, "Failed to remove user", message, "Username", username));
		}else {
			result = delete(user);
		}
		
		return result;
	}

	@Override
	public List<Mistake> delete(Usuario user) {
		
		List<Mistake> result = new ArrayList<Mistake>();
		String username = null;
		
		EntityManager em = emf.createEntityManager();
		try {
			username = user.getUsername();
			
			if(!user.getUsername().equals(this.visitor)) {
				String message = String.format("Failed to remove user. User, %s, does not have privileges to remove user, %s.", this.visitor, username);
				log.warn(message);
				result.add(new Mistake(401, "Unauthorized user", message, user.getClass().getName(), this.visitor));
				
			}else {
				em.getTransaction().begin();
				Usuario temp = em.find(Usuario.class, user.getUid());
				em.remove(temp);
				removeUserFromLdap(user);
				em.getTransaction().commit();
			}
		}catch(NullPointerException | IllegalArgumentException | PersistenceException e) {
			String message = String.format("Failed to remove user. $username: %s. $exception: %s -> $message: %s", username, e.getClass().getCanonicalName(), e.getMessage());
			log.error(message);
			log.debug(message, e);
			result.add(new Mistake(500, "Failed to remove user.", message, user.getClass().getName(), username));
		}catch(NamingException e) {
			String message = String.format("Failed to remove user. $username: %s. $exception: %s -> $message: %s", username, e.getClass().getCanonicalName(), e.getMessage());
			log.error(message);
			log.debug(message, e);
			result.add(new Mistake(500, "Failed to remove user.", message, user.getClass().getName(), username));
			dbRollback(em);
		}finally {
			em.close();
		}
		
		return result;
	}

	@Override
	public List<Mistake> disable(Usuario user) {
		String dnEnabled = this.getLdapDn(user);
		String dnDisabled = String.format("cn=%s,ou=group,ou=disabled,%s", user.getUsername(), settings.get("ldap.domain"));
		return changeUserStatus(user, Status.DISABLED, dnEnabled, dnDisabled);
	}
	
	@Override
	public List<Mistake> enable(Usuario user) {
		String dnDisabled = this.getLdapDn(user);
		String dnEnabled = String.format("cn=%s,ou=people,%s", user.getUsername(), settings.get("ldap.domain"));
		return changeUserStatus(user, Status.ENABLED, dnDisabled, dnEnabled);
	}

	private List<Mistake> changeUserStatus(Usuario user, Status status, String oldDn, String newDn) {
		
		List<Mistake> result = new ArrayList<Mistake>();
		EntityManager em = emf.createEntityManager();
		Status tempStatus = user.getStatus();
		
		try {
			em.getTransaction().begin();
			user.setStatus(status);
			ctx.createSubcontext(newDn, this.getUserAttributes(user));
			em.getTransaction().commit();
			this.removeLdapDn(oldDn);
		}catch(IllegalArgumentException | IllegalAccessException | NamingException | IOException e) {
			String message = String.format("Failed to change status user. $dn: %s, $exception: %s -> $message: %s", oldDn, e.getClass().getCanonicalName(), e.getMessage());
			log.error(message);
			log.debug(message, e);
			result.add(new Mistake(500, "Failed to change user status", message, "exception", e.getClass().getCanonicalName()));
			user.setStatus(tempStatus);
			dbRollback(em);
		}catch(PersistenceException e) {
			String message = String.format("Failed to enable user. $dn: %s, $exception: %s -> $message: %s", oldDn, e.getClass().getCanonicalName(), e.getMessage());
			log.error(message);
			log.debug(message, e);
			result.add(new Mistake(500, "Failed to change user status", message, "exception", e.getClass().getCanonicalName()));
			user.setStatus(tempStatus);
			this.removeLdapDnNoException(newDn);
		}finally {
			em.close();
		}
		
		return result;
	}
	

	@Override
	public void setTomcatToDb() {
		Usuario ldaptomcat = findLdapUserByUsername("tomcat");
		ldaptomcat.setStatus(Status.ENABLED);
		EntityManager em = emf.createEntityManager();
		
		try{
			em.getTransaction().begin();
			em.persist(ldaptomcat);
			Integer uid = ldaptomcat.getUid();
			log.debug("tomcatid: {}", uid);
			String tomcatdn = getLdapDn(ldaptomcat);
			
			List<ModificationItem> modifications = new ArrayList<ModificationItem>();
			modifications.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("uid", Integer.toString(uid))));
			
			ldapModifyUser(tomcatdn, modifications);
			em.getTransaction().commit();
			
		}catch(PersistenceException e) {
			//TODO String message =....
			log.debug(e.getMessage(),e);
		}catch(NamingException | NullPointerException e) {
			//TODO String message =....
			log.debug(e.getMessage(),e);
			dbRollback(em);
		}
	}
	
	private void ldapModifyUser(String dn, List<ModificationItem> modifications) throws NamingException {
		ctx.modifyAttributes(dn, modifications.toArray(ModificationItem[]::new));	
	}

	private boolean canRead(Usuario result) {
		String username = result.getUsername();
		boolean flag = false;
		
		if(result.getUsername().equals(this.visitor) || result.getFriends().contains(result)) {
			log.debug("User, {}, has priveleges to read user: {}", this.visitor, username);
			flag = true;
			
		}else {
			log.info("User, {}, does not have priveleges to get user: {}", this.visitor, username);
			flag = false;
		}
		return flag;
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
			dbuser.setMail(ldapuser.getMail());
			dbuser.setGivenname(ldapuser.getGivenname());
			dbuser.setSurname(ldapuser.getSurname());
		}
		
		return dbuser;
	}
	
	private void dbRollback(EntityManager em) {
		
		try {
			if(em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
		}catch(PersistenceException e) {
			log.debug(e);
		}
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
	
	private void removeUserFromLdap(Usuario user) throws NamingException {
		
		String dn = getLdapDn(user);
		removeLdapDn(dn);
	}
	
	private void removeLdapDn(String dn) throws NamingException {
		
		try {
			ctx.destroySubcontext(dn);
		} catch (NamingException e) {
			String message = String.format("Failed to remove entry from LDAP. $dn: %s. $exception: %s -> $message: %s", dn, e.getClass().getCanonicalName(), e.getMessage());
			log.error(message);
			throw e;
		}
	}
	
	private void removeLdapDnNoException(String dn) {
		
		try {
			removeLdapDn(dn);
		} catch (NamingException e) {
			log.debug(e.getMessage(), e);
		}		
	}
	
	private String getLdapDn(Usuario user) {
		
		String result;
	
		if(user.getStatus() != null && user.getStatus().equals(Status.ENABLED)) {
			result = String.format("cn=%s,ou=people,%s", user.getUsername(), settings.get("ldap.domain"));
		}else {
			result = String.format("cn=%s,ou=people,ou=disabled,%s", user.getUsername(), settings.get("ldap.domain"));
		}
		
		log.debug("$dn: {}", result);
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
					boolean accessible = field.canAccess(user);
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
					field.setAccessible(accessible);
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
			log.debug("User, {}, does not exist in ldap directory", visitor);
		}else if(attributes1 != null && attributes2 != null) {
			log.fatal("User, {}, exists in enabled group and disabled group");
		
		}else if(attributes1 != null && attributes2 == null) {
			log.debug("User, {}, exists and is in enabled root directory");
			result = setUserFieldsFromLdapAttributes(attributes1);

		}else if(attributes1 == null && attributes2 != null) {
			log.debug("User, {}, exists but is in disabled root directory");
			result = setUserFieldsFromLdapAttributes(attributes2);
		
		}else {
			log.fatal("Does not make sense to not fall in any of options before. $username: {}", visitor);
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

	private List<Usuario> searchUserFromLdap(String dn, String filter, SearchControls controls) {
		List<Usuario> result = new ArrayList<Usuario>();
		
		try {
			NamingEnumeration<SearchResult> disabledPeopleResults = ctx.search(dn, filter, controls);
			while(disabledPeopleResults.hasMore()) {
				SearchResult sr = (SearchResult)disabledPeopleResults.next();
				Attributes attr = sr.getAttributes();
				
				Usuario temp = setUserFieldsFromLdapAttributes(attr);
				if(temp != null) {
					result.add(temp);
				}
			}
		} catch (NamingException e) {
			// TODO Auto-generated catch block
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
			log.debug(e.getMessage(), e);
			result = null;
		}catch(IllegalArgumentException | IllegalAccessException | ClassCastException e) {
			//TODO
			log.debug(e.getMessage(), e);
			result = null;
		}
  		
		return result;
	}

	public static void main(String[] args) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.laetienda.user");
		Settings settings = new Settings();
		List<Mistake> mistakes;
		
		Usuario user = new Usuario();
		user.setUsername("username");
		user.setMail("email@address.com");
		user.setGivenname("fname");
		user.setSurname("lname");
		user.setPassword("www.myself.com");
		
		UsuarioJndiRepoImpl urepo = null; 
		
		try {
			urepo = new UsuarioJndiRepoImpl(emf, settings, null);
//			urepo.insert(user);
//			mistakes = urepo.delete(user);
			
			urepo.removeUserFromLdap(user);
			
//			log.debug("Mistakes: {}", mistakes.size());
		
//			Usuario find1 = urepo.findByUsername("username");
//			log.debug("User by username found. $email: {}", find1.getEmail());
//			log.debug("User by username found. $status: {}", find1.getStatus().toString());
//			
//			Usuario find2 = urepo.findByEmail("email@address.com");
//			log.debug("User by email found. $email: {}", find2.getEmail());
//			log.debug("User by email found. $status: {}", find2.getStatus().toString());
			
		}catch (GeneralSecurityException | NamingException e) {
			log.error("Failed to insert user in LDAP Directory. $exception: {} -> $message: {}", e.getClass().getCanonicalName(), e.getMessage());
			log.debug("Failed to insert user in LDAP Directory." ,e);
		}finally {
			urepo.close();
		}
	}
}
