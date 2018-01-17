package com.example.integration;

import com.example.domain.Supplier;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SupplierTest {

    @Autowired
    private MockMvc mockMvc;

    private Utilities utilities;

    @Before
    public void setup() {
        utilities = new Utilities(mockMvc);
    }

    @Test
    public void shouldReturnOnCreate() throws Exception {
        Supplier supplier = new Supplier("name~~", "address...", "contact...");
        utilities.performCreateSupplier(supplier).andExpect(
                status().isCreated()
        ).andExpect(
                content().string(containsString("\"id\""))
        );
    }

    @Test
    public void shouldAllowNoContact() throws Exception {
        Supplier supplier = new Supplier("No contact", "addy", null);
        utilities.performCreateSupplier(supplier).andExpect(status().isCreated());
    }

    @Test
    public void shouldDisallowDuplicateNames() throws Exception {
        Supplier first = new Supplier("Duplicate Name", "addy", null);
        Supplier second = new Supplier(first.getName(), "another addy", "contact");
        utilities.performCreateSupplier(first).andExpect(status().isCreated());
        utilities.performCreateSupplier(second).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldRequireNameAndAddress() throws Exception {
        Supplier[] bad = {
                new Supplier(null, null, null),
                new Supplier(null, null, "contact"),
                new Supplier("just name", null, null),
                new Supplier(null, "just address", null),
                new Supplier(null, "address and contact", "address and contact"),
                new Supplier("name and contact", null, "name and contact")
        };
        for (Supplier supplier : bad) {
            utilities.performCreateSupplier(supplier).andExpect(status().isBadRequest());
        }
    }

    @Test
    public void shouldNotOverwriteIdOnCreate() throws Exception {
        Supplier created = utilities.createSupplier(new Supplier("Overwrite Name", "address", null));
        Supplier duplicateId = new Supplier(created.getId(), "Overwritten Name", "adddddddd",
                "contactttttt");
        utilities.performCreateSupplier(duplicateId).andExpect(content().string(not(containsString(created.getId()))));
    }

    @Test
    public void updateShouldOnlyUpdate() throws Exception {
        Supplier updateOnly = new Supplier("9db6cec0-fae2-11e7-8c3f-9a214cf092ae", "Update Only",
                "adddddddd", "contactttttt");
        utilities.performSupplierUpdate(updateOnly).andExpect(status().isNotFound());
    }

    @Test
    public void shouldGetAfterCreate() throws Exception {
        Supplier supplier = new Supplier("name...", "address...", "contact...");
        Supplier returned = utilities.createSupplier(supplier);
        utilities.performGet(returned).andExpect(
                status().isOk()
        ).andExpect(
                content().json(utilities.jsonString(returned))
        );
    }


    @Test
    public void shouldUpdate() throws Exception {
        Supplier first = new Supplier("name1", "address1", "contact1");
        Supplier returned = utilities.createSupplier(first);
        Supplier second = new Supplier(returned.getId(), "name2", null, "contact2");
        Supplier expected = new Supplier(second.getId(), second.getName(), first.getAddress(), second.getContact());
        utilities.performSupplierUpdate(second).andExpect(status().isOk()).andExpect(
                content().json(utilities.jsonString(expected)));
    }


}