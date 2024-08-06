package com.mobitel.data_management.repository;

import com.mobitel.data_management.auth.entity.user.User;
import com.mobitel.data_management.entity.Amc;
import com.mobitel.data_management.entity.Po;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT p FROM Po p WHERE p.poNumber = :poNumber AND p.id <> :id")
    List<Po> findPoByPoNumberExcludingCurrentPo(@Param("poNumber") long poNumber, @Param("id") Integer id);

    @Query("SELECT p FROM Po p WHERE p.user IN :users")
    Page<Po> findAllByUser(List<User> users, Pageable pageable);

    @Query("SELECT p FROM Po p WHERE p.user IN :users")
    List<Po> findAllByUser(List<User> users);
}
