package com.focus617.webbackendservice.data.datasource.mock

import com.focus617.webbackendservice.domain.model.Product
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class MockDataSourceTest {

    private val mockDataSource = MockProductDataSource()
    private val product101 =
        Product(101, "Code#101", "Title #1", "Description #1", "https://focus617.com/200/200?1", 19.99)
    private val product102 =
        Product(102, "Code#102", "Title #2", "Description #2", "https://focus617.com/200/200?2", 29.99)
    private val product103 =
        Product(103, "Code#103", "Title #3", "Description #3", "https://focus617.com/200/200?3", 39.99)

    @BeforeEach
    fun setUp() {
        mockDataSource.products.add(product101)
        mockDataSource.products.add(product102)
        mockDataSource.products.add(product103)
    }

    @Test
    fun `should provide a collection of products`() {
        // When
        val products = mockDataSource.findAll()

        // Then
        assertThat(products).isNotEmpty
        assertThat(products.size).isEqualTo(mockDataSource.products.size)
    }

    @Test
    fun `should provide some mock data`() {
        // When
        val products = mockDataSource.findAll()

        // Then
        assertThat(products).allMatch { it.title.isNotBlank() }
        assertThat(products).anyMatch { it.price != 0.0 }
        assertThat(products).anyMatch { it.description.startsWith("Description") }
    }
}