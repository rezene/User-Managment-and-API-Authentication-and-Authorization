package com.rezene.dynamicprivileges.ui.request.users;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UploadProfileRequest {
	private MultipartFile  profilePicture;
	private String userUuid;
	

		
}
