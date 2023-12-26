package com.rezene.dynamicprivileges.services.users;

import java.io.IOException;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.rezene.dynamicprivileges.ui.request.users.LoginRequest;
import com.rezene.dynamicprivileges.ui.request.users.ResetPasswordRequest;
import com.rezene.dynamicprivileges.ui.request.users.UploadProfileRequest;
import com.rezene.dynamicprivileges.ui.request.users.UserRequest;
import com.rezene.dynamicprivileges.ui.response.users.UserResponse;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;


public interface UserService {
	
	public UserResponse createUser(UserRequest userRequest);
	public ResponseEntity<?> authenticateUser(LoginRequest userRequest);
	public UserResponse updateUser(String userUuid, UserRequest userRequest);
	public ResponseEntity<?> deleteUser(String userUuid);
	public UserResponse getUser(String userUuid);
	public List<UserResponse> getUsers(int page, int limit);
	public List<UserResponse> getPayerUsers(int page, int limit);
	
	public ResponseEntity<?> resetPassword(ResetPasswordRequest resetPasswordDetail);
	public ResponseEntity<?> changePassword(ResetPasswordRequest resetPasswordDetail, String userUuid);
	public ResponseEntity<?> verifyAccount(String emailVerificationToken);
	public ResponseEntity<?> reSendVerification(String email) throws AddressException, MessagingException, IOException;
	public ResponseEntity<?> sendPasswordResetCode(String email) throws AddressException, MessagingException, IOException;
	public ResponseEntity<?> checkResetCode(ResetPasswordRequest resetPasswordDetail);
	public List<UserResponse> searchUsers(String searchKey, int page, int limit);
	public int loggedInCounter();
	
}
