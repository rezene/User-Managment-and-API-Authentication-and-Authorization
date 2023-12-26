package com.rezene.dynamicprivileges.services.users;

import java.util.List;


import org.springframework.http.ResponseEntity;

import com.rezene.dynamicprivileges.ui.request.users.PrivilegeRequest;
import com.rezene.dynamicprivileges.ui.response.users.PrivilegeResponse;


public interface PrivilegeService {
	public PrivilegeResponse createPrivilege(PrivilegeRequest privilegeRequest);
	public PrivilegeResponse updatePrivilege(String privilegeUuid, PrivilegeRequest roleRequest);
	public ResponseEntity<?> deletePrivilege(String privilegeUuid);
	public PrivilegeResponse getPrivilege(String privilegeUuid);
	public List<PrivilegeResponse> getPrivileges(int page, int limit, String searchKey);
	

}
