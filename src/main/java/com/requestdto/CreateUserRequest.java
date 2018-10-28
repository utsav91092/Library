package com.requestdto;

import io.swagger.annotations.ApiModelProperty;
import com.model.Role;
import lombok.Data;

@Data
public class CreateUserRequest {

    @ApiModelProperty(position = 0)
    private String username;
    @ApiModelProperty(position = 1)
    private String email;
    @ApiModelProperty(position = 2)
    private String password;
    @ApiModelProperty(position = 3)
    private Role role;

}
