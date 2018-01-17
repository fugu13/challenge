package com.example.domain;

import com.example.data.validators.CheckForId;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Transaction domain object
 * <p>
 * Also used in mockMvc integration tests.
 */
public class Transaction {

    @Null(message = "You may not specify id")
    private String id;

    @NotNull(message = "You must specify a supplier id")
    @CheckForId
    private Supplier supplier;

    @Null(message = "You may not specify created")
    private Timestamp created;

    @NotNull(message = "Content cannot be missing")
    @Size(min = 2, max = 2000, message = "Content must be between 2 and 2000 characters")
    private String content;

    public Transaction() {}

    public Transaction(String id, Supplier supplier, Timestamp created, String content) {
        this.setId(id);
        this.setSupplier(supplier);
        this.setCreated(created);
        this.setContent(content);
    }

    public Transaction(Supplier supplier, String content) {
        this(null, supplier, null, content);
    }

    public Transaction(String id) {
        this(id, null, null, null);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void generateId() {
        this.setId(UUID.randomUUID().toString());
    }
}
