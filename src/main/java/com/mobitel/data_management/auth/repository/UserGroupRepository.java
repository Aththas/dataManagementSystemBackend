package com.mobitel.data_management.auth.repository;

import com.mobitel.data_management.auth.entity.user.UserGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup,Integer> {

    @Transactional
    void deleteByUserIdAndGrpName(Integer userId,String grpName);

    Optional<UserGroup> findByUserIdAndGrpName(Integer userId, String grpName);

    Page<UserGroup> findAllByGrpName(String grpName, Pageable pageable);

    List<UserGroup> findAllByGrpName(String grpName);

}
