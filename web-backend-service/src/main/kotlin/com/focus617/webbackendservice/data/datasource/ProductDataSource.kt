package com.focus617.webbackendservice.data.datasource

import com.focus617.webbackendservice.domain.model.Product
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort

interface ProductDataSource {
    fun findAll(): List<Product>
    fun findOnePageWithSorting(pageNumber: Int, sizePerPage: Int, sort: Sort): Page<Product>
    fun findById(id: Int): Product?
    fun create(product: Product): Product
    fun update(product: Product): Product
    fun deleteById(id: Int): Unit
    fun existsById(id: Int): Boolean
}