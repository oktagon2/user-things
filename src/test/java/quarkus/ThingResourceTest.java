package quarkus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.emptyOrNullString;

import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
public class ThingResourceTest {
    @Test
    @Order(1)
    void testCreateThing() {
        Thing newThing= new Thing();
        newThing.setName("Thing 1");

        Thing returnedThing =
            given()
                .contentType(ContentType.JSON)
                .body(newThing)
                .when().post("/things")
                .then()
                    .statusCode(201)
                    .extract()
                    .as(Thing.class);

        assertThat(returnedThing, notNullValue());
        newThing.setId(returnedThing.getId());
        assertThat(returnedThing, equalTo(newThing));

        Response result = 
            given()
                .when().get( "/things")
                .then()
                .statusCode(200)
                .body(
                    containsString("Thing 1")
                )
                .extract()
                .response();

        List<Thing> things= result.jsonPath().getList("$");
        assertThat(things, not(empty()));
        assertThat(things, hasSize(1));
    }

    @Test
    @Order(3)
    void testDeleteThing() {
        given()
            .when().delete( "/things/Thing 1")
            .then()
            .statusCode(204)
            .body(
                is(emptyOrNullString())
            );
    }

    @Test
    @Order(4)
    void thatsAllFolks() {
        assertThat(true, equalTo(true));
    }
}
