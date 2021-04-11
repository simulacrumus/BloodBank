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
import javax.persistence.metamodel.SingularAttribute;
import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Hibernate;

import bloodbank.entity.Address;
import bloodbank.entity.Address_;
import bloodbank.entity.BloodBank;
import bloodbank.entity.BloodBank_;
import bloodbank.entity.BloodDonation;
import bloodbank.entity.BloodDonation_;
import bloodbank.entity.DonationRecord;
import bloodbank.entity.DonationRecord_;
import bloodbank.entity.Person;
import bloodbank.entity.Person_;
import bloodbank.entity.Phone;
import bloodbank.entity.Phone_;
import bloodbank.entity.PojoBase;
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
    	TypedQuery<Person> getAllPeople = em
                .createNamedQuery(ALL_PERSONS_QUERY_NAME, Person.class);
    	return getAllPeople.getResultList();
    	//return getAllEntities(Person.class);
    }

    public Person getPersonById(int id) {
    	return getEntityById(Person.class, Integer.class, Person_.id, id);
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
        Person personToBeUpdated = getPersonById(id);
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
        Person person = getPersonById(id);
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
    
    
    //CRUD for BloodBank
    public List<BloodBank> getAllBloodBanks() {
    	return getAllEntities(BloodBank.class);
    }
    
    public BloodBank getBloodBankById(int id) {
    	return getEntityById(BloodBank.class, Integer.class, BloodBank_.id, id);
    }
    
    @Transactional
    public BloodBank updateBloodBankById(int id, BloodBank bloodBankWithUpdates) {
    	return updateEntityById(BloodBank.class, Integer.class, BloodBank_.id, id, bloodBankWithUpdates);
    }
    
    @Transactional
    public BloodBank deleteBloodBankById(int id) {
    	return deleteEntityById(BloodBank.class, Integer.class, BloodBank_.id, id);
    }
    
    @Transactional
    public BloodBank persistBloodBank(BloodBank newBloodBank) {
    	return persistEntity(newBloodBank);
    }

    
    /***** CRUD for Phone *****/
    public List<Phone> getAllPhones() {
    	return getAllEntities(Phone.class);
    }
    
    public Phone getPhoneById(int id) {
    	return getEntityById(Phone.class, Integer.class, Phone_.id, id);
    }
    
    @Transactional
    public Phone updatePhoneById(int id, Phone phoneWithUpdates) {
    	return updateEntityById(Phone.class, Integer.class, Phone_.id, id, phoneWithUpdates);
    }
    
    @Transactional
    public Phone deletePhoneById(int id) {
    	return deleteEntityById(Phone.class, Integer.class, Phone_.id, id);
    }
    
    @Transactional
    public Phone persistPhone(Phone newPhone) {
    	return persistEntity(newPhone);
    }
    
    
    /***** CRUD for Address *****/
    public List<Address> getAllAddresses() {
    	return getAllEntities(Address.class);
    }
    
    public Address getAddressById(int id) {
    	return getEntityById(Address.class, Integer.class, Address_.id, id);
    }
    
    @Transactional
    public Address updateAddressById(int id, Address addressWithUpdates) {
    	return updateEntityById(Address.class, Integer.class, Address_.id, id, addressWithUpdates);
    }
    
    @Transactional
    public Address deleteAddressById(int id) {
    	return deleteEntityById(Address.class, Integer.class, Address_.id, id);
    }
    
    @Transactional
    public Address persistAddress(Address newAddress) {
    	return persistEntity(newAddress);
    }
    
    
    /***** CRUD for BloodDonation *****/
    public List<BloodDonation> getAllBloodDonations() {
    	return getAllEntities(BloodDonation.class);
    }
    
    public BloodDonation getBloodDonationById(int id) {
    	return getEntityById(BloodDonation.class, Integer.class, BloodDonation_.id, id);
    }
    
    @Transactional
    public BloodDonation updateBloodDonationById(int id, BloodDonation bloodDonationWithUpdates) {
    	return updateEntityById(BloodDonation.class, Integer.class, BloodDonation_.id, id, bloodDonationWithUpdates);
    }
    
    @Transactional
    public BloodDonation deleteBloodDonationById(int id) {
    	return deleteEntityById(BloodDonation.class, Integer.class, BloodDonation_.id, id);
    }
    
    @Transactional
    public BloodDonation persistBloodDonation(BloodDonation newBloodDonation) {
    	return persistEntity(newBloodDonation);
    }
    
    /***** CRUD for DonationRecord *****/
    public List<DonationRecord> getAllDonationRecords() {
    	return getAllEntities(DonationRecord.class);
    }
    
    public DonationRecord getDonationRecordById(int id) {
    	return getEntityById(DonationRecord.class, Integer.class, DonationRecord_.id, id);
    }
    
    @Transactional
    public DonationRecord updateDonationRecordById(int id, DonationRecord donationRecordWithUpdates) {
    	return updateEntityById(DonationRecord.class, Integer.class, DonationRecord_.id, id, donationRecordWithUpdates);
    }
    
    @Transactional
    public DonationRecord deleteDonationRecordById(int id) {
    	return deleteEntityById(DonationRecord.class, Integer.class, DonationRecord_.id, id);
    }
    
    @Transactional
    public DonationRecord persistDonationRecord(DonationRecord newDonationRecord) {
    	return persistEntity(newDonationRecord);
    }
    
    
    /**
     *  Helper method to get an entity by id
     * @param <T>
     * @param <R>
     * @param clazz
     * @param classPK
     * @param sa
     * @param id
     * @return
     */
    private <T extends PojoBase, R> T getEntityById(Class< T> clazz, Class< R> classPK, SingularAttribute< ? super T, R> sa, R id) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery< T> query = builder.createQuery( clazz);
		Root< T> root = query.from( clazz);
		query.select( root);
		query.where( builder.equal( root.get(sa), builder.parameter( classPK, "id")));
		TypedQuery< T> tq = em.createQuery( query);
		tq.setParameter( "id", id);
		return tq.getSingleResult();
	}
    
    /**
     * Helper method to get all entities for an entity type
     * @param <T>
     * @param clazz
     * @return
     */
    private <T extends PojoBase> List< T> getAllEntities( Class< T> clazz) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery( clazz);
		Root< T> root = query.from( clazz);
		query.select( root);
		TypedQuery< T> tq = em.createQuery( query);
		return tq.getResultList();
	}
    
    /**
     * Helper method to delete an entity by id
     * @param <T>
     * @param <R>
     * @param clazz
     * @param classPK
     * @param sa
     * @param id
     */
    private <T extends PojoBase, R> T deleteEntityById(Class< T> clazz, Class< R> classPK, SingularAttribute< ? super T, R> sa, R id) {
    	T t = getEntityById(clazz, classPK, sa, id);
    	if(t!=null) {
    		em.refresh(t);
    		em.remove(t);
    	}
    	return t;
    }
    
    /**
     * Helper method to update an entity by id
     * @param <T>
     * @param <R>
     * @param clazz
     * @param classPK
     * @param sa
     * @param id
     * @param tWithUpdates
     * @return
     */
    private <T extends PojoBase, R> T updateEntityById(Class< T> clazz, Class< R> classPK, SingularAttribute< ? super T, R> sa, R id, T tWithUpdates) {
    	T tToBeUpdated = getEntityById(clazz, classPK, sa, id);
    	if(tToBeUpdated != null) {
    		em.refresh(tToBeUpdated);
            em.merge(tWithUpdates);
            em.flush();
    	}
    	return tToBeUpdated;
    }
    
    /**
     * Helper method to persist an entity
     * @param <T>
     * @param newEntity
     * @return
     */
    private <T extends PojoBase> T persistEntity(T newEntity) {
    	em.persist(newEntity);
    	return newEntity;
    }
    
}