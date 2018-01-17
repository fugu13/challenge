package com.example.data;

import com.example.domain.Transaction;

import java.sql.Timestamp;


/**
 * Convenience data structure to pass to MyBatis for Transaction queries
 */
public class PagedTransaction {
    private final Transaction transaction;
    private final Integer count;
    private final Timestamp starting_timestamp;
    private final String starting_id;

    public PagedTransaction(Transaction transaction, Integer count, Timestamp starting_timestamp, String starting_id) {
        this.transaction = transaction;
        this.count = count;
        this.starting_timestamp = starting_timestamp;
        this.starting_id = starting_id;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public Integer getCount() {
        return count;
    }

    public Timestamp getStarting_timestamp() {
        return starting_timestamp;
    }

    public String getStarting_id() {
        return starting_id;
    }
}
