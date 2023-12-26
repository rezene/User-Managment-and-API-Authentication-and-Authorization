package com.rezene.dynamicprivileges.entity.users;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.rezene.dynamicprivileges.shared.audit.Audit;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles", uniqueConstraints = { @UniqueConstraint(columnNames = "roleName"),
		@UniqueConstraint(columnNames = "roleUuid") })
public class Role extends Audit {

	private static final long serialVersionUID = 4768448303484614360L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotBlank
	@Size(max = 50)
	private String roleName;

	@NotBlank
	@Size(max = 100)
	private String roleDescription;

	@Size(min = 36, max = 40)
	private String roleUuid = UUID.randomUUID().toString();

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "role_privileges", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "privilege_id"))

	private Set<Privilege> privileges = new HashSet<>();

	public Role(String roleName, String roleDescription) {
		this.roleName = roleName;
		this.roleDescription = roleDescription;

	}

}
