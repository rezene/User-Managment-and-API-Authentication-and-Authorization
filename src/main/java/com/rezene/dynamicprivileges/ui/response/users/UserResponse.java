package com.rezene.dynamicprivileges.ui.response.users;

import com.rezene.dynamicprivileges.shared.audit.Audit;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse extends Audit {
	 /**
	 * 
	 */
	private static final long serialVersionUID = -257037957967607541L;
	private String userUuid;
	  private String email;
	  private String title;
	  private String firstName;
	  private String fatherName;
	  private String grandFatherName;
	  private String Gender;
	  
	  private String mobilePhone;
	  private String userStatus;
	  private String userType;
	  private String insuranceUuid;
	  private String agencyUuid;
	  private String profilePicture;
	  
	  private boolean emailVerificationStatus;
	  private boolean phoneVerificationStatus;
	  private Integer roleId;
	  private String roleName;
	  private long totalPages;

}
