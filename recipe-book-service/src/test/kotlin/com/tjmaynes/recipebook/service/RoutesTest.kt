package com.tjmaynes.recipebook.service

import com.tjmaynes.recipebook.core.domain.Ingredient
import com.tjmaynes.recipebook.core.types.IService
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RoutesTest {
    private val ingredientService = mockk<IService<Ingredient>>()

    @BeforeEach
    fun setup() {
    }

    @Test
    fun `should be able to return all ingredients when ingredients exist`() {
//        readRequest("/api/v1/ingredient")
//                .expectStatus().isOk
//                .expectBody()
//                .jsonPath("$").isEqualTo(ingredients)
    }

    @Test
    fun `should be able to return an ingredient by id when ingredients exist`() {
//        val ingredient = ingredients.first()

//        readRequest("/api/v1/ingredient?id=${ingredient.id}")
//                .expectStatus().isOk
//                .expectBody()
//                .jsonPath("$").isEqualTo(ingredient)
    }

    @Test
    fun `should be able to create an ingredient when given a valid ingredient`() {
//        val ingredient = givenAnIngredient

//        writeRequest(HttpMethod.POST, "/api/v1/ingredient", ingredient)
//                .expectStatus().isCreated
//                .expectBody()
//                .jsonPath("$").isEqualTo(ingredient)
    }

    @Test
    fun `should be able to update an ingredient when given a valid ingredient`() {
//        val ingredient = givenAnIngredient

//        writeRequest(HttpMethod.PUT, "/api/v1/ingredient", ingredient)
//                .expectStatus().isCreated
//                .expectBody()
//                .jsonPath("$").isEqualTo(ingredient)
    }

    @Test
    fun `should be able to remove an ingredient by id when a specific ingredient exists`() {
//        val ingredient = ingredients.first()

//        removeRequest("/api/v1/ingredient?id=${ingredient.id}")
//                .expectStatus().isOk
//                .expectBody()
//                .jsonPath("$").isEqualTo(ingredient)
    }
}