package com.tjmaynes.recipebook.persistence.domain

import arrow.core.Either
import com.tjmaynes.recipebook.persistence.domain.Ingredient
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.*

class IngredientTest {
    @Test
    fun `#validate - should return an ingredient when given a valid ingredient`() {
        val sut = Ingredient(
            id = UUID.randomUUID().toString(),
            name = "some-name",
            servingSize = 14,
            sizeType = "teaspoon",
            price = 1.0,
            calories = 400,
            quality = Ingredient.Quality.High,
            createdAt = Date.from(Instant.now()).time,
            updatedAt = Date.from(Instant.now()).time
        )

        assertEquals(Either.Right(sut), sut.validate())
    }

    @Test
    fun `#validate - should return an ingredient when given an identity`() {
        val sut = Ingredient.identity()
        assertEquals(Either.Right(sut), sut.validate())
    }

    @Test
    fun `#validate - should return a list of reasons when given an invalid ingredient`() {
        val sut = Ingredient(
            id = UUID.randomUUID().toString(),
            name = "",
            calories = -1,
            price = -0.99,
            sizeType = "",
            servingSize = -1,
            quality = Ingredient.Quality.High,
            createdAt = Date.from(Instant.now()).time,
            updatedAt = Date.from(Instant.now()).time
        )

        assertEquals(Either.Left(
                listOf(
                    "price should be greater than or equal to zero.",
                    "servingSize should be greater than or equal to zero.",
                    "calories should be greater than or equal to zero.",
                    "name must not be empty.",
                    "sizeType must not be empty."
                )
        ), sut.validate())
    }
}
