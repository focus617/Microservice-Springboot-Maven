package com.focus617.webbackendservice.domain.repository

import com.focus617.webbackendservice.data.datasource.ProductDataSource
import com.focus617.webbackendservice.data.datasource.mock.MockProductDataSource
import com.focus617.webbackendservice.domain.model.Product
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class ProductRepositoryTest {

    private val fakeDataSource = MockProductDataSource()

    private val mockDataSource: ProductDataSource = mockk(relaxed = true)

    private val product101 =
        Product(101, "Code#101", "Title #1", "Description #1", "https://focus617.com/200/200?1", 19.99)
    private val product102 =
        Product(102, "Code#102", "Title #2", "Description #2", "https://focus617.com/200/200?2", 29.99)
    private val product103 =
        Product(103, "Code#103", "Title #3", "Description #3", "https://focus617.com/200/200?3", 39.99)

    @BeforeEach
    fun setUp() {
        fakeDataSource.products.add(product101)
        fakeDataSource.products.add(product102)
        fakeDataSource.products.add(product103)
    }

    @Nested
    @DisplayName("findAll")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class FindAll {

        @Test
        fun `should provide a collection of products in case of fake DataSource`() {
            // When
            val repository = ProductRepository(fakeDataSource)
            val products = repository.findAll()

            // Then
            assertThat(products).isNotEmpty
            assertThat(products.size).isEqualTo(fakeDataSource.products.size)
        }

        @Test
        fun `should retrieve correct values in case of fake DataSource`() {
            // When
            val repository = ProductRepository(fakeDataSource)
            val products = repository.findAll()

            // Then
            assertThat(products).allMatch { it.code.isNotBlank() }
            assertThat(products).allMatch { it.title.isNotBlank() }
            assertThat(products).anyMatch { it.price != 0.0 }
            assertThat(products).anyMatch { it.description.startsWith("Description") }
        }

    }

    @Nested
    @DisplayName("findOnePage")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class FindOnePageWithSorting {
        @Test
        fun `return size should less than sizePerPage`() {
            // When
            val sizePerPage = 5
            val repository = ProductRepository(fakeDataSource)
            val products = repository.findOnePageWithSorting("price", "asc", 1, sizePerPage)

            // Then
            assertThat(products.size).isLessThanOrEqualTo(sizePerPage)
        }
    }

    @Nested
    @DisplayName("findById")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class FindById {

        @Test
        fun `should return the Product with the given id `() {
            // When
            val repository = ProductRepository(fakeDataSource)
            val product = repository.findById(product102.id)

            // Then
            assertThat(product.id).isEqualTo(product102.id)
            assertThat(product).isEqualTo(product102)
        }

        @Test
        fun `should throw NoSuchElementException if the given product id does not exist`() {
            // Given
            val invalidId = 1
            val repository = ProductRepository(mockDataSource)

            // define Mock conditions
            every { mockDataSource.findById(invalidId) } returns null

            // When / Then
            val exceptionThrown = Assertions.assertThrows(
                NoSuchElementException::class.java,
                { repository.findById(invalidId) },
                "java.util.NoSuchElementException was expected"
            )
            assertThat(exceptionThrown).hasMessageContaining("Could not find a Product with id=$invalidId")
        }
    }

    @Nested
    @DisplayName("update")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class Update {

        @Test
        fun `should call datasource update() once`() {
            // Given
            val productNew = Product(
                1,
                "Code#new",
                "Title new",
                "Description new",
                "https://focus617.com/200/200?new",
                9.99
            )

            val repository = ProductRepository(mockDataSource)
            every { mockDataSource.existsById(productNew.id) } returns true
            every { mockDataSource.findAll() } returns emptyList()

            // When
            repository.update(productNew)

            // Then
            verify(exactly = 1) { mockDataSource.update(productNew) }
        }

        @Test
        fun `should throw NoSuchElementException if the given product id does not exist`() {
            // Given
            val invalidId = 9999
            val productInvalid =
                Product(
                    invalidId,
                    "Code#invalid",
                    "Title invalid",
                    "Description invalid",
                    "https://focus617.com/200/200?invalid",
                    19.99
                )

            val repository = ProductRepository(mockDataSource)
            every { mockDataSource.existsById(invalidId) } returns false

            // When / Then
            val exceptionThrown = Assertions.assertThrows(
                NoSuchElementException::class.java,
                { repository.update(productInvalid) },
                "java.util.NoSuchElementException was expected"
            )
            assertThat(exceptionThrown).hasMessageContaining("Could not find a product with id=$invalidId")
        }

        @Test
        fun `should throw IllegalArgumentException if update product code to any taken code in DB`() {
            // Given
            val productId = 101
            val productExistWithSameCode = Product(
                productId,
                "Code#DuplicatedId",
                "Title invalid",
                "Description invalid",
                "https://focus617.com/200/200?invalid",
                19.99
            )

            val repository = ProductRepository(mockDataSource)
            every { mockDataSource.existsById(productId) } returns true
            every { mockDataSource.findAll() } returns listOf(productExistWithSameCode)

            // When / Then
            val exceptionThrown = Assertions.assertThrows(
                IllegalArgumentException::class.java,
                { repository.update(productExistWithSameCode) },
                "java.util.IllegalArgumentException was expected"
            )
            assertThat(exceptionThrown).hasMessageContaining("Product CODE ${productExistWithSameCode.code} already taken")
        }
    }
}