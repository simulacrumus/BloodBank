/**
 * File: RecordService.java
 * Course materials (21W) CST 8277
 *
 * @author Shariar (Shawn) Emami
 * @author (original) Mike Norman
 * 
 * update by : I. Am. A. Student 040nnnnnnn
 *
 */
package bloodbank.ejb;

import static bloodbank.entity.BloodBank.ALL_BLOODBANKS_QUERY_NAME;
import static bloodbank.entity.Person.ALL_PERSONS_QUERY_NAME;
import static bloodbank.entity.SecurityRole.ROLE_BY_NAME_QUERY;
import static bloodbank.entity.SecurityUser.USER_FOR_OWNING_PERSON_QUERY;
import static bloodbank.utility.MyConstants.DEFAULT_KEY_SIZE;
import static bloodbank.utility.MyConstants.DEFAULT_PROPERTY_ALGORITHM;
import static bloodbank.utility.MyConstants.DEFAULT_PROPERTY_ITERATIONS;
import static bloodbank.utility.MyConstants.DEFAULT_SALT_SIZE;
import static bloodbank.utility.MyConstants.DEFAULT_USER_PASSWORD;
import static bloodbank.utility.MyConstants.DEFAULT_USER_PREFIX;
import static bloodbank.utility.MyConstants.PARAM1;
import static bloodbank.utility.MyConstants.PROPERTY_ALGORITHM;
import static bloodbank.utility.MyConstants.PROPERTY_ITERATIONS;
import static bloodbank.utility.MyConstants.PROPERTY_KEYSIZE;
import static bloodbank.utility.MyConstants.PROPERTY_SALTSIZE;
import static bloodbank.utility.MyConstants.PU_NAME;
import static bloodbank.utility.MyConstants.USER_ROLE;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Hibernate;

import bloodbank.entity.Address;
import bloodbank.entity.BloodBank;
import bloodbank.entity.BloodDonation;
import bloodbank.entity.Person;
import bloodbank.entity.SecurityRole;
import bloodbank.entity.SecurityUser;


/**
 * Stateless Singleton ejb Bean - BloodBankService
 */
@Singleton
public class BloodBankService implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private static final Logger LOG = LogManager.getLogger();
    
    @PersistenceContext(name = PU_NAME)
    protected EntityManager em;
    @Inject
    protected Pbkdf2PasswordHash pbAndjPasswordHash;

    public List<Person> getAllPeople() {
    	return null;
    }

    public Person getPersonId(int id) {
    	return null;
    }

    @Transactional
    public Person persistPerson(Person newPerson) {
    	return null;
    }

    @Transactional
    public void buildUserForNewPerson(Person newPerson) {
        SecurityUser userForNewPerson = new SecurityUser();
        userForNewPerson.setUsername(
            DEFAULT_USER_PREFIX + "_" + newPerson.getFirstName() + "." + newPerson.getLastName());
        Map<String, String> pbAndjProperties = new HashMap<>();
        pbAndjProperties.put(PROPERTY_ALGORITHM, DEFAULT_PROPERTY_ALGORITHM);
        pbAndjProperties.put(PROPERTY_ITERATIONS, DEFAULT_PROPERTY_ITERATIONS);
        pbAndjProperties.put(PROPERTY_SALTSIZE, DEFAULT_SALT_SIZE);
        pbAndjProperties.put(PROPERTY_KEYSIZE, DEFAULT_KEY_SIZE);
        pbAndjPasswordHash.initialize(pbAndjProperties);
        String pwHash = pbAndjPasswordHash.generate(DEFAULT_USER_PASSWORD.toCharArray());
        userForNewPerson.setPwHash(pwHash);
        userForNewPerson.setPerson(newPerson);
        SecurityRole userRole = em.createNamedQuery(ROLE_BY_NAME_QUERY, SecurityRole.class)
            .setParameter(PARAM1, USER_ROLE).getSingleResult();
        userForNewPerson.getRoles().add(userRole);
        userRole.getUsers().add(userForNewPerson);
        em.persist(userForNewPerson);
    }

    @Transactional
    public Person setAddressFor(int id, Address newAddress) {
    	return null;
    }

    /**
     * to update a person
     * 
     * @param id - id of entity to update
     * @param personWithUpdates - entity with updated information
     * @return Entity with updated information
     */
    @Transactional
    public Person updatePersonById(int id, Person personWithUpdates) {
        Person personToBeUpdated = getPersonId(id);
        if (personToBeUpdated != null) {
            em.refresh(personToBeUpdated);
            em.merge(personWithUpdates);
            em.flush();
        }
        return personToBeUpdated;
    }

    /**
     * to delete a person by id
     * 
     * @param id - person id to delete
     */
    @Transactional
    public void deletePersonById(int id) {
        Person person = getPersonId(id);
        if (person != null) {
            em.refresh(person);
            TypedQuery<SecurityUser> findUser = em
                .createNamedQuery(USER_FOR_OWNING_PERSON_QUERY, SecurityUser.class)
                .setParameter(PARAM1, person.getId());
            SecurityUser sUser = findUser.getSingleResult();
            em.remove(sUser);
            em.remove(person);
        }
    }
}