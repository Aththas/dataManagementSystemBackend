package com.mobitel.data_management.repository;

import com.mobitel.data_management.entity.UserActivityAmc;
import com.mobitel.data_management.entity.UserActivityPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserActivityPoRepository extends JpaRepository<UserActivityPo,Integer> {

    Page<UserActivityPo> findAll(Pageable pageable);

    Page<UserActivityPo> findAllByUserId(Integer userId, Pageable pageable);
    List<UserActivityPo> findAllByUserId(Integer userId);

    @Query("SELECT MAX(id) FROM UserActivityPo")
    Integer findLastId();
}
