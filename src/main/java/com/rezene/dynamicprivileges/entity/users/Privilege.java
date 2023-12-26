package com.rezene.dynamicprivileges.entity.users;

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
@Table(name = "privileges", uniqueConstraints = { @UniqueConstraint(columnNames = "privilegeName"),
		@UniqueConstraint(columnNames = "privilegeUuid")

})
public class Privilege extends Audit {

	private static final long serialVersionUID = 2369844719759914085L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Size(min = 36, max = 40)
	private String privilegeUuid = UUID.randomUUID().toString();

	@Column(length = 50)
	private String privilegeName;

	@NotBlank
	@Size(max = 100)
	private String privilegeDescription;

	@NotBlank
	@Column(length = 50)
	private String privilegeCategory;

}