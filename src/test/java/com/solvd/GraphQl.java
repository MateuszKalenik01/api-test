package com.solvd;

import com.solvd.utils.ConfigReader;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.*;

public class GraphQl {
    @BeforeClass
    public void setup() {
        RestAssured.baseURI = ConfigReader.getProperty("base.url");
    }

    @Test
    public void TC001_queryAllUsersWithoutTokenTest() {
        String query = ConfigReader.getProperty("users");

        Response response = given()
                .contentType(ContentType.JSON)
                .body(query)
                .when()
                .post("/graphql")
                .then()
                .statusCode(200)
                .extract()
                .response();

        response.prettyPrint();

        List<Map<String, String>> users = response.jsonPath().getList("data.users.nodes");

        assertNotNull(users, "The user list should not be empty");
        assertFalse(users.isEmpty(), "The user list should not be empty");

        for (Map<String, String> user : users) {
            assertNotNull(user.get("id"), "User ID should be present");
            assertNotNull(user.get("name"), "Username should be present");
            assertNotNull(user.get("email"), "The user's email should be present");
            assertNotNull(user.get("status"), "User status should be present");
        }
    }
    @Test
    public void TC002_fetchUserByIdTest() {
        String userId = "7409749";
        String query = ConfigReader.getProperty("user.id").replace("{id}", userId);

        Response response = given()
                .contentType(ContentType.JSON)
                .body(query)
                .when()
                .post("/graphql")
                .then()
                .statusCode(200)
                .extract()
                .response();

        response.prettyPrint();

        String returnedId = response.jsonPath().getString("data.user.id");
        assertEquals(returnedId, userId, "The user ID should match");

        assertNotNull(response.jsonPath().getString("data.user.name"), "The username should not be empty");
        assertNotNull(response.jsonPath().getString("data.user.email"), "The user's email should not be empty");
        assertNotNull(response.jsonPath().getString("data.user.gender"), "User gender should not be blank");
        assertNotNull(response.jsonPath().getString("data.user.status"), "User status should not be blank");
    }

    @Test
    public void TC003_createUserTest() {
        String name = "Test User";
        String email = "tedsasdsadsvdsvst@example.com";
        String gender = "male";
        String status = "active";

        String query = ConfigReader.getProperty("create.user")
                .replace("{name}", name)
                .replace("{email}", email)
                .replace("{gender}", gender)
                .replace("{status}", status);

        Response response = given()
                .header("Authorization", "Bearer " + ConfigReader.getProperty("token"))
                .contentType(ContentType.JSON)
                .body(query)
                .when()
                .post("/graphql")
                .then()
                .statusCode(200)
                .extract()
                .response();

        response.prettyPrint();

        assertEquals(response.jsonPath().getString("data.createUser.user.name"), name, "The username should match");
        assertEquals(response.jsonPath().getString("data.createUser.user.email"), email, "The user's email should match");
        assertEquals(response.jsonPath().getString("data.createUser.user.gender"), gender, "The user's gender should match");
        assertEquals(response.jsonPath().getString("data.createUser.user.status"), status, "The user status should match");
    }

    @Test
    public void TC004_updateUserTest() {
        String userId = "7409788";
        String newName = "Updated Name";

        String query = ConfigReader.getProperty("update.user")
                .replace("{id}", userId)
                .replace("{name}", newName);

        Response response = given()
                .header("Authorization", "Bearer " + ConfigReader.getProperty("token"))
                .contentType(ContentType.JSON)
                .body(query)
                .when()
                .post("/graphql")
                .then()
                .statusCode(200)
                .extract()
                .response();

        response.prettyPrint();


        String updatedId = response.jsonPath().getString("data.updateUser.user.id");
        assertEquals(updatedId, userId, "The user ID should match");

        assertEquals(response.jsonPath().getString("data.updateUser.user.name"), newName, "The username should be updated");
    }
    @Test
    public void TC005_createPostForUserTest() {

        String userId = "7412502";
        String title = "My Test Post";
        String body = "This is the content of the test post.";

        String query = ConfigReader.getProperty("create.user.post")
                .replace("{id}", userId)
                .replace("{title}", title)
                .replace("{body}", body);

        Response response = given()
                .header("Authorization", "Bearer " + ConfigReader.getProperty("token"))
                .contentType(ContentType.JSON)
                .body(query)
                .when()
                .post("/graphql")
                .then()
                .statusCode(200)
                .extract()
                .response();

        response.prettyPrint();

        assertNotNull(response.jsonPath().getString("data.createPost.post"), "The reply should contain the data of the post you created");

        assertEquals(response.jsonPath().getString("data.createPost.post.userId"), userId, "The user ID should match");
        assertEquals(response.jsonPath().getString("data.createPost.post.title"), title, "The post title should fit");
        assertEquals(response.jsonPath().getString("data.createPost.post.body"), body, "The content of the post should match");
    }

}
