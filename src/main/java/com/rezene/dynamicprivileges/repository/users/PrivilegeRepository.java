package com.rezene.dynamicprivileges.repository.users;

import java.util.List;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.rezene.dynamicprivileges.entity.users.Privilege;


@Repository
public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {
	Privilege findByPrivilegeName(String privilegeName);
	  @Query("SELECT p.privilegeName FROM Privilege p INNER JOIN RolePrivilege rp ON p.id = rp.privilegeId WHERE rp.roleId = :roleId Order by p.privilegeCategory ASC")
	    List<String> findPrivilegeNamesByRoleId(@Param("roleId") Integer roleId);
	  
	  @Query("SELECT p.privilegeName, p.privilegeCategory FROM Privilege p INNER JOIN RolePrivilege rp ON p.id = rp.privilegeId WHERE rp.roleId = :roleId Order by p.privilegeCategory ASC")
	    List<String> findPrivilegeNamesAndCategoryByRoleId(@Param("roleId") Integer roleId);

	boolean existsByPrivilegeName(String privilegeName);

	boolean existsByPrivilegeDescription(String privilegeDescription);

	Privilege findByPrivilegeUuid(String privilegeString);
	
	List<Privilege> findAllByPrivilegeName(String privilegeName);
	List<Privilege> findAllByPrivilegeDescription(String privilegeDescription);
																								
	Page<Privilege> findAllByPrivilegeNameContainingOrPrivilegeDescriptionContainingOrPrivilegeCategoryContaining(
			String search, String search2, String search3, Pageable pageRequest);



}
