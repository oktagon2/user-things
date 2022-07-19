package quarkus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
public class UserThingResourceTest {

    @Inject UserRepository userRepository;
    @Inject ThingRepository thingRepository;

    void addUser( String name) {
        User user= new User();
        user.setName(name);
        userRepository.persist(user);
    }

    void addThing( String name) {
        Thing thing= new Thing();
        thing.setName(name);
        thingRepository.persist(thing);
    }

    @Test
    @Order(1)
    void testPutUserThings1() {
        addUser( "Ruedi");
        addUser( "Brigitte");

        addThing( "Thing 1");
        addThing( "Thing 2");
        addThing( "Thing 3");

        List<User> users= userRepository.listAll();
        List<Thing> things= thingRepository.listAll();
        List<Long> thingIds= new ArrayList<>();
        thingIds.add( things.get(0).getId());
        
        String queryString= "?userId="+users.get(0).getId();
        Thing[] returnedThings= given()
            .body( thingIds)
            .when()
                .put( "/userthings"+queryString)
            .then()
                .statusCode( 200)
                .extract()
                .as( Thing[].class);
                
        assertThat( returnedThings.length, greaterThan( 0));
    }
}
