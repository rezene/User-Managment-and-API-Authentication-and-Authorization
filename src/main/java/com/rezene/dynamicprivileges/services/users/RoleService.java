package com.rezene.dynamicprivileges.services.users;

import java.util.List;


import org.springframework.http.ResponseEntity;

import com.rezene.dynamicprivileges.entity.users.Role;
import com.rezene.dynamicprivileges.ui.request.users.AddRolePrivilegesRequest;
import com.rezene.dynamicprivileges.ui.request.users.RoleRequest;
import com.rezene.dynamicprivileges.ui.response.users.RoleResponse;

import jakarta.validation.Valid;

public interface RoleService {
public Role createRole(RoleRequest roleRequest);
public Role updateRole(String roleUuid, @Valid RoleRequest roleUpdateRequest);
public ResponseEntity<?> deleteRole(String roleString);
public RoleResponse getRole(String roleUuid);
    public List<RoleResponse> getRoles(int page, int limit);
public ResponseEntity<?> addRolePrivileges(String roleUuid, @Valid AddRolePrivilegesRequest rolePrivilegesRequest);
public ResponseEntity<?> deleteRolePrivileges(String roleUuid, @Valid AddRolePrivilegesRequest rolePrivilegesRequest);
public List<RoleResponse> searchRoles(String searchKey, int page, int limit);

}
