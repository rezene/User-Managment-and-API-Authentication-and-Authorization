package com.rezene.dynamicprivileges.ui.request.users;


import jakarta.persistence.Column;
import jakarta.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrivilegeRequest {
  @NotBlank
  @Size(min = 3, max = 50)
  private String privilegeName;

  @NotBlank
  @Size(max = 100)
  private String privilegeDescription;
  
  @NotBlank
  @Column(length = 50)
  private String privilegeCategory;


  

}
