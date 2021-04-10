/*****************************************************************c******************o*******v******id********
 * File: BuildSecurityRolesAndUsers.java
 * Course materials (20F) CST 8277
 *
 * @author (original) Mike Norman
 * 
 * Note: students do NOT need to change anything in this class
 *
 */
package bloodbank.ejb;

import static bloodbank.utility.MyConstants.ADMIN_ROLE;
import static bloodbank.utility.MyConstants.DEFAULT_ADMIN_USER;
import static bloodbank.utility.MyConstants.DEFAULT_ADMIN_USER_PASSWORD;
import static bloodbank.utility.MyConstants.DEFAULT_KEY_SIZE;
import static bloodbank.utility.MyConstants.DEFAULT_PROPERTY_ALGORITHM;
import static bloodbank.utility.MyConstants.DEFAULT_PROPERTY_ITERATIONS;
import static bloodbank.utility.MyConstants.DEFAULT_SALT_SIZE;
import static bloodbank.utility.MyConstants.PROPERTY_ALGORITHM;
import static bloodbank.utility.MyConstants.PROPERTY_ITERATIONS;
import static bloodbank.utility.MyConstants.PROPERTY_KEYSIZE;
import static bloodbank.utility.MyConstants.PROPERTY_SALTSIZE;
import static bloodbank.utility.MyConstants.USER_ROLE;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;
import bloodbank.entity.SecurityRole;
import bloodbank.entity.SecurityUser;
import bloodbank.security.CustomIdentityStoreJPAHelper;

/**
 * This Stateless Session bean is 'special' because it is also a Singleton and
 * it runs at startup.
 *
 * How do we 'bootstrap' the security system? This EJB checks to see if the default ADMIN user
 * has already been created. If not, it then builds the ADMIN role, the default ADMIN user with
 * ADMIN role of ADMIN and the USER role ... and stores all of them in the database.
 *
 */
@Startup
@Singleton
public class BuildSecurityRolesAndUsers {
	
	
	private static final Logger LOG = LogManager.getLogger();

    @Inject
    protected CustomIdentityStoreJPAHelper jpaHelper;

    @Inject
    protected Pbkdf2PasswordHash pbAndjPasswordHash;

    @PostConstruct
    public void init() {
        // build default admin user (if needed)
    	LOG.trace( "looking for default admin");
        SecurityUser defaultAdminUser = jpaHelper.findUserByName(DEFAULT_ADMIN_USER);
    	LOG.debug( "default admin={}", defaultAdminUser);
        if (defaultAdminUser == null) {
        	LOG.trace( "making default admin");
            defaultAdminUser = new SecurityUser();
            defaultAdminUser.setUsername(DEFAULT_ADMIN_USER);
            Map<String, String> pbAndjProperties = new HashMap<>();
            pbAndjProperties.put(PROPERTY_ALGORITHM, DEFAULT_PROPERTY_ALGORITHM);
            pbAndjProperties.put(PROPERTY_ITERATIONS, DEFAULT_PROPERTY_ITERATIONS);
            pbAndjProperties.put(PROPERTY_SALTSIZE, DEFAULT_SALT_SIZE);
            pbAndjProperties.put(PROPERTY_KEYSIZE, DEFAULT_KEY_SIZE);
            pbAndjPasswordHash.initialize(pbAndjProperties);
            String pwHash = pbAndjPasswordHash.generate(DEFAULT_ADMIN_USER_PASSWORD.toCharArray());
            defaultAdminUser.setPwHash(pwHash);

            SecurityRole theAdminRole = new SecurityRole();
            theAdminRole.setRoleName(ADMIN_ROLE);
            Set<SecurityRole> roles = defaultAdminUser.getRoles();
            if (roles == null) {
                roles = new HashSet<>();
            }
            roles.add(theAdminRole);
            defaultAdminUser.setRoles(roles);
            jpaHelper.saveSecurityUser(defaultAdminUser);
            
            // if building Admin User/Role,might as well also build USER_ROLE
            SecurityRole theUserRole = new SecurityRole();
            theUserRole.setRoleName(USER_ROLE);
            jpaHelper.saveSecurityRole(theUserRole);
        }
    }
}