package com.example.controller;

import com.example.data.SupplierData;
import com.example.domain.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


/**
 * Handles all the paths and methods under /supplier
 */
@RestController
public class SupplierController {

    final private static Logger logger = LoggerFactory.getLogger(SupplierController.class);

    final private SupplierData supplierData;

    /**
     * Constructor taking our DAO
     *
     * @param supplierData
     */
    public SupplierController(SupplierData supplierData) {
        this.supplierData = supplierData;
    }

    /**
     * Retrieve all suppliers
     *
     * @return all suppliers, sorted by name
     * @throws Exception
     */
    @GetMapping("/supplier")
    public List<Supplier> listSuppliers() throws Exception {
        return supplierData.getSuppliers();
    }

    /**
     * Create a supplier, POST'd as JSON
     * <p>
     * This method is lenient, overwriting any ID that is passed, making it easier to reuse supplier objects.
     *
     * @param supplier supplier to create
     * @return a Supplier, status 201
     * @throws Exception
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/supplier")
    public Supplier createSupplier(@Valid @RequestBody Supplier supplier) throws Exception {
        return supplierData.createSupplier(supplier);
    }

    /**
     * Retrieve a particular supplier by ID
     *
     * @param id supplier's id
     * @return a Supplier
     * @throws Exception
     */
    @GetMapping("/supplier/{id}")
    public Supplier getSupplier(@PathVariable("id") String id) throws Exception {
        return supplierData.getSupplier(id);
    }

    /**
     * Update a supplier
     * <p>
     * Takes a POST'd partial supplier. All fields are optional. All non-null fields replace existing fields on the
     * supplier. If the ID is set incorrectly, it is ignored in favor of the one in the path.
     *
     * @param id       supplier's id
     * @param supplier any new values
     * @return the complete updated supplier
     * @throws Exception
     */
    @PostMapping("/supplier/{id}")
    public Supplier updateSupplier(@PathVariable("id") String id, @RequestBody Supplier supplier) throws Exception {
        // TODO use validator groups to make this check the length attributes while ignoring NotNull
        supplier.setId(id); // Don't allow updating other suppliers.
        return supplierData.updateSupplier(supplier);
    }
}
