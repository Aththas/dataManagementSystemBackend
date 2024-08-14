package com.mobitel.data_management.auth.repository;

import com.mobitel.data_management.auth.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.id NOT IN :ids")
    List<User> findUsersNotInIds(List<Integer> ids);

    @Query("SELECT u FROM User u WHERE u.grpName NOT IN :grpNames")
    List<User> findUsersNotInGrps(List<String> grpNames);

    Page<User> findAll(Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.id <> :id")
    List<User> findUsersByEmailExcludingCurrentUser(@Param("email") String email, @Param("id") Integer id);

    @Query("SELECT u FROM User u WHERE u.grpName IN :grpNames")
    List<User> findAllByGroupNames(List<String> grpNames);

    Optional<User> findByGrpName(String grpName);

    List<User> findBySuperUserTrue();
}
