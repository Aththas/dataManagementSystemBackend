package com.mobitel.data_management.auth.repository;

import com.mobitel.data_management.auth.entity.user.AccessRequest;
import com.mobitel.data_management.auth.entity.user.UserGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccessRequestRepository extends JpaRepository<AccessRequest,Integer> {
    Optional<AccessRequest> findByRequesterIdAndGrpName(Integer requesterId, String grpName);

    Page<AccessRequest> findAllByRequesterId(Integer requesterId, Pageable pageable);
    List<AccessRequest> findAllByRequesterId(Integer requesterId);

    Page<AccessRequest> findAllByGrpOwnerId(Integer grpOwnerId, Pageable pageable);
    List<AccessRequest> findAllByGrpOwnerId(Integer grpOwnerId);
}
