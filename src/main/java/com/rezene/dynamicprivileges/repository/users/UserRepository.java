package com.rezene.dynamicprivileges.repository.users;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rezene.dynamicprivileges.entity.users.User;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

 Boolean existsByEmail(String email);

User findByEmail(String email);

User findByUserUuid(String userUuid);

void deleteByUserUuid(String userUuid);

Page<User> findAllByInsuranceUuid(String payerUuid,Pageable pageRequest);

Page<User> findAllByAgencyUuid(String providerUuid,Pageable pageRequest);

boolean existsByMobilePhone(String mobilePhone);

User findByEmailAndPasswordResetCode(String email, String passwordResetCode);

User findByEmailVerificationToken(String emailVerificationToken);

Page<User> findByFirstNameContainingOrFatherNameContainingOrGrandFatherNameContainingOrMobilePhoneContainingOrEmailContainingOrUserStatusContaining(
		String searchKey, String searchKey2, String searchKey3, String searchKey4, String searchKey5, String searchKey6,
		Pageable pageableRequest);

Page<User> findByFirstNameContainingAndFatherNameContaining(String firstName, String fatherName,
		Pageable pageableRequest);

Page<User> findByFirstNameContainingAndFatherNameContainingAndGrandFatherNameContaining(String firstName,
		String lastName, String grandFatherName, Pageable pageableRequest);

Page<User> findByFirstNameContainingOrFatherNameContainingOrGrandFatherNameContainingOrMobilePhoneContainingOrEmailContainingOrUserStatusContainingOrUserTypeContaining(
		String searchKey, String searchKey2, String searchKey3, String searchKey4, String searchKey5, String searchKey6,
		String searchKey7, Pageable pageableRequest);



}
