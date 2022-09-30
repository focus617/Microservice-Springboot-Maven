package com.focus617.webbackendservice.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.focus617.webbackendservice.domain.interactors.dtos.ProductRegistrationRequest
import com.focus617.webbackendservice.domain.model.Product
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.*

@SpringBootTest
@ExtendWith(SpringExtension::class)
@AutoConfigureMockMvc
internal class ProductControllerTest @Autowired constructor(
    val mockMvc: MockMvc,
    val objectMapper: ObjectMapper
) {
    companion object {
        val log: Logger = LoggerFactory.getLogger(this::class.java)
    }

    private val baseUrl = "/api/v1/products"

    @Nested
    @DisplayName("GET /api/v1/products")
    @TestInstance(Lifecycle.PER_CLASS)
    inner class GetProducts {
        @Test
        fun `should return all products`() {
            // When/then
            mockMvc.get(baseUrl)
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$[0].title") { value("Title #1") }
                }
        }
    }

    @Nested
    @DisplayName("GET /api/v1/products/parameters")
    @TestInstance(Lifecycle.PER_CLASS)
    inner class GetProductsWithSorting {
        @Test
        fun `should return sorted products with default element number per page`() {
            // When/then
            mockMvc.get("$baseUrl/parameters?sort=desc")
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.data.size()") { value(10) }
                    jsonPath("$.page") { value(1) }
//                    jsonPath("$.last_page") { value(("$.total".toInt())/10 + 1) }
                }
        }

        @Test
        fun `should return paged products with requested page`() {
            // When/then
            mockMvc.get("$baseUrl/parameters?sort=asc&page=2")
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.page") { value(2) }
                }
        }
    }

    @Nested
    @DisplayName("GET /api/v1/products/{id}")
    @TestInstance(Lifecycle.PER_CLASS)
    inner class GetProduct {

        @Test
        fun `should return the Product with the given id`() {
            // Given
            val id = 1

            // When/then
            mockMvc.get("$baseUrl/$id")
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.id") { value(id) }
                }
                .andReturn()
        }

        @Test
        fun `should return NOT FOUND if the given product id does not exist`() {
            // Given
            val invalidId = 9999

            // When/then
            mockMvc.get("$baseUrl/$invalidId")
                .andDo { print() }
                .andExpect {
                    status { isNotFound() }
                }
        }

    }

    @Nested
    @DisplayName("POST /api/v1/products")
    @TestInstance(Lifecycle.PER_CLASS)
    inner class PostNewProduct {
        @Test
        fun `should add the new Product with unique code`() {
            // Given
            val request = ProductRegistrationRequest(
                "Code#51",
                "Title NewProduct",
                "Description NewProduct",
                "https://focus617.com/200/200?188",
                99.99
            )

            // When / Then
            mockMvc.post(baseUrl) {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
            }
                .andDo { print() }
                .andExpect {
                    status { isCreated() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.title") { value("Title NewProduct") }
                    jsonPath("$.code") { value(request.code) }
                    jsonPath("$.description") { value(request.description) }
                    jsonPath("$.image") { value(request.image) }
                    jsonPath("$.price") { value(request.price) }
                }
        }

        @Test
        fun `should creat new Product with same content`() {
            // Given
            val request = ProductRegistrationRequest(
                "Code#52",
                "Title NewProduct",
                "Description NewProduct",
                "https://focus617.com/200/200?188",
                99.99
            )

            // When
            val performPost = mockMvc.post(baseUrl) {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
            }
                .andReturn()

            // Then retrieve product with newProduct.id from above performPost and compare
            performPost.response.characterEncoding = "UTF-8"
            val productJson = performPost.response.contentAsString
            //从Json数据序列转换到对象
            val product: Product = objectMapper.readValue(productJson, Product::class.java)
            log.info("After Post: $product")

            mockMvc.get("$baseUrl/${product.id}")
                .andDo { print() }
                .andExpect { content { json(objectMapper.writeValueAsString(product)) } }
        }

        @Test
        fun `should return ILLEGAL STATE if Product with given product CODE already exist`() {
            // Given
            val requestWithDuplicatedCode = ProductRegistrationRequest("Code#1", "Title #1", "Description #1")

            // When
            val performPost = mockMvc.post(baseUrl) {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(requestWithDuplicatedCode)
            }

            // Then
            performPost
                .andDo { print() }
                .andExpect {
                    status { isBadRequest() }
                }
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/products")
    @TestInstance(Lifecycle.PER_CLASS)
    inner class PatchExistingProduct {

        @Test
        fun `should update an existing product`() {
            // Given
            val updatedProduct =
                Product(
                    2,
                    "Code#62",
                    "Title NewProduct",
                    "Description NewProduct",
                    "https://focus617.com/200/200?188",
                    99.99
                )

            // When
            val performPatchRequest = mockMvc.patch(baseUrl) {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(updatedProduct)
            }

            // Then
            performPatchRequest
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content {
                        contentType(MediaType.APPLICATION_JSON)
                        json(objectMapper.writeValueAsString(updatedProduct))
                    }
                }

            mockMvc.get("$baseUrl/${updatedProduct.id}")
                .andExpect { content { json(objectMapper.writeValueAsString(updatedProduct)) } }
        }

        @Test
        fun `should return NOT FOUND if no product with given id exists`() {
            // Given
            val invalidId = 9999
            val invalidProduct =
                Product(
                    invalidId,
                    "Code#Invalid",
                    "Title NewProduct",
                    "Description NewProduct",
                    "https://focus617.com/200/200?188",
                    99.99
                )


            // When
            val performPost = mockMvc.patch(baseUrl) {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(invalidProduct)
            }

            // Then
            performPost
                .andDo { print() }
                .andExpect {
                    status { isNotFound() }
                }
        }

    }

    @Nested
    @DisplayName("PUT /api/v1/products/{id}?xxx=yyy")
    @TestInstance(Lifecycle.PER_CLASS)
    inner class PutExistingProduct {
        @Test
        fun `should return NOT FOUND if no product with given id exists`() {
            // Given
            val invalidId = 9999
            val invalidProduct =
                Product(
                    invalidId,
                    "Code#Invalid",
                    "Title NewProduct",
                    "Description NewProduct",
                    "https://focus617.com/200/200?188",
                    99.99
                )


            // When
            val performPost = mockMvc.put("$baseUrl/$invalidId?title=${invalidProduct.title}") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(invalidProduct)
            }

            // Then
            performPost
                .andDo { print() }
                .andExpect {
                    status { isNotFound() }
                }
        }

        @Test
        fun `should update an existing product with new title`() {
            val id = 1
            val newTitle = "new_title"

            // When/then
            mockMvc.put("$baseUrl/$id?title=$newTitle")
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.id") { value(id) }
                    jsonPath("$.title") { value(newTitle) }
                }
        }

        @Test
        fun `should update an existing product with new description`() {
            val id = 1
            val newDescription = "newDescription"

            // When/then
            mockMvc.put("$baseUrl/$id?description=$newDescription")
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.id") { value(id) }
                    jsonPath("$.description") { value(newDescription) }
                }
        }


        @Test
        fun `should update an existing product with maximum attributes`() {
            val updatingProduct =
                Product(
                    1,
                    "Code#1",
                    "Title NewTitle",
                    "Description NewDescription",
                    "https://focus617.com/200/200?188",
                    99.99
                )

            // When/then
            mockMvc.put(
                "$baseUrl/${updatingProduct.id}?title=${updatingProduct.title}&description=${updatingProduct.description}&image=${updatingProduct.image}&price=${updatingProduct.price}"
            )
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    content { json(objectMapper.writeValueAsString(updatingProduct)) }
                }

            mockMvc.get("$baseUrl/${updatingProduct.id}")
                .andExpect { content { json(objectMapper.writeValueAsString(updatingProduct)) } }

        }

        @Test
        fun `should update product when price is NOT set to 0`() {
            val id = 1
            val newPrice = 9.99999

            // When/then
            mockMvc.put("$baseUrl/$id?price=$newPrice")
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.id") { value(id) }
                    jsonPath("$.price") { value(newPrice) }
                }
        }

        @Test
        fun `should NOT update product when price is set to 0`() {
            val id = 1

            // When/then
            mockMvc.put("$baseUrl/$id?price=0")
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.id") { value(id) }
                    jsonPath("$.price") { isNotEmpty() }
                }
        }

    }

    @Nested
    @DisplayName("DELETE /api/v1/products/{id}")
    @TestInstance(Lifecycle.PER_CLASS)
    inner class DeleteExistingProduct {

        @Test
        fun `should delete the product with the given product id`() {
            // Given
            val productId = "3"

            // When/then
            mockMvc.delete("$baseUrl/$productId")
                .andDo { print() }
                .andExpect { status { isNoContent() } }

            mockMvc.get("$baseUrl/$productId")
                .andExpect { status { isNotFound() } }

        }

        @Test
        fun `should return NOT FOUND if no product with given id exists`() {
            // Given
            val invalidProductId = 9999

            // When/then
            mockMvc.delete("$baseUrl/$invalidProductId")
                .andDo { print() }
                .andExpect { status { isNotFound() } }
        }
    }

}