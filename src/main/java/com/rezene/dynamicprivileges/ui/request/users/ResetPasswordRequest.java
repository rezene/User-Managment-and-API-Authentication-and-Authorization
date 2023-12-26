package com.rezene.dynamicprivileges.ui.request.users;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {

	private String email;
	private String passwordResetCode;
	private String newPassword;


}
