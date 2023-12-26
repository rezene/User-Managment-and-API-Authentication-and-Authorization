package com.rezene.dynamicprivileges.services.impl.users;

import java.util.ArrayList;
import java.util.List;

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
import com.rezene.dynamicprivileges.repository.users.PrivilegeRepository;
import com.rezene.dynamicprivileges.services.users.PrivilegeService;
import com.rezene.dynamicprivileges.ui.request.users.PrivilegeRequest;
import com.rezene.dynamicprivileges.ui.response.MessageResponse;
import com.rezene.dynamicprivileges.ui.response.users.PrivilegeResponse;

@Service
public class PrivilegeServiceImpl implements PrivilegeService {

	@Autowired
	PrivilegeRepository privilegeRepository;

	@Override
	public PrivilegeResponse createPrivilege(PrivilegeRequest privilege) {

		 if (privilegeRepository.existsByPrivilegeName(privilege.getPrivilegeName()))
		        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Privilege Name is already registered.");

		if (privilegeRepository.existsByPrivilegeDescription(privilege.getPrivilegeDescription()))
		  throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: Privilege description is already registered.");

		Privilege priv = new Privilege();
		BeanUtils.copyProperties(privilege, priv);
		PrivilegeResponse privResponse = new PrivilegeResponse();
		BeanUtils.copyProperties(priv, privResponse);
		privilegeRepository.save(priv);

		return privResponse;

	}

	@Override
	public PrivilegeResponse updatePrivilege(String privilegeUuid, PrivilegeRequest privilegeRequest) {
		Privilege privilege = privilegeRepository.findByPrivilegeUuid(privilegeUuid);
		List<Privilege> privilegeName = privilegeRepository.findAllByPrivilegeName(privilegeRequest.getPrivilegeName());
		List<Privilege> privDesc = privilegeRepository
				.findAllByPrivilegeDescription(privilegeRequest.getPrivilegeDescription());

		if (privilege == null)
			throw new RuntimeException("Privilege not found.");
		if (privDesc.size() >= 2 || privilegeName.size() >= 2)
		 throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: Privilege  name or description is exist. Update privilege to have unique name and description.");

		privilege.setPrivilegeName(privilegeRequest.getPrivilegeName());
		privilege.setPrivilegeDescription(privilegeRequest.getPrivilegeDescription());
		privilege.setPrivilegeCategory(privilegeRequest.getPrivilegeCategory());

		privilegeRepository.save(privilege);
		PrivilegeResponse privResponse = new PrivilegeResponse();
		BeanUtils.copyProperties(privilege, privResponse);
		return privResponse;
	}

	@Override
	public ResponseEntity<?> deletePrivilege(String privilegeString) {
		Privilege privilege = privilegeRepository.findByPrivilegeUuid(privilegeString);
		if (privilege == null)
			 throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: Privilege not found.");

		privilegeRepository.delete(privilege);
		return ResponseEntity.ok(new MessageResponse("Privilege deleted permanently!"));
	}

	@Override
	public PrivilegeResponse getPrivilege(String privilegeUuid) {
		Privilege priv = privilegeRepository.findByPrivilegeUuid(privilegeUuid);
		if (priv == null)
			 throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: \"PrivilegeUuid not found.");
		PrivilegeResponse privResponse = new PrivilegeResponse();
		privResponse.setPrivilegeUuid(priv.getPrivilegeUuid());
		privResponse.setPrivilegeName(priv.getPrivilegeName());
		privResponse.setPrivilegeDescription(priv.getPrivilegeDescription());
		return privResponse;
	}

	@Override
	public List<PrivilegeResponse> getPrivileges(int page, int limit, String search) {
		if (page > 0)
			page = page - 1;
		Pageable pageRequest = PageRequest.of(page, limit, Sort.by("id").descending());
		Page<Privilege> privilegesPage;
		if (search != null)
			privilegesPage = privilegeRepository
					.findAllByPrivilegeNameContainingOrPrivilegeDescriptionContainingOrPrivilegeCategoryContaining(
							search, search, search, pageRequest);
		else
			privilegesPage = privilegeRepository.findAll(pageRequest);

		long totalPages = privilegesPage.getTotalPages();
		List<Privilege> privilegeList = privilegesPage.getContent();
		List<PrivilegeResponse> privResponse = new ArrayList<>();
		for (Privilege p : privilegeList) {
			PrivilegeResponse pr = new PrivilegeResponse();
			if (privResponse.size() == 0)
				pr.setTotalPages(totalPages);
			BeanUtils.copyProperties(p, pr);
			privResponse.add(pr);
		}
		return privResponse;
	}

}
