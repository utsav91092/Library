package com.responsedto;

import com.model.Role;
import lombok.Data;

@Data
public class GetUserResponse {

    private Integer id;
    private String username;
    private String email;
    Role role;
}
