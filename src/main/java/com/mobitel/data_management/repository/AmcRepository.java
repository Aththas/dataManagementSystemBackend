package com.mobitel.data_management.repository;

import com.mobitel.data_management.auth.entity.user.User;
import com.mobitel.data_management.entity.Amc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AmcRepository extends JpaRepository<Amc,Integer> {
    Optional<Amc> findByContractName(String contractName);
    Page<Amc> findAll(Pageable pageable);

    List<Amc> findAllByOrderById();
    Page<Amc> findAllByUserId(Integer userId,Pageable pageable);
    List<Amc> findAllByUserId(Integer userId);

    @Query("SELECT a FROM Amc a WHERE a.contractName = :contractName AND a.id <> :id")
    List<Amc> findAmcByContractNameExcludingCurrentAmc(@Param("contractName") String contractName, @Param("id") Integer id);


}
