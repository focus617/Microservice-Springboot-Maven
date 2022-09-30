package com.focus617.webbackendservice.data.dao

import com.focus617.webbackendservice.domain.model.Product
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ProductDao : JpaRepository<Product, Int> {

    @Query("select p from Product p where p.title like %?1% or p.description like %?1%")
    fun search(field: String, pageable: Pageable): List<Product>

    @Query("select COUNT(p) from Product p where p.title like %?1% or p.description like %?1%", countQuery = "*")
    fun countSearch(field: String): Int

}