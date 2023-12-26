package com.rezene.dynamicprivileges.services.impl.users;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.rezene.dynamicprivileges.entity.users.Role;
import com.rezene.dynamicprivileges.entity.users.User;
import com.rezene.dynamicprivileges.repository.users.RoleRepository;
import com.rezene.dynamicprivileges.repository.users.UserRepository;
import com.rezene.dynamicprivileges.security.jwt.JwtUtils;
import com.rezene.dynamicprivileges.security.services.UserDetailsImpl;
import com.rezene.dynamicprivileges.security.ui.response.JwtResponse;
import com.rezene.dynamicprivileges.services.users.UserService;
import com.rezene.dynamicprivileges.shared.email.SendEmail;
import com.rezene.dynamicprivileges.shared.random.GenerateRandomString;
import com.rezene.dynamicprivileges.ui.request.users.LoginRequest;
import com.rezene.dynamicprivileges.ui.request.users.ResetPasswordRequest;
import com.rezene.dynamicprivileges.ui.request.users.SendEmailRequest;
import com.rezene.dynamicprivileges.ui.request.users.UploadProfileRequest;
import com.rezene.dynamicprivileges.ui.request.users.UserRequest;
import com.rezene.dynamicprivileges.ui.response.MessageResponse;
import com.rezene.dynamicprivileges.ui.response.users.UserResponse;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	private SessionRegistry sessionRegistry;

	@Autowired
	JwtUtils jwtUtils;
	

	@Autowired
	SendEmail sendMailComponent;

	@Autowired
	GenerateRandomString generateRandomString;

	//@Value("${file.upload-dir}")
	//private String uploadDirectory;

	@Value("${app.HostDomain}")
	private String applicationHostDomain;

	@Override
	public UserResponse createUser(UserRequest userRequest) {
		if (userRepository.existsByEmail(userRequest.getEmail())) {
			throw new RuntimeException("Error: Email is already in use!");
		}
		if (userRepository.existsByMobilePhone(userRequest.getMobilePhone())) {
			throw new RuntimeException("Error: Mobile Phone is already in use!");
		}

		User user = new User();
		
		BeanUtils.copyProperties(userRequest, user);
		user.setPassword(encoder.encode(userRequest.getPassword()));
		userRepository.save(user);
		UserResponse userResponse = new UserResponse();
		BeanUtils.copyProperties(user, userResponse);

		return userResponse;
	}

	@Override
	public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
				.collect(Collectors.toList());

		Optional<Role> role = roleRepository.findById(userDetails.getRoleId());
		String roleName = role.orElse(new Role()).getRoleName();

		return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUserUuid(), userDetails.getEmail(), roleName,
				userDetails.getTitle(), userDetails.getFirstName(), userDetails.getFatherName(),
				userDetails.getGrandFatherName(), userDetails.getGender(), userDetails.getMobilePhone(),
				userDetails.getUserStatus(), userDetails.getUserType(), userDetails.getAgencyUuid(),
				userDetails.getInsuranceUuid(), userDetails.getProfilePicture(),

				roles));
	}

	@Override
	public UserResponse updateUser(String userUuid, UserRequest userRequest) {

		User user = userRepository.findByUserUuid(userUuid);

		if (user == null)
			throw new RuntimeException("User not found.");

		user.setFirstName(userRequest.getFirstName());
		user.setFatherName(userRequest.getFatherName());
		user.setGrandFatherName(userRequest.getGrandFatherName());
		user.setMobilePhone(userRequest.getMobilePhone());
		user.setAgencyUuid(userRequest.getAgencyUuid());
		user.setInsuranceUuid(userRequest.getInsuranceUuid());
		user.setRoleId(userRequest.getRoleId());
		userRepository.save(user);

		UserResponse userResponse = new UserResponse();
		BeanUtils.copyProperties(user, userResponse);

		return userResponse;
	}

	@Override
	public ResponseEntity<?> deleteUser(String userUuid) {
		User user = userRepository.findByUserUuid(userUuid);
		if (user == null)
			throw new RuntimeException("User not found.");
		user.setDeleted(true);
		userRepository.save(user);
		return ResponseEntity.ok(new MessageResponse("User soft deleted successfully!"));
	}

	@Override
	public UserResponse getUser(String userUuid) {
		User user = userRepository.findByUserUuid(userUuid);

		Optional<Role> role = roleRepository.findById(user.getRoleId());
		String roleName = role.orElse(new Role()).getRoleName();
		UserResponse userResponse = new UserResponse();
		if (role != null)
			userResponse.setRoleName(roleName);
		BeanUtils.copyProperties(user, userResponse);
		return userResponse;

	}

	@Override
	public List<UserResponse> getPayerUsers(int page, int limit) {
		if (page > 0)
			page = page - 1;

		Pageable pageRequest = PageRequest.of(page, limit, Sort.by("id").descending());
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()
				|| authentication instanceof AnonymousAuthenticationToken) {
			throw new RuntimeException("Login to  get authorized.");
		}

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

		String insuranceUuid = userDetails.getInsuranceUuid();
		Page<User> usersPage = userRepository.findAllByInsuranceUuid(insuranceUuid, pageRequest);
		List<User> userList = usersPage.getContent();
		long totalPages = usersPage.getTotalPages();
		List<UserResponse> userResponse = new ArrayList<>();
		for (User u : userList) {
			UserResponse ur = new UserResponse();
			if (userResponse.size() == 0)
				ur.setTotalPages(totalPages);
			BeanUtils.copyProperties(u, ur);
			userResponse.add(ur);
		}
		return userResponse;

	}

	@Override
	public List<UserResponse> getUsers(int page, int limit) {

		if (page > 0)
			page = page - 1;

		Pageable pageableRequest = PageRequest.of(page, limit, Sort.by("id").descending());
		Page<User> usersPage = userRepository.findAll(pageableRequest);
		int totalPages = usersPage.getTotalPages();
		List<User> userList = usersPage.getContent();
		List<UserResponse> userResponse = new ArrayList<>();
		for (User u : userList) {
			UserResponse ur = new UserResponse();
			Optional<Role> role = roleRepository.findById(u.getRoleId());
			String roleName = role.orElse(new Role()).getRoleName();
			ur.setRoleName(roleName);
			if (userResponse.size() == 0)
				ur.setTotalPages(totalPages);
			BeanUtils.copyProperties(u, ur);
			userResponse.add(ur);
		}

		return userResponse;
	}

	@Override
	public List<UserResponse> searchUsers(String searchKey, int page, int limit) {

		if (page > 0)
			page = page - 1;
		String[] searchKeys = searchKey.split(" ");

		Pageable pageableRequest = PageRequest.of(page, limit, Sort.by("id").descending());
		int countSpaces = StringUtils.countOccurrencesOf(searchKey, " ");

		Page<User> usersPage = null;

		if (countSpaces == 0) {
			usersPage = userRepository
					.findByFirstNameContainingOrFatherNameContainingOrGrandFatherNameContainingOrMobilePhoneContainingOrEmailContainingOrUserStatusContainingOrUserTypeContaining(
							searchKey, searchKey, searchKey, searchKey, searchKey, searchKey, searchKey,
							pageableRequest);
		} else if (countSpaces == 1) {
			String firstName = searchKeys[0];
			String fatherName = searchKeys[1];
			usersPage = userRepository.findByFirstNameContainingAndFatherNameContaining(firstName, fatherName,
					pageableRequest);
		} else if (countSpaces == 2) {
			String firstName = searchKeys[0];
			String fatherName = searchKeys[1];
			String grandFatherName = searchKeys[2];
			usersPage = userRepository.findByFirstNameContainingAndFatherNameContainingAndGrandFatherNameContaining(
					firstName, fatherName, grandFatherName, pageableRequest);
		}

		int totalPages = usersPage.getTotalPages();
		List<User> users = usersPage.getContent();
		List<UserResponse> userResponse = new ArrayList<>();
		for (User u : users) {
			UserResponse ur = new UserResponse();
			Optional<Role> role = roleRepository.findById(u.getRoleId());
			String roleName = role.orElse(new Role()).getRoleName();
			ur.setRoleName(roleName);
			if (userResponse.size() == 0)
				ur.setTotalPages(totalPages);
			BeanUtils.copyProperties(u, ur);
			userResponse.add(ur);
		}

		return userResponse;
	}

	
	@Override
	public ResponseEntity<?> resetPassword(ResetPasswordRequest resetPasswordDetail) {
		String returnValue = "Password not changed";
		User userEntity = userRepository.findByEmailAndPasswordResetCode(resetPasswordDetail.getEmail(),
				resetPasswordDetail.getPasswordResetCode());
		if (userEntity == null)
			throw new RuntimeException("Password reset code not found.");

		userEntity.setPassword(encoder.encode(resetPasswordDetail.getNewPassword()));
		User passworUpdated = userRepository.save(userEntity);
		if (passworUpdated != null) {
			returnValue = "Password changed successfully";
		}
		return ResponseEntity.ok(new MessageResponse(returnValue));
	}

	@Override
	public ResponseEntity<?> changePassword(ResetPasswordRequest resetPasswordDetail, String userUuid) {
		String returnValue = "Password not changed";
		User userEntity = userRepository.findByUserUuid(userUuid);
		if (userEntity == null)
			throw new RuntimeException("User not found.");

		userEntity.setPassword(encoder.encode(resetPasswordDetail.getNewPassword()));
		User passworUpdated = userRepository.save(userEntity);
		if (passworUpdated != null) {
			returnValue = "Password changed successfully";
		}
		return ResponseEntity.ok(new MessageResponse(returnValue));
	}

	@Override
	public ResponseEntity<?> verifyAccount(String emailVerificationToken) {
		String returnValue = "";
		User user = userRepository.findByEmailVerificationToken(emailVerificationToken);
		if (user == null)
			throw new RuntimeException("User not found.");

		String userStatus = "Active";
		user.setUserStatus(userStatus);
		User updatedUser = userRepository.save(user);
		if (updatedUser.getUserStatus() == "Active") {
			returnValue = "Account Verified Successfully";
		}
		return ResponseEntity.ok(new MessageResponse(returnValue));
	}

	@Override
	public ResponseEntity<?> reSendVerification(String email) throws AddressException, MessagingException, IOException {
		User userEntity = userRepository.findByEmail(email);
		if (userEntity == null)
			throw new RuntimeException("User not found.");

		String mailSubject = "ClaimConnect account Verification";
		String mailBody = "<b>Verify your ClaimConnect Account</b><br><br> Follow this link --> <a href='"
				+ applicationHostDomain + "/verifyaccount?verificationToken=" + userEntity.getEmailVerificationToken()
				+ "'><b><i>Click me to Verify</i></b></a><br><br> <span style='color:red; font-size:12px;'> Don't reply to this email !</span>";
		SendEmailRequest sendMail = new SendEmailRequest();
		sendMail.setToAddress(email);
		sendMail.setSubject(mailSubject);
		sendMail.setBody(mailBody);
		String returnValue = sendMailComponent.sendMail(sendMail);
		return ResponseEntity.ok(new MessageResponse(returnValue));

	}

	@Override
	public ResponseEntity<?> sendPasswordResetCode(String email)
			throws AddressException, MessagingException, IOException {

		String resetCode = generateRandomString.generateAccountId(6);

		User userEntity = userRepository.findByEmail(email);
		if (userEntity == null)
			throw new RuntimeException("User not found.");

		userEntity.setPasswordResetCode(resetCode);
		userRepository.save(userEntity);

		String mailSubject = "ClaimConnect account Password Reset";
		String mailBody = "<b>Reset your ClaimConnect Account Password</b><br><br> Enter the code or Follow the link <br> Reset Code : <b>"
				+ resetCode + "</b> <br> <a href='" + applicationHostDomain + "/resetpassword?resetCode=" + resetCode
				+ "'><b><i>Click me to Reset Password</i></b></a><br><br> <span style='color:red; font-size:12px;'> Don't reply to this email !</span>";
		SendEmailRequest sendMail = new SendEmailRequest();
		sendMail.setToAddress(email);
		sendMail.setSubject(mailSubject);
		sendMail.setBody(mailBody);
		String emailStatus = sendMailComponent.sendMail(sendMail);
		return ResponseEntity.ok(new MessageResponse("Password Reset Code sent" + emailStatus));

	}

	@Override
	public ResponseEntity<?> checkResetCode(ResetPasswordRequest resetPasswordDetail) {
		User userEntity = userRepository.findByEmailAndPasswordResetCode(resetPasswordDetail.getEmail(),
				resetPasswordDetail.getPasswordResetCode());
		if (userEntity == null)
			throw new RuntimeException("Invalid Reset Code.");
		return ResponseEntity.ok(new MessageResponse("Reset Code is valid"));

	}

	@Override
	public int loggedInCounter() {
		List<Object> principals = sessionRegistry.getAllPrincipals();
		int numberOfUsers = principals.size();
		return numberOfUsers;
	}

	
}
