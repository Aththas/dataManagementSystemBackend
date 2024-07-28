package com.mobitel.data_management.auth.repository;

import com.mobitel.data_management.auth.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    Optional<User> findByEmail(String email);

    List<User> findAllByOrderByIdAsc();
}
