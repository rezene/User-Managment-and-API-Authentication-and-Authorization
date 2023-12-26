package com.rezene.dynamicprivileges.services.impl.users;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.rezene.dynamicprivileges.entity.users.Privilege;
import com.rezene.dynamicprivileges.entity.users.Role;
import com.rezene.dynamicprivileges.repository.users.PrivilegeRepository;
import com.rezene.dynamicprivileges.repository.users.RoleRepository;
import com.rezene.dynamicprivileges.services.users.RoleService;
import com.rezene.dynamicprivileges.ui.request.users.AddRolePrivilegesRequest;
import com.rezene.dynamicprivileges.ui.request.users.RoleRequest;
import com.rezene.dynamicprivileges.ui.response.MessageResponse;
import com.rezene.dynamicprivileges.ui.response.users.RoleResponse;

import jakarta.validation.Valid;


@Service
public class RoleServiceImpl implements RoleService {
	
	  @Autowired
	  RoleRepository roleRepository;
	  @Autowired
	  PrivilegeRepository privilegeRepository;
	  
	@Override
	public Role createRole(RoleRequest roleRequest) {
		  if (roleRepository.existsByRoleName(roleRequest.getRoleName())) 
			  throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error:  Role Name is already existed.");
		         
		   
		    if (roleRepository.existsByRoleDescription(roleRequest.getRoleDescription())) 
		    	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error:  Role description is already existed.");

		    Role role = new Role(roleRequest.getRoleName(), roleRequest.getRoleDescription());
		   
		    String [] privilegeNames = roleRequest.getPrivileges();
		    Set<Privilege> privileges = new HashSet<>();
		    
		    for (String privilegeName : privilegeNames) {
		    	Privilege rolePrivilege = privilegeRepository.findByPrivilegeName(privilegeName);
		    	if (rolePrivilege ==null) {
		    		new RuntimeException("Error: Privilege is not found in database.");
		    	}
		    		privileges.add(rolePrivilege);
		    }
		    
		    role.setPrivileges(privileges);
		    roleRepository.save(role);
		   return role;
	}


	@Override
	public  Role  updateRole(String roleUuid, RoleRequest roleUpdateRequest) {
		 Role role = roleRepository.findByRoleUuid(roleUuid);
		 List<Role> roleName = roleRepository.findAllByRoleName(roleUpdateRequest.getRoleName());
		 List<Role> roleDesc = roleRepository.findAllByRoleDescription(roleUpdateRequest.getRoleDescription());
			
		 if(role == null) 
			 new RuntimeException("Error: role is not found in database.");
		 if (roleName.size() >=2  ||  roleDesc.size() >=2)
			 throw new RuntimeException("Role by the role name or role description is exist. Update a role to have unique name and description");
		
		role.setRoleName(roleUpdateRequest.getRoleName());
		role.setRoleDescription(roleUpdateRequest.getRoleDescription());
		
		String [] privilegeNames = roleUpdateRequest.getPrivileges();
	    Set<Privilege> privileges =  role.getPrivileges(); 
	    
	    for (String privilegeName : privilegeNames) {
	    	Privilege rolePrivilege = privilegeRepository.findByPrivilegeName(privilegeName);
	    	if (rolePrivilege == null) {
	    	    throw new RuntimeException("Error: Privilege is not found in database.");
	    	}
	    		privileges.add(rolePrivilege);
	    }
	    
	    role.setPrivileges(privileges);
	
		 roleRepository.save(role);
		 return role;
	}
	


	@Override
	public ResponseEntity<?> deleteRole(String roleUuid) {
	Role role = roleRepository.findByRoleUuid(roleUuid);
		 if(role == null) 
			throw new RuntimeException("Role not found.");
		 roleRepository.delete(role);
	 return ResponseEntity.ok(new MessageResponse("Role deleted permanently!"));
	
	}
	


	@Override
	public RoleResponse getRole(String roleUuid) {
		Role role= roleRepository.findByRoleUuid(roleUuid);
		    if(role == null) 
				throw new RuntimeException("roleUuid not found.");
		    RoleResponse roleResponse = new RoleResponse();
		    List<String> rolePrivilege = privilegeRepository.findPrivilegeNamesByRoleId(role.getId());
		    BeanUtils.copyProperties(role, roleResponse);
		    roleResponse.setPrivileges(rolePrivilege);
		    
		    return roleResponse;
	}


	@Override
	public List<RoleResponse> getRoles(int page, int limit) {
		if(page > 0) page = page - 1;
		Pageable pageRequest = PageRequest.of(page, limit, Sort.by("id").descending());
		Page<Role> role = roleRepository.findAll(pageRequest);
		long totalPages = role.getTotalPages();
		List<Role>  roleList = role.getContent();

		List<RoleResponse> roleResponse = new ArrayList<>();
		for (Role r : roleList) {
			List<String> rolePrivilege = privilegeRepository.findPrivilegeNamesAndCategoryByRoleId(r.getId());
			RoleResponse rr = new RoleResponse();
			if(roleResponse.size()==0)
				rr.setTotalPages(totalPages);
			BeanUtils.copyProperties(r, rr);
			rr.setPrivileges(rolePrivilege);
			roleResponse.add(rr);
		}
		return roleResponse;
	}


	@Override
	public ResponseEntity<?> addRolePrivileges(String roleUuid, @Valid AddRolePrivilegesRequest rolePrivilegesRequest) {
		 Role role = roleRepository.findByRoleUuid(roleUuid);
		 if(role == null) 
				throw new RuntimeException("Role not found.");
		 String [] privilegeNames = rolePrivilegesRequest.getPrivileges();
		    Set<Privilege> privileges =  role.getPrivileges(); 
		    
		    for (String privilegeName : privilegeNames) {
		    	//System.out.println("-------------------"+privilegeName);
		    	Privilege rolePrivilege = privilegeRepository.findByPrivilegeName(privilegeName);
		    	if (rolePrivilege == null) {
		    	    throw new RuntimeException("Error: Privilege is not found in database.");
		    	}
		    		privileges.add(rolePrivilege);
		    }
		    
		    role.setPrivileges(privileges);
		    roleRepository.save(role);
		 return ResponseEntity.ok(new MessageResponse("Privileges added to Role successfully!"));
	}


	@Override
	public ResponseEntity<?> deleteRolePrivileges(String roleUuid,
			@Valid AddRolePrivilegesRequest rolePrivilegesRequest) {
		Role role = roleRepository.findByRoleUuid(roleUuid);
		if(role == null) 
			throw new RuntimeException("Role not found.");
		
		 String [] privilegeNames = rolePrivilegesRequest.getPrivileges();
		    Set<Privilege> privileges =  role.getPrivileges(); 
		    Set<Privilege> deletedPrivileges =  new HashSet<>();
		    Set<Privilege> filteredPrivileges =  new HashSet<>(privileges);
		    
		    for (String privilegeName : privilegeNames) {
		    	Privilege rolePrivilege = privilegeRepository.findByPrivilegeName(privilegeName);
		    	if (rolePrivilege == null) {
		    	    throw new RuntimeException("Error: Privilege is not found in database.");
		    	}
		    	deletedPrivileges.add(rolePrivilege);
		    }
		    
		    filteredPrivileges.removeAll(deletedPrivileges);  
		    role.setPrivileges(filteredPrivileges);
		    roleRepository.save(role);
		 return ResponseEntity.ok(new MessageResponse("Privileges deleted from a Role successfully!"));
	}

	@Override
	public List<RoleResponse> searchRoles(String searchTerm, int page, int limit) {

		if(page > 0) page = page - 1;

		Pageable pageRequest = PageRequest.of(page, limit, Sort.by("id").descending());

		Page<Role> rolesPage = roleRepository.findByRoleNameContainingOrRoleDescriptionContaining(searchTerm,searchTerm, pageRequest);


		int totalPages = rolesPage.getTotalPages();
		List<Role> roleList = rolesPage.getContent();
		List<RoleResponse> roleResponse = new ArrayList<>();
		for (Role r : roleList) {
			RoleResponse rr = new RoleResponse();
			if(roleResponse.size() == 0)
				rr.setTotalPages(totalPages);
			BeanUtils.copyProperties(r, rr);
			roleResponse.add(rr);
		}

		return roleResponse;
	}
	
	

}
