package com.example.data;

import com.example.data.exceptions.SupplierMissingViolation;
import com.example.data.exceptions.UniqueViolation;
import com.example.domain.Supplier;
import com.example.mapper.SupplierMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Supplier DAO
 */
@Service
public class SupplierData {

    private final static Logger logger = LoggerFactory.getLogger(SupplierData.class);

    final private SupplierMapper supplierMapper;

    /**
     * Constructor taking the myBatis mapper
     *
     * @param supplierMapper
     */
    public SupplierData(SupplierMapper supplierMapper) {
        this.supplierMapper = supplierMapper;
    }

    /**
     * Retrieve all suppliers, ordered by name
     *
     * @return all suppliers
     * @throws Exception
     */
    public List<Supplier> getSuppliers() throws Exception {
        return supplierMapper.getSuppliers();
    }

    /**
     * Create a supplier, generating new ID
     *
     * @param supplier to create
     * @return the newly created supplier
     * @throws Exception
     */
    public Supplier createSupplier(Supplier supplier) throws Exception {
        supplier.generateId();
        try {
            supplierMapper.createSupplier(supplier);
            return supplier;
        } catch (DuplicateKeyException exception) {
            throw new UniqueViolation("Duplicate name for supplier.", exception);
        } catch (Exception e) {
            logger.warn("Other exception!", e);
            throw e;
        }
    }

    /**
     * Retrieve a supplier by ID
     *
     * @param id supplier's id
     * @return the supplier
     * @throws Exception
     */
    public Supplier getSupplier(String id) throws Exception {
        return supplierMapper.getSupplier(id);
    }

    /**
     * Update a supplier by taking a partial one
     * <p>
     * Any new values (other than ID, used to identify the supplier) in the supplier are updated.
     *
     * @param supplier any new values
     * @return the fully updated supplier
     * @throws Exception
     */
    public Supplier updateSupplier(Supplier supplier) throws Exception {
        try {
            supplierMapper.updateSupplier(supplier);
            Supplier updated = supplierMapper.getSupplier(supplier.getId());
            if (updated != null) {
                return updated;
            } else {
                throw new SupplierMissingViolation("Nothing there to update", null);
            }
        } catch (DuplicateKeyException exception) {
            throw new UniqueViolation("Duplicate name for supplier.", exception);
        } catch (Exception e) {
            logger.warn("Other exception!", e);
            throw e;
        }
    }
}
