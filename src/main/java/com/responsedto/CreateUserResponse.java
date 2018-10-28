package com.responsedto;

import io.swagger.annotations.ApiModelProperty;
import com.model.Role;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreateUserResponse {

  @ApiModelProperty(position = 0)
  private Integer id;
  @ApiModelProperty(position = 1)
  private String username;
  @ApiModelProperty(position = 2)
  private String email;
  @ApiModelProperty(position = 3)
  Role role;

}
