package com.repository;

import com.enums.UserStatus;
import com.model.Role;
import com.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Integer> {

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);

  boolean existsByRole(Role role);


  List<Users> findByRole(Role role);

  List<Users> findByRoleAndUserStatus(Role role, UserStatus userStatus);

  Users findByUsername(String username);

  Optional<Users> findById(Integer id);
}
