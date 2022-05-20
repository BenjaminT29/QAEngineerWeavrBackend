package models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public @Data class CreateUserModel {

    private String name;
    private String gender;
    private String email;
    private String status;

}

