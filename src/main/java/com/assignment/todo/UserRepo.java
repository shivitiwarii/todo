package com.assignment.todo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByUsernameAndPassword(String username, String password);
  }