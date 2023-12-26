package com.rezene.dynamicprivileges.ui.request.users;

import jakarta.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleRequest {
  @NotBlank
  @Size(min = 3, max = 50)
  private String roleName;

  @NotBlank
  @Size(max = 100)
  private String roleDescription;
  

  private String [] privileges;

  

}
