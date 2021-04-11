/**
 * File: CustomIdentityStoreJPAHelper.java <br>
 * Course materials (21W) CST 8277
 * 
 * @author Mike Norman
 */
package bloodbank.security;

import static bloodbank.entity.SecurityUser.SECURITY_USER_BY_NAME_QUERY;
import static bloodbank.utility.MyConstants.PARAM1;
import static bloodbank.utility.MyConstants.PU_NAME;
import static java.util.Collections.emptySet;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bloodbank.entity.Person;
import bloodbank.entity.SecurityRole;
import bloodbank.entity.SecurityUser;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;
import javax.transaction.Transactional;

@Singleton
public class CustomIdentityStoreJPAHelper {

	private static final Logger LOG = LogManager.getLogger();
	

    private static final String PROPERTY_ALGORITHM  = "Pbkdf2PasswordHash.Algorithm";
    private static final String DEFAULT_PROPERTY_ALGORITHM  = "PBKDF2WithHmacSHA256";
    private static final String PROPERTY_ITERATIONS = "Pbkdf2PasswordHash.Iterations";
    private static final String DEFAULT_PROPERTY_ITERATIONS = "2048";
    private static final String PROPERTY_SALTSIZE   = "Pbkdf2PasswordHash.SaltSizeBytes";
    private static final String DEFAULT_SALT_SIZE   = "32";
    private static final String PROPERTY_KEYSIZE    = "Pbkdf2PasswordHash.KeySizeBytes";
    private static final String DEFAULT_KEY_SIZE    = "32";
    
    //Hard-coded userid/password's (yikes - NEVER let this get into Production!)
    private static final String ADMIN_CALLER = "admin";
    private static final String ADMIN_PASSWD = "12345";
    private static final String USER_CALLER = "mike";
    private static final String USER_PASSWD = "Password!";	

    @Inject
    protected Pbkdf2PasswordHash pbAndjPasswordHash;    
    
	@PersistenceContext( name = PU_NAME)
	protected EntityManager em;
	
	public SecurityUser findUserByPassword(String callerName, String password) {
        // the nickname of this Hash algorithm is 'PBandJ' (Peanut-Butter-And-Jam, like the sandwich!)
        Map<String, String> pbAndjProperties = new HashMap<>();
        pbAndjProperties.put(PROPERTY_ALGORITHM, DEFAULT_PROPERTY_ALGORITHM);
        pbAndjProperties.put(PROPERTY_ITERATIONS, DEFAULT_PROPERTY_ITERATIONS);
        pbAndjProperties.put(PROPERTY_SALTSIZE, DEFAULT_SALT_SIZE);
        pbAndjProperties.put(PROPERTY_KEYSIZE, DEFAULT_KEY_SIZE);
        pbAndjPasswordHash.initialize(pbAndjProperties);
        SecurityUser securityUser = new SecurityUser();
        if (USER_CALLER.equalsIgnoreCase(callerName)) {
            securityUser.setUsername(USER_CALLER);
            Person per = new Person();
            per.setId(5);
            securityUser.setPerson(per);
            String pwHash = pbAndjPasswordHash.generate(USER_PASSWD.toCharArray());
            securityUser.setPwHash(pwHash);
        }
        else if (ADMIN_CALLER.equalsIgnoreCase(callerName)) {
            securityUser.setUsername(ADMIN_CALLER);
            String pwHash = pbAndjPasswordHash.generate(ADMIN_PASSWD.toCharArray());
            securityUser.setPwHash(pwHash);
            
        }
        return securityUser;
    }
	
	
	public SecurityUser findUserByName( String username) {
		LOG.debug( "find a User By the Name={}", username);
		SecurityUser user = null;
		TypedQuery< SecurityUser> q = em.createNamedQuery( SECURITY_USER_BY_NAME_QUERY, SecurityUser.class);
		q.setParameter( PARAM1, username);
		try {
			user = q.getSingleResult();
		} catch ( NoResultException e) {
			LOG.debug( e);
			user = null;
		}
		return user;
	}

	public Set< String> findRoleNamesForUser( String username) {
		LOG.debug( "find Roles For Username={}", username);
		Set< String> roleNames = emptySet();
		SecurityUser securityUser = findUserByName( username);
		if ( securityUser != null) {
			roleNames = securityUser.getRoles().stream().map( s -> s.getRoleName()).collect( Collectors.toSet());
		}
		return roleNames;
	}

	@Transactional
	public void saveSecurityUser( SecurityUser user) {
		LOG.debug( "adding new user={}", user);
		em.persist( user);
	}

	@Transactional
	public void saveSecurityRole( SecurityRole role) {
		LOG.debug( "adding new role={}", role);
		em.persist( role);
	}
}