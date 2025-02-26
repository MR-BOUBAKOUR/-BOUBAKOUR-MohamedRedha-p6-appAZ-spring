package com.payMyBuddy.repository;

import com.payMyBuddy.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    List<Transaction> findBySenderAccount_User_IdOrReceiverAccount_User_Id(Integer currentUserId, Integer currentUserId1);
}