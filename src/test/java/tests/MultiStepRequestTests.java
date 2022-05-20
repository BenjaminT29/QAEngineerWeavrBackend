package tests;

import io.restassured.response.Response;

import static org.apache.http.HttpStatus.*;
import static org.junit.jupiter.api.Assertions.*;
import models.CreateUserModel;
import models.UpdateUserModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import services.GoRestService;

@Execution(ExecutionMode.CONCURRENT)
public class MultiStepRequestTests {

    Response response;
    int amountOfUsers;
    int createdUserID;
    CreateUserModel createUserModel;
    UpdateUserModel updateUserModel;

    @DisplayName("The client can properly implement basic operations")
    @Test
    public void Users_FlowWithCRUDOperators()  {

        //send get request to get amount of users;
        response=GoRestService.getAllUsers();
        assertEquals(SC_OK, response.statusCode());
        amountOfUsers=response.path("meta.pagination.total");

        //sent a post request to create a new user
        createUserModel = new CreateUserModel("Gino Paloma", "male", "qatest@test1.com", "active");
        response = GoRestService.createUser(createUserModel);
        createdUserID = response.body().path("data.id");
        assertEquals(SC_CREATED, response.statusCode());
        assertFalse(response.path("data.id") == (null));
        assertEquals(createUserModel.getName(),response.path("data.name"));
        assertEquals(createUserModel.getGender(),response.path("data.gender"));
        assertEquals( createUserModel.getEmail(),response.path("data.email"));
        assertEquals(createUserModel.getStatus(),response.path("data.status"));

        //send get request to verify if the user is created
        response=GoRestService.getAllUsers();
        assertEquals(SC_OK, response.statusCode());

        //amount of users should be increased by 1
        int newAmountofUsers=response.path("meta.pagination.total");
        assertTrue(newAmountofUsers==(amountOfUsers+1));

        //the newly created user should be first in the list.
        assertEquals(createUserModel.getName(), response.path("data[0].name"));
        assertEquals(createUserModel.getGender(), response.path("data[0].gender"));
        assertEquals(createUserModel.getEmail(), response.path("data[0].email"));
        assertEquals(createUserModel.getStatus(), response.path("data[0].status"));

        //send a put request to update the data
        updateUserModel=new UpdateUserModel("Michael Smith", "male","msmith@test1.com", "active");
        response=GoRestService.updateUser(updateUserModel, String.valueOf(createdUserID));
        assertEquals(SC_OK, response.statusCode());
        assertEquals(updateUserModel.getName(),response.path("data.name"));
        assertEquals(updateUserModel.getGender(),response.path("data.gender"));
        assertEquals( updateUserModel.getEmail(),response.path("data.email"));
        assertEquals(updateUserModel.getStatus(),response.path("data.status"));

        //send get request with id to verify if the user data is updated
        response=GoRestService.getUserWithQueryParam("id", String.valueOf(createdUserID));
        assertEquals(SC_OK, response.statusCode());

        //the user information should be seen with updated data
        assertEquals(updateUserModel.getName(), response.path("data[0].name"));
        assertEquals(updateUserModel.getGender(), response.path("data[0].gender"));
        assertEquals(updateUserModel.getEmail(), response.path("data[0].email"));
        assertEquals(updateUserModel.getStatus(), response.path("data[0].status"));

        //send a delete request to delete the user
        response=GoRestService.deleteUser(String.valueOf(createdUserID));
        assertEquals(SC_NO_CONTENT, response.statusCode());


        //send get request to verify if the user is deleted
        response=GoRestService.getUserWithPathParam("id", String.valueOf(createdUserID));
        assertEquals(SC_NOT_FOUND, response.statusCode());
        assertEquals("Resource not found", response.path("data.message"));
    }
}
