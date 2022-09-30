package com.focus617.webbackendservice.data.datasource

import com.focus617.webbackendservice.data.dao.ProductDao
import com.focus617.webbackendservice.domain.model.Product
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository

@Repository("Database")
class DatabaseProductDataSource(private val productDao: ProductDao) : ProductDataSource {

    override fun findAll(): List<Product> = productDao.findAll()

    override fun findOnePageWithSorting(pageNumber: Int, sizePerPage: Int, sort: Sort): Page<Product> {
        val pageable: Pageable = PageRequest.of(pageNumber - 1, sizePerPage, sort)
        return productDao.findAll(pageable)
    }

    override fun findById(id: Int): Product? {
        val products = productDao.findAllById(listOf(id))
        return if (products.size != 0) products[0] else null
    }

    override fun create(product: Product): Product = productDao.saveAndFlush(product)

    override fun update(product: Product): Product = productDao.saveAndFlush(product)

    override fun deleteById(id: Int) = productDao.deleteAllById(listOf(id))

    override fun existsById(id: Int): Boolean = productDao.existsById(id)

}