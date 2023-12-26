package com.rezene.dynamicprivileges.entity.users;


import com.rezene.dynamicprivileges.shared.audit.Audit;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "role_privileges")
public class RolePrivilege extends Audit {

	private static final long serialVersionUID = -4814904549349101427L;

	@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer id;

	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "role_id")
	    private Role role;

	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "privilege_id")
	    private Privilege privilege;

	    @Column(name = "privilege_id", insertable=false, updatable=false)
	    private Integer privilegeId;
	    
	    @Column(name = "role_id", insertable=false, updatable=false)
	    private Integer roleId;
}
