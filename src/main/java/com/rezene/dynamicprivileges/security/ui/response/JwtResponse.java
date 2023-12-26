package com.rezene.dynamicprivileges.security.ui.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtResponse {
	private String token;
	private String type = "Bearer";
	private String userUuid;
	private String email;
	private String roleName;
	private String title;
	private String firstName;
	private String fatherName;
	private String grandFatherName;
	private String gender;
	private String mobilePhone;
	private String userStatus;
	private String userType;
	private String insuranceUuid;
	private String agencyUuid;
	private String profilePicture;
	private List<String> privileges;

	public JwtResponse(String accessToken, String userUuid, String email, String roleName, String title, String firstName, String fatherName,
		  String grandFatherName, String gender, String mobilePhone, String userStatus,String userType, String agencyUuid, String insuranceUuid,
		  String profilePicture,List<String> privileges) {
    this.token = accessToken;
    this.userUuid = userUuid;
    this.email = email;
    this.roleName=roleName; this.title=title; this.firstName=firstName; this.fatherName=fatherName; this.grandFatherName=grandFatherName;
    		this.gender=gender; this.mobilePhone=mobilePhone; this.userStatus=userStatus; this.userType=userType;
    				this.agencyUuid=agencyUuid;
    		this.insuranceUuid=insuranceUuid; this.profilePicture=profilePicture;
    
    this.privileges = privileges;
  
  }

	
/*
  public String getAccessToken() {
    return token;
  }

  public void setAccessToken(String accessToken) {
    this.token = accessToken;
  }

  public String getTokenType() {
    return type;
  }

  public void setTokenType(String tokenType) {
    this.type = tokenType;
  }

  public String getUserString() {
    return userUuid;
  }

  public void setUserUuid(String userUuid) {
    this.userUuid = userUuid;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

 

  public List<String> getPrivileges() {
    return privileges;
  }*/
}
