package com.tjmaynes.recipebook.core.domain

import arrow.core.Either
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.*

class IngredientTest {
    @Test
    fun `#validate - should return an ingredient when given a valid ingredient`() {
        val sut = Ingredient(
                id = UUID.randomUUID(),
                name = "some-name",
                servingSize = 14,
                sizeType = "teaspoon",
                price = 1.0,
                calories = 400,
                quality = Ingredient.Quality.High,
                createdAt = Date.from(Instant.now()),
                updatedAt = Date.from(Instant.now())
        )

        assertEquals(Either.Right(sut), sut.validate())
    }

    @Test
    fun `#validate - should return an ingredient when given an identity`() {
        val sut = Ingredient.identity()
        assertEquals(Either.Right(sut), sut.validate())
    }

    @Test
    fun `#validate - should return a list of reasons when given an invalid igredient`() {
        val sut = Ingredient(
                id = UUID.randomUUID(),
                name = "",
                calories = -1,
                price = 0.0,
                sizeType = "",
                servingSize = -1,
                quality = Ingredient.Quality.High,
                createdAt = Date.from(Instant.now()),
                updatedAt = Date.from(Instant.now())
        )

        assertEquals(Either.Left(
                listOf(
                        "price should be greater than zero.",
                        "servingSize should be greater than zero.",
                        "calories should be greater than zero.",
                        "name must not be empty.",
                        "sizeType must not be empty."
                )
        ), sut.validate())
    }
}