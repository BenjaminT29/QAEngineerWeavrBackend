package tests;

import models.CreateUserModel;
import models.UpdateUserModel;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import services.GoRestService;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;

@Execution(ExecutionMode.CONCURRENT)
public class UpdateUserTests {

    static int createdUserID;

    @BeforeAll
    public static void Users_CreateAUser_Success() {
        final CreateUserModel createUserModel = new CreateUserModel("Arya Stark", "female", "winterfell@test1.com", "active");
        createdUserID = GoRestService.createUser(createUserModel).body().path("data.id");
    }

    @AfterAll
    public static void Users_DeleteUser() {
        GoRestService.deleteUser(String.valueOf(createdUserID));
    }

    @DisplayName("Client update a user")
    @ParameterizedTest
    @CsvFileSource(resources = "/DataForUpdateUser.csv")
    public void Users_UpdateUser_Success(String name, String gender, String email, String status) {
        UpdateUserModel updateUserModel = new UpdateUserModel(name, gender, email, status);
        GoRestService.updateUser(updateUserModel, String.valueOf(createdUserID))
                .then()
                .statusCode(SC_OK)
                .body("data.id", equalTo(createdUserID))
                .body("data.name", equalTo(updateUserModel.getName()))
                .body("data.gender", equalTo(updateUserModel.getGender()))
                .body("data.email", equalTo(updateUserModel.getEmail()))
                .body("data.status", equalTo(updateUserModel.getStatus()));
    }

    @DisplayName("Client can't update a user without authentication")
    @ParameterizedTest
    @CsvFileSource(resources = "/DataForUpdateUser.csv")
    public void Users_UpdateUserWithoutAuthentication_Fail(String name, String gender, String email, String status) {
        UpdateUserModel updateUserModel = new UpdateUserModel(name, gender, email, status);
        GoRestService.updateUserWithoutAuthentication(updateUserModel, String.valueOf(createdUserID))
                .then()
                .statusCode(SC_UNAUTHORIZED);//status code should be 401 but it returns 404
    }

    @DisplayName("Client update a user without sending a body")
    @Test
    public void Users_UpdateUserWithoutBody_Success() {
        UpdateUserModel updateUserModel = new UpdateUserModel();
        GoRestService.updateUserWithoutBody(updateUserModel, String.valueOf(createdUserID))
                .then()
                .statusCode(SC_OK);
    }
}
