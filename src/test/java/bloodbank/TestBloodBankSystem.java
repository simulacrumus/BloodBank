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
import static bloodbank.utility.MyConstants.ACCESS_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
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

import bloodbank.entity.Person;

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
    
    public Response deleteResource(HttpAuthenticationFeature authType, String resourceName) {
    	return webTarget.register(authType).path(resourceName).request().delete();
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
		Response response = getResource(adminAuth, PERSON_RESOURCE_NAME);
		assertThat(response.getStatus(), is(200));
		List<Person> emps = response.readEntity(new GenericType<List<Person>>() {
		});
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
	public void test03_get_customer_by_id_adminrole() throws JsonMappingException, JsonProcessingException {
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
		
		response = webTarget.path(PERSON_RESOURCE_NAME).request().get(); //GET ALL Person/customers again
		assertThat(response.getStatus(), is(200)); //check success code from response
		
		List<Person> emps = response.readEntity(new GenericType<List<Person>>() {
		});
		
		assertThat(originalCount+1, is(equalTo(emps.size()))); //check that the size has increased
		
		response = webTarget.path(PERSON_RESOURCE_NAME + "/" + newPerson.getId()).request()
			.get(); //get the new person we just added
		assertThat(response.getStatus(), is(200)); //check success code from response
		returnedPerson = response.readEntity(Person.class);
		
		//check all value are identical
		assertThat(newPerson.getId(), is(equalTo(returnedPerson.getId())));
		assertThat(newPerson.getFirstName(), is(equalTo(returnedPerson.getFirstName())));
		assertThat(newPerson.getLastName(), is(equalTo(returnedPerson.getLastName())));
		
	}
    
    @Order(5)
	@Test
	public void test05_delete_customer_user_role() throws JsonMappingException, JsonProcessingException {

		Response response = deleteResource(userAuth, PERSON_RESOURCE_NAME + "/" + newPerson.getId()); // execute request to attempt delete
		assertThat(response.getStatus(), is((401)));
		assertThat(response.getStatusInfo().getReasonPhrase(), is(equalTo(ACCESS_UNAUTHORIZED)));
	}
    
    @Order(6)
	@Test
	public void test06_delete_customer_adminrole() throws JsonMappingException, JsonProcessingException {
		// GET all customers/persons (to check the original count)
		Response response = getResource(adminAuth, PERSON_RESOURCE_NAME); //pass in adminAuth this time
		assertThat(response.getStatus(), is(200)); // check success code from response

		// use newCount to keep track of the original number of records + the newly
		// added record (sendPerson) in the db (so existing size plus one)
		int count = response.readEntity(new GenericType<List<Person>>() {
		}).size();

		response = webTarget.path(PERSON_RESOURCE_NAME + "/" + newPerson.getId()).request()
				.delete(); // execute request to delete a single person (should be the new person we just
							// created)
		assertThat(response.getStatus(), is(200)); // check success code from response
		assertThat(newPerson.getId(), is(response.readEntity(Person.class).getId()));

		response = webTarget.path(PERSON_RESOURCE_NAME).request().get();
		assertThat(response.getStatus(), is(200)); // check success code from response

		List<Person> emps = response.readEntity(new GenericType<List<Person>>() {
		});

		assertThat(count - 1, is(equalTo(emps.size())));
	}

}