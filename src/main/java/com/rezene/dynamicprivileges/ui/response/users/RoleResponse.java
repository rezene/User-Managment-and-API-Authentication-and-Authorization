package com.rezene.dynamicprivileges.ui.response.users;

import java.util.List;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleResponse {
	  private Integer id;
	  private String roleName;
	  private String roleDescription;
	  private String roleUuid;
	  private List<String> privileges;
	  private long totalPages;



}
