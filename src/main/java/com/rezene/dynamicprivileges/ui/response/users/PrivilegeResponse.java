package com.rezene.dynamicprivileges.ui.response.users;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class PrivilegeResponse {
	 
	  private String privilegeName;
	  private String privilegeDescription;
	  private String privilegeCategory;
	  private String privilegeUuid;
	  private long totalPages;



}
