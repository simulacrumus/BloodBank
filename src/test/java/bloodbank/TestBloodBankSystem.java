/**
 * File: OrderSystemTestSuite.java
 * Course materials (20F) CST 8277
 * (Original Author) Mike Norman
 *
 * @date 2020 10
 *
 * (Modified) @author Jephte Francois
 */
package bloodbank;

import static bloodbank.utility.MyConstants.APPLICATION_API_VERSION;
import static bloodbank.utility.MyConstants.DEFAULT_ADMIN_USER;
import static bloodbank.utility.MyConstants.DEFAULT_ADMIN_USER_PASSWORD;
import static bloodbank.utility.MyConstants.DEFAULT_USER_PASSWORD;
import static bloodbank.utility.MyConstants.DEFAULT_USER_PREFIX;
import static bloodbank.utility.MyConstants.PERSON_RESOURCE_NAME;
import static bloodbank.utility.MyConstants.DONATION_RECORD_RESOURCE_NAME;
import static bloodbank.utility.MyConstants.PHONE_RESOURCE_NAME;
import static bloodbank.utility.MyConstants.BLOOD_DONATION_RESOURCE_NAME;
import static bloodbank.utility.MyConstants.BLOODBANK_RESOURCE_NAME;
import static bloodbank.utility.MyConstants.ADDRESS_RESOURCE_NAME;
import static bloodbank.utility.MyConstants.ACCESS_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.hamcrest.Matchers.equalTo;


import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import bloodbank.entity.Address;
import bloodbank.entity.BloodBank;
import bloodbank.entity.BloodDonation;
import bloodbank.entity.BloodType;
import bloodbank.entity.DonationRecord;
import bloodbank.entity.Person;
import bloodbank.entity.Phone;
import bloodbank.entity.PrivateBloodBank;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class TestBloodBankSystem {
    private static final Class<?> _thisClaz = MethodHandles.lookup().lookupClass();
    private static final Logger logger = LogManager.getLogger(_thisClaz);

    static final String APPLICATION_CONTEXT_ROOT = "REST-BloodBank-Skeleton";
    static final String HTTP_SCHEMA = "http";
    static final String HOST = "localhost";
    static final int PORT = 8080;
    static final int DEFAULT_ID = 1;
    static final String DEFAULT_FIRST_NAME = "Shawn";
    static final String DEFAULT_LAST_NAME = "Emami";
    private static Person newPerson;
    private static Map<String, Object> sendPerson;
    private static Map<String, Object> sendAddress;
    private static DonationRecord record;
    private static Phone phone;
    private static Address newAddress;
    private static BloodDonation bloodDonation;
    private static BloodBank bloodBank;

    // test fixture(s)
    static URI uri;
    static HttpAuthenticationFeature adminAuth;
    static HttpAuthenticationFeature userAuth;
    
    public Response getResource(HttpAuthenticationFeature authType, String resourceName) {
    	return webTarget.register(authType).path(resourceName).request().get();
    }
    
    public Response createResource(HttpAuthenticationFeature authType, String resourceName, Entity<?> newResource) {
    	return webTarget.register(authType).path(resourceName).request().post(newResource);
    }
    
    public Response updateResource(HttpAuthenticationFeature authType, String resourceName, Entity<?> newResource) {
    	return webTarget.register(authType).path(resourceName).request().put(newResource);
    }
    
    public Response deleteResource(HttpAuthenticationFeature authType, String resourceName) {
    	return webTarget.register(authType).path(resourceName).request().delete();
    }
    
    public <T> List<T> getAllResource(HttpAuthenticationFeature authType, String resourceName, Class<T> clazz){
    	Response response = getResource(authType, resourceName);
		assertThat(response.getStatus(), is(200)); // check success code from response
		return response.readEntity(new GenericType<List<T>>() {
		});
    }
    

    @BeforeAll
    public static void oneTimeSetUp() throws Exception {
        logger.debug("oneTimeSetUp");
        uri = UriBuilder
            .fromUri(APPLICATION_CONTEXT_ROOT + APPLICATION_API_VERSION)
            .scheme(HTTP_SCHEMA)
            .host(HOST)
            .port(PORT)
            .build();
        adminAuth = HttpAuthenticationFeature.basic(DEFAULT_ADMIN_USER, DEFAULT_ADMIN_USER_PASSWORD);
        userAuth = HttpAuthenticationFeature.basic(DEFAULT_USER_PREFIX, DEFAULT_USER_PASSWORD);
        
        //Person setup
        newPerson = new Person();
        newPerson.setFullName("Jack", "Ryan");
        
		sendPerson = new HashMap<>();
		sendPerson.put("firstName", newPerson.getFirstName());
		sendPerson.put("lastName", newPerson.getLastName());
		
		//DonationRecord setup
		record = new DonationRecord();
		
		
		//Phone setup
		phone = new Phone();
		phone.setNumber("1", "613", "1112222");
		
		//BloodDonation setup
		BloodType bloodType = new BloodType();
		bloodType.setType("AB", "+");
		
		bloodDonation = new BloodDonation();
		bloodDonation.setMilliliters(5);
		bloodDonation.setBloodType(bloodType);
		
		//BloodBank setup
		bloodBank = new PrivateBloodBank();
		bloodBank.setName("TEST BLOODBANK");

		
		//Address setup
		newAddress = new Address();
		newAddress.setAddress("22", "Walkley", "Ottawa", "ON", "CA", "K1V6A5");
		
		sendAddress = new HashMap<>();
		sendAddress.put("country", newAddress.getCountry());
		sendAddress.put("province", newAddress.getProvince());
		sendAddress.put("zipcode", newAddress.getZipcode());
		sendAddress.put("street", newAddress.getStreet());
		sendAddress.put("streetNumber", newAddress.getStreetNumber());
		sendAddress.put("city", newAddress.getCity());
		
    }

    protected WebTarget webTarget;
    @BeforeEach
    public void setUp() {
        Client client = ClientBuilder.newClient(
            new ClientConfig().register(MyObjectMapperProvider.class).register(new LoggingFeature()));
        webTarget = client.target(uri);		
		
    }
    
    @Order(1)
	@Test
	public void test01_all_customers_adminrole() throws JsonMappingException, JsonProcessingException {
		List<Person> emps = getAllResource(adminAuth, PERSON_RESOURCE_NAME, Person.class);
		assertThat(emps, is(not(empty())));
		assertThat(emps, hasSize(1));
	}
    
    @Order(2)
	@Test
	public void test02_all_customers_user_role() throws JsonMappingException, JsonProcessingException {
		Response response = getResource(userAuth, PERSON_RESOURCE_NAME);
		assertThat(response.getStatus(), is((401)));
		assertThat(response.getStatusInfo().getReasonPhrase(), is(equalTo(ACCESS_UNAUTHORIZED)));
	}
    
    @Order(3)
	@Test
	public void test03_get_customer_by_id() throws JsonMappingException, JsonProcessingException {
		Response response = getResource(adminAuth, PERSON_RESOURCE_NAME + "/" + DEFAULT_ID);
		assertThat(response.getStatus(), is((200))); //check success code from response 
		
		//access entity returned in response
		Person returnedPerson = response.readEntity(Person.class);
		
		//validate values
		assertThat(DEFAULT_ID, is(equalTo(returnedPerson.getId())));
		assertThat(DEFAULT_FIRST_NAME, is(equalTo(returnedPerson.getFirstName())));
		assertThat(DEFAULT_LAST_NAME, is(equalTo(returnedPerson.getLastName())));
	}
    
    @Order(4)
	@Test
	public void test04_add_customer() throws JsonMappingException, JsonProcessingException {
		//GET all customers/persons (to check the original count)
		Response response = getResource(adminAuth, PERSON_RESOURCE_NAME);
		assertThat(response.getStatus(), is(200)); //check success code from response
		
		//use originalCount to keep track of the original number of records in the db
		int originalCount = response.readEntity(new GenericType<List<Person>>() {
		}).size();
		
		response = createResource(adminAuth, PERSON_RESOURCE_NAME, Entity.json(sendPerson)); //execute request to add new Person(sendPerson)

		assertThat(response.getStatus(), is((200))); //check success code from response
		Person returnedPerson = response.readEntity(Person.class);
		newPerson.setId(returnedPerson.getId());
		
		//check returned details match details we submitted via sendPerson
		assertThat(newPerson.getFirstName(), is(equalTo(returnedPerson.getFirstName())));
		assertThat(newPerson.getLastName(), is(equalTo(returnedPerson.getLastName())));
		
		response = getResource(adminAuth, PERSON_RESOURCE_NAME); //GET ALL Person/customers again
		assertThat(response.getStatus(), is(200)); //check success code from response
		
		List<Person> emps = response.readEntity(new GenericType<List<Person>>() {
		});
		
		assertThat(originalCount+1, is(equalTo(emps.size()))); //check that the size has increased
		
	}
    
	@Order(5)
	@Test
	public void test05_add_donation_record_to_customer() throws JsonMappingException, JsonProcessingException {
		// create a structured DonationRecord object for json
		Map<String, Object> sendDonationRecord = new HashMap<>();
		sendDonationRecord.put("tested", false);

		// execute request to add a donation to the newly created person
		Response response = createResource(adminAuth,
				PERSON_RESOURCE_NAME + "/" + newPerson.getId() + "/donationrecord", Entity.json(sendDonationRecord));
		assertThat(response.getStatus(), is((200)));

		DonationRecord returnedRecord = response.readEntity(DonationRecord.class);
		record.setId(returnedRecord.getId());

		// validate the relationship between Person & DonationRecord works as expected
		assertNotEquals(null, returnedRecord); // ensure that the response has returned a valid object
		assertThat((byte) 0, is(returnedRecord.getTested())); // the tested value should be 0b and not 1b since we
																// passed in false on line 197

	}
	
    @Order(6)
	@Test
	public void test06_get_all_donation_records() throws JsonMappingException, JsonProcessingException {
		List<DonationRecord> records = getAllResource(adminAuth, DONATION_RECORD_RESOURCE_NAME, DonationRecord.class);
		assertThat(records, is(not(empty())));
		assertThat(records, hasSize(1));
	}
    
	@Order(7)
	@Test
	public void test07_get_donation_record_by_id_user_role() throws JsonMappingException, JsonProcessingException {
		Response response = getResource(userAuth, DONATION_RECORD_RESOURCE_NAME + "/" + record.getId());
		
		assertThat(response.getStatus(), is((401)));
		assertThat(response.getStatusInfo().getReasonPhrase(), is(equalTo(ACCESS_UNAUTHORIZED)));
	}
    
	@Order(8)
	@Test
	public void test08_get_donation_record_by_id_adminrole() throws JsonMappingException, JsonProcessingException {
		Response response = getResource(adminAuth, DONATION_RECORD_RESOURCE_NAME + "/" + record.getId());
		assertThat(response.getStatus(), is(200)); // check success code from response

		DonationRecord returnedRecord = response.readEntity(DonationRecord.class);

		// validate fields
		assertThat(record.getId(), is(equalTo(returnedRecord.getId())));
		assertThat(record.getTested(), is(returnedRecord.getTested()));
	}
	
    @Order(9)
	@Test
	public void test09_delete_donation_record_user_role() throws JsonMappingException, JsonProcessingException {

		Response response = deleteResource(userAuth, DONATION_RECORD_RESOURCE_NAME + "/" + record.getId()); // execute request to attempt delete
		assertThat(response.getStatus(), is((401))); // check success code from response
		assertThat(response.getStatusInfo().getReasonPhrase(), is(equalTo(ACCESS_UNAUTHORIZED)));
	}
    
    @Order(10)
	@Test
	public void test10_delete_donation_record_adminrole() throws JsonMappingException, JsonProcessingException {

		Response response = deleteResource(adminAuth, DONATION_RECORD_RESOURCE_NAME + "/" + record.getId()); // execute request to attempt delete
		assertThat(response.getStatus(), is((200))); // check success code from response
		
		response = getResource(adminAuth, DONATION_RECORD_RESOURCE_NAME);
		assertThat(response.getStatus(), is(200)); // check success code from response
		
		List<DonationRecord> records = response.readEntity(new GenericType<List<DonationRecord>>() {
		});
		assertThat(records, is(empty()));
		
	}
    
    @Order(11)
	@Test
	public void test11_delete_customer_user_role() throws JsonMappingException, JsonProcessingException {

		Response response = deleteResource(userAuth, PERSON_RESOURCE_NAME + "/" + newPerson.getId()); // execute request to attempt delete
		assertThat(response.getStatus(), is((401))); // check success code from response
		assertThat(response.getStatusInfo().getReasonPhrase(), is(equalTo(ACCESS_UNAUTHORIZED)));
	}
    
    @Order(12)
	@Test
	public void test12_getall_phones_adminrole() throws JsonMappingException, JsonProcessingException {
		List<Phone> phones = getAllResource(adminAuth, PHONE_RESOURCE_NAME, Phone.class);
		assertThat(phones, is(not(empty())));
		assertThat(phones, hasSize(2));
	}
    
    @Order(13)
	@Test
	public void test13_getall_phones_noauth() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget.path(PERSON_RESOURCE_NAME).request().get();
		assertThat(response.getStatus(), is((401)));
		assertThat(response.getStatusInfo().getReasonPhrase(), is(equalTo(ACCESS_UNAUTHORIZED)));
	}
    
    @Order(14)
	@Test
	public void test14_add_phone() throws JsonMappingException, JsonProcessingException {
		// create a structured Phone object for json
		Map<String, Object> sendPhone = new HashMap<>();
		sendPhone.put("areaCode", phone.getAreaCode());
		sendPhone.put("countryCode", phone.getCountryCode());
		sendPhone.put("number", phone.getNumber());
		
		//GET all phones (to check the original count)
		Response response = getResource(adminAuth, PHONE_RESOURCE_NAME);
		assertThat(response.getStatus(), is(200)); //check success code from response
		
		//use originalCount to keep track of the original number of records in the db
		int originalCount = response.readEntity(new GenericType<List<Phone>>() {
		}).size();
		
		response = createResource(adminAuth, PHONE_RESOURCE_NAME, Entity.json(sendPhone)); //execute request to add new Phone(sendPhone)
		assertThat(response.getStatus(), is((200))); //check success code from response
		
		Phone returnedPhone = response.readEntity(Phone.class);
		phone.setId(returnedPhone.getId());
		
		response = getResource(adminAuth, PHONE_RESOURCE_NAME); //GET ALL phones again
		assertThat(response.getStatus(), is(200)); //check success code from response
		
		List<Phone> phones = response.readEntity(new GenericType<List<Phone>>() {
		});
		assertThat(originalCount+1, is(equalTo(phones.size()))); //check that the size has increased
		
	}
    
	@Order(15)
	@Test
	public void test15_get_phone_by_id() throws JsonMappingException, JsonProcessingException {
		Response response = getResource(adminAuth, PHONE_RESOURCE_NAME + "/" + phone.getId());
		assertThat(response.getStatus(), is(200)); // check success code from response

		Phone returnedPhone = response.readEntity(Phone.class);

		// validate fields
		assertThat(phone.getId(), is(equalTo(returnedPhone.getId())));
		assertThat(phone.getAreaCode(), is(equalTo(returnedPhone.getAreaCode())));
		assertThat(phone.getCountryCode(), is(equalTo(returnedPhone.getCountryCode())));
		assertThat(phone.getNumber(), is(equalTo(returnedPhone.getNumber())));
	}
	
	@Order(16)
	@Test
	public void test16_update_phone_by_id() throws JsonMappingException, JsonProcessingException {
		// update phone details
		phone.setNumber("1", "613", "5555552");
		// create a structured Phone object for json
		Map<String, Object> updatePhone = new HashMap<>();
		updatePhone.put("areaCode", phone.getAreaCode());
		updatePhone.put("countryCode", phone.getCountryCode());
		updatePhone.put("number", phone.getNumber());

		Response response = updateResource(adminAuth, PHONE_RESOURCE_NAME + "/" + phone.getId(),
				Entity.json(updatePhone)); // execute request to update existing Phone(updatePhone)
		assertThat(response.getStatus(), is(200)); // check success code from response

		Phone returnedPhone = response.readEntity(Phone.class);

		// validate fields
		assertThat(phone.getId(), is(equalTo(returnedPhone.getId())));
		assertThat(phone.getAreaCode(), is(equalTo(returnedPhone.getAreaCode())));
		assertThat(phone.getCountryCode(), is(equalTo(returnedPhone.getCountryCode())));
		assertThat(phone.getNumber(), is(equalTo(returnedPhone.getNumber())));
	}

	@Order(17)
	@Test
	public void test17_delete_phone_user_role() throws JsonMappingException, JsonProcessingException {
		Response response = deleteResource(userAuth, PHONE_RESOURCE_NAME + "/" + phone.getId()); // execute request to
																									// attempt delete w/ userAuth role
		assertThat(response.getStatus(), is((401))); // check success code from response
		assertThat(response.getStatusInfo().getReasonPhrase(), is(equalTo(ACCESS_UNAUTHORIZED)));
	}
	
	@Order(18)
	@Test
	public void test18_delete_phone_adminrole() throws JsonMappingException, JsonProcessingException {

		Response response = deleteResource(adminAuth, PHONE_RESOURCE_NAME + "/" + phone.getId());
		assertThat(response.getStatus(), is(200)); // check success code from response
		assertThat(phone.getId(), is(response.readEntity(Phone.class).getId()));

		response = getResource(adminAuth, PHONE_RESOURCE_NAME);
		assertThat(response.getStatus(), is(200)); // check success code from response

		List<Phone> phones = response.readEntity(new GenericType<List<Phone>>() {
		});

		assertThat(phones, hasSize(2));
	}
	
	@Order(19)
	@Test
	public void test18_get_all_blood_donation_adminrole() throws JsonMappingException, JsonProcessingException {
		List<BloodDonation> bloodDonations = getAllResource(adminAuth, BLOOD_DONATION_RESOURCE_NAME, BloodDonation.class);
		assertThat(bloodDonations, is(not(empty())));
		assertThat(bloodDonations, hasSize(2));
	}
	
	@Order(20)
	@Test
	public void test20_get_blood_donation_by_id() throws JsonMappingException, JsonProcessingException {
		Response response = getResource(adminAuth, BLOOD_DONATION_RESOURCE_NAME + "/" + DEFAULT_ID);
		assertThat(response.getStatus(), is(200)); // check success code from response

		BloodDonation returnedDonation = response.readEntity(BloodDonation.class);

		// validate fields
		assertNotEquals(returnedDonation, null);
		assertThat(returnedDonation.getId(), is(DEFAULT_ID));
		assertThat(returnedDonation.getBloodType().getBloodGroup(), is("B"));
	}
	
	@Order(21)
	@Test
	public void test21_get_all_blood_bank_adminrole() throws JsonMappingException, JsonProcessingException {
		List<BloodBank> banks = getAllResource(adminAuth, BLOODBANK_RESOURCE_NAME, BloodBank.class);
		assertThat(banks, is(not(empty())));
		assertThat(banks, hasSize(2));
	}
	
    @Order(22)
	@Test
	public void test22_get_all_blood_bank_user_role() throws JsonMappingException, JsonProcessingException {
		Response response = getResource(userAuth, BLOODBANK_RESOURCE_NAME);
		assertThat(response.getStatus(), is((401)));
		assertThat(response.getStatusInfo().getReasonPhrase(), is(equalTo(ACCESS_UNAUTHORIZED)));
	}
    
	@Order(23)
	@Test
	public void test23_add_blood_bank() throws JsonMappingException, JsonProcessingException {
		// create a structured BloodBank object for json
		Map<String, Object> sendBloodBank = new HashMap<>();
		sendBloodBank.put("name", bloodBank.getName());
		sendBloodBank.put("is_public", bloodBank.isPublic());

		// GET all blood banks (to check the original count)
		Response response = getResource(adminAuth, BLOODBANK_RESOURCE_NAME);
		assertThat(response.getStatus(), is(200)); // check success code from response

		// use originalCount to keep track of the original number of records in the db
		int originalCount = response.readEntity(new GenericType<List<BloodBank>>() {
		}).size();

		response = createResource(adminAuth, BLOODBANK_RESOURCE_NAME, Entity.json(sendBloodBank)); // execute request to
																									// add new
																									// BloodBank(sendBloodBank)
		assertThat(response.getStatus(), is((200))); // check success code from response

		BloodBank returnedBloodBank = response.readEntity(BloodBank.class);
		bloodBank.setId(returnedBloodBank.getId());

		response = getResource(adminAuth, BLOODBANK_RESOURCE_NAME); // GET ALL blood banks again
		assertThat(response.getStatus(), is(200)); // check success code from response

		List<BloodBank> banks = response.readEntity(new GenericType<List<BloodBank>>() {
		});
		assertThat(originalCount + 1, is(equalTo(banks.size()))); // check that the size has increased

	}
    
	@Order(24)
	@Test
	public void test24_get_blood_bank_by_id() throws JsonMappingException, JsonProcessingException {
		Response response = getResource(adminAuth, BLOODBANK_RESOURCE_NAME + "/" + bloodBank.getId());
		assertThat(response.getStatus(), is(200)); // check success code from response

		BloodBank returnedBank = response.readEntity(BloodBank.class);

		// validate fields
		assertNotEquals(returnedBank, null);
		assertThat(returnedBank.getId(), is(bloodBank.getId()));
		assertThat(returnedBank.getName(), is(bloodBank.getName()));
	}
	
    @Order(25)
	@Test
	public void test25_delete_blood_bank_user_role() throws JsonMappingException, JsonProcessingException {
		Response response = deleteResource(userAuth, BLOODBANK_RESOURCE_NAME + "/" + bloodBank.getId()); // execute request to attempt delete
		assertThat(response.getStatus(), is((401))); // check success code from response
		assertThat(response.getStatusInfo().getReasonPhrase(), is(equalTo(ACCESS_UNAUTHORIZED)));
	}
	
    @Order(26)
	@Test
	public void test26_delete_blood__bank_adminrole() throws JsonMappingException, JsonProcessingException {

		Response response = deleteResource(adminAuth, BLOODBANK_RESOURCE_NAME + "/" + bloodBank.getId()); // execute request to delete a single BloodBank (should be the new bloodBank we just
							// created)
		assertThat(response.getStatus(), is(200)); // check success code from response
		assertThat(bloodBank.getId(), is(response.readEntity(BloodBank.class).getId()));

		List<BloodBank> banks = getAllResource(adminAuth, BLOODBANK_RESOURCE_NAME, BloodBank.class);

		assertThat(banks, is(not(empty())));
		assertThat(banks, hasSize(2));
	}
    
	@Order(27)
	@Test
	public void test27_delete_blood_donation_user_role() throws JsonMappingException, JsonProcessingException {
		Response response = deleteResource(userAuth, BLOOD_DONATION_RESOURCE_NAME + "/" + DEFAULT_ID); // execute request to
																									// attempt delete w/ userAuth role
		assertThat(response.getStatus(), is((401))); // check success code from response
		assertThat(response.getStatusInfo().getReasonPhrase(), is(equalTo(ACCESS_UNAUTHORIZED)));
	}
    
    @Order(28)
	@Test
	public void test28_delete_customer_adminrole() throws JsonMappingException, JsonProcessingException {

		Response response = deleteResource(adminAuth, PERSON_RESOURCE_NAME + "/" + newPerson.getId()); // execute request to delete a single person (should be the new person we just
							// created)
		assertThat(response.getStatus(), is(200)); // check success code from response
		assertThat(newPerson.getId(), is(response.readEntity(Person.class).getId()));

		response = getResource(adminAuth, PERSON_RESOURCE_NAME);
		assertThat(response.getStatus(), is(200)); // check success code from response

		List<Person> emps = response.readEntity(new GenericType<List<Person>>() {
		});

		assertThat(emps, is(not(empty())));
		assertThat(emps, hasSize(1));
	}

    @Order(29)
    @Test
    public void test29_all_addresses_admin_role() throws JsonMappingException, JsonProcessingException {
    	List<Address> addresses =  getAllResource(adminAuth, ADDRESS_RESOURCE_NAME, Address.class);
    	assertThat(addresses, is(not(empty())));
    	assertThat(addresses, hasSize(1));	
    }
    
    @Order(30)
    @Test
    public void test30_all_addresses_user_role() throws JsonMappingException, JsonProcessingException {
    	Response response = getResource(userAuth, ADDRESS_RESOURCE_NAME);
    	assertThat(response.getStatus(), is((401)));
		assertThat(response.getStatusInfo().getReasonPhrase(), is(equalTo(ACCESS_UNAUTHORIZED)));
    }
    
    
    @Order(31)
	@Test
	public void test31_add_address() throws JsonMappingException, JsonProcessingException {
		//GET all addresses (to check the original count)
		Response response = getResource(adminAuth, ADDRESS_RESOURCE_NAME);
		assertThat(response.getStatus(), is(200)); //check success code from response
		
		//use originalCount to keep track of the original number of records in the db
		int originalCount = response.readEntity(new GenericType<List<Address>>() {
		}).size();
		
		response = createResource(adminAuth, ADDRESS_RESOURCE_NAME, Entity.json(sendAddress)); //execute request to add new Address(sendAddress)

		assertThat(response.getStatus(), is((200))); //check success code from response
		Address returnedAddress = response.readEntity(Address.class);
		newAddress.setId(returnedAddress.getId());
		
		//check returned details match details we submitted via sendPerson
		assertThat(newAddress.getCountry(), is(equalTo(returnedAddress.getCountry())));
		assertThat(newAddress.getProvince(), is(equalTo(returnedAddress.getProvince())));
		assertThat(newAddress.getStreet(), is(equalTo(returnedAddress.getStreet())));
		assertThat(newAddress.getStreetNumber(), is(equalTo(returnedAddress.getStreetNumber())));
		assertThat(newAddress.getZipcode(), is(equalTo(returnedAddress.getZipcode())));
		
		
		response = webTarget.path(ADDRESS_RESOURCE_NAME).request().get(); //GET ALL Addresses again
		assertThat(response.getStatus(), is(200)); //check success code from response
		
		List<Address> addresses = response.readEntity(new GenericType<List<Address>>() {
		});
		
		assertThat(originalCount+1, is(equalTo(addresses.size()))); //check that the size has increased
		
	}
    
    
    @Order(32)
	@Test
	public void test32_delete_address_user_role() throws JsonMappingException, JsonProcessingException {
		Response response = deleteResource(userAuth, ADDRESS_RESOURCE_NAME + "/" + newAddress.getId()); // execute request to attempt delete
		assertThat(response.getStatus(), is((401))); // check success code from response
		assertThat(response.getStatusInfo().getReasonPhrase(), is(equalTo(ACCESS_UNAUTHORIZED)));
	}
    
    @Order(33)
	@Test
	public void test33_delete_address_adminrole() throws JsonMappingException, JsonProcessingException {
		// GET all addresses (to check the original count)
		Response response = getResource(adminAuth, ADDRESS_RESOURCE_NAME); //pass in adminAuth this time
		assertThat(response.getStatus(), is(200)); // check success code from response

		// use newCount to keep track of the original number of records + the newly
		// added address (sendAddress) in the db (so existing size plus one)
		int count = response.readEntity(new GenericType<List<Address>>() {
		}).size();

		response = deleteResource(adminAuth, ADDRESS_RESOURCE_NAME + "/" + newAddress.getId()); // execute request to delete a single address (should be the new address we just
							// created)
		assertThat(response.getStatus(), is(200)); // check success code from response
		assertThat(newAddress.getId(), is(response.readEntity(Address.class).getId()));

		response = webTarget.path(ADDRESS_RESOURCE_NAME).request().get();
		assertThat(response.getStatus(), is(200)); // check success code from response

		List<Address> addresses = response.readEntity(new GenericType<List<Address>>() {
		});

		assertThat(count - 1, is(equalTo(addresses.size())));
	}
}