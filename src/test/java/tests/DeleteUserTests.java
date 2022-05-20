package tests;

import models.CreateUserModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import services.GoRestService;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

@Execution(ExecutionMode.CONCURRENT)
public class DeleteUserTests {
    static int createdUserID;

    @BeforeAll
    public static void Users_CreateUser() {
        try {
            final CreateUserModel createUserModel = new CreateUserModel("Bruce Wayne", "male", "gotham123@test1.com", "active");
            createdUserID = GoRestService.createUser(createUserModel).body().path("data.id");
        } catch (Exception e) {
            e.printStackTrace();
            createdUserID = GoRestService.getUserWithQueryParam("email", "gotham123@test1.com").body().path("data[0].id");
        }
    }

    @DisplayName("Client deletes an existing user")
    @Test
    public void Users_DeleteUsers_Success() {

        GoRestService.deleteUser(String.valueOf(createdUserID))
                .then()
                .statusCode(SC_NO_CONTENT);

    }

    @DisplayName("Client can't delete a user without authentication")
    @Test //status code should be 401 but it returns 404
    public void Users_DeleteWithoutAuthentication_Fail() {
        GoRestService.deleteUserWithoutAuthentication(String.valueOf(createdUserID))
                .then()
                .statusCode(SC_UNAUTHORIZED)
                .body("data.message", equalTo("Authentication failed"));
    }

    @DisplayName("Client can't delete a non-exist user")
    @Test
    public void Users_DeleteNonExistData_Fail() {
        GoRestService.deleteUser(String.valueOf(0))
                .then()
                .statusCode(SC_NOT_FOUND)
                .body("data.message", equalTo("Resource not found"));
    }
}
