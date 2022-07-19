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
public class UserResourceTest {
    @Test
    @Order(1)
    void testCreateUser() {
        User newUser= new User();
        newUser.setName("Ruedi");

        User returnedUser =
            given()
                .contentType(ContentType.JSON)
                .body(newUser)
                .when().post("/users")
                .then()
                    .statusCode(201)
                    .extract()
                    .as(User.class);

        assertThat(returnedUser, notNullValue());
        newUser.setId(returnedUser.getId());
        assertThat(returnedUser, equalTo(newUser));

        Response result = 
            given()
                .when().get( "/users")
                .then()
                .statusCode(200)
                .body(
                    containsString("Ruedi")
                )
                .extract()
                .response();

        List<User> users= result.jsonPath().getList("$");
        assertThat(users, not(empty()));
        assertThat(users, hasSize(1));
    }

    @Test
    @Order(3)
    void testDeleteUser() {
        given()
            .when().delete( "/users/ruedi")
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
