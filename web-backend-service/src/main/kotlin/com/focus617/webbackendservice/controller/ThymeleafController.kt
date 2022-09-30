package com.focus617.webbackendservice.controller

import com.focus617.webbackendservice.domain.interactors.ProductService
import com.focus617.webbackendservice.domain.model.Product
import org.springframework.data.domain.Page
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class ThymeleafController(private val service: ProductService) {

    @GetMapping("/index")
    fun index(model: Model): String {
        model.addAttribute("title", "Thymeleaf Tutorial")
        model.addAttribute("welcome", "Hello, this is test page for Thymeleaf.")
        return "index"
    }

    @GetMapping("/api/v1/products/template/parameters")
    fun getPageWithSort(
        model: Model,
        @RequestParam("field", defaultValue = "id") field: String,
        @RequestParam("sortDir", defaultValue = "asc") sortDir: String,
        @RequestParam("page", defaultValue = "1") currentPage: Int,
        @RequestParam("limit", defaultValue = "10") sizePerPage: Int
    ): String {
        val page: Page<Product> = service.getProductsInPageWithSorting(field, sortDir, currentPage, sizePerPage)

        val totalPages = page.totalPages
        val totalItems = page.totalElements
        val products: List<Product> = page.content

        model.addAttribute("welcome", "Hello, this is test page for Thymeleaf.")
        model.addAttribute("currentPage", currentPage)
        model.addAttribute("totalPages", totalPages)
        model.addAttribute("totalItems", totalItems)

        model.addAttribute("sortDir", sortDir)
        model.addAttribute("reverseSortDir", if (sortDir == "asc") "desc" else "asc")

        model.addAttribute("products", products)

        return "product"
    }

}