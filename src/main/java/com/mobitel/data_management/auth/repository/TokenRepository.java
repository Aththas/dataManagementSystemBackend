package com.mobitel.data_management.auth.repository;

import com.mobitel.data_management.auth.entity.token.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token,Integer> {

}
