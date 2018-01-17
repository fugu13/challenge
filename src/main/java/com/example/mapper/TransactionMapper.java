package com.example.mapper;

import com.example.data.PagedTransaction;
import com.example.domain.Transaction;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TransactionMapper {
    Transaction getTransaction(String id) throws Exception;
    void createTransaction(Transaction transaction) throws Exception;
    List<Transaction> queryTransactions(PagedTransaction paged) throws Exception;
}
