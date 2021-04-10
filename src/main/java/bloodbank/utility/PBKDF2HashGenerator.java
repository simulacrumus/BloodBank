/**************************************************************************************************
 * File: PBKDF2HashGenerator.java
 * Course materials (20W) CST 8277
 * @author Mike Norman
 * 
 * Note: students do NOT need to change anything in this class
 *
 */
package bloodbank.utility;

import static bloodbank.utility.MyConstants.DEFAULT_KEY_SIZE;
import static bloodbank.utility.MyConstants.DEFAULT_PROPERTY_ALGORITHM;
import static bloodbank.utility.MyConstants.DEFAULT_PROPERTY_ITERATIONS;
import static bloodbank.utility.MyConstants.DEFAULT_SALT_SIZE;
import static bloodbank.utility.MyConstants.PROPERTY_ALGORITHM;
import static bloodbank.utility.MyConstants.PROPERTY_ITERATIONS;
import static bloodbank.utility.MyConstants.PROPERTY_KEYSIZE;
import static bloodbank.utility.MyConstants.PROPERTY_SALTSIZE;

import java.util.HashMap;
import java.util.Map;

import org.glassfish.soteria.identitystores.hash.Pbkdf2PasswordHashImpl;

import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;



public class PBKDF2HashGenerator {
    // the nickname of this Hash algorithm is 'PBandJ' (Peanut-Butter-And-Jam, like the sandwich!)
    // I would like to use the constants from org.glassfish.soteria.identitystores.hash.Pbkdf2PasswordHashImpl
    // but they are not visible, so type in them all over again :-( Hope there are no typos!

    public static void main(String[] args) {
    	
        Pbkdf2PasswordHash pbAndjPasswordHash = new Pbkdf2PasswordHashImpl();

        Map<String, String> pbAndjProperties = new HashMap<>();
        pbAndjProperties.put(PROPERTY_ALGORITHM, DEFAULT_PROPERTY_ALGORITHM);
        pbAndjProperties.put(PROPERTY_ITERATIONS, DEFAULT_PROPERTY_ITERATIONS);
        pbAndjProperties.put(PROPERTY_SALTSIZE, DEFAULT_SALT_SIZE);
        pbAndjProperties.put(PROPERTY_KEYSIZE, DEFAULT_KEY_SIZE);
        pbAndjPasswordHash.initialize(pbAndjProperties);
        String pwHash = pbAndjPasswordHash.generate(args[0].toCharArray());
        System.out.printf("Hash for %s is %s%n", args[0], pwHash);
    }
}
