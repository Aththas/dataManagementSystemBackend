package com.mobitel.data_management.repository;

import com.mobitel.data_management.entity.Amc;
import com.mobitel.data_management.entity.Po;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PoRepository extends JpaRepository<Po,Integer> {
    Optional<Po> findByPoNumber(long poNumber);

    Page<Po> findAll(Pageable pageable);

    List<Po> findAllByOrderById();

    Page<Po> findAllByUserId(Integer userId, Pageable pageable);
    List<Po> findAllByUserId(Integer userId);
}
