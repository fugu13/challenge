package com.example.mapper;

import com.example.domain.Supplier;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface SupplierMapper {
    List<Supplier> getSuppliers() throws Exception;
    Supplier getSupplier(String id) throws Exception;
    void createSupplier(Supplier supplier) throws Exception;
    void updateSupplier(Supplier supplier) throws Exception;
}