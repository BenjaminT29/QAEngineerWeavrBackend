package services;

import io.restassured.response.Response;
import models.CreateUserModel;
import models.UpdateUserModel;


public class GoRestService extends BaseService {

    public static Response createUser(final CreateUserModel createUserModel){
        return defaultRequestSpecification()
                .body(createUserModel)
                .when()
                .post("/public/v1/users");
    }
    public static Response createUserWithoutAuthentication(final CreateUserModel createUserModel){
        return  restAssured()
                .header("Content-type", "application/json")
                .body(createUserModel)
                .when()
                .post("/public/v1/users");
    }
    public static Response createUserWithoutBody(final CreateUserModel createUserModel){
        return defaultRequestSpecification()
                .when()
                .post("/public/v1/users");
    }
    public static Response deleteUser(String id){
        return defaultRequestSpecification()
                .when()
                .delete("/public/v1/users/{id}");
    }
    public static Response deleteUserWithoutAuthentication(String id){
        return  restAssured()
                .header("Content-type", "application/json")
                .when()
                .delete("/public/v1/users/{id}");
    }
    public static Response getAllUsers(){
        return defaultRequestSpecification()
                .when()
                .get("/public/v1/users/");
    }
    public static Response getUserWithPathParam(String parameterName, String id){
        return defaultRequestSpecification()
                .when()
                .pathParam(parameterName,id)
                .get("/public/v1/users/{id}");
    }
    public static Response getUserWithQueryParam(String Key, String Value){
        return defaultRequestSpecification()
                .when()
                .queryParam(Key,Value)
                .get("/public/v1/users/");
    }
    public static Response updateUser(UpdateUserModel updateUserModel, String id){
        return defaultRequestSpecification()
                .when()
                .body(updateUserModel)
                .put("/public/v1/users/{id}");
    }
    public static Response updateUserWithoutAuthentication(UpdateUserModel updateUserModel, String id){
        return  restAssured()
                .header("Content-type", "application/json")
                .body(updateUserModel)
                .when()
                .put("/public/v1/users/{id}");
    }
    public static Response updateUserWithoutBody(UpdateUserModel updateUserModel, String id){
        return defaultRequestSpecification()
                .when()
                .put("/public/v1/users/{id}");
    }
}
