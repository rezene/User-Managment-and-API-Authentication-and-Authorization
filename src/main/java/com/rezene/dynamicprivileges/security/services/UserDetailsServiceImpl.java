package com.rezene.dynamicprivileges.security.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rezene.dynamicprivileges.entity.users.User;
import com.rezene.dynamicprivileges.repository.users.PrivilegeRepository;
import com.rezene.dynamicprivileges.repository.users.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  @Autowired
  UserRepository userRepository;
  
  @Autowired
  PrivilegeRepository privilegeRepository;
  

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
	 
	  
    User user = userRepository.findByEmail(email);
    if (user==null)new UsernameNotFoundException("User Not Found with email: " + email);
    
    List<String> privilegesForRole = privilegeRepository.findPrivilegeNamesByRoleId(user.getRoleId());
   

    privilegesForRole = privilegesForRole.stream().map(p -> "ROLE_" + p).collect(Collectors.toList());
    return UserDetailsImpl.build(user,privilegesForRole);
  }

}
