package com.payMyBuddy.repository;

import com.payMyBuddy.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

    boolean existsByNameAndUser_Id(String name, Integer userId);

    Set<Account> findByUserId(Integer userId);
}