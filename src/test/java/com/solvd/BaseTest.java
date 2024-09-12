package com.solvd;

import com.solvd.model.User;
import com.solvd.service.UserService;
import com.solvd.utils.ConfigReader;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.*;

public class BaseTest {

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = ConfigReader.getProperty("base.url");
    }

    @Test
    public void createUserTest() {
        User newUser = UserService.createUser();

        Response response = given()
                .auth().oauth2(ConfigReader.getProperty("token"))
                .contentType(ContentType.JSON)
                .body(newUser)
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .extract()
                .response();

        response.prettyPrint();

        String userId = response.jsonPath().getString("id");
        assertNotNull(userId, "User ID should be present in the response");

        assertEquals(response.jsonPath().getString("name"), newUser.getName(), "User name should match");
        assertEquals(response.jsonPath().getString("email"), newUser.getEmail(), "User email should match");
        assertEquals(response.jsonPath().getString("gender"), newUser.getGender().getValue(), "User gender should match");
        assertEquals(response.jsonPath().getString("status"), newUser.getStatus().getValue(), "User status should match");
    }

    @Test
    public void TC002_getUserDetailsTest() {
        User newUser = UserService.createUser();

        Response createUserResponse = given()
                .auth().oauth2(ConfigReader.getProperty("token"))
                .contentType(ContentType.JSON)
                .body(newUser)
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .extract()
                .response();

        String userId = createUserResponse.jsonPath().getString("id");

        Response getUserResponse = given()
                .auth().oauth2(ConfigReader.getProperty("token"))
                .contentType(ContentType.JSON)
                .when()
                .get("/users/" + userId)
                .then()
                .statusCode(200)
                .extract()
                .response();

        getUserResponse.prettyPrint();

        assertEquals(getUserResponse.jsonPath().getString("id"), userId, "User ID should match");
        assertEquals(getUserResponse.jsonPath().getString("name"), newUser.getName(), "User name should match");
        assertEquals(getUserResponse.jsonPath().getString("email"), newUser.getEmail(), "User email should match");
        assertEquals(getUserResponse.jsonPath().getString("gender"), newUser.getGender().getValue(), "User gender should match");
        assertEquals(getUserResponse.jsonPath().getString("status"), newUser.getStatus().getValue(), "User status should match");
    }

    @Test
    public void TC003_updateUserDetailsTest() {
        User newUser = UserService.createUser();

        Response createUserResponse = given()
                .auth().oauth2(ConfigReader.getProperty("token"))
                .contentType(ContentType.JSON)
                .body(newUser)
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .extract()
                .response();

        String userId = createUserResponse.jsonPath().getString("id");

        User updatedUser = User.builder()
                .name("Updated Namez")
                .email("updated_email@examplez.com")
                .gender(newUser.getGender())
                .status(newUser.getStatus())
                .build();

        Response updateUserResponse = given()
                .auth().oauth2(ConfigReader.getProperty("token"))
                .contentType(ContentType.JSON)
                .body(updatedUser)
                .when()
                .put("/users/" + userId)
                .then()
                .statusCode(200)
                .extract()
                .response();

        updateUserResponse.prettyPrint();

        assertEquals(updateUserResponse.jsonPath().getString("name"), updatedUser.getName(), "Updated name should match");
        assertEquals(updateUserResponse.jsonPath().getString("email"), updatedUser.getEmail(), "Updated email should match");
        assertEquals(updateUserResponse.jsonPath().getString("gender"), updatedUser.getGender().getValue(), "User gender should match");
        assertEquals(updateUserResponse.jsonPath().getString("status"), updatedUser.getStatus().getValue(), "User status should match");
    }

    @Test
    public void TC004_patchUserDetailsTest() {
        User newUser = UserService.createUser();

        Response createUserResponse = given()
                .auth().oauth2(ConfigReader.getProperty("token"))
                .contentType(ContentType.JSON)
                .body(newUser)
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .extract()
                .response();

        String userId = createUserResponse.jsonPath().getString("id");

        Map<String, String> updatedData = new HashMap<>();
        updatedData.put("name", "Patched Name");

        Response patchUserResponse = given()
                .auth().oauth2(ConfigReader.getProperty("token"))
                .contentType(ContentType.JSON)
                .body(updatedData)
                .when()
                .patch("/users/" + userId)
                .then()
                .statusCode(200)
                .extract()
                .response();

        patchUserResponse.prettyPrint();

        assertEquals(patchUserResponse.jsonPath().getString("name"), updatedData.get("name"), "User name should be updated");
        assertEquals(patchUserResponse.jsonPath().getString("email"), newUser.getEmail(), "User email should remain unchanged");
        assertEquals(patchUserResponse.jsonPath().getString("gender"), newUser.getGender().getValue(), "User gender should remain unchanged");
        assertEquals(patchUserResponse.jsonPath().getString("status"), newUser.getStatus().getValue(), "User status should remain unchanged");
    }

    @Test
    public void TC005_deleteUserTest() {
        User newUser = UserService.createUser();

        Response createUserResponse = given()
                .auth().oauth2(ConfigReader.getProperty("token"))
                .contentType(ContentType.JSON)
                .body(newUser)
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .extract()
                .response();

        String userId = createUserResponse.jsonPath().getString("id");

        given()
                .auth().oauth2(ConfigReader.getProperty("token"))
                .when()
                .delete("/users/" + userId)
                .then()
                .statusCode(204);

        given()
                .auth().oauth2(ConfigReader.getProperty("token"))
                .when()
                .get("/users/" + userId)
                .then()
                .statusCode(404);
    }

    @Test
    public void TC006_createPostForUserTest() {
        User newUser = UserService.createUser();

        Response createUserResponse = given()
                .auth().oauth2(ConfigReader.getProperty("token"))
                .contentType(ContentType.JSON)
                .body(newUser)
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .extract()
                .response();

        String userId = createUserResponse.jsonPath().getString("id");

        Map<String, String> postData = new HashMap<>();
        postData.put("title", "My First Post");
        postData.put("body", "This is the content of the first post.");

        Response createPostResponse = given()
                .auth().oauth2(ConfigReader.getProperty("token"))
                .contentType(ContentType.JSON)
                .body(postData)
                .when()
                .post("/users/" + userId + "/posts")
                .then()
                .statusCode(201)
                .extract()
                .response();

        createPostResponse.prettyPrint();

        assertNotNull(createPostResponse.jsonPath().getString("id"), "Post ID should be present in the response");
        assertEquals(createPostResponse.jsonPath().getString("title"), postData.get("title"), "Post title should match");
        assertEquals(createPostResponse.jsonPath().getString("body"), postData.get("body"), "Post body should match");
        assertEquals(createPostResponse.jsonPath().getString("user_id"), userId, "User ID should match");
    }

    @Test
    public void TC007_getPostsByUserTest() {
        User newUser = UserService.createUser();

        Response createUserResponse = given()
                .auth().oauth2(ConfigReader.getProperty("token"))
                .contentType(ContentType.JSON)
                .body(newUser)
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .extract()
                .response();

        String userId = createUserResponse.jsonPath().getString("id");

        Map<String, String> postData1 = new HashMap<>();
        postData1.put("title", "First Post");
        postData1.put("body", "This is the content of the first post.");

        given()
                .auth().oauth2(ConfigReader.getProperty("token"))
                .contentType(ContentType.JSON)
                .body(postData1)
                .when()
                .post("/users/" + userId + "/posts")
                .then()
                .statusCode(201);

        Map<String, String> postData2 = new HashMap<>();
        postData2.put("title", "Second Post");
        postData2.put("body", "This is the content of the second post.");

        given()
                .auth().oauth2(ConfigReader.getProperty("token"))
                .contentType(ContentType.JSON)
                .body(postData2)
                .when()
                .post("/users/" + userId + "/posts")
                .then()
                .statusCode(201);

        Response getPostsResponse = given()
                .auth().oauth2(ConfigReader.getProperty("token"))
                .contentType(ContentType.JSON)
                .when()
                .get("/users/" + userId + "/posts")
                .then()
                .statusCode(200)
                .extract()
                .response();

        getPostsResponse.prettyPrint();

        List<Map<String, String>> posts = getPostsResponse.jsonPath().getList("$");
        assertNotNull(posts, "Posts should be returned for the user");

        boolean foundFirstPost = false;
        boolean foundSecondPost = false;

        for (Map<String, String> post : posts) {
            if (post.get("title").equals("First Post")) {
                assertEquals(post.get("body"), "This is the content of the first post.", "First post content should match");
                foundFirstPost = true;
            } else if (post.get("title").equals("Second Post")) {
                assertEquals(post.get("body"), "This is the content of the second post.", "Second post content should match");
                foundSecondPost = true;
            }
        }

        assertTrue(foundFirstPost, "First post should be found");
        assertTrue(foundSecondPost, "Second post should be found");
    }

    @Test
    public void TC008_createCommentForPostTest() {
        User newUser = UserService.createUser();

        Response createUserResponse = given()
                .auth().oauth2(ConfigReader.getProperty("token"))
                .contentType(ContentType.JSON)
                .body(newUser)
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .extract()
                .response();

        String userId = createUserResponse.jsonPath().getString("id");

        Map<String, String> postData = new HashMap<>();
        postData.put("title", "Post for Comment");
        postData.put("body", "This post is for testing comment creation.");

        Response createPostResponse = given()
                .auth().oauth2(ConfigReader.getProperty("token"))
                .contentType(ContentType.JSON)
                .body(postData)
                .when()
                .post("/users/" + userId + "/posts")
                .then()
                .statusCode(201)
                .extract()
                .response();

        String postId = createPostResponse.jsonPath().getString("id");

        Map<String, String> commentData = new HashMap<>();
        commentData.put("name", "Test Commenter");
        commentData.put("email", "test_commenter@example.com");
        commentData.put("body", "This is a test comment.");

        Response createCommentResponse = given()
                .auth().oauth2(ConfigReader.getProperty("token"))
                .contentType(ContentType.JSON)
                .body(commentData)
                .when()
                .post("/posts/" + postId + "/comments")
                .then()
                .statusCode(201)
                .extract()
                .response();

        createCommentResponse.prettyPrint();

        assertNotNull(createCommentResponse.jsonPath().getString("id"), "Comment ID should be present in the response");
        assertEquals(createCommentResponse.jsonPath().getString("name"), commentData.get("name"), "Comment name should match");
        assertEquals(createCommentResponse.jsonPath().getString("email"), commentData.get("email"), "Comment email should match");
        assertEquals(createCommentResponse.jsonPath().getString("body"), commentData.get("body"), "Comment body should match");
    }

    @Test
    public void TC009_getCommentsForPostTest() {
        User newUser = UserService.createUser();

        Response createUserResponse = given()
                .auth().oauth2(ConfigReader.getProperty("token"))
                .contentType(ContentType.JSON)
                .body(newUser)
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .extract()
                .response();

        String userId = createUserResponse.jsonPath().getString("id");

        Map<String, String> postData = new HashMap<>();
        postData.put("title", "Post for Comment Retrieval");
        postData.put("body", "This post is for testing comment retrieval.");

        Response createPostResponse = given()
                .auth().oauth2(ConfigReader.getProperty("token"))
                .contentType(ContentType.JSON)
                .body(postData)
                .when()
                .post("/users/" + userId + "/posts")
                .then()
                .statusCode(201)
                .extract()
                .response();

        String postId = createPostResponse.jsonPath().getString("id");

        Map<String, String> commentData1 = new HashMap<>();
        commentData1.put("name", "First Commenter");
        commentData1.put("email", "first_commenter@example.com");
        commentData1.put("body", "This is the first comment.");

        given()
                .auth().oauth2(ConfigReader.getProperty("token"))
                .contentType(ContentType.JSON)
                .body(commentData1)
                .when()
                .post("/posts/" + postId + "/comments")
                .then()
                .statusCode(201);

        Map<String, String> commentData2 = new HashMap<>();
        commentData2.put("name", "Second Commenter");
        commentData2.put("email", "second_commenter@example.com");
        commentData2.put("body", "This is the second comment.");

        given()
                .auth().oauth2(ConfigReader.getProperty("token"))
                .contentType(ContentType.JSON)
                .body(commentData2)
                .when()
                .post("/posts/" + postId + "/comments")
                .then()
                .statusCode(201);

        Response getCommentsResponse = given()
                .auth().oauth2(ConfigReader.getProperty("token"))
                .contentType(ContentType.JSON)
                .when()
                .get("/posts/" + postId + "/comments")
                .then()
                .statusCode(200)
                .extract()
                .response();

        getCommentsResponse.prettyPrint();

        List<Map<String, String>> comments = getCommentsResponse.jsonPath().getList("$");
        assertNotNull(comments, "Comments should be returned for the post");
        assertTrue(comments.size() >= 2, "There should be at least 2 comments returned");

        boolean foundFirstComment = false;
        boolean foundSecondComment = false;

        for (Map<String, String> comment : comments) {
            if (comment.get("name").equals("First Commenter")) {
                assertEquals(comment.get("body"), "This is the first comment.", "First comment body should match");
                foundFirstComment = true;
            } else if (comment.get("name").equals("Second Commenter")) {
                assertEquals(comment.get("body"), "This is the second comment.", "Second comment body should match");
                foundSecondComment = true;
            }
        }

        assertTrue(foundFirstComment, "First comment should be found");
        assertTrue(foundSecondComment, "Second comment should be found");
    }

    @Test
    public void TC010_createTodoForUserTest() {
        User newUser = UserService.createUser();

        Response createUserResponse = given()
                .auth().oauth2(ConfigReader.getProperty("token"))
                .contentType(ContentType.JSON)
                .body(newUser)
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .extract()
                .response();

        String userId = createUserResponse.jsonPath().getString("id");

        Map<String, String> todoData = new HashMap<>();
        todoData.put("title", "Complete the report");
        todoData.put("due_on", "2024-09-30T00:00:00.000+05:30");
        todoData.put("status", "pending");

        Response createTodoResponse = given()
                .auth().oauth2(ConfigReader.getProperty("token"))
                .contentType(ContentType.JSON)
                .body(todoData)
                .when()
                .post("/users/" + userId + "/todos")
                .then()
                .statusCode(201)
                .extract()
                .response();

        createTodoResponse.prettyPrint();

        assertNotNull(createTodoResponse.jsonPath().getString("id"), "Todo ID should be present in the response");
        assertEquals(createTodoResponse.jsonPath().getString("title"), todoData.get("title"), "Todo title should match");
        assertEquals(createTodoResponse.jsonPath().getString("due_on"), todoData.get("due_on"), "Todo due date should match");
        assertEquals(createTodoResponse.jsonPath().getString("status"), todoData.get("status"), "Todo status should match");
    }
}
