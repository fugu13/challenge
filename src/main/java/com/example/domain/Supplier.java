package com.example.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;


/**
 * Supplier domain object
 * <p>
 * Also used in mockMvc integration tests.
 */
public class Supplier {

    private String id;

    @NotNull(message = "Name cannot be missing")
    @Size(min = 2, max = 200, message = "Name must be between 2 and 200 characters")
    private String name;

    @NotNull(message = "Address cannot be missing")
    @Size(min = 2, max = 200, message = "Address must be between 2 and 200 characters")
    private String address;

    @Size(min = 2, max = 200, message = "Contact must be between 2 and 200 characters")
    private String contact;

    public Supplier() {
    }

    public Supplier(String id, String name, String address, String contact) {
        this.setId(id);
        this.setName(name);
        this.setAddress(address);
        this.setContact(contact);
    }

    public Supplier(String name, String address, String contact) {
        this(null, name, address, contact);
    }

    public Supplier(String id) {
        this(id, null, null, null);
    }

    public void generateId() {
        this.setId(UUID.randomUUID().toString());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
