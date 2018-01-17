package com.example.data;

import com.example.data.exceptions.SupplierMissingViolation;
import com.example.domain.Transaction;
import com.example.mapper.TransactionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

/**
 * Supplier DAO
 */
@Service
public class TransactionData {

    private final static Logger logger = LoggerFactory.getLogger(TransactionData.class);

    final private TransactionMapper transactionMapper;

    /**
     * Constructor taking the myBatis mapper
     *
     * @param transactionMapper
     */
    public TransactionData(TransactionMapper transactionMapper) {
        this.transactionMapper = transactionMapper;
    }

    /**
     * Create a new transaction. ID and Created are generated.
     *
     * @param transaction to create
     * @return new Transaction
     * @throws Exception
     */
    public Transaction createTransaction(Transaction transaction) throws Exception {
        transaction.generateId();
        try {
            transactionMapper.createTransaction(transaction);
        } catch (DataIntegrityViolationException e) {
            throw new SupplierMissingViolation("No supplier found", null);
        }
        return transaction;
    }

    /**
     * Filter transactions by example (and paginated)
     *
     * All transaction values may be used to filter, along with Supplier.id and Supplier.name. Matches are exact.
     *
     * @param transaction filter by example
     * @param count how many to retrieve at most
     * @param starting_timestamp timestamp to begin retrieving at (descending)
     * @param starting_id within timestamp, id to retrieve from
     * @return matching transactions
     * @throws Exception
     */
    public List<Transaction> queryTransactions(Transaction transaction, Integer count, Timestamp starting_timestamp, String starting_id) throws Exception {
        return transactionMapper.queryTransactions(new PagedTransaction(transaction, count, starting_timestamp, starting_id));
    }
}
