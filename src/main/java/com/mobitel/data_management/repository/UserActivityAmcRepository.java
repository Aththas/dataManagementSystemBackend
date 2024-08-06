package com.mobitel.data_management.repository;

import com.mobitel.data_management.auth.entity.user.User;
import com.mobitel.data_management.entity.Amc;
import com.mobitel.data_management.entity.Po;
import com.mobitel.data_management.entity.UserActivityAmc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserActivityAmcRepository extends JpaRepository<UserActivityAmc,Integer> {
    Page<UserActivityAmc> findAll(Pageable pageable);

    Page<UserActivityAmc> findAllByUserId(Integer userId, Pageable pageable);
    List<UserActivityAmc> findAllByUserId(Integer userId);
    @Query("SELECT MAX(id) FROM UserActivityAmc")
    Integer findLastId();

    @Query("SELECT uaa FROM UserActivityAmc uaa WHERE uaa.user IN :users")
    Page<UserActivityAmc> findAllByUser(List<User> users, Pageable pageable);

    @Query("SELECT uaa FROM UserActivityAmc uaa WHERE uaa.user IN :users")
    List<UserActivityAmc> findAllByUser(List<User> users);
}
