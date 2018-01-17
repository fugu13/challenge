package com.example.integration;

import com.example.domain.Supplier;
import com.example.domain.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TransactionTest {

    @Autowired
    private MockMvc mockMvc;

    private Utilities utilities;
    private static final Logger logger = LoggerFactory.getLogger(TransactionTest.class);

    @Before
    public void setup() {
        utilities = new Utilities(mockMvc);
    }

    @Test
    public void shouldDisallowId() throws Exception {
        Supplier supplier = utilities.createSupplier(new Supplier("With Id Transaction", "address", null));
        Transaction withIdTransaction = new Transaction("9db6cec0-fae2-11e7-8c3f-9a214bf093ad", supplier, null, "data");
        utilities.performCreateTransaction(withIdTransaction).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldDisallowTimestamp() throws Exception {
        Supplier supplier = utilities.createSupplier(new Supplier("With Timestamp Transaction", "address", null));
        Transaction withTimestampTransaction = new Transaction(null, supplier, new Timestamp(5), "data");
        utilities.performCreateTransaction(withTimestampTransaction).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldDisallowNoData() throws Exception {
        Supplier supplier = utilities.createSupplier(new Supplier("No Data Transaction", "address", null));
        Transaction noDataTransaction = new Transaction(supplier, null);
        utilities.performCreateTransaction(noDataTransaction).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldDisallowNoSupplier() throws Exception {
        utilities.performCreateTransaction(new Transaction(null, "some data")).andExpect(
                status().isBadRequest());
    }

    @Test
    public void shouldDisallowBadSupplier() throws Exception {
        utilities.performCreateTransaction(new Transaction(
                new Supplier("9db6cec0-fae2-11e7-8c3f-9a214bf093ad"), "data!")).andExpect(
                status().isBadRequest());
    }

    @Test
    public void shouldAllowIdOnlySupplier() throws Exception {
        Supplier supplier = utilities.createSupplier(new Supplier("Id Only Name", "address", null));
        utilities.performCreateTransaction(new Transaction(new Supplier(supplier.getId()), "daaaaata")).andExpect(
                status().isCreated());
    }

    @Test
    public void shouldRetrieveInDescendingOrder() throws Exception {
        Supplier supplier = utilities.createSupplier(new Supplier("In order name", "address", null));
        Transaction first = utilities.createTransaction(new Transaction(supplier, "lore"));
        Thread.sleep(100); // ensure different timestamps
        Transaction second = utilities.createTransaction(new Transaction(supplier, "data"));
        Transaction[] ordered = {second, first};

        utilities.performQuery(new Transaction())
                .andExpect(jsonPath("$.transactions[0].id").value(second.getId()))
                .andExpect(jsonPath("$.transactions[1].id").value(first.getId()));
    }

    @Test
    public void shouldPaginate() throws Exception {
        Supplier supplier = utilities.createSupplier(new Supplier("All retrieval name", "address", null));

        int totalCreated = 40;
        for (int ii = 0; ii < totalCreated; ii++) {
            utilities.createTransaction(new Transaction(supplier, "same"));
        }

        Transaction matchingJustThese = new Transaction(supplier, null);

        int retrievePer = 17;
        String basePath = "/transaction/query?count=" + retrievePer;

        int totalRetrieved = 0;

        String currentPath = basePath;
        double retrievalsToFetchAll = Math.ceil(totalCreated * 1.0 / retrievePer);
        for (int pass = 0; pass < retrievalsToFetchAll; pass++) {
            JsonNode results = utilities.retrieveTransactionResults(currentPath, matchingJustThese);
            int numberRetrieved = results.get("transactions").size();
            boolean finalPass = pass + 1 == retrievalsToFetchAll;
            assertTrue("Wrong number retrieved this pass!",
                    finalPass || numberRetrieved == retrievePer);

            totalRetrieved += numberRetrieved;
            currentPath = nextPathFromResults(basePath, results);
        }
        assertEquals("Did not retrieve all!", totalCreated, totalRetrieved);
    }

    private String nextPathFromResults(String basePath, JsonNode results) {
        return basePath + "&starting_timestamp=" + results.get("next_timestamp").asText()
                + "&starting_id=" + results.get("next_id").asText();
    }

    @Test
    public void shouldFilterQueries() throws Exception {
        Supplier one = utilities.createSupplier(new Supplier("filter one", "address", null));
        Supplier three = utilities.createSupplier(new Supplier("filter three", "address", null));

        Transaction first = utilities.createTransaction(new Transaction(one, "filter four"));
        for (int n = 0; n < 3; n++) {
            utilities.createTransaction(new Transaction(three, "filter four"));
        }

        // one has supplier "one"
        queryAndExpectNTransactions(new Transaction(one, null), 1);

        // three have supplier "three"
        queryAndExpectNTransactions(new Transaction(three, null), 3);

        // four have data "filter four"
        queryAndExpectNTransactions(new Transaction(null, "filter four"), 4);

        // three have supplier "three" and data "filter four"
        queryAndExpectNTransactions(new Transaction(three, "filter four"), 3);

        // none have the ID of the one sent with supplier one and the supplier "three"
        queryAndExpectNTransactions(new Transaction(first.getId(), three, null, null), 0);

        // one has the ID of the one sent with supplier one and the supplier "one"
        queryAndExpectNTransactions(new Transaction(first.getId(), one, null, null), 1);

        // one has the timestamp of the one sent with supplier one and the supplier "one"
        queryAndExpectNTransactions(new Transaction(null, one, first.getCreated(), null), 1);

    }

    private void queryAndExpectNTransactions(Transaction filter, int n) throws Exception {
        utilities.performQuery(filter).andExpect(jsonPath("$.transactions").value(hasSize(n)));
    }
}