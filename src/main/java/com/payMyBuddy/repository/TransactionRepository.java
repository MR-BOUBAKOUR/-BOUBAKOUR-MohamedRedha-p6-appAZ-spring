package com.payMyBuddy.repository;

import com.payMyBuddy.model.Account;
import com.payMyBuddy.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    List<Transaction> findBySenderAccount_User_IdOrReceiverAccount_User_Id(Integer currentUserId, Integer currentUserId1);
}