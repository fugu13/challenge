package com.example.integration;


import com.example.domain.Supplier;
import com.example.domain.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

public class Utilities {

    private final MockMvc mockMvc;
    public final ObjectMapper objectMapper = new ObjectMapper();

    Utilities(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    ResultActions performGet(Supplier supplier) throws Exception {
        return mockMvc.perform(get("/supplier/" + supplier.getId()));
    }

    String jsonString(Object object) throws java.io.IOException {
        return objectMapper.writeValueAsString(object);
    }

    ResultActions performSupplierUpdate(Supplier supplier) throws Exception {
        return mockMvc.perform(makeSupplierUpdateRequest(supplier));
    }

    MockHttpServletRequestBuilder makeSupplierUpdateRequest(Supplier supplier) throws java.io.IOException {
        return post("/supplier/" + supplier.getId()).contentType(
                MediaType.APPLICATION_JSON).content(jsonString(supplier));
    }

    Supplier createSupplier(Supplier supplier) throws Exception {
        return objectMapper.readValue(performCreate("/supplier", supplier).andReturn().getResponse().getContentAsByteArray(),
                Supplier.class);
    }

    Transaction createTransaction(Transaction transaction) throws Exception {
        ResultActions perform = performCreate("/transaction", transaction);
        return objectMapper.readValue(perform.andReturn().getResponse().getContentAsByteArray(),
                Transaction.class);
    }

    ResultActions performCreate(String path, Object object) throws Exception {
        MockHttpServletRequestBuilder request = post(path).contentType(
                MediaType.APPLICATION_JSON).content(jsonString(object));
        return mockMvc.perform(request);
    }

    public ResultActions performCreateSupplier(Supplier supplier) throws Exception {
        return performCreate("/supplier", supplier);
    }

    public ResultActions performCreateTransaction(Transaction transaction) throws Exception {
        return performCreate("/transaction", transaction);
    }

    public ResultActions performQuery(Transaction transaction) throws Exception {
        return performCreate("/transaction/query", transaction);
    }

    JsonNode retrieveTransactionResults(String path, Transaction filter) throws Exception {
        ResultActions query = performCreate(path, filter);
        byte[] resultsAsByteArray = query.andReturn().getResponse().getContentAsByteArray();
        return objectMapper.readTree(resultsAsByteArray);
    }
}
