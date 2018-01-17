package com.example.controller;


import com.example.data.TransactionData;
import com.example.domain.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.List;

/**
 * Handles all the paths and methods under /transaction
 */
@RestController
public class TransactionController {
    final private TransactionData transactionData;

    final private static Logger logger = LoggerFactory.getLogger(SupplierController.class);

    /**
     * Constructor taking our DAO
     *
     * @param transactionData
     */
    public TransactionController(TransactionData transactionData) {
        this.transactionData = transactionData;
    }


    /**
     * Create a transaction, POST'd as JSON
     * <p>
     * This method is strict, to help prevent code errors for the more critical, immutable transaction objects.
     * No ID or Timestamp may be specified, but will be generated. The Supplier ID and data must specified. Since
     * Suppliers are handled leniently, specifying the rest of the Supplier is allowed but ignored.
     *
     * @param transaction transaction to create
     * @return a Transaction, status 201
     * @throws Exception
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/transaction")
    public Transaction createTransaction(@Valid @RequestBody Transaction transaction) throws Exception {
        return transactionData.createTransaction(transaction);
    }

    /**
     * Data structure for query responses
     * <p>
     * TODO: move this to HATEOAS (link the next page) when the rest of the API is migrated.
     */
    public static class QueryResponse {
        private List<Transaction> transactions;
        private Timestamp next_timestamp;
        private String next_id;

        public QueryResponse(List<Transaction> transactions, Timestamp next_timestamp, String next_id) {
            this.transactions = transactions;
            this.next_timestamp = next_timestamp;
            this.next_id = next_id;
        }

        public List<Transaction> getTransactions() {
            return transactions;
        }

        public Timestamp getNext_timestamp() {
            return next_timestamp;
        }

        public String getNext_id() {
            return next_id;
        }
    }

    /**
     * Query transactions by example
     * <p>
     * All fields in the transaction may be queried, and both supplier ID and Name may be queried. Matches are exact.
     *
     * @param transaction filter by example
     * @param count       how many retrieved at most?
     * @param timestamp   start from this timestamp (descending)
     * @param marker      within the timestamp, start from this id
     * @return data structure with transactions and next page markers
     * @throws Exception
     */
    @PostMapping("/transaction/query")
    public QueryResponse queryTransactions(@RequestBody Transaction transaction,
                                           @RequestParam(name = "count", defaultValue = "20") Integer count,
                                           @RequestParam(name = "starting_timestamp", required = false)
                                                   Long timestamp,
                                           @RequestParam(name = "starting_id", required = false)
                                                   String marker) throws Exception {

        Timestamp starting = null;
        if (timestamp != null) {
            starting = new Timestamp(timestamp);
        }
        List<Transaction> transactions = transactionData.queryTransactions(transaction,
                count + 1, starting, marker);

        if (transactions.size() == count + 1) {
            Transaction last = transactions.get(count);
            return new QueryResponse(transactions.subList(0, count), last.getCreated(), last.getId());
        } else {
            return new QueryResponse(transactions, null, null);
        }
    }
}
