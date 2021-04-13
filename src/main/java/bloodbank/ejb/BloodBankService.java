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
import static bloodbank.entity.Phone.ALL_PHONES_QUERY_NAME;
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
    	return getEntityById(Person.class, Person.GET_PERSION_BY_ID_QUERY_NAME, id);
    }

    @Transactional
    public Person persistPerson(Person newPerson) {
    	Person person = persistEntity(newPerson);
    	buildUserForNewPerson( person);
    	return person;
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
    	return getAllEntities( BloodBank.class, BloodBank.ALL_BLOODBANKS_QUERY_NAME);
    }
    
    public BloodBank getBloodBankById(int id) {
    	return getEntityById(BloodBank.class, BloodBank.GET_BLOODBANK_BY_ID_QUERY_NAME, id);
    }
    
    @Transactional
    public BloodBank updateBloodBankById(int id, BloodBank bloodBankWithUpdates) {
    	return updateEntityById(BloodBank.class, BloodBank.GET_BLOODBANK_BY_ID_QUERY_NAME, id, bloodBankWithUpdates);
    }
    
    @Transactional
    public BloodBank deleteBloodBankById(int id) {
    	BloodBank bloodBank = getEntityById(BloodBank.class, BloodBank.GET_BLOODBANK_BY_ID_QUERY_NAME, id);
    	bloodBank.getDonations().forEach(donation -> {
    		if(donation.getRecord() != null) {
    			DonationRecord donationRecord = getEntityById(DonationRecord.class, DonationRecord.GET_RECORD_BY_ID_QUERY_NAME, id);
    			donationRecord.setDonation(null);
    		}
    		donation.setRecord(null);
    		em.merge(donation);
    	});
    	return deleteEntityById(BloodBank.class, BloodBank.GET_BLOODBANK_BY_ID_QUERY_NAME, id);
    }
    
    @Transactional
    public BloodBank persistBloodBank(BloodBank newBloodBank) {
    	return persistEntity(newBloodBank);
    }

    
    /***** CRUD for Phone *****/
    
    
    public List<Phone> getAllPhones() {
    	return getAllEntities( Phone.class, Phone.ALL_PHONES_QUERY_NAME);
    }
    
    public Phone getPhoneById(int id) {
    	return getEntityById(Phone.class, Phone.GET_PHONE_BY_ID_QUERY_NAME , id);
    }
    
    @Transactional
    public Phone deletePhoneById(int id) {
    	return deleteEntityById(Phone.class, Phone.GET_PHONE_BY_ID_QUERY_NAME, id);
    }
    
    @Transactional
    public Phone persistPhone(Phone newPhone) {
    	return persistEntity(newPhone);
    }
    
    @Transactional
    public Phone updatePhoneById(int id, Phone updatingPhone) {
    	Phone phoneToBeUpdated = getEntityById(Phone.class, Phone.GET_PHONE_BY_ID_QUERY_NAME, id);
    	if(phoneToBeUpdated != null) {
    		em.refresh(phoneToBeUpdated);
    		phoneToBeUpdated.setAreaCode(updatingPhone.getAreaCode());
    		phoneToBeUpdated.setCountryCode(updatingPhone.getCountryCode());
    		phoneToBeUpdated.setNumber(updatingPhone.getNumber());
    		em.merge(phoneToBeUpdated);
    		em.flush();
    	}
    	return phoneToBeUpdated;
    }
    
    
    /***** CRUD for Address *****/
    
    
    public List<Address> getAllAddresses() {
    	return getAllEntities( Address.class, Address.ALL_ADDRESSES_QUERY_NAME);
    }
    
    public Address getAddressById(int id) {
    	return getEntityById(Address.class, Address.GET_ADDRESSS_BY_ID_QUERY_NAME, id);
    }
    
    @Transactional
    public Address deleteAddressById(int id) {
    	return deleteEntityById(Address.class, Address.GET_ADDRESSS_BY_ID_QUERY_NAME, id);
    }
    
    @Transactional
    public Address persistAddress(Address newAddress) {
    	return persistEntity(newAddress);
    }
    
    @Transactional
    public Address updateAddressById(int id, Address updatingAddress) {
    	Address addressToBeUpdated = getEntityById(Address.class, Address.GET_ADDRESSS_BY_ID_QUERY_NAME, id);
    	if(addressToBeUpdated != null) {
    		em.refresh(addressToBeUpdated);
    		addressToBeUpdated.setAddress(
    				updatingAddress.getStreetNumber(),
    				updatingAddress.getStreet(),
    				updatingAddress.getCity(),
    				updatingAddress.getProvince(),
    				updatingAddress.getCountry(),
    				updatingAddress.getZipcode());
    		em.merge(addressToBeUpdated);
    		em.flush();
    		
    	}
    	return addressToBeUpdated;
    }
    
    
    /***** CRUD for BloodDonation *****/
    
    
    public List<BloodDonation> getAllBloodDonations() {
    	return getAllEntities( BloodDonation.class, BloodDonation.ALL_BLOOD_DONATION_QUERY_NAME);
    }
    
    public BloodDonation getBloodDonationById(int id) {
    	return getEntityById( BloodDonation.class, BloodDonation.BLOOD_DONATION_BY_ID_QUERY_NAME, id);
    }
    
//    @Transactional
//    public BloodDonation updateBloodDonationById(int id, BloodDonation bloodDonationWithUpdates) {
//    	return updateEntityById(BloodDonation.class, BloodDonation.BLOOD_DONATION_BY_ID_QUERY_NAME, id, bloodDonationWithUpdates);
//    }
    
    @Transactional
    public BloodDonation deleteBloodDonationById(int id) {
    	BloodDonation bloodDonation = getEntityById( BloodDonation.class, BloodDonation.BLOOD_DONATION_BY_ID_QUERY_NAME, id);
    	BloodBank bloodBank = bloodDonation.getBank();
    	bloodBank.getDonations().remove(bloodDonation);
    	bloodDonation.setBank(null);
    	DonationRecord donationRecord = bloodDonation.getRecord();
    	donationRecord.setDonation(null);
    	em.refresh(bloodDonation);
		em.remove(bloodDonation);
		em.flush();
		return bloodDonation;
    }
    
    @Transactional
    public BloodDonation persistBloodDonation(BloodDonation newBloodDonation, int bloodBankId) {
    	BloodBank bank = getEntityById(BloodBank.class, BloodBank.GET_BLOODBANK_BY_ID_QUERY_NAME, bloodBankId);
    	newBloodDonation.setBank(bank);
    	bank.getDonations().add(newBloodDonation);
    	return persistEntity(newBloodDonation);
    }
    
    
    /***** CRUD for DonationRecord *****/
    
    
    public List<DonationRecord> getAllDonationRecords() {
    	return getAllEntities(DonationRecord.class, DonationRecord.ALL_RECORDS_QUERY_NAME);
    }
    
    public DonationRecord getDonationRecordById(int id) {
    	return getEntityById(DonationRecord.class, DonationRecord.ALL_RECORDS_QUERY_NAME, id);
    }
    
    @Transactional
    public DonationRecord updateDonationRecordById(int id, DonationRecord donationRecordWithUpdates) {
    	return updateEntityById(DonationRecord.class, DonationRecord.ALL_RECORDS_QUERY_NAME, id, donationRecordWithUpdates);
    }
    
    @Transactional
    public DonationRecord deleteDonationRecordById(int id) {
    	return deleteEntityById(DonationRecord.class, DonationRecord.ALL_RECORDS_QUERY_NAME, id);
    }
    
    @Transactional
    public DonationRecord persistDonationRecord(DonationRecord newDonationRecord) {
    	return persistEntity(newDonationRecord);
    }
    
    public boolean isBloodBankDuplicated(BloodBank newBloodBank) {
    	TypedQuery<Long> allBloodDonationsQuery = em.createNamedQuery(BloodBank.IS_DUPLICATE_QUERY_NAME, Long.class);
    	allBloodDonationsQuery.setParameter(PARAM1, newBloodBank.getName());
    	return allBloodDonationsQuery.getSingleResult() >= 1;
    }
    
    
    
    /***** HELPER METHODS *****/
    
    private <T extends PojoBase> List<T> getAllEntities(Class< T> entity, String queryName){
    	TypedQuery<T> allBloodDonationsQuery = em.createNamedQuery(queryName, entity);
    	return allBloodDonationsQuery.getResultList();
    }
    
    private <T extends PojoBase> T persistEntity(T newEntity) {
    	em.persist(newEntity);
    	return newEntity;
    }
    
    private <T extends PojoBase> T getEntityById(Class< T> entity, String queryName, int id ) {
    	TypedQuery<T> allBloodDonationsQuery = em.createNamedQuery(queryName, entity);
    	allBloodDonationsQuery.setParameter(PARAM1, id);
    	return allBloodDonationsQuery.getSingleResult(); 
	}
    

    private <T extends PojoBase> T deleteEntityById(Class< T> entity, String queryName, int id) {
    	T t = getEntityById(entity, queryName, id);
    	if(t != null) {
    		em.refresh(t);
    		em.remove(t);
    		em.flush();
    	}
    	return t;
    }

    private <T extends PojoBase> T updateEntityById(Class< T> entity, String queryName, int id, T updatingEntity ) {
    	T tToBeUpdated = getEntityById(entity, queryName, id);
    	if(tToBeUpdated != null) {
    		em.refresh(tToBeUpdated);
            em.merge(updatingEntity);
            em.flush();
    	}
    	return tToBeUpdated;
    }
}