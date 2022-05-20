package tests;

import io.restassured.response.Response;
import models.CreateUserModel;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import services.GoRestService;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@Execution(ExecutionMode.CONCURRENT)
public class CreateUserTests {
    static int createdUserID;

    @AfterAll
    public static void Users_DeleteUser() {
        GoRestService.deleteUser(String.valueOf(createdUserID));
    }

    @DisplayName("Client create a user successfully")
    @Test
    public static void Users_CreateUsers_Success() {

        final CreateUserModel createUserModel = new CreateUserModel("Gino Paloma", "male", "qatest@test1.com", "active");
        Response response = GoRestService.createUser(createUserModel);
        createdUserID = response.body().path("data.id");
        assertEquals(SC_CREATED, response.statusCode());
        assertFalse(response.path("data.id") == (null));
        assertEquals(createUserModel.getName(),response.path("data.name"));
        assertEquals(createUserModel.getGender(),response.path("data.gender"));
        assertEquals( createUserModel.getEmail(),response.path("data.email"));
        assertEquals(createUserModel.getStatus(),response.path("data.status"));
    }

    @DisplayName("Client can't create a user without authentication")
    @Test
    public void Users_CreateUsers_WithoutAuthentication_Fail() {

        final CreateUserModel createUserModel = new CreateUserModel("Michael Smith", "male", "msmith@test1.com", "active");
        GoRestService.createUserWithoutAuthentication(createUserModel)
                .then()
                .statusCode(SC_UNAUTHORIZED)
                .body("data.message", equalTo("Authentication failed"));
    }

    @DisplayName("Client can't create a user without filling all fields out")
    @ParameterizedTest
    @CsvFileSource(resources = "/DataForMissingEntryToCreateUser.csv")
    public void Users_CreateUsers_MissingEntry_Fail(String name, String gender, String email, String status) {
        final CreateUserModel createUserModel = new CreateUserModel(name, gender, email, status);
        GoRestService.createUser(createUserModel)
                .then()
                .statusCode(SC_UNPROCESSABLE_ENTITY)
                .body("data[0].message", equalTo("can't be blank"));
    }

    @DisplayName("Client can't create a user with an email already taken ")
    @Test
    public void Users_CreateUsers_DuplicateEmail_Fail() {
        try {
            Users_CreateUsers_Success();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            final CreateUserModel createUserModel = new CreateUserModel("Michael Smith", "male", "qatest@test1.com", "active");
            GoRestService.createUser(createUserModel)
                    .then()
                    .statusCode(SC_UNPROCESSABLE_ENTITY)
                    .body("data[0].field", equalTo("email"))
                    .body("data[0].message", equalTo("has already been taken"));
        }
    }

    @DisplayName("Client can't create a user without sending body ")
    @Test
    public void Users_CreateUserWithoutBody_Fail() {
        final CreateUserModel createUserModel = new CreateUserModel();
        GoRestService.createUserWithoutBody(createUserModel)
                .then()
                .statusCode(SC_UNPROCESSABLE_ENTITY)
                .body("data[0].field", equalTo("email"))
                .body("data[0].message", equalTo("can't be blank"))
                .body("data[1].field", equalTo("name"))
                .body("data[1].message", equalTo("can't be blank"))
                .body("data[2].field", equalTo("gender"))
                .body("data[2].message", equalTo("can't be blank"))
                .body("data[3].field", equalTo("status"))
                .body("data[3].message", equalTo("can't be blank"));
    }

    @DisplayName("Client can't create a user with an invalid email address")
    @ParameterizedTest
    @CsvFileSource(resources = "/DataForCreateUserWithInvalidEmail.csv")
    public void Users_CreateUserWithInvalidEmail(String name, String gender, String email, String status) {
        final CreateUserModel createUserModel = new CreateUserModel(name, gender, email, status);
        GoRestService.createUser(createUserModel)
                .then()
                .statusCode(SC_UNPROCESSABLE_ENTITY)
                .body("data[0].field", equalTo("email"))
                .body("data[0].message", equalTo("is invalid"));
    }

}
