package tests;

import io.restassured.response.Response;
import models.CreateUserModel;
import models.GetUserModel;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import services.GoRestService;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;

@Execution(ExecutionMode.CONCURRENT)
public class GetUserTest {
    static GetUserModel getUserModel;

    @BeforeAll
    public static void Users_CreateUser() {
        final CreateUserModel createUserModel = new CreateUserModel("Benjamin Button", "male", "bbutton123@test1.com", "active");
        Response response = GoRestService.createUser(createUserModel);
        getUserModel = new GetUserModel(response);
    }

    @AfterAll
    public static void Users_DeleteUser() {
        GoRestService.deleteUser(String.valueOf(getUserModel.getId()));
    }

    @DisplayName("Client get users that are on the first page")
    @Test
    public void Users_GetAllUsers_Success() {
        GoRestService.getAllUsers()
                .then()
                .statusCode(SC_OK)
                .body("meta.pagination.page", equalTo(1))
                .body("meta.pagination.limit", equalTo(20));
    }

    @DisplayName("Client get a specific users with id number")
    @Test
    public void Users_GetUserWithID_Success() {
        GoRestService.getUserWithPathParam("id", String.valueOf(getUserModel.getId()))
                .then()
                .statusCode(SC_OK)
                .body("data.name", equalTo(getUserModel.getName()))
                .body("data.email", equalTo(getUserModel.getEmail()))
                .body("data.gender", equalTo(getUserModel.getGender()))
                .body("data.status", equalTo(getUserModel.getStatus()));

    }

    @DisplayName("Client get a specific users with email address")
    @Test
    public void Users_GetUserWithEmail_Success() {
        GoRestService.getUserWithQueryParam("email", getUserModel.getEmail())
                .then()
                .statusCode(SC_OK)
                .body("data[0].id", equalTo(getUserModel.getId()))
                .body("data[0].name", equalTo(getUserModel.getName()))
                .body("data[0].gender", equalTo(getUserModel.getGender()))
                .body("data[0].status", equalTo(getUserModel.getStatus()));
    }

    @DisplayName("Client get users that are on a specific page")
    @ParameterizedTest
    @CsvSource({
            "page,1",
            "page,2",
            "page,5",
            "page,10"
    })
    public void Users_GetUsersWithPageNumber_Success(String queryParameter, int pageNumber) {
        GoRestService.getUserWithQueryParam(queryParameter, String.valueOf(pageNumber))
                .then()
                .statusCode(SC_OK)
                .body("meta.pagination.page", equalTo(pageNumber))
                .body("meta.pagination.limit", equalTo(20));
    }
    @DisplayName("Client can't retrieve a non-exist user")
    @Test
    public void Users_GetNonExistData_Fail() {
        GoRestService.getUserWithPathParam("id","0")
                .then()
                .statusCode(SC_NOT_FOUND)
                .body("data.message", Matchers.equalTo("Resource not found"));
    }

}
