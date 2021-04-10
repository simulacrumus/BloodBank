/**
 * File: RestConfig.java
 * Course materials (21W) CST 8277
 *
 * @author Mike Norman
 * @date 2020 10
 * 
 * Note: students do NOT need to change anything in this class
 */
package bloodbank.rest;

import static bloodbank.utility.MyConstants.ADMIN_ROLE;
import static bloodbank.utility.MyConstants.APPLICATION_API_VERSION;
import static bloodbank.utility.MyConstants.USER_ROLE;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.security.DeclareRoles;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath(APPLICATION_API_VERSION)
//this used to be in web.xml
@DeclareRoles({USER_ROLE, ADMIN_ROLE})
public class RestConfig extends Application {

    /*
     * Without the following 'feature', the default Serialization/Deserialization for Jakarta EE 8
     * is JSON-B via a project called 'Yasson'.
     * 
     * However Yasson does not 'nicely' handle a variety of issues ... so substitute a non-standard
     * (but much more popular!) package called 'Jackson'
     */
    
    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put("jersey.config.jsonFeature", "JacksonFeature");
        return props;
    }
}