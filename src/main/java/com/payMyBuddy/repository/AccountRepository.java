package com.payMyBuddy.repository;

import com.payMyBuddy.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

    Optional<Object> findByName(String name);

    boolean existsByName(String name);
}