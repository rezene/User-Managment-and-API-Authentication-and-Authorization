package com.rezene.dynamicprivileges.ui.request.users;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendEmailRequest {

	private String toAddress;
	private String subject;
	private String body;
}
