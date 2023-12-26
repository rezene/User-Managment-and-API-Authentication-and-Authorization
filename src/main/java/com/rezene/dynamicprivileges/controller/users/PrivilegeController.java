package com.rezene.dynamicprivileges.controller.users;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rezene.dynamicprivileges.services.users.PrivilegeService;
import com.rezene.dynamicprivileges.ui.request.users.PrivilegeRequest;
import com.rezene.dynamicprivileges.ui.response.users.PrivilegeResponse;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users/privilege")
@SecurityRequirement(name = "bearerAuth")
public class PrivilegeController {
	
	 @Autowired
	  AuthenticationManager authenticationManager;

	  
	  @Autowired
	  PrivilegeService privilegeService;
	  
	  @PostMapping
	  @PreAuthorize("hasRole('Create-Privilege')")
	  public PrivilegeResponse createPrivilege(@Valid @RequestBody PrivilegeRequest privilegeRequest) {
		  return privilegeService.createPrivilege(privilegeRequest);
	    
	  }
	  
	  @GetMapping(path="/{privilegeUuid}")
	  @PreAuthorize("hasRole('Read-Privilege')")
	  public PrivilegeResponse getRole(@PathVariable String privilegeUuid) {
		  return privilegeService.getPrivilege(privilegeUuid);
	  }

	@GetMapping("/list")
	@PreAuthorize("hasRole('Read-Privileges')")
	public List<PrivilegeResponse> getPrivileges(@RequestParam(name="search", required=false) String searchKey, @RequestParam(value="page", defaultValue = "1") int page,
												 @RequestParam(value="limit", defaultValue = "500") int limit) {
		return privilegeService.getPrivileges(page,limit, searchKey);
	}
	  @PutMapping(path="/{privilegeUuid}")
	  @PreAuthorize("hasRole('Update-Privilege')")
	  public PrivilegeResponse updatePrivilege(@PathVariable String privilegeUuid, @Valid @RequestBody PrivilegeRequest privilegeRequest) {
		  return privilegeService.updatePrivilege(privilegeUuid, privilegeRequest);
	    
	  }
	  
	  @DeleteMapping(path="/{privilegeUuid}")
	  @PreAuthorize("hasRole('Delete-Privilege')")
	  public ResponseEntity<?> deletePrivilege(@PathVariable String privilegeUuid) {
		  return privilegeService.deletePrivilege(privilegeUuid);
	    
	  }




}
